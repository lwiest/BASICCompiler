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

import de.lorenzwiest.basiccompiler.classfile.ConstantPool;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_MethodRef;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_NameAndType;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_Utf8;
import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;

public class BytecodeFlowAnalysis {
	private static final boolean SHOW_BYTECODE_FLOW_ANALYSIS = false;

	private static final char DESC_BOOLEAN = 'Z';
	private static final char DESC_BYTE    = 'B';
	private static final char DESC_CHAR    = 'C';
	private static final char DESC_DOUBLE  = 'D';
	private static final char DESC_FLOAT   = 'F';
	private static final char DESC_INTEGER = 'I';
	private static final char DESC_LONG    = 'J';
	private static final char DESC_OBJ_REF = 'L';
	private static final char DESC_SHORT   = 'S';
	private static final char DESC_VOID    = 'V';

	private final static String INDENT = "  ";

	public static int calculateStackSize(byte[] bytecodes, ConstantPool constantPool, ExceptionTableInfo[] exceptionTableInfos, int methodRefIndex) {
		if (SHOW_BYTECODE_FLOW_ANALYSIS) {
			logln(createTitle(constantPool, methodRefIndex));
			logln(INDENT + "Bytecode flow analysis:");
		}

		int[] iBytecodes = new int[bytecodes.length];
		for (int i = 0; i < iBytecodes.length; i++) {
			iBytecodes[i] = bytecodes[i] & 0xff;
		}

		String[] postOpStacks = new String[iBytecodes.length];
		traverse(iBytecodes, 0, postOpStacks, "", constantPool, "Start of method");
		traverseExceptionTable(iBytecodes, postOpStacks, constantPool, exceptionTableInfos);

		int maxStackSize = 0;
		for (String postOpStack : postOpStacks) {
			if (postOpStack != null) {
				int stackSize = BytecodeUtils.toStackSize(postOpStack);
				if (stackSize > maxStackSize) {
					maxStackSize = stackSize;
				}
			}
		}

		if (SHOW_BYTECODE_FLOW_ANALYSIS) {
			printResult(maxStackSize, iBytecodes, postOpStacks, constantPool);
		}
		return maxStackSize;
	}

	private static void traverse(int[] bytecodes, int pos, String[] postOpStacks, String currOpStack, ConstantPool constantPool, String title) {
		final String FMT_POS = INDENT + INDENT + "%" + ("" + bytecodes.length).length() + "d:";

		String strPos = String.format(FMT_POS, pos);
		logln(String.format("%s <- Enter (%s)", strPos, title));

		currOpStack = new String(currOpStack);

		while (pos < bytecodes.length) {
			Bytecode oBytecode = Bytecode.get(bytecodes[pos], pos, currOpStack);

			strPos = String.format(FMT_POS, pos);

			boolean wasVisited = postOpStacks[pos] != null;
			if (wasVisited) {
				logln(String.format("%s -> Exit (Already visited)", strPos));

				// stacks *before* bytecode execution must be the same when branching to an already visited bytecode position
				String preOpStack = calcPreOpStack(bytecodes, pos, currOpStack, postOpStacks, constantPool);
				if (BytecodeUtils.hasCompatibleTypes(preOpStack, currOpStack) == false) {
					throw new BytecodeException(String.format("Stack not balanced at position %d (present: \"%s\", entering \"%s\").", pos, preOpStack, currOpStack));
				}
				return;
			}

			logln(String.format("%s %s", strPos, formatBytecode(oBytecode, bytecodes, pos, constantPool)));

			int length = oBytecode.getLength(bytecodes, pos);
			String popDesc = oBytecode.getPopDesc(bytecodes, pos, constantPool);
			String pushDesc = oBytecode.getPushDesc(bytecodes, pos, constantPool);

			int stackSize = BytecodeUtils.toStackSize(currOpStack);
			int stackSizePopDesc = BytecodeUtils.toStackSize(popDesc);
			if (stackSize < stackSizePopDesc) {
				throw new BytecodeException(String.format("Stack underflow after position %d.", stackSize - stackSizePopDesc));
			} else {
				int newEndPos = currOpStack.length() - popDesc.length();
				if (BytecodeUtils.endsWithCompatibleTypes(currOpStack, popDesc)) {
					currOpStack = currOpStack.substring(0, newEndPos);
					if (isReturnBytecode(oBytecode)) {
						if (currOpStack.equals("") == false) {
							logln(String.format("%s WARNING: Stack not empty after bytecode (actual: \"%s\").", strPos, currOpStack));
						}
					}
				} else {
					String actualDesc = currOpStack.substring(newEndPos);
					throw new BytecodeException(String.format("Stack operand type(s) mismatch for bytecode %s at position %d (expected: \"%s\" actual: \"...%s\").", oBytecode.getName(), pos, popDesc, actualDesc));
				}
			}

			currOpStack += pushDesc;
			postOpStacks[pos] = currOpStack;

			if (hasConditional_s2_branchOffset(oBytecode)) {
				int branchPos = BytecodeUtils.get_s2(bytecodes, pos + 1);
				String branchTitle = String.format("From %d: %s", pos, oBytecode.getName());
				traverse(bytecodes, pos + branchPos, postOpStacks, currOpStack, constantPool, branchTitle);
				pos += length;
			} else if (hasConditional_s4_branchOffset(oBytecode)) {
				int branchPos = BytecodeUtils.get_s4(bytecodes, pos + 1);
				String branchTitle = String.format("From %d: %s", pos, oBytecode.getName());
				traverse(bytecodes, pos + branchPos, postOpStacks, currOpStack, constantPool, branchTitle);
				pos += length;
			} else if (hasUnconditional_s2_branchOffset(oBytecode)) {
				int branchPos = BytecodeUtils.get_s2(bytecodes, pos + 1);
				pos += branchPos;
			} else if (hasUnconditional_s4_branchOffset(oBytecode)) {
				int branchPos = BytecodeUtils.get_s4(bytecodes, pos + 1);
				pos += branchPos;
			} else if (oBytecode == Bytecode.LOOKUPSWITCH) {
				int newPos = BytecodeUtils.get_u4_paddedPos(pos + 1);
				int defaultPos = BytecodeUtils.get_s4(bytecodes, newPos);
				logln(String.format("%s %s default -> %d", strPos, oBytecode.getName(), pos + defaultPos));
				newPos += 4; // skip <default>
				String branchTitle = String.format("From %d %s, default branch", pos, oBytecode.getName());
				traverse(bytecodes, pos + defaultPos, postOpStacks, currOpStack, constantPool, branchTitle);
				int npairs = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4; // skip <npairs>
				for (int i = 0; i < npairs; i++) {
					int match = BytecodeUtils.get_s4(bytecodes, newPos);
					newPos += 4; // skip <match>
					int branchPos = BytecodeUtils.get_s4(bytecodes, newPos);
					logln(String.format("%s %s match %d -> %d", strPos, oBytecode.getName(), match, pos + branchPos));
					newPos += 4; // skip <branchPos>
					branchTitle = String.format("From %d: %s, matching %d", pos, oBytecode.getName(), match);
					traverse(bytecodes, pos + branchPos, postOpStacks, currOpStack, constantPool, branchTitle);
				}
				logln(String.format("%s -> Exit (End of bytecode LOOKUPSWITCH)", strPos, newPos - 1));
				return;
			} else if (oBytecode == Bytecode.TABLESWITCH) {
				int newPos = BytecodeUtils.get_u4_paddedPos(pos + 1);
				int defaultPos = BytecodeUtils.get_s4(bytecodes, newPos);
				logln(String.format("%s %s default -> %d", strPos, oBytecode.getName(), pos + defaultPos));
				newPos += 4; // skip <default>
				String branchTitle = String.format("From %d: %s, default branch", pos, oBytecode.getName());
				traverse(bytecodes, pos + defaultPos, postOpStacks, currOpStack, constantPool, branchTitle);
				int low = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4; // skip <low>
				int high = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4; // skip <high>
				int tabLength = (high - low) + 1;
				for (int i = 0; i < tabLength; i++) {
					int branchPos = BytecodeUtils.get_s4(bytecodes, newPos);
					logln(String.format("%s %s match %d -> %d", strPos, oBytecode.getName(), low + i, pos + branchPos));
					branchTitle = String.format("From %d: %s, matching %d", pos, oBytecode.getName(), low + i);
					traverse(bytecodes, pos + branchPos, postOpStacks, currOpStack, constantPool, branchTitle);
					newPos += 4; // skip <branch>
				}
				logln(String.format("%s -> Exit (End of bytecode TABLESWITCH)", strPos, newPos - 1));
				return;
			} else if (isReturnBytecode(oBytecode) || (oBytecode == Bytecode.ATHROW)) {
				logln(String.format("%s -> Exit (Bytecode %s)", strPos, oBytecode.getName()));
				return;
			} else if (oBytecode == Bytecode.WIDE) {
				Bytecode oNestedBytecode = Bytecode.getNestedByteCode(bytecodes, pos);
				if (oNestedBytecode == Bytecode.RET) {
					logln(String.format("%s -> Exit (Bytecode WIDE(RET))", strPos));
					return;
				}
				pos += length;
			} else {
				pos += length;
			}
		}
		logln(String.format("%s -> Exit (End of code reached)", strPos));
	}

	private static void traverseExceptionTable(int[] bytecodes, String[] postOpStacks, ConstantPool constantPool, ExceptionTableInfo[] exceptionTableInfos) {
		final String INITIAL_OP_STACK = Bytecode.ATHROW.getPopDesc(bytecodes, 0, constantPool);
		final int[] exceptionEntryPositions = getExceptionEntryPositions(exceptionTableInfos);
		for (int pos : exceptionEntryPositions) {
			traverse(bytecodes, pos, postOpStacks, INITIAL_OP_STACK, constantPool, "Exception handler code #" + 1);
		}
	}

	private static int[] getExceptionEntryPositions(ExceptionTableInfo[] exceptionTableInfos) {
		int[] exceptionEntryPositions = new int[exceptionTableInfos.length];
		for (int i = 0; i < exceptionTableInfos.length; i++) {
			exceptionEntryPositions[i] = exceptionTableInfos[i].getHandler_pc();
		}
		return exceptionEntryPositions;
	}

	private static String calcPreOpStack(int[] bytecodes, int pos, String currOpStack, String[] postOpStacks, ConstantPool constantPool) {
		String postOpStack = postOpStacks[pos];
		Bytecode oBytecode = Bytecode.get(bytecodes[pos], pos, currOpStack);
		String pushDesc = oBytecode.getPushDesc(bytecodes, pos, constantPool);
		String popDesc = oBytecode.getPopDesc(bytecodes, pos, constantPool);
		String preOpStack = postOpStack.substring(0,  postOpStack.length() - pushDesc.length()) + popDesc;
		return preOpStack;
	}

	private static boolean isReturnBytecode(Bytecode oBytecode) {
		return (oBytecode == Bytecode.ARETURN)
				|| (oBytecode == Bytecode.DRETURN)
				|| (oBytecode == Bytecode.FRETURN)
				|| (oBytecode == Bytecode.IRETURN)
				|| (oBytecode == Bytecode.LRETURN)
				|| (oBytecode == Bytecode.RETURN)
				|| (oBytecode == Bytecode.RET);
	}

	private static boolean hasConditional_s2_branchOffset(Bytecode oBytecode) {
		return (oBytecode == Bytecode.IF_ACMPEQ)
				|| (oBytecode == Bytecode.IF_ACMPNE)
				|| (oBytecode == Bytecode.IF_ICMPEQ)
				|| (oBytecode == Bytecode.IF_ICMPNE)
				|| (oBytecode == Bytecode.IF_ICMPLT)
				|| (oBytecode == Bytecode.IF_ICMPGE)
				|| (oBytecode == Bytecode.IF_ICMPGT)
				|| (oBytecode == Bytecode.IF_ICMPLE)
				|| (oBytecode == Bytecode.IFEQ)
				|| (oBytecode == Bytecode.IFNE)
				|| (oBytecode == Bytecode.IFLT)
				|| (oBytecode == Bytecode.IFGE)
				|| (oBytecode == Bytecode.IFGT)
				|| (oBytecode == Bytecode.IFLE)
				|| (oBytecode == Bytecode.IFNONULL)
				|| (oBytecode == Bytecode.IFNULL)
				|| (oBytecode == Bytecode.JSR);
	}

	private static boolean hasConditional_s4_branchOffset(Bytecode oBytecode) {
		return oBytecode == Bytecode.JSR_W;
	}

	private static boolean hasUnconditional_s2_branchOffset(Bytecode oBytecode) {
		return oBytecode == Bytecode.GOTO;
	}

	private static boolean hasUnconditional_s4_branchOffset(Bytecode oBytecode) {
		return oBytecode == Bytecode.GOTO_W;
	}

	private static boolean has_s2_branchOffset(Bytecode oBytecode) {
		return hasConditional_s2_branchOffset(oBytecode) || hasUnconditional_s2_branchOffset(oBytecode);
	}

	private static boolean has_s4_branchOffset(Bytecode oBytecode) {
		return hasConditional_s4_branchOffset(oBytecode) || hasUnconditional_s4_branchOffset(oBytecode);
	}

	private static boolean has_s1_index(Bytecode oBytecode) {
		return (oBytecode == Bytecode.ALOAD)
				|| (oBytecode == Bytecode.ASTORE)
				|| (oBytecode == Bytecode.DLOAD)
				|| (oBytecode == Bytecode.DSTORE)
				|| (oBytecode == Bytecode.FLOAD)
				|| (oBytecode == Bytecode.FSTORE)
				|| (oBytecode == Bytecode.ILOAD)
				|| (oBytecode == Bytecode.ISTORE)
				|| (oBytecode == Bytecode.LDC)
				|| (oBytecode == Bytecode.LLOAD)
				|| (oBytecode == Bytecode.LSTORE)
				|| (oBytecode == Bytecode.RET);
	}

	private static boolean has_u2_index(Bytecode oBytecode) {
		return (oBytecode == Bytecode.ANEWARRAY)
				|| (oBytecode == Bytecode.CHECKCAST)
				|| (oBytecode == Bytecode.GETFIELD)
				|| (oBytecode == Bytecode.GETSTATIC)
				|| (oBytecode == Bytecode.INSTANCEOF)
				|| (oBytecode == Bytecode.INVOKEDYNAMIC)
				|| (oBytecode == Bytecode.INVOKEINTERFACE)
				|| (oBytecode == Bytecode.INVOKESPECIAL)
				|| (oBytecode == Bytecode.INVOKESTATIC)
				|| (oBytecode == Bytecode.INVOKEVIRTUAL)
				|| (oBytecode == Bytecode.LDC_W)
				|| (oBytecode == Bytecode.LDC2_W)
				|| (oBytecode == Bytecode.NEW)
				|| (oBytecode == Bytecode.PUTFIELD)
				|| (oBytecode == Bytecode.PUTSTATIC);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	private static String createTitle(ConstantPool constantPool, int methodRefIndex) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getJavaMethodReturnTypeDesc(constantPool, methodRefIndex));
		buffer.append(" ");
		buffer.append(getJavaMethodName(constantPool, methodRefIndex));
		buffer.append("(");
		buffer.append(getJavaMethodArgsDesc(constantPool, methodRefIndex));
		buffer.append(")");
		return buffer.toString();
	}

	private static String getJavaMethodReturnTypeDesc(ConstantPool constantPool, int index) {
		String descriptor = getJavaMethodDescriptor(constantPool, index);

		int endParenPos = descriptor.indexOf(")");
		descriptor = descriptor.substring(endParenPos + 1);
		return toJavaDescriptor(descriptor);
	}

	private static String getJavaMethodName(ConstantPool constantPool, int index) {
		ConstantPoolInfo_NameAndType nameAndTypeRef = getJavaMethodNameAndTypeRef(constantPool, index);
		int nameIndex = nameAndTypeRef.getNameIndex();
		ConstantPoolInfo_Utf8 utf8 = (ConstantPoolInfo_Utf8) constantPool.get(nameIndex);
		return utf8.getString();
	}

	private static String getJavaMethodArgsDesc(ConstantPool constantPool, int index) {
		String descriptor = getJavaMethodDescriptor(constantPool, index);

		int startParenPos = descriptor.indexOf("(");
		int endParenPos = descriptor.indexOf(")");
		descriptor = descriptor.substring(startParenPos + 1, endParenPos);
		return toJavaDescriptor(descriptor);
	}

	private static String getJavaMethodDescriptor(ConstantPool constantPool, int index) {
		ConstantPoolInfo_NameAndType nameAndTypeRef = getJavaMethodNameAndTypeRef(constantPool, index);
		int descriptorIndex = nameAndTypeRef.getDescriptorIndex();
		ConstantPoolInfo_Utf8 utf8 = (ConstantPoolInfo_Utf8) constantPool.get(descriptorIndex);
		return utf8.getString();
	}

	private static ConstantPoolInfo_NameAndType getJavaMethodNameAndTypeRef(ConstantPool constantPool, int index) {
		ConstantPoolInfo_MethodRef methodRef = (ConstantPoolInfo_MethodRef) constantPool.get(index);
		int nameAndTypeIndex = methodRef.getNameAndTypeIndex();
		ConstantPoolInfo_NameAndType nameAndTypeRef = (ConstantPoolInfo_NameAndType) constantPool.get(nameAndTypeIndex);
		return nameAndTypeRef;
	}

	private static String toJavaDescriptor(String desc) {
		StringBuffer buffer = new StringBuffer();
		int arrayCount = 0;

		for (int pos = 0; pos < desc.length(); pos++) {
			char aChar = desc.charAt(pos);
			if (aChar == '[') {
				arrayCount++;
				continue;
			}

			if (buffer.length() > 0) {
				buffer.append(", ");
			}

			if (aChar == DESC_BYTE) {
				buffer.append("byte");
			} else if (aChar == DESC_CHAR) {
				buffer.append("char");
			} else if (aChar == DESC_DOUBLE) {
				buffer.append("double");
			} else if (aChar == DESC_FLOAT) {
				buffer.append("float");
			} else if (aChar == DESC_INTEGER) {
				buffer.append("int");
			} else if (aChar == DESC_LONG) {
				buffer.append("long");
			} else if (aChar == DESC_OBJ_REF) {
				int endPos = desc.indexOf(";", pos + 1);
				String classPath = desc.substring(pos + 1, endPos);
				classPath = classPath.replace("/", ".");
				buffer.append(classPath);
				pos = endPos + 1;
			} else if (aChar == DESC_SHORT) {
				buffer.append("short");
			} else if (aChar == DESC_VOID) {
				buffer.append("void");
			} else if (aChar == DESC_BOOLEAN) {
				buffer.append("boolean");
			}

			if (arrayCount > 0) {
				for (int i = 0; i < arrayCount; i++) {
					buffer.append("[]");
				}
				arrayCount = 0;
			}
		}
		return buffer.toString();
	}

	private static void printResult(int stackSize, int[] bytecodes, String[] postOpStacks, ConstantPool constantPool) {
		logln();
		logln(String.format(INDENT + "Bytecode stack analysis"));
		logln(String.format(INDENT + INDENT + "Max stack size: %d", stackSize));

		final String FMT_POS = INDENT + INDENT + "%" + ("" + bytecodes.length).length() + "d:";
		final int numStackChars = Math.max(stackSize, 5);
		final String FMT_STACK = "[%-" + numStackChars + "s]";
		final String STR_STACK_UNDEFINED = new String(new char[numStackChars + 2]).replace("\0", "*");

		int pos = 0;
		while (pos < bytecodes.length) {
			Bytecode oBytecode = Bytecode.get(bytecodes[pos], pos, postOpStacks[pos]);
			String postOpStack = postOpStacks[pos];

			log(String.format(FMT_POS, pos));

			boolean wasVisited = postOpStack != null;
			if (wasVisited) {
				String preOpStack = calcPreOpStack(bytecodes, pos, postOpStack, postOpStacks, constantPool);
				log(String.format(" %s -> %s", String.format(FMT_STACK, preOpStack), String.format(FMT_STACK, postOpStack)));
			} else {
				log(String.format(" %s -> %s", STR_STACK_UNDEFINED, STR_STACK_UNDEFINED));
			}

			log(String.format(" %s", formatBytecode(oBytecode, bytecodes, pos, constantPool)));

			if (oBytecode == Bytecode.LOOKUPSWITCH) {
				int newPos = BytecodeUtils.get_u4_paddedPos(pos + 1);
				int defaultPos = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4;  // skip <default>
				int npairs = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4;  // skip <npairs>
				logln();
				for (int i = 0; i < npairs; i++) {
					int match = BytecodeUtils.get_s4(bytecodes, newPos);
					newPos += 4; // skip <match>
					int offset = BytecodeUtils.get_s4(bytecodes, newPos);
					newPos += 4; // skip <offset>
					logln(String.format(INDENT + INDENT + "    %d -> %d", match, pos + offset));
				}
				log(String.format(INDENT + INDENT + "    default -> %d", pos + defaultPos));
				pos = newPos;
			} else if (oBytecode == Bytecode.TABLESWITCH) {
				int newPos = BytecodeUtils.get_u4_paddedPos(pos + 1);
				int defaultPos = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4;  // skip <default>
				int low = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4;  // skip <low>
				int high = BytecodeUtils.get_s4(bytecodes, newPos);
				newPos += 4;  // skip <high>
				logln();
				for (int i = low; i <= high; i++) {
					int offset = BytecodeUtils.get_s4(bytecodes, newPos);
					newPos += 4;
					logln(String.format(INDENT + INDENT + "    %d -> %d", i, pos + offset));
				}
				log(String.format(INDENT + INDENT + "    default -> %d", pos + defaultPos));
				pos = newPos;
			} else {
				pos += oBytecode.getLength(bytecodes, pos);
			}
			logln();
		}
		logln();
	}

	private static String formatBytecode(Bytecode oBytecode, int[] bytecodes, int pos, ConstantPool constantPool) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("%s", oBytecode.getName()));

		if (has_s2_branchOffset(oBytecode)) {
			int branchPos = BytecodeUtils.get_s2(bytecodes, pos + 1);
			buffer.append(String.format(" %d", pos + branchPos));
		} else if (has_s4_branchOffset(oBytecode)) {
			int branchPos = BytecodeUtils.get_s4(bytecodes, pos + 1);
			buffer.append(String.format(" %d", pos + branchPos));
		} else if (has_s1_index(oBytecode)) {
			int index = BytecodeUtils.get_u1(bytecodes, pos + 1);
			buffer.append(String.format(" #%d", index));
		} else if (has_u2_index(oBytecode)) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			buffer.append(String.format(" #%d", index));
		} else if (oBytecode == Bytecode.BIPUSH) {
			int literal = BytecodeUtils.get_s1(bytecodes, pos + 1);
			buffer.append(String.format(" %d", literal));
		} else if (oBytecode == Bytecode.SIPUSH) {
			int literal = BytecodeUtils.get_s2(bytecodes, pos + 1);
			buffer.append(String.format(" %d", literal));
		} else if (oBytecode == Bytecode.IINC) {
			int index = BytecodeUtils.get_u1(bytecodes, pos + 1);
			int increment = BytecodeUtils.get_s1(bytecodes, pos + 1);
			buffer.append(String.format(" #%d, %d", index, increment));
		} else if (oBytecode == Bytecode.MULTINEWARRAY) {
			int index = BytecodeUtils.get_u2(bytecodes, pos + 1);
			int dimensions = BytecodeUtils.get_u1(bytecodes, pos + 3);
			buffer.append(String.format(" #%d, %d", index, dimensions));
		} else if (oBytecode == Bytecode.NEWARRAY) {
			int type = BytecodeUtils.get_u1(bytecodes, pos + 1);
			String strType = "";
			switch (type) {
				case 4 :  strType = "BOOLEAN"; break;
				case 5 :  strType = "CHAR";    break;
				case 6 :  strType = "FLOAT";   break;
				case 7 :  strType = "DOUBLE";  break;
				case 8 :  strType = "BYTE";    break;
				case 9 :  strType = "SHORT";   break;
				case 10 : strType = "INT";     break;
				case 11 : strType = "LONG";    break;
			}
			buffer.append(String.format(" %s", strType));
		} else if (oBytecode == Bytecode.WIDE) {
			Bytecode oNestedBytecode = Bytecode.getNestedByteCode(bytecodes, pos);
			if (oNestedBytecode == Bytecode.IINC) {
				int index = BytecodeUtils.get_u2(bytecodes, pos + 2);
				int increment = BytecodeUtils.get_s2(bytecodes, pos + 4);
				buffer.append(String.format(" %s #%d, %d", oNestedBytecode.getName(), index, increment));
			} else {
				int index = BytecodeUtils.get_u2(bytecodes, pos + 2);
				buffer.append(String.format(" %s #%d", oNestedBytecode.getName(), index));
			}
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	private static final String CR = System.getProperty("line.separator");

	private static void logln(String text) {
		log(text);
		logln();
	}

	private static void logln() {
		log(CR);
	}

	private static void log(String text) {
		if (SHOW_BYTECODE_FLOW_ANALYSIS) {
			System.out.print(text);
		}
	}
}
