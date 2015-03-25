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

package org.basiccompiler.compiler.library;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.basiccompiler.bytecode.ClassModel;
import org.basiccompiler.compiler.library.methods.Method;
import org.basiccompiler.compiler.library.methods.arrays.Method_Check1DFloatArrayAccess;
import org.basiccompiler.compiler.library.methods.arrays.Method_Check1DStringArrayAccess;
import org.basiccompiler.compiler.library.methods.arrays.Method_Check2DFloatArrayAccess;
import org.basiccompiler.compiler.library.methods.arrays.Method_Check2DStringArrayAccess;
import org.basiccompiler.compiler.library.methods.arrays.Method_Dim1DCheckSize;
import org.basiccompiler.compiler.library.methods.arrays.Method_Dim1DFloatArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_Dim1DStringArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_Dim2DCheckSize;
import org.basiccompiler.compiler.library.methods.arrays.Method_Dim2DFloatArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_Dim2DStringArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_LoadFloatFrom1DArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_LoadFloatFrom2DArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_LoadStringFrom1DArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_LoadStringFrom2DArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_StoreFloatIn1DArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_StoreFloatIn2DArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_StoreStringIn1DArray;
import org.basiccompiler.compiler.library.methods.arrays.Method_StoreStringIn2DArray;
import org.basiccompiler.compiler.library.methods.functions.Method_Abs;
import org.basiccompiler.compiler.library.methods.functions.Method_Asc;
import org.basiccompiler.compiler.library.methods.functions.Method_Atn;
import org.basiccompiler.compiler.library.methods.functions.Method_Chr;
import org.basiccompiler.compiler.library.methods.functions.Method_Cos;
import org.basiccompiler.compiler.library.methods.functions.Method_Exp;
import org.basiccompiler.compiler.library.methods.functions.Method_Fix;
import org.basiccompiler.compiler.library.methods.functions.Method_Instr;
import org.basiccompiler.compiler.library.methods.functions.Method_Int;
import org.basiccompiler.compiler.library.methods.functions.Method_Left;
import org.basiccompiler.compiler.library.methods.functions.Method_Len;
import org.basiccompiler.compiler.library.methods.functions.Method_Log;
import org.basiccompiler.compiler.library.methods.functions.Method_Mid;
import org.basiccompiler.compiler.library.methods.functions.Method_Pos;
import org.basiccompiler.compiler.library.methods.functions.Method_Right;
import org.basiccompiler.compiler.library.methods.functions.Method_Rnd;
import org.basiccompiler.compiler.library.methods.functions.Method_Sgn;
import org.basiccompiler.compiler.library.methods.functions.Method_Sin;
import org.basiccompiler.compiler.library.methods.functions.Method_Space;
import org.basiccompiler.compiler.library.methods.functions.Method_Spc;
import org.basiccompiler.compiler.library.methods.functions.Method_Sqr;
import org.basiccompiler.compiler.library.methods.functions.Method_Str;
import org.basiccompiler.compiler.library.methods.functions.Method_Tab;
import org.basiccompiler.compiler.library.methods.functions.Method_Tan;
import org.basiccompiler.compiler.library.methods.functions.Method_Val;
import org.basiccompiler.compiler.library.methods.helper.Method_CharsToFloat;
import org.basiccompiler.compiler.library.methods.helper.Method_CheckLogicalOperatorArguments;
import org.basiccompiler.compiler.library.methods.helper.Method_CheckOnGotoGosubArg;
import org.basiccompiler.compiler.library.methods.helper.Method_FloatToChars;
import org.basiccompiler.compiler.library.methods.helper.Method_GosubStackInitialize;
import org.basiccompiler.compiler.library.methods.helper.Method_GosubStackPop;
import org.basiccompiler.compiler.library.methods.helper.Method_GosubStackPush;
import org.basiccompiler.compiler.library.methods.helper.Method_Input;
import org.basiccompiler.compiler.library.methods.helper.Method_PrintCharFromStack;
import org.basiccompiler.compiler.library.methods.helper.Method_PrintCharsFromStack;
import org.basiccompiler.compiler.library.methods.helper.Method_PrintFloatFromStack;
import org.basiccompiler.compiler.library.methods.helper.Method_PrintStringFromStack;
import org.basiccompiler.compiler.library.methods.helper.Method_ReadCharsToStack;
import org.basiccompiler.compiler.library.methods.helper.Method_ReadNumFromDataToStack;
import org.basiccompiler.compiler.library.methods.helper.Method_ReadStringFromDataToStack;
import org.basiccompiler.compiler.library.methods.helper.Method_RoundToInt;
import org.basiccompiler.compiler.library.methods.helper.Method_StringToChars;
import org.basiccompiler.compiler.library.methods.helper.Method_Substring;
import org.basiccompiler.compiler.library.methods.helper.Method_ThrowRuntimeException;
import org.basiccompiler.compiler.library.methods.operators.Method_And;
import org.basiccompiler.compiler.library.methods.operators.Method_Division;
import org.basiccompiler.compiler.library.methods.operators.Method_DivisionByZero;
import org.basiccompiler.compiler.library.methods.operators.Method_IntegerDivision;
import org.basiccompiler.compiler.library.methods.operators.Method_Mod;
import org.basiccompiler.compiler.library.methods.operators.Method_Not;
import org.basiccompiler.compiler.library.methods.operators.Method_Or;
import org.basiccompiler.compiler.library.methods.operators.Method_Power;
import org.basiccompiler.compiler.library.methods.operators.Method_StringConcatenation;
import org.basiccompiler.compiler.library.methods.operators.Method_StringEqual;
import org.basiccompiler.compiler.library.methods.operators.Method_StringGreaterOrEqual;
import org.basiccompiler.compiler.library.methods.operators.Method_StringGreaterThan;
import org.basiccompiler.compiler.library.methods.operators.Method_StringLessOrEqual;
import org.basiccompiler.compiler.library.methods.operators.Method_StringLessThan;
import org.basiccompiler.compiler.library.methods.operators.Method_StringNotEqual;
import org.basiccompiler.compiler.library.methods.operators.Method_Xor;

public class LibraryManager {
	private final ClassModel classModel;
	private final Map<MethodEnum, Method> methodMap = new HashMap<MethodEnum, Method>();
	private final LinkedList<MethodEnum> usedMethods = new LinkedList<MethodEnum>();

	public static enum MethodEnum {
		ABS,
		AND,
		ASC,
		ATN,
		CHARS_TO_FLOAT,
		CHECK_1D_FLOAT_ARRAY_ACCESS,
		CHECK_1D_STRING_ARRAY_ACCESS,
		CHECK_2D_FLOAT_ARRAY_ACCESS,
		CHECK_2D_STRING_ARRAY_ACCESS,
		CHECK_LOGICAL_OPERATION_ARGUMENTS,
		CHECK_ON_GOTO_GOSUB_ARG,
		CHR,
		COS,
		DIM_1D_CHECK_SIZE,
		DIM_1D_FLOAT_ARRAY,
		DIM_1D_STRING_ARRAY,
		DIM_2D_CHECK_SIZE,
		DIM_2D_FLOAT_ARRAY,
		DIM_2D_STRING_ARRAY,
		DIVISION,
		DIVISION_BY_ZERO,
		EXP,
		FIX,
		FLOAT_TO_CHARS,
		GOSUB_STACK_INITIALIZE,
		GOSUB_STACK_POP,
		GOSUB_STACK_PUSH,
		INSTR,
		INT,
		INTEGER_DIVISION,
		INPUT,
		LEFT,
		LEN,
		LOAD_FLOAT_FROM_1D_ARRAY,
		LOAD_FLOAT_FROM_2D_ARRAY,
		LOAD_STRING_FROM_1D_ARRAY,
		LOAD_STRING_FROM_2D_ARRAY,
		LOG,
		MID,
		MOD,
		NOT,
		OR,
		POS,
		POWER,
		PRINT_CHAR_FROM_STACK,
		PRINT_CHARS_FROM_STACK,
		PRINT_FLOAT_FROM_STACK,
		PRINT_STRING_FROM_STACK,
		READ_CHARS_TO_STACK,
		READ_NUM_FROM_DATA_TO_STACK,
		READ_STRING_FROM_DATA_TO_STACK,
		RIGHT,
		RND,
		ROUND_TO_INT,
		SGN,
		SIN,
		SPACE,
		SPC,
		SQR,
		STORE_FLOAT_IN_1D_ARRAY,
		STORE_FLOAT_IN_2D_ARRAY,
		STORE_STRING_IN_1D_ARRAY,
		STORE_STRING_IN_2D_ARRAY,
		STR,
		STRING_CONCATENATION,
		STRING_EQUAL,
		STRING_GREATER_OR_EQUAL,
		STRING_GREATER_THAN,
		STRING_LESS_OR_EQUAL,
		STRING_LESS_THAN,
		STRING_NOT_EQUAL,
		STRING_TO_CHARS,
		SUBSTRING,
		TAB,
		TAN,
		THROW_RUNTIME_EXCEPTION,
		VAL,
		XOR; //
	}

	public LibraryManager(ClassModel classModel) {
		this.classModel = classModel;
	}

	public ClassModel getClassModel() {
		return this.classModel;
	}

	public Method getMethod(MethodEnum m) { // TODO: can we simplify this? Via reflection? Elegantly?
		if (this.methodMap.containsKey(m) == false) {
			if (m == MethodEnum.PRINT_FLOAT_FROM_STACK) {
				this.methodMap.put(m, new Method_PrintFloatFromStack(this));
			} else if (m == MethodEnum.READ_CHARS_TO_STACK) {
				this.methodMap.put(m, new Method_ReadCharsToStack(this));
			} else if (m == MethodEnum.PRINT_CHARS_FROM_STACK) {
				this.methodMap.put(m, new Method_PrintCharsFromStack(this));
			} else if (m == MethodEnum.PRINT_STRING_FROM_STACK) {
				this.methodMap.put(m, new Method_PrintStringFromStack(this));
			} else if (m == MethodEnum.PRINT_CHAR_FROM_STACK) {
				this.methodMap.put(m, new Method_PrintCharFromStack(this));
			} else if (m == MethodEnum.STRING_TO_CHARS) {
				this.methodMap.put(m, new Method_StringToChars(this));
			} else if (m == MethodEnum.SQR) {
				this.methodMap.put(m, new Method_Sqr(this));
			} else if (m == MethodEnum.TAB) {
				this.methodMap.put(m, new Method_Tab(this));
			} else if (m == MethodEnum.INT) {
				this.methodMap.put(m, new Method_Int(this));
			} else if (m == MethodEnum.CHECK_1D_FLOAT_ARRAY_ACCESS) {
				this.methodMap.put(m, new Method_Check1DFloatArrayAccess(this));
			} else if (m == MethodEnum.CHECK_2D_FLOAT_ARRAY_ACCESS) {
				this.methodMap.put(m, new Method_Check2DFloatArrayAccess(this));
			} else if (m == MethodEnum.CHECK_1D_STRING_ARRAY_ACCESS) {
				this.methodMap.put(m, new Method_Check1DStringArrayAccess(this));
			} else if (m == MethodEnum.CHECK_2D_STRING_ARRAY_ACCESS) {
				this.methodMap.put(m, new Method_Check2DStringArrayAccess(this));
			} else if (m == MethodEnum.LOAD_FLOAT_FROM_1D_ARRAY) {
				this.methodMap.put(m, new Method_LoadFloatFrom1DArray(this));
			} else if (m == MethodEnum.LOAD_FLOAT_FROM_2D_ARRAY) {
				this.methodMap.put(m, new Method_LoadFloatFrom2DArray(this));
			} else if (m == MethodEnum.LOAD_STRING_FROM_1D_ARRAY) {
				this.methodMap.put(m, new Method_LoadStringFrom1DArray(this));
			} else if (m == MethodEnum.LOAD_STRING_FROM_2D_ARRAY) {
				this.methodMap.put(m, new Method_LoadStringFrom2DArray(this));
			} else if (m == MethodEnum.STORE_FLOAT_IN_1D_ARRAY) {
				this.methodMap.put(m, new Method_StoreFloatIn1DArray(this));
			} else if (m == MethodEnum.STORE_FLOAT_IN_2D_ARRAY) {
				this.methodMap.put(m, new Method_StoreFloatIn2DArray(this));
			} else if (m == MethodEnum.STORE_STRING_IN_1D_ARRAY) {
				this.methodMap.put(m, new Method_StoreStringIn1DArray(this));
			} else if (m == MethodEnum.STORE_STRING_IN_2D_ARRAY) {
				this.methodMap.put(m, new Method_StoreStringIn2DArray(this));
			} else if (m == MethodEnum.DIM_1D_FLOAT_ARRAY) {
				this.methodMap.put(m, new Method_Dim1DFloatArray(this));
			} else if (m == MethodEnum.DIM_2D_FLOAT_ARRAY) {
				this.methodMap.put(m, new Method_Dim2DFloatArray(this));
			} else if (m == MethodEnum.DIM_1D_STRING_ARRAY) {
				this.methodMap.put(m, new Method_Dim1DStringArray(this));
			} else if (m == MethodEnum.DIM_2D_STRING_ARRAY) {
				this.methodMap.put(m, new Method_Dim2DStringArray(this));
			} else if (m == MethodEnum.DIM_1D_CHECK_SIZE) {
				this.methodMap.put(m, new Method_Dim1DCheckSize(this));
			} else if (m == MethodEnum.DIM_2D_CHECK_SIZE) {
				this.methodMap.put(m, new Method_Dim2DCheckSize(this));
			} else if (m == MethodEnum.STRING_CONCATENATION) {
				this.methodMap.put(m, new Method_StringConcatenation(this));
			} else if (m == MethodEnum.STRING_EQUAL) {
				this.methodMap.put(m, new Method_StringEqual(this));
			} else if (m == MethodEnum.STRING_NOT_EQUAL) {
				this.methodMap.put(m, new Method_StringNotEqual(this));
			} else if (m == MethodEnum.STRING_LESS_THAN) {
				this.methodMap.put(m, new Method_StringLessThan(this));
			} else if (m == MethodEnum.STRING_LESS_OR_EQUAL) {
				this.methodMap.put(m, new Method_StringLessOrEqual(this));
			} else if (m == MethodEnum.STRING_GREATER_THAN) {
				this.methodMap.put(m, new Method_StringGreaterThan(this));
			} else if (m == MethodEnum.STRING_GREATER_OR_EQUAL) {
				this.methodMap.put(m, new Method_StringGreaterOrEqual(this));
			} else if (m == MethodEnum.LEFT) {
				this.methodMap.put(m, new Method_Left(this));
			} else if (m == MethodEnum.RIGHT) {
				this.methodMap.put(m, new Method_Right(this));
			} else if (m == MethodEnum.AND) {
				this.methodMap.put(m, new Method_And(this));
			} else if (m == MethodEnum.OR) {
				this.methodMap.put(m, new Method_Or(this));
			} else if (m == MethodEnum.XOR) {
				this.methodMap.put(m, new Method_Xor(this));
			} else if (m == MethodEnum.NOT) {
				this.methodMap.put(m, new Method_Not(this));
			} else if (m == MethodEnum.CHECK_LOGICAL_OPERATION_ARGUMENTS) {
				this.methodMap.put(m, new Method_CheckLogicalOperatorArguments(this));
			} else if (m == MethodEnum.LEN) {
				this.methodMap.put(m, new Method_Len(this));
			} else if (m == MethodEnum.ABS) {
				this.methodMap.put(m, new Method_Abs(this));
			} else if (m == MethodEnum.SGN) {
				this.methodMap.put(m, new Method_Sgn(this));
			} else if (m == MethodEnum.COS) {
				this.methodMap.put(m, new Method_Cos(this));
			} else if (m == MethodEnum.SIN) {
				this.methodMap.put(m, new Method_Sin(this));
			} else if (m == MethodEnum.ATN) {
				this.methodMap.put(m, new Method_Atn(this));
			} else if (m == MethodEnum.ASC) {
				this.methodMap.put(m, new Method_Asc(this));
			} else if (m == MethodEnum.CHR) {
				this.methodMap.put(m, new Method_Chr(this));
			} else if (m == MethodEnum.POS) {
				this.methodMap.put(m, new Method_Pos(this));
			} else if (m == MethodEnum.SPC) {
				this.methodMap.put(m, new Method_Spc(this));
			} else if (m == MethodEnum.SPACE) {
				this.methodMap.put(m, new Method_Space(this));
			} else if (m == MethodEnum.STR) {
				this.methodMap.put(m, new Method_Str(this));
			} else if (m == MethodEnum.VAL) {
				this.methodMap.put(m, new Method_Val(this));
			} else if (m == MethodEnum.FIX) {
				this.methodMap.put(m, new Method_Fix(this));
			} else if (m == MethodEnum.ROUND_TO_INT) {
				this.methodMap.put(m, new Method_RoundToInt(this));
			} else if (m == MethodEnum.LOG) {
				this.methodMap.put(m, new Method_Log(this));
			} else if (m == MethodEnum.EXP) {
				this.methodMap.put(m, new Method_Exp(this));
			} else if (m == MethodEnum.MID) {
				this.methodMap.put(m, new Method_Mid(this));
			} else if (m == MethodEnum.DIVISION) {
				this.methodMap.put(m, new Method_Division(this));
			} else if (m == MethodEnum.INTEGER_DIVISION) {
				this.methodMap.put(m, new Method_IntegerDivision(this));
			} else if (m == MethodEnum.MOD) {
				this.methodMap.put(m, new Method_Mod(this));
			} else if (m == MethodEnum.DIVISION_BY_ZERO) {
				this.methodMap.put(m, new Method_DivisionByZero(this));
			} else if (m == MethodEnum.POWER) {
				this.methodMap.put(m, new Method_Power(this));
			} else if (m == MethodEnum.INSTR) {
				this.methodMap.put(m, new Method_Instr(this));
			} else if (m == MethodEnum.TAN) {
				this.methodMap.put(m, new Method_Tan(this));
			} else if (m == MethodEnum.SUBSTRING) {
				this.methodMap.put(m, new Method_Substring(this));
			} else if (m == MethodEnum.INPUT) {
				this.methodMap.put(m, new Method_Input(this));
			} else if (m == MethodEnum.CHECK_ON_GOTO_GOSUB_ARG) {
				this.methodMap.put(m, new Method_CheckOnGotoGosubArg(this));
			} else if (m == MethodEnum.GOSUB_STACK_INITIALIZE) {
				this.methodMap.put(m, new Method_GosubStackInitialize(this));
			} else if (m == MethodEnum.GOSUB_STACK_PUSH) {
				this.methodMap.put(m, new Method_GosubStackPush(this));
			} else if (m == MethodEnum.GOSUB_STACK_POP) {
				this.methodMap.put(m, new Method_GosubStackPop(this));
			} else if (m == MethodEnum.THROW_RUNTIME_EXCEPTION) {
				this.methodMap.put(m, new Method_ThrowRuntimeException(this));
			} else if (m == MethodEnum.READ_NUM_FROM_DATA_TO_STACK) {
				this.methodMap.put(m, new Method_ReadNumFromDataToStack(this));
			} else if (m == MethodEnum.READ_STRING_FROM_DATA_TO_STACK) {
				this.methodMap.put(m, new Method_ReadStringFromDataToStack(this));
			} else if (m == MethodEnum.RND) {
				this.methodMap.put(m, new Method_Rnd(this));
			} else if (m == MethodEnum.CHARS_TO_FLOAT) {
				this.methodMap.put(m, new Method_CharsToFloat(this));
			} else if (m == MethodEnum.FLOAT_TO_CHARS) {
				this.methodMap.put(m, new Method_FloatToChars(this));
			}
			this.usedMethods.add(m);
		}
		return this.methodMap.get(m);
	}

	public void flush() {
		// methods must be flushed in the order they were added,
		// otherwise a concurrent modification exception is thrown
		for (int i = 0; i < this.usedMethods.size(); i++) {
			MethodEnum m = this.usedMethods.get(i);
			this.methodMap.get(m).addMethod();
		}
	}
}
