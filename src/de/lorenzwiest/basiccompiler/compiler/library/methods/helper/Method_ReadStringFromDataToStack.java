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

public class Method_ReadStringFromDataToStack extends Method {
	private final static String METHOD_NAME = "ReadStringFromDataToStack";
	private final static String DESCRIPTOR = "()[C";
	private final static int NUM_LOCALS = 4;

	public Method_ReadStringFromDataToStack(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodBytecode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// SOURCE CODE (PSEUDO CODE)
		//
		// public static int dataIndex = ...;    // current data index
		// public static char[] dataInfo = ...;  // data info array (contains a pair of char index and char length for every data element)
		// public static char[] data = ...;      // data
		//
		// char[] readStringFromData() {
		//   if (dataIndex >= (dataInfo.length / 2)) {
		//     throw new RuntimeException("Out of data.");
		//   }
		//
		//   int dataElementOffset = dataIndex * 2;
		//   int dataElementIndex = dataInfo[dataElementOffset];
		//   int dataElementLength = dataInfo[dataElementOffset + 1];
		//   char[] dataElementChars = new char[dataElementLength];
		//
		//   dataElementLength--;
		//   dataElementIndex += dataElementLength;
		//
		//   while (dataElementLength >= 0) {
		//     dataElementChars[dataElementLength] = data[dataElementIndex];
		//     dataElementLength--;
		//     dataElementIndex--;
		//   }
		//
		//   dataIndex++;
		//
		//   return dataElementChars;
		// }

		final int DATA_ELEMENT_INDEX = 0;   // local 0: I  data element index
		final int DATA_ELEMENT_LENGTH = 1;  // local 1: I  data element length
		final int DATA_ELEMENT_CHARS = 2;   // local 2: [C data element chars
		final int DATA_ELEMENT_OFFSET = 3;  // local 3: I  data element offset

		int dataIndexFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_DATA_INDEX, "I");
		int dataInfoFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_DATA_INFO, "[C");
		int dataFieldRef = this.classModel.getFieldRefIndex(Compiler.FIELD_DATA, "[C");

		// public static int dataIndex = ...;    // current data index
		// public static char[] dataInfo = ...;  // data info array (contains a pair of char index and char length for every data element)
		// public static char[] data = ...;      // data

		// if (dataIndex >= (dataInfo.length / 2)) {
		//   throw new RuntimeException("Out of data.");
		// }

		o.getstatic(dataIndexFieldRef);
		o.getstatic(dataInfoFieldRef);
		o.arraylength();
		o.iconst_1();
		o.ishr();
		o.if_icmplt("not out of data");

		emitThrowRuntimeException(o, "Out of data.");

		o.label("not out of data");

		// int dataElementOffset = dataIndex * 2;

		o.getstatic(dataIndexFieldRef);
		o.iconst_1();
		o.ishl();
		o.istore_opt(DATA_ELEMENT_OFFSET);

		// int dataElementIndex = dataInfo[dataElementOffset];

		o.getstatic(dataInfoFieldRef);
		o.iload_opt(DATA_ELEMENT_OFFSET);
		o.caload();
		o.istore_opt(DATA_ELEMENT_INDEX);

		// int dataElementLength = dataInfo[dataElementOffset + 1];

		o.getstatic(dataInfoFieldRef);
		o.iload_opt(DATA_ELEMENT_OFFSET);
		o.iconst_1();
		o.iadd();
		o.caload();
		o.istore_opt(DATA_ELEMENT_LENGTH);

		// char[] dataElementChars = new char[dataElementLength];

		o.iload_opt(DATA_ELEMENT_LENGTH);
		o.newarray_char();
		o.astore_opt(DATA_ELEMENT_CHARS);

		// dataElementLength--;

		o.iinc(DATA_ELEMENT_LENGTH, -1);

		// dataElementIndex += dataElementLength;

		o.iload_opt(DATA_ELEMENT_INDEX);
		o.iload_opt(DATA_ELEMENT_LENGTH);
		o.iadd();
		o.istore_opt(DATA_ELEMENT_INDEX);

		// while (dataElementLength >= 0) {
		//   dataElementChars[dataElementLength] = data[dataElementIndex];
		//   dataElementLength--;
		//   dataElementIndex--;
		// }

		o.goto_("loopCond");

		o.label("loop");

		o.aload_opt(DATA_ELEMENT_CHARS);
		o.iload_opt(DATA_ELEMENT_LENGTH);
		o.getstatic(dataFieldRef);
		o.iload_opt(DATA_ELEMENT_INDEX);
		o.caload();
		o.castore();

		o.iinc(DATA_ELEMENT_LENGTH, -1);
		o.iinc(DATA_ELEMENT_INDEX, -1);

		o.label("loopCond");
		o.iload_opt(DATA_ELEMENT_LENGTH);
		o.ifge("loop");

		// dataIndex++;

		o.getstatic(dataIndexFieldRef);
		o.iconst_1();
		o.iadd();
		o.putstatic(dataIndexFieldRef);

		// return dataElementChars;

		o.aload_opt(DATA_ELEMENT_CHARS);
		o.areturn();
	}
}
