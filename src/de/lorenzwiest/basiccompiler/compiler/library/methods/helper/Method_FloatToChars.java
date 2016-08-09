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

import de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaMethod;
import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

/**
This function converts a float f into a char array c.

The char array c contains a sign character (1 character), the mantissa (up to 8
characters, including a decimal point), and an optional exponent (4 characters).
The mantissa has up to 7 digits of precision with 6 digits of accuracy.
Example: f = -1.234567E+12 -> c = "-1.234567E+12"

If f > 0 then the sign character is " " (space).
Example: f = 1.23 -> c = " 1.23"

If f < 0 then the sign character is "-" (minus).
Example: f = -1.23 -> c = "-1.23"

If the absolute value of f < 1 then the mantissa starts with "." (period).
Example: f = 0.12 -> c = " .12"

If f has no fractional part then the mantissa does not end with "." (period).
Example: f = 123 -> c = " 123"

If f < 1E-07f or f >= 1E+07f then c has an exponent in the format "E[+-][0-9][0-9]".
Example: f = 1.23E-08 -> c = " 1.23E-08"
Example: f = 1.23E+08 -> c = " 1.23E+08"

If f is not a number then c contains " NaN".
Example: f = 0/0 -> c = " NaN"  // TODO: Impossible to create NaN in BASIC

If f is positive infinity then c contains " Infinity".
Example: f = 1/0 -> c = " Infinity"

If f is negative infinity then c contains "-Infinity".
Example: f = -1/0 -> c = "-Infinity"
 */

public class Method_FloatToChars extends Method {
	private final static String METHOD_NAME = "FloatToChars";
	private final static String DESCRIPTOR = "(F)[C";
	private final static int NUM_LOCALS = 12;

	public Method_FloatToChars(LibraryManager libraryManager) {
		super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
	}

	@Override
	public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

		//  SOURCE CODE
		//
		//  private static char[] floatToChars(float f) {
		//    final int DIGITS_PRECISION = 7;
		//    final int MAX_EXPONENT = 38;
		//
		//    if (f == 0.0) {
		//      return " 0".toCharArray();
		//    }
		//    if (f != f) {
		//      return " NaN".toCharArray();
		//    }
		//    if (f == Float.POSITIVE_INFINITY) {
		//      return " Infinity".toCharArray();
		//    }
		//    if (f == Float.NEGATIVE_INFINITY) {
		//      return "-Infinity".toCharArray();
		//    }
		//
		//    char buf[] = new char[13]; // enough space for floats like "-1.234567E+00"
		//    int pos = 0;
		//
		//    if (f >= 0f) {
		//      buf[pos++] = ' ';
		//    } else {
		//      buf[pos++] = '-';
		//      f = -f;
		//    }
		//
		//    int exp = (int) Math.floor((float) Math.log10(f));
		//    int currExp = (f >= 1) ? exp : -1;
		//
		//    int m;
		//    if (exp > ((-MAX_EXPONENT + (DIGITS_PRECISION - 1)) - 1)) {
		//      m = (int) (f * (float) Math.pow(10, DIGITS_PRECISION - 1 - exp));
		//    } else {   // handle IEEE 754-1985 denormalization and gradual underflow
		//      m = (int) (f * (float) Math.pow(10, MAX_EXPONENT) * (float) Math.pow(10, (-MAX_EXPONENT + (DIGITS_PRECISION - 1)) - exp));
		//    }
		//
		//    int tmpExp = exp;
		//    boolean useExp = (f < 1E-07f) || (f >= 1E+07f);
		//    if (useExp) {
		//      currExp = 0;
		//      tmpExp = 0;
		//    }
		//
		//    int pow10 = (int) Math.pow(10, DIGITS_PRECISION - 1);
		//    int digits = 0;
		//    while (digits < DIGITS_PRECISION) {
		//      if ((m <= 0) && (currExp < 0)) {
		//        break;
		//      }
		//
		//      if (currExp == -1) {
		//        buf[pos++] = '.';
		//      }
		//      if (currExp > tmpExp) {
		//        buf[pos++] = '0';
		//      } else {
		//        int digit = m / pow10;
		//        m = m - (digit * pow10);
		//        buf[pos++] = (char) ('0' + digit);
		//        pow10 /= 10;
		//      }
		//      currExp--;
		//      digits++;
		//    }
		//
		//    if (useExp) {
		//      buf[pos++] = 'E';
		//      if (exp >= 0) {
		//        buf[pos++] = '+';
		//      } else {
		//        buf[pos++] = '-';
		//        exp = -exp;
		//      }
		//      if (exp > 9) {
		//        buf[pos++] = (char) ('0' + (exp / 10));
		//      } else {
		//        buf[pos++] = '0';
		//      }
		//      buf[pos++] = (char) ('0' + (exp % 10));
		//    }
		//
		//    char[] result = new char[pos];
		//    for (--pos; pos >= 0; pos--) {
		//      result[pos] = buf[pos];
		//    }
		//    return result;
		//  }

		final int DIGITS_PRECISION = 7;
		final int MAX_EXPONENT = 38;

		final int F = 0;             // local 0:  F  float value
		final int BUF = 1;           // local 1:  [C working character buffer, 13 characters long
		final int POS = 2;           // local 2:  I  position in character buffer
		final int EXP = 3;           // local 3:  I  exponent of normalized mantissa
		final int CURR_EXP = 4;      // local 4:  I  current running exponent
		final int M = 5;             // local 5:  I  initially: normalized mantissa * 10^6
		final int TMP_EXP = 6;       // local 6:  I  temporary exponent
		final int USE_EXP = 7;       // local 7:  Z  uses exponent?
		final int POW10 = 8;         // local 8:  I  max power...
		final int DIGITS = 9;        // local 9:  I  number of digits
		final int DIGIT = 10;        // local 10: C  digit character
		final int RESULT = 11;       // local 11: [C final character buffer

		//  if (f == 0.0) {
		//    return " 0".toCharArray();
		//  }

		o.fload_opt(F);
		o.fconst_0();
		o.fcmpg();
		o.ifne("checkNaN");

		o.ldc(this.classModel.getStringIndex(" 0"));
		this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_TO_CHARS).emitCall(o);
		o.areturn();

		o.label("checkNaN");

		//  if (f != f) {
		//    return " NaN".toCharArray();
		//  }

		o.fload_opt(F);
		o.fload_opt(F);
		o.fcmpg();
		o.ifeq("checkPositiveInfinity");

		o.ldc(this.classModel.getStringIndex(" NaN"));
		this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_TO_CHARS).emitCall(o);
		o.areturn();

		//  if (f == Float.POSITIVE_INFINITY) {
		//    return " Infinity".toCharArray();
		//  }
		//  if (f == Float.NEGATIVE_INFINITY) {
		//    return "-Infinity".toCharArray();
		//  }

		o.label("checkPositiveInfinity");
		o.fload_opt(F);
		o.ldc(this.classModel.getFloatIndex(Float.POSITIVE_INFINITY));
		o.fcmpg();
		o.ifne("checkNegativeInfinity");

		o.ldc(this.classModel.getStringIndex(" Infinity"));
		this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_TO_CHARS).emitCall(o);
		o.areturn();

		o.label("checkNegativeInfinity");
		o.fload_opt(F);
		o.ldc(this.classModel.getFloatIndex(Float.NEGATIVE_INFINITY));
		o.fcmpg();
		o.ifne("afterSpecialValues");

		o.ldc(this.classModel.getStringIndex("-Infinity"));
		this.libraryManager.getMethod(LibraryManager.MethodEnum.STRING_TO_CHARS).emitCall(o);
		o.areturn();

		o.label("afterSpecialValues");

		//  char buf[] = new char[13]; // enough space for floats like "-1.234567E+00"

		o.iconst(13);
		o.newarray_char();
		o.astore_opt(BUF);

		//  int pos = 0;

		o.iconst_0();
		o.istore_opt(POS);

		//  if (f >= 0f) {
		//    buf[pos++] = ' ';
		//  } else {
		//    buf[pos++] = '-';
		//    f = -f;
		//  }

		o.aload_opt(BUF);
		o.iload_opt(POS);

		o.fload_opt(F);
		o.fconst_0();
		o.fcmpg();
		o.iflt("handleNegativeSign");

		o.iconst(' ');
		o.goto_("afterSign");

		o.label("handleNegativeSign");
		o.iconst('-');

		o.fload_opt(F);
		o.fneg();
		o.fstore_opt(F);
		o.label("afterSign");

		o.castore();
		o.iinc(POS, 1);

		//  int exp = (int) Math.floor((float) Math.log10(f));

		o.fload_opt(F);
		o.f2d();
		o.invokestatic(this.classModel.getJavaMethodRefIndex(JavaMethod.MATH_LOG10));
		o.d2f();
		o.f2d();
		o.invokestatic(this.classModel.getJavaMethodRefIndex(JavaMethod.MATH_FLOOR));
		o.d2i();
		o.istore_opt(EXP);

		//  int currExp = (f >= 1) ? exp : -1;

		o.fload_opt(F);
		o.fconst_1();
		o.fcmpg();
		o.iflt("handleSmallExponent");

		o.iload_opt(EXP);
		o.goto_("initExponent");

		o.label("handleSmallExponent");
		o.iconst_m1();

		o.label("initExponent");
		o.istore_opt(CURR_EXP);

		//    int m;
		//    if (exp > (-MAX_EXPONENT + (DIGITS_PRECISION - 1) - 1)) {
		//      m = (int) (f * (float) Math.pow(10, DIGITS_PRECISION - 1 - exp));
		//    } else {   // handle IEEE 754-1985 denormalization and gradual underflow
		//      m = (int) (f * (float) Math.pow(10, MAX_EXPONENT) * (float) Math.pow(10, -MAX_EXPONENT + (DIGITS_PRECISION - 1) - exp));
		//    }

		o.fload_opt(F);

		o.iload_opt(EXP);
		o.iconst((-MAX_EXPONENT + (DIGITS_PRECISION - 1)) - 1);
		o.if_icmple("denormalizeOrUnderflow");

		o.iconst(10);
		o.i2d();
		o.iconst(DIGITS_PRECISION - 1);
		o.iload_opt(EXP);
		o.isub();
		o.i2d();
		o.invokestatic(this.classModel.getJavaMethodRefIndex(JavaMethod.MATH_POW));
		o.d2f();
		o.fmul();
		o.goto_("storeM");

		o.label("denormalizeOrUnderflow");

		o.iconst(10);
		o.i2d();
		o.iconst(MAX_EXPONENT);
		o.i2d();
		o.invokestatic(this.classModel.getJavaMethodRefIndex(JavaMethod.MATH_POW));
		o.d2f();
		o.fmul();

		o.iconst(10);
		o.i2d();
		o.iconst(-MAX_EXPONENT + (DIGITS_PRECISION - 1));
		o.iload_opt(EXP);
		o.isub();
		o.i2d();
		o.invokestatic(this.classModel.getJavaMethodRefIndex(JavaMethod.MATH_POW));
		o.d2f();
		o.fmul();

		o.label("storeM");
		o.f2i();
		o.istore_opt(M);

		//  int tmpExp = exp;

		o.iload_opt(EXP);
		o.istore_opt(TMP_EXP);

		//  boolean useExp = (f < 1E-07f) || (f >= 1E+07f);

		o.iconst_0();
		o.istore_opt(USE_EXP);
		o.fload_opt(F);
		o.ldc(this.classModel.getFloatIndex(1E-07f));
		o.fcmpg();
		o.iflt("one");

		o.fload_opt(F);
		o.ldc(this.classModel.getFloatIndex(1E+07f));
		o.fcmpg();
		o.iflt("afterInitUseExp");

		o.label("one");
		o.iconst_1();
		o.istore_opt(USE_EXP);

		o.label("afterInitUseExp");

		//  if (useExp) {
		//    currExp = 0;
		//    tmpExp = 0;
		//  }

		o.iload_opt(USE_EXP);
		o.ifeq("afterUseExp");

		o.iconst_0();
		o.istore_opt(CURR_EXP);
		o.iconst_0();
		o.istore_opt(TMP_EXP);

		o.label("afterUseExp");

		//  int pow10 = (int) Math.pow(10, DIGITS_PRECISION - 1);

		o.iconst(10);
		o.i2d();
		o.iconst(DIGITS_PRECISION - 1);
		o.i2d();
		o.invokestatic(this.classModel.getJavaMethodRefIndex(JavaMethod.MATH_POW));
		o.d2i();
		o.istore_opt(POW10);

		//  int digits = 0;

		o.iconst_0();
		o.istore_opt(DIGITS);

		//  while (digits < DIGITS_PRECISION) {
		//    if ((m <= 0) && (currExp < 0)) {
		//      break;
		//    }
		//
		//    if (currExp == -1) {
		//      buf[pos++] = '.';
		//    }
		//    if (currExp > tmpExp) {
		//      buf[pos++] = '0';
		//    } else {
		//      int digit = m / pow10;
		//      m = m - (digit * pow10);
		//      buf[pos++] = (char) ('0' + digit);
		//      pow10 /= 10;
		//    }
		//    currExp--;
		//    digits++;
		//  }

		o.label("while");
		o.iload_opt(DIGITS);
		o.iconst(DIGITS_PRECISION);
		o.if_icmpge("afterWhile");

		o.iload_opt(M);
		o.ifgt("foo");

		o.iload_opt(CURR_EXP);
		o.iflt("afterWhile");

		o.label("foo");

		o.iload_opt(CURR_EXP);
		o.iconst_m1();
		o.if_icmpne("skipSeenDot");

		o.aload_opt(BUF);
		o.iload_opt(POS);
		o.iconst('.');
		o.castore();
		o.iinc(POS, 1);

		o.label("skipSeenDot");
		o.iload_opt(CURR_EXP);
		o.iload_opt(TMP_EXP);
		o.if_icmple("skipExpOverflow");

		o.aload_opt(BUF);
		o.iload_opt(POS);
		o.iconst('0');
		o.castore();
		o.iinc(POS, 1);
		o.goto_("afterExpOverflow");

		o.label("skipExpOverflow");
		o.iload_opt(M);
		o.iload_opt(POW10);
		o.idiv();
		o.istore_opt(DIGIT);

		o.iload_opt(M);
		o.iload_opt(DIGIT);
		o.iload_opt(POW10);
		o.imul();
		o.isub();
		o.istore_opt(M);

		o.aload_opt(BUF);
		o.iload_opt(POS);
		o.iconst('0');
		o.iload_opt(DIGIT);
		o.iadd();

		o.castore();
		o.iinc(POS, 1);

		o.iload_opt(POW10);
		o.iconst(10);
		o.idiv();
		o.istore_opt(POW10);

		o.label("afterExpOverflow");
		o.iinc(CURR_EXP, -1);
		o.iinc(DIGITS, 1);
		o.goto_("while");

		o.label("afterWhile");

		//  if (useExp) {
		//    buf[pos++] = 'E';
		//    if (exp >= 0) {
		//      buf[pos++] = '+';
		//    } else {
		//      buf[pos++] = '-';
		//      exp = -exp;
		//    }
		//
		//    if (exp > 9) {
		//      buf[pos++] = (char) ('0' + (exp / 10));
		//    } else {
		//      buf[pos++] = '0';
		//    }
		//    buf[pos++] = (char) ('0' + (exp % 10));
		//  }

		o.iload_opt(USE_EXP);
		o.ifeq("afterAddExponent");

		o.aload_opt(BUF);
		o.iload_opt(POS);
		o.iconst('E');
		o.castore();
		o.iinc(POS, 1);

		o.aload_opt(BUF);
		o.iload_opt(POS);

		o.iload_opt(EXP);
		o.iflt("addNegativeExponentSign");

		o.iconst('+');
		o.goto_("afterAddExponentSign");

		o.label("addNegativeExponentSign");
		o.iconst('-');

		o.iload_opt(EXP);
		o.ineg();
		o.istore_opt(EXP);

		o.label("afterAddExponentSign");
		o.castore();
		o.iinc(POS, 1);

		o.aload_opt(BUF);
		o.iload_opt(POS);

		o.iload_opt(EXP);
		o.iconst(9);
		o.if_icmple("addZeroTensExponentDigit");

		o.iconst('0');
		o.iload_opt(EXP);
		o.iconst(10);
		o.idiv();
		o.iadd();
		o.goto_("addOnesExponentDigit");

		o.label("addZeroTensExponentDigit");
		o.iconst('0');

		o.label("addOnesExponentDigit");
		o.castore();
		o.iinc(POS, 1);

		o.aload_opt(BUF);
		o.iload_opt(POS);

		o.iconst('0');
		o.iload_opt(EXP);
		o.iconst(10);
		o.irem();
		o.iadd();

		o.castore();
		o.iinc(POS, 1);

		o.label("afterAddExponent");

		//  char[] result = new char[pos];
		//  for (--pos; pos >= 0; pos--) {
		//    result[pos] = buf[pos];
		//  }
		//  return result;

		o.iload_opt(POS);
		o.newarray_char();
		o.astore_opt(RESULT);

		o.iload_opt(POS);
		o.goto_("continueCopyLoop");

		o.label("copyLoop");
		o.aload_opt(RESULT);
		o.iload_opt(POS);

		o.aload_opt(BUF);
		o.iload_opt(POS);
		o.caload();

		o.castore();

		o.label("continueCopyLoop");
		o.iinc(POS, -1);
		o.iload_opt(POS);
		o.ifge("copyLoop");

		o.aload_opt(RESULT);
		o.areturn();
	}
}
