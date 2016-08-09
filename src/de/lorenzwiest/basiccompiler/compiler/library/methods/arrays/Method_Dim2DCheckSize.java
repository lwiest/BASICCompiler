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
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Dim2DCheckSize extends Method {
	private final static String METHOD_NAME = "Dim2DCheckSize";
	private final static String DESCRIPTOR = "(II)V";
	private final static int NUM_LOCALS = 2;

	public Method_Dim2DCheckSize(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: I dim1 size
		// local 1: I dim2 size

		o.iload_0();

		o.ifge("skipSize1Underflow");

		emitThrowRuntimeException(o, "DIM 2D array: Max first index of array < 0.");

		o.label("skipSize1Underflow");
		o.iload_0();
		o.iconst(32767);
		o.if_icmple("skipSize1Overflow");

		emitThrowRuntimeException(o, "DIM 2D array: Max first index of array > 32767.");

		o.label("skipSize1Overflow");
		o.iload_1();
		o.ifge("skipSize2Underflow");

		emitThrowRuntimeException(o, "DIM 2D array: Max second index of array < 0.");

		o.label("skipSize2Underflow");
		o.iload_1();
		o.iconst(32767);
		o.if_icmple("skipSize2Overflow");

		emitThrowRuntimeException(o, "DIM 2D array: Max second index of array > 32767.");

		o.label("skipSize2Overflow");
		o.return_();
	}
}
