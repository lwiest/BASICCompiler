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

public class Method_Left extends Method {

	private final static String METHOD_NAME = "Left";
	private final static String DESCRIPTOR = "([CF)[C";
	private final static int NUM_LOCALS = 2;

	public Method_Left(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: [C src char[]
		// local 1: F  length of substring

		o.fload_1();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_1();

		o.iload_1();
		o.ifge("skipIndexUnderflow");

		emitThrowRuntimeException(o, "LEFT$(): Index < 0.");

		o.label("skipIndexUnderflow");
		o.iload_1();
		o.iconst(0x0FF);
		o.if_icmple("skipIndexOverflow");

		emitThrowRuntimeException(o, "LEFT$(): Index > 255.");

		o.label("skipIndexOverflow");

		o.aload_0();
		o.iconst_0();

		o.iload_1();
		o.aload_0();
		o.arraylength();
		o.if_icmpge("endOfString");

		o.iload_1();
		o.goto_("substring");

		o.label("endOfString");
		o.aload_0();
		o.arraylength();

		o.label("substring");
		this.libraryManager.getMethod(MethodEnum.SUBSTRING).emitCall(o);
		o.areturn();
	}
}
