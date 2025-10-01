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
import de.lorenzwiest.basiccompiler.parser.tokens.Token;

public class BinaryNode implements INode {
	private final Token op;
	private final INode leftNode;
	private final INode rightNode;
	private final NodeType nodeType;

	private BinaryNode(Token op, INode leftNode, INode rightNode, NodeType nodeType) {
		this.op = op;
		this.leftNode = leftNode;
		this.rightNode = rightNode;
		this.nodeType = nodeType;
	}

	public static INode create(Token op, INode leftNode, INode rightNode, NodeType nodeType) {
		if ((leftNode == null) || (rightNode == null)) {
			return null;
		}
		return new BinaryNode(op, leftNode, rightNode, nodeType);
	}

	public Token getOp() {
		return this.op;
	}

	public INode getLeftNode() {
		return this.leftNode;
	}

	public INode getRightNode() {
		return this.rightNode;
	}

	@Override
	public NodeType getType() {
		return this.nodeType;
	}
}
