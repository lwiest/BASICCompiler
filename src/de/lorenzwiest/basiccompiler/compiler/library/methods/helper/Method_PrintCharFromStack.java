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
