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

package org.basiccompiler.bytecode.constantpoolinfo.impl;

import org.basiccompiler.bytecode.ConstantPool;
import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;

public class ConstantPoolInfo_NameAndType extends ConstantPoolInfo {
	private final int nameIndex;       // u2
	private final int descriptorIndex; // u2

	public ConstantPoolInfo_NameAndType(int nameIndex, int descriptorIndex) {
		super(TAG_NAME_AND_TYPE);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public int getNameIndex() {
		return this.nameIndex;
	}

	public int getDescriptorIndex() {
		return this.descriptorIndex;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);
		o.write_u2(this.nameIndex);
		o.write_u2(this.descriptorIndex);
	}

	public static int addAndGetIndex(ConstantPool constantPool, String name, String descriptor) {
		String key = getKey(name, descriptor);
		if (constantPool.contains(key) == false) {
			constantPool.put(key, createInfo(constantPool, name, descriptor));
		}
		return constantPool.getIndex(key);
	}

	private static String getKey(String name, String descriptor) {
		return "NAME_AND_TYPE_" + name + descriptor;
	}

	private static ConstantPoolInfo_NameAndType createInfo(ConstantPool constantPool, String name, String descriptor) {
		int nameIndex = ConstantPoolInfo_Utf8.addAndGetIndex(constantPool, name);
		int descriptorIndex = ConstantPoolInfo_Utf8.addAndGetIndex(constantPool, descriptor);
		return new ConstantPoolInfo_NameAndType(nameIndex, descriptorIndex);
	}
}
