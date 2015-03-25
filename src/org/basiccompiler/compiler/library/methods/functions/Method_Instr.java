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
import org.basiccompiler.compiler.etc.ByteOutStream;
import org.basiccompiler.compiler.library.LibraryManager;
import org.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import org.basiccompiler.compiler.library.methods.Method;

public class Method_Instr extends Method {
	private final static String METHOD_NAME = "Instr";
	private final static String DESCRIPTOR = "(F[C[C)F";
	private final static int NUM_LOCALS = 7;

	public Method_Instr(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// INSTR([I], X$, Y$)

		// local_0: F=>I start index
		// local_1: [C   array reference string to be searched
		// local_2: [C   array reference string to search
		// local_3: I    maxlength
		// local_4: I    i (outer loop counter)
		// local_5: I    matchFound flag
		// local_6: I    j (inner loop counter)

		o.fload_0();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_0();

		// I < 1 => exception

		o.iload_0();
		o.ifgt("skipIndexUnderflow");

		emitThrowRuntimeException(o, "INSTR(): Index < 1.");

		o.label("skipIndexUnderflow");

		// I > 255 => exception

		o.iload_0();
		o.iconst(0xFF);
		o.if_icmple("skipIndexOverflow");

		emitThrowRuntimeException(o, "INSTR(): Index > 255.");

		o.label("skipIndexOverflow");

		// I > LEN(X$) => 0

		o.iload_0();
		o.aload_1();
		o.arraylength();
		o.if_icmple("skipIndexAfterString");

		o.fconst_0();
		o.freturn();

		o.label("skipIndexAfterString");

		// X$ = "" => 0

		o.aload_1();
		o.arraylength();
		o.ifgt("skipEmptyString");

		o.fconst_0();
		o.freturn();

		o.label("skipEmptyString");

		// Y$ = "" => I

		o.aload_2();
		o.arraylength();
		o.ifgt("skipEmptySearchString");

		o.iload_0();
		o.i2f();
		o.freturn();

		o.label("skipEmptySearchString");

		o.iinc(0, -1);

		o.aload_1();
		o.arraylength();
		o.aload_2();
		o.arraylength();
		o.isub();
		o.istore_3();

		o.iload_0();
		o.istore(4);
		o.goto_("loopICond");

		o.label("loopI");
		o.iconst_1();
		o.istore(5);

		o.iload(4);
		o.istore(6);
		o.goto_("loopJCond");

		o.label("loopJ");
		o.aload_1();
		o.iload(6);
		o.caload();
		o.aload_2();
		o.iload(6);
		o.iload(4);
		o.isub();
		o.caload();
		o.if_icmpeq("loopJNext");

		o.iconst_0();
		o.istore(5);
		o.goto_("breakLoopJ");

		o.label("loopJNext");
		o.iinc(6, 1);

		o.label("loopJCond");
		o.iload(6);
		o.iload(4);
		o.aload_2();
		o.arraylength();
		o.iadd();
		o.if_icmplt("loopJ");

		o.label("breakLoopJ");
		o.iload(5);
		o.ifeq("preloopICond");

		o.iinc(4, 1);
		o.iload(4);
		o.i2f();
		o.freturn();

		o.label("preloopICond");
		o.iinc(4, 1);

		o.label("loopICond");
		o.iload(4);
		o.iload(3);
		o.if_icmplt("loopI");

		o.fconst_0();
		o.freturn();
	}
}
