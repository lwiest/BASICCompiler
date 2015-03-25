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

package org.basiccompiler.parser.nodes.impl;

import org.basiccompiler.parser.nodes.INode;
import org.basiccompiler.parser.nodes.NodeType;
import org.basiccompiler.parser.tokens.Token;

public class BinaryNode implements INode {
	private final Token op;
	private final INode leftNode;
	private final INode rightNode;

	private BinaryNode(Token op, INode leftNode, INode rightNode) {
		this.op = op;
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	public static INode createBinaryNode(Token op, INode leftNode, INode rightNode) {
		if ((leftNode == null) || (rightNode == null)) {
			return null;
		}
		return new BinaryNode(op, leftNode, rightNode);
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

	// @Override commented for JDK 5 compatibility
	public NodeType getType() {
		if ((this.op == Token.STRING_ADD)) {
			return NodeType.STR;
		}
		return NodeType.NUM;
	}
}
