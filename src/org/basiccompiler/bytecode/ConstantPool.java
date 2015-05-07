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

package org.basiccompiler.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;

public class ConstantPool {
	private List<ConstantPoolInfo> listOfConstantPoolInfos = new ArrayList<ConstantPoolInfo>();
	private Map<String, Integer> mapOfKeys = new HashMap<String, Integer>();

	public ConstantPool() {
		this.listOfConstantPoolInfos.add(null);  // add first constant pool entry, a dummy entry
	}

	public void put(String key, ConstantPoolInfo constantPoolInfo) {
		int nextIndex = this.listOfConstantPoolInfos.size();

		this.listOfConstantPoolInfos.add(constantPoolInfo);
		this.mapOfKeys.put(key, nextIndex);
	}

	public ConstantPoolInfo get(int constantPoolIndex) {
		return this.listOfConstantPoolInfos.get(constantPoolIndex);
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
		return this.listOfConstantPoolInfos.size();
	}

	public ConstantPoolInfo[] getConstantPoolInfos() {
		List<ConstantPoolInfo> allButFirstConstantPoolInfo = this.listOfConstantPoolInfos.subList(1, this.listOfConstantPoolInfos.size());
		return allButFirstConstantPoolInfo.toArray(new ConstantPoolInfo[0]);
	}
}
