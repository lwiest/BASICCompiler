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

public class VariableNode implements INode {
	private final static INode[] EMPTY = new INode[0];

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

	public static VariableNode createVariableNode(String variableName, NodeType type, INode... dimExpressions) {
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

	// @Override commented for JDK 5 compatibility
	public NodeType getType() {
		return this.type;
	}
}