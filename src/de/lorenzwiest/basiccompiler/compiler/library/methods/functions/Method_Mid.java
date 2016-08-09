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

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Mid extends Method {
	private final static String METHOD_NAME = "Mid";
	private final static String DESCRIPTOR = "([CFF)[C";
	private final static int NUM_LOCALS = 5;

	public Method_Mid(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// MID$(X$,I[,J])

		// local 0: [C   src char[]
		// local 1: F=>I start index (1-based) of substring, later 0-based, inclusive
		// local 2: F=>I length of substring
		// local 3: I    end index of substring, exclusive
		// local 4: I    length of src char[]

		o.fload_1();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_1();

		o.iload_1();
		o.ifgt("skipIndexUnderflow");

		emitThrowRuntimeException(o, "MID$(): Index <= 0.");

		o.label("skipIndexUnderflow");
		o.iload_1();
		o.iconst(0xFF);
		o.if_icmple("skipIndexOverflow");

		emitThrowRuntimeException(o, "MID$(): Index > 255.");

		o.label("skipIndexOverflow");
		o.fload_2();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_2();

		o.iload_2();
		o.ifgt("skipLenUnderflow");

		emitThrowRuntimeException(o, "MID$(): Length <= 0.");

		o.label("skipLenUnderflow");
		o.iload_2();
		o.iconst(0xFF);
		o.if_icmple("skipLenOverflow");

		emitThrowRuntimeException(o, "MID$(): Length > 255.");

		o.label("skipLenOverflow");

		o.aload_0();
		o.arraylength();
		o.istore(4);

		o.aload_0();

		o.iload_1();
		o.iload(4);
		o.if_icmpgt("startAtEndOfString");

		o.iinc(1, -1);
		o.iload_1();
		o.goto_("calcEndPos");

		o.label("startAtEndOfString");
		o.iload(4);

		o.label("calcEndPos");
		o.iload_1();
		o.iload_2();
		o.iadd();
		o.istore_3();

		o.iload_3();
		o.iload(4);
		o.if_icmpge("endsAtEndOfString");

		o.iload_3();
		o.goto_("substring");

		o.label("endsAtEndOfString");
		o.iload(4);

		o.label("substring");
		this.libraryManager.getMethod(MethodEnum.SUBSTRING).emitCall(o);
		o.areturn();
	}
}
