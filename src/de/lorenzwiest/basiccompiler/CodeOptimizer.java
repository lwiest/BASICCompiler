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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.parser.nodes.INode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.StrNode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.TokenNode;
import de.lorenzwiest.basiccompiler.parser.statements.Statement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.GosubStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.GotoStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.IfStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.LineNumberStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.OnGosubStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.OnGotoStatement;
import de.lorenzwiest.basiccompiler.parser.statements.impl.PrintStatement;
import de.lorenzwiest.basiccompiler.parser.tokens.Token;

public class CodeOptimizer {
	private List<Statement> statements;
	private Set<String> lineNumbersBranchedTo;

	public void optimize(List<Statement> statements) {
		this.statements = statements;
		this.lineNumbersBranchedTo = createLineNumberBranchedToSet();

		foldPrintStatements(statements);
	}

	private void foldPrintStatements(List<Statement> statements) {
		List<INode> printExprs = new ArrayList<INode>();
		int startPos = -1;
		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			if (s instanceof PrintStatement) {
				if (startPos == -1) {
					startPos = i;
				}
				printExprs.addAll(normalize(((PrintStatement) s).getExpressions()));
			} else if (startPos != -1) {
				if (((s instanceof LineNumberStatement) == false) || (isBranchedTo((LineNumberStatement) s))) {
					foldPrintStatements(statements, startPos, i, printExprs);
					i = startPos + 1;
					startPos = -1;
				}
			}
		}
		if (startPos != -1) {
			foldPrintStatements(statements, startPos, statements.size(), printExprs);
		}
	}

	private List<INode> normalize(INode[] printExprs) {
		List<INode> normalizedPrintExprs = new ArrayList<INode>(); // we need a copy here!
		normalizedPrintExprs.addAll(Arrays.asList(printExprs));
		if ((printExprs.length == 0) || (!isTokenExpr(printExprs[printExprs.length - 1], Token.SEMICOLON) && !isTokenExpr(printExprs[printExprs.length - 1], Token.COMMA))) {
			normalizedPrintExprs.add(TokenNode.createTokenNode(Token.SEMICOLON));
			normalizedPrintExprs.add(StrNode.createStringNode(Compiler.CR));
			normalizedPrintExprs.add(TokenNode.createTokenNode(Token.SEMICOLON));
		}
		return normalizedPrintExprs;
	}

	private void foldPrintStatements(List<Statement> statements, int fromIncl, int toExcl, List<INode> printExprs) {
		PrintStatement newPrintStatement = new PrintStatement(fold(printExprs).toArray(new INode[0]));
		statements.subList(fromIncl, toExcl).clear();
		statements.add(fromIncl, newPrintStatement);
		printExprs.clear();
	}

	private boolean isTokenExpr(INode expr, Token token) {
		return (expr instanceof TokenNode) && (((TokenNode) expr).getToken() == token);
	}

	private List<INode> fold(List<INode> printExprs) {
		List<INode> foldedExprs = new ArrayList<INode>(printExprs);

		for (int i = 0; i < (foldedExprs.size() - 1); i++) { // remove all but last semicolon
			INode printExpr = foldedExprs.get(i);
			if (isTokenExpr(printExpr, Token.SEMICOLON)) {
				foldedExprs.remove(i);
				i--;
			}
		}

		for (int i = 0; i < foldedExprs.size(); i++) {
			INode printExpr1 = foldedExprs.get(i);
			if (printExpr1 instanceof StrNode) {
				if (i < (foldedExprs.size() - 1)) {
					INode printExpr2 = foldedExprs.get(i + 1);
					if (printExpr2 instanceof StrNode) {
						String s1 = ((StrNode) printExpr1).getValue();
						String s2 = ((StrNode) printExpr2).getValue();
						foldedExprs.remove(i);
						foldedExprs.remove(i);
						foldedExprs.add(i, StrNode.createStringNode(s1 + s2));
						i--;
					}
				}
			}
		}
		return foldedExprs;
	}

	private boolean isBranchedTo(LineNumberStatement s) {
		return this.lineNumbersBranchedTo.contains(s.getLineNumber());
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
}
