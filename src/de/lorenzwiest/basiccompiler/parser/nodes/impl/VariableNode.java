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

package de.lorenzwiest.basiccompiler.parser.nodes.impl;

import de.lorenzwiest.basiccompiler.parser.nodes.INode;
import de.lorenzwiest.basiccompiler.parser.nodes.NodeType;

public class VariableNode implements INode {
	private static final INode[] EMPTY = new INode[0];

	private final String varName;
	private final NodeType type;
	private final INode[] dimExpressions;

	private VariableNode(String variableName, NodeType type, INode[] dimExpressions) {
		this.varName = variableName;
		this.type = type;
		this.dimExpressions = dimExpressions;
	}

	protected VariableNode(String variableName, NodeType type) {
		this(variableName, type, EMPTY);
	}

	public static VariableNode create(String variableName, NodeType type, INode... dimExpressions) {
		return new VariableNode(variableName, type, dimExpressions);
	}

	public static VariableNode createVariableNode(String variableName, NodeType type) {
		return new VariableNode(variableName, type, EMPTY);
	}

	public String getVariableName() {
		return this.varName;
	}

	public INode[] getDimExpressions() {
		return this.dimExpressions;
	}

	@Override
	public NodeType getType() {
		return this.type;
	}
}