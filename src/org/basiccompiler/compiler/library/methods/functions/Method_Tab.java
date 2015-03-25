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

package org.basiccompiler.compiler.library.methods.functions;

import java.util.List;

import org.basiccompiler.bytecode.info.ExceptionTableInfo;
import org.basiccompiler.compiler.Compiler;
import org.basiccompiler.compiler.etc.ByteOutStream;
import org.basiccompiler.compiler.library.LibraryManager;
import org.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import org.basiccompiler.compiler.library.methods.Method;

public class Method_Tab extends Method {
	private final static String METHOD_NAME = "Tab";
	private final static String DESCRIPTOR = "(F)V";
	private final static int NUM_LOCALS = 2;

	public Method_Tab(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0 : F=>I argument
		// local 1 : I    delta loop counter

		int posFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_CURSOR_POS, "I");

		o.fload_0();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_0();

		o.iload_0();
		o.ifgt("skipArgUnderflow");

		emitThrowRuntimeException(o, "TAB(): Argument < 1.");

		o.label("skipArgUnderflow");
		o.iload_0();
		o.iconst(0xFF);
		o.if_icmple("skipArgOverflow");

		emitThrowRuntimeException(o, "TAB(): Argument > 255.");

		o.label("skipArgOverflow");
		o.iload_0();
		o.iinc(0, -1);
		o.getstatic(posFieldRef);
		o.iload_0();
		o.if_icmple("skipCarriageReturn");

		emitPrint(o, Compiler.CR);

		o.label("skipCarriageReturn");
		o.iload_0();
		o.getstatic(posFieldRef);
		o.isub();
		o.istore_1();
		o.goto_("loopCond");

		o.label("loop");
		o.iconst(' ');
		this.libraryManager.getMethod(MethodEnum.PRINT_CHAR_FROM_STACK).emitCall(o);
		o.iinc(1, -1);

		o.label("loopCond");
		o.iload_1();
		o.ifgt("loop");

		o.return_();
	}
}
