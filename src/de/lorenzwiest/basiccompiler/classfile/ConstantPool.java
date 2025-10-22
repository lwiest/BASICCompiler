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

package de.lorenzwiest.basiccompiler.classfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.ConstantPoolInfo;

public class ConstantPool {
	private List<ConstantPoolInfo> constantPoolInfos = new ArrayList<ConstantPoolInfo>();
	private Map<String, Integer> mapOfKeys = new HashMap<String, Integer>();

	public ConstantPool() {
		this.constantPoolInfos.add(null);  // add first constant pool entry, a dummy entry
	}

	public void put(String key, ConstantPoolInfo constantPoolInfo) {
		int nextIndex = this.constantPoolInfos.size();

		this.constantPoolInfos.add(constantPoolInfo);
		this.mapOfKeys.put(key, nextIndex);
	}

	public ConstantPoolInfo get(int constantPoolIndex) {
		return this.constantPoolInfos.get(constantPoolIndex);
	}

	public boolean contains(String key) {
		return this.mapOfKeys.containsKey(key);
	}

	public int getIndex(String key) {
		Integer anIndex = this.mapOfKeys.get(key);
		if (anIndex != null) {
			return anIndex.intValue();
		}
		return -1;
	}

	public int getCount() {
		return this.constantPoolInfos.size();
	}

	public ConstantPoolInfo[] getConstantPoolInfos() {
		List<ConstantPoolInfo> allButFirstConstantPoolInfo = this.constantPoolInfos.subList(1, this.constantPoolInfos.size());
		return allButFirstConstantPoolInfo.toArray(new ConstantPoolInfo[0]);
	}
}
