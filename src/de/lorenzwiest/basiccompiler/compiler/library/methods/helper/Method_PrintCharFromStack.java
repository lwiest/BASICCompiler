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

package de.lorenzwiest.basiccompiler.compiler.library.methods.helper;

import static de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaField.SYSTEM_OUT;
import static de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaMethod.PRINT_STREAM_PRINT;

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_PrintCharFromStack extends Method {
	private final static String METHOD_NAME = "PrintCharFromStack";
	private final static String DESCRIPTOR = "(C)V";
	private final static int NUM_LOCALS = 3;

	public Method_PrintCharFromStack(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {
		final int TAB_ZONE_WIDTH = 14;

		// local 0: C char to be printed
		// local 1: I num spaces to next print zone
		// local 2: I for index

		int posFieldRef = this.classModel.addFieldAndGetFieldRefIndex(Compiler.FIELD_CURSOR_POS, "I");

		o.iload_0();
		o.iconst('\t');
		o.if_icmpne("checkCR");

		// local 1 := (pos / TAB_ZONE_WIDTH + 1) * TAB_ZONE_WIDTH - pos

		o.getstatic(posFieldRef);
		o.iconst(TAB_ZONE_WIDTH);
		o.idiv();
		o.iconst_1();
		o.iadd();
		o.iconst(TAB_ZONE_WIDTH);
		o.imul();
		o.getstatic(posFieldRef);
		o.isub();
		o.istore_1();

		o.iconst_0();
		o.istore_2();
		o.goto_("loopCond");

		o.label("loop");
		o.getstatic(this.classModel.getJavaFieldRefIndex(SYSTEM_OUT));
		o.iconst(' ');
		o.invokevirtual(this.classModel.getJavaMethodRefIndex(PRINT_STREAM_PRINT));

		o.getstatic(posFieldRef);
		o.iconst_1();
		o.iadd();
		o.putstatic(posFieldRef);

		o.iinc(2, 1);

		o.label("loopCond");
		o.iload_2();
		o.iload_1();
		o.if_icmplt("loop");

		o.return_();

		// handle \n

		o.label("checkCR");
		o.iload_0();
		o.iconst('\n');
		o.if_icmpne("checkLF");

		o.iconst_0();
		o.putstatic(posFieldRef);
		o.goto_("print");

		// handle \r

		o.label("checkLF");
		o.iload_0();
		o.iconst('\r');
		o.if_icmpne("checkText");

		o.iconst_0();
		o.putstatic(posFieldRef);
		o.goto_("print");

		// handle the rest

		o.label("checkText");
		o.getstatic(posFieldRef);
		o.iconst_1();
		o.iadd();
		o.putstatic(posFieldRef);

		o.label("print");
		o.getstatic(this.classModel.getJavaFieldRefIndex(SYSTEM_OUT));
		o.iload_0();
		o.invokevirtual(this.classModel.getJavaMethodRefIndex(PRINT_STREAM_PRINT));
		o.return_();
	}
}
