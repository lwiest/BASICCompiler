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

package de.lorenzwiest.basiccompiler.bytecode.constantpoolinfo.impl;

import de.lorenzwiest.basiccompiler.bytecode.ConstantPool;
import de.lorenzwiest.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

public class ConstantPoolInfo_FieldRef extends ConstantPoolInfo {
	private final int classIndex;       // u2
	private final int nameAndTypeIndex; // u2

	public ConstantPoolInfo_FieldRef(int classIndex, int nameAndTypeIndex) {
		super(TAG_FIELDREF);
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public int getClassIndex() {
		return this.classIndex;
	}

	public int getNameAndTypeIndex() {
		return this.nameAndTypeIndex;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);
		o.write_u2(this.classIndex);
		o.write_u2(this.nameAndTypeIndex);
	}

	public static int addAndGetIndex(ConstantPool constantPool, String className, String fieldName, String descriptor) {
		String key = getKey(className, fieldName, descriptor);
		if (constantPool.contains(key) == false) {
			constantPool.put(key, createInfo(constantPool, className, fieldName, descriptor));
		}
		return constantPool.getIndex(key);
	}

	private static String getKey(String className, String fieldName, String descriptor) {
		return "FIELD_" + className + fieldName + descriptor;
	}

	private static ConstantPoolInfo_FieldRef createInfo(ConstantPool constantPool, String className, String fieldName, String descriptor) {
		int classIndex = ConstantPoolInfo_Class.addAndGetIndex(constantPool, className);
		int nameAndTypeIndex = ConstantPoolInfo_NameAndType.addAndGetIndex(constantPool, fieldName, descriptor);
		return new ConstantPoolInfo_FieldRef(classIndex, nameAndTypeIndex);
	}
}
