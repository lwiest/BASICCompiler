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

import de.lorenzwiest.basiccompiler.classfile.ClassModel.JavaClass;
import de.lorenzwiest.basiccompiler.classfile.ClassModel.JavaField;
import de.lorenzwiest.basiccompiler.classfile.ClassModel.JavaMethod;
import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_ReadCharsToStack extends Method {
	private static final String METHOD_NAME = "ReadCharsToStack";
	private static final String DESCRIPTOR = "()[C";
	private static final int NUM_LOCALS = 5;

	public Method_ReadCharsToStack(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// PSEUDO SOURCE CODE
		//
		//  byte[] buffer = new byte[255];
		//
		//  int numBytes = -1;
		//  try {
		//  readloop:
		//    numBytes = System.in.read(buffer);
		//    // set cursor position to 0
		//  } catch (IOException e) {
		//    goto readLoop
		//  }
		//  if (numBytes < 0) {
		//    goto readLoop
		//  }
		//
		//  for (int len = 0; len < numBytes; len++) {
		//    if ((buffer[len] == '\n') || (buffer[len] == '\r')) {
		//      break;
		//    }
		//    len++;
		//  }
		//
		//  char[] input = new char[len];
		//  for (int i = 0; i < len; i++) {
		//    input[i] = (char) (buffer[i] & 0xff);
		//  }
		//  return input;

		final int BUFFER = 0;    // [B buffer
		final int NUM_BYTES = 1; // I  numBytes
		final int LEN = 2;       // I  len
		final int I = 3;         // I  i
		final int INPUT = 4;     // [C input

		//  byte[] buffer = new byte[255];

		o.iconst(255);
		o.newarray_byte();
		o.astore_opt(BUFFER);

		//  int numBytes = -1;

		o.iconst_m1();
		o.istore_opt(NUM_BYTES);

		//  try {
		//  readloop:

		int posTryBegin = o.pos();
		o.label("readLoop");

		//    numBytes = System.in.read(buffer);

		o.getstatic(this.classModel.getJavaFieldRefIndex(JavaField.SYSTEM_IN));
		o.aload_opt(BUFFER);
		o.invokevirtual(this.classModel.getJavaMethodRefIndex(JavaMethod.INPUT_STREAM_READ));
		o.istore_opt(NUM_BYTES);

		//    // set cursor position to 0

		o.iconst_0();
		int posFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_CURSOR_POS, "I"); // TODO: refactor to getRuntimeFieldRefIndex(<enum>)
		o.putstatic(posFieldRef);

		//  } catch (IOException e) {
		//    goto readLoop
		//  }

		int posTryEnd = o.pos();

		//  if (numBytes < 0) {
		//    goto readLoop
		//  }

		o.iload_opt(NUM_BYTES);
		o.ifeq("readLoop");

		//  for (int len = 0; len < numBytes; len++) {
		//    if ((buffer[len] == '\n') || (buffer[len] == '\r')) {
		//      break;
		//    }
		//    len++;
		//  }

		o.iconst_0();
		o.istore_opt(LEN);
		o.goto_("lenLoopCond");

		o.label("lenLoop");
		o.aload_opt(BUFFER);
		o.iload_opt(LEN);
		o.baload();
		o.bipush('\r');
		o.if_icmpeq("afterLenLoop");

		o.aload_opt(BUFFER);
		o.iload_opt(LEN);
		o.baload();
		o.bipush('\n');
		o.if_icmpeq("afterLenLoop");

		o.iinc(LEN, 1);

		o.label("lenLoopCond");
		o.iload_opt(LEN);
		o.iload_opt(NUM_BYTES);
		o.if_icmplt("lenLoop");

		o.label("afterLenLoop");

		//  char[] input = new char[len];

		o.iload_opt(LEN);
		o.newarray_char();
		o.astore_opt(INPUT);

		//  for (int i = 0; i < len; i++) {
		//    input[i] = (char) (buffer[i] & 0xff);
		//  }

		o.iconst_0();
		o.istore_opt(I);
		o.goto_("copyLoopCond");

		o.label("copyLoop");
		o.aload_opt(INPUT);
		o.iload_opt(I);

		o.aload_opt(BUFFER);
		o.iload_opt(I);
		o.baload();
		o.sipush(0x00ff);
		o.iand();
		o.i2c();
		o.castore();

		o.iinc(I, 1);

		o.label("copyLoopCond");
		o.iload_opt(I);
		o.iload_opt(LEN);
		o.if_icmplt("copyLoop");

		//  return input;

		o.aload_opt(INPUT);
		o.areturn();

		// exception handler

		int posCatch = o.pos();
		o.pop(); // throw away exception class reference
		o.goto_("readLoop");

		// create exception table

		e.add(new ExceptionTableInfo(posTryBegin, posTryEnd, posCatch, this.classModel.getJavaClassRefIndex(JavaClass.RUNTIME_EXCEPTION)));
	}
}
