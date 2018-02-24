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

public class Method_GosubStackPop extends Method {
	private final static String METHOD_NAME = "GosubStackPop";
	private final static String DESCRIPTOR = "()I";
	private final static int NUM_LOCALS = 0;

	public Method_GosubStackPop(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {
		int gosubStackFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_GOSUB_STACK, "[I");
		int gosubStackIndexFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_GOSUB_STACK_INDEX, "I");

		o.getstatic(gosubStackIndexFieldRef);
		o.ifge("noStackUnderflow");

		emitThrowRuntimeException(o, "RETURN without GOSUB.");

		o.label("noStackUnderflow");
		o.getstatic(gosubStackIndexFieldRef);
		o.iconst(Compiler.GOSUB_STACK_FRAME_SIZE);
		o.isub();
		o.putstatic(gosubStackIndexFieldRef);

		o.getstatic(gosubStackFieldRef);
		o.getstatic(gosubStackIndexFieldRef);
		o.iaload();
		o.ireturn();
	}
}
