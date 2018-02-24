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

import static de.lorenzwiest.basiccompiler.compiler.Compiler.CR;

import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager.MethodEnum;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Input extends Method {
	private final static String METHOD_NAME = "Input";
	private final static String DESCRIPTOR = "([C)[[C";
	private final static int NUM_LOCALS = 11;

	public Method_Input(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		// SOURCE CODE (PSEUDO CODE)
		//
		//  public static char[][] input(char[] argTypes) {
		//    int argLen = argTypes.length;
		//    char[][] resultArray = new char[argLen][0];
		//
		//    // readLoop:
		//    char[] inputChrs = readline();
		//
		//    int argIndex = 0;
		//    int start = -1;
		//    int end = -1;
		//    boolean isQuoting = false;
		//
		//    int UNDEFINED = 0;
		//    int QUOTE     = 1;
		//    int VERBATIM  = 2;
		//    int dataType = UNDEFINED;
		//
		//    for (int i = 0; i < inputChrs.length; i++) {
		//      char chr = inputChrs[i];
		//      if ((chr == ' ') || (chr == '\t')) {
		//        if (isQuoting == false) {
		//          continue;
		//        }
		//      }
		//      if ((chr == ',') && (isQuoting == false)) {
		//        if (argIndex >= argLen) {
		//          return error("Too many parsed args");
		//        }
		//        resultArray[argIndex++] = substring(inputChrs, start, end);
		//        start = -1;
		//        end = -1;
		//        dataType = UNDEFINED;
		//        continue;
		//      }
		//      if (chr == '"') {
		//        if (dataType == UNDEFINED) {
		//          dataType = QUOTE;
		//          isQuoting = true;
		//          continue;
		//        } else if (isQuoting) {
		//          isQuoting = false;
		//          continue;
		//        }
		//      }
		//      if ((dataType == QUOTE) && (isQuoting == false)) {
		//        return error(BAD_CHARACTER_AFTER_QUOTE);
		//      }
		//      if (dataType == UNDEFINED) {
		//        dataType = VERBATIM;
		//      }
		//      if (start == -1) {
		//        start = i;
		//      }
		//      end = i + 1;
		//    }
		//
		//    if (argIndex >= argLen) {
		//      return error("Too many parsed args");
		//    }
		//    resultArray[argIndex++] = substring(inputChrs, start, end);
		//    if (argIndex != argLen) {
		//      return error("Too few args parsed");
		//    }
		//
		//    for (int i = 0; i < argLen; i++) {
		//      if (argTypes[i] == 'F') {
		//        if (resultArray[i].length != 0) {
		//        	try {
		//        		Float.parseFloat(String.valueOf(resultArray[i]));
		//        	} catch (NumberFormatException e) {
		//          	return error("Is not a number");
		//					}
		//        }
		//      }
		//    }
		//    return resultArray;
		//  }
		//
		//  private static char[] substring(char[] chrs, int start, int end) {
		//    int len = end - start;
		//    char[] result = new char[len];
		//    for (int i = 0; i < len; i++) {
		//      result[i] = chrs[start + i];
		//    }
		//    return result;
		//  }
		//
		//  private static char[][] error(String message) {
		//    char[][] result = new char[1][];
		//    result[0] = message.toCharArray();
		//    return result;
		//  }

		// out-of-order indexing optimizes bytecode length of method

		final int ARG_TYPES = 0;       // local 0:  [C  input argument type string, for example "FSF"
		final int ARG_INDEX = 1;       // local 1:  I   argument index
		final int ARG_LEN = 5;         // local 5:  I   argument length
		final int RESULT_ARRAY = 8;    // local 8:  [[C returned string array
		final int INPUT_CHRS = 4;      // local 4:  [C  entered string
		final int I = 2;               // local 2:  I   loop index
		final int CHR = 6;             // local 6:  I   single character of entered string
		final int DATA_TYPE = 7;       // local 7:  I   type of input chunk

		final int UNDEFINED = 0;
		final int QUOTE = 1;
		final int VERBATIM = 2;

		final int IS_QUOTING = 3;      // local 3:  I   is in quoting mode?
		final int START = 9;           // local 9:  I   start input index, inclusive
		final int END = 10;            // local 10: I   end input index, exclusive

		//  int argLen = argTypes.length;

		o.aload_opt(ARG_TYPES);
		o.arraylength();
		o.istore_opt(ARG_LEN);

		//  char[][] resultArray = new char[argLen][0];

		o.iload_opt(ARG_LEN);
		o.iconst_0();
		o.multianewarray(this.classModel.getClassIndex("[[C"), 2);
		o.astore_opt(RESULT_ARRAY);

		//  // readLoop:
		//  char[] inputChrs = readLine();

		o.label("readLoop");
		this.libraryManager.getMethod(MethodEnum.READ_CHARS_TO_STACK).emitCall(o);
		o.astore_opt(INPUT_CHRS);

		//  int argIndex = 0;

		o.iconst_0();
		o.istore_opt(ARG_INDEX);

		//  int start = -1;
		//  int end = -1;
		//  boolean isQuoting = false;

		o.iconst_m1();
		o.istore_opt(START);
		o.iconst_m1();
		o.istore_opt(END);
		o.iconst_0();
		o.istore_opt(IS_QUOTING);

		//  int UNDEFINED = 0;
		//  int QUOTE     = 1;
		//  int VERBATIM  = 2;
		//  int datatype = UNDEFINED;

		o.iconst(UNDEFINED);
		o.istore_opt(DATA_TYPE);

		//  for (int i = 0; i < input.length(); i++) {

		o.iconst_0();
		o.istore_opt(I);
		o.goto_("loopCond");

		o.label("loop");

		//    char c = inputChrs[i];

		o.aload_opt(INPUT_CHRS);
		o.iload_opt(I);
		o.caload();
		o.istore_opt(CHR);

		//    if ((c == ' ') || (c == '\t')) {
		//      if (isQuoting == false) {
		//        continue;
		//      }
		//    }

		o.iload_opt(CHR);
		o.iconst(' ');
		o.if_icmpeq("gotBlank");

		o.iload_opt(CHR);
		o.iconst('\t');
		o.if_icmpne("noWhitespace");

		o.label("gotBlank");
		o.iload_opt(IS_QUOTING);
		o.ifeq("continueLoop");

		o.label("noWhitespace");

		//    if ((chr == ',') && (isQuoting == false)) {
		//      if (argIndex >= argLen) {
		//        return error("Too many parsed args");
		//      }
		//
		//      resultArray[argIndex++] = substring(inputChrs, start, end);
		//
		//      start = -1;
		//      end = -1;
		//      dataType = UNDEFINED;
		//      continue;
		//    }

		o.iload_opt(CHR);
		o.iconst(',');
		o.if_icmpne("noComma");

		o.iload_opt(IS_QUOTING);
		o.ifne("noComma");

		o.iload_opt(ARG_INDEX);
		o.iload_opt(ARG_LEN);
		o.if_icmpge("tooManyParsedArgs");

		o.aload_opt(RESULT_ARRAY);
		o.iload_opt(ARG_INDEX);
		o.aload_opt(INPUT_CHRS);
		o.iload_opt(START);
		o.iload_opt(END);
		this.libraryManager.getMethod(MethodEnum.SUBSTRING).emitCall(o);
		o.aastore();

		o.iinc(ARG_INDEX, 1);

		o.iconst_m1();
		o.istore_opt(START);
		o.iconst_m1();
		o.istore_opt(END);
		o.iconst(UNDEFINED);
		o.istore_opt(DATA_TYPE);
		o.goto_("continueLoop");

		o.label("noComma");

		//    if (c == '"') {
		//      if (dataType == UNDEFINED) {
		//        dataType = QUOTE;
		//        isQuoting = true;
		//        continue;
		//      } else if (isQuoting) {
		//        isQuoting = false;
		//        continue;
		//      }
		//    }

		o.iload_opt(CHR);
		o.iconst('"');
		o.if_icmpne("noQuote");

		o.iload_opt(DATA_TYPE);
		o.iconst(UNDEFINED);
		o.if_icmpne("notUndefined");

		o.iconst(QUOTE);
		o.istore_opt(DATA_TYPE);
		o.iconst_1();
		o.istore_opt(IS_QUOTING);
		o.goto_("continueLoop");

		o.label("notUndefined");
		o.iload_opt(IS_QUOTING);
		o.ifeq("noQuote");

		o.iconst_0();
		o.istore_opt(IS_QUOTING);
		o.goto_("continueLoop");

		o.label("noQuote");

		//        if ((dataType == QUOTE) && (isQuoting == false)) {
		//          return error(BAD_CHARACTER_AFTER_QUOTE);
		//        }

		o.iload_opt(DATA_TYPE);
		o.iconst(QUOTE);
		o.if_icmpne("noBadCharacterAfterQuoting");

		o.iload_opt(IS_QUOTING);
		o.ifeq("badCharacterAfterQuoting");

		o.label("noBadCharacterAfterQuoting");

		//      if (dataType == UNDEFINED) {
		//        dataType = VERBATIM;
		//      }
		//

		o.iload_opt(DATA_TYPE);
		o.iconst(UNDEFINED);
		o.if_icmpne("notUndefined2");

		o.iconst(VERBATIM);
		o.istore_opt(DATA_TYPE);

		o.label("notUndefined2");

		//      if (start == -1) {
		//        start = i;
		//      }
		//      end = i + 1;

		o.iload_opt(START);
		o.iconst_m1();
		o.if_icmpne("skipStartInit");

		o.iload_opt(I);
		o.istore_opt(START);

		o.label("skipStartInit");
		o.iload_opt(I);
		o.iconst_1();
		o.iadd();
		o.istore_opt(END);

		//    }

		o.label("continueLoop");
		o.iinc(I, 1);

		o.label("loopCond");
		o.iload_opt(I);
		o.aload_opt(INPUT_CHRS);
		o.arraylength();
		o.if_icmplt("loop");

		//    if (argIndex >= argLen) {
		//      return error("Too many parsed args");
		//    }

		o.iload_opt(ARG_INDEX);
		o.iload_opt(ARG_LEN);
		o.if_icmpge("tooManyParsedArgs");

		//    resultArray[argIndex++] = substring(inputChrs, start, end);

		o.aload_opt(RESULT_ARRAY);
		o.iload_opt(ARG_INDEX);
		o.aload_opt(INPUT_CHRS);
		o.iload_opt(START);
		o.iload_opt(END);
		this.libraryManager.getMethod(MethodEnum.SUBSTRING).emitCall(o);
		o.aastore();

		o.iinc(ARG_INDEX, 1);

		//    if (argIndex != argLen) {
		//      return error("Too few args parsed");
		//    }

		o.iload_opt(ARG_INDEX);
		o.iload_opt(ARG_LEN);
		o.if_icmpne("tooFewArgsParsed");

		//    for (int i = 0; i < argLen; i++) {
		//      if (argTypes[i] == 'F') {
		//        if (resultArray[i].length != 0) {
		//          try {
		//            Float.parseFloat(String.valueOf(resultArray[i]));
		//          } catch (NumberFormatException e) {
		//            return error("Is not a number");
		//          }
		//        }
		//      }
		//    }

		o.iconst_0();
		o.istore_opt(I);
		o.goto_("checkArgTypesLoopCond");

		o.label("checkArgTypesLoop");
		o.aload_opt(ARG_TYPES);
		o.iload_opt(I);
		o.caload();
		o.iconst('F');
		o.if_icmpne("skipFloatCheck");

		o.aload_opt(RESULT_ARRAY); // skip float check of empty string
		o.iload_opt(I);
		o.aaload();
		o.arraylength();
		o.ifeq("skipFloatCheck");

		o.aload_opt(RESULT_ARRAY);
		o.iload_opt(I);
		o.aaload();
		this.libraryManager.getMethod(MethodEnum.CHARS_TO_FLOAT).emitCall(o);
		o.dup();
		o.fcmpg();
		o.ifne("isNaN");

		o.label("skipFloatCheck");
		o.iinc(I, 1);

		o.label("checkArgTypesLoopCond");
		o.iload_opt(I);
		o.iload_opt(ARG_LEN);
		o.if_icmplt("checkArgTypesLoop");

		// return resultArray;

		o.aload_opt(RESULT_ARRAY);
		o.areturn();

		// error handling

		o.label("tooManyParsedArgs");
		o.label("tooFewArgsParsed");
		o.label("badCharacterAfterQuoting");
		o.label("isNaN");

		emitPrint(o, "?Redo from start" + CR);
		o.goto_("readLoop");
	}
}
