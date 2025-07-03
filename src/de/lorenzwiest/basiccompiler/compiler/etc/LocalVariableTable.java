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

package de.lorenzwiest.basiccompiler.compiler.etc;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.lorenzwiest.basiccompiler.parser.nodes.NodeType;
import de.lorenzwiest.basiccompiler.parser.nodes.impl.LocalVariableNode;

public class LocalVariableTable {
	private Map<String /* varName */ , LocalVariableNode /* varNode */ > map = new HashMap<String, LocalVariableNode>();

	public LocalVariableNode addAndGetLocalVariableNode(String variableName, NodeType type) {
		if (this.map.containsKey(variableName) == false) {
			int localIndex = this.map.size() + 1;
			LocalVariableNode locVarNode = LocalVariableNode.create(variableName, type, localIndex);
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
