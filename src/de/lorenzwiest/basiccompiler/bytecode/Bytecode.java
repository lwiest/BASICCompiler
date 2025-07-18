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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.lorenzwiest.basiccompiler.classfile.ConstantPool;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.ConstantPoolInfo;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_Double;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_FieldRef;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_Float;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_Integer;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_Long;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_MethodRef;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_NameAndType;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_Utf8;

/*
 Pop and push argument descriptor ("desc") encoding:

 A Address (rarely used)
 D Double
 F Float
 I Integer
 J Long
 L (Object) reference
 [ (Array) reference
 1 Type 1 argument (neither Double nor Long)
 2 Type 2 argument (Double, Long)
 ! One argument
 ? Zero or one argument
 * Zero or more arguments
 */
public enum Bytecode {
	AALOAD         (0x32, 1, "[I",   "L"),
	AASTORE        (0x53, 1, "[IL",  ""),
	ACONST_NULL    (0x01, 1, "",     "L"),
	ALOAD          (0x19, 2, "",     "L"),
	ALOAD_0        (0x2A, 1, "",     "L"),
	ALOAD_1        (0x2B, 1, "",     "L"),
	ALOAD_2        (0x2C, 1, "",     "L"),
	ALOAD_3        (0x2D, 1, "",     "L"),
	ANEWARRAY      (0xBD, 3, "I",    "["),
	ARETURN        (0xB0, 1, "L",    ""),

	ARRAYLENGTH    (0xBE, 1, "[",    "I"),
	ASTORE         (0x3A, 2, "L",    ""),
	ASTORE_0       (0x4B, 1, "L",    ""),
	ASTORE_1       (0x4C, 1, "L",    ""),
	ASTORE_2       (0x4D, 1, "L",    ""),
	ASTORE_3       (0x4E, 1, "L",    ""),
	ATHROW         (0xBF, 1, "L",    "L"), // clears stack, pushes ref to exception then branches unconditionally
	BALOAD         (0x33, 1, "[I",   "I"),
	BASTORE        (0x54, 1, "[II",  ""),
	BIPUSH         (0x10, 2, "",     "I"),

	CALOAD         (0x34, 1, "[I",   "I"),
	CASTORE        (0x55, 1, "[II",  ""),
	CHECKCAST      (0xC0, 3, "L",    "L"),
	D2F            (0x90, 1, "D",    "F"),
	D2I            (0x8E, 1, "D",    "I"),
	D2L            (0x8F, 1, "D",    "J"),
	DADD           (0x63, 1, "DD",   "D"),
	DALOAD         (0x31, 1, "[I",   "D"),
	DASTORE        (0x52, 1, "[ID",  ""),
	DCMPG          (0x98, 1, "DD",   "I"),

	DCMPL          (0x97, 1, "DD",   "I"),
	DCONST_0       (0x0E, 1, "",     "D"),
	DCONST_1       (0x0F, 1, "",     "D"),
	DDIV           (0x6F, 1, "DD",   "D"),
	DLOAD          (0x18, 2, "",     "D"),
	DLOAD_0        (0x26, 1, "",     "D"),
	DLOAD_1        (0x27, 1, "",     "D"),
	DLOAD_2        (0x28, 1, "",     "D"),
	DLOAD_3        (0x29, 1, "",     "D"),
	DMUL           (0x6B, 1, "DD",   "D"),

	DNEG           (0x77, 1, "D",    "D"),
	DREM           (0x73, 1, "DD",   "D"),
	DRETURN        (0xAF, 1, "D",    ""),
	DSTORE         (0x39, 2, "D",    ""),
	DSTORE_0       (0x47, 1, "D",    ""),
	DSTORE_1       (0x48, 1, "D",    ""),
	DSTORE_2       (0x49, 1, "D",    ""),
	DSTORE_3       (0x4A, 1, "D",    ""),
	DSUB           (0x67, 1, "DD",   "D"),
	DUP            (0x59, 1, "1",    "11"),

	DUP_X1         (0x5A, 1, "1",    "111"),    //              v1          -> v1 v1 v1
	DUP_X2         (0x5B, 1, "111",  "1111"),   // regular,     v3 v2 v1    -> v1 v3 v2 v1
	DUP_X2_ALT     (0x5B, 1, "21",   "121"),    // alternative, v2 v1       -> v1 v2 v1
	DUP2           (0x5C, 1, "11",   "1111"),   // regular,     v2 v1       -> v2 v1 v2 v1
	DUP2_ALT       (0x5C, 1, "2",    "22"),     // alternative, v1          -> v1 v1
	DUP2_X1        (0x5D, 1, "111",  "11111"),  // regular,     v3 v2 v1    -> v2 v1 v3 v2 v1
	DUP2_X1_ALT    (0x5D, 1, "12",   "212"),    // alternative, v2 v1       -> v1 v2 v1
	DUP2_X2        (0x5E, 1, "1111", "111111"), // regular,     v4 v3 v2 v1 -> v2 v1 v4 v3 v2 v1
	DUP2_X2_ALT1   (0x5E, 1, "112",  "2112"),   // alternative, v3 v2 v1    -> v1 v3 v2 v1
	DUP2_X2_ALT2   (0x5E, 1, "211",  "11211"),  // alternative, v3 v2 v1    -> v2 v1 v3 v2 v1

	DUP2_X2_ALT3   (0x5E, 1, "22",   "222"),    // alternative, v2 v1       -> v1 v2 v1
	F2D            (0x8D, 1, "F",    "D"),
	F2I            (0x8B, 1, "F",    "I"),
	F2L            (0x8C, 1, "F",    "J"),
	FADD           (0x62, 1, "FF",   "F"),
	FALOAD         (0x30, 1, "[I",   "F"),
	FASTORE        (0x51, 1, "[IF",  ""),
	FCMPG          (0x96, 1, "FF",   "I"),
	FCMPL          (0x95, 1, "FF",   "I"),
	FCONST_0       (0x0B, 1, "",     "F"),

	FCONST_1       (0x0C, 1, "",     "F"),
	FCONST_2       (0x0D, 1, "",     "F"),
	FDIV           (0x6E, 1, "FF",   "F"),
	FLOAD          (0x17, 2, "",     "F"),
	FLOAD_0        (0x22, 1, "",     "F"),
	FLOAD_1        (0x23, 1, "",     "F"),
	FLOAD_2        (0x24, 1, "",     "F"),
	FLOAD_3        (0x25, 1, "",     "F"),
	FMUL           (0x6A, 1, "FF",   "F"),
	FNEG           (0x76, 1, "F",    "F"),

	FREM           (0x72, 1, "FF",   "F"),
	FRETURN        (0xAE, 1, "F",    ""),
	FSTORE         (0x38, 2, "F",    ""),
	FSTORE_0       (0x43, 1, "F",    ""),
	FSTORE_1       (0x44, 1, "F",    ""),
	FSTORE_2       (0x45, 1, "F",    ""),
	FSTORE_3       (0x46, 1, "F",    ""),
	FSUB           (0x66, 1, "FF",   "F"),
	GETFIELD       (0xB4, 3, "L",    "!"),
	GETSTATIC      (0xB2, 3, "",     "!"),

	GOTO           (0xA7, 3, "",     ""),
	GOTO_W         (0xC8, 5, "",     ""),
	I2B            (0x91, 1, "I",    "I"),
	I2C            (0x92, 1, "I",    "I"),
	I2D            (0x87, 1, "I",    "D"),
	I2F            (0x86, 1, "I",    "F"),
	I2L            (0x85, 1, "I",    "J"),
	I2S            (0x93, 1, "I",    "I"),
	IADD           (0x60, 1, "II",   "I"),
	IALOAD         (0x2E, 1, "[I",   "I"),

	IAND           (0x7E, 1, "II",   "I"),
	IASTORE        (0x4F, 1, "[II",  ""),
	ICONST_M1      (0x02, 1, "",     "I"),
	ICONST_0       (0x03, 1, "",     "I"),
	ICONST_1       (0x04, 1, "",     "I"),
	ICONST_2       (0x05, 1, "",     "I"),
	ICONST_3       (0x06, 1, "",     "I"),
	ICONST_4       (0x07, 1, "",     "I"),
	ICONST_5       (0x08, 1, "",     "I"),
	IDIV           (0x6C, 1, "II",   "I"),

	IF_ACMPEQ      (0xA5, 3, "LL",   ""),
	IF_ACMPNE      (0xA6, 3, "LL",   ""),
	IF_ICMPEQ      (0x9F, 3, "II",   ""),
	IF_ICMPNE      (0xA0, 3, "II",   ""),
	IF_ICMPLT      (0xA1, 3, "II",   ""),
	IF_ICMPGE      (0xA2, 3, "II",   ""),
	IF_ICMPGT      (0xA3, 3, "II",   ""),
	IF_ICMPLE      (0xA4, 3, "II",   ""),
	IFEQ           (0x99, 3, "I",    ""),
	IFNE           (0x9A, 3, "I",    ""),

	IFLT           (0x9B, 3, "I",    ""),
	IFGE           (0x9C, 3, "I",    ""),
	IFGT           (0x9D, 3, "I",    ""),
	IFLE           (0x9E, 3, "I",    ""),
	IFNONULL       (0xC7, 3, "L",    ""),
	IFNULL         (0xC6, 3, "L",    ""),
	IINC           (0x84, 3, "",     ""),
	ILOAD          (0x15, 2, "",     "I"),
	ILOAD_0        (0x1A, 1, "",     "I"),
	ILOAD_1        (0x1B, 1, "",     "I"),

	ILOAD_2        (0x1C, 1, "",     "I"),
	ILOAD_3        (0x1D, 1, "",     "I"),
	IMUL           (0x68, 1, "II",   "I"),
	INEG           (0x74, 1, "I",    "I"),
	INSTANCEOF     (0xC1, 3, "L",    "I"),
	INVOKEDYNAMIC  (0xBA, 5, "*",    "?"),
	INVOKEINTERFACE(0xB9, 5, "L*",   "?"),
	INVOKESPECIAL  (0xB7, 3, "L*",   "?"),
	INVOKESTATIC   (0xB8, 3, "*",    "?"),
	INVOKEVIRTUAL  (0xB6, 3, "L*",   "?"),

	IOR            (0x80, 1, "II",   "I"),
	IREM           (0x70, 1, "II",   "I"),
	IRETURN        (0xAC, 1, "I",    ""),
	ISHL           (0x78, 1, "II",   "I"),
	ISHR           (0x7A, 1, "II",   "I"),
	ISTORE         (0x36, 2, "I",    ""),
	ISTORE_0       (0x3B, 1, "I",    ""),
	ISTORE_1       (0x3C, 1, "I",    ""),
	ISTORE_2       (0x3D, 1, "I",    ""),
	ISTORE_3       (0x3E, 1, "I",    ""),

	ISUB           (0x64, 1, "II",   "I"),
	IUSHR          (0x7C, 1, "II",   "I"),
	IXOR           (0x82, 1, "II",   "I"),
	JSR            (0xA8, 3, "",     "A"),
	JSR_W          (0xC9, 5, "",     "A"),
	L2D            (0x8A, 1, "J",    "D"),
	L2F            (0x89, 1, "J",    "F"),
	L2I            (0x88, 1, "J",    "I"),
	LADD           (0x61, 1, "JJ",   "J"),
	LALOAD         (0x2F, 1, "[I",   "J"),

	LAND           (0x7F, 1, "JJ",   "J"),
	LASTORE        (0x50, 1, "[IJ",  ""),
	LCMP           (0x94, 1, "JJ",   "I"),
	LCONST_0       (0x09, 1, "",     "J"),
	LCONST_1       (0x0A, 1, "",     "J"),
	LDC            (0x12, 2, "",     "1"),
	LDC_W          (0x13, 3, "",     "1"),
	LDC2_W         (0x14, 3, "",     "2"),
	LDIV           (0x6D, 1, "JJ",   "J"),
	LLOAD          (0x16, 2, "",     "J"),

	LLOAD_0        (0x1E, 1, "",     "J"),
	LLOAD_1        (0x1F, 1, "",     "J"),
	LLOAD_2        (0x20, 1, "",     "J"),
	LLOAD_3        (0x21, 1, "",     "J"),
	LMUL           (0x69, 1, "JJ",   "J"),
	LNEG           (0x75, 1, "J",    "J"),
	LOOKUPSWITCH   (0xAB,-1, "I",    ""),  // variable length
	LOR            (0x81, 1, "JJ",   "J"),
	LREM           (0x71, 1, "JJ",   "J"),
	LRETURN        (0xAD, 1, "J",    ""),

	LSHL           (0x79, 1, "JI",   "J"),
	LSHR           (0x7B, 1, "JI",   "J"),
	LSTORE         (0x37, 2, "J",    ""),
	LSTORE_0       (0x3F, 1, "J",    ""),
	LSTORE_1       (0x40, 1, "J",    ""),
	LSTORE_2       (0x41, 1, "J",    ""),
	LSTORE_3       (0x42, 1, "J",    ""),
	LSUB           (0x65, 1, "JJ",   "J"),
	LUSHR          (0x7D, 1, "JI",   "J"),
	LXOR           (0x83, 1, "JJ",   "J"),

	MONITORENTER   (0xC2, 1, "L",    ""),
	MONITOREXIT    (0xC3, 1, "L",    ""),
	MULTINEWARRAY  (0xC5, 4, "*",    "["),
	NEW            (0xBB, 3, "",     "L"),
	NEWARRAY       (0xBC, 2, "I",    "["),
	NOP            (0x00, 1, "",     ""),
	POP            (0x57, 1, "1",    ""),
	POP2           (0x58, 1, "11",   ""),  // regular
	POP2_ALT       (0x58, 1, "2",    ""),  // alternative
	PUTFIELD       (0xB5, 3, "L!",   ""),

	PUTSTATIC      (0xB3, 3, "!",    ""),
	RET            (0xA9, 2, "",     ""),
	RETURN         (0xB1, 1, "",     ""),
	SALOAD         (0x35, 1, "[I",   "I"),
	SASTORE        (0x56, 1, "[II",  ""),
	SIPUSH         (0x11, 3, "",     "I"),
	SWAP           (0x5F, 1, "11",   "11"),
	TABLESWITCH    (0xAA,-1, "I",    ""),   // variable length
	WIDE           (0xC4, 4, "*",    "*"),  // regular
	WIDE_ALT       (0xC4, 6, "",     "");   // alternative

	private static final String DESC_DOUBLE  = "D";
	private static final String DESC_FLOAT   = "F";
	private static final String DESC_INTEGER = "I";
	private static final String DESC_LONG    = "J";
	private static final String DESC_OBJ_REF = "L";

	private int bytecode;
	private int length;
	private String popDesc;
	private String pushDesc;

	Bytecode(int bytecode, int length, String popDesc, String pushDesc) {
		this.bytecode = bytecode;
		this.length = length;
		this.popDesc = popDesc;
		this.pushDesc = pushDesc;
	}

	public String getName() {
		return this.name();
	}

	public int getBytecode() {
		return this.bytecode;
	}

	private static Map<Integer, Bytecode> BYTECODE_MAP = new HashMap<Integer, Bytecode>();

	static {
		for (Bytecode oBytecode : Bytecode.values()) {
			int bytecode = oBytecode.bytecode;
			if (BYTECODE_MAP.containsKey(bytecode) == false) {  // skip alternative bytecodes
				BYTECODE_MAP.put(bytecode,  oBytecode);
			}
		}
	}

	private static Bytecode get(int bytecode) {
		return BYTECODE_MAP.get(bytecode);
	}

	public static Bytecode get(int bytecode, int pos, String opStack) {
		Bytecode oBytecode = get(bytecode);
		if (oBytecode == DUP_X2) {
			if (matchesOperandsOnStack(DUP_X2, opStack)) {
				return DUP_X2;
			} else if (matchesOperandsOnStack(DUP_X2_ALT, opStack)) {
				return DUP_X2_ALT;
			} 
			throw new BytecodeException(createExceptionMessage(oBytecode, pos));
		} else if (oBytecode == DUP2) {
			if (matchesOperandsOnStack(DUP2, opStack)) {
				return DUP2;
			} else if (matchesOperandsOnStack(DUP2_ALT, opStack)) {
				return DUP2_ALT;
			}
			throw new BytecodeException(createExceptionMessage(oBytecode, pos));
		} else if (oBytecode == DUP2_X1) {
			if (matchesOperandsOnStack(DUP2_X1, opStack))  {
				return DUP2_X1;
			} else if (matchesOperandsOnStack(DUP2_X1_ALT, opStack)) {
				return DUP2_X1_ALT;
			}
			throw new BytecodeException(createExceptionMessage(oBytecode, pos));
		} else if (oBytecode == DUP2_X2) {
			if (matchesOperandsOnStack(DUP2_X2, opStack)) {
				return DUP2_X2;
			} else if (matchesOperandsOnStack(DUP2_X2_ALT1, opStack)) {
				return DUP2_X2_ALT1;
			} else if (matchesOperandsOnStack(DUP2_X2_ALT2, opStack)) {
				return DUP2_X2_ALT2;
			} else if (matchesOperandsOnStack(DUP2_X2_ALT3, opStack)) {
				return DUP2_X2_ALT3;
			}
			throw new BytecodeException(createExceptionMessage(oBytecode, pos));
		} else if (oBytecode == POP2) {
			if (matchesOperandsOnStack(POP2, opStack)) {
				return POP2;
			} else if (matchesOperandsOnStack(POP2_ALT, opStack)) {
				return POP2_ALT;
			}
			throw new BytecodeException(createExceptionMessage(oBytecode, pos));
		}
		return oBytecode;
	}

	private static String createExceptionMessage(Bytecode oBytecode, int pos) {
		return String.format("Bytecode %s at position %d: Can't determine which stack operands to use.", oBytecode.getName(), pos);
	}

	public static Bytecode getNestedBytecode(int[] bytecodes, int posWideBytecode) {
		return get(bytecodes[posWideBytecode + 1]);
	}

	private static boolean matchesOperandsOnStack(Bytecode oBytecode, String opStack) {
		String popDesc = oBytecode.popDesc;
		if (opStack.length() < popDesc.length()) {
			return false;
		}
		return BytecodeUtils.endsWithCompatibleTypes(opStack, popDesc);
	}

	public int getLength(int[] bytecodes, int pos) {
		int length;
		Bytecode oBytecode = get(bytecodes[pos]);
		if (oBytecode == Bytecode.LOOKUPSWITCH) {
			int newPos = BytecodeUtils.get_u4_paddedPos(pos + 1);
			newPos += 4; // skip <default>
			int npairs = BytecodeUtils.get_s4(bytecodes, newPos);
			newPos += 4; // skip <npairs>
			newPos += npairs * (4 + 4);
			length = newPos - pos;
		} else if (oBytecode == Bytecode.TABLESWITCH) {
			int newPos = BytecodeUtils.get_u4_paddedPos(pos + 1);
			newPos += 4; // skip <default>
			int low = BytecodeUtils.get_s4(bytecodes, newPos);
			newPos += 4;  // skip <low>
			int high = BytecodeUtils.get_s4(bytecodes, newPos);
			newPos += 4; // skip <high>
			newPos += ((high - low) + 1) * 4;
			length = newPos - pos;
		} else if (oBytecode == Bytecode.WIDE) {
			Bytecode oNestedBytecode = get(bytecodes[pos + 1]);
			oBytecode = (oNestedBytecode == Bytecode.IINC) ? Bytecode.WIDE_ALT : Bytecode.WIDE;
			length = oBytecode.length;
		} else {
			length = this.length;
		}
		return length;
	}

	public String getPopDesc(int[] bytecodes, int pos, ConstantPool constantPool) {
		String popDesc;
		if (this == Bytecode.INVOKEDYNAMIC) {
			throw new BytecodeException("Determining the argument types of bytecode INVOKEDYNAMIC at position " + pos + " is not supported.");
		} else if (this == Bytecode.INVOKEINTERFACE) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			popDesc = DESC_OBJ_REF + getMethodArgsDesc(constantPool, index);
		} else if (this == Bytecode.INVOKESPECIAL) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			popDesc = DESC_OBJ_REF + getMethodArgsDesc(constantPool, index);
		} else if (this == Bytecode.INVOKESTATIC) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			popDesc = getMethodArgsDesc(constantPool, index);
		} else if (this == Bytecode.INVOKEVIRTUAL) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			popDesc = DESC_OBJ_REF + getMethodArgsDesc(constantPool, index);
		} else if (this == MULTINEWARRAY) {
			int dimensions = BytecodeUtils.get_u1(bytecodes, pos + 3);
			char[] buffer = new char[dimensions];
			Arrays.fill(buffer, DESC_INTEGER.charAt(0));
			popDesc = String.copyValueOf(buffer);
		} else if (this == PUTFIELD) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			popDesc = DESC_OBJ_REF + getFieldDesc(constantPool, index);
		} else if (this == PUTSTATIC) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			popDesc = getFieldDesc(constantPool, index);
		} else if ((this == Bytecode.WIDE) || (this == Bytecode.WIDE_ALT)) {
			Bytecode oNestedBytecode = get(bytecodes[pos + 1]);
			popDesc = oNestedBytecode.popDesc;
		} else {
			popDesc = this.popDesc;
		}
		return popDesc;
	}

	public String getPushDesc(int[] bytecodes, int pos, ConstantPool constantPool) {
		String pushDesc;
		if (this == GETFIELD) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getFieldDesc(constantPool, index);
		} else if (this == GETSTATIC) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getFieldDesc(constantPool, index);
		} else if (this == Bytecode.INVOKEDYNAMIC) {
			throw new BytecodeException("Determining the return type of bytecode INVOKEDYNAMIC at position " + pos + " is not supported.");
		} else if (this == Bytecode.INVOKEINTERFACE) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getMethodReturnDesc(constantPool, index);
		} else if (this == Bytecode.INVOKESPECIAL) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getMethodReturnDesc(constantPool, index);
		} else if (this == Bytecode.INVOKESTATIC) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getMethodReturnDesc(constantPool, index);
		} else if (this == Bytecode.INVOKEVIRTUAL) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getMethodReturnDesc(constantPool, index);
		} else if (this == LDC) {
			int index = BytecodeUtils.get_u1(bytecodes, pos + 1);
			pushDesc = getConstantType1Desc(constantPool, index);
		} else if (this == LDC_W) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getConstantType1Desc(constantPool, index);
		} else if (this == LDC2_W) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			pushDesc = getConstantType2Desc(constantPool, index);
		} else if ((this == Bytecode.WIDE) || (this == Bytecode.WIDE_ALT)) {
			Bytecode oNestedBytecode = get(bytecodes[pos + 1]);
			pushDesc = oNestedBytecode.popDesc;
		} else  {
			pushDesc = this.pushDesc;
		}
		return pushDesc;
	}

	private static String getFieldDesc(ConstantPool constantPool, int index) {
		ConstantPoolInfo_FieldRef fieldRef = (ConstantPoolInfo_FieldRef) constantPool.get(index);
		int nameAndTypeIndex = fieldRef.getNameAndTypeIndex();
		ConstantPoolInfo_NameAndType nameAndTypeRef = (ConstantPoolInfo_NameAndType) constantPool.get(nameAndTypeIndex);
		int descriptorIndex = nameAndTypeRef.getDescriptorIndex();
		ConstantPoolInfo_Utf8 utf8 = (ConstantPoolInfo_Utf8) constantPool.get(descriptorIndex);
		String descriptor = utf8.getString();

		return normalizeDesc(descriptor);
	}

	private static String getMethodReturnDesc(ConstantPool constantPool, int index) {
		ConstantPoolInfo_MethodRef methodRef = (ConstantPoolInfo_MethodRef) constantPool.get(index);
		int nameAndTypeIndex = methodRef.getNameAndTypeIndex();
		ConstantPoolInfo_NameAndType nameAndTypeRef = (ConstantPoolInfo_NameAndType) constantPool.get(nameAndTypeIndex);
		int descriptorIndex = nameAndTypeRef.getDescriptorIndex();
		ConstantPoolInfo_Utf8 utf8 = (ConstantPoolInfo_Utf8) constantPool.get(descriptorIndex);
		String descriptor = utf8.getString();

		int endParenPos = descriptor.indexOf(")");
		descriptor = descriptor.substring(endParenPos + 1);
		return normalizeDesc(descriptor);
	}

	private static String getMethodArgsDesc(ConstantPool constantPool, int index) {
		ConstantPoolInfo_MethodRef methodRef = (ConstantPoolInfo_MethodRef) constantPool.get(index);
		int nameAndTypeIndex = methodRef.getNameAndTypeIndex();
		ConstantPoolInfo_NameAndType nameAndTypeRef = (ConstantPoolInfo_NameAndType) constantPool.get(nameAndTypeIndex);
		int descriptorIndex = nameAndTypeRef.getDescriptorIndex();
		ConstantPoolInfo_Utf8 utf8 = (ConstantPoolInfo_Utf8) constantPool.get(descriptorIndex);
		String descriptor = utf8.getString();

		int startParenPos = descriptor.indexOf("(");
		int endParenPos = descriptor.indexOf(")");
		descriptor = descriptor.substring(startParenPos + 1, endParenPos);
		return normalizeDesc(descriptor);
	}

	private static String normalizeDesc(String descriptor) {
		String normalizedDesc = descriptor.replaceAll("L[^;]*;", "L");
		normalizedDesc = normalizedDesc.replaceAll("\\[+", "\\[");
		normalizedDesc = normalizedDesc.replaceAll("\\[[^\\[]", "\\[");
		normalizedDesc = normalizedDesc.replaceAll("V", "");
		return normalizedDesc;
	}

	private static String getConstantType1Desc(ConstantPool constantPool, int index) {
		String desc;
		ConstantPoolInfo constantPoolInfo = constantPool.get(index);
		if (constantPoolInfo instanceof ConstantPoolInfo_Integer) {
			desc = DESC_INTEGER;
		} else if (constantPoolInfo instanceof ConstantPoolInfo_Float) {
			desc = DESC_FLOAT;
		} else {
			desc = DESC_OBJ_REF;
		}
		return desc;
	}

	private static String getConstantType2Desc(ConstantPool constantPool, int index) {
		String desc = null;
		ConstantPoolInfo constantPoolInfo = constantPool.get(index);
		if (constantPoolInfo instanceof ConstantPoolInfo_Long) {
			desc = DESC_LONG;
		} else if (constantPoolInfo instanceof ConstantPoolInfo_Double) {
			desc = DESC_DOUBLE;
		}
		return desc;
	}
}