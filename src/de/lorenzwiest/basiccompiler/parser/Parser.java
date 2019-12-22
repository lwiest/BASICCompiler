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

package de.lorenzwiest.basiccompiler.parser;

import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.ABS;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.ASC;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.ATN;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.CHR;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.COS;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.EXP;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.FIX;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.INSTR;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.INT;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.LEFT;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.LEN;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.LOG;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.MID;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.POS;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.RIGHT;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.RND;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.SGN;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.SIN;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.SPACE;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.SPC;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.SQR;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.STR;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.TAB;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.TAN;
import static de.lorenzwiest.basiccompiler.parser.tokens.FunctionToken.VAL;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.ADD;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.AND;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.CLOSE;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.COLON;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.COMMA;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.DATA;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.DEF;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.DIM;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.DIVIDE;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.ELSE;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.END;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.EQUAL;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.FOR;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.GOSUB;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.GOTO;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.GREATER;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.GREATER_OR_EQUAL;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.IF;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.INPUT;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.INT_DIVIDE;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.LESS;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.LESS_OR_EQUAL;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.LET;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.MOD;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.MULTIPLY;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.NEXT;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.NOT;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.NOT_EQUAL;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.ON;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.OPEN;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.OR;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.POWER;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.PRINT;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.READ;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.REM;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.RESTORE;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.RETURN;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.SEMICOLON;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.STEP;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.STOP;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.SUBTRACT;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.SWAP;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.THEN;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.TO;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.WEND;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.WHILE;
import static de.lorenzwiest.basiccompiler.parser.tokens.Token.XOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lorenzwiest.basiccompiler.compiler.etc.CompileException;
import de.lorenzwiest.basiccompiler.parser.nodes.INode;
import de.lorenzwiest.basiccompiler.parser.nodes.NodeType;
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
import de.lorenzwiest.basiccompiler.parser.tokens.Token.TokenType;

public class Parser {
	private static final String BEFORE_FIRST_LINE_NUMBER = "";
	public static final String RESTORE_DEFAULT_LINE_NUMBER = "";

	private static final INode[] EMPTY_INODE_ARRAY = new INode[0];
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final Statement[] EMPTY_STATEMENT_ARRAY = new Statement[0];
	private static final VariableNode[] EMPTY_VARIABLE_NODE_ARRAY = new VariableNode[0];

	private final Map<String /* function name */, DefFnStatement> defFnMap = new HashMap<String, DefFnStatement>();
	private final Map<String /* function name */, FnFunctionNode> fnMap = new HashMap<String, FnFunctionNode>();

	private String stringToParse;

	private int pos;
	private Token token;
	private boolean hasCachedToken;

	private boolean isParsingDATAElement;

	private String currentLineNumber;

	//////////////////////////////////////////////////////////////////////////////

	private class ArrayVariableInfo {
		private int numDims;
		private boolean isUsedInDimStatement;

		public ArrayVariableInfo(int numDims, boolean isUsedInDimStatement) {
			this.numDims = numDims;
			this.isUsedInDimStatement = isUsedInDimStatement;
		}

		public int getNumDims() {
			return this.numDims;
		}

		public boolean isUsedInDimStatement() {
			return this.isUsedInDimStatement;
		}
	}

	//////////////////////////////////////////////////////////////////////////////

	private Map<String /* array variable name */, ArrayVariableInfo> arrayVariables = new HashMap<String, ArrayVariableInfo>();

	public Parser() {
		this.currentLineNumber = BEFORE_FIRST_LINE_NUMBER;
	}

	public List<Statement> parseLine(String stringToParse) {
		this.stringToParse = stringToParse;

		this.pos = 0;
		this.token = null;
		this.hasCachedToken = false;

		this.isParsingDATAElement = false;

		if (stringToParse.trim().isEmpty()) {
			return Collections.emptyList();
		}
		if (stringToParse.length() > 255) {
			throw new CompileException("Line has more than 255 characters.");
		}

		List<Statement> statements = new ArrayList<Statement>();
		statements.add(parseLineNumberStatement());
		statements.addAll(parseStatements());

		if (this.token != Token.END_OF_INPUT) {
			throw new CompileException("Not fully parsed. Invalid characters at position " + this.pos + ".");
		}
		return statements;
	}

	public void flush() {
		// verify that all FN have a DEF FN
		for (String fnFuncName : this.fnMap.keySet()) {
			if (this.defFnMap.containsKey(fnFuncName) == false) {
				throw new CompileException("Function " + fnFuncName + "() is undefined.");
			}
		}
	}

	private Statement parseLineNumberStatement() {
		String strLineNumber = parseLineNumber();
		if (strLineNumber == null) {
			throw new CompileException("Line has no or an invalid line number.");
		}
		return (new LineNumberStatement(strLineNumber));
	}

	private String parseLineNumber() {
		String strLineNumber = getLineNumber();
		if (strLineNumber != null) {
			int lineNumber = Integer.parseInt(strLineNumber);
			strLineNumber = String.valueOf(lineNumber);
			this.currentLineNumber = strLineNumber;
		}
		return strLineNumber;
	}

	// Parse statements //////////////////////////////////////////////////////////

	private List<Statement> parseStatements() {
		List<Statement> statements = new ArrayList<Statement>();
		do {
			Statement statement = parseStatement();
			if (statement == null) {
				break;
			}
			statements.add(statement);
		} while (isNextToken(COLON));
		return statements;
	}

	private Statement parseStatement() {
		Statement result;
		if (isNextToken(DATA)) {
			result = parseDATA();
		} else if (isNextToken(DEF)) {
			result = parseDEF();
		} else if (isNextToken(DIM)) {
			result = parseDIM();
		} else if (isNextToken(END)) {
			result = parseEND();
		} else if (isNextToken(FOR)) {
			result = parseFOR();
		} else if (isNextToken(IF)) {
			result = parseIF();
		} else if (isNextToken(INPUT)) {
			result = parseINPUT();
		} else if (isNextToken(GOTO)) {
			result = parseGOTO();
		} else if (isNextToken(GOSUB)) {
			result = parseGOSUB();
		} else if (isNextToken(LET)) {
			result = parseLET();
		} else if (isNextToken(NEXT)) {
			result = parseNEXT();
		} else if (isNextToken(ON)) {
			result = parseON();
		} else if (isNextToken(PRINT)) {
			result = parsePRINT();
		} else if (isNextToken(READ)) {
			result = parseREAD();
		} else if (isNextToken(REM)) {
			result = parseREM();
		} else if (isNextToken(RESTORE)) {
			result = parseRESTORE();
		} else if (isNextToken(RETURN)) {
			result = parseRETURN();
		} else if (isNextToken(STOP)) {
			result = parseSTOP();
		} else if (isNextToken(SWAP)) {
			result = parseSWAP();
		} else if (isNextToken(WEND)) {
			result = parseWEND();
		} else if (isNextToken(WHILE)) {
			result = parseWHILE();
		} else { // no keyword found
			result = parseImplicitLET();
		}
		if (result == null) {
			throw new CompileException("Cannot parse statement.");
		}
		return result;
	}

	private Statement parseDATA() {
		// DATA <constant>[,<constant>]*
		//   <constant> := "<string>" | ^[,:]*

		List<String> constants = new ArrayList<String>();
		do {
			StrNode strNode = parseDataElement();
			if (strNode == null) {
				throw new CompileException("Missing or invalid DATA element.");
			}
			String constant = strNode.getValue();
			constants.add(constant);
		} while (isNextToken(COMMA));

		return new DataStatement(this.currentLineNumber, constants.toArray(EMPTY_STRING_ARRAY));
	}

	private Statement parseDEF() {
		// DEF <fnFuncName>(<var>[,<var>]*)=<expression>

		NodeType fnFuncType = NodeType.STR;
		String fnFuncName = getStrFNFunctionName();
		if (fnFuncName == null) {
			fnFuncType = NodeType.NUM;
			fnFuncName = getNumFNFunctionName();
		}
		if (fnFuncName == null) {
			throw new CompileException("DEF FN: Missing or invalid function name. It must start with FN.");
		}

		List<VariableNode> fnFuncVars = new ArrayList<VariableNode>();
		do {
			VariableNode fnFuncVar = parseStrVar();
			if (fnFuncVar == null) {
				fnFuncVar = parseNumVar();
			}
			if (fnFuncVar == null) {
				throw new CompileException("DEF FN: Missing or invalid function variable with function " + fnFuncName + "().");
			}
			fnFuncVars.add(fnFuncVar);
		} while (isNextToken(COMMA));

		if (isNextToken(CLOSE) == false) {
			throw new CompileException("DEF FN: Missing closing parenthesis ()) in function " + fnFuncName + "().");
		}

		if (fnFuncVars.isEmpty()) {
			throw new CompileException("DEF FN: Function " + fnFuncName + "() has no function variables.");
		}

		if (isNextToken(EQUAL) == false) {
			throw new CompileException("DEF FN: Missing equal sign (=) in function " + fnFuncName + "().");
		}

		INode fnFuncExpr = parseExpr();
		if (fnFuncExpr == null) {
			throw new CompileException("DEF FN: Missing or invalid expression in function " + fnFuncName + "().");
		}

		if (fnFuncType != fnFuncExpr.getType()) {
			throw new CompileException("DEF FN: Function type and expression type of function " + fnFuncName + "() are not the same.");
		}

		if (this.defFnMap.containsKey(fnFuncName)) {
			throw new CompileException("DEF FN: Function " + fnFuncName + "() is already defined.");
		}

		DefFnStatement statement = new DefFnStatement(fnFuncName, fnFuncVars.toArray(EMPTY_VARIABLE_NODE_ARRAY), fnFuncExpr);

		// compare signatures of DEF FN<name> against previous FN<name> calls
		if (this.fnMap.containsKey(fnFuncName)) {
			FnFunctionNode fnFuncNode = this.fnMap.get(fnFuncName);

			if (fnFuncVars.size() != fnFuncNode.getFuncArgExprs().length) {
				throw new CompileException("DEF FN: Number of arguments of function " + fnFuncName + "() is not the same as in earlier call of function.");
			}

			for (int i = 0; i < fnFuncVars.size(); i++) {
				if (fnFuncVars.get(i).getType() != fnFuncNode.getFuncArgExprs()[i].getType()) {
					throw new CompileException("DEF FN: Argument #" + (i + 1) + " of function " + fnFuncName + "() has not the same type as argument in earlier call of function.");
				}
			}
		}

		this.defFnMap.put(fnFuncName, statement);
		return statement;
	}

	private Statement parseDIM() {
		// DIM <arrayVar>[,<arrayVar>]*

		List<VariableNode> arrayVars = new ArrayList<VariableNode>();
		do {
			VariableNode arrayVar = parseStrArrayVar();
			if (arrayVar == null) {
				arrayVar = parseNumArrayVar();
			}
			if (arrayVar == null) {
				throw new CompileException("Missing or invalid variable name(s) after DIM statement.");
			}

			String arrayVarName = arrayVar.getVariableName();
			if (this.arrayVariables.containsKey(arrayVarName)) {
				ArrayVariableInfo info = this.arrayVariables.get(arrayVarName);
				if (info.isUsedInDimStatement()) {
					NodeType arrayVarType = arrayVar.getType();
					if (arrayVarType == NodeType.NUM) {
						throw new CompileException("Number array variable " + arrayVarName + "() already has been used in a DIM statement.");
					} else if (arrayVarType == NodeType.STR) {
						throw new CompileException("String array variable " + arrayVarName + "() already has been used in a DIM statement.");
					}
				}
			}
			ArrayVariableInfo info = new ArrayVariableInfo(arrayVar.getDimExpressions().length, true /* isUsedInDimStatement */);
			this.arrayVariables.put(arrayVarName, info);

			arrayVars.add(arrayVar);
		} while (isNextToken(COMMA));

		return new DimStatement(arrayVars.toArray(EMPTY_VARIABLE_NODE_ARRAY));
	}

	private Statement parseEND() {
		// END

		return EndStatement.getInstance();
	}

	private Statement parseFOR() {
		// FOR <loopVarName>=<startExpr> TO <endExpr> [STEP <stepExpr>]

		VariableNode loopVar = parseNumVar();
		if (loopVar == null) {
			throw new CompileException("Missing or invalid loop variable in FOR statement.");
		}
		if (isNextToken(EQUAL) == false) {
			throw new CompileException("Missing equal sign (=) after loop variable in FOR statement.");
		}
		INode startExpr = parseNumExpr();
		if (startExpr == null) {
			throw new CompileException("Missing or invalid number expression for start value in FOR statement.");
		}
		if (isNextToken(TO) == false) {
			throw new CompileException("Missing TO in FOR statement.");
		}
		INode endExpr = parseNumExpr();
		if (endExpr == null) {
			throw new CompileException("Missing or invalid number expression for end value in FOR statement.");
		}
		INode stepExpr = null;
		if (isNextToken(STEP)) {
			stepExpr = parseNumExpr();
			if (stepExpr == null) {
				throw new CompileException("Missing or invalid number expression for step value in FOR statement.");
			}
		} else {
			stepExpr = NumNode.create("1");
		}
		return new ForStatement(loopVar, startExpr, endExpr, stepExpr);
	}

	private Statement parseIF() {
		// IF <numExpr> THEN {<line number>|<statements>} [ELSE {<line number>|<statements>}]
		// IF <numExpr> GOTO <line number> [ELSE {<line number>|<statements>}]

		INode numExpr = parseNumExpr();
		if (numExpr == null) {
			throw new CompileException("Missing or invalid condition expression in IF statement.");
		}

		List<Statement> thenStatements = new ArrayList<Statement>();
		if (isNextToken(THEN)) {
			String lineNumber = parseLineNumber();
			if (lineNumber != null) {
				thenStatements.add(new GotoStatement(lineNumber));
			} else {
				thenStatements = parseStatements();
			}
			if (thenStatements.isEmpty()) { // NOTE: Never happens, parseStatement() will throw an exception first
				throw new CompileException("Missing or invalid line number or statements after THEN in IF statement.");
			}
		} else if (isNextToken(GOTO)) {
			String lineNumber = parseLineNumber();
			if (lineNumber == null) {
				throw new CompileException("Missing or invalid line number after GOTO io IF statement.");
			}
			thenStatements.add(new GotoStatement(lineNumber));
		} else {
			throw new CompileException("Missing THEN or GOTO in IF statement.");
		}

		List<Statement> elseStatements = new ArrayList<Statement>();
		if (isNextToken(ELSE)) {
			String lineNumber = parseLineNumber();
			if (lineNumber != null) {
				elseStatements.add(new GotoStatement(lineNumber));
			} else {
				elseStatements = parseStatements();
			}
			if (elseStatements.isEmpty()) { // NOTE: Never happens, parseStatement() will throw an exception first
				throw new CompileException("Missing or invalid line number or statements after ELSE in IF statement.");
			}
		}
		return new IfStatement(numExpr, thenStatements.toArray(EMPTY_STATEMENT_ARRAY), elseStatements.toArray(EMPTY_STATEMENT_ARRAY));
	}

	private Statement parseINPUT() {
		// INPUT ["<prompt>"{,|;}] <varName>[,<varName>]*

		StrNode promptNode = parseStrConst(); // is null if not present
		String prompt = promptNode != null ? promptNode.getValue() : null;

		Token separator = SEMICOLON;
		if (promptNode != null) {
			separator = getNextTokenOutOf(COMMA, SEMICOLON);
			if (separator == null) {
				throw new CompileException("Missing comma (,) or semicolon (;) in INPUT statement.");
			}
		}

		List<VariableNode> vars = new ArrayList<VariableNode>();
		do {
			VariableNode var = parseStrVarOrStrArrayVar();
			if (var == null) {
				var = parseNumVarOrNumArrayVar();
			}
			if (var == null) {
				throw new CompileException("Missing or invalid variable in INPUT statement.");
			}
			vars.add(var);
		} while (isNextToken(COMMA));

		return new InputStatement(prompt, separator, vars.toArray(EMPTY_VARIABLE_NODE_ARRAY));
	}

	private Statement parseGOTO() {
		// GOTO <line number>

		String lineNumber = parseLineNumber();
		if (lineNumber == null) {
			throw new CompileException("Missing or invalid line number in GOTO statement.");
		}
		return new GotoStatement(lineNumber);
	}

	private Statement parseGOSUB() {
		// GOSUB <line number>

		String lineNumber = parseLineNumber();
		if (lineNumber == null) {
			throw new CompileException("Missing or invalid line number in GOSUB statement.");
		}
		return new GosubStatement(lineNumber);
	}

	private Statement parseImplicitLET() {
		// <varName>=<expr>

		return parseInternalLET(true /* isImplicitLET */);
	}

	private Statement parseLET() {
		// LET <varName>=<expr>

		return parseInternalLET(false /* isImplicitLET */);
	}

	private Statement parseInternalLET(boolean isImplicitLET) {
		Statement statement = null;

		FnFunctionNode strFnFuncNode = parseStrFN();
		if (strFnFuncNode != null) {
			throw new CompileException("You cannot assign an expression to a string function call.");
		}
		FnFunctionNode numFnFuncNode = parseNumFN();
		if (numFnFuncNode != null) {
			throw new CompileException("You cannot assign an expression to a number function call.");
		}

		VariableNode var = parseStrVarOrStrArrayVar();
		if (var != null) {
			if (isNextToken(EQUAL)) {
				INode strExpr = parseStrExpr();
				if (strExpr == null) {
					throw new CompileException("Missing or invalid expression after equal sign (=) or type mismatch. You can assign only a string expression to a string variable.");
				}
				statement = new LetStatement(var, strExpr, isImplicitLET);
			} else {
				throw new CompileException("Missing equal sign (=) after string variable name.");
			}
		}

		if (var == null) {
			var = parseNumVarOrNumArrayVar();
			if (var != null) {
				if (isNextToken(EQUAL)) {
					INode numExpr = parseNumExpr();
					if (numExpr == null) {
						throw new CompileException("Missing or invalid expression after equals (=) or type mismatch. You can assign only a number expression to a number variable.");
					}
					statement = new LetStatement(var, numExpr, isImplicitLET);
				} else {
					throw new CompileException("Missing equal sign (=) after number variable name.");
				}
			}
		}
		return statement;
	}

	private Statement parseNEXT() {
		// NEXT [<loopVarName>[,<loopVarName>]*]

		List<VariableNode> loopVars = new ArrayList<VariableNode>();
		do {
			VariableNode loopVar = parseNumVar();
			if (loopVar != null) {
				loopVars.add(loopVar);
			}
			if ((loopVar == null) && (loopVars.size() > 0)) {
				throw new CompileException("Missing or invalid number variable after comma (,) in NEXT statement.");
			}
		} while (isNextToken(COMMA));

		return new NextStatement(loopVars.toArray(EMPTY_VARIABLE_NODE_ARRAY));
	}

	private Statement parseON() {
		// ON <numExpr> {GOTO|GOSUB} <line number>[,<line number>]*

		List<String> lineNumbers = new ArrayList<String>();

		INode numExpr = parseNumExpr();
		if (numExpr == null) {
			throw new CompileException("Missing or invalid number expression in ON statement.");
		}

		boolean isOnGoto = false;
		if (isNextToken(GOTO)) {
			isOnGoto = true;
		} else if (isNextToken(GOSUB)) {
			isOnGoto = false;
		} else {
			throw new CompileException("Missing GOTO or GOSUB in ON statement.");
		}

		do {
			String lineNumber = parseLineNumber();
			if (lineNumber == null) {
				if (isOnGoto) {
					throw new CompileException("Missing or invalid line number in ON GOTO statement.");
				} else {
					throw new CompileException("Missing or invalid line number in ON GOSUB statement.");
				}
			}
			lineNumbers.add(lineNumber);
		} while (isNextToken(COMMA));

		if (isOnGoto) {
			return new OnGotoStatement(numExpr, lineNumbers.toArray(new String[lineNumbers.size()]));
		}
		return new OnGosubStatement(numExpr, lineNumbers.toArray(new String[lineNumbers.size()]));
	}

	private Statement parsePRINT() {
		// PRINT [[TAB(<numExpr>)|SPC(<numExpr>)|<expr>]{;|,}]*

		List<INode> exprs = new ArrayList<INode>();
		while (true) {
			INode expr = internalParseFunction(TAB);
			if (expr == null) {
				expr = internalParseFunction(SPC);
			}
			if (expr == null) {
				expr = parseExpr();
			}
			if (expr == null) {
				if (isNextToken(SEMICOLON)) {
					expr = TokenNode.create(SEMICOLON);
				}
			}
			if (expr == null) {
				if (isNextToken(COMMA)) {
					expr = TokenNode.create(COMMA);
				}
			}

			if (expr != null) {
				exprs.add(expr);
			} else {
				break;
			}
		}
		return new PrintStatement(exprs.toArray(EMPTY_INODE_ARRAY));
	}

	private Statement parseREAD() {
		// READ <varName>[,<varName>]*

		List<VariableNode> vars = new ArrayList<VariableNode>();
		do {
			VariableNode var = parseStrVarOrStrArrayVar();
			if (var == null) {
				var = parseNumVarOrNumArrayVar();
			}
			if (var == null) {
				throw new CompileException("Missing or invalid variable in READ statement.");
			}
			vars.add(var);
		} while (isNextToken(COMMA));

		return new ReadStatement(vars.toArray(EMPTY_VARIABLE_NODE_ARRAY));
	}

	private Statement parseREM() {
		// REM <comment>

		String comment = this.stringToParse.substring(this.pos);
		this.pos = this.stringToParse.length(); // place parse position at end of parse string
		return new RemStatement(comment);
	}

	private Statement parseRESTORE() {
		// RESTORE [<line number>]

		String lineNumber = parseLineNumber();
		if (lineNumber == null) {
			lineNumber = RESTORE_DEFAULT_LINE_NUMBER;
		}
		return new RestoreStatement(lineNumber);
	}

	private Statement parseRETURN() {
		// RETURN

		return ReturnStatement.getInstance();
	}

	private Statement parseSTOP() {
		// STOP

		return StopStatement.getInstance();
	}

	private Statement parseSWAP() {
		// SWAP <varName1>,<varName2>

		VariableNode var1 = parseStrVarOrStrArrayVar();
		if (var1 == null) {
			var1 = parseNumVarOrNumArrayVar();
		}
		if (var1 == null) {
			throw new CompileException("Missing or invalid first variable in SWAP statement.");
		}
		if (isNextToken(COMMA) == false) {
			throw new CompileException("Missing comma (,) in SWAP statement.");
		}

		VariableNode var2 = null;
		if (var1.getType() == NodeType.NUM) {
			var2 = parseNumVarOrNumArrayVar();
		} else if (var1.getType() == NodeType.STR) {
			var2 = parseStrVarOrStrArrayVar();
		}
		if (var2 == null) {
			throw new CompileException("Missing or invalid second variable in SWAP statement.");
		}
		return new SwapStatement(var1, var2);
	}

	private Statement parseWEND() {
		// WEND

		return WendStatement.getInstance();
	}

	private Statement parseWHILE() {
		// WHILE <numExpr>

		INode numExpr = parseNumExpr();
		if (numExpr == null) {
			throw new CompileException("Missing or invalid number expression in WHILE statement.");
		}
		return new WhileStatement(numExpr);
	}

	// Parse expressions /////////////////////////////////////////////////////////

	/**
	 * Expression grammar
	 *
	 * <expr> := <strExpr> | <numExpr>
	 *
	 * <strExpr> := <strAddExpr>
	 * <strAddExpr> := <strTerm> [+ <strTerm>]*
	 * <strTerm> := <strFN> | <strFunc> | <strOrStrArrayVar> | <strConst>
	 * <strFN> := FN<varName>$(<expr>[,<expr>]*)
	 * <strOrStrArrayVar> := <strArrayVar> | <strVar>
	 * <strArrayVar> := <varName>$(<numExpr>[,<numExpr>])
	 * <strVar> := <varName>$
	 *
	 * <numExpr> := <numNotExpr> [AND|OR|XOR <numNotExpr>]*
	 * <numNotExpr> := NOT <numNotExpr> | <numRelExpr>
	 * <numRelExpr> := <numAddSubExpr> [<>|<=|>=|<|=|> <numAddSubExpr>]*
	 * <numAddSubExpr> := <numModExpr> [+|- <numModExpr>]*
	 * <numModExpr> := <numIntDivExpr> [MOD <numIntDivExpr>]*
	 * <numIntDivExpr> := <numMultDivExpr> [\ <numMultDivExp>]*
	 * <numMultDivExpr> := <numPowerExpr> [*|/ <numPowerExpr>]*
	 * <numPowerExpr> := <numFactor> [^ <numFactor>]*
	 * <numFactor> := <numStrRelExpr> | <numFN> | <numFunc> | <numOrNumArrayVar> | <numConst> | -<numFactor> | +<numFactor> | (<numExpr>)
	 * <numStrRelExpr> := <numStrAddExpr> [<>|<=|>=|<|=|> <numStrAddExpr>]*
	 * <numStrAddExpr> := <strTerm> [+ <strTerm>]*
	 * <numFN> := FN<varName>(<expr>[,<expr>]*)
	 * <numOrNumArrayVar> := <numArrayVar> | <numVar>
	 * <numArrayVar> := <varName>(<numExpr>[,<numExpr>])
	 * <numVar> := <varName>
	 */

	// Parse expression //////////////////////////////////////////////////////////

	private INode parseExpr() {
		INode result = parseStrExpr(); // parse string expressions before number expressions
		if (result == null) {
			result = parseNumExpr();
		}
		return result;
	}

	// Parse string expression ///////////////////////////////////////////////////

	private INode parseStrExpr() {
		ParserState state = saveParserState();
		INode result = parseStrAddExpr();
		if (result != null) {
			Token token = getNextTokenOutOf(NOT_EQUAL, LESS_OR_EQUAL, GREATER_OR_EQUAL, LESS, EQUAL, GREATER); // op order matters!
			if (token != null) {
				restoreParserState(state);
				result = null; // unparse "<strAddExpr> <>|<=|>=|<|=|>", it's not a string expression but a num expression
			}
		}
		return result;
	}

	private INode parseStrAddExpr() {
		INode result = parseStrTerm();
		if (result != null) {
			while (isNextToken(ADD)) {
				INode node = parseStrTerm();
				if (node == null) {
					throw new CompileException("Missing or invalid string expression after string concatenation operator +.");
				}
				result = BinaryNode.create(ADD, result, node, NodeType.STR);
			}
		}
		return result;
	}

	private INode parseStrTerm() {
		INode result = parseStrFN(); // parse functions before variables
		if (result == null) {
			result = parseStrFunc();
		}
		if (result == null) { // parse array variable before non-array variables
			result = parseStrVarOrStrArrayVar();
		}
		if (result == null) {
			result = parseStrConst();
		}
		return result;
	}

	private FnFunctionNode parseStrFN() {
		String fnFuncName = getStrFNFunctionName();
		if (fnFuncName == null) {
			return null;
		}
		return internalParseFNFunction(fnFuncName, NodeType.STR);
	}

	private FunctionNode parseStrFunc() {
		FunctionNode result = internalParseFunction(CHR);
		if (result == null) {
			result = internalParseFunction(LEFT);
		}
		if (result == null) {
			if (isNextToken(MID)) {
				INode arg1Expr = parseStrExpr();
				if (arg1Expr == null) {
					throw new CompileException("MID$(): Missing or invalid first argument.");
				}
				if (isNextToken(COMMA) == false) {
					throw new CompileException("MID$(): Missing first comma separator (,).");
				}

				INode arg2Expr = parseNumExpr();
				if (arg2Expr == null) { // NOTE: Never happens, parseNumExpr() will throw an exception first
					throw new CompileException("MID$(): Missing or invalid second argument.");
				}

				INode arg3Expr;
				if (isNextToken(COMMA)) {
					arg3Expr = parseNumExpr();
					if (arg3Expr == null) { // NOTE: Never happens, parseNumExpr() will throw an exception first
						throw new CompileException("MID$(): Missing or invalid third argument.");
					}
				} else {
					arg3Expr = NumNode.create("255");
				}

				result = FunctionNode.create(MID, arg1Expr, arg2Expr, arg3Expr);
				if (isNextToken(CLOSE) == false) {
					throw new CompileException("MID$(): Missing closing parenthesis ()).");
				}
			}
		}
		if (result == null) {
			result = internalParseFunction(RIGHT);
		}
		if (result == null) {
			result = internalParseFunction(SPACE);
		}
		if (result == null) {
			result = internalParseFunction(STR);
		}
		return result;
	}

	private VariableNode parseStrVarOrStrArrayVar() {
		VariableNode strVar = parseStrArrayVar();
		if (strVar == null) {
			strVar = parseStrVar();
		}
		return strVar;
	}

	private VariableNode parseStrArrayVar() {
		String varName = getStrArrayVariableName();
		if (varName != null) {
			INode dim1Expr = parseNumExpr();
			if (dim1Expr == null) {
				throw new CompileException("Missing or invalid expression for first index of string array variable " + varName + "().");
			}
			if (isNextToken(COMMA)) {
				INode dim2Expr = parseNumExpr();
				if (dim2Expr == null) {
					throw new CompileException("Missing or invalid expression for second index of string array variable " + varName + "().");
				}
				if (isNextToken(CLOSE)) {
					if (this.arrayVariables.containsKey(varName)) {
						if (this.arrayVariables.get(varName).getNumDims() != 2) {
							throw new CompileException("String array variable " + varName + "() does not have a single index like in earlier part of code.");
						}
					} else {
						ArrayVariableInfo info = new ArrayVariableInfo(2, false /* isUsedInDimStatement */);
						this.arrayVariables.put(varName, info);
					}
					return VariableNode.create(varName, NodeType.STR, dim1Expr, dim2Expr);
				}
				throw new CompileException("Missing closing parenthesis ()) after second index of string array variable " + varName + "().");
			}
			if (isNextToken(CLOSE)) {
				if (this.arrayVariables.containsKey(varName)) {
					if (this.arrayVariables.get(varName).getNumDims() != 1) {
						throw new CompileException("String array variable " + varName + "() does not have two indexes like in earlier part of code.");
					}
				} else {
					ArrayVariableInfo info = new ArrayVariableInfo(1, false /* isUsedInDimStatement */);
					this.arrayVariables.put(varName, info);
				}
				return VariableNode.create(varName, NodeType.STR, dim1Expr);
			}
			throw new CompileException("Missing closing parenthesis ()) after first index of string array variable " + varName + "().");
		}
		return null;
	}

	private VariableNode parseStrVar() {
		String varName = getStrVariableName();
		if (varName != null) {
			return VariableNode.createVariableNode(varName, NodeType.STR);
		}
		return null;
	}

	private StrNode parseStrConst() {
		String strConst = getStrConstant();
		if (strConst != null) {
			if (strConst.length() > 255) { // NOTE: Never happens, code lines are 255 chars maximum
				throw new CompileException("String constant has more than 255 characters.");
			}
			return StrNode.create(strConst);
		}
		return null;
	}

	// Parse number expression ///////////////////////////////////////////////////

	private INode parseNumExpr() {
		INode result = parseNumNotExpr();
		if (result != null) {
			Token token;

			// the while loop turns a left-recursive (= right-associative) binary
			// operation into a right-recursive (= left-associative) operation by
			// holding back the recursive descent until all adjacent operators of the
			// same production level are parsed.
			//
			// sample expression: 1 - 2 - 3
			// left-recursive:    1 - ( 2 - 3 ) = 2  (wrong)
			// right-recursive:   ( 1 - 2 ) - 3 = -4 (correct)

			while ((token = getNextTokenOutOf(AND, OR, XOR)) != null) {
				INode node = parseNumNotExpr();
				if (node == null) {
					throw new CompileException("Missing or invalid number expression after logical operator " + token.getChars() + ".");
				}
				result = BinaryNode.create(token, result, node, NodeType.NUM);
			}
		}
		return result;
	}

	private INode parseNumNotExpr() {
		INode result = null;
		if (isNextToken(NOT)) {
			result = UnaryNode.create(NOT, parseNumNotExpr());
		} else {
			result = parseNumRelExpr();
		}
		return result;
	}

	private INode parseNumRelExpr() {
		INode result = parseNumAddSubExpr();
		if (result != null) {
			Token token;
			while ((token = getNextTokenOutOf(NOT_EQUAL, LESS_OR_EQUAL, GREATER_OR_EQUAL, LESS, EQUAL, GREATER)) != null) { // op order matters!
				INode node = parseNumAddSubExpr();
				if (node == null) {
					throw new CompileException("Missing or invalid number expression after relational operator " + token.getChars() + ".");
				}
				result = BinaryNode.create(token, result, node, NodeType.NUM);
			}
		}
		return result;
	}

	private INode parseNumAddSubExpr() {
		INode result = parseNumModExpr();
		if (result != null) {
			Token token;
			while ((token = getNextTokenOutOf(ADD, SUBTRACT)) != null) {
				INode node = parseNumModExpr();
				if (node == null) {
					throw new CompileException("Missing or invalid number expression after arithmetic operator " + token.getChars() + ".");
				}
				result = BinaryNode.create(token, result, node, NodeType.NUM);
			}
		}
		return result;
	}

	private INode parseNumModExpr() {
		INode result = parseNumIntDivExpr();
		if (result != null) {
			while (isNextToken(MOD)) {
				INode node = parseNumIntDivExpr();
				if (node == null) {
					throw new CompileException("Missing or invalid number expression after arithmetic operator MOD.");
				}
				result = BinaryNode.create(MOD, result, node, NodeType.NUM);
			}
		}
		return result;
	}

	private INode parseNumIntDivExpr() {
		INode result = parseNumMultDivExpr();
		if (result != null) {
			while (isNextToken(INT_DIVIDE)) {
				INode node = parseNumMultDivExpr();
				if (node == null) {
					throw new CompileException("Missing or invalid number expression after arithmetic operator \\.");
				}
				result = BinaryNode.create(INT_DIVIDE, result, node, NodeType.NUM);
			}
		}
		return result;
	}

	private INode parseNumMultDivExpr() {
		INode result = parseNumPowerExpr();
		if (result != null) {
			Token token;
			while ((token = getNextTokenOutOf(MULTIPLY, DIVIDE)) != null) {
				INode node = parseNumPowerExpr();
				if (node == null) {
					throw new CompileException("Missing or invalid number expression after arithmetic operator " + token.getChars() + ".");
				}
				result = BinaryNode.create(token, result, node, NodeType.NUM);
			}
		}
		return result;
	}

	private INode parseNumPowerExpr() {
		INode result = parseNumFactor();
		if (result != null) {
			while (isNextToken(POWER)) {
				INode node = parseNumFactor();
				if (node == null) {
					throw new CompileException("Missing or invalid number expression after arithmetic operator ^.");
				}
				result = BinaryNode.create(POWER, result, node, NodeType.NUM);
			}
		}
		return result;
	}

	private INode parseNumFactor() {
		INode result = parseNumStrRelExpr();
		if (result == null) {
			result = parseNumFN();
		}
		if (result == null) {
			result = parseNumFunc();
		}
		if (result == null) { // reject string array variables
			result = parseStrVarOrStrArrayVar();
			if (result != null) {
				throw new CompileException("Invalid string variable(s) in number expression.");
			}
		}
		if (result == null) { // parse array variable before non-array variables
			result = parseNumVarOrNumArrayVar();
		}
		if (result == null) {
			result = parseNumConst();
		}
		if (result == null) {
			if (isNextToken(SUBTRACT)) {
				result = UnaryNode.create(SUBTRACT, parseNumFactor());
			}
		}
		if (result == null) {
			if (isNextToken(ADD)) {
				result = UnaryNode.create(ADD, parseNumFactor());
			}
		}
		if (result == null) {
			if (isNextToken(OPEN)) {
				result = UnaryNode.create(OPEN, parseNumExpr());
				if (isNextToken(CLOSE) == false) {
					throw new CompileException("Missing closing parenthesis ()) in number expression.");
				}
			}
		}
		return result;
	}

	private INode parseNumStrRelExpr() {
		INode result = parseNumStrAddExpr();
		if (result != null) {
			Token token = getNextTokenOutOf(NOT_EQUAL, LESS_OR_EQUAL, GREATER_OR_EQUAL, LESS, EQUAL, GREATER); // op order matters!
			if (token == null) {
				throw new CompileException("Missing relational string operator to convert the string expression into a number.");
			}
			INode node = parseNumStrAddExpr();
			if (node == null) {
				throw new CompileException("Missing or invalid string expression after relational string operator " + token.getChars() + ".");
			}
			result = BinaryNode.create(token, result, node, NodeType.NUM);
		}
		return result;
	}

	private INode parseNumStrAddExpr() {
		INode result = parseStrTerm();
		if (result != null) {
			while (true) {
				ParserState state = saveParserState();
				if (isNextToken(ADD)) {
					INode node = parseStrTerm();
					if (node != null) {
						result = BinaryNode.create(ADD, result, node, NodeType.STR);
					} else {
						// "<strTerm> + not-a-<strTerm>" encountered, unparse "+ not-a-<strTerm>"
						restoreParserState(state);
						break;
					}
				} else {
					// no "+" encountered -> stop parsing for "+ <strTerm>"
					break;
				}
			}
		}
		return result;
	}

	private FnFunctionNode parseNumFN() {
		String fnFuncName = getNumFNFunctionName();
		if (fnFuncName == null) {
			return null;
		}
		return internalParseFNFunction(fnFuncName, NodeType.NUM);
	}

	private FunctionNode parseNumFunc() {
		FunctionNode result = internalParseFunction(ABS);
		if (result == null) {
			result = internalParseFunction(ASC);
		}
		if (result == null) {
			result = internalParseFunction(ATN);
		}
		if (result == null) {
			result = internalParseFunction(COS);
		}
		if (result == null) {
			result = internalParseFunction(EXP);
		}
		if (result == null) {
			result = internalParseFunction(FIX);
		}
		if (result == null) {
			if (isNextToken(INSTR)) {
				// INSTR([I,] X$, Y$)
				INode arg1Expr = parseExpr(); // can be a number expression or a string expression
				if (arg1Expr == null) {
					throw new CompileException("INSTR(): Missing or invalid first argument.");
				}
				if (isNextToken(COMMA) == false) {
					throw new CompileException("INSTR(): Missing comma separator (,) after first argument.");
				}

				INode arg2Expr = null;
				if (arg1Expr.getType() == NodeType.NUM) {
					// INSTR(I, X$, Y$)
					arg2Expr = parseStrExpr();
					if (arg2Expr == null) {
						throw new CompileException("INSTR(): Missing or invalid but-last argument.");
					}
					if (isNextToken(COMMA) == false) {
						throw new CompileException("INSTR(): Missing comma separator (,) before last argument.");
					}
				} else if (arg1Expr.getType() == NodeType.STR) {
					// INSTR(X$, Y$)
					arg2Expr = arg1Expr;
					arg1Expr = NumNode.create("1");
				}

				INode arg3Expr = parseStrExpr();
				if (arg3Expr == null) {
					throw new CompileException("INSTR(): Missing or invalid last argument.");
				}

				result = FunctionNode.create(INSTR, arg1Expr, arg2Expr, arg3Expr);
				if (isNextToken(CLOSE) == false) {
					throw new CompileException("INSTR(): Missing closing parenthesis ()).");
				}
			}
		}
		if (result == null) {
			result = internalParseFunction(INT);
		}
		if (result == null) {
			result = internalParseFunction(LEN);
		}
		if (result == null) {
			result = internalParseFunction(LOG);
		}
		if (result == null) {
			result = internalParseFunction(POS);
		}
		if (result == null) {
			result = internalParseFunction(RND);
		}
		if (result == null) {
			result = internalParseFunction(SGN);
		}
		if (result == null) {
			result = internalParseFunction(SIN);
		}
		if (result == null) {
			result = internalParseFunction(SQR);
		}
		if (result == null) {
			result = internalParseFunction(TAN);
		}
		if (result == null) {
			result = internalParseFunction(VAL);
		}
		return result;
	}

	private VariableNode parseNumVarOrNumArrayVar() {
		VariableNode numVar = parseNumArrayVar();
		if (numVar == null) {
			numVar = parseNumVar();
		}
		return numVar;
	}

	private VariableNode parseNumArrayVar() {
		String varName = getNumArrayVariableName();
		if (varName != null) {
			INode dim1Expr = parseNumExpr();
			if (dim1Expr == null) {
				throw new CompileException("Missing or invalid expression for first index of number array variable " + varName + "().");
			}
			if (isNextToken(COMMA)) {
				INode dim2Expr = parseNumExpr();
				if (dim2Expr == null) {
					throw new CompileException("Missing or invalid expression for second index of number array variable " + varName + "().");
				}
				if (isNextToken(CLOSE)) {
					if (this.arrayVariables.containsKey(varName)) {
						if (this.arrayVariables.get(varName).getNumDims() != 2) {
							throw new CompileException("Number array variable " + varName + "() does not have a single index like in earlier part of code.");
						}
					} else {
						ArrayVariableInfo info = new ArrayVariableInfo(2, false /* isUsedInDimStatement */);
						this.arrayVariables.put(varName, info);
					}
					return VariableNode.create(varName, NodeType.NUM, dim1Expr, dim2Expr);
				}
				throw new CompileException("Missing closing parentheses ()) after second index of number array variable " + varName + "().");
			}
			if (isNextToken(CLOSE)) {
				if (this.arrayVariables.containsKey(varName)) {
					if (this.arrayVariables.get(varName).getNumDims() != 1) {
						throw new CompileException("Number array variable " + varName + "() does not have two indexes like in earlier part of code.");
					}
				} else {
					ArrayVariableInfo info = new ArrayVariableInfo(1, false /* isUsedInDimStatement */);
					this.arrayVariables.put(varName, info);
				}
				return VariableNode.create(varName, NodeType.NUM, dim1Expr);
			}
			throw new CompileException("Missing closing parentheses ()) after first index of number array variable " + varName + "().");
		}
		return null;
	}

	private VariableNode parseNumVar() {
		String varName = getNumVariableName();
		if (varName != null) {
			return VariableNode.createVariableNode(varName, NodeType.NUM);
		}
		return null;
	}

	private NumNode parseNumConst() {
		String strNumber = getNumConstant();
		if (strNumber != null) {
			return NumNode.create(strNumber);
		}
		return null;
	}

	// Parse other things ////////////////////////////////////////////////////////

	private StrNode parseDataElement() {
		String constant = getDataElement();
		if (constant != null) {
			if (constant.length() > 255) { // NOTE: Never happens, code lines are 255 chars maximum
				throw new CompileException("DATA element has more than 255 characters.");
			}
			return StrNode.create(constant);
		}
		return null;
	}

	private FunctionNode internalParseFunction(FunctionToken functionToken) {
		FunctionNode result = null;

		if (isNextToken(functionToken)) {
			List<INode> args = new ArrayList<INode>();
			NodeType[] argTypes = functionToken.getArgTypes();
			int numArgs = argTypes.length;

			for (int i = 0; i < numArgs; i++) {
				if (i > 0) {
					if (isNextToken(COMMA) == false) {
						throw new CompileException("Missing comma separator (,) while parsing function " + functionToken.getChars() + "().");
					}
				}

				INode argExpr = null;
				if (argTypes[i] == NodeType.NUM) {
					argExpr = parseNumExpr();
				} else if (argTypes[i] == NodeType.STR) {
					argExpr = parseStrExpr();
				}
				if (argExpr == null) {
					throw new CompileException("Missing or invalid argument(s) while parsing function " + functionToken.getChars() + "().");
				}
				args.add(argExpr);
			}
			result = FunctionNode.create(functionToken, args.toArray(new INode[numArgs]));
			if (isNextToken(CLOSE) == false) {
				throw new CompileException("Missing closing parenthesis ()) while parsing function " + functionToken.getChars() + "().");
			}
		}

		return result;
	}

	private FnFunctionNode internalParseFNFunction(String fnFuncName, NodeType fnFuncType) {
		// FN<name>[$](<expr>[,<expr>]*)

		List<INode> fnFuncArgExprs = new ArrayList<INode>();
		do {
			INode fnFuncArgExpr = parseExpr();
			if (fnFuncArgExpr == null) {
				throw new CompileException("FN: Missing or invalid argument expression in function call of " + fnFuncName + "().");
			}
			fnFuncArgExprs.add(fnFuncArgExpr);
		} while (isNextToken(COMMA));

		if (isNextToken(CLOSE) == false) {
			throw new CompileException("FN: Missing closing parenthesis ()) in function call of " + fnFuncName + "().");
		}

		if (fnFuncArgExprs.isEmpty()) {
			throw new CompileException("FN: Function call of " + fnFuncName + "() has no arguments.");
		}

		// compare signature of this FN<name> against DEF FN<name> (if present)
		if (this.defFnMap.containsKey(fnFuncName)) {
			DefFnStatement statement = this.defFnMap.get(fnFuncName);

			if (fnFuncType != statement.getFuncExpr().getType()) {
				throw new CompileException("FN: Return type of function call of " + fnFuncName + "() does not match return type of definition.");
			}

			if (fnFuncArgExprs.size() != statement.getFuncVars().length) {
				throw new CompileException("FN: Number of arguments in function call of " + fnFuncName + "() does not match number of arguments in definition.");
			}

			for (int i = 0; i < fnFuncArgExprs.size(); i++) {
				if (fnFuncArgExprs.get(i).getType() != statement.getFuncVars()[i].getType()) {
					throw new CompileException("FN: Type of argument #" + (i + 1) + " in function call of " + fnFuncName + "() does not match argument type in definition.");
				}
			}
		}

		// compare signature of this FN<name> against previous FN<name> calls
		if (this.fnMap.containsKey(fnFuncName)) {
			FnFunctionNode fnNode = this.fnMap.get(fnFuncName);

			if (fnFuncType != fnNode.getType()) {
				throw new CompileException("FN: Return type of function call of " + fnFuncName + "() does not match return type of previous FN.");
			}

			if (fnFuncArgExprs.size() != fnNode.getFuncArgExprs().length) {
				throw new CompileException("FN: Number of arguments in function call of " + fnFuncName + "() does not match number of arguments in previous FN.");
			}

			for (int i = 0; i < fnFuncArgExprs.size(); i++) {
				if (fnFuncArgExprs.get(i).getType() != fnNode.getFuncArgExprs()[i].getType()) {
					throw new CompileException("FN: Type of argument #" + (i + 1) + " in function call of " + fnFuncName + "() does not match argument type in previous FN.");
				}
			}
		}

		FnFunctionNode fnFunctionNode = FnFunctionNode.create(fnFuncName, fnFuncType, fnFuncArgExprs.toArray(new INode[fnFuncArgExprs.size()]));
		this.fnMap.put(fnFuncName, fnFunctionNode);

		return fnFunctionNode;
	}

	// Parser ////////////////////////////////////////////////////////////////////

	private boolean isNextToken(Token tokenToCompare) {
		Token token = readToken();
		if (token == tokenToCompare) {
			consumeToken();
			return true;
		}
		return false;
	}

	private Token getNextTokenOutOf(Token... tokens) {
		Token token = readToken();
		for (Token tokenToCompare : tokens) {
			if (token == tokenToCompare) {
				consumeToken();
				return token;
			}
		}
		return null;
	}

	private String getLineNumber() {
		String constant = getNumConstant();
		if (constant != null) {
			if (constant.contains("E") || constant.contains(".")) {
				return null;
			}
		}
		return constant;
	}

	private String getNumConstant() {
		return internalGetName(TokenType.NUM_CONSTANT);
	}

	private String getNumVariableName() {
		return internalGetName(TokenType.NUM_VAR_ID);
	}

	private String getNumArrayVariableName() {
		return internalGetName(TokenType.NUM_ARRAY_VAR_ID);
	}

	private String getNumFNFunctionName() {
		return internalGetName(TokenType.NUM_FN_ID);
	}

	private String getStrConstant() {
		return internalGetName(TokenType.STR_CONSTANT);
	}

	private String getStrVariableName() {
		return internalGetName(TokenType.STR_VAR_ID);
	}

	private String getStrArrayVariableName() {
		return internalGetName(TokenType.STR_ARRAY_VAR_ID);
	}

	private String getStrFNFunctionName() {
		return internalGetName(TokenType.STR_FN_ID);
	}

	private String internalGetName(TokenType tokenType) {
		Token token = readToken();
		if (token.getType() == tokenType) {
			consumeToken();
			return token.getChars();
		}
		return null;
	}

	private String getDataElement() {
		this.isParsingDATAElement = true;
		String constant = getStrConstant();
		this.isParsingDATAElement = false;
		return constant;
	}

	// Save and restore parser state /////////////////////////////////////////////

	private static class ParserState {
		private int pos;
		private Token token;
		private boolean hasCachedToken;
	}

	private ParserState saveParserState() {
		ParserState state = new ParserState();
		state.pos = this.pos;
		state.token = this.token;
		state.hasCachedToken = this.hasCachedToken;
		return state;
	}

	private void restoreParserState(ParserState state) {
		this.pos = state.pos;
		this.token = state.token;
		this.hasCachedToken = state.hasCachedToken;
	}

	// Lexer /////////////////////////////////////////////////////////////////////

	private Token readToken() {
		if (this.hasCachedToken == false) {
			this.token = internalReadToken();
			this.hasCachedToken = true;
		}
		return this.token;
	}

	private void consumeToken() {
		this.hasCachedToken = false;
	}

	private Token internalReadToken() {
		final int maxLen = this.stringToParse.length();

		// skip leading whitespace
		while ((this.pos < maxLen) && (Character.isWhitespace(this.stringToParse.charAt(this.pos)))) {
			this.pos++;
		}

		// if string fully parsed return end-of-input
		if (this.pos >= maxLen) {
			return Token.END_OF_INPUT;
		}

		char chr = this.stringToParse.charAt(this.pos);
		final int startPos = this.pos;

		// parse string constants
		if (chr == '"') {
			this.pos++;

			while ((this.pos < maxLen) && (this.stringToParse.charAt(this.pos) != '"')) {
				this.pos++;
			}
			boolean hasClosingQuote = (this.pos < maxLen);
			this.pos++;

			if (hasClosingQuote == false) {
				throw new CompileException("String constant has no closing quote (\") character.");
			}

			String chars = this.stringToParse.substring(startPos + 1, this.pos - 1);
			return new Token(TokenType.STR_CONSTANT, chars);
		}

		// parse DATA elements
		if (this.isParsingDATAElement) {
			while ((this.pos < maxLen) && ((this.stringToParse.charAt(this.pos) != ',') && (this.stringToParse.charAt(this.pos) != ':'))) {
				this.pos++;
			}

			String strDataElement = this.stringToParse.substring(startPos, this.pos).trim();
			return new Token(TokenType.STR_CONSTANT, strDataElement);
		}

		// parse IDs, keywords, and DATA elements
		if (Character.isLetter(chr)) {
			this.pos++;

			// parse IDs and keywords
			final String STR_FN = "FN";

			while ((this.pos < maxLen) && (Character.isLetterOrDigit(this.stringToParse.charAt(this.pos)) || (this.stringToParse.charAt(this.pos) == '.'))) {
				this.pos++;
			}

			String name = this.stringToParse.substring(startPos, this.pos);
			if ((this.pos < maxLen) && (this.stringToParse.charAt(this.pos) == '$')) {
				this.pos++;
				final String fullName = name + "$";
				if ((this.pos < maxLen) && (this.stringToParse.charAt(this.pos) == '(')) {
					this.pos++;

					if (name.startsWith(STR_FN) && (name.length() > STR_FN.length())) {
						return new Token(TokenType.STR_FN_ID, fullName);
					}
					if (Token.isKeyword(fullName)) {
						return Token.getKeywordToken(fullName);
					}
					return new Token(TokenType.STR_ARRAY_VAR_ID, fullName);
				}
				return new Token(TokenType.STR_VAR_ID, fullName);
			} else if ((this.pos < maxLen) && (this.stringToParse.charAt(this.pos) == '(')) {
				this.pos++;
				if (name.startsWith(STR_FN) && (name.length() > STR_FN.length())) {
					return new Token(TokenType.NUM_FN_ID, name);
				}
				if (Token.isKeyword(name)) {
					return Token.getKeywordToken(name);
				}
				return new Token(TokenType.NUM_ARRAY_VAR_ID, name);
			}
			if (Token.isKeyword(name)) {
				return Token.getKeywordToken(name);
			}
			return new Token(TokenType.NUM_VAR_ID, name);
		}

		// parse special characters (before parsing numbers, which are treated as unsigned)
		String strChr = String.valueOf(chr);

		if (Token.isSpecialCharacter(strChr)) {
			int endPos = this.pos + 1;

			this.pos++;
			while (this.pos <= maxLen) {
				String chars = this.stringToParse.substring(startPos, this.pos);
				if (Token.isSpecialCharacter(chars)) {
					endPos = this.pos;
					this.pos++;
				} else {
					this.pos = endPos;
					break;
				}
			}

			String chars = this.stringToParse.substring(startPos, endPos);
			return Token.getSpecialCharacterToken(chars);
		}

		// parse unsigned number constants
		if (Character.isDigit(chr) || (chr == '.')) {
			int rollBackPos = this.pos;

			boolean hasIntMantissa = false;
			boolean hasDecimalPoint = false;
			boolean hasFracMantisssa = false;

			while ((this.pos < maxLen) && Character.isDigit(this.stringToParse.charAt(this.pos))) {
				hasIntMantissa = true;
				this.pos++;
			}

			if ((this.pos < maxLen) && (this.stringToParse.charAt(this.pos) == '.')) {
				hasDecimalPoint = true;
				this.pos++;
			}

			while ((this.pos < maxLen) && Character.isDigit(this.stringToParse.charAt(this.pos))) {
				hasFracMantisssa = true;
				this.pos++;
			}

			if (hasIntMantissa || (hasDecimalPoint && hasFracMantisssa)) {
				rollBackPos = this.pos;

				if ((this.pos < maxLen) && ((this.stringToParse.charAt(this.pos) == 'E') || (this.stringToParse.charAt(this.pos) == 'e'))) {
					this.pos++;

					boolean hasExponent = false;

					if ((this.pos < maxLen) && ((this.stringToParse.charAt(this.pos) == '+') || (this.stringToParse.charAt(this.pos) == '-'))) {
						this.pos++;
					}

					while ((this.pos < maxLen) && Character.isDigit(this.stringToParse.charAt(this.pos))) {
						hasExponent = true;
						this.pos++;
					}

					if (hasExponent == false) {
						this.pos = rollBackPos;
					}
				}
				return new Token(TokenType.NUM_CONSTANT, this.stringToParse.substring(startPos, this.pos));
			}

			this.pos = rollBackPos;
		}

		return new Token(TokenType.UNKNOWN, strChr);
	}
}
