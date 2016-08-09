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

import static de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaMethod.MATH_RANDOM;

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Rnd extends Method {
	private final static String METHOD_NAME = "Rnd";
	private final static String DESCRIPTOR = "(F)F";
	private final static int NUM_LOCALS = 1;

	public Method_Rnd(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// TODO: Implement RND(X) with X < 0

		// local 0: F argument

		int lastRndFieldRef = this.classModel.addFieldAndGetFieldRefIndex(Compiler.FIELD_LAST_RND, "F");

		o.fload_0();
		o.fconst_0();
		o.fcmpg();
		o.istore_0();

		o.iload_0();
		o.ifge("isZeroOrPositiveArg");

		emitThrowRuntimeException(o, "RND(): Argument < 0.");

		o.label("isZeroOrPositiveArg");
		o.iload_0();
		o.ifgt("newRandomNumber");

		o.getstatic(lastRndFieldRef); // HACK: initializes last random number
		o.fconst_0();
		o.fcmpg();
		o.ifeq("newRandomNumber");

		o.getstatic(lastRndFieldRef);
		o.freturn();

		o.label("newRandomNumber");
		o.invokestatic(this.classModel.getJavaMethodRefIndex(MATH_RANDOM));
		o.d2f();
		o.dup();
		o.putstatic(lastRndFieldRef);
		o.freturn();
	}
}
