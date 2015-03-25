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

package org.basiccompiler.bytecode.info;

import java.util.List;

import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_Utf8;
import org.basiccompiler.compiler.etc.ByteOutStream;

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

	public CodeAttributeInfo(List<ConstantPoolInfo> constantPool, int maxLocals, byte[] code, ExceptionTableInfo[] exceptionTable) {
		this.attributeNameIndex = ConstantPoolInfo_Utf8.addAndReturnIndex(constantPool, CODE_ID);
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
		o.write_u2(this.attributeNameIndex + 1); // NOTE: serialized constant pool indexes are 1-based
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