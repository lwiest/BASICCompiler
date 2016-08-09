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

import static de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaMethod.MATH_POW;

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

/**
This function tries to read as much as numeric information as possible from the
passed character array and returns the resulting float value. If the conversion
fails then NaN is returned.

A valid character array complies to the following regular expression:

\s*[-+]?([0-9]+(\.[0-9]*)?|\.[0-9]+)([eE][-+]?[0-9]+)?

(Optional leading whitespace, optional sign, optional integer, optional 
fraction, and optional exponent. If the integer part is omitted, the fraction 
part is mandatory. If the fraction is omitted, the decimal point is optional.)
 */

public class Method_CharsToFloat extends Method {
  private final static String METHOD_NAME = "CharsToFloat";
  private final static String DESCRIPTOR = "([C)F";
  private final static int NUM_LOCALS = 12;

  public Method_CharsToFloat(LibraryManager libraryManager) {
    super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
  }

  @Override
  public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

    // TODO: Improve precision near zero, when IEEE 754-1985 denormalization and gradual underflow start	  

    //  SOURCE CODE
    //
    // float charsToFloat(char[] s) {
    //   int pos = 0;
    //
    //    int chr;
    //    for (; pos < s.length; pos++) {
    //      chr = s[pos];
    //      if ((chr == ' ') || (chr == '\t') || (chr == '\r') || (chr == '\n')) {
    //        continue;
    //      }
    //      break;
    //    }
    //
    //  	if (pos >= s.length) {
    //  		return Float.NaN;
    //  	}
    //
    //    boolean isNeg = false;
    //    chr = s[pos];
    //    if (chr == '-') {
    //      isNeg = true;
    //      pos++;
    //    } else if (chr == '+') {
    //      pos++;
    //    }
    //
    //    float m = 0f;
    //    int mExp = -1;
    //    int dotExp = 0;
    //
    //    boolean seenMantissa = false;
    //    boolean seenDot = false;
    //
    //    for (; pos < s.length; pos++) {
    //      chr = s[pos];
    //      if ((chr >= '0') && (chr <= '9')) {
    //        m = (10 * m) + (chr - '0');
    //        mExp++;
    //        seenMantissa = true;
    //      } else if (chr == '.') {
    //        if (seenDot) {
    //          break;
    //        }
    //        dotExp = mExp;
    //        seenDot = true;
    //      } else {
    //        break;
    //      }
    //    }
    //
    //    if (seenMantissa == false) {
    //      return Float.NaN;
    //    }
    //
    //    boolean isExpNeg = false;
    //    int exp = 0;
    //
    //    if ((pos + 1) < s.length) {
    //      chr = s[pos];
    //      if ((chr == 'E') || (chr == 'e')) {
    //        pos++;
    //        chr = s[pos];
    //        if (chr == '-') {
    //          isExpNeg = true;
    //          pos++;
    //        } else if (chr == '+') {
    //          pos++;
    //        }
    //
    //        for (; pos < s.length; pos++) {
    //          chr = s[pos];
    //          if ((chr >= '0') && (chr <= '9')) {
    //            exp = (10 * exp) + (chr - '0');
    //          } else {
    //            break;
    //          }
    //        }
    //      }
    //    }
    //
    //    if (isExpNeg) {
    //      exp = -exp;
    //    }
    //
    //    int effExp = seenDot ? mExp - dotExp : 0;
    //    float f = m / (float) Math.pow(10.0f, effExp - exp);
    //    if (isNeg) {
    //      f = -f;
    //    }
    //    return f;
    //  }

    final int S = 0;             // local 0:  [C s
    final int POS = 1;           // local 1:  I  pos
    final int CHR = 2;           // local 2:  I  character of s[pos]
    final int IS_NEG = 3;        // local 3:  Z  is number negative?
    final int M = 4;             // local 4:  F  mantissa
    final int M_EXP = 5;         // local 5:  I  exponent of mantissa
    final int DOT_EXP = 6;       // local 6:  I  exponent of decimal point
    final int SEEN_MANTISSA = 7; // local 7:  Z  has parsing of mantissa started?
    final int SEEN_DOT = 8;      // local 8:  Z  has parsing of decimal point started?
    final int IS_EXP_NEG = 9;    // local 9:  Z  is exponent negative?
    final int EXP = 10;          // local 10: I  exponent of number
    final int EFF_EXP = 11;      // local 11: I  effective exponent

    // int pos = 0;

    o.iconst_0();
    o.istore_opt(POS);

    //  int chr;
    //  for (; pos < s.length; pos++) {
    //    chr = s[pos];
    //    if ((chr == ' ') || (chr == '\t') || (chr == '\r') || (chr == '\n')) {
    //      continue;
    //    }
    //    break;
    //  }

    o.goto_("whitespaceLoopCond");

    o.label("whitespaceLoop");
    o.aload_opt(S);
    o.iload_opt(POS);
    o.caload();
    o.istore_opt(CHR);

    o.iload_opt(CHR);
    o.iconst(' ');
    o.if_icmpeq("continueWhitespaceLoop");

    o.iload_opt(CHR);
    o.iconst('\t');
    o.if_icmpeq("continueWhitespaceLoop");

    o.iload_opt(CHR);
    o.iconst('\r');
    o.if_icmpeq("continueWhitespaceLoop");

    o.iload_opt(CHR);
    o.iconst('\n');
    o.if_icmpne("afterWhitespaceLoop");

    o.label("continueWhitespaceLoop");
    o.iinc(POS, 1);

    o.label("whitespaceLoopCond");
    o.iload_opt(POS);
    o.aload_opt(S);
    o.arraylength();
    o.if_icmplt("whitespaceLoop");

    o.label("afterWhitespaceLoop");

    //	if (pos >= s.length) {
    //		return Float.NaN;
    //	}

    o.iload_opt(POS);
    o.aload_opt(S);
    o.arraylength();
    o.if_icmplt("afterWhitespace");

    o.ldc(this.classModel.getFloatIndexOfNaN());
    o.freturn();

    o.label("afterWhitespace");

    //  boolean isNeg = false;
    //  chr = s[pos];
    //  if (chr == '-') {
    //    isNeg = true;
    //    pos++;
    //  } else if (chr == '+') {
    //    pos++;
    //  }

    o.iconst_0();
    o.istore_opt(IS_NEG);

    o.aload_opt(S);
    o.iload_opt(POS);
    o.caload();
    o.istore_opt(CHR);

    o.iload_opt(CHR);
    o.iconst('-');
    o.if_icmpne("elseCheckMantissaPlus");

    o.iconst_1();
    o.istore_opt(IS_NEG);
    o.iinc(POS, 1);
    o.goto_("afterMantissaSign");

    o.label("elseCheckMantissaPlus");
    o.iload_opt(CHR);
    o.iconst('+');
    o.if_icmpne("afterMantissaSign");

    o.iinc(POS, 1);

    o.label("afterMantissaSign");

    //  float m = 0f;
    //  int mExp = -1;
    //  int dotExp = 0;
    //
    //  boolean seenMantissa = false;
    //  boolean seenDot = false;

    o.fconst_0();
    o.fstore_opt(M);
    o.iconst_m1();
    o.istore_opt(M_EXP);
    o.iconst_0();
    o.istore_opt(DOT_EXP);
    o.iconst_0();
    o.istore_opt(SEEN_MANTISSA);
    o.iconst_0();
    o.istore_opt(SEEN_DOT);

    //  for (; pos < s.length; pos++) {
    //    chr = s[pos];
    //    if ((chr >= '0') && (chr <= '9')) {
    //      m = (10 * m) + (chr - '0');
    //      mExp++;
    //      seenMantissa = true;
    //    } else if (chr == '.') {
    //      if (seenDot) {
    //        break;
    //      }
    //      dotExp = mExp;
    //      seenDot = true;
    //    } else {
    //      break;
    //    }
    //  }

    o.goto_("mantissaLoopCond");

    o.label("mantissaLoop");
    o.aload_opt(S);
    o.iload_opt(POS);
    o.caload();
    o.istore_opt(CHR);

    o.iload_opt(CHR);
    o.iconst('0');
    o.if_icmplt("elseCheckDot");

    o.iload_opt(CHR);
    o.iconst('9');
    o.if_icmpgt("elseCheckDot");

    o.iconst(10);
    o.i2f();
    o.fload_opt(M);
    o.fmul();

    o.iload_opt(CHR);
    o.iconst('0');
    o.isub();
    o.i2f();

    o.fadd();
    o.fstore_opt(M);

    o.iinc(M_EXP, 1);
    o.iconst_1();
    o.istore_opt(SEEN_MANTISSA);
    o.goto_("continueMantissaLoop");

    o.label("elseCheckDot");
    o.iload_opt(CHR);
    o.iconst('.');
    o.if_icmpne("afterMantissaLoop");

    o.iload_opt(SEEN_DOT);
    o.ifne("afterMantissaLoop");

    o.iload_opt(M_EXP);
    o.istore_opt(DOT_EXP);

    o.iconst_1();
    o.istore_opt(SEEN_DOT);

    o.label("continueMantissaLoop");
    o.iinc(POS, 1);

    o.label("mantissaLoopCond");
    o.iload_opt(POS);
    o.aload_opt(S);
    o.arraylength();
    o.if_icmplt("mantissaLoop");

    o.label("afterMantissaLoop");

    //  if (seenMantissa == false) {
    //    return Float.NaN;
    //  }

    o.iload_opt(SEEN_MANTISSA);
    o.ifne("afterMantissa");

    o.ldc(this.classModel.getFloatIndexOfNaN());
    o.freturn();

    o.label("afterMantissa");

    //  boolean isExpNeg = false;
    //  int exp = 0;

    o.iconst_0();
    o.istore_opt(IS_EXP_NEG);
    o.iconst_0();
    o.istore_opt(EXP);

    //  if ((pos + 1) < s.length) {
    //    chr = s[pos];
    //    if ((chr == 'E') || (chr == 'e')) {

    o.iload_opt(POS);
    o.iconst_1();
    o.iadd();
    o.aload_opt(S);
    o.arraylength();
    o.if_icmpge("afterExponent");

    o.aload_opt(S);
    o.iload_opt(POS);
    o.caload();
    o.istore_opt(CHR);

    o.iload_opt(CHR);
    o.iconst('E');
    o.if_icmpeq("parseExponent");

    o.iload_opt(CHR);
    o.iconst('e');
    o.if_icmpne("afterExponent");

    o.label("parseExponent");

    //      pos++;
    //      chr = s[pos];
    //      if (chr == '-') {
    //        isExpNeg = true;
    //        pos++;
    //      } else if (chr == '+') {
    //        pos++;
    //      }

    o.iinc(POS, 1);

    o.aload_opt(S);
    o.iload_opt(POS);
    o.caload();
    o.istore_opt(CHR);

    o.iload_opt(CHR);
    o.iconst('-');
    o.if_icmpne("elseCheckExponentPlus");

    o.iconst_1();
    o.istore_opt(IS_EXP_NEG);
    o.iinc(POS, 1);
    o.goto_("afterExponentSign");

    o.label("elseCheckExponentPlus");
    o.iload_opt(CHR);
    o.iconst('+');
    o.if_icmpne("afterExponentSign");

    o.iinc(POS, 1);

    o.label("afterExponentSign");

    //      for (; pos < s.length; pos++) {
    //        chr = s[pos];
    //        if ((chr >= '0') && (chr <= '9')) {
    //          exp = (10 * exp) + (chr - '0');
    //        } else {
    //          break;
    //        }
    //      }
    //    }
    //  }

    o.goto_("exponentLoopCond");

    o.label("exponentLoop");
    o.aload_opt(S);
    o.iload_opt(POS);
    o.caload();
    o.istore_opt(CHR);

    o.iload_opt(CHR);
    o.iconst('0');
    o.if_icmplt("afterExponent");

    o.iload_opt(CHR);
    o.iconst('9');
    o.if_icmpgt("afterExponent");

    o.iconst(10);
    o.iload_opt(EXP);
    o.imul();

    o.iload_opt(CHR);
    o.iconst('0');
    o.isub();

    o.iadd();
    o.istore_opt(EXP);

    o.iinc(POS, 1);

    o.label("exponentLoopCond");
    o.iload_opt(POS);
    o.aload_opt(S);
    o.arraylength();
    o.if_icmplt("exponentLoop");

    o.label("afterExponent");

    //  if (isExpNeg) {
    //    exp = -exp;
    //  }

    o.iload_opt(IS_EXP_NEG);
    o.ifeq("skipExpNegation");

    o.iload_opt(EXP);
    o.ineg();
    o.istore_opt(EXP);

    o.label("skipExpNegation");

    //  int effExp = seenDot ? mExp - dotExp : 0;

    o.iload_opt(SEEN_DOT);
    o.ifeq("clearEffectiveExponent");

    o.iload_opt(M_EXP);
    o.iload_opt(DOT_EXP);
    o.isub();
    o.goto_("storeEffectiveExponent");

    o.label("clearEffectiveExponent");
    o.iconst_0();

    o.label("storeEffectiveExponent");
    o.istore_opt(EFF_EXP);

    //  float f = m / (float) Math.pow(10.0f, effExp - exp);
    //  if (isNeg) {
    //    f = -f;
    //  }
    //  return f;

    o.fload_opt(M);
    o.iconst(10);
    o.i2d();
    o.iload_opt(EFF_EXP);
    o.iload_opt(EXP);
    o.isub();
    o.i2d();
    o.invokestatic(this.classModel.getJavaMethodRefIndex(MATH_POW));
    o.d2f();
    o.fdiv();

    o.iload_opt(IS_NEG);
    o.ifeq("skipNegation");

    o.fneg();

    o.label("skipNegation");
    o.freturn();
  }
}
