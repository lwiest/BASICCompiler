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

public class Method_StringConcatenation extends Method {
	private final static String METHOD_NAME = "StringConcatenation";
	private final static String DESCRIPTOR = "([C[C)[C";
	private final static int NUM_LOCALS = 4;

	public Method_StringConcatenation(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [C array1 reference, loop counter 2
		// local 1: [C array2 reference
		// local 2: [C array reference
		// local 3: I  loop counter 1

		o.aload_0();
		o.arraylength();
		o.dup();
		o.istore_3();
		o.aload_1();
		o.arraylength();
		o.iadd();

		o.dup();
		o.iconst(0xFF);
		o.if_icmple("skipStringOverflow");

		emitThrowRuntimeException(o, "Concatenated string size > 255.");

		o.label("skipStringOverflow");
		o.newarray_char();
		o.astore_2();

		o.goto_("loop1Cond");
		o.label("loop1");
		o.aload_2();
		o.iinc(3, -1);
		o.iload_3();
		o.aload_0();
		o.iload_3();
		o.caload();
		o.castore();

		o.label("loop1Cond");
		o.iload_3();
		o.ifgt("loop1");

		o.aload_1();
		o.arraylength();
		o.dup();
		o.istore_3();

		o.aload_0();
		o.arraylength();
		o.iadd();
		o.istore_0();

		o.goto_("loop2Cond");
		o.label("loop2");

		o.aload_2();

		o.iinc(0, -1);
		o.iinc(3, -1);

		o.iload_0();

		o.aload_1();
		o.iload_3();
		o.caload();
		o.castore();

		o.label("loop2Cond");
		o.iload_3();
		o.ifgt("loop2");

		o.aload_2();
		o.areturn();
	}
}
