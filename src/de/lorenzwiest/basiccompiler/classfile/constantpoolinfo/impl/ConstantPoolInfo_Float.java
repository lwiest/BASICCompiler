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

public class ConstantPoolInfo_Float extends ConstantPoolInfo {
	private final float aFloat;

	public ConstantPoolInfo_Float(float aFloat) {
		super(TAG_FLOAT);
		this.aFloat = aFloat;
	}

	public float getFloat() {
		return this.aFloat;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);
		int bitsOfFloat = Float.floatToRawIntBits(this.aFloat);
		o.write_u4(bitsOfFloat);
	}

	public static int addAndGetIndex(ConstantPool constantPool, float aFloat) {
		String key = getKey(aFloat);
		if (constantPool.contains(key) == false) {
			constantPool.put(key, createInfo(constantPool, aFloat));
		}
		return constantPool.getIndex(key);
	}

	private static String getKey(float aFloat) {
		return "FLOAT_" + aFloat;
	}

	private static ConstantPoolInfo_Float createInfo(ConstantPool constantPool, float aFloat) {
		return new ConstantPoolInfo_Float(aFloat);
	}
}
