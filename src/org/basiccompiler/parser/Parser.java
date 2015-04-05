/*
 * Copyright (c) 2015, Lorenz Wiest
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the FreeBSD Project.
 */

package org.basiccompiler.parser;

import static org.basiccompiler.parser.tokens.FunctionToken.ABS;
import static org.basiccompiler.parser.tokens.FunctionToken.ASC;
import static org.basiccompiler.parser.tokens.FunctionToken.ATN;
import static org.basiccompiler.parser.tokens.FunctionToken.CHR;
import static org.basiccompiler.parser.tokens.FunctionToken.COS;
import static org.basiccompiler.parser.tokens.FunctionToken.EXP;
import static org.basiccompiler.parser.tokens.FunctionToken.FIX;
import static org.basiccompiler.parser.tokens.FunctionToken.INSTR;
import static org.basiccompiler.parser.tokens.FunctionToken.INT;
import static org.basiccompiler.parser.tokens.FunctionToken.LEFT;
import static org.basiccompiler.parser.tokens.FunctionToken.LEN;
import static org.basiccompiler.parser.tokens.FunctionToken.LOG;
import static org.basiccompiler.parser.tokens.FunctionToken.MID;
import static org.basiccompiler.parser.tokens.FunctionToken.POS;
import static org.basiccompiler.parser.tokens.FunctionToken.RIGHT;
import static org.basiccompiler.parser.tokens.FunctionToken.RND;
import static org.basiccompiler.parser.tokens.FunctionToken.SGN;
import static org.basiccompiler.parser.tokens.FunctionToken.SIN;
import static org.basiccompiler.parser.tokens.FunctionToken.SPACE;
import static org.basiccompiler.parser.tokens.FunctionToken.SPC;
import static org.basiccompiler.parser.tokens.FunctionToken.SQR;
import static org.basiccompiler.parser.tokens.FunctionToken.STR;
import static org.basiccompiler.parser.tokens.FunctionToken.TAB;
import static org.basiccompiler.parser.tokens.FunctionToken.TAN;
import static org.basiccompiler.parser.tokens.FunctionToken.VAL;
import static org.basiccompiler.parser.tokens.Token.ADD;
import static org.basiccompiler.parser.tokens.Token.AND;
import static org.basiccompiler.parser.tokens.Token.CLOSE;
import static org.basiccompiler.parser.tokens.Token.COLON;
import static org.basiccompiler.parser.tokens.Token.COMMA;
import static org.basiccompiler.parser.tokens.Token.DATA;
import static org.basiccompiler.parser.tokens.Token.DEF;
import static org.basiccompiler.parser.tokens.Token.DIM;
import static org.basiccompiler.parser.tokens.Token.DIVIDE;
import static org.basiccompiler.parser.tokens.Token.ELSE;
import static org.basiccompiler.parser.tokens.Token.END;
import static org.basiccompiler.parser.tokens.Token.EQUAL;
import static org.basiccompiler.parser.tokens.Token.FOR;
import static org.basiccompiler.parser.tokens.Token.GOSUB;
import static org.basiccompiler.parser.tokens.Token.GOTO;
import static org.basiccompiler.parser.tokens.Token.GREATER;
import static org.basiccompiler.parser.tokens.Token.GREATER_OR_EQUAL;
import static org.basiccompiler.parser.tokens.Token.IF;
import static org.basiccompiler.parser.tokens.Token.INPUT;
import static org.basiccompiler.parser.tokens.Token.INT_DIVIDE;
import static org.basiccompiler.parser.tokens.Token.LESS;
import static org.basiccompiler.parser.tokens.Token.LESS_OR_EQUAL;
import static org.basiccompiler.parser.tokens.Token.LET;
import static org.basiccompiler.parser.tokens.Token.MOD;
import static org.basiccompiler.parser.tokens.Token.MULTIPLY;
import static org.basiccompiler.parser.tokens.Token.NEXT;
import static org.basiccompiler.parser.tokens.Token.NOT;
import static org.basiccompiler.parser.tokens.Token.NOT_EQUAL;
import static org.basiccompiler.parser.tokens.Token.ON;
import static org.basiccompiler.parser.tokens.Token.OPEN;
import static org.basiccompiler.parser.tokens.Token.OR;
import static org.basiccompiler.parser.tokens.Token.POWER;
import static org.basiccompiler.parser.tokens.Token.PRINT;
import static org.basiccompiler.parser.tokens.Token.READ;
import static org.basiccompiler.parser.tokens.Token.REM;
import static org.basiccompiler.parser.tokens.Token.RESTORE;
import static org.basiccompiler.parser.tokens.Token.RETURN;
import static org.basiccompiler.parser.tokens.Token.SEMICOLON;
import static org.basiccompiler.parser.tokens.Token.STEP;
import static org.basiccompiler.parser.tokens.Token.STOP;
import static org.basiccompiler.parser.tokens.Token.STRING_ADD;
import static org.basiccompiler.parser.tokens.Token.STRING_EQUAL;
import static org.basiccompiler.parser.tokens.Token.STRING_GREATER;
import static org.basiccompiler.parser.tokens.Token.STRING_GREATER_OR_EQUAL;
import static org.basiccompiler.parser.tokens.Token.STRING_LESS;
import static org.basiccompiler.parser.tokens.Token.STRING_LESS_OR_EQUAL;
import static org.basiccompiler.parser.tokens.Token.STRING_NOT_EQUAL;
import static org.basiccompiler.parser.tokens.Token.SUBTRACT;
import static org.basiccompiler.parser.tokens.Token.SWAP;
import static org.basiccompiler.parser.tokens.Token.THEN;
import static org.basiccompiler.parser.tokens.Token.TO;
import static org.basiccompiler.parser.tokens.Token.UNARY_MINUS;
import static org.basiccompiler.parser.tokens.Token.WEND;
import static org.basiccompiler.parser.tokens.Token.WHILE;
import static org.basiccompiler.parser.tokens.Token.XOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.basiccompiler.compiler.etc.CompileException;
import org.basiccompiler.parser.nodes.INode;
import org.basiccompiler.parser.nodes.NodeType;
import org.basiccompiler.parser.nodes.impl.BinaryNode;
import org.basiccompiler.parser.nodes.impl.FnFunctionNode;
import org.basiccompiler.parser.nodes.impl.FunctionNode;
import org.basiccompiler.parser.nodes.impl.NumNode;
import org.basiccompiler.parser.nodes.impl.StrNode;
import org.basiccompiler.parser.nodes.impl.TokenNode;
import org.basiccompiler.parser.nodes.impl.UnaryNode;
import org.basiccompiler.parser.nodes.impl.VariableNode;
import org.basiccompiler.parser.statements.Statement;
import org.basiccompiler.parser.statements.impl.DataStatement;
import org.basiccompiler.parser.statements.impl.DefFnStatement;
import org.basiccompiler.parser.statements.impl.DimStatement;
import org.basiccompiler.parser.statements.impl.EndStatement;
import org.basiccompiler.parser.statements.impl.ForStatement;
import org.basiccompiler.parser.statements.impl.GosubStatement;
import org.basiccompiler.parser.statements.impl.GotoStatement;
import org.basiccompiler.parser.statements.impl.IfStatement;
import org.basiccompiler.parser.statements.impl.InputStatement;
import org.basiccompiler.parser.statements.impl.LetStatement;
import org.basiccompiler.parser.statements.impl.LineNumberStatement;
import org.basiccompiler.parser.statements.impl.NextStatement;
import org.basiccompiler.parser.statements.impl.OnGosubStatement;
import org.basiccompiler.parser.statements.impl.OnGotoStatement;
import org.basiccompiler.parser.statements.impl.PrintStatement;
import org.basiccompiler.parser.statements.impl.ReadStatement;
import org.basiccompiler.parser.statements.impl.RemStatement;
import org.basiccompiler.parser.statements.impl.RestoreStatement;
import org.basiccompiler.parser.statements.impl.ReturnStatement;
import org.basiccompiler.parser.statements.impl.StopStatement;
import org.basiccompiler.parser.statements.impl.SwapStatement;
import org.basiccompiler.parser.statements.impl.WendStatement;
import org.basiccompiler.parser.statements.impl.WhileStatement;
import org.basiccompiler.parser.tokens.FunctionToken;
import org.basiccompiler.parser.tokens.Token;

public class Parser {
  public static final String BEFORE_FIRST_LINE_NUMBER = "";
  public static final String RESTORE_DEFAULT_LINE_NUMBER = "";

  private String currentLineNumber;
  private String stringToParse;
  private int pos;

  private final Map<String, DefFnStatement> defFnMap = new HashMap<String, DefFnStatement>();

  private final Map<String, FnFunctionNode> fnMap = new HashMap<String, FnFunctionNode>();
  
  //////////////////////////////////////////////////////////////////////////////
  
  private class ArrayVariableInfo {
    private int numDims;
    private boolean usedInDimStatement;

    public ArrayVariableInfo(int numDims, boolean usedInDimStatement) {
      this.numDims = numDims;
      this.usedInDimStatement = usedInDimStatement;
    }

    public int getNumDims() {
      return this.numDims;
    }

    public boolean usedInDimStatement() {
      return this.usedInDimStatement;
    }
  }
  
  private Map<String /* array variable name */, ArrayVariableInfo> arrayVariables = new HashMap<String, ArrayVariableInfo>();  
  
  public Parser() {
    this.currentLineNumber = BEFORE_FIRST_LINE_NUMBER;
  }

  public List<Statement> parseLine(String stringToParse) {
    this.stringToParse = stringToParse;
    this.pos = 0;

    if (stringToParse.trim().isEmpty()) {
      return Collections.emptyList();
    }
    if (stringToParse.length() > 255) {
      throw new CompileException("Line has more than 255 characters.");
    }

    List<Statement> statements = new ArrayList<Statement>();
    String strLineNumber = parseLineNumber();
    if (strLineNumber == null) {
      throw new CompileException("Line has no line number.");
    }

    statements.add(new LineNumberStatement(strLineNumber));
    statements.addAll(parseStatements());
    if (this.pos < this.stringToParse.length()) {
      throw new CompileException("Not fully parsed. Invalid characters at position " + this.pos + ".");
    }
    return statements;
  }

  public void flush() {
    // verify that all FN have a DEF FN
    for (String funcName : this.fnMap.keySet()) {
      if (this.defFnMap.containsKey(funcName) == false) {
        throw new CompileException("Function " + funcName + " is undefined.");
      }
    }
  }

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
    if (isNextKeyword(DATA)) {
      result = parseDATA();
    } else if (isNextKeyword(DEF)) {
      result = parseDEF();
    } else if (isNextKeyword(DIM)) {
      result = parseDIM();
    } else if (isNextKeyword(END)) {
      result = parseEND();
    } else if (isNextKeyword(FOR)) {
      result = parseFOR();
    } else if (isNextKeyword(IF)) {
      result = parseIF();
    } else if (isNextKeyword(INPUT)) {
      result = parseINPUT();
    } else if (isNextKeyword(GOTO)) {
      result = parseGOTO();
    } else if (isNextKeyword(GOSUB)) {
      result = parseGOSUB();
    } else if (isNextKeyword(LET)) {
      result = parseLET();
    } else if (isNextKeyword(NEXT)) {
      result = parseNEXT();
    } else if (isNextKeyword(ON)) {
      result = parseON();
    } else if (isNextKeyword(PRINT)) {
      result = parsePRINT();
    } else if (isNextKeyword(READ)) {
      result = parseREAD();
    } else if (isNextKeyword(REM)) {
      result = parseREM();
    } else if (isNextKeyword(RESTORE)) {
      result = parseRESTORE();
    } else if (isNextKeyword(RETURN)) {
      result = parseRETURN();
    } else if (isNextKeyword(STOP)) {
      result = parseSTOP();
    } else if (isNextKeyword(SWAP)) {
      result = parseSWAP();
    } else if (isNextKeyword(WEND)) {
      result = parseWEND();
    } else if (isNextKeyword(WHILE)) {
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
    while (true) {
      StrNode strNode = parseStrConst();
      if (strNode == null) {
        strNode = parseAnyConstant();
      }
      if (strNode == null) {
        throw new CompileException("Cannot parse DATA.");
      }

      String constant = strNode.getValue();
      constants.add(constant.trim());
      if (isNextToken(COMMA) == false) {
        break;
      }
    }
    return new DataStatement(this.currentLineNumber, constants.toArray(new String[0]));
  }

  private Statement parseDEF() {
    // DEF <funcName>(<var>[,<var>]*)=<expression>

    String funcName = getStrFunctionName();
    if (funcName == null) {
      funcName = getNumFunctionName();
    }
    if (funcName == null) {
      throw new CompileException("DEF FN: Cannot parse function name. It must start with FN.");
    }
    funcName = funcName.substring(0, funcName.length() - 1);

    List<VariableNode> funcVars = new ArrayList<VariableNode>();
    do {
      VariableNode funcVar = parseStrVar();
      if (funcVar == null) {
        funcVar = parseNumVar();
      }
      if (funcVar == null) {
        throw new CompileException("DEF FN: Cannot parse or invalid function variable with function " + funcName + ".");
      }
      funcVars.add(funcVar);
    } while (isNextToken(COMMA));

    if (isNextToken(CLOSE) == false) {
      throw new CompileException("DEF FN: Missing ) in function " + funcName + ".");
    }

    if (funcVars.isEmpty()) {
      throw new CompileException("DEF FN: Function " + funcName + " has zero function variables.");
    }

    if (isNextToken(EQUAL) == false) {
      throw new CompileException("DEF FN: Missing = in function " + funcName + " .");
    }

    INode funcExpr = parseExpr();
    if (funcExpr == null) {
      throw new CompileException("DEF FN: Cannot parse expression of function " + funcName + ".");
    }

    NodeType funcType = funcName.endsWith("$") ? NodeType.STR : NodeType.NUM;
    if (funcType != funcExpr.getType()) {
      throw new CompileException("DEF FN: Function type and expression type of function " + funcName + " are not the same.");
    }

    if (this.defFnMap.containsKey(funcName)) {
      throw new CompileException("DEF FN: Function " + funcName + " already defined.");
    }

    DefFnStatement statement = new DefFnStatement(funcName, funcVars.toArray(new VariableNode[0]), funcExpr);

    // compare signatures of DEF FN<name> against previous FN<name> calls
    if (this.fnMap.containsKey(funcName)) {
      FnFunctionNode fnFuncNode = this.fnMap.get(funcName);

      if (funcVars.size() != fnFuncNode.getFuncArgExprs().length) {
        throw new CompileException("DEF FN: Number of arguments of function " + funcName + " are not the same as in earlier call of function.");
      }

      for (int i = 0; i < funcVars.size(); i++) {
        if (funcVars.get(i).getType() != fnFuncNode.getFuncArgExprs()[i].getType()) {
          throw new CompileException("DEF FN: Arguments " + i + " of function " + funcName + " has not the same type as argument in earlier call of function.");
        }
      }
    }

    this.defFnMap.put(funcName, statement);
    return statement;
  }
  
  private Statement parseDIM() {
    // DIM <arrayVar>[,<arrayVar>]*

    List<VariableNode> arrayVars = new ArrayList<VariableNode>();
    while (true) {
      VariableNode arrayVar = parseStrArrayVar();
      if (arrayVar == null) {
        arrayVar = parseNumArrayVar();
      }
      if (arrayVar == null) {
        throw new CompileException("Missing or illegal variable name(s) after DIM.");
      }
      
      String arrayVarName = arrayVar.getVariableName();
      if (this.arrayVariables.containsKey(arrayVarName)) {
        ArrayVariableInfo info = arrayVariables.get(arrayVarName);
        if (info.usedInDimStatement()) {
          NodeType arrayVarType = arrayVar.getType();
          String message = "";
          if (arrayVarType == NodeType.NUM) {
            message = "Number array variable " + arrayVarName + ") has already been used in a DIM statement.";
          } else if (arrayVarType == NodeType.STR) {
            message = "String array variable " + arrayVarName + ") has already been used in a DIM statement.";
          }
          throw new CompileException(message);
        }
      }
      ArrayVariableInfo info = new ArrayVariableInfo(arrayVar.getDimExpressions().length, true /* usedInDimStatement */);
      this.arrayVariables.put(arrayVarName, info);

      arrayVars.add(arrayVar);
      if (isNextToken(COMMA) == false) {
        break;
      }
    }
    return new DimStatement(arrayVars.toArray(new VariableNode[0]));
  }

  private Statement parseEND() {
    // END

    return EndStatement.getInstance();
  }

  private Statement parseFOR() {
    // FOR <loopVarName>=<startExpr> TO <endExpr> [STEP <stepExpr>]

    VariableNode loopVar = parseNumVar();
    if (loopVar == null) {
      throw new CompileException("Missing or illegal loop variable in FOR statement.");
    }
    if (isNextToken(EQUAL) == false) {
      throw new CompileException("Missing equal sign after loop variable in FOR statement.");
    }
    INode startExpr = parseNumExpr();
    if (startExpr == null) {
      throw new CompileException("Missing or illegal number expression for start value in FOR statement.");
    }
    if (isNextKeyword(TO) == false) {
      throw new CompileException("Missing TO in FOR statement.");
    }
    INode endExpr = parseNumExpr();
    if (endExpr == null) {
      throw new CompileException("Missing or illegal number expression for end value in FOR statement.");
    }
    INode stepExpr = null;
    if (isNextKeyword(STEP)) {
      stepExpr = parseNumExpr();
      if (stepExpr == null) {
        throw new CompileException("Missing or illegal number expression for step value in FOR statement.");
      }
    } else {
      stepExpr = NumNode.createNumberNode("1");
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
    if (isNextKeyword(THEN)) {
      String lineNumber = parseLineNumber();
      if (lineNumber != null) {
        thenStatements.add(new GotoStatement(lineNumber));
      } else {
        thenStatements = parseStatements();
      }
      if (thenStatements.isEmpty()) { // NOTE: Never happens: parseStatement() will cause an exception first...
        throw new CompileException("Missing or invalid line number or statements after THEN in IF statement.");
      }
    } else if (isNextKeyword(GOTO)) {
      String lineNumber = parseLineNumber();
      if (lineNumber == null) {
        throw new CompileException("Missing or invalid line number after GOTO io IF statement.");
      }
      thenStatements.add(new GotoStatement(lineNumber));
    } else {
      throw new CompileException("Missing THEN or GOTO in IF statement.");
    }

    List<Statement> elseStatements = new ArrayList<Statement>();
    if (isNextKeyword(ELSE)) {
      String lineNumber = parseLineNumber();
      if (lineNumber != null) {
        elseStatements.add(new GotoStatement(lineNumber));
      } else {
        elseStatements = parseStatements();
      }
      if (elseStatements.isEmpty()) { // NOTE: Never happens: parseStatement() will cause an exception first...
        throw new CompileException("Missing or invalid line number or statements after ELSE in IF statement.");
      }
    }
    return new IfStatement(numExpr, thenStatements.toArray(new Statement[0]), elseStatements.toArray(new Statement[0]));
  }

  private Statement parseINPUT() {
    // INPUT ["<prompt>"{,|;}] <varName>[,<varName>]*

    StrNode promptNode = parseStrConst(); // is null if not present
    String prompt = promptNode != null ? promptNode.getValue() : null;

    Token separator = SEMICOLON;
    if (promptNode != null) {
      separator = getNextTokenOutOf(COMMA, SEMICOLON);
      if (separator == null) {
        throw new CompileException("Missing comma or semicolon in INPUT statement.");
      }
    }

    List<VariableNode> vars = new ArrayList<VariableNode>();
    while (true) {
      VariableNode var = parseStrOrStrArrayVar();
      if (var == null) {
        var = parseNumOrNumArrayVar();
      }
      if (var == null) {
        throw new CompileException("Missing or illegal variable in INPUT statement.");
      }
      vars.add(var);
      if (isNextToken(COMMA) == false) {
        break;
      }
    }
    return new InputStatement(prompt, separator, vars.toArray(new VariableNode[0]));
  }

  private Statement parseGOTO() {
    // GOTO <line number>

    String lineNumber = parseLineNumber();
    if (lineNumber == null) {
      throw new CompileException("Missing or illegal line number in GOTO statement.");
    }
    return new GotoStatement(lineNumber);
  }

  private Statement parseGOSUB() {
    // GOSUB <line number>

    String lineNumber = parseLineNumber();
    if (lineNumber == null) {
      throw new CompileException("Missing or illegal line number in GOSUB statement.");
    }
    return new GosubStatement(lineNumber);
  }

  private Statement parseImplicitLET() {
    // <varName>=<expr>

    return parseInternalLET(true /* isImplicit */);
  }

  private Statement parseLET() {
    // [LET ]<varName>=<expr>

    return parseInternalLET(false /* isImplicit */);
  }

  private Statement parseInternalLET(boolean isImplicit) {
    Statement statement = null;

    FnFunctionNode strFuncNode = parseStrFN();
    if (strFuncNode != null) {
      throw new CompileException("You cannot assign an expression to a function call.");
    }
    FnFunctionNode numFuncNode = parseNumFN();
    if (numFuncNode != null) {
      throw new CompileException("You cannot assign an expression to a function call.");
    }

    VariableNode var = parseStrOrStrArrayVar();
    if (var != null) {
      if (isNextToken(EQUAL)) {
        INode strExpr = parseStrExpr();
        if (strExpr == null) {
          throw new CompileException("Missing or invalid expression after equals (=), or type mismatch. You can assign only a string expression to a string variable.");
        }
        statement = new LetStatement(var, strExpr, isImplicit);
      } else {
        throw new CompileException("Equals (=) missing after string variable name.");
      }
    }

    if (var == null) {
      var = parseNumOrNumArrayVar();
      if (var != null) {
        if (isNextToken(EQUAL)) {
          INode numExpr = parseNumExpr();
          if (numExpr == null) {
            throw new CompileException("Missing or invalid expression after equals (=), or type mismatch. You can assign only a number expression to a number variable.");
          }
          statement = new LetStatement(var, numExpr, isImplicit);
        } else {
          throw new CompileException("Equals (=) missing after number variable name.");
        }
      }
    }
    return statement;
  }

  private Statement parseNEXT() {
    // NEXT [<loopVarName>[,<loopVarName>]*]

    List<VariableNode> loopVars = new ArrayList<VariableNode>();
    boolean expectNextLoopVar = false;
    while (true) {
      VariableNode loopVar = parseNumVar();
      if (loopVar != null) {
        loopVars.add(loopVar);
      } else {
        if (expectNextLoopVar) {
          throw new CompileException("Missing or illegal number variable after comma in NEXT statement.");
        }
        break;
      }
      expectNextLoopVar = isNextToken(COMMA);
    }
    return new NextStatement(loopVars.toArray(new VariableNode[0]));
  }

  private Statement parseON() {
    // ON <numExpr> {GOTO|GOSUB} <line number>[,<line number>]*

    List<String> lineNumbers = new ArrayList<String>();

    INode numExpr = parseNumExpr();
    if (numExpr == null) {
      throw new CompileException("Missing or illegal number expression in ON statement.");
    }

    boolean isOnGoto = false;
    if (isNextKeyword(GOTO)) {
      isOnGoto = true;
    } else if (isNextKeyword(GOSUB)) {
      isOnGoto = false;
    } else {
      throw new CompileException("Missing GOTO or GOSUB in ON statement.");
    }

    while (true) {
      String lineNumber = parseLineNumber();
      if (lineNumber == null) {
        String strGo = isOnGoto ? "GOTO" : "GOSUB";
        throw new CompileException("Missing or illegal line number in ON " + strGo + " statement.");
      }
      lineNumbers.add(lineNumber);
      if (isNextToken(COMMA) == false) {
        break;
      }
    }

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
          expr = TokenNode.createTokenNode(SEMICOLON);
        }
      }
      if (expr == null) {
        if (isNextToken(COMMA)) {
          expr = TokenNode.createTokenNode(COMMA);
        }
      }

      if (expr != null) {
        exprs.add(expr);
      } else {
        break;
      }
    }
    return new PrintStatement(exprs.toArray(new INode[0]));
  }

  private Statement parseREAD() {
    // READ <varName>[,<varName>]*

    List<VariableNode> vars = new ArrayList<VariableNode>();
    while (true) {
      VariableNode var = parseStrOrStrArrayVar();
      if (var == null) {
        var = parseNumOrNumArrayVar();
      }
      if (var == null) {
        throw new CompileException("Cannot parse variable in READ");
      }
      vars.add(var);
      if (isNextToken(COMMA) == false) {
        break;
      }
    }
    return new ReadStatement(vars.toArray(new VariableNode[0]));
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

    VariableNode var1 = parseStrOrStrArrayVar();
    if (var1 == null) {
      var1 = parseNumOrNumArrayVar();
    }
    if (var1 == null) {
      throw new CompileException("Missing or invalid first variable in SWAP statement.");
    }
    if (isNextToken(COMMA) == false) {
      throw new CompileException("Missing comma in SWAP statement.");
    }

    VariableNode var2 = null;
    if (var1.getType() == NodeType.NUM) {
      var2 = parseNumOrNumArrayVar();
    } else if (var1.getType() == NodeType.STR) {
      var2 = parseStrOrStrArrayVar();
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
      throw new CompileException("WHILE: Missing or illegal number expression in WHILE statement.");
    }
    return new WhileStatement(numExpr);
  }

  // parse expressions /////////////////////////////////////////////////////////

  /**
   * Expression grammar.
   *
   * <expr> := <strExpr> | <numExpr>
   *
   * <strExpr> := <strAddExpr>
   * <strAddExpr> := <strTerm> [+ <strTerm>]*
   * <strTerm> := <strFN> | <strFunc> | <strOrStrArrayVar> | <strConst>
   * <strFN> := FN<varName>$([<expr>[,<expr>]*])
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
   * <numFactor> := <numStrRelExpr> | <numFN> | <numFunc> | <strOrStrArrayVar> | <numOrNumArrayVar> | <numConst> | -<numFactor> | (<numExpr>)
   * <numFN> := FN<varName>([<expr>[,<expr>]*])
   *
   * <numOrNumArrayVar> := <numArrayVar> | <numVar>
   * <numArrayVar> := <varName>(<numExpr>[,<numExpr>])
   * <numVar> := <varName>
   *
   * <numStrRelExpr> := <numStrAddExpr> [<>|<=|>=|<|=|> <numStrAddExpr>]*
   * <numStrAddExpr> := <strTerm> [+ <strTerm>]*
   */

  private Token getNextTokenOutOf(Token... tokens) {
    for (Token token : tokens) {
      if (isNextToken(token)) {
        return token;
      }
    }
    return null;
  }

  private boolean isNextToken(Token token) {
    int savePos = this.pos;

    // skip leading whitespace, terminate if EOL reached
    while (true) {
      if (this.pos >= this.stringToParse.length()) {
        return false; // EOL
      }
      if (Character.isWhitespace(this.stringToParse.charAt(this.pos)) == false) {
        break;
      }
      this.pos++;
    }

    String tokenChars = token.getChars();
    boolean isNextToken = this.stringToParse.startsWith(tokenChars, this.pos);
    this.pos = isNextToken ? this.pos + tokenChars.length() : savePos;
    return isNextToken;
  }

  // Parse expression //////////////////////////////////////////////////////////

  private INode parseExpr() {
    INode result = parseStrExpr(); // parse string expressions before number expressions
    if (result == null) {
      result = parseNumExpr();
    }
    return result;
  }

  // Parse number expression ///////////////////////////////////////////////////

  private INode parseNumExpr() {
    INode result = parseNumNotExpr();
    if (result != null) {
      Token token;

      // the while loop turns a left-recursive binary operation into a
      // right-recursive operation by holding back the recursive descent until
      // all adjacent operators of the same production level are parsed.
      //
      // sample expression: 1 - 2 - 3
      // left-recursive:    1 - ( 2 - 3 ) = 2  (wrong)
      // right-recursive:   ( 1 - 2 ) - 3 = -4 (correct)

      while ((token = getNextTokenOutOf(AND, OR, XOR)) != null) {
        INode node = parseNumNotExpr();
        if (node == null) {
          throw new CompileException("Missing or invalid number expression after logical operator \"" + token.getChars() + "\".");
        }
        result = BinaryNode.createBinaryNode(token, result, node);
      }
    }
    return result;
  }

  private INode parseNumNotExpr() {
    INode result = null;
    if (isNextKeyword(NOT)) {
      result = UnaryNode.createUnaryNode(NOT, parseNumNotExpr());
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
          throw new CompileException("Missing or invalid number expression after relational operator \"" + token.getChars() + "\".");
        }
        result = BinaryNode.createBinaryNode(token, result, node);
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
          throw new CompileException("Missing or invalid number expression after arithmetic operator \"" + token.getChars() + "\".");
        }
        result = BinaryNode.createBinaryNode(token, result, node);
      }
    }
    return result;
  }

  private INode parseNumModExpr() {
    INode result = parseNumIntDivExpr();
    if (result != null) {
      while (isNextKeyword(MOD)) {
        INode node = parseNumIntDivExpr();
        if (node == null) {
          throw new CompileException("Missing or invalid number expression after arithmetic operator \"MOD\".");
        }
        result = BinaryNode.createBinaryNode(MOD, result, node);
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
          throw new CompileException("Missing or invalid number expression after arithmetic operator \"\\\".");
        }
        result = BinaryNode.createBinaryNode(INT_DIVIDE, result, node);
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
          throw new CompileException("Missing or invalid number expression after arithmetic operator \"" + token.getChars() + "\".");
        }
        result = BinaryNode.createBinaryNode(token, result, node);
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
          throw new CompileException("Missing or invalid number expression after arithmetic operator \"^\".");
        }
        result = BinaryNode.createBinaryNode(POWER, result, node);
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
      result = parseStrOrStrArrayVar();
      if (result != null) {
        throw new CompileException("Illegal string variable(s) in number expression.");
      }
    }
    if (result == null) { // parse arrays before (simple) variables
      result = parseNumOrNumArrayVar();
    }
    if (result == null) {
      result = parseNumConst();
    }
    if (result == null) {
      if (isNextToken(UNARY_MINUS)) {
        result = UnaryNode.createUnaryNode(UNARY_MINUS, parseNumFactor());
      }
    }
    if (result == null) {
      if (isNextToken(OPEN)) {
        result = UnaryNode.createUnaryNode(OPEN, parseNumExpr());
        if (isNextToken(CLOSE) == false) {
          throw new CompileException("Missing closing parentheses in number expression.");
        }
      }
    }
    return result;
  }

  private VariableNode parseNumOrNumArrayVar() {
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
        throw new CompileException("Missing or invalid expression for first index of number array variable " + varName + ").");
      }
      if (isNextToken(COMMA)) {
        INode dim2Expr = parseNumExpr();
        if (dim2Expr == null) {
          throw new CompileException("Missing or invalid expression for second index of number array variable " + varName + ").");
        }
        if (isNextToken(CLOSE)) {
          if (this.arrayVariables.containsKey(varName)) {
            if (arrayVariables.get(varName).getNumDims() != 2) {
              throw new CompileException("Number array variable " + varName + ") does not have two indexes like in earlier part of code.");
            }
          } else {
            ArrayVariableInfo info = new ArrayVariableInfo(2, false /* usedInDimStatement */);
            this.arrayVariables.put(varName, info);
          }
          return VariableNode.createVariableNode(varName, NodeType.NUM, dim1Expr, dim2Expr);
        }
        throw new CompileException("Missing closing parentheses after second index of number array variable " + varName + ").");
      }
      if (isNextToken(CLOSE)) {
        if (this.arrayVariables.containsKey(varName)) {
          if (arrayVariables.get(varName).getNumDims() != 1) {
            throw new CompileException("Number array variable " + varName + ") does not have one index like in earlier part of code.");
          }
        } else {
          ArrayVariableInfo info = new ArrayVariableInfo(1, false /* usedInDimStatement */);
          this.arrayVariables.put(varName, info);
        }
        return VariableNode.createVariableNode(varName, NodeType.NUM, dim1Expr);
      }
      throw new CompileException("Missing closing parentheses after first index of number array variable " + varName + ").");
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
      return NumNode.createNumberNode(strNumber);
    }
    return null;
  }

  private INode parseNumStrRelExpr() {
    INode result = parseNumStrAddExpr();
    if (result != null) {
      Token token = getNextTokenOutOf(STRING_NOT_EQUAL, STRING_LESS_OR_EQUAL, STRING_GREATER_OR_EQUAL, STRING_LESS, STRING_EQUAL, STRING_GREATER); // op order matters!
      if (token == null) {
        throw new CompileException("No relational string operator found to convert the string expression into a number.");
      }
      INode node = parseNumStrAddExpr();
      if (node == null) {
        throw new CompileException("Missing or invalid string expression after relational string operator \"" + token.getChars() + "\".");
      }
      result = BinaryNode.createBinaryNode(token, result, node);
    }
    return result;
  }

  private INode parseNumStrAddExpr() {
    INode result = parseStrTerm();
    if (result != null) {
      while (true) {
        int tmpPos = this.pos;
        if (isNextToken(STRING_ADD)) {
          INode node = parseStrTerm();
          if (node != null) {
            result = BinaryNode.createBinaryNode(STRING_ADD, result, node);
          } else {
            // "+ <numExpr>" encountered -> unparse "+ <numExpr>"
            this.pos = tmpPos;
            break;
          }
        } else {
          // no "+" encountered -> stop parsing for "+ <strExpr>"
          break;
        }
      }
    }
    return result;
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
          throw new CompileException("INSTR(): Missing or illegal first argument.");
        }
        if (isNextToken(COMMA) == false) {
          throw new CompileException("INSTR(): Missing comma after first argument.");
        }

        INode arg2Expr = null;
        if (arg1Expr.getType() == NodeType.NUM) {
          // INSTR(I, X$, Y$)
          arg2Expr = parseStrExpr();
          if (arg2Expr == null) {
            throw new CompileException("INSTR(): Missing or illegal but last argument.");
          }
          if (isNextToken(COMMA) == false) {
            throw new CompileException("INSTR(): Missing comma before last argument.");
          }
        } else if (arg1Expr.getType() == NodeType.STR) {
          // INSTR(X$, Y$)
          arg2Expr = arg1Expr;
          arg1Expr = NumNode.createNumberNode("1");
        }

        INode arg3Expr = parseStrExpr();
        if (arg3Expr == null) {
          throw new CompileException("INSTR(): Missing or illegal last argument.");
        }

        result = FunctionNode.createFunctionNode(INSTR, arg1Expr, arg2Expr, arg3Expr);
        if (isNextToken(CLOSE) == false) {
          throw new CompileException("INSTR(): Cannot find closing parenthesis.");
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

  // Parse string expressions //////////////////////////////////////////////////

  private INode parseStrExpr() {
    int tmpPos = this.pos;
    INode result = parseStrAddExpr();
    if (result != null) {
      Token token = getNextTokenOutOf(STRING_NOT_EQUAL, STRING_LESS_OR_EQUAL, STRING_GREATER_OR_EQUAL, STRING_LESS, STRING_EQUAL, STRING_GREATER); // op order matters!
      if (token != null) {
        this.pos = tmpPos;
        result = null;
      }
    }
    return result;
  }

  private INode parseStrAddExpr() {
    INode result = parseStrTerm();
    if (result != null) {
      while (isNextToken(STRING_ADD)) {
        INode node = parseStrTerm();
        if (node == null) {
          throw new CompileException("Missing or invalid string expression after +.");
        }
        result = BinaryNode.createBinaryNode(STRING_ADD, result, node);
      }
    }
    return result;
  }

  private INode parseStrTerm() {
    INode result = parseStrFN(); // parse functions before variables
    if (result == null) {
      result = parseStrFunc();
    }
    if (result == null) { // parse arrays before (simple) variables
      result = parseStrOrStrArrayVar();
    }
    if (result == null) {
      result = parseStrConst();
    }
    return result;
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
          throw new CompileException("MID$(): Missing or illegal first argument.");
        }
        if (isNextToken(COMMA) == false) {
          throw new CompileException("MID$(): Missing first comma.");
        }

        INode arg2Expr = parseNumExpr();
        if (arg2Expr == null) { // NOTE: Will never happen - parseNumExpr() throws exception first
          throw new CompileException("MID$(): Missing or illegal second argument.");
        }

        INode arg3Expr;
        if (isNextToken(COMMA)) {
          arg3Expr = parseNumExpr();
          if (arg3Expr == null) { // NOTE: Will never happen - parseNumExpr() throws exception first
            throw new CompileException("MID$(): Missing or illegal third argument.");
          }
        } else {
          arg3Expr = NumNode.createNumberNode("255");
        }

        result = FunctionNode.createFunctionNode(MID, arg1Expr, arg2Expr, arg3Expr);
        if (isNextToken(CLOSE) == false) {
          throw new CompileException("MID$(): Missing closing parenthesis.");
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

  private VariableNode parseStrOrStrArrayVar() {
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
        throw new CompileException("Cannot parse expression for first index of string array variable " + varName + ".");
      }
      if (isNextToken(COMMA)) {
        INode dim2Expr = parseNumExpr();
        if (dim2Expr == null) {
          throw new CompileException("Cannot parse expression for second index of string array variable " + varName + ".");
        }
        if (isNextToken(CLOSE)) {
          if (this.arrayVariables.containsKey(varName)) {
            if (arrayVariables.get(varName).getNumDims() != 2) {
              throw new CompileException("String array variable " + varName + ") does not have two indexes like in earlier part of code.");
            }
          } else {
            ArrayVariableInfo info = new ArrayVariableInfo(2, false /* usedInDimStatement */);
            this.arrayVariables.put(varName, info);
          }
          return VariableNode.createVariableNode(varName, NodeType.STR, dim1Expr, dim2Expr);
        }
        throw new CompileException("Closing parentheses missing after second index of string array variable " + varName + ".");
      }
      if (isNextToken(CLOSE)) {
        if (this.arrayVariables.containsKey(varName)) {
          if (arrayVariables.get(varName).getNumDims() != 1) {
            throw new CompileException("String array variable " + varName + ") does not have one index like in earlier part of code.");
          }
        } else {
          ArrayVariableInfo info = new ArrayVariableInfo(1, false /* usedInDimStatement */);
          this.arrayVariables.put(varName, info);
        }
        return VariableNode.createVariableNode(varName, NodeType.STR, dim1Expr);
      }
      throw new CompileException("Closing parentheses missing after first index of string array variable " + varName + ".");
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
      if (strConst.length() > 255) { // NOTE: will never happen, code lines are 255 chars maximum
        throw new CompileException("String constant has more than 255 characters.");
      }
      return StrNode.createStringNode(strConst);
    }
    return null;
  }

  private StrNode parseAnyConstant() {
    String str = getAnyConstant();
    if (str != null) {
      if (str.length() > 255) { // NOTE: will never happen, code lines are 255 chars maximum
        throw new CompileException("DATA Element has more than 255 characters.");
      }
      return StrNode.createStringNode(str);
    }
    return null;
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

  private FunctionNode internalParseFunction(FunctionToken functionToken) {
    FunctionNode result = null;
    if (isNextToken(functionToken)) {
      List<INode> args = new ArrayList<INode>();
      NodeType[] argTypes = functionToken.getArgTypes();
      int numArgs = argTypes.length;

      for (int i = 0; i < numArgs; i++) {
        if (i > 0) {
          if (isNextToken(COMMA) == false) {
            throw new CompileException("Missing comma while parsing function " + functionToken.getChars() + ").");
          }
        }

        INode argExpr = null;
        if (argTypes[i] == NodeType.NUM) {
          argExpr = parseNumExpr();
        } else if (argTypes[i] == NodeType.STR) {
          argExpr = parseStrExpr();
        }
        if (argExpr == null) {
          throw new CompileException("Missing or invalid argument(s) while parsing function " + functionToken.getChars() + ").");
        }
        args.add(argExpr);
      }
      result = FunctionNode.createFunctionNode(functionToken, args.toArray(new INode[numArgs]));
      if (isNextToken(CLOSE) == false) {
        throw new CompileException("Missing closing parenthesis while parsing function " + functionToken.getChars() + ").");
      }
    }
    return result;
  }

  private FnFunctionNode parseNumFN() {
    String funcName = getNumFunctionName();
    if (funcName == null) {
      return null;
    }
    return parseInternalFN(funcName, NodeType.NUM);
  }

  private FnFunctionNode parseStrFN() {
    String funcName = getStrFunctionName();
    if (funcName == null) {
      return null;
    }
    return parseInternalFN(funcName, NodeType.STR);
  }

  private FnFunctionNode parseInternalFN(String funcName, NodeType funcType) {
    // FN<name>[$](<expr>[,<expr>]*)

    String funcNameString = funcName.substring(0, funcName.length() - 1);

    List<INode> funcArgExprs = new ArrayList<INode>();
    do {
      INode funcArgExpr = parseExpr();
      if (funcArgExpr == null) {
        throw new CompileException("FN: Cannot parse or invalid argument expression in function call of " + funcNameString + ".");
      }
      funcArgExprs.add(funcArgExpr);
    } while (isNextToken(COMMA));

    if (isNextToken(CLOSE) == false) {
      throw new CompileException("FN: Missing ) in function call of " + funcNameString + ".");
    }

    if (funcArgExprs.isEmpty()) {
      throw new CompileException("FN: Function call of " + funcNameString + " has zero arguments.");
    }

    // compare signature of this FN<name> against DEF FN<name> (if present)
    if (this.defFnMap.containsKey(funcNameString)) {
      DefFnStatement statement = this.defFnMap.get(funcNameString);

      if (funcType != statement.getFuncExpr().getType()) {
        throw new CompileException("FN: Return type of function call of " + funcNameString + " does not match return type of definition.");
      }

      if (funcArgExprs.size() != statement.getFuncVars().length) {
        throw new CompileException("FN: Number of arguments in function call of " + funcNameString + " does not match number of arguments in definition.");
      }

      for (int i = 0; i < funcArgExprs.size(); i++) {
        if (funcArgExprs.get(i).getType() != statement.getFuncVars()[i].getType()) {
          throw new CompileException("FN: type of argument " + i + " in function call of " + funcNameString + " does not match argument type in definition.");
        }
      }
    }

    // compare signature of this FN<name> against previous FN<name> calls
    if (this.fnMap.containsKey(funcNameString)) {
      FnFunctionNode fnNode = this.fnMap.get(funcNameString);

      if (funcType != fnNode.getType()) {
        throw new CompileException("FN: Return type of function call of " + funcNameString + " does not match return type of previous FN.");
      }

      if (funcArgExprs.size() != fnNode.getFuncArgExprs().length) {
        throw new CompileException("FN: Number of arguments of function call of " + funcNameString + " does not match number of arguments in previous FN.");
      }

      for (int i = 0; i < funcArgExprs.size(); i++) {
        if (funcArgExprs.get(i).getType() != fnNode.getFuncArgExprs()[i].getType()) {
          throw new CompileException("FN: Type of argument " + i + " of function call of " + funcNameString + " does not match argument type in previous FN.");
        }
      }
    }

    FnFunctionNode fnFunctionNode = FnFunctionNode.createFnFunctionNode(funcNameString, funcType, funcArgExprs.toArray(new INode[funcArgExprs.size()]));
    this.fnMap.put(funcNameString, fnFunctionNode);

    return fnFunctionNode;
  }

  // ATOMIC MATCHERS ///////////////////////////////////////////////////////////

  /*
   * ^           | Start of string
   * \\s*?       | Match any whitespace, consumed lazily
   * (           | Start capture group 1 (number)
   * [+-]?       | Match one plus or minus, if present
   * (?:         | Start unnamed capture group 2 (mantissa)
   * [0-9]+      | Option 1: Match one or more digits, consumed lazily
   * (?:         |   Start unnamed capture group 3 (fractional part of mantissa)
   * \\.         |   Match one single period
   * [0-9]*      |   Match any digits, consumed ###
   * )           |   End unnamed capture group 3 (fractional part of mantissa)
   * ?           |   ...capture group 3 (fractional part of mantissa) is optional
   * |           | ...or...
   * \\.         | Option2: Match one single period
   * [0-9]++     |   Match one or more digits, consumed possessively
   * )           |   End unnamed capture group 2 (mantissa)
   * (?:         | Start unnamed capture group 3 (exponent)
   * [eE]        | Match one 'e' or 'E'
   * [+-]?       | Match one plus or minus, if present
   * [0-9]++     | Match one or more digits, consumed possessively
   * )           | End unnamed capture group 3 (exponent), ...
   * ?           | ... capture group 3 (exponent) is optional
   * )           | End capture group 1 (number)
   */
  private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\s*?([-+]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]++)(?:[eE][-+]?[0-9]++)?)");

  private String getNumConstant() {
    return findMatch(NUMBER_PATTERN);
  }

  /*
   * ^           | Start of string
   * \\s*?       | Match any whitespace, consumed lazily
   * \"          | Match one quote
   * (           | Start capture group (content of string)
   * [^\"]*?     | Match any character but a quote, consumed lazily
   * )           | End capture group
   * \"          | Match one quote
   */
  private static final Pattern STRING_CONST_PATTERN = Pattern.compile("^\\s*?\"([^\"]*?)\"");

  private String getStrConstant() {
    return findMatch(STRING_CONST_PATTERN);
  }

  /*
   * ^           | Start of string
   * \\s*?       | Match any whitespace, consumed lazily
   * (           | Start capture group (keyword)
   * [A-Z]       | Match one letter
   * [A-Z0-9]*+  | Match any letter or digit, consumed possessively
   * )           | End capture group
   */
  private static final Pattern KEYWORD_PATTERN = Pattern.compile("^\\s*?([A-Z][A-Z0-9]*+)");

  private boolean isNextKeyword(Token token) {
    String match = findMatch(KEYWORD_PATTERN);
    if (match != null) {
      if (match.trim().equals(token.getChars())) {
        return true;
      }
      match = unmatch(match);
    }
    return false;
  }

  /*
   * ^           | Start of string
   * \\s*?       | Match any whitespace, consumed lazily
   * (           | Start capture group (constant)
   * [^,:]*+     | Match any character but a comma or a colon, consumed possessively
   * )           | End capture group
   */
  private static final Pattern ANY_CONST_PATTERN = Pattern.compile("^\\s*?([^,:]*+)");

  private String getAnyConstant() {
    return findMatch(ANY_CONST_PATTERN);
  }

  /*
   * ^           | Start of string
   * \\s*?       | Match any whitespace, consumed lazily
   * (           | Start capture group (line number)
   * [0-9]{1,5}  | Match any sequence of digits, 1 to 5 characters long
   * )           | End capture group
   */
  private static final Pattern LINE_NUMBER_PATTERN = Pattern.compile("^\\s*?([0-9]{1,5})");

  private String getLineNumber() {
    return findMatch(LINE_NUMBER_PATTERN);
  }

  private static String statementKeywords[] = new String[] { //
    "DATA",
    "DEF",
    "DIM",
    "ELSE",
    "END",
    "FOR",
    "GOTO",
    "GOSUB",
    "IF",
    "INPUT",
    "LET",
    "NEXT",
    "ON",
    "PRINT",
    "READ",
    "REM",
    "RESTORE",
    "RETURN",
    "STEP",
    "STOP",
    "SWAP",
    "THEN",
    "TO",
    "WEND",
    "WHILE", //
  };

  private static Set<String> STATEMENT_KEYWORDS = new HashSet<String>();

  static {
    STATEMENT_KEYWORDS.addAll(Arrays.asList(statementKeywords));
  }

  /*
   * ^              | Start of string
   * \\s*?          | Match any whitespace, consumed lazily
   * (              | Start capture group (number variable name)
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*+  | Match any letter, digit, or dot, consumed possessively
   * )              | End capture group
   */
  private static final Pattern NUM_VARIABLENAME_PATTERN = Pattern.compile("^\\s*?([A-Z][A-Z0-9\\.]*+)");

  private String getNumVariableName() {
    String match = findMatch(NUM_VARIABLENAME_PATTERN);
    if (match != null) {
      if (STATEMENT_KEYWORDS.contains(match)) {
        match = unmatch(match);
      }
    }
    return match;
  }

  /*
   * ^              | Start of string
   * \\s*?          | Match any whitespace, consumed lazily
   * (              | Start capture group (string variable name)
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*?  | Match any letter, digit, or dot, consumed lazily
   * \\$            | Match one dollar sign
   * )              | End capture group
   */
  private static final Pattern STR_VARIABLENAME_PATTERN = Pattern.compile("^\\s*?([A-Z][A-Z0-9\\.]*?\\$)");

  private String getStrVariableName() {
    return findMatch(STR_VARIABLENAME_PATTERN);
  }

  private static String numFunctionKeywords[] = new String[] { //
    "INT(",
    "SQR(",
    "LEN(",
    "ABS(",
    "SGN(",
    "SIN(",
    "COS(",
    "TAN(",
    "ATN(",
    "ASC(",
    "POS(",
    "VAL(",
    "FIX(",
    "LOG(",
    "EXP(",
    "RND(",
    "INSTR(", //
  };

  private static Set<String> NUM_FUNCTION_KEYWORDS = new HashSet<String>();

  static {
    NUM_FUNCTION_KEYWORDS.addAll(Arrays.asList(numFunctionKeywords));
  }

  /*
   * ^              | Start of string
   * \\s*?          | Match any whitespace, consumed lazily
   * (              | Start capture group (number function name)
   * FN             | Match "FN"
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*?  | Match any letter, digit, or dot, consumed lazily
   * \\(            | Match an opening parenthesis
   * )              | End capture group
   */
  private static final Pattern NUM_FUNCTION_NAME_PATTERN = Pattern.compile("^\\s*?(FN[A-Z][A-Z0-9\\.]*?\\()");

  private String getNumFunctionName() {
    return findMatch(NUM_FUNCTION_NAME_PATTERN);
  }

  /*
   * ^              | Start of string
   * \\s*?          | Match any whitespace, consumed lazily
   * (?!            | Start negative lookahead. No match if characters ahead...
   * FN             | Match FN
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*?  | Match any letter, digit, or dot, consumed lazily
   * \\$            | Match one dollar sign
   * \\(            | Match an opening parenthesis
   * )              | End negative lookahead
   * (              | Start capture group (number array variable name)
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*?  | Match any letter, digit, or dot, consumed lazily
   * \\(            | Match an opening parenthesis
   * )              | End capture group
   */
  private static final Pattern NUM_ARRAY_VARIABLE_NAME_PATTERN = Pattern.compile("^\\s*?(?!FN[A-Z][A-Z0-9\\.]*?\\()([A-Z][A-Z0-9\\.]*?\\()");

  private String getNumArrayVariableName() {
    String match = findMatch(NUM_ARRAY_VARIABLE_NAME_PATTERN);
    if (match != null) {
      if (NUM_FUNCTION_KEYWORDS.contains(match)) {
        match = unmatch(match);
      }
    }
    return match;
  }

  private static String strFunctionKeywords[] = new String[] { //
    "CHR$(",
    "LEFT$(",
    "MID$(",
    "RIGHT$(",
    "STR$(",
    "SPACE$(", //
  };

  private static Set<String> STR_FUNCTION_KEYWORDS = new HashSet<String>();

  static {
    STR_FUNCTION_KEYWORDS.addAll(Arrays.asList(strFunctionKeywords));
  }

  /*
   * ^              | Start of string
   * \\s*?          | Match any whitespace, consumed lazily
   * (              | Start capture group (string function name)
   * FN             | Match "FN"
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*?  | Match any letter, digit, or dot, consumed lazily
   * \\$            | Match one dollar sign
   * \\(            | Match an opening parenthesis
   * )              | End capture group
   */
  private static final Pattern STR_FUNCTION_NAME_PATTERN = Pattern.compile("^\\s*?(FN[A-Z][A-Z0-9\\.]*?\\$\\()");

  private String getStrFunctionName() {
    return findMatch(STR_FUNCTION_NAME_PATTERN);
  }

  /*
   * ^              | Start of string
   * \\s*?          | Match any whitespace, consumed lazily
   * (?!            | Start negative lookahead. No match if characters ahead...
   * FN             | Match FN
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*?  | Match any letter, digit, or dot, consumed lazily
   * \\$            | Match one dollar sign
   * \\(            | Match an opening parenthesis
   * )              | End negative lookahead
   * (              | Start capture group (string array name)
   * [A-Z]          | Match one letter
   * [A-Z0-9\\.]*?  | Match any letter, digit, or dot, consumed lazily
   * \\$            | Match one dollar sign
   * \\(            | Match an opening parenthesis
   * )              | End capture group
   */
  private static final Pattern STR_ARRAY_VARIABLE_NAME_PATTERN = Pattern.compile("^\\s*?(?!FN[A-Z][A-Z0-9\\.]*?\\$\\()([A-Z][A-Z0-9\\.]*?\\$\\()");

  private String getStrArrayVariableName() {
    String match = findMatch(STR_ARRAY_VARIABLE_NAME_PATTERN);
    if (match != null) {
      if (STR_FUNCTION_KEYWORDS.contains(match)) {
        match = unmatch(match);
      }
    }
    return match;
  }

  private String findMatch(Pattern pattern) {
    String matched = null;
    Matcher matcher = pattern.matcher(this.stringToParse.substring(this.pos));
    if (matcher.find()) {
      matched = matcher.group(1);
      this.pos += matcher.end();
    }
    return matched;
  }

  private String unmatch(String match) {
    this.pos -= match.length();
    return null;
  }
}
