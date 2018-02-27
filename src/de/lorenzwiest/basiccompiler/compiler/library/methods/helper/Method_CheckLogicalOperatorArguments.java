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

package de.lorenzwiest.basiccompiler.compiler.library.methods.helper;

import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_CheckLogicalOperatorArguments extends Method {
	private final static String METHOD_NAME = "CheckLogicalOperatorAguments";
	private final static String DESCRIPTOR = "(FF)V";
	private final static int NUM_LOCALS = 2;

	public Method_CheckLogicalOperatorArguments(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: F=>I argument 1
		// local 1: F=>I argument 2

		o.fload_0();
		o.f2i();
		o.istore_0();

		o.iload_0();
		o.iconst(-32768);
		o.if_icmpge("skipArg1Underflow");

		emitThrowRuntimeException(o, "Logical operator: First argument < -32768.");

		o.label("skipArg1Underflow");
		o.iload_0();
		o.iconst(32767);
		o.if_icmple("skipArg1Overflow");

		emitThrowRuntimeException(o, "Logical operator: First argument > 32767.");

		o.label("skipArg1Overflow");
		o.fload_1();
		o.f2i();
		o.istore_1();

		o.iload_1();
		o.iconst(-32768);
		o.if_icmpge("skipArg2Underflow");

		emitThrowRuntimeException(o, "Logical operator: Second argument < -32768.");

		o.label("skipArg2Underflow");
		o.iload_1();
		o.iconst(32767);
		o.if_icmple("skipArg2Overflow");

		emitThrowRuntimeException(o, "Logical operator: Second argument > 32767.");

		o.label("skipArg2Overflow");
		o.return_();
	}
}
