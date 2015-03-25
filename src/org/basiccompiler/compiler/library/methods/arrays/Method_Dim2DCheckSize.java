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

package org.basiccompiler.compiler.library.methods.arrays;

import java.util.List;

import org.basiccompiler.bytecode.info.ExceptionTableInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;
import org.basiccompiler.compiler.library.LibraryManager;
import org.basiccompiler.compiler.library.methods.Method;

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
