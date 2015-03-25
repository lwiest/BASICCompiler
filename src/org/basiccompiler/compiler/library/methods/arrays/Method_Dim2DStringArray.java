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

package org.basiccompiler.compiler.library.methods.arrays;

import java.util.List;

import org.basiccompiler.bytecode.info.ExceptionTableInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;
import org.basiccompiler.compiler.library.LibraryManager;
import org.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import org.basiccompiler.compiler.library.methods.Method;

public class Method_Dim2DStringArray extends Method {
	private final static String METHOD_NAME = "Dim2DStringArray";
	private final static String DESCRIPTOR = "(FF)[[[C";
	private final static int NUM_LOCALS = 5;

	public Method_Dim2DStringArray(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: F=>I dim1 max element index (= size - 1), outer loop counter
		// local 1: F=>I dim2 max element index (= size - 1), inner loop counter
		// local 2: [[[C array ref
		// local 3: [[C  array ref "sub array"
		// local 4: [C   empty char array ref

		o.fload_0();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_0();

		o.fload_1();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_1();

		o.iload_0();
		o.iload_1();
		this.libraryManager.getMethod(MethodEnum.DIM_2D_CHECK_SIZE).emitCall(o);

		o.iinc(0, 1);
		o.iinc(1, 1);
		o.iload_0();
		o.iload_1();
		o.iconst_0();
		o.multianewarray(this.classModel.getClassIndex("[[[C"), 3);
		o.astore_2();

		o.iconst_0(); // create empty char array
		o.newarray_char();
		o.astore(4);

		o.goto_("outerLoopCond");

		o.label("outerLoop");
		o.iinc(0, -1);

		o.aload_2(); // load sub array
		o.iload_0();
		o.aaload();
		o.astore_3();

		o.aload_3();
		o.arraylength();
		o.istore_1();
		o.goto_("innerLoopCond");

		o.label("innerLoop");
		o.iinc(1, -1);

		o.aload_3();
		o.iload_1();
		o.aload(4);
		o.aastore();

		o.label("innerLoopCond");
		o.iload_1();
		o.ifgt("innerLoop");

		o.label("outerLoopCond");
		o.iload_0();
		o.ifgt("outerLoop");

		o.aload_2();
		o.areturn();
	}
}
