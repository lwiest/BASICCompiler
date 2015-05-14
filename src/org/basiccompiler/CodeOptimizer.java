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

package org.basiccompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.basiccompiler.compiler.Compiler;
import org.basiccompiler.parser.nodes.INode;
import org.basiccompiler.parser.nodes.impl.StrNode;
import org.basiccompiler.parser.nodes.impl.TokenNode;
import org.basiccompiler.parser.statements.Statement;
import org.basiccompiler.parser.statements.impl.GosubStatement;
import org.basiccompiler.parser.statements.impl.GotoStatement;
import org.basiccompiler.parser.statements.impl.IfStatement;
import org.basiccompiler.parser.statements.impl.LineNumberStatement;
import org.basiccompiler.parser.statements.impl.OnGosubStatement;
import org.basiccompiler.parser.statements.impl.OnGotoStatement;
import org.basiccompiler.parser.statements.impl.PrintStatement;
import org.basiccompiler.parser.tokens.Token;

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
