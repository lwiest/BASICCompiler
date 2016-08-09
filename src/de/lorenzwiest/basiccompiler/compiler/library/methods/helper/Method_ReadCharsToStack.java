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

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaClass;
import de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaField;
import de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaMethod;
import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_ReadCharsToStack extends Method {
	private final static String METHOD_NAME = "ReadCharsToStack";
	private final static String DESCRIPTOR = "()[C";
	private final static int NUM_LOCALS = 5;

	public Method_ReadCharsToStack(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

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
