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

package de.lorenzwiest.basiccompiler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.CompileException;
import de.lorenzwiest.basiccompiler.parser.Parser;
import de.lorenzwiest.basiccompiler.parser.nodes.INode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.BinaryNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.FnFunctionNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.FunctionNode;
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

public class CodeFormatter {
	private List<Statement> statements;
	private Map<String, String> lineNumberMap;
	private Set<String> lineNumbersBranchedTo;

	public void formatToFile(List<Statement> statements, String outFilename) throws IOException {
		this.statements = statements;
		BufferedWriter o = null;
		try {
			o = new BufferedWriter(new FileWriter(outFilename));
			this.lineNumberMap = createLineNumberMap();
			this.lineNumbersBranchedTo = createLineNumberBranchedToSet();
			internalFormat(this.statements.toArray(new Statement[0]), o);
		} catch (IOException e) {
			throw e;
		} finally {
			if (o != null) {
				try {
					o.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	private void internalFormat(Statement[] statements, BufferedWriter o) throws IOException {
		boolean isFirstLineNumber = true;
		boolean isFirstStatement = true;

		for (Statement statement : statements) {
			if (isFirstStatement) {
				isFirstStatement = false;
			} else {
				if ((statement instanceof LineNumberStatement) == false) {
					o.write(" : ");
				}
			}

			if (statement instanceof DataStatement) {
				DataStatement s = (DataStatement) statement;
				o.write("DATA ");
				o.write(toCsvString(s.getConstants()));
			} else if (statement instanceof DefFnStatement) {
				DefFnStatement s = (DefFnStatement) statement;
				o.write("DEF ");
				o.write(s.getFuncName());
				o.write("(");
				o.write(toCsvString(s.getFuncVars()));
				o.write(")=");
				o.write(toString(s.getFuncExpr()));
			} else if (statement instanceof DimStatement) {
				DimStatement s = (DimStatement) statement;
				o.write("DIM ");
				o.write(toCsvString(s.getVariables()));
			} else if (statement instanceof EndStatement) {
				o.write("END");
			} else if (statement instanceof ForStatement) {
				ForStatement s = (ForStatement) statement;
				o.write("FOR ");
				o.write(toString(s.getLoopVariable()));
				o.write("=");
				o.write(toString(s.getStartExpression()));
				o.write(" TO ");
				o.write(toString(s.getEndExpression()));
				INode stepExpression = s.getStepExpression();
				if ((stepExpression instanceof NumNode) && (((NumNode) stepExpression).getStrValue().equals("1") == false)) {
					o.write(" STEP ");
					o.write(toString(stepExpression));
				}
			} else if (statement instanceof GosubStatement) {
				GosubStatement s = (GosubStatement) statement;
				o.write("GOSUB");
				o.write(" " + this.lineNumberMap.get(s.getLineNumber()));
			} else if (statement instanceof GotoStatement) {
				GotoStatement s = (GotoStatement) statement;
				o.write("GOTO");
				o.write(" " + this.lineNumberMap.get(s.getLineNumber()));
			} else if (statement instanceof IfStatement) {
				IfStatement s = (IfStatement) statement;
				o.write("IF ");
				o.write(toString(s.getExpression()));
				o.write(" THEN ");
				Statement[] thenStatements = s.getThenStatements();
				if ((thenStatements.length == 1) && (thenStatements[0] instanceof GotoStatement)) {
					GotoStatement gotoStatement = (GotoStatement) thenStatements[0];
					o.write(this.lineNumberMap.get(gotoStatement.getLineNumber()));
				} else {
					internalFormat(thenStatements, o);
				}
				Statement[] elseStatements = s.getElseStatements();
				if (elseStatements.length > 0) {
					o.write(" ELSE ");
					if ((elseStatements.length == 1) && (elseStatements[0] instanceof GotoStatement)) {
						GotoStatement gotoStatement = (GotoStatement) elseStatements[0];
						o.write(this.lineNumberMap.get(gotoStatement.getLineNumber()));
					} else {
						internalFormat(elseStatements, o);
					}
				}
			} else if (statement instanceof InputStatement) {
				InputStatement s = (InputStatement) statement;
				o.write("INPUT ");
				String prompt = s.getPrompt();
				if (prompt != null) {
					o.write("\"" + prompt + "\"");
					o.write(s.getSeparator().getChars());
				}
				o.write(toCsvString(s.getVariables()));
			} else if (statement instanceof LetStatement) {
				LetStatement s = (LetStatement) statement;
				if (s.isImplicit() == false) {
					o.write("LET ");
				}
				o.write(toString(s.getVariable()));
				o.write("=");
				o.write(toString(s.getExpression()));
			} else if (statement instanceof LineNumberStatement) {
				LineNumberStatement s = (LineNumberStatement) statement;
				if (isFirstLineNumber) {
					isFirstLineNumber = false;
				} else {
					o.write(Compiler.CR); // EOL previous line of statements
				}
				isFirstStatement = true;
				String lineNumber = String.valueOf(s.getLineNumber());
				o.write(this.lineNumberMap.get(lineNumber) + " ");
				// boolean isLineNumberBranchedTo = isLineNumberBranchedTo(lineNumber);
				// o.write(isLineNumberBranchedTo ? "@" : " ");
			} else if (statement instanceof NextStatement) {
				NextStatement s = (NextStatement) statement;
				o.write("NEXT");
				VariableNode[] loopVariables = s.getLoopVariables();
				if (loopVariables.length > 0) {
					o.write(" ");
					o.write(toCsvString(loopVariables));
				}
			} else if (statement instanceof OnGosubStatement) {
				OnGosubStatement s = (OnGosubStatement) statement;
				o.write("ON ");
				o.write(toString(s.getExpression()));
				o.write(" GOSUB ");
				o.write(toCsvStringLineNumbers(s.getLineNumbers()));
			} else if (statement instanceof OnGotoStatement) {
				OnGotoStatement s = (OnGotoStatement) statement;
				o.write("ON ");
				o.write(toString(s.getExpression()));
				o.write(" GOTO ");
				o.write(toCsvStringLineNumbers(s.getLineNumbers()));
			} else if (statement instanceof PrintStatement) {
				PrintStatement s = (PrintStatement) statement;
				o.write("PRINT");
				INode[] expressions = s.getExpressions();
				if (expressions.length > 0) {
					o.write(" ");
				}
				INode prevExpr = null;
				for (INode expression : expressions) {
					if (isPrintExpression(prevExpr) && isPrintExpression(expression)) {
						o.write(" ");
					}
					o.write(toString(expression));
					prevExpr = expression;
				}
			} else if (statement instanceof ReadStatement) {
				ReadStatement s = (ReadStatement) statement;
				o.write("READ ");
				o.write(toCsvString(s.getVariables()));
			} else if (statement instanceof RemStatement) {
				RemStatement s = (RemStatement) statement;
				o.write("REM");
				o.write(s.getComment());
			} else if (statement instanceof RestoreStatement) {
				RestoreStatement s = (RestoreStatement) statement;
				o.write("RESTORE");
				String lineNumber = s.getLineNumber();
				if (lineNumber.equals(Parser.RESTORE_DEFAULT_LINE_NUMBER) == false) {
					o.write(" ");
					o.write(this.lineNumberMap.get(lineNumber));
				}
			} else if (statement instanceof ReturnStatement) {
				o.write("RETURN");
			} else if (statement instanceof StopStatement) {
				o.write("STOP");
			} else if (statement instanceof SwapStatement) {
				SwapStatement s = (SwapStatement) statement;
				o.write("SWAP ");
				o.write(toString(s.getVariable1()));
				o.write(",");
				o.write(toString(s.getVariable2()));
			} else if (statement instanceof WendStatement) {
				o.write("WEND");
			} else if (statement instanceof WhileStatement) {
				WhileStatement s = (WhileStatement) statement;
				o.write("WHILE ");
				o.write(toString(s.getExpression()));
			} else {
				throw new CompileException("Cannot format unknown statement");
			}
		}
	}

	private boolean isPrintExpression(INode node) {
		if (node == null) {
			return false;
		}
		if (node instanceof TokenNode) {
			Token token = ((TokenNode) node).getToken();
			if (token == Token.SEMICOLON) {
				return false;
			}
			if (token == Token.COLON) {
				return false;
			}
		}
		return true;
	}

	private Map<String, String> createLineNumberMap() {
		Map<String /* old line number */, String /* new line number */> lineMap = new LinkedHashMap<String, String>();

		int startLineNr = 1000;
		int incLineNr = 10;

		for (Statement statement : this.statements) {
			if (statement instanceof LineNumberStatement) {
				LineNumberStatement s = (LineNumberStatement) statement;
				lineMap.put(String.valueOf(s.getLineNumber()), String.valueOf(startLineNr));
				startLineNr += incLineNr;
			}
		}
		return lineMap;
	}

	private Set<String> createLineNumberBranchedToSet() {
		Set<String /* old line number */> lineNumberBranchedToSet = new HashSet<String>();
		for (Statement statement : this.statements) {
			internalLineNumberBranchedToSet(lineNumberBranchedToSet, statement);
		}
		return lineNumberBranchedToSet;
	}

	private void internalLineNumberBranchedToSet(Set<String> lineNumberBranchedToSet, Statement statement) {
		if (statement instanceof GotoStatement) {
			GotoStatement s = (GotoStatement) statement;
			lineNumberBranchedToSet.add(s.getLineNumber());
		} else if (statement instanceof GosubStatement) {
			GosubStatement s = (GosubStatement) statement;
			lineNumberBranchedToSet.add(s.getLineNumber());
		} else if (statement instanceof OnGotoStatement) {
			OnGotoStatement s = (OnGotoStatement) statement;
			lineNumberBranchedToSet.addAll(Arrays.asList(s.getLineNumbers()));
		} else if (statement instanceof OnGosubStatement) {
			OnGosubStatement s = (OnGosubStatement) statement;
			lineNumberBranchedToSet.addAll(Arrays.asList(s.getLineNumbers()));
		} else if (statement instanceof IfStatement) {
			IfStatement s = (IfStatement) statement;
			Statement[] thenStatements = s.getThenStatements();
			if ((thenStatements.length == 1) && (thenStatements[0] instanceof GotoStatement)) {
				GotoStatement gotoStatement = (GotoStatement) thenStatements[0];
				lineNumberBranchedToSet.add(gotoStatement.getLineNumber());
			} else {
				for (Statement thenStatement : thenStatements) {
					internalLineNumberBranchedToSet(lineNumberBranchedToSet, thenStatement);
				}
			}
			Statement[] elseStatements = s.getElseStatements();
			if (elseStatements.length > 0) {
				if ((elseStatements.length == 1) && (elseStatements[0] instanceof GotoStatement)) {
					GotoStatement gotoStatement = (GotoStatement) elseStatements[0];
					lineNumberBranchedToSet.add(gotoStatement.getLineNumber());
				} else {
					for (Statement elseStatement : elseStatements) {
						internalLineNumberBranchedToSet(lineNumberBranchedToSet, elseStatement);
					}
				}
			}
		}
	}

	private boolean isLineNumberBranchedTo(String lineNumber) {
		return this.lineNumbersBranchedTo.contains(lineNumber);
	}

	private String toString(INode expression) {
		StringBuffer sb = new StringBuffer();
		if (expression instanceof BinaryNode) {
			BinaryNode node = (BinaryNode) expression;

			Token op = node.getOp();
			boolean isKeyword = false;
			if (op == Token.AND) {
				isKeyword = true;
			} else if (op == Token.OR) {
				isKeyword = true;
			} else if (op == Token.XOR) {
				isKeyword = true;
			} else if (op == Token.NOT) {
				isKeyword = true;
			} else if (op == Token.MOD) {
				isKeyword = true;
			}

			sb.append(toString(node.getLeftNode()));
			if (isKeyword) {
				sb.append(" ");
			}
			sb.append(op.getChars());
			if (isKeyword) {
				sb.append(" ");
			}
			sb.append(toString(node.getRightNode()));
		} else if (expression instanceof FunctionNode) {
			FunctionNode node = (FunctionNode) expression;
			FunctionToken functionToken = node.getFunctionToken();

			INode[] allArgExpressions = node.getArgNodes();
			//List<INode> argList = Arrays.asList(allArgExpressions);
			List<INode> argList = new ArrayList<INode>();
			for (INode argNode : allArgExpressions) {
				argList.add(argNode);
			}
			if (functionToken == FunctionToken.INSTR) {
				INode arg0 = allArgExpressions[0];
				if ((arg0 instanceof NumNode) && (((NumNode) arg0).getValue() == 1.0f)) {
					argList.remove(0);
				}
			} else if (functionToken == FunctionToken.MID) {
				INode arg2 = allArgExpressions[2];
				if ((arg2 instanceof NumNode) && (((NumNode) arg2).getValue() == 255.0f)) {
					argList.remove(2);
				}
			}
			INode[] argsExpressions = argList.toArray(new INode[0]);

			sb.append(functionToken.getChars());
			sb.append(toCsvString(argsExpressions));
			sb.append(")");
		} else if (expression instanceof NumNode) {
			NumNode node = (NumNode) expression;
			String strValue = node.getStrValue();
			if (strValue.startsWith(".")) {
				sb.append("0");
				sb.append(strValue);
			} else if (strValue.startsWith("-.")) {
				sb.append("-0" + strValue.substring(1));
			} else {
				sb.append(strValue);
			}
		} else if (expression instanceof StrNode) {
			StrNode node = (StrNode) expression;
			sb.append("\"");
			sb.append(node.getValue());
			sb.append("\"");
		} else if (expression instanceof TokenNode) {
			TokenNode node = (TokenNode) expression;
			sb.append(node.getToken().getChars());
		} else if (expression instanceof UnaryNode) {
			UnaryNode node = (UnaryNode) expression;
			Token op = node.getOp();
			INode unaryExpression = node.getArgNode();
			if (op == Token.NOT) {
				sb.append("NOT ");
				sb.append(toString(unaryExpression));
			} else if (op == Token.OPEN) {
				sb.append("(");
				sb.append(toString(unaryExpression));
				sb.append(")");
			} else if (op == Token.UNARY_MINUS) {
				sb.append("-");
				sb.append(toString(unaryExpression));
			}
		} else if (expression instanceof VariableNode) {
			VariableNode node = (VariableNode) expression;
			String varName = node.getVariableName();
			sb.append(varName);
			INode[] dimExpressions = node.getDimExpressions();
			sb.append(toCsvString(dimExpressions));
			if (dimExpressions.length > 0) {
				sb.append(")");
			}
		} else if (expression instanceof FnFunctionNode) {
			FnFunctionNode node = (FnFunctionNode) expression;
			String funcName = node.getFuncName();
			sb.append(funcName);
			sb.append("(");
			sb.append(toCsvString(node.getFuncArgExprs()));
			sb.append(")");
		}
		return sb.toString();
	}

	private String toCsvString(INode[] nodes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nodes.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(toString(nodes[i]));
		}
		return sb.toString();
	}

	private String toCsvString(String[] strings) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(strings[i]);
		}
		return sb.toString();
	}

	private String toCsvStringLineNumbers(String[] lineNumbers) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lineNumbers.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(this.lineNumberMap.get(lineNumbers[i]));
		}
		return sb.toString();
	}
}
