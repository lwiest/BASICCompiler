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

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_StringLessThan extends Method {
	private static final String METHOD_NAME = "StringLessThan";
	private static final String DESCRIPTOR = "([C[C)F";
	private static final int NUM_LOCALS = 4;

	public Method_StringLessThan(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [C char array1 reference
		// local 1: [C char array2 reference
		// local 2: I  loop counter,
		// local 3: I  min string length

		// PSEUDO SOURCE CODE
		//
		// int min := min(str1.length, str2.length)
		// for (int i = 0; i < min; i++) {
		//   if (str1[i] > str2[i] {
		//     return false;
		//   }
		//   if (str1[i] < str2[i] {
		//     return true;
		//   }
		// }
		// if (str1.length == str2.length) => return false;
		// if (str1.length > str2.length)  => return false;
		// if (str1.length < str2.length)  => return true;

		o.aload_0();
		o.arraylength();
		o.aload_1();
		o.arraylength();
		o.if_icmplt("skip");
		o.aload_1();
		o.goto_("skip2");
		o.label("skip");
		o.aload_0();
		o.label("skip2");
		o.arraylength();
		o.istore_3();

		o.iconst_0();
		o.istore_2();
		o.goto_("loopCond");

		o.label("loop");
		o.aload_0();
		o.iload_2();
		o.caload();
		o.aload_1();
		o.iload_2();
		o.caload();
		o.if_icmpgt("false");

		o.aload_0();
		o.iload_2();
		o.caload();
		o.aload_1();
		o.iload_2();
		o.caload();
		o.if_icmplt("true");

		o.iinc(2, 1);

		o.label("loopCond");
		o.iload_2();
		o.iload_3();
		o.if_icmplt("loop");

		o.aload_0();
		o.arraylength();
		o.aload_1();
		o.arraylength();
		o.isub();
		o.ifge("false");

		o.label("true");
		o.fconst_1();
		o.fneg();
		o.freturn();

		o.label("false");
		o.fconst_0();
		o.freturn();
	}
}
