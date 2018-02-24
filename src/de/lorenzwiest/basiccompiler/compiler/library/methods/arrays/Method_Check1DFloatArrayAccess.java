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

public class Method_Check1DFloatArrayAccess extends Method {
	private final static String METHOD_NAME = "Check1DFloatArrayAccess";
	private final static String DESCRIPTOR = "([[FI)V";
	private final static int NUM_LOCALS = 2;

	public Method_Check1DFloatArrayAccess(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [[F reference to array reference
		// local 1: I   array index

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.ifnonnull("skipInitialize");

		o.aload_0();
		o.iconst(10);
		o.i2f();
		this.libraryManager.getMethod(MethodEnum.DIM_1D_FLOAT_ARRAY).emitCall(o);

		o.label("skipInitialize");
		o.iload_1();
		o.ifge("skipIndexUnderflow");

		emitThrowRuntimeException(o, "Index of 1D number array < 0.");

		o.label("skipIndexUnderflow");
		o.iload_1();
		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.arraylength();
		o.if_icmplt("skipIndexOverflow");

		emitThrowRuntimeException(o, "Index of 1D number array out of max bounds.");

		o.label("skipIndexOverflow");
		o.return_();
	}
}
