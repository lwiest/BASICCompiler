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

package de.lorenzwiest.basiccompiler.bytecode.constantpoolinfo.impl;

import de.lorenzwiest.basiccompiler.bytecode.ConstantPool;
import de.lorenzwiest.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

public class ConstantPoolInfo_Class extends ConstantPoolInfo {
	private final int nameIndex; // u2

	public ConstantPoolInfo_Class(int nameIndex) {
		super(TAG_CLASS);
		this.nameIndex = nameIndex;
	}

	public int getNameIndex() {
		return this.nameIndex;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);
		o.write_u2(this.nameIndex);
	}

	public static int getIndex(ConstantPool constantPool, String className) {
		String key = getKey(className);
		return constantPool.getIndex(key);
	}

	private static String getKey(String className) {
		return "CLASS_" + className;
	}

	public static int addAndGetIndex(ConstantPool constantPool, String className) {
		String key = getKey(className);
		if (constantPool.contains(key) == false) {
			constantPool.put(key, createInfo(constantPool, className));
		}
		return constantPool.getIndex(key);
	}

	private static ConstantPoolInfo_Class createInfo(ConstantPool constantPool, String className) {
		int nameIndex = ConstantPoolInfo_Utf8.addAndGetIndex(constantPool, className);
		return new ConstantPoolInfo_Class(nameIndex);
	}
}