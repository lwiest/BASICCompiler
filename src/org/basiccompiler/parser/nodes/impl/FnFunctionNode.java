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

public class FnFunctionNode implements INode {
	private String funcName;
	private NodeType funcType;
	private INode[] funcArgExprs;

	private FnFunctionNode(String funcName, NodeType funcType, INode... funcArgExprs) {
		this.funcName = funcName;
		this.funcType = funcType;
		this.funcArgExprs = funcArgExprs;
	}

	public static FnFunctionNode createFnFunctionNode(String funcName, NodeType funcType, INode... funcArgExprs) {
		return new FnFunctionNode(funcName, funcType, funcArgExprs);
	}

	public String getFuncName() {
		return this.funcName;
	}

	public INode[] getFuncArgExprs() {
		return this.funcArgExprs;
	}

	// @Override commented for JDK 5 compatibility
	public NodeType getType() {
		return this.funcType;
	}
}