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

package org.basiccompiler.compiler.etc;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.basiccompiler.parser.nodes.NodeType;
import org.basiccompiler.parser.nodes.impl.LocalVariableNode;

public class LocalVariableTable {

	private Map<String /* varName */ , LocalVariableNode /* varNode */ > map = new HashMap<String, LocalVariableNode>();

	public LocalVariableNode addAndGetLocalVariableNode(String variableName, NodeType type) {
		if (this.map.containsKey(variableName) == false) {
			int localIndex = this.map.size() + 1;
			LocalVariableNode locVarNode = LocalVariableNode.createLocalVariableNode(variableName, type, localIndex);
			this.map.put(variableName, locVarNode);
		}
		return this.map.get(variableName);
	}

	public LocalVariableNode get(String varName) {
		return this.map.get(varName);
	}

	public int size() {
		return this.map.size();
	}

	public LocalVariableNode[] sortByLocalIndex() {
		Map<Integer, LocalVariableNode> sortedMap = new TreeMap<Integer, LocalVariableNode>();
		for (LocalVariableNode locVarNode : this.map.values()) {
			sortedMap.put(locVarNode.getLocalIndex(), locVarNode);
		}
		return sortedMap.values().toArray(new LocalVariableNode[sortedMap.size()]);
	}
}
