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

package org.basiccompiler.compiler.library.methods.helper;

import java.util.List;

import org.basiccompiler.bytecode.info.ExceptionTableInfo;
import org.basiccompiler.compiler.Compiler;
import org.basiccompiler.compiler.etc.ByteOutStream;
import org.basiccompiler.compiler.library.LibraryManager;
import org.basiccompiler.compiler.library.methods.Method;

public class Method_ReadStringFromDataToStack extends Method {
  private final static String METHOD_NAME = "ReadStringFromDataToStack";
  private final static String DESCRIPTOR = "()[C";
  private final static int NUM_LOCALS = 4;

  public Method_ReadStringFromDataToStack(LibraryManager libraryManager) {
    super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
  }

  @Override
  public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

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
