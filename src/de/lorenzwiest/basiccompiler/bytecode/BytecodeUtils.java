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

package de.lorenzwiest.basiccompiler.bytecode;

public class BytecodeUtils {

	public static byte get_s1(int[] bytecodes, int pos) {
		return (byte) bytecodes[pos];
	}

	public static int get_u1(int[] bytecodes, int pos) {
		return bytecodes[pos];
	}

	public static int get_u2(int[] bytecodes, int pos) {
		return (bytecodes[pos] << 8) | bytecodes[pos + 1];
	}

	public static int get_s2(int[] bytecodes, int pos) {
		return (short) (bytecodes[pos] << 8) | bytecodes[pos + 1];
	}

	public static int get_s4(int[] bytecodes, int pos) {
		return (bytecodes[pos] << 24) | (bytecodes[pos + 1] << 16) | (bytecodes[pos + 2] << 8) | bytecodes[pos + 3];
	}

	public static int get_u4_paddedPos(int pos) {
		int rem = pos % 4;
		if (rem == 0) {
			return pos;
		} else if (rem == 1) {
			return pos + 3;
		} else if (rem == 2) {
			return pos + 2;
		} else { // (if rem == 3) {
			return pos + 1;
		}
	}

	public static boolean endsWithCompatibleTypes(String desc, String descTail) {
		String desc1 = toType12Desc(desc);
		String desc2 = toType12Desc(descTail);
		return desc1.endsWith(desc2);
	}

	public static boolean hasCompatibleTypes(String desc1, String desc2) {
		if (desc1.length() != desc2.length()) {
			return false;
		}
		return endsWithCompatibleTypes(desc1, desc2);
	}

	private static String toType12Desc(String desc) {
		StringBuffer buffer = new StringBuffer(desc.length());
		for (int i = 0; i < desc.length(); i++) {
			char charDesc = desc.charAt(i);
			buffer.append(isType2Desc(charDesc) ? "2" : "1");
		}
		return buffer.toString();
	}

	public static int toStackSize(String desc) {
		int stackSize = 0;
		for (int i = 0; i < desc.length(); i++) {
			char charDesc = desc.charAt(i);
			stackSize += isType2Desc(charDesc) ? 2 : 1;
		}
		return stackSize;
	}

	private static boolean isType2Desc(char charDesc) {
		return (charDesc == 'J') || (charDesc == 'D') || (charDesc == '2');
	}
}