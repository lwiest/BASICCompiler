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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.lorenzwiest.basiccompiler.bytecode.ConstantPool;
import de.lorenzwiest.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

public class ConstantPoolInfo_Utf8 extends ConstantPoolInfo {
	private final String string;

	public ConstantPoolInfo_Utf8(String string) {
		super(TAG_UTF8);
		this.string = string;
	}

	public String getString() {
		return this.string;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);

		byte[] modUtf8 = toModifiedUtf8(this.string);
		o.write_u2(modUtf8.length);
		for (byte b : modUtf8) {
			o.write_u1(b);
		}
	}

	private byte[] toModifiedUtf8(String string) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				b.write(0x0000 | (c & 0x007F));
			} else if ((c == 0x0000) || ((c >= 0x0080) && (c <= 0x07FF))) {
				b.write(0x00C0 | ((c >> 6) & 0x001F));
				b.write(0x0080 | (c & 0x003F));
			} else if ((c >= 0x0800) && (c <= 0xFFFF)) {
				b.write(0x00E0 | ((c >> 12) & 0x000F));
				b.write(0x0080 | ((c >> 6) & 0x003F));
				b.write(0x0080 | (c & 0x003F));
			}
		}

		try {
			b.flush();
			b.close();
		} catch (IOException e) {
			// ignore
		}
		return b.toByteArray();
	}

	public static int addAndGetIndex(ConstantPool constantPool, String string) {
		String key = getKey(string);
		if (constantPool.contains(key) == false) {
			constantPool.put(key, createInfo(constantPool, string));
		}
		return constantPool.getIndex(key);
	}

	private static String getKey(String string) {
		return "UTF8_" + string;
	}

	private static ConstantPoolInfo_Utf8 createInfo(ConstantPool constantPool, String string) {
		return new ConstantPoolInfo_Utf8(string);
	}
}
