/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Lorenz Wiest
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

public class ConstantPoolInfo_Long extends ConstantPoolInfo {
	private final long aLong;

	public ConstantPoolInfo_Long(long aLong) {
		super(TAG_LONG);
		this.aLong = aLong;
	}

	public long getLong() {
		return this.aLong;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);
		o.write_u8(this.aLong);
	}

	public static int addAndGetIndex(ConstantPool constantPool, long aLong) {
		String key = getKey(aLong);
		if (constantPool.contains(key) == false) {
			constantPool.put(key, createInfo(constantPool, aLong));
		}
		return constantPool.getIndex(key);
	}

	private static String getKey(long aLong) {
		return "LONG_" + aLong;
	}

	private static ConstantPoolInfo_Long createInfo(ConstantPool constantPool, long aLong) {
		return new ConstantPoolInfo_Long(aLong);
	}
}
