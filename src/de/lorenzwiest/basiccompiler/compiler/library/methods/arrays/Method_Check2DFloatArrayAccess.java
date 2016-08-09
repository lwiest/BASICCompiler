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

public class Method_Check2DFloatArrayAccess extends Method {
	private final static String METHOD_NAME = "Check2DFloatArrayAccess";
	private final static String DESCRIPTOR = "([[[FII)V";
	private final static int NUM_LOCALS = 3;

	public Method_Check2DFloatArrayAccess(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [[[F reference to array reference
		// local 1: F=>I array index 1
		// local 2: F=>I array index 2

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.ifnonnull("skipInitialize");

		o.aload_0();
		o.iconst(10);
		o.i2f();
		o.dup();
		this.libraryManager.getMethod(MethodEnum.DIM_2D_FLOAT_ARRAY).emitCall(o);

		o.label("skipInitialize");
		o.iload_1();
		o.ifge("skipIndex1Underflow");

		emitThrowRuntimeException(o, "First index of 2D number array < 0.");

		o.label("skipIndex1Underflow");
		o.iload_1();

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.arraylength();
		o.if_icmplt("skipIndex1Overflow");

		emitThrowRuntimeException(o, "First index of 2D number array out of max bounds.");

		o.label("skipIndex1Overflow");
		o.iload_2();
		o.ifge("skipIndex2Underflow");

		emitThrowRuntimeException(o, "Second index of 2D number array < 0.");

		o.label("skipIndex2Underflow");
		o.iload_2();

		o.aload_0();
		o.iconst_0();
		o.aaload();
		o.iload_1();  // fetch sub array reference
		o.aaload();
		o.arraylength();
		o.if_icmplt("skipIndex2Overflow");

		emitThrowRuntimeException(o, "Second index of 2D number array out of max bounds.");

		o.label("skipIndex2Overflow");
		o.return_();
	}
}
