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

package de.lorenzwiest.basiccompiler.compiler.etc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.lorenzwiest.basiccompiler.bytecode.Bytecode;

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

	public void write_u8(long value) {
		write_u1((int) (value >> 56));
		write_u1((int) (value >> 48));
		write_u1((int) (value >> 40));
		write_u1((int) (value >> 32));
		write_u1((int) (value >> 24));
		write_u1((int) (value >> 16));
		write_u1((int) (value >> 8));
		write_u1((int) (value));
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
		write_u1(Bytecode.AALOAD.getBytecode());
	}

	public void aastore() {
		write_u1(Bytecode.AASTORE.getBytecode());
	}

	public void aload_0() {
		write_u1(Bytecode.ALOAD_0.getBytecode());
	}

	public void aload_1() {
		write_u1(Bytecode.ALOAD_1.getBytecode());
	}

	public void aload_2() {
		write_u1(Bytecode.ALOAD_2.getBytecode());
	}

	public void aload_3() {
		write_u1(Bytecode.ALOAD_3.getBytecode());
	}

	public void aload(int local_index) {
		write_u1(Bytecode.ALOAD.getBytecode());
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

	public void anewarray(int cp_index) {
		write_u1(Bytecode.ANEWARRAY.getBytecode());
		write_u2(cp_index);
	}

	public void areturn() {
		write_u1(Bytecode.ARETURN.getBytecode());
	}

	public void arraylength() {
		write_u1(Bytecode.ARRAYLENGTH.getBytecode());
	}

	public void astore_0() {
		write_u1(Bytecode.ASTORE_0.getBytecode());
	}

	public void astore_1() {
		write_u1(Bytecode.ASTORE_1.getBytecode());
	}

	public void astore_2() {
		write_u1(Bytecode.ASTORE_2.getBytecode());
	}

	public void astore_3() {
		write_u1(Bytecode.ASTORE_3.getBytecode());
	}

	public void astore(int local_index) {
		write_u1(Bytecode.ASTORE.getBytecode());
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
		write_u1(Bytecode.ATHROW.getBytecode());
	}

	public void baload() {
		write_u1(Bytecode.BALOAD.getBytecode());
	}

	public void bipush(int value) {
		write_u1(Bytecode.BIPUSH.getBytecode());
		write_u1(value);
	}

	public void caload() {
		write_u1(Bytecode.CALOAD.getBytecode());
	}

	public void castore() {
		write_u1(Bytecode.CASTORE.getBytecode());
	}

	public void d2f() {
		write_u1(Bytecode.D2F.getBytecode());
	}

	public void d2i() {
		write_u1(Bytecode.D2I.getBytecode());
	}

	public void dup() {
		write_u1(Bytecode.DUP.getBytecode());
	}

	public void f2d() {
		write_u1(Bytecode.F2D.getBytecode());
	}

	public void f2i() {
		write_u1(Bytecode.F2I.getBytecode());
	}

	public void fadd() {
		write_u1(Bytecode.FADD.getBytecode());
	}

	public void faload() {
		write_u1(Bytecode.FALOAD.getBytecode());
	}

	public void fastore() {
		write_u1(Bytecode.FASTORE.getBytecode());
	}

	public void fcmpg() {
		write_u1(Bytecode.FCMPG.getBytecode());
	}

	public void fconst_0() {
		write_u1(Bytecode.FCONST_0.getBytecode());
	}

	public void fconst_1() {
		write_u1(Bytecode.FCONST_1.getBytecode());
	}

	public void fconst_2() {
		write_u1(Bytecode.FCONST_2.getBytecode());
	}

	public void fdiv() {
		write_u1(Bytecode.FDIV.getBytecode());
	}

	public void fload_0() {
		write_u1(Bytecode.FLOAD_0.getBytecode());
	}

	public void fload_1() {
		write_u1(Bytecode.FLOAD_1.getBytecode());
	}

	public void fload_2() {
		write_u1(Bytecode.FLOAD_2.getBytecode());
	}

	public void fload_3() {
		write_u1(Bytecode.FLOAD_3.getBytecode());
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
		write_u1(Bytecode.FLOAD.getBytecode());
		write_u1(local_index);
	}

	public void fmul() {
		write_u1(Bytecode.FMUL.getBytecode());
	}

	public void fneg() {
		write_u1(Bytecode.FNEG.getBytecode());
	}

	public void freturn() {
		write_u1(Bytecode.FRETURN.getBytecode());
	}

	public void fstore_0() {
		write_u1(Bytecode.FSTORE_0.getBytecode());
	}

	public void fstore_1() {
		write_u1(Bytecode.FSTORE_1.getBytecode());
	}

	public void fstore_2() {
		write_u1(Bytecode.FSTORE_2.getBytecode());
	}

	public void fstore_3() {
		write_u1(Bytecode.FSTORE_3.getBytecode());
	}

	public void fstore(int local_index) {
		write_u1(Bytecode.FSTORE.getBytecode());
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
		write_u1(Bytecode.FSUB.getBytecode());
	}

	public void getstatic(int cp_index) {
		write_u1(Bytecode.GETSTATIC.getBytecode());
		write_u2(cp_index);
	}

	public void goto_() {
		write_u1(Bytecode.GOTO.getBytecode());
	}

	public void goto_(String label) {
		write_u1(Bytecode.GOTO.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void i2c() {
		write_u1(Bytecode.I2C.getBytecode());
	}

	public void i2d() {
		write_u1(Bytecode.I2D.getBytecode());
	}

	public void i2f() {
		write_u1(Bytecode.I2F.getBytecode());
	}

	public void iadd() {
		write_u1(Bytecode.IADD.getBytecode());
	}

	public void iaload() {
		write_u1(Bytecode.IALOAD.getBytecode());
	}

	public void iand() {
		write_u1(Bytecode.IAND.getBytecode());
	}

	public void iastore() {
		write_u1(Bytecode.IASTORE.getBytecode());
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
		write_u1(Bytecode.ICONST_M1.getBytecode());
	}

	public void iconst_0() {
		write_u1(Bytecode.ICONST_0.getBytecode());
	}

	public void iconst_1() {
		write_u1(Bytecode.ICONST_1.getBytecode());
	}

	public void iconst_2() {
		write_u1(Bytecode.ICONST_2.getBytecode());
	}

	public void idiv() {
		write_u1(Bytecode.IDIV.getBytecode());
	}

	public void ifeq() {
		write_u1(Bytecode.IFEQ.getBytecode());
	}

	public void ifeq(String label) {
		write_u1(Bytecode.IFEQ.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifge() {
		write_u1(Bytecode.IFGE.getBytecode());
	}

	public void ifge(String label) {
		write_u1(Bytecode.IFGE.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifgt() {
		write_u1(Bytecode.IFGT.getBytecode());
	}

	public void ifgt(String label) {
		write_u1(Bytecode.IFGT.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifle() {
		write_u1(Bytecode.IFLE.getBytecode());
	}

	public void ifle(String label) {
		write_u1(Bytecode.IFLE.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void iflt() {
		write_u1(Bytecode.IFLT.getBytecode());
	}

	public void iflt(String label) {
		write_u1(Bytecode.IFLT.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifne() {
		write_u1(Bytecode.IFNE.getBytecode());
	}

	public void ifne(String label) {
		write_u1(Bytecode.IFNE.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifnonnull(String label) {
		write_u1(Bytecode.IFNONULL.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void ifnull(String label) {
		write_u1(Bytecode.IFNULL.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void iinc(int local_index, int increment) {
		write_u1(Bytecode.IINC.getBytecode());
		write_u1(local_index);
		write_u1(increment);
	}

	public void if_icmpeq(String label) {
		write_u1(Bytecode.IF_ICMPEQ.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmpge(String label) {
		write_u1(Bytecode.IF_ICMPGE.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmpgt(String label) {
		write_u1(Bytecode.IF_ICMPGT.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmple(String label) {
		write_u1(Bytecode.IF_ICMPLE.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmplt(String label) {
		write_u1(Bytecode.IF_ICMPLT.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void if_icmpne(String label) {
		write_u1(Bytecode.IF_ICMPNE.getBytecode());
		patchHereToLabel(label);
		write_u2(0x00);
	}

	public void iload_0() {
		write_u1(Bytecode.ILOAD_0.getBytecode());
	}

	public void iload_1() {
		write_u1(Bytecode.ILOAD_1.getBytecode());
	}

	public void iload_2() {
		write_u1(Bytecode.ILOAD_2.getBytecode());
	}

	public void iload_3() {
		write_u1(Bytecode.ILOAD_3.getBytecode());
	}

	public void iload(int local_index) {
		write_u1(Bytecode.ILOAD.getBytecode());
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
		write_u1(Bytecode.IMUL.getBytecode());
	}

	public void ineg() {
		write_u1(Bytecode.INEG.getBytecode());
	}

	public void invokespecial(int cp_index) {
		write_u1(Bytecode.INVOKESPECIAL.getBytecode());
		write_u2(cp_index);
	}

	public void invokestatic(int cp_index) {
		write_u1(Bytecode.INVOKESTATIC.getBytecode());
		write_u2(cp_index);
	}

	public void invokevirtual(int cp_index) {
		write_u1(Bytecode.INVOKEVIRTUAL.getBytecode());
		write_u2(cp_index);
	}

	public void ior() {
		write_u1(Bytecode.IOR.getBytecode());
	}

	public void irem() {
		write_u1(Bytecode.IREM.getBytecode());
	}

	public void ireturn() {
		write_u1(Bytecode.IRETURN.getBytecode());
	}

	public void ishl() {
		write_u1(Bytecode.ISHL.getBytecode());
	}

	public void ishr() {
		write_u1(Bytecode.ISHR.getBytecode());
	}

	public void istore_0() {
		write_u1(Bytecode.ISTORE_0.getBytecode());
	}

	public void istore_1() {
		write_u1(Bytecode.ISTORE_1.getBytecode());
	}

	public void istore_2() {
		write_u1(Bytecode.ISTORE_2.getBytecode());
	}

	public void istore_3() {
		write_u1(Bytecode.ISTORE_3.getBytecode());
	}

	public void istore(int local_index) {
		write_u1(Bytecode.ISTORE.getBytecode());
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
		write_u1(Bytecode.ISUB.getBytecode());
	}

	public void ixor() {
		write_u1(Bytecode.IXOR.getBytecode());
	}

	public void ldc(int cp_index) {
		if (cp_index < 256) {
			write_u1(Bytecode.LDC.getBytecode());
			write_u1(cp_index);
		} else {
			write_u1(Bytecode.LDC_W.getBytecode());
			write_u2(cp_index);
		}
	}

	public void multianewarray(int arrayClassIndex, int numDims) {
		write_u1(Bytecode.MULTINEWARRAY.getBytecode());
		write_u2(arrayClassIndex);
		write_u1(numDims);
	}

	public void new_(int cp_index) {
		write_u1(Bytecode.NEW.getBytecode());
		write_u2(cp_index);
	}

	public void newarray_byte() {
		write_u1(Bytecode.NEWARRAY.getBytecode());
		write_u1(0x08);
	}

	public void newarray_char() {
		write_u1(Bytecode.NEWARRAY.getBytecode());
		write_u1(0x05);
	}

	public void newarray_float() {
		write_u1(Bytecode.NEWARRAY.getBytecode());
		write_u1(0x06);
	}

	public void newarray_int() {
		write_u1(Bytecode.NEWARRAY.getBytecode());
		write_u1(0x0a);
	}

	public void pop() {
		write_u1(Bytecode.POP.getBytecode());
	}

	public void putstatic(int cp_index) {
		write_u1(Bytecode.PUTSTATIC.getBytecode());
		write_u2(cp_index);
	}

	public void return_() {
		write_u1(Bytecode.RETURN.getBytecode());
	}

	public void sipush(int value) {
		write_u1(Bytecode.SIPUSH.getBytecode());
		write_u2(value);
	}

	public void swap() {
		write_u1(Bytecode.SWAP.getBytecode());
	}

	public void tableswitch() {
		write_u1(Bytecode.TABLESWITCH.getBytecode());
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

	public void closeGracefully() {
		try {
			this.close();
		} catch (IOException e) {
			// ignore
		}
	}

	public void flushAndCloseGracefully() {
		flush();
		closeGracefully();
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
