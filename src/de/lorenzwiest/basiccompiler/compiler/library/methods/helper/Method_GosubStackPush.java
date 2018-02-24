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
import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_GosubStackPush extends Method {
	private final static String METHOD_NAME = "GosubStackPush";
	private final static String DESCRIPTOR = "(I)V";
	private final static int NUM_LOCALS = 1;

	public Method_GosubStackPush(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// local 0: I integer to push on GOSUB stack

		int gosubStackFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_GOSUB_STACK, "[I");
		int gosubStackIndexFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_GOSUB_STACK_INDEX, "I");

		o.getstatic(gosubStackIndexFieldRef);
		o.iconst(Compiler.GOSUB_STACK_FRAME_SIZE);
		o.iadd();
		o.iconst(Compiler.GOSUB_STACK_SIZE * Compiler.GOSUB_STACK_FRAME_SIZE);
		o.if_icmple("noStackOverflow");

		emitThrowRuntimeException(o, "Too many nested GOSUBs.");

		o.label("noStackOverflow");
		o.getstatic(gosubStackFieldRef);
		o.getstatic(gosubStackIndexFieldRef);
		o.iload_0();
		o.iastore();

		o.getstatic(gosubStackIndexFieldRef);
		o.iconst_1();
		o.iadd();
		o.putstatic(gosubStackIndexFieldRef);
		o.return_();
	}
}
