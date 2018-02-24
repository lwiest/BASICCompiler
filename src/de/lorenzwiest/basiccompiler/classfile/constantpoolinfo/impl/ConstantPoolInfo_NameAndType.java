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

package de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl;

import de.lorenzwiest.basiccompiler.classfile.ConstantPool;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.ConstantPoolInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

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
