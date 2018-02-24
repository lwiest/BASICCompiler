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

package de.lorenzwiest.basiccompiler.compiler.library.methods.arrays;

import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Dim2DStringArray extends Method {
	private final static String METHOD_NAME = "Dim2DStringArray";
	private final static String DESCRIPTOR = "([[[[CFF)V";
	private final static int NUM_LOCALS = 5;

	public Method_Dim2DStringArray(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [[[[C reference to array reference
		// local 1: F=>I  dim1 max element index (= size - 1), outer loop counter
		// local 2: F=>I  dim2 max element index (= size - 1), inner loop counter
		// local 3: [[C   array ref "sub array"
		// local 4: [C    empty char array ref

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.ifnull("initialize");

		emitThrowRuntimeException(o, "2D string array already dimensioned.");

		o.label("initialize");
		o.aload_0();
		o.iconst_0();

		o.fload_1();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_1();

		o.fload_2();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_2();

		o.iload_1();
		o.iload_2();
		this.libraryManager.getMethod(MethodEnum.DIM_2D_CHECK_SIZE).emitCall(o);

		o.iinc(1, 1);
		o.iinc(2, 1);
		o.iload_1();
		o.iload_2();
		o.iconst_0();
		o.multianewarray(this.classModel.getClassIndex("[[[C"), 3);
		o.aastore();

		// create empty char array

		o.iconst_0();
		o.newarray_char();
		o.astore(4);

		// outer initialization loop

		o.goto_("outerLoopCond");

		o.label("outerLoop");
		o.iinc(1, -1);

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.iload_1();
		o.aaload();
		o.astore_3();

		o.aload_3();
		o.arraylength();
		o.istore_2();

		// inner initialization loop

		o.goto_("innerLoopCond");

		o.label("innerLoop");
		o.iinc(2, -1);

		o.aload_3();
		o.iload_2();
		o.aload(4);
		o.aastore();

		o.label("innerLoopCond");
		o.iload_2();
		o.ifgt("innerLoop");

		o.label("outerLoopCond");
		o.iload_1();
		o.ifgt("outerLoop");

		o.return_();
	}
}
