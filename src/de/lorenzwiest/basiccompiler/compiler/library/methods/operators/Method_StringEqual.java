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

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_StringEqual extends Method {
	private final static String METHOD_NAME = "StringEqual";
	private final static String DESCRIPTOR = "([C[C)F";
	private final static int NUM_LOCALS = 3;

	public Method_StringEqual(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [C char array1 reference
		// local 1: [C char array2 reference
		// local 2: I  loop counter

		o.aload_0();
		o.arraylength();
		o.aload_1();
		o.arraylength();
		o.if_icmpne("false");

		o.aload_0();
		o.arraylength();
		o.istore_2();
		o.goto_("loopCond");

		o.label("loop");
		o.iinc(2, -1);

		o.aload_0();
		o.iload_2();
		o.caload();
		o.aload_1();
		o.iload_2();
		o.caload();
		o.if_icmpne("false");

		o.label("loopCond");
		o.iload_2();
		o.ifgt("loop");

		o.fconst_1();
		o.fneg();
		o.freturn();

		o.label("false");
		o.fconst_0();
		o.freturn();
	}
}
