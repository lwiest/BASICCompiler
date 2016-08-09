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

package de.lorenzwiest.basiccompiler.compiler.library.methods.arrays;

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Dim1DStringArray extends Method {
	private final static String METHOD_NAME = "Dim1DStringArray";
	private final static String DESCRIPTOR = "([[[CF)V";
	private final static int NUM_LOCALS = 3;

	public Method_Dim1DStringArray(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [[[C reference to array reference
		// local 1: F=>I dim max element index (= size - 1), loop counter
		// local 2: [C   empty char array reference

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.ifnull("initialize");

		emitThrowRuntimeException(o, "1D string array already dimensioned.");

		o.label("initialize");
		o.aload_0();
		o.iconst_0();

		o.fload_1();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_1();

		o.iload_1();
		this.libraryManager.getMethod(MethodEnum.DIM_1D_CHECK_SIZE).emitCall(o);

		o.iinc(1, 1);
		o.iload_1();
		o.iconst_0();
		o.multianewarray(this.classModel.getClassIndex("[[C"), 2);
		o.aastore();

		// create empty string array

		o.iconst_0();
		o.newarray_char();
		o.astore_2();

		// initialization loop

		o.goto_("loopCond");

		o.label("loop");
		o.iinc(1, -1);

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.iload_1();
		o.aload_2();
		o.aastore();

		o.label("loopCond");
		o.iload_1();
		o.ifgt("loop");

		o.return_();
	}
}
