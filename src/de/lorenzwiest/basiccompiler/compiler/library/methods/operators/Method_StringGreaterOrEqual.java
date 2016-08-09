/*
 * Copyright (c) 2015, Lorenz Wiest
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the FreeBSD Project.
 */

package de.lorenzwiest.basiccompiler.compiler.library.methods.operators;

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_StringGreaterOrEqual extends Method {
	private final static String METHOD_NAME = "StringGreaterOrEqual";
	private final static String DESCRIPTOR = "([C[C)F";
	private final static int NUM_LOCALS = 4;

	public Method_StringGreaterOrEqual(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [C char array1 reference
		// local 1: [C char array2 reference
		// local 2: I  loop counter,
		// local 3: I  min string length

		// PSEUDO SOURCE CODE
		//
		// int min := min(str1.length, str2.length)
		// for (int i = 0; i < min; i++) {
		//   if (str1[i] < str2[i] {
		//     return false;
		//   }
		// }
		// if (str1.length == str2.length) => return true;
		// if (str1.length > str2.length)  => return true;
		// if (str1.length < str2.length)  => return false;

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
		o.if_icmplt("false");

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
		o.iflt("false");

		o.label("true");
		o.fconst_1();
		o.fneg();
		o.freturn();

		o.label("false");
		o.fconst_0();
		o.freturn();
	}
}
