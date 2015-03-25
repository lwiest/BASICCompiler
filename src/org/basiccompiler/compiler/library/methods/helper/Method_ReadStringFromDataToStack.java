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

package org.basiccompiler.compiler.library.methods.helper;

import java.util.List;

import org.basiccompiler.bytecode.info.ExceptionTableInfo;
import org.basiccompiler.compiler.Compiler;
import org.basiccompiler.compiler.etc.ByteOutStream;
import org.basiccompiler.compiler.library.LibraryManager;
import org.basiccompiler.compiler.library.methods.Method;

public class Method_ReadStringFromDataToStack extends Method {
	private final static String METHOD_NAME = "ReadStringFromDataToStack";
	private final static String DESCRIPTOR = "()[C";
	private final static int NUM_LOCALS = 3;

	public Method_ReadStringFromDataToStack(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// TODO: Add documentation!

		// local 0:
		// local 1:
		// local 2:

		int dataIndexFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_DATA_INDEX, "I");
		int dataInfoFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_DATA_INFO, "[C");
		int dataFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_DATA, "[C");

		o.getstatic(dataIndexFieldRef);
		o.getstatic(dataInfoFieldRef);
		o.arraylength();
		o.iconst_1();
		o.ishr();
		o.if_icmplt("not out of data");

		emitThrowRuntimeException(o, "Out of data.");

		o.label("not out of data");
		o.getstatic(dataIndexFieldRef);
		o.iconst_1();
		o.ishl();

		o.dup();
		o.getstatic(dataInfoFieldRef);
		o.swap();
		o.caload();
		o.istore_0(); // local 0 := index of data element

		o.iconst_1();
		o.iadd();
		o.getstatic(dataInfoFieldRef);
		o.swap();
		o.caload();
		o.istore_1(); // local 1 := length of data element

		o.iload_1();
		o.newarray_char();
		o.astore_2(); // local 2 := new char[length of data element]

		// copy loop
		//
		// @0 := @0 + (@1--);
		// while (@1 >= 0) {
		//   @2[@1] := _data[@0];
		//   @1--;
		//   @0--;
		// }

		o.iinc(1, -1);

		o.iload_0();
		o.iload_1();
		o.iadd();
		o.istore_0();

		o.goto_("loopCond");

		o.label("loop");
		o.aload_2();
		o.iload_1();

		o.iload_0();
		o.getstatic(dataFieldRef);
		o.swap();
		o.caload();
		o.castore();

		o.iinc(1, -1);
		o.iinc(0, -1);

		o.label("loopCond");
		o.iload_1();
		o.ifge("loop");

		// dataIndex++

		o.getstatic(dataIndexFieldRef);
		o.iconst_1();
		o.iadd();
		o.putstatic(dataIndexFieldRef);

		o.aload_2();
		o.areturn();
	}
}
