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

package de.lorenzwiest.basiccompiler.compiler.library.methods.functions;

import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Instr extends Method {
	private static final String METHOD_NAME = "Instr";
	private static final String DESCRIPTOR = "(F[C[C)F";
	private static final int NUM_LOCALS = 7;

	public Method_Instr(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e) {

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
