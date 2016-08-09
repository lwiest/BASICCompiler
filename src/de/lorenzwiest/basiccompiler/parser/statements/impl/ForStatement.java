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

package de.lorenzwiest.basiccompiler.parser.statements.impl;

import de.lorenzwiest.basiccompiler.parser.nodes.INode;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.VariableNode;
import de.lorenzwiest.basiccompiler.parser.statements.Statement;

public class ForStatement implements Statement {
	private final VariableNode loopVariable;
	private final INode startExpression;
	private final INode endExpression;
	private final INode stepExpression;

	public ForStatement(VariableNode loopVariable, INode startExpression, INode endExpression, INode stepExpression) {
		this.loopVariable = loopVariable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
	}

	public VariableNode getLoopVariable() {
		return this.loopVariable;
	}

	public INode getStartExpression() {
		return this.startExpression;
	}

	public INode getEndExpression() {
		return this.endExpression;
	}

	public INode getStepExpression() {
		return this.stepExpression;
	}
}