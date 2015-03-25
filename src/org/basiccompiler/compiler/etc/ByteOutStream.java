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

package org.basiccompiler.compiler.etc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ByteOutStream extends ByteArrayOutputStream {
	private final int maxLength;

	public ByteOutStream(int maxLength) {
		this.maxLength = maxLength;
	}

	public ByteOutStream() {
		this(Integer.MAX_VALUE);
	}

	public int pos() {
		return this.count;
	}

	public void write_u1(int value) {
		if (this.count >= this.maxLength) {
			throw new CompileException("Write after end of stream.");
		}
		write(value & 0xff);
	}

	public void write_u2(int value) {
		write_u1(value >> 8);
		write_u1(value);
	}

	public void write_u4(int value) {
		write_u1(value >> 24);
		write_u1(value >> 16);
		write_u1(value >> 8);
		write_u1(value);
	}

	private void patch_u1(int patchPos, int value) {
		this.buf[patchPos] = (byte) (value & 0xff);
	}

	public void patch_u2(int patchPos, int value) {
		patch_u1(patchPos, value >> 8);
		patch_u1(patchPos + 1, value);
	}

	public void patch_u4(int patchPos, int value) {
		patch_u1(patchPos, value >> 24);
		patch_u1(patchPos + 1, value >> 16);
		patch_u1(patchPos + 2, value >> 8);
		patch_u1(patchPos + 3, value);
	}

	public void pad4ByteBoundary() {
		while ((this.pos() % 4) != 0) {
			write_u1(0x00);
		}
	}

	public void aaload() {
		write_u1(0x32);
	}

	public void aastore() {
		write_u1(0x53);
	}

	public void aload_0() {
		write_u1(0x2a);
	}

	public void aload_1() {
		write_u1(0x2b);
	}

	public void aload_2() {
		write_u1(0x2c);
	}

	public void aload_3() {
		write_u1(0x2d);
	}

	public void aload(int local_index) {
		write_u1(0x19);
		write_u1(local_index);
	}

	public void aload_opt(int local_index) {
		if (local_index == 0) {
			aload_0();
		} else if (local_index == 1) {
			aload_1();
		} else if (local_index == 2) {
			aload_2();
		} else if (local_index == 3) {
			aload_3();
		} else {
			aload(local_index);
		}
	}

	public void areturn() {
		write_u1(0xb0);
	}

	public void arraylength() {
		write_u1(0xbe);
	}

	public void astore_0() {
		write_u1(0x4b);
	}

	public void astore_1() {
		write_u1(0x4c);
	}

	public void astore_2() {
		write_u1(0x4d);
	}

	public void astore_3() {
		write_u1(0x4e);
	}

	public void astore(int local_index) {
		write_u1(0x3a);
		write_u1(local_index);
	}

	public void astore_opt(int local_index) {
		if (local_index == 0) {
			astore_0();
		} else if (local_index == 1) {
			astore_1();
		} else if (local_index == 2) {
			astore_2();
		} else if (local_index == 3) {
			astore_3();
		} else {
			astore(local_index);
		}
	}

	public void athrow() {
		write_u1(0xbf);
	}

	public void baload() {
		write_u1(0x33);
	}

	public void bipush(int value) {
		write_u1(0x10);
		write_u1(value);
	}

	public void caload() {
		write_u1(0x34);
	}

	public void castore() {
		write_u1(0x55);
	}

	public void d2f() {
		write_u1(0x90);
	}

	public void d2i() {
		write_u1(0x8e);
	}

	public void dup() {
		write_u1(0x59);
	}

	public void f2d() {
		write_u1(0x8d);
	}

	public void f2i() {
		write_u1(0x8b);
	}

	public void fadd() {
		write_u1(0x62);
	}

	public void faload() {
		write_u1(0x30);
	}

	public void fastore() {
		write_u1(0x51);
	}

	public void fcmpg() {
		write_u1(0x96);
	}

	public void fconst_0() {
		write_u1(0x0b);
	}

	public void fconst_1() {
		write_u1(0x0c);
	}

	public void fconst_2() {
		write_u1(0x0d);
	}

	public void fdiv() {
		write_u1(0x6e);
	}

	public void fload_0() {
		write_u1(0x22);
	}

	public void fload_1() {
		write_u1(0x23);
	}

	public void fload_2() {
		write_u1(0x24);
	}

	public void fload_3() {
		write_u1(0x25);
	}

	public void fload_opt(int local_index) {
		if (local_index == 0) {
			fload_0();
		} else if (local_index == 1) {
			fload_1();
		} else if (local_index == 2) {
			fload_2();
		} else if (local_index == 3) {
			fload_3();
		} else {
			fload(local_index);
		}
	}

	public void fload(int local_index) {
		write_u1(0x17);
		write_u1(local_index);
	}

	public void fmul() {
		write_u1(0x6a);
	}

	public void fneg() {
		write_u1(0x76);
	}

	public void freturn() {
		write_u1(0xae);
	}

	public void fstore_0() {
		write_u1(0x43);
	}

	public void fstore_1() {
		write_u1(0x44);
	}

	public void fstore_2() {
		write_u1(0x45);
	}

	public void fstore_3() {
		write_u1(0x46);
	}

	public void fstore(int local_index) {
		write_u1(0x38);
		write_u1(local_index);
	}

	public void fstore_opt(int local_index) {
		if (local_index == 0) {
			fstore_0();
		} else if (local_index == 1) {
			fstore_1();
		} else if (local_index == 2) {
			fstore_2();
		} else if (local_index == 3) {
			fstore_3();
		} else {
			fstore(local_index);
		}
	}

	public void fsub() {
		write_u1(0x66);
	}

	public void getstatic(int cp_index) {
		write_u1(0xb2);
		write_u2(cp_index);
	}

	public void goto_() {
		write_u1(0xa7);
	}

	public void goto_(String label) {
		write_u1(0xa7);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void i2c() {
		write_u1(0x92);
	}

	public void i2d() {
		write_u1(0x87);
	}

	public void i2f() {
		write_u1(0x86);
	}

	public void iadd() {
		write_u1(0x60);
	}

	public void iaload() {
		write_u1(0x2e);
	}

	public void iand() {
		write_u1(0x7e);
	}

	public void iastore() {
		write_u1(0x4f);
	}

	public void iconst(int value) {
		if (value == -1) {
			iconst_m1();
		} else if (value == 0) {
			iconst_0();
		} else if (value == 1) {
			iconst_1();
		} else if (value == 2) {
			iconst_2();
		} else if ((value >= -128) && (value < 128)) {
			bipush(value);
		} else if ((value >= -65536) && (value < 65536)) {
			sipush(value);
		} else {
			// TODO: Add support for iconst() with 32-bit integer value
			throw new CompileException("Using iconst() with a 32-bit integer is not supported yet.");
		}
	}

	public void iconst_m1() {
		write_u1(0x02);
	}

	public void iconst_0() {
		write_u1(0x03);
	}

	public void iconst_1() {
		write_u1(0x04);
	}

	public void iconst_2() {
		write_u1(0x05);
	}

	public void idiv() {
		write_u1(0x6c);
	}

	public void ifeq() {
		write_u1(0x99);
	}

	public void ifeq(String label) {
		write_u1(0x99);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifge() {
		write_u1(0x9c);
	}

	public void ifge(String label) {
		write_u1(0x9c);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifgt() {
		write_u1(0x9d);
	}

	public void ifgt(String label) {
		write_u1(0x9d);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifle() {
		write_u1(0x9e);
	}

	public void ifle(String label) {
		write_u1(0x9e);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void iflt() {
		write_u1(0x9b);
	}

	public void iflt(String label) {
		write_u1(0x9b);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifne() {
		write_u1(0x9a);
	}

	public void ifne(String label) {
		write_u1(0x9a);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifnonnull(String label) {
		write_u1(0xc7);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void iinc(int local_index, int increment) {
		write_u1(0x84);
		write_u1(local_index);
		write_u1(increment);
	}

	public void if_icmpeq(String label) {
		write_u1(0x9f);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmpge(String label) {
		write_u1(0xa2);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmpgt(String label) {
		write_u1(0xa3);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmple(String label) {
		write_u1(0xa4);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmplt(String label) {
		write_u1(0xa1);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmpne(String label) {
		write_u1(0xa0);
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void iload_0() {
		write_u1(0x1a);
	}

	public void iload_1() {
		write_u1(0x1b);
	}

	public void iload_2() {
		write_u1(0x1c);
	}

	public void iload_3() {
		write_u1(0x1d);
	}

	public void iload(int local_index) {
		write_u1(0x15);
		write_u1(local_index);
	}

	public void iload_opt(int local_index) {
		if (local_index == 0) {
			iload_0();
		} else if (local_index == 1) {
			iload_1();
		} else if (local_index == 2) {
			iload_2();
		} else if (local_index == 3) {
			iload_3();
		} else {
			iload(local_index);
		}
	}

	public void imul() {
		write_u1(0x68);
	}

	public void ineg() {
		write_u1(0x74);
	}

	public void invokespecial(int cp_index) {
		write_u1(0xb7);
		write_u2(cp_index);
	}

	public void invokestatic(int cp_index) {
		write_u1(0xb8);
		write_u2(cp_index);
	}

	public void invokevirtual(int cp_index) {
		write_u1(0xb6);
		write_u2(cp_index);
	}

	public void ior() {
		write_u1(0x80);
	}

	public void irem() {
		write_u1(0x70);
	}

	public void ireturn() {
		write_u1(0xac);
	}

	public void ishl() {
		write_u1(0x78);
	}

	public void ishr() {
		write_u1(0x7a);
	}

	public void istore_0() {
		write_u1(0x3b);
	}

	public void istore_1() {
		write_u1(0x3c);
	}

	public void istore_2() {
		write_u1(0x3d);
	}

	public void istore_3() {
		write_u1(0x3e);
	}

	public void istore(int local_index) {
		write_u1(0x36);
		write_u1(local_index);
	}

	public void istore_opt(int local_index) {
		if (local_index == 0) {
			istore_0();
		} else if (local_index == 1) {
			istore_1();
		} else if (local_index == 2) {
			istore_2();
		} else if (local_index == 3) {
			istore_3();
		} else {
			istore(local_index);
		}
	}

	public void isub() {
		write_u1(0x64);
	}

	public void ixor() {
		write_u1(0x82);
	}

	public void ldc(int cp_index) {
		if (cp_index < 256) {
			write_u1(0x12);
			write_u1(cp_index);
		} else {
			write_u1(0x13);
			write_u2(cp_index);
		}
	}

	public void multianewarray(int arrayClassIndex, int numDims) {
		write_u1(0xc5);
		write_u2(arrayClassIndex);
		write_u1(numDims);
	}

	public void new_(int cp_index) {
		write_u1(0xbb);
		write_u2(cp_index);
	}

	public void newarray_byte() {
		write_u1(0xbc);
		write_u1(0x08);
	}

	public void newarray_char() {
		write_u1(0xbc);
		write_u1(0x05);
	}

	public void newarray_float() {
		write_u1(0xbc);
		write_u1(0x06);
	}

	public void newarray_int() {
		write_u1(0xbc);
		write_u1(0x0a);
	}

	public void pop() {
		write_u1(0x57);
	}

	public void putstatic(int cp_index) {
		write_u1(0xb3);
		write_u2(cp_index);
	}

	public void return_() {
		write_u1(0xb1);
	}

	public void sipush(int value) {
		write_u1(0x11);
		write_u2(value);
	}

	public void swap() {
		write_u1(0x5f);
	}

	public void tableswitch() {
		write_u1(0xaa);
	}

	//////////////////////////////////////////////////////////////////////////////

	private static int labelCounter = 0;

	private final Map<String /* label */, Integer /* label pos */> labelTable = new HashMap<String, Integer>();

	public static String generateLabel() {
		return "_label" + labelCounter++;
	}

	public void label(String label) {
		if (this.labelTable.containsKey(label)) {
			throw new CompileException("Label \"" + label + "\" already exists.");
		}
		this.labelTable.put(label, this.pos());
	}

	private final Map<Integer /* patchPos */, String /* toLabel */> patchHereMap = new HashMap<Integer, String>();

	private void patchHereToLabel(String toLabel) {
		this.patchHereMap.put(this.pos(), toLabel);
	}

	public void patchThereToLabel(int patchPos, String toLabel) {
		this.patchHereMap.put(patchPos, toLabel);
	}

	@Override
	public void flush() {
		patch();
		try {
			super.flush();
		} catch (IOException e) {
			// ignore
		}
	}

	private void patch() {
		Set<Entry<Integer, String>> patchHereEntrySet = this.patchHereMap.entrySet();
		for (Entry<Integer, String> patchHereEntry : patchHereEntrySet) {
			int fromPos = patchHereEntry.getKey();
			String toLabel = patchHereEntry.getValue();
			if (this.labelTable.containsKey(toLabel)) {
				int toPos = this.labelTable.get(toLabel);
				patch_u2(fromPos, (toPos - fromPos) + 1);
			} else {
				throw new CompileException("Cannot find label \"" + toLabel + "\" while patching.");
			}
		}
	}
}
