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

public class Method_Check2DFloatArrayAccess extends Method {
	private final static String METHOD_NAME = "Check2DFloatArrayAccess";
	private final static String DESCRIPTOR = "([[[FII)V";
	private final static int NUM_LOCALS = 3;

	public Method_Check2DFloatArrayAccess(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [[[F reference to array reference
		// local 1: F=>I array index 1
		// local 2: F=>I array index 2

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.ifnonnull("skipInitialize");

		o.aload_0();
		o.iconst(10);
		o.i2f();
		o.dup();
		this.libraryManager.getMethod(MethodEnum.DIM_2D_FLOAT_ARRAY).emitCall(o);

		o.label("skipInitialize");
		o.iload_1();
		o.ifge("skipIndex1Underflow");

		emitThrowRuntimeException(o, "First index of 2D number array < 0.");

		o.label("skipIndex1Underflow");
		o.iload_1();

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.arraylength();
		o.if_icmplt("skipIndex1Overflow");

		emitThrowRuntimeException(o, "First index of 2D number array out of max bounds.");

		o.label("skipIndex1Overflow");
		o.iload_2();
		o.ifge("skipIndex2Underflow");

		emitThrowRuntimeException(o, "Second index of 2D number array < 0.");

		o.label("skipIndex2Underflow");
		o.iload_2();

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.iload_1();  // fetch sub array reference
		o.aaload();
		o.arraylength();
		o.if_icmplt("skipIndex2Overflow");

		emitThrowRuntimeException(o, "Second index of 2D number array out of max bounds.");

		o.label("skipIndex2Overflow");
		o.return_();
	}
}
