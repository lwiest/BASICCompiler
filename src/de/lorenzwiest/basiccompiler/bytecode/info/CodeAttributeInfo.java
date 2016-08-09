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

package de.lorenzwiest.basiccompiler.bytecode.info;

import de.lorenzwiest.basiccompiler.bytecode.ConstantPool;
import de.lorenzwiest.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_Utf8;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

public class CodeAttributeInfo {
	// u2 attribute_name_index;
	// u4 attribute_length;
	// u2 max_stack;
	// u2 max_locals;
	// u4 code_length;
	// u1 code[code_length];
	// u2 exception_table_length;
	// { u2 start_pc;
	//   u2 end_pc;
	//   u2 handler_pc;
	//   u2 catch_type;
	// } exception_table[exception_table_length];
	// u2 attributes_count;
	// attribute_info attributes[attributes_count];

	private static final String CODE_ID = "Code";

	private final int attributeNameIndex;              // u2
	// private int attributeLength;                    // u4
	private final int maxStack;                        // u2
	private final int maxLocals;                       // u2
	// private int codeLength;                         // u4
	private final byte[] code;                         // u1[]
	// private int exceptionTableLength;               // u2
	private final ExceptionTableInfo[] exceptionTable; // exception_table[]
	// private int attributesCount;                    // u2
	private final Object[] attributeInfos;             // attribute_info[]

	public CodeAttributeInfo(ConstantPool constantPool, int maxLocals, byte[] code, ExceptionTableInfo[] exceptionTable) {
		this.attributeNameIndex = ConstantPoolInfo_Utf8.addAndGetIndex(constantPool, CODE_ID);
		// this.attributeLength calculated implicitly in write()
		this.maxStack = 100; // TODO: variable max operand stack size
		this.maxLocals = maxLocals;
		// this.codeLength calculated implicitly in write()
		this.code = code;
		// this.exceptionTableLength calculated implicitly in write()
		this.exceptionTable = exceptionTable;
		// this.attributesCount calculated implicitly in write()
		this.attributeInfos = new Object[0];
	}

	public void write(ByteOutStream o) {
		o.write_u2(this.attributeNameIndex);
		o.write_u4(12 + this.code.length + (this.exceptionTable.length * 2 * 4));
		o.write_u2(this.maxStack);
		o.write_u2(this.maxLocals);
		o.write_u4(this.code.length);
		for (int i = 0; i < this.code.length; i++) {
			o.write_u1(this.code[i]);
		}
		o.write_u2(this.exceptionTable.length);
		for (int i = 0; i < this.exceptionTable.length; i++) {
			this.exceptionTable[i].write(o);
		}
		o.write_u2(this.attributeInfos.length);
		for (int i = 0; i < this.attributeInfos.length; i++) {
			// not implemented, not needed yet
		}
	}
}