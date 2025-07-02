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

package de.lorenzwiest.basiccompiler.compiler.library.methods.operators;

import static de.lorenzwiest.basiccompiler.classfile.ClassModel.JavaMethod.MATH_POW;

import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Power extends Method {
	private static final String METHOD_NAME = "Power";
	private static final String DESCRIPTOR = "(FF)F";
	private static final int NUM_LOCALS = 2;

	public Method_Power(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: F base
		// local 1: F exponent

		o.fload_0();
		o.fconst_0();
		o.fcmpg();
		o.ifne("baseNotZero");
		o.fload_1();
		o.fconst_0();
		o.fcmpg();
		o.iflt("divisionByZero");

		o.label("baseNotZero");
		o.fload_0();
		o.f2d();
		o.fload_1();
		o.f2d();
		o.invokestatic(this.classModel.getJavaMethodRefIndex(MATH_POW));
		o.d2f();
		o.freturn();

		o.label("divisionByZero");
		o.fconst_1(); // always POSITIVE_INFINITY
		this.libraryManager.getMethod(MethodEnum.DIVISION_BY_ZERO).emitCall(o);
		o.freturn();
	}
}
