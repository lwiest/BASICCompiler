/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Lorenz Wiest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package de.lorenzwiest.basiccompiler.compiler;

import static de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaClass.RUNTIME_EXCEPTION;
import static de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaMethod.EXCEPTION_GET_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import de.lorenzwiest.basiccompiler.bytecode.ClassModel;
import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.etc.CompileException;
import de.lorenzwiest.basiccompiler.compiler.etc.LineNumberTable;
import de.lorenzwiest.basiccompiler.compiler.etc.LocalVariableTable;
import de.lorenzwiest.basiccompiler.compiler.etc.ReturnTable;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.parser.Parser;
import de.lorenzwiest.basiccompiler.parser.nodes.INode;
import de.lorenzwiest.basiccompiler.parser.nodes.NodeType;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.BinaryNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.FnFunctionNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.FunctionNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.LocalVariableNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.NumNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.StrNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.TokenNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.UnaryNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.VariableNode;
import de.lorenzwiest.basiccompiler.parser.statements.Statement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.DataStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.DefFnStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.DimStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.EndStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.ForStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.GosubStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.GotoStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.IfStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.InputStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.LetStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.LineNumberStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.NextStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.OnGosubStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.OnGotoStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.PrintStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.ReadStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.RemStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.RestoreStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.ReturnStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.StopStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.SwapStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.WendStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.WhileStatement;
import de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken;
import de.lorenzwiest.basiccompiler.parser.tokens.Token;

public class Compiler {
	private static final String TAB = "\t";
	public static final String CR = System.getProperty("line.separator");

	public static final String FIELD_CURSOR_POS = "_cursorPos";

	public static final String FIELD_LAST_RND = "_lastRnd";

	public static final String FIELD_GOSUB_STACK = "_gosubStack";
	public static final String FIELD_GOSUB_STACK_INDEX = "_gosubStackIndex";
	public static final int GOSUB_STACK_SIZE = 256; // holds this many nested GOSUB calls
	public static final int GOSUB_STACK_FRAME_SIZE = 1; // holds 1 int per GOSUB stack frame

	private static final String FOR_POSTFIX_END_VAR = "_end";
	private static final String FOR_POSTFIX_STEP_VAR = "_step";

	public static final String FIELD_DATA = "_data";
	public static final String FIELD_DATA_INFO = "_dataInfo";
	public static final String FIELD_DATA_INDEX = "_dataIndex";

	private final static String IS_DEF_PREFIX = "_isdef_";

	private static final String LABEL_END = "END";

	private final ClassModel classModel;
	private ByteOutStream o;
	private final LibraryManager libraryManager;
	private final LineNumberTable lineNumberTable;
	private final ReturnTable returnTable;
	private final Stack<WhileInfo> whileCompiletimeStack;
	private final Stack<ForInfo> forCompiletimeStack;
	private final TreeMap<String /* line number */, List<String> /* constants */> dataMap;
	private final TreeMap<String /* line number */, List<RestoreInfo>> restoreMap;

	private final Set<String /* varName */> strVariables;

	private final Map<String /* varName */, Integer /* var position */> localFnVariables;

	private final List<DefFnStatement> defFns;

	private final LocalVariableTable localVariables;

	private Map<String /* arrName */, String /* field descriptor*/> arrVariables;

	public Compiler(String className) {
		this.classModel = new ClassModel(className);
		this.o = new ByteOutStream(ClassModel.MAX_METHOD_LENGTH);
		this.libraryManager = new LibraryManager(this.classModel);
		this.lineNumberTable = new LineNumberTable();
		this.returnTable = new ReturnTable();
		this.whileCompiletimeStack = new Stack<WhileInfo>();
		this.forCompiletimeStack = new Stack<ForInfo>();
		this.dataMap = new TreeMap<String, List<String>>();
		this.restoreMap = new TreeMap<String, List<RestoreInfo>>();
		this.strVariables = new HashSet<String>();
		this.defFns = new ArrayList<DefFnStatement>();
		this.localFnVariables = new HashMap<String, Integer>();
		this.localVariables = new LocalVariableTable();
		this.arrVariables = new HashMap<String, String>();
	}

	public ClassModel getClassModel() {
		return this.classModel;
	}

	public void compile(Statement statement) {
		if (statement instanceof DataStatement) {
			emitData((DataStatement) statement);
		} else if (statement instanceof DefFnStatement) {
			emitDefFn((DefFnStatement) statement);
		} else if (statement instanceof DimStatement) {
			emitDim((DimStatement) statement);
		} else if (statement instanceof EndStatement) {
			emitEnd();
		} else if (statement instanceof ForStatement) {
			emitFor((ForStatement) statement);
		} else if (statement instanceof GosubStatement) {
			emitGosub((GosubStatement) statement);
		} else if (statement instanceof GotoStatement) {
			emitGoto((GotoStatement) statement);
		} else if (statement instanceof IfStatement) {
			emitIf((IfStatement) statement);
		} else if (statement instanceof InputStatement) {
			emitInput((InputStatement) statement);
		} else if (statement instanceof LetStatement) {
			emitLet((LetStatement) statement);
		} else if (statement instanceof LineNumberStatement) {
			emitLineNumber((LineNumberStatement) statement);
		} else if (statement instanceof NextStatement) {
			emitNext((NextStatement) statement);
		} else if (statement instanceof OnGosubStatement) {
			emitOnGosub((OnGosubStatement) statement);
		} else if (statement instanceof OnGotoStatement) {
			emitOnGoto((OnGotoStatement) statement);
		} else if (statement instanceof PrintStatement) {
			emitPrint((PrintStatement) statement);
		} else if (statement instanceof ReadStatement) {
			emitRead((ReadStatement) statement);
		} else if (statement instanceof RemStatement) {
			// ignore
		} else if (statement instanceof RestoreStatement) {
			emitRestore((RestoreStatement) statement);
		} else if (statement instanceof ReturnStatement) {
			emitReturn();
		} else if (statement instanceof StopStatement) {
			emitStop();
		} else if (statement instanceof SwapStatement) {
			emitSwap((SwapStatement) statement);
		} else if (statement instanceof WendStatement) {
			emitWend();
		} else if (statement instanceof WhileStatement) {
			emitWhile((WhileStatement) statement);
		} else {
			throw new CompileException("Unknown statement");
		}
	}

	public void flush() {
		this.o.label(LABEL_END);
		this.o.return_();

		flushData();
		flushForNext();
		flushRestore();
		flushWhileWend();
		this.lineNumberTable.flush(this.o);
		this.returnTable.flush(this.o);

		int posExceptionHandler = this.o.pos();
		flushExceptionHandler();

		this.o.flush();
		byte[] bodyByteCode = this.o.toByteArray();

		flushDefFns(); // after flushing body byte code, before initialization byte code!

		byte[] initByteCode = getInitializationByteCode();

		byte[] byteCode = combineByteCodeParts(initByteCode, bodyByteCode);
		ExceptionTableInfo[] exceptionTable = getExceptionTable(initByteCode.length + posExceptionHandler);

		int numLocals = this.localVariables.size();
		this.classModel.addMainMethod(numLocals, byteCode, exceptionTable);

		this.libraryManager.flush();

		this.o.closeGracefully();
	}

	private void flushDefFns() {
		for (DefFnStatement defFn : this.defFns) {

			String funcName = defFn.getFuncName();
			NodeType funcType = defFn.getFuncExpr().getType();

			int numLocals = defFn.getFuncVars().length;
			this.localFnVariables.clear();

			String descriptor = "(";
			for (int i = 0; i < numLocals; i++) {
				VariableNode funcVar = defFn.getFuncVars()[i];
				descriptor += (funcVar.getType() == NodeType.NUM) ? "F" : "[C";
				this.localFnVariables.put(funcVar.getVariableName(), i); // TODO: we lose type information here
			}
			descriptor += ")";
			descriptor += (funcType == NodeType.NUM) ? "F" : "[C";

			ByteOutStream saveStream = this.o;
			ByteOutStream o = new ByteOutStream(ClassModel.MAX_METHOD_LENGTH);
			this.o = o;

			// runtime definition flag
			String fieldName = IS_DEF_PREFIX + funcName;
			o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(fieldName, "Z"));
			o.ifne("isDefinedAtRuntime");

			o.ldc(this.classModel.getStringIndex("Undefined function " + funcName + "()."));
			this.libraryManager.getMethod(MethodEnum.THROW_RUNTIME_EXCEPTION).emitCall(o);

			o.label("isDefinedAtRuntime");

			INode funcExpr = defFn.getFuncExpr();
			if (funcType == NodeType.NUM) {
				emitNumExpressionToStack(funcExpr);
				o.freturn();
			} else if (funcType == NodeType.STR) {
				emitStrExpressionToStack(funcExpr);
				o.areturn();
			}

			o.flushAndCloseGracefully();

			this.o = saveStream;
			this.classModel.addMethod(funcName, descriptor, numLocals, o.toByteArray());
		}
	}

	private void flushExceptionHandler() {
		this.o.ldc(this.classModel.getStringIndex(Compiler.CR + "ERROR: "));
		this.libraryManager.getMethod(MethodEnum.PRINT_STRING_FROM_STACK).emitCall(this.o);
		this.o.invokevirtual(this.classModel.getJavaMethodRefIndex(EXCEPTION_GET_MESSAGE));
		this.libraryManager.getMethod(MethodEnum.PRINT_STRING_FROM_STACK).emitCall(this.o);
		this.o.goto_(LABEL_END);
	}

	private byte[] getInitializationByteCode() {
		ByteOutStream o = new ByteOutStream();

		initStrVars(o);
		initArrVars(o);
		initLocalVars(o);
		initData(o);
		initGosubStack(o);

		o.pad4ByteBoundary(); // padding for tableswitch in body code

		o.flushAndCloseGracefully();
		return o.toByteArray();
	}

	private void initStrVars(ByteOutStream o) {
		if (this.strVariables.size() > 0) {
			o.iconst_0();
			o.newarray_char();

			if (this.strVariables.size() == 1) {
				o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(this.strVariables.iterator().next(), "[C"));
			} else {
				for (String strVar : this.strVariables) {
					o.dup();
					o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(strVar, "[C"));
				}
				o.pop();
			}
		}
	}

	private void initArrVars(ByteOutStream o) {
		for (String arrVarName : this.arrVariables.keySet()) {
			String arrVarFieldDescriptor = this.arrVariables.get(arrVarName);
			o.iconst_1();
			o.anewarray(this.classModel.getClassIndex(arrVarFieldDescriptor.substring(1)));
			o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(arrVarName, arrVarFieldDescriptor));
		}
	}

	private void initLocalVars(ByteOutStream o) {
		List<LocalVariableNode> numLocVars = new ArrayList<LocalVariableNode>();
		List<LocalVariableNode> strLocVars = new ArrayList<LocalVariableNode>();

		LocalVariableNode[] sortedLocVars = this.localVariables.sortByLocalIndex();
		for (int i = 0; i < sortedLocVars.length; i++) {
			LocalVariableNode locVarNode = sortedLocVars[i];
			if (locVarNode.getType() == NodeType.NUM) {
				numLocVars.add(locVarNode);
			} else if (locVarNode.getType() == NodeType.STR) {
				strLocVars.add(locVarNode);
			}
		}

		int numLocVarsCount = numLocVars.size();
		if (numLocVarsCount > 0) {
			for (int i = 0; i < numLocVarsCount; i++) {
				o.fconst_0();
				o.fstore(numLocVars.get(i).getLocalIndex());
			}
		}

		int strLocVarsCount = strLocVars.size();
		if (strLocVarsCount > 0) {
			o.iconst_0();
			o.newarray_char();
			if (strLocVarsCount == 1) {
				o.astore(strLocVars.get(0).getLocalIndex());
			} else {
				for (int i = 0; i < strLocVarsCount; i++) {
					o.dup();
					o.astore(strLocVars.get(i).getLocalIndex());
				}
				o.pop();
			}
		}
	}

	private void initGosubStack(ByteOutStream o) {
		if (this.returnTable.isUsed()) {
			this.classModel.addField(Compiler.FIELD_GOSUB_STACK, "[I");

			o.iconst_0();
			o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(Compiler.FIELD_GOSUB_STACK_INDEX, "I"));

			this.libraryManager.getMethod(MethodEnum.GOSUB_STACK_INITIALIZE).emitCall(o);
		}
	}

	private void initData(ByteOutStream o) {
		if (this.strDataIndex > 0) {
			o.ldc(this.strDataIndex);
			this.libraryManager.getMethod(MethodEnum.STRING_TO_CHARS).emitCall(o);
			o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(Compiler.FIELD_DATA, "[C"));

			o.ldc(this.strDataInfoIndex);
			this.libraryManager.getMethod(MethodEnum.STRING_TO_CHARS).emitCall(o);
			o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(Compiler.FIELD_DATA_INFO, "[C"));

			o.iconst_0();
			o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(Compiler.FIELD_DATA_INDEX, "I"));
		}
	}

	private byte[] combineByteCodeParts(byte[] initByteCode, byte[] bodyByteCode) {
		int lenInit = initByteCode.length;
		int lenBody = bodyByteCode.length;
		byte[] byteCode = new byte[lenInit + lenBody];
		System.arraycopy(initByteCode, 0, byteCode, 0, lenInit);
		System.arraycopy(bodyByteCode, 0, byteCode, lenInit, lenBody);
		return byteCode;
	}

	private ExceptionTableInfo[] getExceptionTable(int posCatch) {
		int posTryBegin = 0;
		int posTryEnd = posCatch;
		return new ExceptionTableInfo[] { //
				new ExceptionTableInfo(posTryBegin, posTryEnd, posCatch, this.classModel.getJavaClassRefIndex(RUNTIME_EXCEPTION)), //
		};
	}

	private int branchOffset(int from, int to) {
		return (to - from) + 1;
	}

	//////////////////////////////////////////////////////////////////////////////

	private void emitData(DataStatement dataStatement) {
		String lineNumber = dataStatement.getLineNumber();
		String[] constants = dataStatement.getConstants();

		if (this.dataMap.containsKey(lineNumber) == false) {
			this.dataMap.put(lineNumber, new ArrayList<String>());
		}
		this.dataMap.get(lineNumber).addAll(Arrays.asList(constants));
	}

	private int strDataIndex;
	private int strDataInfoIndex;

	private void flushData() {
		StringBuffer strData = new StringBuffer();
		StringBuffer strDataInfo = new StringBuffer();

		for (List<String> dataElements : this.dataMap.values()) { // sorted by line number. DEFAULT_LABEL is first.
			for (String dataElement : dataElements) {
				int index = strData.length(); // assert index < 2^16 = 65536
				int length = dataElement.length(); // assert length < 2^16 = 65536

				strData.append(dataElement);
				strDataInfo.append((char) index);
				strDataInfo.append((char) length);
			}
		}

		boolean hasData = strData.length() > 0;
		this.strDataIndex = hasData ? this.classModel.getStringIndex(strData.toString()) : 0;
		this.strDataInfoIndex = hasData ? this.classModel.getStringIndex(strDataInfo.toString()) : 0;
	}

	private void emitDefFn(DefFnStatement defFnStatement) {
		this.defFns.add(defFnStatement);

		String fieldName = IS_DEF_PREFIX + defFnStatement.getFuncName();

		this.o.iconst_1();
		this.o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(fieldName, "Z"));
	}

	private void emitDim(DimStatement dimStatement) {
		for (VariableNode var : dimStatement.getVariables()) {
			String varName = var.getVariableName();
			int numDims = var.getDimExpressions().length;
			if (numDims == 1) {
				if (var.getType() == NodeType.NUM) {
					this.arrVariables.put(varName, "[[F");
					this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[F"));
					emitNumExpressionToStack(var.getDimExpressions()[0]);
					this.libraryManager.getMethod(MethodEnum.DIM_1D_FLOAT_ARRAY).emitCall(this.o);
				} else if (var.getType() == NodeType.STR) {
					this.arrVariables.put(varName, "[[[C");
					this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[C"));
					emitNumExpressionToStack(var.getDimExpressions()[0]);
					this.libraryManager.getMethod(MethodEnum.DIM_1D_STRING_ARRAY).emitCall(this.o);
				}
			} else if (numDims == 2) {
				if (var.getType() == NodeType.NUM) {
					this.arrVariables.put(varName, "[[[F");
					this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[F"));
					emitNumExpressionToStack(var.getDimExpressions()[0]);
					emitNumExpressionToStack(var.getDimExpressions()[1]);
					this.libraryManager.getMethod(MethodEnum.DIM_2D_FLOAT_ARRAY).emitCall(this.o);
				} else if (var.getType() == NodeType.STR) {
					this.arrVariables.put(varName, "[[[[C");
					this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[[C"));
					emitNumExpressionToStack(var.getDimExpressions()[0]);
					emitNumExpressionToStack(var.getDimExpressions()[1]);
					this.libraryManager.getMethod(MethodEnum.DIM_2D_STRING_ARRAY).emitCall(this.o);
				}
			}
		}
	}

	private void emitEnd() {
		this.o.goto_(LABEL_END);
	}

	private static class ForInfo {
		private final String forLabel;
		private final VariableNode loopVar;
		private final int patchPosToSkipForNextLoop;

		public ForInfo(String forLabel, VariableNode loopVar, int patchPosToSkipForNextLoop) {
			this.forLabel = forLabel;
			this.loopVar = loopVar;
			this.patchPosToSkipForNextLoop = patchPosToSkipForNextLoop;
		}

		public String getForLabel() {
			return this.forLabel;
		}

		public VariableNode getLoopVar() {
			return this.loopVar;
		}

		public int getPatchPosToSkipForNextLoop() {
			return this.patchPosToSkipForNextLoop;
		}
	}

	private void emitFor(ForStatement forStatement) {
		VariableNode loopVar = forStatement.getLoopVariable();
		INode startExpr = forStatement.getStartExpression();
		INode endExpr = forStatement.getEndExpression();
		INode stepExpr = forStatement.getStepExpression();

		String loopVarName = loopVar.getVariableName();

		LocalVariableNode stepVar = this.localVariables.addAndGetLocalVariableNode(loopVarName + FOR_POSTFIX_STEP_VAR, NodeType.NUM);
		LocalVariableNode endVar = this.localVariables.addAndGetLocalVariableNode(loopVarName + FOR_POSTFIX_END_VAR, NodeType.NUM);

		int loopVarFieldRefIndex = this.classModel.addFieldAndGetFieldRefIndex(loopVar.getVariableName(), "F");

		emitNumExpressionToStack(startExpr);
		emitFloatFromStackToNumVariable(loopVar);

		emitNumExpressionToStack(stepExpr);
		emitFloatFromStackToNumVariable(stepVar);

		emitNumExpressionToStack(endExpr);
		emitFloatFromStackToNumVariable(endVar);

		String forLabel = "_for" + ByteOutStream.generateLabel();
		this.o.label(forLabel);

		// skip FOR-NEXT if <loopVar> * SGN(<stepExpr>) > <endExpr> * SGN(<stepExpr>)

		this.o.fload_opt(stepVar.getLocalIndex());

		this.libraryManager.getMethod(MethodEnum.SGN).emitCall(this.o);
		this.o.dup();
		this.o.getstatic(loopVarFieldRefIndex);
		this.o.fmul();
		this.o.swap();

		this.o.fload_opt(endVar.getLocalIndex());
		this.o.fmul();
		this.o.fcmpg();

		this.o.ifgt(); // ifgt(...)
		int patchPosToSkipForNextLoop = this.o.pos();
		this.o.write_u2(0x0000); // ...will be patched

		this.forCompiletimeStack.push(new ForInfo(forLabel, loopVar, patchPosToSkipForNextLoop));
	}

	private void emitNext(NextStatement nextStatement) {
		VariableNode[] loopVars = nextStatement.getLoopVariables();
		if (loopVars.length > 0) {
			for (VariableNode loopVar : loopVars) {
				emitInternalNext(loopVar);
			}
		} else {
			emitInternalNext(null);
		}
	}

	private void emitInternalNext(VariableNode nextLoopVar) {
		if (this.forCompiletimeStack.isEmpty()) {
			throw new CompileException("NEXT without FOR");
		}
		ForInfo forInfo = this.forCompiletimeStack.pop();
		String forLabel = forInfo.getForLabel();
		VariableNode forLoopVar = forInfo.getLoopVar();
		int patchPosToSkipForNextLoop = forInfo.getPatchPosToSkipForNextLoop();

		if (nextLoopVar != null) {
			String forLoopVarName = forLoopVar.getVariableName();
			String nextLoopVarName = nextLoopVar.getVariableName();
			if (forLoopVarName.equals(nextLoopVarName) == false) {
				throw new CompileException("NEXT does not match FOR");
			}
		}

		emitFloatFromNumVariableToStack(forLoopVar);

		String stepVarName = forLoopVar.getVariableName() + FOR_POSTFIX_STEP_VAR;
		this.o.fload_opt(this.localVariables.get(stepVarName).getLocalIndex());

		this.o.fadd();
		emitFloatFromStackToNumVariable(forLoopVar);

		this.o.goto_(forLabel);

		int branchOffset = branchOffset(patchPosToSkipForNextLoop, this.o.pos());
		this.o.patch_u2(patchPosToSkipForNextLoop, branchOffset);
	}

	private void flushForNext() {
		while (this.forCompiletimeStack.isEmpty() == false) {
			ForInfo forInfo = this.forCompiletimeStack.pop();
			int patchPosToSkipForNextLoop = forInfo.getPatchPosToSkipForNextLoop();
			this.o.patchThereToLabel(patchPosToSkipForNextLoop, LABEL_END);
		}
	}

	private void emitGoto(GotoStatement gotoStatement) {
		String lineNumber = gotoStatement.getLineNumber();
		this.o.goto_(); // goto(...)
		this.lineNumberTable.patchHere_u2(this.o.pos(), lineNumber);
		this.o.write_u2(0x0000); // ...will be patched
	}

	private void emitOnGoto(OnGotoStatement onGotoStatement) {
		INode numExpr = onGotoStatement.getExpression();
		String[] lineNumbers = onGotoStatement.getLineNumbers();
		emitNumExpressionToStack(numExpr);
		this.libraryManager.getMethod(LibraryManager.MethodEnum.ROUND_TO_INT).emitCall(this.o);
		this.o.dup();
		this.libraryManager.getMethod(LibraryManager.MethodEnum.CHECK_ON_GOTO_GOSUB_ARG).emitCall(this.o);

		this.o.tableswitch();
		int posAfterTableSwitch = this.o.pos();

		this.o.pad4ByteBoundary();

		int numLineNumbers = lineNumbers.length;
		int posDefaultSwitch = this.o.pos();
		this.o.write_u4(0x00000000); // ...will be patched
		this.o.write_u4(1);
		this.o.write_u4(numLineNumbers);

		for (int i = 0; i < numLineNumbers; i++) {
			this.lineNumberTable.patchThere_u4(this.o.pos(), posAfterTableSwitch, lineNumbers[i]);
			this.o.write_u4(0x00000000); // ...will be patched
		}

		// switch "default" will point here
		this.o.patch_u4(posDefaultSwitch, branchOffset(posAfterTableSwitch, this.o.pos()));
	}

	private void emitGosub(GosubStatement gosubStatement) {
		String lineNumber = gosubStatement.getLineNumber();
		int gosubId = this.returnTable.nextIndex();
		this.o.iconst(gosubId);
		this.libraryManager.getMethod(MethodEnum.GOSUB_STACK_PUSH).emitCall(this.o);
		emitGoto(new GotoStatement(lineNumber));
		this.returnTable.addReturnPos(gosubId, this.o.pos());
	}

	private void emitOnGosub(OnGosubStatement onGosubStatement) {
		INode numExpr = onGosubStatement.getExpression();
		String[] lineNumbers = onGosubStatement.getLineNumbers();
		int gosubId = this.returnTable.nextIndex();
		this.o.iconst(gosubId);
		this.libraryManager.getMethod(MethodEnum.GOSUB_STACK_PUSH).emitCall(this.o);
		emitOnGoto(new OnGotoStatement(numExpr, lineNumbers));
		this.returnTable.addReturnPos(gosubId, this.o.pos());
	}

	private void emitReturn() {
		// pop gosubId
		this.libraryManager.getMethod(MethodEnum.GOSUB_STACK_POP).emitCall(this.o);
		this.o.goto_(); // goto <tableswitch>
		this.returnTable.patchToTableSwitch(this.o.pos());
		this.o.write_u2(0x0000); // ...will be patched
	}

	private void emitIf(IfStatement ifStatement) {
		INode numExpr = ifStatement.getExpression();
		if (isNumRelationalExpression(numExpr)) {
			emitIfRelational(ifStatement);
		} else {
			emitNumExpressionToStack(numExpr);
			this.o.fconst_0();
			this.o.fcmpg();

			String ifId = ByteOutStream.generateLabel();
			String afterThenId = "_afterThen" + ifId;
			String afterElseId = "_afterElse" + ifId;

			this.o.ifeq(afterThenId);
			for (Statement thenStatement : ifStatement.getThenStatements()) {
				compile(thenStatement);
			}
			Statement[] elseStatements = ifStatement.getElseStatements();
			if (elseStatements.length > 0) {
				this.o.goto_(afterElseId);
			}
			this.o.label(afterThenId);
			for (Statement elseStatement : elseStatements) {
				compile(elseStatement);
			}
			if (elseStatements.length > 0) {
				this.o.label(afterElseId);
			}
		}
	}

	private GotoStatement getSingleGotoOfIf(Statement[] statements) {
		if (statements.length == 1) {
			if (statements[0] instanceof GotoStatement) {
				return (GotoStatement) statements[0];
			}
		}
		return null;
	}

	private void emitIfRelational(IfStatement ifStatement) {
		BinaryNode binaryNode = (BinaryNode) ifStatement.getExpression();
		emitNumExpressionToStack(binaryNode.getLeftNode());
		emitNumExpressionToStack(binaryNode.getRightNode());
		Token opToken = binaryNode.getOp();

		this.o.fcmpg();

		String ifId = ByteOutStream.generateLabel();
		String afterThenId = "_afterThen" + ifId;
		String afterElseId = "_afterElse" + ifId;

		Statement[] thenStatements = ifStatement.getThenStatements();
		Statement[] elseStatements = ifStatement.getElseStatements();

		GotoStatement thenGotoStatement = getSingleGotoOfIf(thenStatements);
		if (thenGotoStatement != null) {
			if (opToken == Token.EQUAL) {
				this.o.ifeq(); // ifeq(...)
			} else if (opToken == Token.NOT_EQUAL) {
				this.o.ifne(); // ifne(...)
			} else if (opToken == Token.LESS_OR_EQUAL) {
				this.o.ifle(); // ifle(...)
			} else if (opToken == Token.GREATER_OR_EQUAL) {
				this.o.ifge(); // ifge(...)
			} else if (opToken == Token.LESS) {
				this.o.iflt(); // iflt(...)
			} else if (opToken == Token.GREATER) {
				this.o.ifgt(); // ifgt(...)
			}
			String lineNumber = thenGotoStatement.getLineNumber();
			this.lineNumberTable.patchHere_u2(this.o.pos(), lineNumber);
			this.o.write_u2(0x0000); // ...will be patched
		} else {
			if (opToken == Token.EQUAL) {
				this.o.ifne(afterThenId);
			} else if (opToken == Token.NOT_EQUAL) {
				this.o.ifeq(afterThenId);
			} else if (opToken == Token.LESS_OR_EQUAL) {
				this.o.ifgt(afterThenId);
			} else if (opToken == Token.GREATER_OR_EQUAL) {
				this.o.iflt(afterThenId);
			} else if (opToken == Token.LESS) {
				this.o.ifge(afterThenId);
			} else if (opToken == Token.GREATER) {
				this.o.ifle(afterThenId);
			}

			for (Statement thenStatement : thenStatements) {
				compile(thenStatement);
			}

			if (elseStatements.length > 0) {
				this.o.goto_(afterElseId);
			}
			this.o.label(afterThenId);
		}

		GotoStatement elseGotoStatement = getSingleGotoOfIf(elseStatements);
		if (elseGotoStatement != null) {
			this.o.goto_(); // goto(...)
			String lineNumber = elseGotoStatement.getLineNumber();
			this.lineNumberTable.patchHere_u2(this.o.pos(), lineNumber);
			this.o.write_u2(0x0000); // ...will be patched
		} else {
			for (Statement elseStatement : elseStatements) {
				compile(elseStatement);
			}
		}

		if (elseStatements.length > 0) {
			this.o.label(afterElseId);
		}
	}

	private void emitInput(InputStatement inputStatement) {
		StringBuffer buffer = new StringBuffer();

		String prompt = inputStatement.getPrompt();
		if (prompt != null) {
			buffer.append(prompt);
		}

		boolean showQuestionMark = inputStatement.getSeparator() == Token.SEMICOLON;
		if (showQuestionMark) {
			buffer.append("?");
		}

		if (buffer.length() > 0) {
			emitPrintStringConstFromStack(buffer.toString());
		}

		VariableNode[] vars = inputStatement.getVariables();

		StringBuffer typeBuffer = new StringBuffer();
		for (VariableNode var : vars) {
			if (var.getType() == NodeType.NUM) {
				typeBuffer.append("F");
			} else if (var.getType() == NodeType.STR) {
				typeBuffer.append("S");
			}
		}

		emitStrExpressionToStack(StrNode.create(typeBuffer.toString()));
		this.libraryManager.getMethod(LibraryManager.MethodEnum.INPUT).emitCall(this.o);

		for (int i = 0; i < vars.length; i++) {
			if (i < (vars.length - 1)) {
				this.o.dup();
			}
			this.o.iconst(i);
			this.o.aaload();
			VariableNode var = vars[i];
			if (var.getType() == NodeType.NUM) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.VAL).emitCall(this.o);
				emitFloatFromStackToNumVariable(var);
			} else if (vars[i].getType() == NodeType.STR) {
				emitCharsFromStackToStrVariable(var);
			}
		}
	}

	private void emitLet(LetStatement letStatement) {
		VariableNode var = (VariableNode) letStatement.getVariable();
		if (var.getType() == NodeType.NUM) {
			emitNumExpressionToStack(letStatement.getExpression());
			emitFloatFromStackToNumVariable(var);
		} else if (var.getType() == NodeType.STR) {
			emitStrExpressionToStack(letStatement.getExpression());
			emitCharsFromStackToStrVariable(var);
		}
	}

	private void emitLineNumber(LineNumberStatement lineNumberStatement) {
		String lineNumber = lineNumberStatement.getLineNumber();
		this.lineNumberTable.add(this.o.pos(), lineNumber);
	}

	private void emitPrint(PrintStatement printStatement) {
		for (INode expr : printStatement.getExpressions()) {
			if (expr instanceof TokenNode) {
				TokenNode tokenNode = (TokenNode) expr;
				if (tokenNode.getToken() == Token.SEMICOLON) {
					// do nothing
				} else if (tokenNode.getToken() == Token.COMMA) {
					emitPrintStringConstFromStack(TAB);
				}
			} else if (isFunctionExpressionOf(expr, FunctionToken.TAB)) {
				INode arg = ((FunctionNode) expr).getArgNodes()[0];
				emitNumExpressionToStack(arg);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.TAB).emitCall(this.o);
			} else if (isFunctionExpressionOf(expr, FunctionToken.SPC)) {
				INode arg = ((FunctionNode) expr).getArgNodes()[0];
				emitNumExpressionToStack(arg);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.SPC).emitCall(this.o);
			} else {
				if (expr.getType() == NodeType.NUM) {
					emitNumExpressionToStack(expr);
					this.libraryManager.getMethod(LibraryManager.MethodEnum.PRINT_FLOAT_FROM_STACK).emitCall(this.o);
				} else if (expr.getType() == NodeType.STR) {
					if (expr instanceof StrNode) {
						emitPrintStringConstFromStack(((StrNode) expr).getValue());
					} else {
						emitStrExpressionToStack(expr);
						this.libraryManager.getMethod(LibraryManager.MethodEnum.PRINT_CHARS_FROM_STACK).emitCall(this.o);
					}
				}
			}
		}

		boolean addPrintln = false;
		INode[] exprs = printStatement.getExpressions();
		if (exprs.length == 0) {
			addPrintln = true;
		} else {
			addPrintln = true;
			INode lastExpr = exprs[exprs.length - 1];
			if (lastExpr instanceof TokenNode) {
				Token op = ((TokenNode) lastExpr).getToken();
				if ((op == Token.SEMICOLON) || (op == Token.COMMA)) {
					addPrintln = false;
				}
			}
		}
		if (addPrintln) {
			emitPrintStringConstFromStack(CR);
		}
	}

	private void emitRead(ReadStatement readStatement) {
		VariableNode[] vars = readStatement.getVariables();
		for (VariableNode var : vars) {
			if (var.getType() == NodeType.NUM) {
				emitReadNumFromDataToStack();
				emitFloatFromStackToNumVariable(var);
			} else if (var.getType() == NodeType.STR) {
				emitReadStrFromDataToStack();
				emitCharsFromStackToStrVariable(var);
			}
		}
	}

	private void emitReadStrFromDataToStack() {
		this.libraryManager.getMethod(LibraryManager.MethodEnum.READ_STRING_FROM_DATA_TO_STACK).emitCall(this.o);
	}

	private void emitReadNumFromDataToStack() {
		this.libraryManager.getMethod(LibraryManager.MethodEnum.READ_NUM_FROM_DATA_TO_STACK).emitCall(this.o);
	}

	private static class RestoreInfo {
		private final int patchPos;

		public RestoreInfo(int patchPos) {
			this.patchPos = patchPos;
		}

		public int getPatchPos() {
			return this.patchPos;
		}
	}

	private void emitRestore(RestoreStatement restoreStatement) {
		String lineNumber = restoreStatement.getLineNumber();

		if (this.restoreMap.containsKey(lineNumber) == false) {
			this.restoreMap.put(lineNumber, new ArrayList<RestoreInfo>());
		}
		List<RestoreInfo> restoreInfos = this.restoreMap.get(lineNumber);
		restoreInfos.add(new RestoreInfo(this.o.pos() + 1));

		// _dataElementIndex := <patched index>
		int dataIndexFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_DATA_INDEX, "I");

		this.o.sipush(0x0000); // ...will be patched in flush()
		this.o.putstatic(dataIndexFieldRef);
	}

	private void flushRestore() {
		Map<String /* line number */, Integer /* dataInfoIndex */> dataInfoIndexMap = new LinkedHashMap<String, Integer>();
		int dataInfoIndex = 0;
		for (Entry<String /* line number */, List<String> /* constants */> e : this.dataMap.entrySet()) { // sorted by line number. DEFAULT_LABEL is first.
			String lineNumber = e.getKey();
			dataInfoIndexMap.put(lineNumber, dataInfoIndex);
			List<String> dataElements = e.getValue();
			dataInfoIndex += dataElements.size();
		}

		if (this.restoreMap.isEmpty() == false) {
			// add default entry for RESTORE statement
			dataInfoIndexMap.put(Parser.RESTORE_DEFAULT_LINE_NUMBER, 0);
		}

		for (Entry<String /* line number */, List<RestoreInfo> /* RestoreInfo */> e : this.restoreMap.entrySet()) {
			String lineNumber = e.getKey();
			if (dataInfoIndexMap.containsKey(lineNumber) == false) {
				throw new CompileException("No DATA to RESTORE at " + lineNumber);
			}
			List<RestoreInfo> restoreInfos = e.getValue();
			for (RestoreInfo restoreInfo : restoreInfos) {
				int patchPos = restoreInfo.getPatchPos();
				dataInfoIndex = dataInfoIndexMap.get(lineNumber);
				this.o.patch_u2(patchPos, dataInfoIndex);
			}
		}
	}

	private void emitStop() {
		this.o.goto_(LABEL_END);
	}

	private void emitSwap(SwapStatement swapStatement) {
		VariableNode var1 = swapStatement.getVariable1();
		VariableNode var2 = swapStatement.getVariable2();
		if (var1.getType() == NodeType.NUM) {
			emitNumExpressionToStack(var1);
			emitNumExpressionToStack(var2);
			emitFloatFromStackToNumVariable(var1);
			emitFloatFromStackToNumVariable(var2);
		} else if (var1.getType() == NodeType.STR) {
			emitStrExpressionToStack(var1);
			emitStrExpressionToStack(var2);
			emitCharsFromStackToStrVariable(var1);
			emitCharsFromStackToStrVariable(var2);
		}
	}

	private static class WhileInfo {
		private final String whileLabel;
		private final int patchPosToSkipWhileWendLoop;

		public WhileInfo(String whileLabel, int patchPosToSkipWhileWendLoop) {
			this.whileLabel = whileLabel;
			this.patchPosToSkipWhileWendLoop = patchPosToSkipWhileWendLoop;
		}

		public String getWhileLabel() {
			return this.whileLabel;
		}

		public int getPatchPosToSkipWhileWendLoop() {
			return this.patchPosToSkipWhileWendLoop;
		}
	}

	private void emitWhile(WhileStatement whileStatement) {
		INode numExpr = whileStatement.getExpression();
		String whileLabel = "_while" + ByteOutStream.generateLabel();
		this.o.label(whileLabel);
		emitNumExpressionToStack(numExpr);
		this.o.fconst_0();
		this.o.fcmpg();

		this.o.ifeq(); // ifeq(...)
		int patchPosToSkipWhileWendLoop = this.o.pos();
		this.o.write_u2(0x0000); // ...will be patched

		this.whileCompiletimeStack.push(new WhileInfo(whileLabel, patchPosToSkipWhileWendLoop));
	}

	private void emitWend() {
		if (this.whileCompiletimeStack.isEmpty()) {
			throw new CompileException("WEND without WHILE");
		}
		WhileInfo whileInfo = this.whileCompiletimeStack.pop();
		String whileLabel = whileInfo.getWhileLabel();
		int patchPosToSkipWhileWendLoop = whileInfo.getPatchPosToSkipWhileWendLoop();

		this.o.goto_(whileLabel);
		int branchOffset = branchOffset(patchPosToSkipWhileWendLoop, this.o.pos());
		this.o.patch_u2(patchPosToSkipWhileWendLoop, branchOffset);
	}

	private void flushWhileWend() {
		while (this.whileCompiletimeStack.isEmpty() == false) {
			WhileInfo whileInfo = this.whileCompiletimeStack.pop();
			int patchPosToSkipWhileWendLoop = whileInfo.getPatchPosToSkipWhileWendLoop();
			this.o.patchThereToLabel(patchPosToSkipWhileWendLoop, LABEL_END);
		}
	}

	/// HELPER METHODS ///////////////////////////////////////////////////////////

	private void emitPrintStringConstFromStack(String string) {
		if (string.length() == 1) { // Code size optimization: may save inclusion of library methods
			this.o.iconst(string.charAt(0));
			this.libraryManager.getMethod(LibraryManager.MethodEnum.PRINT_CHAR_FROM_STACK).emitCall(this.o);
		} else {
			this.o.ldc(this.classModel.getStringIndex(string));
			this.libraryManager.getMethod(LibraryManager.MethodEnum.PRINT_STRING_FROM_STACK).emitCall(this.o);
		}
	}

	private void emitFloatFromStackToNumVariable(VariableNode numVar) {
		String varName = numVar.getVariableName();

		if (numVar instanceof LocalVariableNode) {
			LocalVariableNode numLocVar = (LocalVariableNode) numVar;
			this.o.fstore_opt(numLocVar.getLocalIndex());
		} else {
			int numDims = numVar.getDimExpressions().length;
			if (numDims == 0) {
				this.o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "F"));
			} else if (numDims == 1) {
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[F"));
				emitNumExpressionToStack(numVar.getDimExpressions()[0]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.STORE_FLOAT_IN_1D_ARRAY).emitCall(this.o);
			} else if (numDims == 2) {
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[F"));
				emitNumExpressionToStack(numVar.getDimExpressions()[0]);
				emitNumExpressionToStack(numVar.getDimExpressions()[1]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.STORE_FLOAT_IN_2D_ARRAY).emitCall(this.o);
			}
		}
	}

	private void emitCharsFromStackToStrVariable(VariableNode strVar) {
		String varName = strVar.getVariableName();

		if (strVar instanceof LocalVariableNode) {
			LocalVariableNode strLocVar = (LocalVariableNode) strVar;
			this.o.astore_opt(strLocVar.getLocalIndex());
		} else {
			int numDims = strVar.getDimExpressions().length;
			if (numDims == 0) {
				this.o.putstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[C"));
			} else if (numDims == 1) {
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[C"));
				emitNumExpressionToStack(strVar.getDimExpressions()[0]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.STORE_STRING_IN_1D_ARRAY).emitCall(this.o);
			} else if (numDims == 2) {
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[[C"));
				emitNumExpressionToStack(strVar.getDimExpressions()[0]);
				emitNumExpressionToStack(strVar.getDimExpressions()[1]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.STORE_STRING_IN_2D_ARRAY).emitCall(this.o);
			}
		}
	}

	private void emitNumExpressionToStack(INode expr) {
		if (expr instanceof BinaryNode) {
			BinaryNode binNode = (BinaryNode) expr;
			INode leftNode = binNode.getLeftNode();
			INode rightNode = binNode.getRightNode();
			if (leftNode.getType() == NodeType.NUM) { // not fully correct, but we use the return type of the left node as a result type indicator
				emitNumExpressionToStack(leftNode);
				emitNumExpressionToStack(rightNode);
			} else if (leftNode.getType() == NodeType.STR) {
				emitStrExpressionToStack(leftNode);
				emitStrExpressionToStack(rightNode);
			}

			Token opToken = binNode.getOp();
			if (isArithmeticOpToken(opToken)) {
				if (opToken == Token.ADD) {
					this.o.fadd();
				} else if (opToken == Token.SUBTRACT) {
					this.o.fsub();
				} else if (opToken == Token.MULTIPLY) {
					this.o.fmul();
				} else if (opToken == Token.DIVIDE) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.DIVISION).emitCall(this.o);
				} else if (opToken == Token.INT_DIVIDE) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.INTEGER_DIVISION).emitCall(this.o);
				} else if (opToken == Token.MOD) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.MOD).emitCall(this.o);
				} else if (opToken == Token.POWER) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.POWER).emitCall(this.o);
				}
			} else if (isLogicalBinaryOpToken(opToken)) {
				if (opToken == Token.AND) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.AND).emitCall(this.o);
				} else if (opToken == Token.OR) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.OR).emitCall(this.o);
				} else if (opToken == Token.XOR) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.XOR).emitCall(this.o);
				}
			} else if (isNumRelationalOpToken(opToken)) {
				String label1 = ByteOutStream.generateLabel();
				String label2 = ByteOutStream.generateLabel();

				this.o.fcmpg();
				if (opToken == Token.LESS) {
					this.o.iflt(label1);
				} else if (opToken == Token.LESS_OR_EQUAL) {
					this.o.ifle(label1);
				} else if (opToken == Token.EQUAL) {
					this.o.ifeq(label1);
				} else if (opToken == Token.GREATER_OR_EQUAL) {
					this.o.ifge(label1);
				} else if (opToken == Token.GREATER) {
					this.o.ifgt(label1);
				} else if (opToken == Token.NOT_EQUAL) {
					this.o.ifne(label1);
				}
				this.o.fconst_0();
				this.o.goto_(label2);
				this.o.label(label1);
				this.o.fconst_1();
				this.o.fneg();
				this.o.label(label2);
			} else if (isStrRelationalOpToken(opToken)) {
				if (opToken == Token.STRING_LESS) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_LESS_THAN).emitCall(this.o);
				} else if (opToken == Token.STRING_LESS_OR_EQUAL) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_LESS_OR_EQUAL).emitCall(this.o);
				} else if (opToken == Token.STRING_EQUAL) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_EQUAL).emitCall(this.o);
				} else if (opToken == Token.STRING_GREATER_OR_EQUAL) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_GREATER_OR_EQUAL).emitCall(this.o);
				} else if (opToken == Token.STRING_GREATER) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_GREATER_THAN).emitCall(this.o);
				} else if (opToken == Token.STRING_NOT_EQUAL) {
					this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_NOT_EQUAL).emitCall(this.o);
				}
			}
		} else if (expr instanceof UnaryNode) {
			UnaryNode unaryNode = (UnaryNode) expr;
			INode nodeExpr = unaryNode.getArgNode();
			emitNumExpressionToStack(nodeExpr);

			Token opToken = unaryNode.getOp();
			if (opToken == Token.NOT) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.NOT).emitCall(this.o);
			} else if (opToken == Token.OPEN) {
				// do nothing
			} else if (opToken == Token.UNARY_MINUS) {
				this.o.fneg();
			}
		} else if (expr instanceof NumNode) {
			NumNode numNode = (NumNode) expr;
			float floatValue = numNode.getValue();
			if (floatValue == 0.0f) {
				this.o.fconst_0();
			} else if (floatValue == 1.0f) {
				this.o.fconst_1();
			} else if (floatValue == 2.0f) {
				this.o.fconst_2();
			} else {
				this.o.ldc(this.classModel.getFloatIndex(floatValue));
			}
		} else if (expr instanceof VariableNode) {
			emitFloatFromNumVariableToStack((VariableNode) expr);
		} else if (expr instanceof FunctionNode) {
			FunctionNode functionNode = (FunctionNode) expr;
			FunctionToken functionToken = functionNode.getFunctionToken();

			INode[] args = functionNode.getArgNodes();
			NodeType[] argTypes = functionToken.getArgTypes();
			for (int i = 0; i < args.length; i++) {
				INode arg = args[i];
				NodeType nodeType = argTypes[i];
				if (nodeType == NodeType.NUM) {
					emitNumExpressionToStack(arg);
				} else if (nodeType == NodeType.STR) {
					emitStrExpressionToStack(arg);
				}
			}
			if (functionToken == FunctionToken.ABS) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.ABS).emitCall(this.o);
			} else if (functionToken == FunctionToken.ASC) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.ASC).emitCall(this.o);
			} else if (functionToken == FunctionToken.ATN) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.ATN).emitCall(this.o);
			} else if (functionToken == FunctionToken.COS) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.COS).emitCall(this.o);
			} else if (functionToken == FunctionToken.EXP) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.EXP).emitCall(this.o);
			} else if (functionToken == FunctionToken.FIX) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.FIX).emitCall(this.o);
			} else if (functionToken == FunctionToken.INSTR) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.INSTR).emitCall(this.o);
			} else if (functionToken == FunctionToken.INT) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.INT).emitCall(this.o);
			} else if (functionToken == FunctionToken.LEN) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.LEN).emitCall(this.o);
			} else if (functionToken == FunctionToken.LOG) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.LOG).emitCall(this.o);
			} else if (functionToken == FunctionToken.POS) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.POS).emitCall(this.o);
			} else if (functionToken == FunctionToken.RND) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.RND).emitCall(this.o);
			} else if (functionToken == FunctionToken.SGN) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.SGN).emitCall(this.o);
			} else if (functionToken == FunctionToken.SIN) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.SIN).emitCall(this.o);
			} else if (functionToken == FunctionToken.SQR) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.SQR).emitCall(this.o);
			} else if (functionToken == FunctionToken.TAN) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.TAN).emitCall(this.o);
			} else if (functionToken == FunctionToken.VAL) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.VAL).emitCall(this.o);
			}
		} else if (expr instanceof FnFunctionNode) {
			emitFunctionCall((FnFunctionNode) expr);
		} else {
			throw new UnsupportedOperationException("Unknown number expression.");
		}
	}

	private void emitFloatFromNumVariableToStack(VariableNode numVar) {
		String varName = numVar.getVariableName();

		if (this.localFnVariables.containsKey(varName)) {
			int localVarIndex = this.localFnVariables.get(varName).intValue();
			this.o.fload_opt(localVarIndex);
		} else {
			int numDims = numVar.getDimExpressions().length;
			if (numDims == 0) {
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "F"));
			} else if (numDims == 1) {
				this.arrVariables.put(varName, "[[F");
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[F"));
				emitNumExpressionToStack(numVar.getDimExpressions()[0]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.LOAD_FLOAT_FROM_1D_ARRAY).emitCall(this.o);
			} else if (numDims == 2) {
				this.arrVariables.put(varName, "[[[F");
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[F"));
				emitNumExpressionToStack(numVar.getDimExpressions()[0]);
				emitNumExpressionToStack(numVar.getDimExpressions()[1]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.LOAD_FLOAT_FROM_2D_ARRAY).emitCall(this.o);
			}
		}
	}

	private void emitStrExpressionToStack(INode expr) {
		if (expr instanceof BinaryNode) {
			BinaryNode binNode = (BinaryNode) expr;
			Token opToken = binNode.getOp();
			if (opToken == Token.STRING_ADD) {
				emitStrExpressionToStack(binNode.getLeftNode());
				emitStrExpressionToStack(binNode.getRightNode());
				this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_CONCATENATION).emitCall(this.o); // TOOD
			}
		} else if (expr instanceof StrNode) {
			StrNode strNode = (StrNode) expr;
			String string = strNode.getValue();
			this.o.ldc(this.classModel.getStringIndex(string));
			this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_TO_CHARS).emitCall(this.o);
		} else if (expr instanceof VariableNode) {
			emitCharsFromStrVariableToStack((VariableNode) expr);
		} else if (expr instanceof FunctionNode) {
			FunctionNode functionNode = (FunctionNode) expr;
			FunctionToken functionToken = functionNode.getFunctionToken();

			INode[] args = functionNode.getArgNodes();
			NodeType[] argTypes = functionToken.getArgTypes();
			for (int i = 0; i < args.length; i++) {
				INode arg = args[i];
				NodeType nodeType = argTypes[i];
				if (nodeType == NodeType.NUM) {
					emitNumExpressionToStack(arg);
				} else if (nodeType == NodeType.STR) {
					emitStrExpressionToStack(arg);
				}
			}
			if (functionToken == FunctionToken.CHR) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.CHR).emitCall(this.o);
			} else if (functionToken == FunctionToken.LEFT) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.LEFT).emitCall(this.o);
			} else if (functionToken == FunctionToken.MID) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.MID).emitCall(this.o);
			} else if (functionToken == FunctionToken.RIGHT) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.RIGHT).emitCall(this.o);
			} else if (functionToken == FunctionToken.SPACE) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.SPACE).emitCall(this.o);
			} else if (functionToken == FunctionToken.STR) {
				this.libraryManager.getMethod(LibraryManager.MethodEnum.STR).emitCall(this.o);
			}
		} else if (expr instanceof FnFunctionNode) {
			emitFunctionCall((FnFunctionNode) expr);
		} else {
			throw new UnsupportedOperationException("Unknown string expression.");
		}
	}

	private void emitCharsFromStrVariableToStack(VariableNode strVar) {
		String varName = strVar.getVariableName();

		if (this.localFnVariables.containsKey(varName)) {
			int localVarIndex = this.localFnVariables.get(varName).intValue();
			this.o.aload_opt(localVarIndex);
		} else {
			int numDims = strVar.getDimExpressions().length;
			if (numDims == 0) {
				this.strVariables.add(varName);
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[C"));
			} else if (numDims == 1) {
				this.arrVariables.put(varName, "[[[C");
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[C"));
				emitNumExpressionToStack(strVar.getDimExpressions()[0]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.LOAD_STRING_FROM_1D_ARRAY).emitCall(this.o);
			} else if (numDims == 2) {
				this.arrVariables.put(varName, "[[[[C");
				this.o.getstatic(this.classModel.addFieldAndGetFieldRefIndex(varName, "[[[[C"));
				emitNumExpressionToStack(strVar.getDimExpressions()[0]);
				emitNumExpressionToStack(strVar.getDimExpressions()[1]);
				this.libraryManager.getMethod(LibraryManager.MethodEnum.LOAD_STRING_FROM_2D_ARRAY).emitCall(this.o);
			}
		}
	}

	private void emitFunctionCall(FnFunctionNode fnFuncNode) {
		INode[] funcArgExprs = fnFuncNode.getFuncArgExprs();
		for (INode funcArgExpr : funcArgExprs) {
			if (funcArgExpr.getType() == NodeType.NUM) {
				emitNumExpressionToStack(funcArgExpr);
			} else if (funcArgExpr.getType() == NodeType.STR) {
				emitStrExpressionToStack(funcArgExpr);
			}
		}

		String methodName = fnFuncNode.getFuncName();
		String descriptor = "(";
		for (INode arg : funcArgExprs) {
			descriptor += (arg.getType() == NodeType.NUM) ? "F" : "[C";
		}
		descriptor += ")";
		descriptor += (fnFuncNode.getType() == NodeType.NUM) ? "F" : "[C";
		this.o.invokestatic(this.classModel.getMethodRefIndex(methodName, descriptor));
	}

	private boolean isArithmeticOpToken(Token opToken) {
		if ((opToken == Token.ADD) || //
				(opToken == Token.SUBTRACT) || //
				(opToken == Token.MULTIPLY) || //
				(opToken == Token.DIVIDE) || //
				(opToken == Token.INT_DIVIDE) || //
				(opToken == Token.MOD) || //
				(opToken == Token.POWER)) {
			return true;
		}
		return false;
	}

	private boolean isLogicalBinaryOpToken(Token opToken) {
		if ((opToken == Token.AND) || //
				(opToken == Token.OR) || //
				(opToken == Token.XOR)) {
			return true;
		}
		return false;
	}

	private boolean isStrRelationalOpToken(Token opToken) {
		if ((opToken == Token.STRING_EQUAL) || //
				(opToken == Token.STRING_NOT_EQUAL) || //
				(opToken == Token.STRING_LESS) || //
				(opToken == Token.STRING_LESS_OR_EQUAL) || //
				(opToken == Token.STRING_GREATER_OR_EQUAL) || //
				(opToken == Token.STRING_GREATER)) {
			return true;
		}
		return false;
	}

	private boolean isNumRelationalOpToken(Token opToken) {
		if ((opToken == Token.EQUAL) || //
				(opToken == Token.NOT_EQUAL) || //
				(opToken == Token.LESS) || //
				(opToken == Token.LESS_OR_EQUAL) || //
				(opToken == Token.GREATER_OR_EQUAL) || //
				(opToken == Token.GREATER)) {
			return true;
		}
		return false;
	}

	private boolean isNumRelationalExpression(INode expr) {
		if (expr instanceof BinaryNode) {
			BinaryNode binaryNode = (BinaryNode) expr;
			Token opToken = binaryNode.getOp();
			return isNumRelationalOpToken(opToken);
		}
		return false;
	}

	private boolean isFunctionExpressionOf(INode expr, FunctionToken functionToken) {
		if (expr instanceof FunctionNode) {
			if (((FunctionNode) expr).getFunctionToken() == functionToken) {
				return true;
			}
		}
		return false;
	}
}
