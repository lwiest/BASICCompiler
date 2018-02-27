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

package de.lorenzwiest.basiccompiler.compiler.library.methods;

import java.util.ArrayList;
import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.ClassModel;
import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;

public abstract class Method {
	protected final LibraryManager libraryManager;
	protected final ClassModel classModel;
	private final String methodName;
	private final String descriptor;
	private final int numLocals;

	public Method(LibraryManager libraryManager, String methodName, String descriptor, int numLocals) {
		this.libraryManager = libraryManager;
		this.classModel = libraryManager.getClassModel();
		this.methodName = methodName;
		this.descriptor = descriptor;
		this.numLocals = numLocals;
	}

	public void emitCall(ByteOutStream o) {
		o.invokestatic(this.classModel.getMethodRefIndex(this.methodName, this.descriptor));
	}

	protected void emitThrowRuntimeException(ByteOutStream o, String message) {
		o.ldc(this.classModel.getStringIndex(message));
		this.libraryManager.getMethod(MethodEnum.THROW_RUNTIME_EXCEPTION).emitCall(o);
	}

	protected void emitPrint(ByteOutStream o, String message) {
		o.ldc(this.classModel.getStringIndex(message));
		this.libraryManager.getMethod(MethodEnum.PRINT_STRING_FROM_STACK).emitCall(o);
	}

	public void addMethod() {
		ByteOutStream o = new ByteOutStream(ClassModel.MAX_METHOD_LENGTH);

		List<ExceptionTableInfo> exInfo = new ArrayList<ExceptionTableInfo>();
		addMethodBytecode(o, exInfo);

		o.flushAndCloseGracefully();
		this.classModel.addMethod(this.methodName, this.descriptor, this.numLocals, o.toByteArray(), exInfo.toArray(new ExceptionTableInfo[0]));
	}

	// implemented by subclasses
	public abstract void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e);
}
