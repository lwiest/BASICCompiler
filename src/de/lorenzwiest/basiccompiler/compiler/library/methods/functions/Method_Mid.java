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
