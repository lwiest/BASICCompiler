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

package de.lorenzwiest.basiccompiler.compiler.library.methods.functions;

import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Tan extends Method {
	private static final String METHOD_NAME = "Tan";
	private static final String DESCRIPTOR = "(F)F";
	private static final int NUM_LOCALS = 2;

	public Method_Tan(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: F argument, sin(argument)
		// local 1: F cos(argument)

		o.fload_0();
		this.libraryManager.getMethod(MethodEnum.COS).emitCall(o);
		o.fstore_1();

		o.fload_0();
		this.libraryManager.getMethod(MethodEnum.SIN).emitCall(o);
		o.fstore_0();

		o.fload_1();
		o.fconst_0();
		o.fcmpg();
		o.ifeq("divisionByZero");

		o.fload_0(); // POSITIVE_INFINITY or NEGATIVE_INFINITY
		o.fload_1();
		o.fdiv();
		o.freturn();

		o.label("divisionByZero");
		o.fload_0();
		this.libraryManager.getMethod(MethodEnum.DIVISION_BY_ZERO).emitCall(o);
		o.freturn();
	}
}
