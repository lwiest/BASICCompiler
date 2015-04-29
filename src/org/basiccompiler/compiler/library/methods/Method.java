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

package org.basiccompiler.compiler.library.methods;

import java.util.ArrayList;
import java.util.List;

import org.basiccompiler.bytecode.ClassModel;
import org.basiccompiler.bytecode.info.ExceptionTableInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;
import org.basiccompiler.compiler.library.LibraryManager;
import org.basiccompiler.compiler.library.LibraryManager.MethodEnum;

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
		addMethodByteCode(o, exInfo);
		
		o.flushAndCloseGracefully();
		this.classModel.addMethod(this.methodName, this.descriptor, this.numLocals, o.toByteArray());
	}

	// implemented by subclasses
	public abstract void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e);
}
