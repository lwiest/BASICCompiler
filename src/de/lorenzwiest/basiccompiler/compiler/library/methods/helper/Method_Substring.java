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

package de.lorenzwiest.basiccompiler.compiler.library.methods.helper;

import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Substring extends Method {
	private final static String METHOD_NAME = "Substring";
	private final static String DESCRIPTOR = "([CII)[C";
	private final static int NUM_LOCALS = 5;

	public Method_Substring(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [C source char[]
		// local 1: I  start index, inclusive
		// local 2: I  end index, exclusive
		// local 3: I  temp length substring
		// local 4: [C substring char[]

		o.iload_2();
		o.iload_1();
		o.isub();
		o.istore_3();

		o.iload_3();
		o.newarray_char();
		o.astore(4);
		o.goto_("loopCond");

		o.label("loop");
		o.iinc(3, -1);
		o.iinc(2, -1);

		o.aload(4);
		o.iload_3();

		o.aload_0();
		o.iload_2();
		o.caload();
		o.castore();

		o.label("loopCond");
		o.iload_3();
		o.ifgt("loop");

		o.aload(4);
		o.areturn();
	}
}
