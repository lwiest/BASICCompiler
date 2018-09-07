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

package de.lorenzwiest.basiccompiler.compiler.library;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.lorenzwiest.basiccompiler.classfile.ClassModel;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Check1DFloatArrayAccess;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Check1DStringArrayAccess;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Check2DFloatArrayAccess;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Check2DStringArrayAccess;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Dim1DCheckSize;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Dim1DFloatArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Dim1DStringArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Dim2DCheckSize;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Dim2DFloatArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_Dim2DStringArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_LoadFloatFrom1DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_LoadFloatFrom2DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_LoadStringFrom1DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_LoadStringFrom2DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_StoreFloatIn1DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_StoreFloatIn2DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_StoreStringIn1DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.arrays.Method_StoreStringIn2DArray;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Abs;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Asc;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Atn;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Chr;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Cos;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Exp;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Fix;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Instr;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Int;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Left;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Len;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Log;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Mid;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Pos;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Right;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Rnd;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Sgn;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Sin;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Space;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Spc;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Sqr;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Str;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Tab;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Tan;
import de.lorenzwiest.basiccompiler.compiler.library.methods.functions.Method_Val;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_CharsToFloat;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_CheckLogicalOperatorArguments;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_CheckOnGotoGosubArg;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_FloatToChars;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_GosubStackInitialize;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_GosubStackPop;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_GosubStackPush;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_Input;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_PrintCharFromStack;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_PrintCharsFromStack;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_PrintFloatFromStack;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_PrintStringFromStack;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_ReadCharsToStack;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_ReadNumFromDataToStack;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_ReadStringFromDataToStack;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_RoundToInt;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_StringToChars;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_Substring;
import de.lorenzwiest.basiccompiler.compiler.library.methods.helper.Method_ThrowRuntimeException;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_And;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_Division;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_DivisionByZero;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_IntegerDivision;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_Mod;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_Not;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_Or;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_Power;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_StringConcatenation;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_StringEqual;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_StringGreaterOrEqual;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_StringGreaterThan;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_StringLessOrEqual;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_StringLessThan;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_StringNotEqual;
import de.lorenzwiest.basiccompiler.compiler.library.methods.operators.Method_Xor;

public class LibraryManager {
	private final ClassModel classModel;
	private final Map<MethodEnum, Method> methodMap = new HashMap<MethodEnum, Method>();
	private final LinkedList<MethodEnum> usedMethodsDeque = new LinkedList<MethodEnum>();

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
		XOR;
	}

	public LibraryManager(ClassModel classModel) {
		this.classModel = classModel;
	}

	public ClassModel getClassModel() {
		return this.classModel;
	}

	public Method getMethod(MethodEnum m) {
		if (this.methodMap.containsKey(m) == false) {
			Method method = null;
			switch (m) {
			case ABS:
				method = new Method_Abs(this);
				break;
			case AND:
				method = new Method_And(this);
				break;
			case ASC:
				method = new Method_Asc(this);
				break;
			case ATN:
				method = new Method_Atn(this);
				break;
			case CHARS_TO_FLOAT:
				method = new Method_CharsToFloat(this);
				break;
			case CHECK_1D_FLOAT_ARRAY_ACCESS:
				method = new Method_Check1DFloatArrayAccess(this);
				break;
			case CHECK_1D_STRING_ARRAY_ACCESS:
				method = new Method_Check1DStringArrayAccess(this);
				break;
			case CHECK_2D_FLOAT_ARRAY_ACCESS:
				method = new Method_Check2DFloatArrayAccess(this);
				break;
			case CHECK_2D_STRING_ARRAY_ACCESS:
				method = new Method_Check2DStringArrayAccess(this);
				break;
			case CHECK_LOGICAL_OPERATION_ARGUMENTS:
				method = new Method_CheckLogicalOperatorArguments(this);
				break;
			case CHECK_ON_GOTO_GOSUB_ARG:
				method = new Method_CheckOnGotoGosubArg(this);
				break;
			case CHR:
				method = new Method_Chr(this);
				break;
			case COS:
				method = new Method_Cos(this);
				break;
			case DIM_1D_CHECK_SIZE:
				method = new Method_Dim1DCheckSize(this);
				break;
			case DIM_1D_FLOAT_ARRAY:
				method = new Method_Dim1DFloatArray(this);
				break;
			case DIM_1D_STRING_ARRAY:
				method = new Method_Dim1DStringArray(this);
				break;
			case DIM_2D_CHECK_SIZE:
				method = new Method_Dim2DCheckSize(this);
				break;
			case DIM_2D_FLOAT_ARRAY:
				method = new Method_Dim2DFloatArray(this);
				break;
			case DIM_2D_STRING_ARRAY:
				method = new Method_Dim2DStringArray(this);
				break;
			case DIVISION:
				method = new Method_Division(this);
				break;
			case DIVISION_BY_ZERO:
				method = new Method_DivisionByZero(this);
				break;
			case EXP:
				method = new Method_Exp(this);
				break;
			case FIX:
				method = new Method_Fix(this);
				break;
			case FLOAT_TO_CHARS:
				method = new Method_FloatToChars(this);
				break;
			case GOSUB_STACK_INITIALIZE:
				method = new Method_GosubStackInitialize(this);
				break;
			case GOSUB_STACK_POP:
				method = new Method_GosubStackPop(this);
				break;
			case GOSUB_STACK_PUSH:
				method = new Method_GosubStackPush(this);
				break;
			case INPUT:
				method = new Method_Input(this);
				break;
			case INSTR:
				method = new Method_Instr(this);
				break;
			case INT:
				method = new Method_Int(this);
				break;
			case INTEGER_DIVISION:
				method = new Method_IntegerDivision(this);
				break;
			case LEFT:
				method = new Method_Left(this);
				break;
			case LEN:
				method = new Method_Len(this);
				break;
			case LOAD_FLOAT_FROM_1D_ARRAY:
				method = new Method_LoadFloatFrom1DArray(this);
				break;
			case LOAD_FLOAT_FROM_2D_ARRAY:
				method = new Method_LoadFloatFrom2DArray(this);
				break;
			case LOAD_STRING_FROM_1D_ARRAY:
				method = new Method_LoadStringFrom1DArray(this);
				break;
			case LOAD_STRING_FROM_2D_ARRAY:
				method = new Method_LoadStringFrom2DArray(this);
				break;
			case LOG:
				method = new Method_Log(this);
				break;
			case MID:
				method = new Method_Mid(this);
				break;
			case MOD:
				method = new Method_Mod(this);
				break;
			case NOT:
				method = new Method_Not(this);
				break;
			case OR:
				method = new Method_Or(this);
				break;
			case POS:
				method = new Method_Pos(this);
				break;
			case POWER:
				method = new Method_Power(this);
				break;
			case PRINT_CHAR_FROM_STACK:
				method = new Method_PrintCharFromStack(this);
				break;
			case PRINT_CHARS_FROM_STACK:
				method = new Method_PrintCharsFromStack(this);
				break;
			case PRINT_FLOAT_FROM_STACK:
				method = new Method_PrintFloatFromStack(this);
				break;
			case PRINT_STRING_FROM_STACK:
				method = new Method_PrintStringFromStack(this);
				break;
			case READ_CHARS_TO_STACK:
				method = new Method_ReadCharsToStack(this);
				break;
			case READ_NUM_FROM_DATA_TO_STACK:
				method = new Method_ReadNumFromDataToStack(this);
				break;
			case READ_STRING_FROM_DATA_TO_STACK:
				method = new Method_ReadStringFromDataToStack(this);
				break;
			case RIGHT:
				method = new Method_Right(this);
				break;
			case RND:
				method = new Method_Rnd(this);
				break;
			case ROUND_TO_INT:
				method = new Method_RoundToInt(this);
				break;
			case SGN:
				method = new Method_Sgn(this);
				break;
			case SIN:
				method = new Method_Sin(this);
				break;
			case SPACE:
				method = new Method_Space(this);
				break;
			case SPC:
				method = new Method_Spc(this);
				break;
			case SQR:
				method = new Method_Sqr(this);
				break;
			case STORE_FLOAT_IN_1D_ARRAY:
				method = new Method_StoreFloatIn1DArray(this);
				break;
			case STORE_FLOAT_IN_2D_ARRAY:
				method = new Method_StoreFloatIn2DArray(this);
				break;
			case STORE_STRING_IN_1D_ARRAY:
				method = new Method_StoreStringIn1DArray(this);
				break;
			case STORE_STRING_IN_2D_ARRAY:
				method = new Method_StoreStringIn2DArray(this);
				break;
			case STR:
				method = new Method_Str(this);
				break;
			case STRING_CONCATENATION:
				method = new Method_StringConcatenation(this);
				break;
			case STRING_EQUAL:
				method = new Method_StringEqual(this);
				break;
			case STRING_GREATER_OR_EQUAL:
				method = new Method_StringGreaterOrEqual(this);
				break;
			case STRING_GREATER_THAN:
				method = new Method_StringGreaterThan(this);
				break;
			case STRING_LESS_OR_EQUAL:
				method = new Method_StringLessOrEqual(this);
				break;
			case STRING_LESS_THAN:
				method = new Method_StringLessThan(this);
				break;
			case STRING_NOT_EQUAL:
				method = new Method_StringNotEqual(this);
				break;
			case STRING_TO_CHARS:
				method = new Method_StringToChars(this);
				break;
			case SUBSTRING:
				method = new Method_Substring(this);
				break;
			case TAB:
				method = new Method_Tab(this);
				break;
			case TAN:
				method = new Method_Tan(this);
				break;
			case THROW_RUNTIME_EXCEPTION:
				method = new Method_ThrowRuntimeException(this);
				break;
			case VAL:
				method = new Method_Val(this);
				break;
			case XOR:
				method = new Method_Xor(this);
				break;
			}
			this.methodMap.put(m, method);
			this.usedMethodsDeque.addLast(m);
		}
		return this.methodMap.get(m);
	}

	public void flush() {
		// methods must be flushed in the order they were added,
		while (this.usedMethodsDeque.size() > 0) {
			MethodEnum m = this.usedMethodsDeque.removeFirst();
			this.methodMap.get(m).addMethod();
		}
	}
}
