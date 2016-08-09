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

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Dim1DStringArray extends Method {
	private final static String METHOD_NAME = "Dim1DStringArray";
	private final static String DESCRIPTOR = "([[[CF)V";
	private final static int NUM_LOCALS = 3;

	public Method_Dim1DStringArray(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [[[C reference to array reference
		// local 1: F=>I dim max element index (= size - 1), loop counter
		// local 2: [C   empty char array reference

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.ifnull("initialize");

		emitThrowRuntimeException(o, "1D string array already dimensioned.");

		o.label("initialize");
		o.aload_0();
		o.iconst_0();

		o.fload_1();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_1();

		o.iload_1();
		this.libraryManager.getMethod(MethodEnum.DIM_1D_CHECK_SIZE).emitCall(o);

		o.iinc(1, 1);
		o.iload_1();
		o.iconst_0();
		o.multianewarray(this.classModel.getClassIndex("[[C"), 2);
		o.aastore();

		// create empty string array

		o.iconst_0();
		o.newarray_char();
		o.astore_2();

		// initialization loop

		o.goto_("loopCond");

		o.label("loop");
		o.iinc(1, -1);

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.iload_1();
		o.aload_2();
		o.aastore();

		o.label("loopCond");
		o.iload_1();
		o.ifgt("loop");

		o.return_();
	}
}