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

public class Method_Space extends Method {
	private final static String METHOD_NAME = "Space";
	private final static String DESCRIPTOR = "(F)[C";
	private final static int NUM_LOCALS = 2;

	public Method_Space(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: F  argument
		// local 1: [C array reference

		o.fload_0();
		this.libraryManager.getMethod(MethodEnum.ROUND_TO_INT).emitCall(o);
		o.istore_0();

		o.iload_0();
		o.ifge("skipArgUnderflow");

		emitThrowRuntimeException(o, "SPACE$(): Argument < 0.");

		o.label("skipArgUnderflow");
		o.iload_0();
		o.iconst(0xFF);
		o.if_icmple("skipArgOverflow");

		emitThrowRuntimeException(o, "SPACE$(): Argument > 255.");

		o.label("skipArgOverflow");

		o.iload_0();
		o.newarray_char();
		o.astore_1();

		o.goto_("loopCond");
		o.label("loop");

		o.iinc(0, -1);

		o.aload_1();
		o.iload_0();
		o.iconst(' ');
		o.castore();

		o.label("loopCond");
		o.iload_0();
		o.ifgt("loop");

		o.aload_1();
		o.areturn();
	}
}