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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;

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
		} catch (IOException e) {
			// ignore
		}
		return b.toByteArray();
	}

	public static int addAndReturnIndex(List<ConstantPoolInfo> constantPool, String string) {
		if (contains(constantPool, string) == false) {
			add(constantPool, string);
		}
		return getIndex(constantPool, string);
	}

	private static boolean contains(List<ConstantPoolInfo> constantPool, String string) {
		return getIndex(constantPool, string) > -1;
	}

	private static void add(List<ConstantPoolInfo> constantPool, String string) {
		constantPool.add(new ConstantPoolInfo_Utf8(string));
	}

	public static String getString(List<ConstantPoolInfo> constantPool, int index) {
		ConstantPoolInfo_Utf8 infoUtf8 = (ConstantPoolInfo_Utf8) constantPool.get(index);
		return infoUtf8.getString();
	}

	private static int getIndex(List<ConstantPoolInfo> constantPool, String string) {
		for (int i = 0; i < constantPool.size(); i++) {  // TODO: implement faster lookup, although speed not an issue yet
			ConstantPoolInfo info = constantPool.get(i);
			if (info.getTag() == TAG_UTF8) {
				ConstantPoolInfo_Utf8 infoUtf8 = (ConstantPoolInfo_Utf8) info;
				String stringToCompare = infoUtf8.getString();
				if (string.equals(stringToCompare)) {
					return i;
				}
			}
		}
		return -1;
	}
}
