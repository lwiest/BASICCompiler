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

package org.basiccompiler.tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.basiccompiler.BASICCompiler;
import org.basiccompiler.compiler.etc.CompileException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CompilerTest {
	private final static String CR = System.getProperty("line.separator");

	private final static String TEST_FOLDER_PATH = "c:\\temp";
	private final static String TEST_CLASS_NAME = "CompilerTestClass";
	private final static String TEST_CLASS_FULLFILENAME = TEST_FOLDER_PATH + "\\" + TEST_CLASS_NAME + ".class";

	private static int testCount;

	@BeforeClass
	public static void beforeClass() {
		testCount = 0;
	}

	@AfterClass
	public static void afterClass() {
		System.out.println("Number of tested BASIC programs: " + testCount);
	}

	@Test
	public void testSimple() {
		// add ad-hoc tests here...
	}

	@Test
	public void testVariableAndFunctionNamesWithDots() {
		assertEquals(compileAndRun("10 A.HELLO = 1 : PRINT A.HELLO"), " 1 ");
		assertEquals(compileAndRun("10 A.HELLO$ = \"HELLO\" : PRINT A.HELLO$"), "HELLO");
		assertEquals(compileAndRun("10 DIM A.HELLO(2) : A.HELLO(1) = 3 : PRINT A.HELLO(1)"), " 3 ");
		assertEquals(compileAndRun("10 DIM A.HELLO$(2) : A.HELLO$(1) = \"HELLO\" : PRINT A.HELLO$(1)"), "HELLO");
		assertEquals(compileAndRun("10 DEF FNA.HELLO(X)=X*X : PRINT FNA.HELLO(2)"), " 4 ");
		assertEquals(compileAndRun("10 DEF FNA.HELLO$(A$)=A$+A$ : PRINT FNA.HELLO$(\"HELLO\")"), "HELLOHELLO");
	}

	@Test
	public void testFloatConversion_VAL_Simple() {
		// effectively checks class Method_CharsToFloat, Method_FloatToChars
		assertEquals(compileAndRun("10 PRINT VAL(\"0\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-0\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1\")"), "-1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"+1\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"10\")"), " 10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.1\")"), " 1.1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.1\")"), " .1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\".1\")"), " .1 ");
	}

	@Test
	public void testFloatConversion_SimpleFractions() {
		// effectively checks class Method_FloatToChars
		assertEquals(compileAndRun("10 PRINT 1/2"), " .5 ");
		assertEquals(compileAndRun("10 PRINT 1/3"), " .3333333 ");
		assertEquals(compileAndRun("10 PRINT 1/4"), " .25 ");
		assertEquals(compileAndRun("10 PRINT 1/5"), " .2 ");
		assertEquals(compileAndRun("10 PRINT 1/6"), " .1666666 ");
		assertEquals(compileAndRun("10 PRINT 1/7"), " .1428571 ");
		assertEquals(compileAndRun("10 PRINT 1/8"), " .125 ");
		assertEquals(compileAndRun("10 PRINT 1/9"), " .1111111 ");
		assertEquals(compileAndRun("10 PRINT 1/10"), " .1 ");
		assertEquals(compileAndRun("10 PRINT 1/11"), " .0909090 ");
		assertEquals(compileAndRun("10 PRINT 1/12"), " .0833333 ");
	}

	@Test
	public void testFloatConversion_SimpleExponentParsing() {
		assertEquals(compileAndRun("10 PRINT 1.E+1"), " 10 ");
		assertEquals(compileAndRun("10 PRINT 1.0E+01"), " 10 ");
		assertEquals(compileAndRun("10 PRINT 1.E+01"), " 10 ");
		assertEquals(compileAndRun("10 PRINT 1.E+001"), " 10 ");

		assertEquals(compileAndRun("10 PRINT 1E-1"), " .1 ");
		assertEquals(compileAndRun("10 PRINT 1.E-1"), " .1 ");
		assertEquals(compileAndRun("10 PRINT 1.0E-1"), " .1 ");
		assertEquals(compileAndRun("10 PRINT 1.0E-01"), " .1 ");
		assertEquals(compileAndRun("10 PRINT 1.0E-001"), " .1 ");
	}

	@Test
	public void testFloatConversion_VAL_SimpleExponentParsing() {
		assertEquals(compileAndRun("10 PRINT VAL(\"1.E+1\")"), " 10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.0E+01\")"), " 10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.E+01\")"), " 10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.E+001\")"), " 10 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"1E-1\")"), " .1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.E-1\")"), " .1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.0E-1\")"), " .1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.0E-01\")"), " .1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.0E-001\")"), " .1 ");
	}

	@Test
	public void testFloatConversion_NearZero() {
		// Denormalization and underflow start below or equal (+/-)Float.MIN_NORMAL = (+/-)1.17549435E-38
		// Zero starts below or equal                         (+/-)Float.MIN_VALUE  = (+/-)1.4E-45

		assertEquals(compileAndRun("10 PRINT 1E-32"), " 1E-32 ");
		assertEquals(compileAndRun("10 PRINT 1E-33"), " 1E-33 ");
		assertEquals(compileAndRun("10 PRINT 1E-34"), " 1E-34 ");
		assertEquals(compileAndRun("10 PRINT 1E-35"), " 1E-35 ");
		assertEquals(compileAndRun("10 PRINT 1E-36"), " 1E-36 ");
		assertEquals(compileAndRun("10 PRINT 1E-37"), " 1E-37 ");
		// assertEquals(compileAndRun("10 PRINT 1E-38"), " 1E-38 ");  // was 0.999999E-38 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT 1E-39"), " 1E-39 ");
		// assertEquals(compileAndRun("10 PRINT 1E-40"), " 1E-40 ");  // was 0.999999E-38 (reasonable rounding error)
		// assertEquals(compileAndRun("10 PRINT 1E-41"), " 1E-41 ");  // was 9.999665E-42 (reasonable rounding error, underflow starts to creep in)
		// assertEquals(compileAndRun("10 PRINT 1E-42"), " 1E-42 ");  // was 1.000527E-42 (reasonable rounding error, underflow starts to creep in)
		// assertEquals(compileAndRun("10 PRINT 1E-43"), " 1E-43 ");  // was 9.949219E-44 (reasonable rounding error, underflow starts to creep in)
		// assertEquals(compileAndRun("10 PRINT 1E-44"), " 1E-44 ");  // was 9.809089E-45 (reasonable rounding error, underflow starts to creep in)
		// assertEquals(compileAndRun("10 PRINT 1.5E-45"), " 0 ");    // was 1.401298E-45 (reasonable rounding error, underflow starts to creep in)
		// assertEquals(compileAndRun("10 PRINT 1.4E-45"), " 0 ");    // was 1.401298E-45 (reasonable rounding error, underflow starts to creep in)
		// assertEquals(compileAndRun("10 PRINT 1.3E-45"), " 0 ");    // was 1.401298E-45 (reasonable rounding error, underflow starts to creep in)
		// assertEquals(compileAndRun("10 PRINT 1E-45"), " 1E-45 ");  // was 1.401298E-45 (reasonable rounding error, underflow starts to creep in)
		assertEquals(compileAndRun("10 PRINT 1E-46"), " 0 ");
	}

	@Test
	public void testFloatConversion_VAL_NearZero() {
		// Denormalization and underflow start below or equal (+/-)Float.MIN_NORMAL = (+/-)1.17549435E-38
		// Zero starts below or equal                         (+/-)Float.MIN_VALUE  = (+/-)1.4E-45

		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-32\")"), " 1E-32 ");  // was 0.999999E-32 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"1E-33\")"), " 1E-33 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E-34\")"), " 1E-34 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-35\")"), " 1E-35 ");  // was 0.999999E-35 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"1E-36\")"), " 1E-36 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E-37\")"), " 1E-37 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E-38\")"), " 1E-38 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-39\")"), " 1E-39 ");  // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-40\")"), " 1E-40 ");  // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-41\")"), " 1E-41 ");  // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-42\")"), " 1E-42 ");  // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-43\")"), " 1E-43 ");  // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-44\")"), " 1E-44 ");  // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1.5E-45\")"), " 0 ");    // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1.4E-45\")"), " 0 ");    // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1.3E-45\")"), " 0 ");    // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-45\")"), " 1E-45 ");  // TODO: was 0, improve Method_CharsToFloat
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E-46\")"), " 0 ");
	}

	@Test
	public void testFloatConversion_NearInfinity() {
		// Infinity starts at (+/-)Float.MAX_VALUE = (+/-)3.4028235e+38f

		assertEquals(compileAndRun("10 PRINT 1E+32"), " 1E+32 ");
		assertEquals(compileAndRun("10 PRINT 1E+33"), " 1E+33 ");
		assertEquals(compileAndRun("10 PRINT 1E+34"), " 1E+34 ");
		assertEquals(compileAndRun("10 PRINT 1E+35"), " 1E+35 ");
		// assertEquals(compileAndRun("10 PRINT 1E+36"), " 1E+36 "); // was 0.999999E+36 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT 1E+37"), " 1E+37 ");
		assertEquals(compileAndRun("10 PRINT 1E+38"), " 1E+38 ");
		assertEquals(compileAndRun("10 PRINT 3.4E+38"), " 3.4E+38 ");
		assertEquals(compileAndRun("10 PRINT 3.5E+38"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT 4E+38"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT 1E+39"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT 1E+40"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT 1E+100"), " Infinity ");

		assertEquals(compileAndRun("10 PRINT -1E+32"), "-1E+32 ");
		assertEquals(compileAndRun("10 PRINT -1E+33"), "-1E+33 ");
		assertEquals(compileAndRun("10 PRINT -1E+34"), "-1E+34 ");
		assertEquals(compileAndRun("10 PRINT -1E+35"), "-1E+35 ");
		// assertEquals(compileAndRun("10 PRINT -1E+36"), "-1E+36 "); // was -0.999999E+36 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT -1E+37"), "-1E+37 ");
		assertEquals(compileAndRun("10 PRINT -1E+38"), "-1E+38 ");
		assertEquals(compileAndRun("10 PRINT -3.4E+38"), "-3.4E+38 ");
		assertEquals(compileAndRun("10 PRINT -3.5E+38"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT -4E+38"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT -1E+39"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT -1E+40"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT -1E+100"), "-Infinity ");
	}

	@Test
	public void testFloatConversion_VAL_NearInfinity() {
		// Infinity starts at (+/-)Float.MAX_VALUE = (+/-)3.4028235e+38f

		// assertEquals(compileAndRun("10 PRINT VAL(\"1E+32\")"), " 1E+32 "); // was 0.999999E+32 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"1E+33\")"), " 1E+33 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E+34\")"), " 1E+34 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E+35\")"), " 1E+35 "); // was 0.999999E+35 (reasonable rounding error)
		// assertEquals(compileAndRun("10 PRINT VAL(\"1E+36\")"), " 1E+36 "); // was 0.999999E+36 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"1E+37\")"), " 1E+37 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E+38\")"), " 1E+38 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"3.4E+38\")"), " 3.4E+38 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"3.5E+38\")"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"4E+38\")"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E+39\")"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E+40\")"), " Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1E+100\")"), " Infinity ");

		// assertEquals(compileAndRun("10 PRINT VAL(\"-1E+32\")"), "-1E+32 "); // was -0.999999E+32 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"-1E+33\")"), "-1E+33 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1E+34\")"), "-1E+34 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"-1E+35\")"), "-1E+35 "); // was -0.999999E+35 (reasonable rounding error)
		// assertEquals(compileAndRun("10 PRINT VAL(\"-1E+36\")"), "-1E+36 "); // was -0.999999E+36 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"-1E+37\")"), "-1E+37 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1E+38\")"), "-1E+38 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-3.4E+38\")"), "-3.4E+38 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-3.5E+38\")"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-4E+38\")"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1E+39\")"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1E+40\")"), "-Infinity ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1E+100\")"), "-Infinity ");
	}

	@Test
	public void testFloatConversion_DivisionByZero() {
		assertEquals(compileAndRun("10 PRINT  1/0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT -1/0"), "Division by zero" + CR + "-Infinity ");
		assertEquals(compileAndRun("10 PRINT  0/0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT -0/0"), "Division by zero" + CR + " Infinity ");
	}

	@Test
	public void testFloatConversion_BASIC() {
		assertEquals(compileAndRun("10 PRINT 1/3"), " .3333333 ");
		assertEquals(compileAndRun("10 PRINT 1/7"), " .1428571 ");
		assertEquals(compileAndRun("10 PRINT 1000000"), " 1000000 ");
		assertEquals(compileAndRun("10 PRINT 10000000"), " 1E+07 ");
		assertEquals(compileAndRun("10 PRINT 12345678"), " 1.234567E+07 ");
		assertEquals(compileAndRun("10 PRINT 0.123456789"), " .1234567 ");
		assertEquals(compileAndRun("10 PRINT 0.0000001"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT 0.00000012"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT 0.00000001"), " 1E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.0000000123"), " 1.23E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.0000000123456789"), " 1.234567E-08 ");
	}

	@Test
	public void testFloatConversion_VAL_BASIC() {
		assertEquals(compileAndRun("10 PRINT VAL(\"1000000\")"), " 1000000 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"10000000\")"), " 1E+07 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12345678\")"), " 1.234567E+07 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.123456789\")"), " .1234567 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000001\")"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000012\")"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000001\")"), " 1E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000000123\")"), " 1.23E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000000123456789\")"), " 1.234567E-08 ");
	}

	@Test
	public void testFloatConversion() {
		assertEquals(compileAndRun("10 PRINT 1"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 12"), " 12 ");
		assertEquals(compileAndRun("10 PRINT 123"), " 123 ");
		assertEquals(compileAndRun("10 PRINT 1234"), " 1234 ");
		assertEquals(compileAndRun("10 PRINT 12345"), " 12345 ");
		assertEquals(compileAndRun("10 PRINT 123456"), " 123456 ");
		assertEquals(compileAndRun("10 PRINT 1234567"), " 1234567 ");
		assertEquals(compileAndRun("10 PRINT 12345678"), " 1.234567E+07 ");

		assertEquals(compileAndRun("10 PRINT 1.2"), " 1.2 ");
		assertEquals(compileAndRun("10 PRINT 1.23"), " 1.23 ");
		assertEquals(compileAndRun("10 PRINT 1.234"), " 1.234 ");
		assertEquals(compileAndRun("10 PRINT -1.234"), "-1.234 ");
		assertEquals(compileAndRun("10 PRINT 1.2345"), " 1.2345 ");
		assertEquals(compileAndRun("10 PRINT 1.23456"), " 1.23456 ");
		assertEquals(compileAndRun("10 PRINT 1.234567"), " 1.234567 ");
		assertEquals(compileAndRun("10 PRINT 1.2345678"), " 1.234567 ");

		assertEquals(compileAndRun("10 PRINT 12.3"), " 12.3 ");
		assertEquals(compileAndRun("10 PRINT 12.34"), " 12.34 ");
		assertEquals(compileAndRun("10 PRINT 12.345"), " 12.345 ");
		assertEquals(compileAndRun("10 PRINT 12.3456"), " 12.3456 ");
		assertEquals(compileAndRun("10 PRINT 12.34567"), " 12.34567 ");
		assertEquals(compileAndRun("10 PRINT 12.345678"), " 12.34567 ");

		assertEquals(compileAndRun("10 PRINT 0.1"), " .1 ");
		assertEquals(compileAndRun("10 PRINT 0.01"), " .01 ");
		assertEquals(compileAndRun("10 PRINT 0.001"), " .001 ");
		assertEquals(compileAndRun("10 PRINT 0.0001"), " .0001 ");
		// assertEquals(compileAndRun("10 PRINT 0.00001"), " .00001 ");  // 0.0000099
		assertEquals(compileAndRun("10 PRINT 0.000001"), " .000001 ");
		assertEquals(compileAndRun("10 PRINT 0.0000001"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT 0.00000001"), " 1E-08 ");

		assertEquals(compileAndRun("10 PRINT 0.1234"), " .1234 ");
		assertEquals(compileAndRun("10 PRINT 0.12345678"), " .1234567 ");
		assertEquals(compileAndRun("10 PRINT 0.123456789"), " .1234567 ");

		assertEquals(compileAndRun("10 PRINT 0.01"), " .01 ");
		assertEquals(compileAndRun("10 PRINT 0.01234"), " .01234 ");
		assertEquals(compileAndRun("10 PRINT 0.012345678"), " .0123456 ");

		assertEquals(compileAndRun("10 PRINT 0.001"), " .001 ");
		assertEquals(compileAndRun("10 PRINT 0.001234"), " .001234 ");
		assertEquals(compileAndRun("10 PRINT 0.001234567"), " .0012345 ");

		assertEquals(compileAndRun("10 PRINT 0.00000123456789"), " .0000012 ");
		assertEquals(compileAndRun("10 PRINT 0.00000001234"), " 1.234E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.0000000123456789"), " 1.234567E-08 ");

		assertEquals(compileAndRun("10 PRINT 0.000000123456789"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT 0.00000001"), " 1E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.00000001234"), " 1.234E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.0000000123456789"), " 1.234567E-08 ");

		assertEquals(compileAndRun("10 PRINT 0.3333"), " .3333 ");
		assertEquals(compileAndRun("10 PRINT 0.33333"), " .33333 ");
		// assertEquals(compileAndRun("10 PRINT 0.333333"), " .333333 ");  // actual " .3333329"
		assertEquals(compileAndRun("10 PRINT 0.3333333"), " .3333333 ");

		assertEquals(compileAndRun("10 PRINT 0.00000003"), " 3E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.000000033"), " 3.3E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.0000000333"), " 3.33E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.00000003333"), " 3.333E-08 ");
		// assertEquals(compileAndRun("10 PRINT 0.000000033333"), " 3.3333E-08 ");  // 3.3333E-08
		assertEquals(compileAndRun("10 PRINT 0.0000000333333"), " 3.33333E-08 ");
		assertEquals(compileAndRun("10 PRINT 0.00000003333333"), " 3.333333E-08 ");

		assertEquals(compileAndRun("10 PRINT 13000000"), " 1.3E+07 ");
		assertEquals(compileAndRun("10 PRINT 10000000"), " 1E+07 ");
		assertEquals(compileAndRun("10 PRINT 12340000"), " 1.234E+07 ");
		assertEquals(compileAndRun("10 PRINT 12345600"), " 1.23456E+07 ");
		assertEquals(compileAndRun("10 PRINT 12345678"), " 1.234567E+07 ");

		assertEquals(compileAndRun("10 PRINT 1300000000"), " 1.3E+09 ");
		assertEquals(compileAndRun("10 PRINT 3000000000"), " 3E+09 ");
		assertEquals(compileAndRun("10 PRINT -3000000000"), "-3E+09 ");

		assertEquals(compileAndRun("10 PRINT 10000000000"), " 1E+10 ");
		// assertEquals(compileAndRun("10 PRINT 13000000000"), " 1.3E+10 ");  // 1.299999E+10
		assertEquals(compileAndRun("10 PRINT 13300000000"), " 1.33E+10 ");
		assertEquals(compileAndRun("10 PRINT 13330000000"), " 1.333E+10 ");
		assertEquals(compileAndRun("10 PRINT 13333000000"), " 1.3333E+10 ");
		assertEquals(compileAndRun("10 PRINT 13333300000"), " 1.33333E+10 ");
		assertEquals(compileAndRun("10 PRINT 13333330000"), " 1.333333E+10 ");
		assertEquals(compileAndRun("10 PRINT 13333333000"), " 1.333333E+10 ");

		assertEquals(compileAndRun("10 PRINT 12340000000"), " 1.234E+10 ");
		assertEquals(compileAndRun("10 PRINT 12345600000"), " 1.23456E+10 ");
		assertEquals(compileAndRun("10 PRINT 12345678900"), " 1.234567E+10 ");
	}

	@Test
	public void testFloatConversion_VAL() {
		assertEquals(compileAndRun("10 PRINT VAL(\"1\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12\")"), " 12 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"123\")"), " 123 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1234\")"), " 1234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12345\")"), " 12345 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"123456\")"), " 123456 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1234567\")"), " 1234567 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12345678\")"), " 1.234567E+07 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"1.2\")"), " 1.2 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.23\")"), " 1.23 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.234\")"), " 1.234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1.234\")"), "-1.234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.2345\")"), " 1.2345 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.23456\")"), " 1.23456 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.234567\")"), " 1.234567 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.2345678\")"), " 1.234567 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"12.3\")"), " 12.3 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12.34\")"), " 12.34 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12.345\")"), " 12.345 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12.3456\")"), " 12.3456 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12.34567\")"), " 12.34567 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12.345678\")"), " 12.34567 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.1\")"), " .1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.01\")"), " .01 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.001\")"), " .001 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0001\")"), " .0001 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"0.00001\")"), " .00001 "); // was .0000099 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"0.000001\")"), " .000001 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000001\")"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000001\")"), " 1E-08 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.1234\")"), " .1234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.12345678\")"), " .1234567 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.123456789\")"), " .1234567 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.01\")"), " .01 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.01234\")"), " .01234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.012345678\")"), " .0123456 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.001\")"), " .001 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.001234\")"), " .001234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.001234567\")"), " .0012345 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000123456789\")"), " .0000012 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000001234\")"), " 1.234E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000000123456789\")"), " 1.234567E-08 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.000000123456789\")"), " .0000001 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000001\")"), " 1E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000001234\")"), " 1.234E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000000123456789\")"), " 1.234567E-08 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.3333\")"), " .3333 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.33333\")"), " .33333 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"0.333333\")"), " .333333 "); // was .3333329 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"0.3333333\")"), " .3333333 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000003\")"), " 3E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.000000033\")"), " 3.3E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000000333\")"), " 3.33E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000003333\")"), " 3.333E-08 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"0.000000033333\")"), " 3.3333E-08 "); // was 3.333299E-08 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"0.0000000333333\")"), " 3.33333E-08 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"0.00000003333333\")"), " 3.333333E-08 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"13000000\")"), " 1.3E+07 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"10000000\")"), " 1E+07 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12340000\")"), " 1.234E+07 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12345600\")"), " 1.23456E+07 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12345678\")"), " 1.234567E+07 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"1300000000\")"), " 1.3E+09 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"3000000000\")"), " 3E+09 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-3000000000\")"), "-3E+09 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"10000000000\")"), " 1E+10 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"13000000000\")"), " 1.3E+10 "); // was 1.299999E+10 (reasonable rounding error)
		assertEquals(compileAndRun("10 PRINT VAL(\"13300000000\")"), " 1.33E+10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"13330000000\")"), " 1.333E+10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"13333000000\")"), " 1.3333E+10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"13333300000\")"), " 1.33333E+10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"13333330000\")"), " 1.333333E+10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"13333333000\")"), " 1.333333E+10 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"12340000000\")"), " 1.234E+10 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"12345600000\")"), " 1.23456E+10 ");
		// assertEquals(compileAndRun("10 PRINT VAL(\"12345678900\")"), " 1.234567E+10 "); // was 1.234568E+10 (reasonable rounding error)
	}

	@Test
	public void testDEF_FN() {
		// OK
		assertEquals(compileAndRun("10 DEF FNA(X) = 1"), "");
		assertEquals(compileAndRun("10 DEF FNA(X) = X * X"), "");
		assertEquals(compileAndRun("10 DEF FNA(A$) = LEN(A$)"), "");
		assertEquals(compileAndRun("10 DEF FNA(X,Y) = X * Y"), "");

		// parsing errors with num DEF FN
		assertCompileError(compileAndRun("10 DEF"));
		assertCompileError(compileAndRun("10 DEF 1"));
		assertCompileError(compileAndRun("10 DEF A"));
		assertCompileError(compileAndRun("10 DEF FN"));
		assertCompileError(compileAndRun("10 DEF FNA"));
		assertCompileError(compileAndRun("10 DEF FNA("));
		assertCompileError(compileAndRun("10 DEF FNA(X"));
		assertCompileError(compileAndRun("10 DEF FNA()"));
		assertCompileError(compileAndRun("10 DEF FNA()=2"));
		assertCompileError(compileAndRun("10 DEF FNA(X)=X * X : PRINT FNA(\"HELLO\")"));
		assertCompileError(compileAndRun("10 DEF FNA(X,Y)=X * Y : PRINT FNA(2)"));
		assertCompileError(compileAndRun("10 DEF FNA(X,Y)=X * Y : PRINT FNA(2,3,5)"));
		assertCompileError(compileAndRun("10 DEF FNA(1)"));
		assertCompileError(compileAndRun("10 DEF FNA(X)"));
		assertCompileError(compileAndRun("10 DEF FNA(X)="));
		assertCompileError(compileAndRun("10 DEF FNA(X)=\"X\""));
		assertCompileError(compileAndRun("10 DEF FNA(1)=X"));
		assertCompileError(compileAndRun("10 DEF FN(X)=X"));
		assertCompileError(compileAndRun("10 DEF FN(X+1)=X"));
		assertCompileError(compileAndRun("10 DEF FN(X)= IF X = 1 THEN X = 2 ELSE X = 3"));
		assertCompileError(compileAndRun("10 DEF XNA(X)=X"));
		assertCompileError(compileAndRun("10 DEF FNA(X,)="));
		assertCompileError(compileAndRun("10 DEF FNA(X,Y)="));

		assertCompileError(compileAndRun("10 DEF = 1"));
		assertCompileError(compileAndRun("10 A = DEF"));

		// OK
		assertEquals(compileAndRun("10 DEF FNA$(X) = STR$(X)"), "");
		assertEquals(compileAndRun("10 DEF FNA$(X$) = \"X\""), "");
		assertEquals(compileAndRun("10 DEF FNA$(X$) = X$"), "");

		assertEquals(compileAndRun("10 DEF FNA$(X,Y) = \"X\" + STR$(X) + \"Y\" + STR$(Y)"), "");
		assertEquals(compileAndRun("10 DEF FNA$(X$,Y) = X$ + STR$(Y)"), "");

		// parsing errors with str DEF FN
		assertCompileError(compileAndRun("10 DEF \"X\""));
		assertCompileError(compileAndRun("10 DEF A$"));

		assertCompileError(compileAndRun("10 DEF FNA$"));
		assertCompileError(compileAndRun("10 DEF FNA$("));
		assertCompileError(compileAndRun("10 DEF FNA$(X"));
		assertCompileError(compileAndRun("10 DEF FNA$()"));
		assertCompileError(compileAndRun("10 DEF FNA$(1)"));
		assertCompileError(compileAndRun("10 DEF FNA$(X)"));
		assertCompileError(compileAndRun("10 DEF FNA$(X)="));
		assertCompileError(compileAndRun("10 DEF FNA$(X)=1"));
		assertCompileError(compileAndRun("10 DEF FNA$(1)=X"));
		assertCompileError(compileAndRun("10 DEF FN$(X)=X"));
		assertCompileError(compileAndRun("10 DEF XNA$(X)=X"));
		assertCompileError(compileAndRun("10 DEF FNA$(X,)="));
		assertCompileError(compileAndRun("10 DEF FNA$(X,Y)="));

		// duplicate DEF FN
		assertEquals(compileAndRun("10 DEF FNA(X)=X : DEF FNB(X)=X*X"), "");
		assertEquals(compileAndRun("10 DEF FNA(X)=X : DEF FNA$(X)=STR$(X)"), "");
		assertCompileError(compileAndRun("10 DEF FNA(X)=X : DEF FNA(X)=X"));
		assertCompileError(compileAndRun("10 DEF FNA(X)=X : DEF FNA(X,Y)=X"));

		// checking signatures of num DEF FN and FN
		assertEquals(compileAndRun("10 GOSUB 40" + CR + "20 A = FNA(1)" + CR + "30 END" + CR + "40 DEF FNA(X) = X" + CR + "50 RETURN"), "");
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A = FNA(1)" + CR + "30 END" + CR + "40 DEF FNA(X,Y) = X" + CR + "50 RETURN"));
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A = FNA(1, 2)" + CR + "30 END" + CR + "40 DEF FNA(X) = X" + CR + "50 RETURN"));
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A = FNA(1)" + CR + "30 END" + CR + "40 DEF FNA(X$) = STR(X$)" + CR + "50 RETURN"));
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A = FNA(1, 2)" + CR + "30 END" + CR + "40 DEF FNA(X, Y$) = X" + CR + "50 RETURN"));

		// checking signatures of str DEF FN and FN
		assertEquals(compileAndRun("10 GOSUB 40" + CR + "20 A$ = FNA$(1)" + CR + "30 END" + CR + "40 DEF FNA$(X) = \"X\"" + CR + "50 RETURN"), "");
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A$ = FNA$(1)" + CR + "30 END" + CR + "40 DEF FNA$(X,Y) = \"X\"" + CR + "50 RETURN"));
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A$ = FNA$(1, 2)" + CR + "30 END" + CR + "40 DEF FNA$(X) = \"X\"" + CR + "50 RETURN"));
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A$ = FNA$(1)" + CR + "30 END" + CR + "40 DEF FNA$(X$) = X$" + CR + "50 RETURN"));
		assertCompileError(compileAndRun("10 GOSUB 40" + CR + "20 A$ = FNA$(1, 2)" + CR + "30 END" + CR + "40 DEF FNA$(X, Y$) = Y$" + CR + "50 RETURN"));

		// Forbid num and str arrays starting with FN...
		assertCompileError(compileAndRun("10 DIM FNA(1)"));
		assertCompileError(compileAndRun("10 DIM FNA$(1)"));
		assertCompileError(compileAndRun("10 FNA(0) = 1"));
		assertCompileError(compileAndRun("10 FNA$(0) = \"X\""));

		// Permit variables (not array variables) with FN or FN$()
		assertEquals(compileAndRun("10 FN = 1"), "");
		assertEquals(compileAndRun("10 FNA = 1"), "");
		assertEquals(compileAndRun("10 FN$ = \"X\""), "");
		assertEquals(compileAndRun("10 FNA$ = \"X\""), "");

		// Permit arrays with FN() or FN$()
		assertEquals(compileAndRun("10 DIM FN(1)"), "");
		assertEquals(compileAndRun("10 DIM FN(2) : FN(1) = 1"), "");
		assertEquals(compileAndRun("10 DIM FN$(1)"), "");
		assertEquals(compileAndRun("10 DIM FN$(2) : FN$(1) = \"X\""), "");

		// illegal function assignment
		assertEquals(compileAndRun("10 FNA = 1"), "");
		assertEquals(compileAndRun("10 FNA$ = \"X\""), "");

		assertCompileError(compileAndRun("10 FNA(1) = 1"));
		assertCompileError(compileAndRun("10 FNA(X) = 1"));
		assertCompileError(compileAndRun("10 FNA$(1) = \"X\""));
		assertCompileError(compileAndRun("10 FNA$(X) = \"X\""));

		// function undefined
		assertCompileError(compileAndRun("10 A = FNA(X)"));
		assertCompileError(compileAndRun("10 A$ = FNA$(X)"));

		// function undefined and wrong type
		assertCompileError(compileAndRun("10 A = FNA$(X)"));
		assertCompileError(compileAndRun("10 A$ = FNA(X)"));
	}

	@Test
	public void testFN() {
		assertEquals(compileAndRun("10 DEF FNA(X) = X * X: A = FNA(2) : PRINT A"), " 4 ");
		assertEquals(compileAndRun("10 DEF FNA$(X$,Y$) = X$ + \" \" + Y$ : A$ = FNA$(\"HELLO\",\"WORLD\") : PRINT A$"), "HELLO WORLD");
		assertEquals(compileAndRun("10 DEF FNA$(X$,A) = LEFT$(X$, A) : A$ = FNA$(\"HELLO WORLD\", 5) : PRINT A$"), "HELLO");
		assertEquals(compileAndRun("10 DEF FNA$(X$) = X$ + \"!\": A$ = FNA$(\"HELLO\") : PRINT A$"), "HELLO!");

		// undefined at runtime
		assertRuntimeError(compileAndRun("10 REM *** GOSUB 40" + CR + "20 A = FNA(2) : PRINT A" + CR + "30 END" + CR + "40 DEF FNA(X) = X * X" + CR + "50 RETURN"));

		// Binding global number variables
		assertEquals(compileAndRun("10 DEF FNA(X) = X + Y : A = FNA(1) : PRINT A"), " 1 ");
		assertEquals(compileAndRun("10 Y = 1 : DEF FNA(X) = X + Y : A = FNA(1) : PRINT A"), " 2 ");
		assertEquals(compileAndRun("10 DEF FNA(X) = X + Y : Y = 1 : A = FNA(1) : PRINT A"), " 2 ");
		assertEquals(compileAndRun("10 DEF FNA(X) = X + Y : A = FNA(1) : Y = 1 : PRINT A"), " 1 ");

		// Binding global string variables
		assertEquals(compileAndRun("10 DEF FNA$(X$) = X$ + Y$ : A$ = FNA$(\"A\") : PRINT A$"), "A");
		assertEquals(compileAndRun("10 Y$ = \"B\" : DEF FNA$(X$) = X$ + Y$ : A$ = FNA$(\"A\") : PRINT A$"), "AB");
		assertEquals(compileAndRun("10 DEF FNA$(X$) = X$ + Y$ : Y$ = \"B\" : A$ = FNA$(\"A\") : PRINT A$"), "AB");
		assertEquals(compileAndRun("10 DEF FNA$(X$) = X$ + Y$ : A$ = FNA$(\"A\") : Y$ = \"B\" : PRINT A$"), "A");

		// typo in DEF FN, referencing a global string by mistake
		assertEquals(compileAndRun("10 DEF FNA$(X$,A) = LEFT$(A$, A) : A$ = FNA$(\"HELLO WORLD\", 5) : PRINT A$"), "");

		// Binding global number arrays
		assertEquals(compileAndRun("10 DIM Y(1) : DEF FNA(X) = X + Y(0) : A = FNA(1) : PRINT A"), " 1 ");
		assertEquals(compileAndRun("10 DEF FNA(X) = X + Y(0) : A = FNA(1) : PRINT A"), " 1 ");
		assertEquals(compileAndRun("10 DEF FNA(X) = X + Y(0) : DIM Y(1) : A = FNA(1) : PRINT A"), " 1 ");
		assertRuntimeError(compileAndRun("10 DEF FNA(X) = X + Y(0) : A = FNA(1) : DIM Y(1) : PRINT A"));

		assertEquals(compileAndRun("10 DIM Y(1) : Y(0) = 2 : DEF FNA(X) = X + Y(0) : A = FNA(1) : PRINT A"), " 3 ");
		assertEquals(compileAndRun("10 DEF FNA(X) = X + Y(0) : DIM Y(1) : Y(0) = 2 : A = FNA(1) : PRINT A"), " 3 ");
		assertRuntimeError(compileAndRun("10 DEF FNA(X) = X + Y(0) : A = FNA(1) : DIM Y(1) : Y(0) = 2 : PRINT A"));

		// Binding global string arrays
		assertEquals(compileAndRun("10 DIM Y$(1) : DEF FNA$(X$) = X$ + Y$(0) : A$ = FNA$(\"X\") : PRINT A$"), "X");
		assertEquals(compileAndRun("10 DEF FNA$(X$) = X$ + Y$(0) : A$ = FNA$(\"X\") : PRINT A$"), "X");
		assertEquals(compileAndRun("10 DEF FNA$(X$) = X$ + Y$(0) : DIM Y$(1) : A$ = FNA$(\"X\") : PRINT A$"), "X");
		assertRuntimeError(compileAndRun("10 DEF FNA$(X$) = X$ + Y$(0) : A$ = FNA$(\"X\") : DIM Y$(1) : PRINT A$"));

		assertEquals(compileAndRun("10 DIM Y$(1) : Y$(0) = \"Y\" : DEF FNA$(X$) = X$ + Y$(0) : A$ = FNA$(\"X\") : PRINT A$"), "XY");
		assertEquals(compileAndRun("10 DIM Y$(1) : DEF FNA$(X$) = X$ + Y$(0) : Y$(0) = \"Y\" : A$ = FNA$(\"X\") : PRINT A$"), "XY");
		assertEquals(compileAndRun("10 DIM Y$(1) : DEF FNA$(X$) = X$ + Y$(0) : A$ = FNA$(\"X\") : Y$(0) = \"Y\" : PRINT A$"), "X");
	}

	@Test
	public void testDIMNumArrays() {
		assertEquals(compileAndRun("10 DIM A(1)"), "");
		assertEquals(compileAndRun("10 DIM A(1,1)"), "");
		assertEquals(compileAndRun("10 DIM A(1),B(1)"), "");
		assertEquals(compileAndRun("10 DIM A(1), B(1)"), "");
		assertEquals(compileAndRun("10 DIM A(1) , B(1)"), "");

		assertCompileError(compileAndRun("10 DIM "));
		assertCompileError(compileAndRun("10 DIM A"));
		assertCompileError(compileAndRun("10 DIM A("));
		assertCompileError(compileAndRun("10 DIM A()"));
		assertCompileError(compileAndRun("10 DIM A(1"));
		assertCompileError(compileAndRun("10 DIM A(1,"));
		assertCompileError(compileAndRun("10 DIM A(1,)"));
		assertCompileError(compileAndRun("10 DIM A(1,1"));
		assertCompileError(compileAndRun("10 DIM A(1,1,"));
		assertCompileError(compileAndRun("10 DIM A(1,1,)"));
		assertCompileError(compileAndRun("10 DIM A(1,1,1"));
		assertCompileError(compileAndRun("10 DIM A(1,1,1,"));
		assertCompileError(compileAndRun("10 DIM A(1,1,1,)"));

		assertRuntimeError(compileAndRun("10 DIM A(-1)"));
		assertEquals(compileAndRun("10 DIM A(0)"), "");
		assertEquals(compileAndRun("10 DIM A(32767)"), "");
		assertRuntimeError(compileAndRun("10 DIM A(32768)"));

		assertRuntimeError(compileAndRun("10 DIM A(-1, 1)"));
		assertEquals(compileAndRun("10 DIM A(0, 1)"), "");
		assertEquals(compileAndRun("10 DIM A(32767, 1)"), "");
		assertRuntimeError(compileAndRun("10 DIM A(32768, 1)"));

		assertRuntimeError(compileAndRun("10 DIM A(1, -1)"));
		assertEquals(compileAndRun("10 DIM A(1, 0)"), "");
		assertEquals(compileAndRun("10 DIM A(1, 32767)"), "");
		assertRuntimeError(compileAndRun("10 DIM A(1, 32768)"));

		assertCompileError(compileAndRun("10 DIM A(10), A(10)"));
		assertCompileError(compileAndRun("10 DIM A(10) : DIM A(10)"));
		assertCompileError(compileAndRun("10 DIM A(10) : DIM A(10,10)"));

		assertEquals(compileAndRun("10 DIM A(10) : DIM A$(10)"), "");
		assertEquals(compileAndRun("10 DIM A(10) : DIM A$(10,10)"), "");
		assertEquals(compileAndRun("10 DIM A(10,10) : DIM A$(10)"), "");
		assertEquals(compileAndRun("10 DIM A(10,10) : DIM A$(10,10)"), "");

		assertCompileError(compileAndRun("10 DIM A(10) : A(0,1) = 0"));
		assertCompileError(compileAndRun("10 A(0,1) = 0 : DIM A(10)"));
		assertCompileError(compileAndRun("10 A(0) = 0 : A(0,1) = 0)"));
		assertRuntimeError(compileAndRun("10 A(0) = 0 : DIM A(10)"));
	}

	@Test
	public void testDIMStrArrays() {
		assertEquals(compileAndRun("10 DIM A$(1)"), "");
		assertEquals(compileAndRun("10 DIM A$(1,1)"), "");
		assertEquals(compileAndRun("10 DIM A$(1),B$(1)"), "");
		assertEquals(compileAndRun("10 DIM A$(1), B$(1)"), "");
		assertEquals(compileAndRun("10 DIM A$(1) , B$(1)"), "");

		assertCompileError(compileAndRun("10 DIM "));
		assertCompileError(compileAndRun("10 DIM A$"));
		assertCompileError(compileAndRun("10 DIM A$("));
		assertCompileError(compileAndRun("10 DIM A$()"));
		assertCompileError(compileAndRun("10 DIM A$(1"));
		assertCompileError(compileAndRun("10 DIM A$(1,"));
		assertCompileError(compileAndRun("10 DIM A$(1,)"));
		assertCompileError(compileAndRun("10 DIM A$(1,1"));
		assertCompileError(compileAndRun("10 DIM A$(1,1,"));
		assertCompileError(compileAndRun("10 DIM A$(1,1,)"));
		assertCompileError(compileAndRun("10 DIM A$(1,1,1"));
		assertCompileError(compileAndRun("10 DIM A$(1,1,1,"));
		assertCompileError(compileAndRun("10 DIM A$(1,1,1,)"));

		assertRuntimeError(compileAndRun("10 DIM A$(-1)"));
		assertEquals(compileAndRun("10 DIM A$(0)"), "");
		assertEquals(compileAndRun("10 DIM A$(32767)"), "");
		assertRuntimeError(compileAndRun("10 DIM A$(32768)"));

		assertRuntimeError(compileAndRun("10 DIM A$(-1, 1)"));
		assertEquals(compileAndRun("10 DIM A$(0, 1)"), "");
		assertEquals(compileAndRun("10 DIM A$(32767, 1)"), "");
		assertRuntimeError(compileAndRun("10 DIM A$(32768, 1)"));

		assertRuntimeError(compileAndRun("10 DIM A$(1,-1)"));
		assertEquals(compileAndRun("10 DIM A$(1,0)"), "");
		assertEquals(compileAndRun("10 DIM A$(1,32767)"), "");
		assertRuntimeError(compileAndRun("10 DIM A$(1,32768)"));

		assertCompileError(compileAndRun("10 DIM A$(10), A$(10)"));
		assertCompileError(compileAndRun("10 DIM A$(10) : DIM A$(10)"));
		assertCompileError(compileAndRun("10 DIM A$(10) : DIM A$(10,10)"));

		assertCompileError(compileAndRun("10 DIM A$(10) : A$(0,1) = \"HELLO\""));
		assertCompileError(compileAndRun("10 A$(0,1) = \"HELLO\" : DIM A$(10)"));
		assertCompileError(compileAndRun("10 A$(0) = \"HELLO\" : A$(0,1) = \"HELLO\")"));
		assertRuntimeError(compileAndRun("10 A$(0) = \"HELLO\" : DIM A$(10)"));
	}

	@Test
	public void testEND() {
		assertEquals(compileAndRun("10 X = 0" + CR + "20 IF X = 5 THEN END" + CR + "30 PRINT X; : X = X + 1 : GOTO 20"), " 0  1  2  3  4 ");
	}

	@Test
	public void testFOR_NEXT() {

		// regular cases
		assertEquals(compileAndRun("10 FOR I = 0 TO 4 : PRINT I; : NEXT I"), " 0  1  2  3  4 ");
		assertEquals(compileAndRun("10 FOR I = 0 TO 4 STEP 2 : PRINT I; : NEXT I"), " 0  2  4 ");
		assertEquals(compileAndRun("10 FOR I = 4 TO 0 STEP -1 : PRINT I; : NEXT I"), " 4  3  2  1  0 ");
		assertEquals(compileAndRun("10 FOR I = 4 TO 0 STEP -2 : PRINT I; : NEXT I"), " 4  2  0 ");

		assertEquals(compileAndRun("10 FOR I = 0 TO 1 : FOR J = 0 TO 2 : PRINT I;J; : NEXT J : NEXT I"), " 0  0  0  1  0  2  1  0  1  1  1  2 ");

		// reject loop
		assertEquals(compileAndRun("10 FOR I = 4 TO 0 STEP 1 : PRINT I; : NEXT I"), "");
		assertEquals(compileAndRun("10 FOR I = 0 TO 4 STEP -1 : PRINT I; : NEXT I"), "");

		// partial loops
		assertEquals(compileAndRun("10 FOR I = 0 TO 4 : PRINT I;"), " 0 ");
		assertEquals(compileAndRun("10 FOR I = 0 TO 1 : FOR J = 0 TO 2 : PRINT I;J; : NEXT"), " 0  0  0  1  0  2 ");

		// syntactic peculiarities
		assertEquals(compileAndRun("10 FOR I = 0 TO 4 : PRINT I; : NEXT"), " 0  1  2  3  4 ");

		assertEquals(compileAndRun("10 FOR I = 0 TO 1 : FOR J = 0 TO 2 : PRINT I;J; : NEXT J,I"), " 0  0  0  1  0  2  1  0  1  1  1  2 ");
		assertEquals(compileAndRun("10 FOR I = 0 TO 1 : FOR J = 0 TO 2 : PRINT I;J; : NEXT : NEXT"), " 0  0  0  1  0  2  1  0  1  1  1  2 ");

		assertEquals(compileAndRun("10 ST = 2 : FOR I = 0 TO 4 STEP ST: PRINT I; : ST = 3 : NEXT"), " 0  2  4 ");
		assertEquals(compileAndRun("10 EN = 4 : FOR I = 0 TO EN : PRINT I; : EN = 10 : NEXT"), " 0  1  2  3  4 ");

		assertEquals(compileAndRun("10 FOR I = 0 TO 1 : FOR J = 0 TO 1 : FOR K = 0 TO 1 : FOR L = 0 TO 1 : NEXT L : NEXT K : NEXT J : NEXT I"), "");

		// syntax errors
		assertCompileError(compileAndRun("10 FOR I = 0 TO 1 : FOR J = 0 TO 2 : PRINT I;J; : NEXT I : NEXT J"));
		assertCompileError(compileAndRun("10 FOR I = 0 TO 1 : FOR J = 0 TO 2 : PRINT I;J; : NEXT I,J,"));

		assertCompileError(compileAndRun("10 FOR = 0 TO 4 : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR 1 = 0 TO 4 : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR \"XXX\" = 0 TO 4 : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR A$ = 0 TO 4 : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 DIM A(5) : FOR A(0) = 0 TO 4 : PRINT I; : NEXT I"));

		assertCompileError(compileAndRun("10 FOR I 0 TO 4 : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = \"XXX\" TO 4 : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = A$ TO 4 : PRINT I; : NEXT I"));

		assertCompileError(compileAndRun("10 FOR I = 0 4 : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = 0 TO A$ : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = 0 TO \"XXX\" : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = 0 TO 4 STEP : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = 0 TO 4 STEP A$ : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = 0 TO 4 STEP \"XXX\" : PRINT I; : NEXT I"));

		assertCompileError(compileAndRun("10 FOR I = : PRINT I; : NEXT I"));
		assertCompileError(compileAndRun("10 FOR I = 1 TO : PRINT I; : NEXT I"));

		assertCompileError(compileAndRun("10 NEXT"));
	}

	@Test
	public void testGOTO() {
		assertEquals(compileAndRun("10 GOTO 30" + CR + "20 PRINT \"HELLO\"" + CR + "30 PRINT \"WORLD!\"" + CR), "WORLD!");

		assertCompileError(compileAndRun("10 GOTO XXX"));
	}

	@Test
	public void testGOSUB() {
		assertEquals(compileAndRun("10 GOSUB 30" + CR + "20 GOTO 50" + CR + "30 PRINT \"30\"" + CR + "40 RETURN" + CR + "50 PRINT \"50\"" + CR), "30" + CR + "50");
		assertEquals(compileAndRun("10 GOSUB 30" + CR + "20 GOTO 50" + CR + "30 PRINT 30" + CR + "40 RETURN" + CR + "50 PRINT 50" + CR), " 30 " + CR + " 50 ");
		assertEquals(compileAndRun("10 GOSUB 30" + CR + "20 GOTO 50" + CR + "30 PRINT \"30\"" + CR + "40 RETURN" + CR + "50 PRINT \"50\"" + CR), "30" + CR + "50");
		assertEquals(compileAndRun("10 GOSUB 30" + CR + "20 GOTO 40" + CR + "30 RETURN" + CR + "40 PRINT \"40\";" + CR + "50 PRINT \"50\"" + CR), "4050");

		// test tableswitch padding
		assertEquals(compileAndRun("10 X = 1 : GOSUB 30" + CR + "20 GOTO 40" + CR + "30 RETURN" + CR + "40 PRINT 40"), " 40 ");

		assertEquals(compileAndRun("10 GOSUB 30" + CR + "15 GOSUB 40" + CR + "20 GOTO 80" + CR + "30 GOSUB 50" + CR + "40 RETURN" + CR + "50 PRINT \"50\";" + CR + "60 RETURN" + CR + "80 PRINT \"80\""), "5080");
		assertEquals(compileAndRun("10 GOSUB 40" + CR + "20 GOSUB 50" + CR + "30 GOTO 60" + CR + "40 GOSUB 50" + CR + "50 RETURN" + CR + "60 PRINT \"X\""), "X");
		assertEquals(compileAndRun("10 GOSUB 30" + CR + "20 GOTO 60" + CR + "30 GOSUB 50" + CR + "40 RETURN" + CR + "50 RETURN" + CR + "60 PRINT \"END\""), "END");

		assertCompileError(compileAndRun("10 GOSUB XXX"));
		assertRuntimeError(compileAndRun("10 RETURN"));
		assertRuntimeError(compileAndRun("10 RETURN"));
	}

	@Test
	public void testIF_THEN() {
		assertEquals(compileAndRun("10 IF 0 < 1 THEN PRINT \"ABC\";" + CR + "20 PRINT \"DEF\""), "ABCDEF");
		assertEquals(compileAndRun("10 IF 0 <= 1 THEN PRINT \"ABC\";" + CR + "20 PRINT \"DEF\""), "ABCDEF");
		assertEquals(compileAndRun("10 IF 0 <> 1 THEN PRINT \"ABC\";" + CR + "20 PRINT \"DEF\""), "ABCDEF");
		assertEquals(compileAndRun("10 IF 0 = 1 THEN PRINT \"ABC\";" + CR + "20 PRINT \"DEF\""), "DEF");
		assertEquals(compileAndRun("10 IF 0 >= 1 THEN PRINT \"ABC\";" + CR + "20 PRINT \"DEF\""), "DEF");
		assertEquals(compileAndRun("10 IF 0 > 1 THEN PRINT \"ABC\";" + CR + "20 PRINT \"DEF\""), "DEF");

		assertEquals(compileAndRun("10 IF 0 < 1 THEN PRINT \"HELLO\" : PRINT \"SAILOR\"" + CR + "20 PRINT \"END\"" + CR), "HELLO" + CR + "SAILOR" + CR + "END");
		assertEquals(compileAndRun("10 IF 0 > 1 THEN PRINT \"HELLO\" : PRINT \"SAILOR\"" + CR + "20 PRINT \"END\"" + CR), "END");
	}

	@Test
	public void testIF_THEN_ELSE() {
		assertEquals(compileAndRun("10 A = 0 : B = 1 : IF A > B THEN PRINT \">\" ELSE IF A < B THEN PRINT \"<\" ELSE PRINT \"=\""), "<");
		assertEquals(compileAndRun("10 A = 1 : B = 1 : IF A > B THEN PRINT \">\" ELSE IF A < B THEN PRINT \"<\" ELSE PRINT \"=\""), "=");
		assertEquals(compileAndRun("10 A = 1 : B = 0 : IF A > B THEN PRINT \">\" ELSE IF A < B THEN PRINT \"<\" ELSE PRINT \"=\""), ">");

		assertEquals(compileAndRun("10 A = 0 : B = 0 : C = 0 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "A = C");
		assertEquals(compileAndRun("10 A = 0 : B = 0 : C = 1 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "A <> C");
		assertEquals(compileAndRun("10 A = 0 : B = 1 : C = 0 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "");
		assertEquals(compileAndRun("10 A = 0 : B = 1 : C = 1 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "");
		assertEquals(compileAndRun("10 A = 1 : B = 0 : C = 0 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "");
		assertEquals(compileAndRun("10 A = 1 : B = 0 : C = 1 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "");
		assertEquals(compileAndRun("10 A = 1 : B = 1 : C = 0 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "A <> C");
		assertEquals(compileAndRun("10 A = 1 : B = 1 : C = 1 : IF A = B THEN IF B = C THEN PRINT \"A = C\" ELSE PRINT \"A <> C\""), "A = C");

		assertEquals(compileAndRun("10 FOR I = 0 TO 9 : IF I > 0 THEN PRINT \"X\"; ELSE PRINT \".\"; : NEXT I"), ".X");
		assertEquals(compileAndRun("10 FOR I = 0 TO 9" + CR + "20 IF I<5 THEN PRINT \".\"; ELSE PRINT \"X\";" + CR + "30 NEXT I" + CR), ".....XXXXX");
		assertEquals(compileAndRun("10 FOR I = 0 TO 9" + CR + "20 IF (I>3) * (I<6) THEN PRINT \"X\"; ELSE PRINT \".\";" + CR + "30 NEXT I" + CR), "....XX....");

		assertEquals(compileAndRun("10 I = 0 : IF I = 0 GOTO 20 ELSE 30" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;"), " 20  30 ");
		assertEquals(compileAndRun("10 I = 0 : IF I = 0 THEN 20 ELSE 30" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;"), " 20  30 ");

		// multiple statements in THEN and ELSE clause
		assertEquals(compileAndRun("10 A = 1 : B = 1 : IF A = B THEN A = 3 : B = 4 : PRINT A;B ELSE A = 5 : B = 6 : PRINT A;B"), " 3  4 ");
		assertEquals(compileAndRun("10 A = 0 : B = 1 : IF A = B THEN A = 3 : B = 4 : PRINT A;B ELSE A = 5 : B = 6 : PRINT A;B"), " 5  6 ");

		assertCompileError(compileAndRun("10 I = 0 : IF I = 0 GOTO ELSE 30" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;"));
		assertCompileError(compileAndRun("10 I = 0 : IF I = 0 GOTO XXX ELSE 30" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;"));

		// syntax errors
		assertCompileError(compileAndRun("10 IF THEN PRINT \"TRUE\" ELSE PRINT \"FALSE\""));
		assertCompileError(compileAndRun("10 IF 0 > 1 THEN ELSE PRINT \"FALSE\""));
		assertCompileError(compileAndRun("10 IF 0 > 1 PRINT \"TRUE\" ELSE PRINT \"FALSE\""));
		assertCompileError(compileAndRun("10 IF 0 > 1 ELSE PRINT \"FALSE\""));
	}

	@Test
	public void testIF_THEN_Conditions() {
		assertEquals(compileAndRun("10 IF 0 < 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 0 <= 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 0 = 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 0 <> 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 0 >= 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 0 > 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");

		assertEquals(compileAndRun("10 IF 0 < 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 0 <= 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 0 = 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 0 <> 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 0 >= 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 0 > 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");

		assertEquals(compileAndRun("10 IF 1 < 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 1 <= 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 1 = 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 1 <> 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 1 >= 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 1 > 0 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");

		assertEquals(compileAndRun("10 IF 1 < 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 1 <= 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 1 = 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 1 <> 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
		assertEquals(compileAndRun("10 IF 1 >= 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "SAILOR");
		assertEquals(compileAndRun("10 IF 1 > 1 THEN 30" + CR + "20 PRINT \"HELLO\";" + CR + "30 PRINT \"SAILOR\""), "HELLOSAILOR");
	}

	@Test
	public void testINPUT() {

		// basics
		assertEquals(compileAndRun("10 INPUT A : PRINT A", "1"), "? 1 ");
		assertEquals(compileAndRun("10 INPUT \"YOUR INPUT\";A : PRINT A", "1"), "YOUR INPUT? 1 ");
		assertEquals(compileAndRun("10 INPUT \"YOUR INPUT\",A : PRINT A", "1"), "YOUR INPUT 1 ");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC"), "?ABC");
		assertEquals(compileAndRun("10 INPUT \"YOUR INPUT\";A$ : PRINT A$", "ABC"), "YOUR INPUT?ABC");
		assertEquals(compileAndRun("10 INPUT \"YOUR INPUT\",A$ : PRINT A$", "ABC"), "YOUR INPUTABC");

		// single number input
		assertEquals(compileAndRun("10 INPUT A : PRINT A", "XXX"), "??Redo from start");

		assertEquals(compileAndRun("10 INPUT A : PRINT A", " 1"), "? 1 ");
		assertEquals(compileAndRun("10 INPUT A : PRINT A", "1 "), "? 1 ");
		assertEquals(compileAndRun("10 INPUT A : PRINT A", " 1 "), "? 1 ");

		// empty input
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\""), "?");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"\""), "?");

		// quotes
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"\"\""), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"\"\"\""), "??Redo from start");

		// single string input

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\"XX"), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC\"XX"), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\"XX "), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC\"XX "), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "  \"ABC\"XX"), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\"XX  "), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "  \"ABC\"XX  "), "??Redo from start");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\"\""), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC\"\""), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\"\" "), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC\"\" "), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "  \"ABC\"\""), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\"\"  "), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "  \"ABC\"\"  "), "??Redo from start");

		// compile errors
		assertCompileError(compileAndRun("10 INPUT \"Enter a string\" A$"));
		assertCompileError(compileAndRun("10 INPUT ,A$"));
		assertCompileError(compileAndRun("10 INPUT ;A$"));
		assertCompileError(compileAndRun("10 INPUT \"Enter a \" + \"string\";A$"));
	}

	@Test
	public void testINPUT2() {
		assertEquals(compileAndRun("10 INPUT A : PRINT A", "1"), "? 1 ");
		assertEquals(compileAndRun("10 INPUT A : PRINT A", "-2"), "?-2 ");
		assertEquals(compileAndRun("10 INPUT A : PRINT A", ""), "? 0 ");
		assertEquals(compileAndRun("10 INPUT A : PRINT A", " "), "? 0 ");
		assertEquals(compileAndRun("10 INPUT A : PRINT A", "  "), "? 0 ");

		assertEquals(compileAndRun("10 INPUT A : PRINT A", ","), "??Redo from start");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC"), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", ""), "?");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " "), "?");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "  "), "?");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC"), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC "), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " ABC"), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " ABC "), "?ABC");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC DEF"), "?ABC DEF");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " ABC DEF"), "?ABC DEF");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC DEF "), "?ABC DEF");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " ABC DEF "), "?ABC DEF");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC DEF\""), "?ABC DEF");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC DEF\""), "?ABC DEF");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC DEF\" "), "?ABC DEF");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC DEF\" "), "?ABC DEF");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC"), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC"), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC "), "?ABC ");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC "), "?ABC ");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC\""), "?ABC\"");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " ABC\""), "?ABC\"");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "ABC\" "), "?ABC\"");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " ABC\" "), "?ABC\"");

		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\""), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC\""), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", "\"ABC\" "), "?ABC");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT A$", " \"ABC\" "), "?ABC");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC,"), "?|ABC||");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC,"), "?|ABC||");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC ,"), "?|ABC||");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC ,"), "?|ABC||");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", ",ABC"), "?||ABC|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", ", ABC"), "?||ABC|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", ",ABC "), "?||ABC|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", ", ABC "), "?||ABC|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC,DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC, DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC,DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC, DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC,DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC, DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC,DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC, DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC ,DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC , DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC ,DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC , DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC ,DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC , DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC ,DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC , DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\",DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\", DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\",DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\", DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\",DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\", DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\",DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\", DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" ,DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" , DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" ,DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" , DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" ,DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" , DEF"), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" ,DEF "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" , DEF "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC,\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC, \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC,\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC, \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC,\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC, \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC,\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC, \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC ,\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC , \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC ,\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "ABC , \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC ,\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC , \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC ,\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ABC , \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\",\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\", \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\",\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\", \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\",\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\", \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\",\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\", \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" ,\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" , \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" ,\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" , \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" ,\"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" , \"DEF\""), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" ,\"DEF\" "), "?|ABC|DEF|");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " \"ABC\" , \"DEF\" "), "?|ABC|DEF|");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\"xxx,DEF"), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" xxx,DEF"), "??Redo from start");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"ABC\" xxx"), "??Redo from start");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", ","), "?|||");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " ,"), "?|||");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", ", "), "?|||");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", " , "), "?|||");

		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", "\"A,C\","), "?|A,C||");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT \"|\";A$;\"|\"", "\"A,C,"), "?|A,C,|");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT \"|\";A$;\"|\"", "\"A,,C,,"), "?|A,,C,,|");
		assertEquals(compileAndRun("10 INPUT A$ : PRINT \"|\";A$;\"|\"", "xx\"xx\"xx"), "?|xx\"xx\"xx|");

		assertEquals(compileAndRun("10 INPUT A$,B$,C$ : PRINT \"|\";A$;\"|\";B$;\"|\";C$;\"|\"", ",\"A,C\","), "?||A,C||");
		assertEquals(compileAndRun("10 INPUT A$,B$ : PRINT \"|\";A$;\"|\";B$;\"|\"", ",\"A,C,"), "?||A,C,|");
	}

	@Test
	public void testLET() {
		assertEquals(compileAndRun("10 LET A = 2 * 3 : PRINT A"), " 6 ");
		assertEquals(compileAndRun("10 LET A$ = \"ABCD\" : PRINT A$"), "ABCD");
		assertCompileError(compileAndRun("10 LET A$ = 1"));
		assertCompileError(compileAndRun("10 LET A = \"ABCD\""));
	}

	@Test
	public void testON_GOSUB() {
		assertEquals(compileAndRun("10 X = 0 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"), " 10  50 ");
		assertEquals(compileAndRun("10 X = 1 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"), " 20  10  50 ");
		assertEquals(compileAndRun("10 X = 2 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"), " 30  10  50 ");
		assertEquals(compileAndRun("10 X = 3 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"), " 40  10  50 ");
		assertEquals(compileAndRun("10 X = 4 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"), " 10  50 ");

		assertEquals(compileAndRun("10 X = 1.5 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"), " 30  10  50 ");
		assertEquals(compileAndRun("10 X = 255 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"), " 10  50 ");

		assertRuntimeError(compileAndRun("10 X = -1 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertRuntimeError(compileAndRun("10 X = 256 : ON X GOSUB 20, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));

		assertCompileError(compileAndRun("10 X = 1 : ON : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X GOSUB : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X GOSUB PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X GOSUB 20, 30, : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X GOSUB 25, 30, 40 : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
	}

	@Test
	public void testON_GOTO() {
		assertEquals(compileAndRun("10 X = 0 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"), " 10  20  30  40  50 ");
		assertEquals(compileAndRun("10 X = 1 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"), " 20  30  40  50 ");
		assertEquals(compileAndRun("10 X = 2 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"), " 30  40  50 ");
		assertEquals(compileAndRun("10 X = 3 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"), " 40  50 ");
		assertEquals(compileAndRun("10 X = 4 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"), " 10  20  30  40  50 ");

		assertEquals(compileAndRun("10 X = 1.5 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"), " 30  40  50 ");
		assertEquals(compileAndRun("10 X = 255 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"), " 10  20  30  40  50 ");

		assertRuntimeError(compileAndRun("10 X = -1 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"));
		assertRuntimeError(compileAndRun("10 X = 256 : ON X GOTO 20, 30, 40 : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"));

		assertCompileError(compileAndRun("10 X = 1 : ON X GOTO : PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X GOTO PRINT 10; : GOTO 50" + CR + "20 PRINT 20; : RETURN" + CR + "30 PRINT 30; : RETURN" + CR + "40 PRINT 40; : RETURN" + CR + "50 PRINT 50; : END"));
		assertCompileError(compileAndRun("10 X = 1 : ON X GOTO 20, 30, : PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"));
		assertCompileError(compileAndRun("10 X = 1 : ON X GOTO 25, 30, 40: PRINT 10;" + CR + "20 PRINT 20;" + CR + "30 PRINT 30;" + CR + "40 PRINT 40;" + CR + "50 PRINT 50;"));
	}

	@Test
	public void testPRINT() {
		assertEquals(compileAndRun("10 PRINT \"HELLO WORLD!\""), "HELLO WORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";\"WORLD!\""), "HELLOWORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\" ; \"WORLD!\""), "HELLOWORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";;\"WORLD!\";"), "HELLOWORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\"" + CR + "20 PRINT \"WORLD!\""), "HELLO" + CR + "WORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";" + CR + "20 PRINT \"WORLD!\""), "HELLOWORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";\" \";" + CR + "20 PRINT \"WORLD!\""), "HELLO WORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";1;\"WORLD!\""), "HELLO 1 WORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";-1;\"WORLD!\""), "HELLO-1 WORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";1.5;\"WORLD!\""), "HELLO 1.5 WORLD!");
		assertEquals(compileAndRun("10 PRINT \"HELLO\";-1.5;\"WORLD!\""), "HELLO-1.5 WORLD!");
		assertEquals(compileAndRun("10 PRINT \"A\",\"B\""), "A             B");
		assertEquals(compileAndRun("10 PRINT \"A\",\"B\",\"C\""), "A             B             C");
		assertEquals(compileAndRun("10 PRINT \"A\",\"B\",\"C\",\"D\""), "A             B             C             D");
		assertEquals(compileAndRun("10 PRINT 1,2,3"), " 1             2             3 ");
		assertEquals(compileAndRun("10 PRINT 1,-1,-1.5,2"), " 1            -1            -1.5           2 ");

		assertEquals(compileAndRun("10 PRINT \"HELLO\" \" WORLD!\""), "HELLO WORLD!");
		assertCompileError(compileAndRun("10 PRINT \"HELLO"));
		assertEquals(compileAndRun("10 A = 2 : PRINT \"HELLO\" A A + 1"), "HELLO 2  3 ");
		assertEquals(compileAndRun("10 A = 2 : PRINT \"HELLO\" A SQR(4) A + 1"), "HELLO 2  2  3 ");
		assertEquals(compileAndRun("10 PRINT"), "");
		assertEquals(compileAndRun("10 PRINT      "), "");
		assertEquals(compileAndRun("10 PRINT      \"HELLO\""), "HELLO");

		assertEquals(compileAndRun("10 A$=\"A\" : X = 1 : PRINT A$;X"), "A 1 ");

		// test: do not confuse number variables with statement keywords
		assertCompileError(compileAndRun("10 PRINT 1;PRINT"));
	}

	@Test
	public void testREAD_DATA_RESTORE() {
		assertEquals(compileAndRun("10 READ A : DATA 1 : PRINT A"), " 1 ");
		assertEquals(compileAndRun("10 READ A, B, C : DATA 1,2,3 : PRINT A;B;C"), " 1  2  3 ");
		assertEquals(compileAndRun("10 READ A, B : DATA 1,2,3 : PRINT A;B"), " 1  2 ");
		assertEquals(compileAndRun("10 READ A, A$, B : DATA 1,\"ABC\",3 : PRINT A;A$;B"), " 1 ABC 3 ");
		assertEquals(compileAndRun("10 READ A, A$, B : DATA 1,ABC,3 : PRINT A;A$;B"), " 1 ABC 3 ");
		assertEquals(compileAndRun("10 READ A, A$, B : DATA 1,AB C,3 : PRINT A;A$;B"), " 1 AB C 3 ");
		assertEquals(compileAndRun("10 READ A, A$, B : DATA 1, AB C,3 : PRINT A;A$;B"), " 1 AB C 3 ");
		assertEquals(compileAndRun("10 DATA 1 : READ A, B, C : DATA 2,3 : PRINT A;B;C"), " 1  2  3 ");

		assertEquals(compileAndRun("10 READ A, B : RESTORE : READ C, D : DATA 1,2,3 : PRINT A;B;C;D"), " 1  2  1  2 ");
		assertEquals(compileAndRun("10 DATA 1 : READ A, B" + CR + "15 RESTORE 10 : READ C, D" + CR + "20 DATA 2,3 : PRINT A;B;C;D"), " 1  2  1  2 ");
		assertEquals(compileAndRun("10 DATA 1 : READ A, B" + CR + "15 RESTORE 20 : READ C, D" + CR + "20 DATA 2,3 : PRINT A;B;C;D"), " 1  2  2  3 ");

		assertCompileError(compileAndRun("10 READ : DATA 1,2,3"));
		assertCompileError(compileAndRun("10 READ A, : DATA 1,2,3"));
		assertCompileError(compileAndRun("10 DATA 1 : READ A, B" + CR + "15 RESTORE 100 : READ C, D" + CR + "20 DATA 2,3 : PRINT A;B;C;D"));
		assertEquals(compileAndRun("10 DATA 1 : READ A, B" + CR + "15 RESTORE 10 : READ C, D" + CR + "20 DATA 2,3 : PRINT A;B;C;D"), " 1  2  1  2 ");

		assertEquals(compileAndRun("10 READ A, B" + CR + "20 DATA 1" + CR + "30 RESTORE : READ C, D" + CR + "40 DATA 2,3 : PRINT A;B;C;D"), " 1  2  1  2 ");
		assertEquals(compileAndRun("10 READ A, B" + CR + "20 DATA 1" + CR + "30 RESTORE 20 : READ C, D" + CR + "40 DATA 2,3 : PRINT A;B;C;D"), " 1  2  1  2 ");

		assertRuntimeError(compileAndRun("10 READ A, B, C, D : DATA 1,2,3 : PRINT A;B;C"));
		assertRuntimeError(compileAndRun("10 READ A, B, C, D : DATA 1,\"ABC\",3 : PRINT A;B;C"));
	}

	@Test
	public void testREM() {
		assertEquals(compileAndRun("10 REM Comment"), "");
		assertEquals(compileAndRun("10 PRINT \"A\"; : REM XXX : PRINT \"B\""), "A");
	}

	@Test
	public void testSWAP() {
		assertEquals(compileAndRun("10 A = 1 : B = 2 : SWAP A,B : PRINT A;B"), " 2  1 ");
		assertEquals(compileAndRun("10 A = 1 : B = 2 : SWAP B,A : PRINT A;B"), " 2  1 ");
		assertEquals(compileAndRun("10 A = 1 : B = 2 : SWAP A,B : SWAP B,A : PRINT A;B"), " 1  2 ");
		assertEquals(compileAndRun("10 A$ = \"ABC\" : B$ = \"DEF\" : SWAP A$,B$ : PRINT A$;B$"), "DEFABC");
		assertEquals(compileAndRun("10 A$ = \"ABC\" : B$ = \"DEF\" : SWAP B$,A$ : PRINT A$;B$"), "DEFABC");
		assertEquals(compileAndRun("10 A$ = \"ABC\" : B$ = \"DEF\" : SWAP A$,B$ : SWAP B$,A$ : PRINT A$;B$"), "ABCDEF");

		assertCompileError(compileAndRun("10 SWAP A 1"));
		assertCompileError(compileAndRun("10 SWAP A, 1"));
		assertCompileError(compileAndRun("10 SWAP 1, A"));
		assertCompileError(compileAndRun("10 SWAP A, \"XXX\""));
		assertCompileError(compileAndRun("10 SWAP \"XXX\", A"));
		assertCompileError(compileAndRun("10 SWAP A$, \"XXX\""));
		assertCompileError(compileAndRun("10 SWAP \"XXX\", A$"));
		assertCompileError(compileAndRun("10 SWAP A$, 1"));
		assertCompileError(compileAndRun("10 SWAP 1, A$"));
	}

	@Test
	public void testWHILE_WEND() {
		assertEquals(compileAndRun("10 I = 0 : WHILE I < 5 : PRINT I; : I = I + 1 : WEND"), " 0  1  2  3  4 ");
		assertEquals(compileAndRun("10 I = 0 : WHILE I < 5 : PRINT I; : I = I + 1 : WEND : PRINT I"), " 0  1  2  3  4  5 ");

		assertEquals(compileAndRun("10 I = 0 : WHILE I < 5 : PRINT I"), " 0 ");

		assertCompileError(compileAndRun("10 GOTO 50" + CR + "20 PRINT I;" + CR + "30 WEND" + CR + "40 END" + CR + "50 WHILE I < 5" + CR + "60 GOTO 20"));
		assertCompileError(compileAndRun("10 WEND"));

		assertCompileError(compileAndRun("10 WHILE : PRINT 10"));
		assertCompileError(compileAndRun("10 WHILE \"XXX\": PRINT 10"));
	}

	@Test
	public void testParsing() {
		assertEquals(compileAndRun("10 LETA=1 : PRINT A"), " 0 ");
		assertEquals(compileAndRun("10 A=1 : PRINT NOTA"), " 0 ");
		assertEquals(compileAndRun("10 PRINT NOT1"), " 0 ");
		assertEquals(compileAndRun("10 PRINTA=0"), "");

		assertCompileError(compileAndRun("10 PRINTHELLO"));
		assertCompileError(compileAndRun("10 PRINT PRINT"));
	}

	@Test
	public void testNumberLiterals() {
		assertEquals(compileAndRun("10 PRINT 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 2"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 8192"), " 8192 ");

		assertEquals(compileAndRun("10 PRINT -0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT -1"), "-1 ");
		assertEquals(compileAndRun("10 PRINT -2"), "-2 ");
		assertEquals(compileAndRun("10 PRINT -8192"), "-8192 ");

		assertEquals(compileAndRun("10 PRINT --1"), " 1 ");

		assertEquals(compileAndRun("10 PRINT 0.5"), " .5 ");
		assertEquals(compileAndRun("10 PRINT .5"), " .5 ");
		assertEquals(compileAndRun("10 PRINT 1.33"), " 1.33 ");
		assertEquals(compileAndRun("10 PRINT 100.0"), " 100 ");
		assertEquals(compileAndRun("10 PRINT 1.5E+02"), " 150 ");
		assertEquals(compileAndRun("10 PRINT 1.5E-02"), " .015 ");
		assertEquals(compileAndRun("10 PRINT -0.5"), "-.5 ");

		assertEquals(compileAndRun("10 PRINT -.5"), "-.5 ");
		assertEquals(compileAndRun("10 PRINT -1.33"), "-1.33 ");
		assertEquals(compileAndRun("10 PRINT -100.0"), "-100 ");
		assertEquals(compileAndRun("10 PRINT -1.5E+02"), "-150 ");
		assertEquals(compileAndRun("10 PRINT -1.5E-02"), "-.015 ");
	}

	@Test
	public void testAdditionSubtraction() {
		assertEquals(compileAndRun("10 PRINT 1+1"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 1+2+3"), " 6 ");
		assertEquals(compileAndRun("10 PRINT 1-2+3"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 1-2-3"), "-4 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1+ : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1+"));
		assertCompileError(compileAndRun("10 PRINT 1- : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1-"));
	}

	@Test
	public void testMultiplication() {
		assertEquals(compileAndRun("10 PRINT 2*3"), " 6 ");
		assertEquals(compileAndRun("10 PRINT 2*3*4"), " 24 ");
		assertEquals(compileAndRun("10 PRINT 2*3*-4"), "-24 ");
		assertEquals(compileAndRun("10 PRINT -2*3*-4"), " 24 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1* : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1*"));
	}

	@Test
	public void testDivision() {
		assertEquals(compileAndRun("10 PRINT 6/2"), " 3 ");
		assertEquals(compileAndRun("10 PRINT 6/-2"), "-3 ");
		assertEquals(compileAndRun("10 PRINT -6/2"), "-3 ");
		assertEquals(compileAndRun("10 PRINT -6/-2"), " 3 ");

		assertEquals(compileAndRun("10 PRINT 3/2"), " 1.5 ");
		assertEquals(compileAndRun("10 PRINT 3/-2"), "-1.5 ");
		assertEquals(compileAndRun("10 PRINT -3/2"), "-1.5 ");
		assertEquals(compileAndRun("10 PRINT -3/-2"), " 1.5 ");

		assertEquals(compileAndRun("10 PRINT 1/0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT 1/-0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT -1/0"), "Division by zero" + CR + "-Infinity ");
		assertEquals(compileAndRun("10 PRINT -1/-0"), "Division by zero" + CR + "-Infinity ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1/ : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1/"));
	}

	@Test
	public void testPower() {
		assertEquals(compileAndRun("10 PRINT 2^3"), " 8 ");
		assertEquals(compileAndRun("10 PRINT 2^3^2"), " 64 ");
		assertEquals(compileAndRun("10 PRINT 2^2^-1"), " .25 ");
		assertEquals(compileAndRun("10 PRINT 0^-1"), "Division by zero" + CR + " Infinity ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1^ : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1^"));
	}

	@Test
	public void testOperatorPrecedence() {
		assertEquals(compileAndRun("10 PRINT 1+2*3"), " 7 ");
		assertEquals(compileAndRun("10 PRINT 1*2+3"), " 5 ");
		assertEquals(compileAndRun("10 PRINT 1+2*3+4"), " 11 ");
		assertEquals(compileAndRun("10 PRINT 1*2+3*4"), " 14 ");
	}

	@Test
	public void testUnaryMinus() {
		assertEquals(compileAndRun("10 PRINT 1+-2*3"), "-5 ");
		assertEquals(compileAndRun("10 PRINT -1*-2+-3"), "-1 ");
		assertEquals(compileAndRun("10 PRINT -1+-2*-3+-4"), " 1 ");
	}

	@Test
	public void testParentheses() {
		assertEquals(compileAndRun("10 PRINT 1*(2+3)*4"), " 20 ");
		assertEquals(compileAndRun("10 PRINT (1+2)*(3+4)"), " 21 ");
		assertEquals(compileAndRun("10 PRINT 1*(-2-3)*-4"), " 20 ");
		assertEquals(compileAndRun("10 PRINT -(1+2)*(3+4)"), "-21 ");
		assertEquals(compileAndRun("10 PRINT -(1+2)*-(3+4)"), " 21 ");
	}

	@Test
	public void testNumComparison() {
		assertEquals(compileAndRun("10 PRINT 0 < 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 <= 0"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 0 = 0"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 0 >= 0"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 0 > 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 <> 0"), " 0 ");

		assertEquals(compileAndRun("10 PRINT 0 < 1"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 0 <= 1"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 0 = 1"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 >= 1"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 > 1"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 <> 1"), "-1 ");

		assertEquals(compileAndRun("10 PRINT 1 < 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1 <= 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1 = 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1 >= 0"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 1 > 0"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 1 <> 0"), "-1 ");

		assertEquals(compileAndRun("10 PRINT 1 < 1"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1 <= 1"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 1 = 1"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 1 >= 1"), "-1 ");
		assertEquals(compileAndRun("10 PRINT 1 > 1"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1 <> 1"), " 0 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1 <>"));
		assertCompileError(compileAndRun("10 PRINT 1 <> : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 ="));
		assertCompileError(compileAndRun("10 PRINT 1 = : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 <"));
		assertCompileError(compileAndRun("10 PRINT 1 < : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 <="));
		assertCompileError(compileAndRun("10 PRINT 1 <= : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 >="));
		assertCompileError(compileAndRun("10 PRINT 1 >= : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 >"));
		assertCompileError(compileAndRun("10 PRINT 1 > : PRINT"));
	}

	@Test
	public void testStrConcatenation() {
		assertEquals(compileAndRun("10 PRINT \"A\" + \"B\""), "AB");
		assertEquals(compileAndRun("10 PRINT \"A\" + \"\""), "A");
		assertEquals(compileAndRun("10 PRINT \"A\" + \"B\" + \"C\""), "ABC");

		assertCompileError(compileAndRun("10 PRINT \"A\" + "));
		assertCompileError(compileAndRun("10 PRINT \"A\" - \"B\""));
		assertCompileError(compileAndRun("10 PRINT \"A\" + 1"));
		assertCompileError(compileAndRun("10 PRINT 1 + \"A\""));
		assertCompileError(compileAndRun("10 PRINT 1 - \"A\""));

		assertCompileError(compileAndRun("10 X = 1 : PRINT \"A\" + X"));
		assertCompileError(compileAndRun("10 X = 1 : PRINT X + \"A\""));
		assertCompileError(compileAndRun("10 A$=\"A\" : X = 1 : PRINT A$ + X"));
		assertCompileError(compileAndRun("10 A$=\"A\" : X = 1 : PRINT X + A$"));

		assertEquals(compileAndRun("10 A$=\"0123456789ABCDEF\" : A$ = A$ + A$ : A$ = A$ + A$ : A$ = A$ + A$ : PRINT A$"), "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
		assertRuntimeError(compileAndRun("10 A$=\"0123456789ABCDEF\" : A$ = A$ + A$ : A$ = A$ + A$ : A$ = A$ + A$ : A$ = A$ + A$"));
	}

	@Test
	public void testStrComparison() {
		assertEquals(compileAndRun("10 PRINT \"\" < \"HELLO\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"\" <= \"HELLO\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"\" = \"HELLO\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"\" >= \"HELLO\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"\" > \"HELLO\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"\" <> \"HELLO\""), "-1 ");

		assertEquals(compileAndRun("10 PRINT \"HELLO\" < \"\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"HELLO\" <= \"\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"HELLO\" = \"\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"HELLO\" >= \"\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"HELLO\" > \"\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"HELLO\" <> \"\""), "-1 ");

		assertEquals(compileAndRun("10 PRINT \"\" < \"\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"\" <= \"\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"\" = \"\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"\" >= \"\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"\" > \"\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"\" <> \"\""), " 0 ");

		assertEquals(compileAndRun("10 PRINT \"ABC\" < \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" <= \"ABC\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" = \"ABC\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" >= \"ABC\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" > \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" <> \"ABC\""), " 0 ");

		assertEquals(compileAndRun("10 PRINT \"ABC\" < \"BCD\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" <= \"BCD\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" = \"BCD\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" >= \"BCD\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" > \"BCD\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" <> \"BCD\""), "-1 ");

		assertEquals(compileAndRun("10 PRINT \"BCD\" < \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"BCD\" <= \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"BCD\" = \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"BCD\" >= \"ABC\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"BCD\" > \"ABC\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"BCD\" <> \"ABC\""), "-1 ");

		assertEquals(compileAndRun("10 PRINT \"AB\" < \"ABC\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"BC\" < \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"AB\" <= \"ABC\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"AB\" = \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"AB\" >= \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"AB\" > \"ABC\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"AB\" <> \"ABC\""), "-1 ");

		assertEquals(compileAndRun("10 PRINT \"ABC\" < \"AB\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" <= \"AB\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" = \"AB\""), " 0 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" >= \"AB\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" > \"AB\""), "-1 ");
		assertEquals(compileAndRun("10 PRINT \"ABC\" <> \"AB\""), "-1 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT \"ABC\" < : PRINT"));
		assertCompileError(compileAndRun("10 PRINT \"ABC\" <= : PRINT"));
		assertCompileError(compileAndRun("10 PRINT \"ABC\" = : PRINT"));
		assertCompileError(compileAndRun("10 PRINT \"ABC\" <> : PRINT"));
		assertCompileError(compileAndRun("10 PRINT \"ABC\" >= : PRINT"));
		assertCompileError(compileAndRun("10 PRINT \"ABC\" > : PRINT"));

		assertEquals(compileAndRun("10 PRINT LEFT$(\"DEF\",3)=MID$(\"ABCDEFHI\",4,3)"), "-1 ");
	}

	@Test
	public void testBooleanOperations() {
		assertEquals(compileAndRun("10 PRINT 0 AND 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 AND 1"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1 AND 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 1 AND 1"), " 1 ");

		assertEquals(compileAndRun("10 PRINT 1 AND 1 AND 1"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 1 AND 0 AND 1"), " 0 ");

		assertEquals(compileAndRun("10 PRINT 63 AND 16"), " 16 ");
		assertEquals(compileAndRun("10 PRINT 15 AND 14"), " 14 ");
		assertEquals(compileAndRun("10 PRINT -1 AND 8"), " 8 ");
		assertEquals(compileAndRun("10 PRINT -16384 AND 16384"), " 16384 ");

		assertEquals(compileAndRun("10 PRINT 32767 AND 1"), " 1 ");
		assertRuntimeError(compileAndRun("10 PRINT 32768 AND 2"));
		assertEquals(compileAndRun("10 PRINT -32768 AND 1"), " 0 ");
		assertRuntimeError(compileAndRun("10 PRINT -32769 AND 1"));

		assertEquals(compileAndRun("10 PRINT 0 OR 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 OR 1"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 1 OR 0"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 1 OR 1"), " 1 ");

		assertEquals(compileAndRun("10 PRINT 1 OR 1 OR 1"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 1 OR 0 OR 1"), " 1 ");

		assertEquals(compileAndRun("10 PRINT 4 OR 2"), " 6 ");
		assertEquals(compileAndRun("10 PRINT 10 OR 10"), " 10 ");

		assertEquals(compileAndRun("10 PRINT 0 XOR 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 0 XOR 1"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 1 XOR 0"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 1 XOR 1"), " 0 ");

		assertEquals(compileAndRun("10 PRINT NOT 0"), "-1 ");
		assertEquals(compileAndRun("10 PRINT NOT 1"), "-2 ");

		assertEquals(compileAndRun("10 PRINT NOT NOT 0"), " 0 ");
		assertEquals(compileAndRun("10 PRINT NOT NOT 1"), " 1 ");

		assertEquals(compileAndRun("10 PRINT NOT 0 AND 1"), " 1 ");
		assertEquals(compileAndRun("10 PRINT NOT 1 OR 1"), "-1 ");
		assertEquals(compileAndRun("10 PRINT NOT (0 AND 1)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT NOT (1 OR 1)"), "-2 ");
		assertEquals(compileAndRun("10 PRINT NOT INT(1)"), "-2 ");
		assertEquals(compileAndRun("10 PRINT NOT INT(0)"), "-1 ");

		assertEquals(compileAndRun("10 PRINT NOT 1 + 1"), "-3 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1 AND"));
		assertCompileError(compileAndRun("10 PRINT 1 AND : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 OR"));
		assertCompileError(compileAndRun("10 PRINT 1 OR : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 XOR"));
		assertCompileError(compileAndRun("10 PRINT 1 XOR : PRINT"));
	}

	@Test
	public void testIntegerDivision() {
		assertEquals(compileAndRun("10 PRINT 9 \\ 4"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 9 \\ 2"), " 4 ");
		assertEquals(compileAndRun("10 PRINT 11.2 \\ 4"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 11.5 \\ 4"), " 3 ");
		assertEquals(compileAndRun("10 PRINT 11.7 \\ 4"), " 3 ");
		assertEquals(compileAndRun("10 PRINT 8 \\ 4.2"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 8 \\ 4.5"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 8 \\ 4.7"), " 1 ");

		assertEquals(compileAndRun("10 PRINT -9 \\ 4"), "-2 ");
		assertEquals(compileAndRun("10 PRINT 9 \\ -4"), "-2 ");
		assertEquals(compileAndRun("10 PRINT -9 \\ -4"), " 2 ");

		assertEquals(compileAndRun("10 PRINT 1 \\ 0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT 1 \\ -0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT -1 \\ 0"), "Division by zero" + CR + "-Infinity ");
		assertEquals(compileAndRun("10 PRINT -1 \\ -0"), "Division by zero" + CR + "-Infinity ");

		assertRuntimeError(compileAndRun("10 PRINT 32768 \\ 10"));
		assertRuntimeError(compileAndRun("10 PRINT 10 \\ 32768"));

		assertEquals(compileAndRun("10 PRINT 10 \\ 4"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 25.68 \\ 6.99"), " 3 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1\\ : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1\\"));
	}

	@Test
	public void testTrigonometricFunctions() {
		assertEquals(compileAndRun("10 PRINT SIN(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT COS(0)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT ATN(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT TAN(0)"), " 0 ");
	}

	@Test
	public void testFunctions() {
		assertCompileError(compileAndRun("10 PRINT LEFT$(\"ABC\" 2)"));
		assertCompileError(compileAndRun("10 PRINT LEFT$(\"ABC\", 2 : PRINT"));
		assertCompileError(compileAndRun("10 PRINT LEFT$(\"ABC\", \"X\") : PRINT"));
	}

	@Test
	public void testAssignment() {
		assertEquals(compileAndRun("10 A = 2 * 1 : A = A + 1 : PRINT A"), " 3 ");

		assertEquals(compileAndRun("10 A = \"ABC\" < \"DEF\" : PRINT A"), "-1 ");
		assertEquals(compileAndRun("10 A = \"ABC\" < \"DEF\" = -1 : PRINT A"), "-1 ");
		assertEquals(compileAndRun("10 A = \"ABC\" > \"DEF\" = -1 : PRINT A"), " 0 ");
		assertCompileError(compileAndRun("10 A = \"ABC\" < \"DEF\" < \"GHI\": PRINT A"));

		assertCompileError(compileAndRun("10 A = \"ABC\" + \"DEF\" : PRINT A"));
		assertEquals(compileAndRun("10 A = \"ABC\" < \"DEF\" + \"GHI\": PRINT A"), "-1 ");

		assertEquals(compileAndRun("10 A = \"ABC\" = \"ABC\" + 1 : PRINT A"), " 0 ");
		assertEquals(compileAndRun("10 A = 1 + \"ABC\" = \"ABC\": PRINT A"), " 0 ");
		assertEquals(compileAndRun("10 A = \"ABC\" > \"ABC\" + 1 : PRINT A"), " 1 ");
		assertEquals(compileAndRun("10 A = 1 + \"ABC\" > \"ABC\" : PRINT A"), " 1 ");
		assertEquals(compileAndRun("10 A = 1 + \"AB\" + \"CD\" = \"AB\" + \"C\" + \"D\" : PRINT A"), " 0 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT (1"));
		assertCompileError(compileAndRun("10 PRINT (1 : PRINT"));

		// code coverage
		assertCompileError(compileAndRun("10 A ="));
		assertCompileError(compileAndRun("10 A = : PRINT A"));
	}

	@Test
	public void testNumArrays() {
		assertEquals(compileAndRun("10 DIM A(1) : A(0) = 1 : PRINT A(0)"), " 1 ");
		assertEquals(compileAndRun("10 DIM A(1) : A(0) = 2 : PRINT A(0)"), " 2 ");
		assertEquals(compileAndRun("10 DIM A(10) : A(2) = 2 : A(4) = 4 : PRINT A(4);A(2)"), " 4  2 ");

		assertEquals(compileAndRun("10 DIM A(4) : A(2.2) = 2 : A(2.7) = 4 : PRINT A(2);A(3)"), " 2  4 ");
		assertEquals(compileAndRun("10 DIM A(4) : A(2) = 2 : A(3) = 4 : PRINT A(2.2);A(2.7)"), " 2  4 ");
		assertEquals(compileAndRun("10 DIM A(4,4) : A(2.2,0) = 2 : A(2.7,0) = 4 : PRINT A(2,0);A(3,0)"), " 2  4 ");
		assertEquals(compileAndRun("10 DIM A(4,4) : A(2,0) = 2 : A(3,0) = 4 : PRINT A(2.2,0);A(2.7,0)"), " 2  4 ");
		assertEquals(compileAndRun("10 DIM A(4,4) : A(0,2.2) = 2 : A(0,2.7) = 4 : PRINT A(0,2);A(0,3)"), " 2  4 ");
		assertEquals(compileAndRun("10 DIM A(4,4) : A(0,2) = 2 : A(0,3) = 4 : PRINT A(0,2.2);A(0,2.7)"), " 2  4 ");

		assertEquals(compileAndRun("10 DIM A(0.2) : A(0) = 1 : PRINT A(0)"), " 1 ");
		assertRuntimeError(compileAndRun("10 DIM A(0.2) : A(0) = 1 : A(1) = 1"));
		assertEquals(compileAndRun("10 DIM A(0.5) : A(0) = 0 : A(1) = 1 : PRINT A(0);A(1)"), " 0  1 ");
		assertEquals(compileAndRun("10 DIM A(0.7) : A(0) = 0 : A(1) = 1 : PRINT A(0);A(1)"), " 0  1 ");

		assertRuntimeError(compileAndRun("10 DIM A(1) : A(-1) = 1"));
		assertRuntimeError(compileAndRun("10 DIM A(1) : A(2) = 1"));
		assertEquals(compileAndRun("10 DIM A(1,1) : A(0,0) = 1 : PRINT A(0,0)"), " 1 ");

		assertEquals(compileAndRun("10 DIM A(1,1) : A(0,0) = 2 : PRINT A(0,0)"), " 2 ");
		assertEquals(compileAndRun("10 DIM A(2,2) : A(0,0) = 1: A(0,1) = 2 : A(1,0) = 3 : A(1,1) = 4 : PRINT A(0,0);A(0,1);A(1,0);A(1,1);"), " 1  2  3  4 ");
		assertEquals(compileAndRun("10 DIM A(2,3) : A(0,0) = 1: A(0,1) = 2 : A(0,2) = 3 : A(1,0) = 4 : A(1,1) = 5 : A(1,2) = 6 : PRINT A(0,0);A(0,1);A(0,2);A(1,0);A(1,1);A(1,2);"), " 1  2  3  4  5  6 ");

		assertEquals(compileAndRun("10 DIM A(0.2, 1) : A(0, 0) = 1 : PRINT A(0, 0)"), " 1 ");
		assertRuntimeError(compileAndRun("10 DIM A(0.2, 1) : A(0, 0) = 1 : A(1, 0) = 1"));
		assertEquals(compileAndRun("10 DIM A(0.5, 1) : A(0, 0) = 0 : A(1, 0) = 1 : PRINT A(0, 0);A(1, 0)"), " 0  1 ");
		assertEquals(compileAndRun("10 DIM A(0.7, 1) : A(0, 0) = 0 : A(1, 0) = 1 : PRINT A(0, 0);A(1, 0)"), " 0  1 ");

		assertEquals(compileAndRun("10 DIM A(1, 0.2) : A(0, 0) = 1 : PRINT A(0, 0)"), " 1 ");
		assertRuntimeError(compileAndRun("10 DIM A(1, 0.2) : A(0, 0) = 1 : A(0, 1) = 1"));
		assertEquals(compileAndRun("10 DIM A(1, 0.5) : A(0, 0) = 0 : A(0, 1) = 1 : PRINT A(0, 0);A(0, 1)"), " 0  1 ");
		assertEquals(compileAndRun("10 DIM A(1, 0.7) : A(0, 0) = 0 : A(0, 1) = 1 : PRINT A(0, 0);A(0, 1)"), " 0  1 ");

		assertRuntimeError(compileAndRun("10 DIM A(1,1) : A(-1,0) = 1"));
		assertRuntimeError(compileAndRun("10 DIM A(1,1) : A(2,0) = 1"));
		assertRuntimeError(compileAndRun("10 DIM A(1,1) : A(0,-1) = 1"));
		assertRuntimeError(compileAndRun("10 DIM A(1,1) : A(0,2) = 1"));
		assertRuntimeError(compileAndRun("10 DIM A(1,1) : A(-1,-1) = 1"));
		assertRuntimeError(compileAndRun("10 DIM A(1,1) : A(2,2) = 1"));

		// test: do not confuse number arrays with function keywords
		assertCompileError(compileAndRun("10 DIM SIN(1)"));
		// test: do not confuse number arrays with function keywords
		assertCompileError(compileAndRun("10 SIN(1) = 1"));
	}

	@Test
	public void testNumArraysWithoutDIM() {
		assertEquals(compileAndRun("10 DIM A(10)"), "");
		assertEquals(compileAndRun("10 DIM A(10) : A(0) = 1"), "");
		assertEquals(compileAndRun("10 DIM A(10) : PRINT A(0)"), " 0 ");

		assertEquals(compileAndRun("10 PRINT A(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(1)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(10)"), " 0 ");
		assertRuntimeError(compileAndRun("10 PRINT A(11)"));

		assertEquals(compileAndRun("10 A(0) = 1: PRINT A(0)"), " 1 ");
		assertEquals(compileAndRun("10 A(1) = 1 : PRINT A(1)"), " 1 ");
		assertEquals(compileAndRun("10 A(10) = 1 : PRINT A(10)"), " 1 ");
		assertRuntimeError(compileAndRun("10 A(11) = 1 : PRINT A(11)"));

		assertRuntimeError(compileAndRun("10 A(0) = 1 : DIM A(10)"));
		assertCompileError(compileAndRun("10 A(0) = 0 : DIM A(10,10)"));
		assertCompileError(compileAndRun("10 A(0,0) = 1 : DIM A(10)"));
		assertRuntimeError(compileAndRun("10 A(0,0) = 1 : DIM A(10,10)"));
		assertCompileError(compileAndRun("10 DIM A(10) : DIM A(10,10)"));
		assertCompileError(compileAndRun("10 DIM A(10,10) : DIM A(10)"));

		assertEquals(compileAndRun("10 DIM A(10,10)"), "");
		assertEquals(compileAndRun("10 DIM A(10,10) : A(0,0) = 1"), "");
		assertEquals(compileAndRun("10 DIM A(10,10) : PRINT A(0,0)"), " 0 ");
		assertEquals(compileAndRun("10 A(0,0) = 1: PRINT A(0,0)"), " 1 ");

		assertEquals(compileAndRun("10 PRINT A(0,0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(1,0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(10,0)"), " 0 ");
		assertRuntimeError(compileAndRun("10 PRINT A(11,0)"));

		assertEquals(compileAndRun("10 PRINT A(0,1)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(0,10)"), " 0 ");
		assertRuntimeError(compileAndRun("10 PRINT A(0,11)"));

		assertEquals(compileAndRun("10 PRINT A(1,1)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(1,10)"), " 0 ");
		assertRuntimeError(compileAndRun("10 PRINT A(1,11)"));

		assertEquals(compileAndRun("10 PRINT A(10,1)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(10,10)"), " 0 ");
		assertRuntimeError(compileAndRun("10 PRINT A(10,11)"));

		assertRuntimeError(compileAndRun("10 PRINT A(11,0)"));
		assertRuntimeError(compileAndRun("10 PRINT A(11,1)"));
		assertRuntimeError(compileAndRun("10 PRINT A(11,10)"));
		assertRuntimeError(compileAndRun("10 PRINT A(11,11)"));

		assertEquals(compileAndRun("10 A(0,0) = 1 : PRINT A(0,0)"), " 1 ");
		assertEquals(compileAndRun("10 A(1,0) = 1 : PRINT A(1,0)"), " 1 ");
		assertEquals(compileAndRun("10 A(10,0) = 1 : PRINT A(10,0)"), " 1 ");
		assertRuntimeError(compileAndRun("10 A(11,0) = 1 : PRINT A(11,0)"));

		assertEquals(compileAndRun("10 A(0,1) = 1 : PRINT A(0,1)"), " 1 ");
		assertEquals(compileAndRun("10 A(0,10) = 1 : PRINT A(0,10)"), " 1 ");
		assertRuntimeError(compileAndRun("10 A(0,11) = 1 : PRINT A(0,11)"));

		assertEquals(compileAndRun("10 A(1,1) = 1 : PRINT A(1,1)"), " 1 ");
		assertEquals(compileAndRun("10 A(10,10) = 1 : PRINT A(10,10)"), " 1 ");
		assertRuntimeError(compileAndRun("10 A(11,11) = 1 : PRINT A(11,11)"));
	}

	@Test
	public void testStrArrays() {
		assertEquals(compileAndRun("10 DIM A$(1) : PRINT A$(0)"), "");
		assertEquals(compileAndRun("10 DIM A$(1) : A$(0) = \"1\" : PRINT A$(0)"), "1");
		assertEquals(compileAndRun("10 DIM A$(1) : A$(0) = \"2\" : PRINT A$(0)"), "2");
		assertEquals(compileAndRun("10 DIM A$(10) : A$(2) = \"2\" : A$(4) = \"4\" : PRINT A$(4);A$(2)"), "42");

		assertEquals(compileAndRun("10 DIM A$(4) : A$(2.2) = \"A\" : A$(2.7) = \"B\" : PRINT A$(2);A$(3)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(4) : A$(2) = \"A\" : A$(3) = \"B\" : PRINT A$(2.2);A$(2.7)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(4,4) : A$(2.2,0) = \"A\" : A$(2.7,0) = \"B\" : PRINT A$(2,0);A$(3,0)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(4,4) : A$(2,0) = \"A\" : A$(3,0) = \"B\" : PRINT A$(2.2,0);A$(2.7,0)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(4,4) : A$(0,2.2) = \"A\" : A$(0,2.7) = \"B\" : PRINT A$(0,2);A$(0,3)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(4,4) : A$(0,2) = \"A\" : A$(0,3) = \"B\" : PRINT A$(0,2.2);A$(0,2.7)"), "AB");

		assertEquals(compileAndRun("10 DIM A$(0.2) : A$(0) = \"A\" : PRINT A$(0)"), "A");
		assertRuntimeError(compileAndRun("10 DIM A$(0.2) : A$(0) = \"A\" : A$(1) = \"B\""));
		assertEquals(compileAndRun("10 DIM A$(0.5) : A$(0) = \"A\" : A$(1) = \"B\" : PRINT A$(0);A$(1)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(0.7) : A$(0) = \"A\" : A$(1) = \"B\" : PRINT A$(0);A$(1)"), "AB");

		assertRuntimeError(compileAndRun("10 DIM A$(1) : A$(-1) = \"1\""));
		assertRuntimeError(compileAndRun("10 DIM A$(1) : A$(2) = \"1\""));

		assertEquals(compileAndRun("10 DIM A$(1,1) : A$(0,0) = \"1\" : PRINT A$(0,0)"), "1");
		assertEquals(compileAndRun("10 DIM A$(2,2) : A$(0,0) = \"1\": A$(0,1) = \"2\" : A$(1,0) = \"3\" : A$(1,1) = \"4\" : PRINT A$(0,0);A$(0,1);A$(1,0);A$(1,1);"), "1234");

		assertEquals(compileAndRun("10 DIM A$(0.2, 1) : A$(0, 0) = \"A\" : PRINT A$(0, 0)"), "A");
		assertRuntimeError(compileAndRun("10 DIM A$(0.2, 1) : A$(0, 0) = \"A\" : A$(1, 0) = \"B\""));
		assertEquals(compileAndRun("10 DIM A$(0.5, 1) : A$(0, 0) = \"A\" : A$(1, 0) = \"B\" : PRINT A$(0, 0);A$(1, 0)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(0.7, 1) : A$(0, 0) = \"A\" : A$(1, 0) = \"B\" : PRINT A$(0, 0);A$(1, 0)"), "AB");

		assertEquals(compileAndRun("10 DIM A$(1, 0.2) : A$(0, 0) = \"A\" : PRINT A$(0, 0)"), "A");
		assertRuntimeError(compileAndRun("10 DIM A$(1, 0.2) : A$(0, 0) = \"A\" : A$(0, 1) = \"B\""));
		assertEquals(compileAndRun("10 DIM A$(1, 0.5) : A$(0, 0) = \"A\" : A$(0, 1) = \"B\" : PRINT A$(0, 0);A$(0, 1)"), "AB");
		assertEquals(compileAndRun("10 DIM A$(1, 0.7) : A$(0, 0) = \"A\" : A$(0, 1) = \"B\" : PRINT A$(0, 0);A$(0, 1)"), "AB");

		assertEquals(compileAndRun("10 DIM A$(2) : A$(0) = \"0\" : A$(1) = \"1\" : A$(0) = A$(1) : A$(1) = \"2\" : PRINT A$(0);A$(1)"), "12");

		assertRuntimeError(compileAndRun("10 DIM A$(1,1) : A$(-1,0) = \"1\""));
		assertRuntimeError(compileAndRun("10 DIM A$(1,1) : A$(2,0) = \"1\""));
		assertRuntimeError(compileAndRun("10 DIM A$(1,1) : A$(0,-1) = \"1\""));
		assertRuntimeError(compileAndRun("10 DIM A$(1,1) : A$(0,2) = \"1\""));
		assertRuntimeError(compileAndRun("10 DIM A$(1,1) : A$(-1,-1) = \"1\""));
		assertRuntimeError(compileAndRun("10 DIM A$(1,1) : A$(2,2) = \"1\""));

		// test: do not confuse number arrays with function keywords
		assertCompileError(compileAndRun("10 DIM LEFT$(1)"));
		// test: do not confuse number arrays with function keywords
		assertCompileError(compileAndRun("10 LEFT$(1) = \"XXX\""));
	}

	@Test
	public void testStrArraysWithoutDIM() {
		assertEquals(compileAndRun("10 DIM A$(10)"), "");
		assertEquals(compileAndRun("10 DIM A$(10) : A$(0) = \"HELLO\""), "");
		assertEquals(compileAndRun("10 DIM A$(10) : PRINT A$(0)"), "");

		assertEquals(compileAndRun("10 PRINT A$(0)"), "");
		assertEquals(compileAndRun("10 PRINT A$(1)"), "");
		assertEquals(compileAndRun("10 PRINT A$(10)"), "");
		assertRuntimeError(compileAndRun("10 PRINT A$(11)"));

		assertEquals(compileAndRun("10 A$(0) = \"HELLO\" : PRINT A$(0)"), "HELLO");
		assertEquals(compileAndRun("10 A$(1) = \"HELLO\" : PRINT A$(1)"), "HELLO");
		assertEquals(compileAndRun("10 A$(10) = \"HELLO\" : PRINT A$(10)"), "HELLO");
		assertRuntimeError(compileAndRun("10 A$(11) = \"HELLO\" : PRINT A$(11)"));

		assertRuntimeError(compileAndRun("10 A$(0) = \"HELLO\" : DIM A$(10)"));
		assertCompileError(compileAndRun("10 A$(0) = \"HELLO\" : DIM A$(10,10)"));
		assertCompileError(compileAndRun("10 A$(0,0) = \"HELLO\" : DIM A$(10)"));
		assertRuntimeError(compileAndRun("10 A$(0,0) = \"HELLO\" : DIM A$(10,10)"));
		assertCompileError(compileAndRun("10 DIM A$(10) : DIM A$(10,10)"));
		assertCompileError(compileAndRun("10 DIM A$(10,10) : DIM A$(10)"));

		assertEquals(compileAndRun("10 DIM A$(10,10)"), "");
		assertEquals(compileAndRun("10 DIM A$(10,10) : A$(0,0) = \"HELLO\""), "");
		assertEquals(compileAndRun("10 DIM A$(10,10) : PRINT A$(0,0)"), "");
		assertEquals(compileAndRun("10 A$(0,0) = \"HELLO\": PRINT A$(0,0)"), "HELLO");

		assertEquals(compileAndRun("10 PRINT A$(0,0)"), "");
		assertEquals(compileAndRun("10 PRINT A$(1,0)"), "");
		assertEquals(compileAndRun("10 PRINT A$(10,0)"), "");
		assertRuntimeError(compileAndRun("10 PRINT A$(11,0)"));

		assertEquals(compileAndRun("10 PRINT A$(0,1)"), "");
		assertEquals(compileAndRun("10 PRINT A$(0,10)"), "");
		assertRuntimeError(compileAndRun("10 PRINT A$(0,11)"));

		assertEquals(compileAndRun("10 PRINT A$(1,1)"), "");
		assertEquals(compileAndRun("10 PRINT A$(1,10)"), "");
		assertRuntimeError(compileAndRun("10 PRINT A$(1,11)"));

		assertEquals(compileAndRun("10 PRINT A$(10,1)"), "");
		assertEquals(compileAndRun("10 PRINT A$(10,10)"), "");
		assertRuntimeError(compileAndRun("10 PRINT A$(10,11)"));

		assertRuntimeError(compileAndRun("10 PRINT A$(11,0)"));
		assertRuntimeError(compileAndRun("10 PRINT A$(11,1)"));
		assertRuntimeError(compileAndRun("10 PRINT A$(11,10)"));
		assertRuntimeError(compileAndRun("10 PRINT A$(11,11)"));

		assertEquals(compileAndRun("10 A$(0,0) = \"HELLO\" : PRINT A$(0,0)"), "HELLO");
		assertEquals(compileAndRun("10 A$(1,0) = \"HELLO\" : PRINT A$(1,0)"), "HELLO");
		assertEquals(compileAndRun("10 A$(10,0) = \"HELLO\" : PRINT A$(10,0)"), "HELLO");
		assertRuntimeError(compileAndRun("10 A$(11,0) = \"HELLO\" : PRINT A$(11,0)"));

		assertEquals(compileAndRun("10 A$(0,1) = \"HELLO\" : PRINT A$(0,1)"), "HELLO");
		assertEquals(compileAndRun("10 A$(0,10) = \"HELLO\" : PRINT A$(0,10)"), "HELLO");
		assertRuntimeError(compileAndRun("10 A$(0,11) = \"HELLO\" : PRINT A$(0,11)"));

		assertEquals(compileAndRun("10 A$(1,1) = \"HELLO\" : PRINT A$(1,1)"), "HELLO");
		assertEquals(compileAndRun("10 A$(10,10) = \"HELLO\" : PRINT A$(10,10)"), "HELLO");
		assertRuntimeError(compileAndRun("10 A$(11,11) = \"HELLO\" : PRINT A$(11,11)"));
	}

	@Test
	public void testInitialVariables() {
		assertEquals(compileAndRun("10 PRINT A"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A(0,0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT A$"), "");
		assertEquals(compileAndRun("10 PRINT A$(0)"), "");
		assertEquals(compileAndRun("10 PRINT A$(0,0)"), "");
	}

	@Test
	public void testLineTooLong() {
		assertCompileError(compileAndRun("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"));
	}

	@Test
	public void testLineHasNoLineNumber() {
		assertCompileError(compileAndRun("PRINT"));
	}

	@Test
	public void testSortLines() {
		assertEquals(compileAndRun("20 PRINT \"WORLD\"" + CR + "10 PRINT \"HELLO\""), "HELLO" + CR + "WORLD");
	}

	@Test
	public void testDuplicateLines() {
		assertEquals(compileAndRun("10 PRINT \"HELLO\"" + CR + "20 PRINT \"SAILOR\"" + CR + "20 PRINT \"WORLD\""), "HELLO" + CR + "WORLD");
	}

	@Test
	public void testLineNumbersWithLeadingZeroes() {
		assertEquals(compileAndRun("10 GOTO 30" + CR + "20 PRINT \"HELLO\"" + CR + "030 PRINT \"WORLD!\"" + CR), "WORLD!");
		assertEquals(compileAndRun("10 GOTO 030" + CR + "20 PRINT \"HELLO\"" + CR + "030 PRINT \"WORLD!\"" + CR), "WORLD!");
		assertEquals(compileAndRun("10 GOTO 030" + CR + "20 PRINT \"HELLO\"" + CR + "30 PRINT \"WORLD!\"" + CR), "WORLD!");
	}

	@Test
	public void testLineNumberSorting() {
		assertEquals(compileAndRun("20 PRINT \"WORLD\"" + CR + "10 PRINT \"HELLO\""), "HELLO" + CR + "WORLD");
		assertEquals(compileAndRun("30 PRINT \"HELLO\"" + CR + "100 PRINT \"WORLD!\"" + CR), "HELLO" + CR + "WORLD!");
	}

	@Test
	public void testProgramTooLong() {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < 5980; i++) {
			sb.append("" + i + " PRINT " + i + CR);
		}
		assertCompileError(compileAndRun(sb.toString()));
	}

	@Test
	public void testABS() {
		assertEquals(compileAndRun("10 PRINT ABS(1)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT ABS(-1)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT ABS(1.2)"), " 1.2 ");
		assertEquals(compileAndRun("10 PRINT ABS(-1.2)"), " 1.2 ");
		assertEquals(compileAndRun("10 PRINT ABS(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT ABS(-0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT ABS(-1E10)"), " 1E+10 ");
		assertEquals(compileAndRun("10 PRINT ABS(1E10)"), " 1E+10 ");
	}

	@Test
	public void testASC() {
		assertEquals(compileAndRun("10 PRINT ASC(\"A\")"), " 65 ");
		assertEquals(compileAndRun("10 PRINT ASC(\" \")"), " 32 ");
		assertRuntimeError(compileAndRun("10 PRINT ASC(\"\")"));
	}

	@Test
	public void testCHR() {
		assertEquals(compileAndRun("10 PRINT CHR$(65)"), "A");
		assertEquals(compileAndRun("10 PRINT CHR$(32)"), " ");

		assertEquals(compileAndRun("10 PRINT CHR$(65.2)"), "A");
		assertEquals(compileAndRun("10 PRINT CHR$(65.5)"), "B");
		assertEquals(compileAndRun("10 PRINT CHR$(65.7)"), "B");

		assertRuntimeError(compileAndRun("10 PRINT CHR$(-1)"));
		assertRuntimeError(compileAndRun("10 PRINT CHR$(128)"));
	}

	@Test
	public void testEXP() {
		assertEquals(compileAndRun("10 PRINT EXP(0)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT EXP(88)"), "Overflow" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT EXP(-1/0)"), "Division by zero" + CR + " 0 ");
		assertEquals(compileAndRun("10 PRINT EXP(1/0)"), "Division by zero" + CR + "Overflow" + CR + " Infinity ");
	}

	@Test
	public void testFIX() {
		assertEquals(compileAndRun("10 PRINT FIX(1.7)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT FIX(1.5)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT FIX(1.2)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT FIX(1.0)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT FIX(0.7)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(0.5)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(0.2)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(1234567.1)"), " 1234567 ");
		assertEquals(compileAndRun("10 PRINT FIX(1E10)"), " 1E+10 ");

		assertEquals(compileAndRun("10 PRINT FIX(-0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(-0.2)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(-0.5)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(-0.7)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT FIX(-1)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT FIX(-1.2)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT FIX(-1.5)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT FIX(-1.7)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT FIX(-1234567.1)"), "-1234567 ");
		assertEquals(compileAndRun("10 PRINT FIX(-1E10)"), "-1E+10 ");
	}

	@Test
	public void testINSTR() {
		assertEquals(compileAndRun("10 PRINT INSTR(1, \"HELLO\", \"LL\")"), " 3 ");
		assertEquals(compileAndRun("10 PRINT INSTR(1, \"HELLO\", \"X\")"), " 0 ");

		assertRuntimeError(compileAndRun("10 PRINT INSTR(0, \"HELLOX\", \"LL\")"));
		assertRuntimeError(compileAndRun("10 PRINT INSTR(-1, \"HELLO\", \"LL\")"));
		assertEquals(compileAndRun("10 PRINT INSTR(5, \"HELLO\", \"LL\")"), " 0 ");

		assertEquals(compileAndRun("10 PRINT INSTR(6, \"HELLO\", \"LL\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INSTR(255, \"HELLO\", \"LL\")"), " 0 ");
		assertRuntimeError(compileAndRun("10 PRINT INSTR(256, \"HELLO\", \"LL\")"));

		assertEquals(compileAndRun("10 PRINT INSTR(1, \"\", \"LL\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INSTR(1, \"HELLO\", \"\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT INSTR(2, \"HELLO\", \"\")"), " 2 ");

		assertEquals(compileAndRun("10 PRINT INSTR(\"HELLO\", \"LL\")"), " 3 ");
		assertEquals(compileAndRun("10 PRINT INSTR(\"HELLO\", \"X\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INSTR(\"\", \"LL\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INSTR(\"HELLO\", \"\")"), " 1 ");

		assertCompileError(compileAndRun("10 PRINT INSTR( : END"));
		assertCompileError(compileAndRun("10 PRINT INSTR() : END"));
		assertCompileError(compileAndRun("10 PRINT INSTR(1) : END"));
		assertCompileError(compileAndRun("10 PRINT INSTR(\"HELLO\") : END"));
		assertCompileError(compileAndRun("10 PRINT INSTR(\"HELLO\", 1) : END"));
		assertCompileError(compileAndRun("10 PRINT INSTR(\"HELLO\", 1, ) : END"));
		assertCompileError(compileAndRun("10 PRINT INSTR(\"HELLO\", \"EL\", ) : END"));
		assertCompileError(compileAndRun("10 PRINT INSTR(\"HELLO\", \"EL\", : END"));

		// code coverage
		assertCompileError(compileAndRun("10 PRINT INSTR(1, 1)"));
		assertCompileError(compileAndRun("10 PRINT INSTR(1, )"));
		assertCompileError(compileAndRun("10 PRINT INSTR(1, \"XXX\")"));
	}

	@Test
	public void testINT() {
		assertEquals(compileAndRun("10 PRINT INT(2)"), " 2 ");
		assertEquals(compileAndRun("10 PRINT INT(1.7)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT INT(1.5)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT INT(1.2)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT INT(1.0)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT INT(0.7)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INT(0.5)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INT(0.2)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INT(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INT(1234567.1)"), " 1234567 ");
		assertEquals(compileAndRun("10 PRINT INT(1E10)"), " 1E+10 ");

		assertEquals(compileAndRun("10 PRINT INT(-0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT INT(-0.2)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT INT(-0.5)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT INT(-0.7)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT INT(-1)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT INT(-1.2)"), "-2 ");
		assertEquals(compileAndRun("10 PRINT INT(-1.5)"), "-2 ");
		assertEquals(compileAndRun("10 PRINT INT(-1.7)"), "-2 ");
		assertEquals(compileAndRun("10 PRINT INT(-1234567.1)"), "-1234568 ");
		assertEquals(compileAndRun("10 PRINT INT(-1E10)"), "-1E+10 ");
	}

	@Test
	public void testLEFT() {
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 0)"), "");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", -0)"), "");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 1)"), "A");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 1.4)"), "A");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 1.5)"), "AB");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 1.8)"), "AB");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 2)"), "AB");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 4)"), "ABCD");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"ABCD\", 10)"), "ABCD");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"\", 0)"), "");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"\", 1)"), "");
		assertEquals(compileAndRun("10 PRINT LEFT$(\"\", 2)"), "");

		assertRuntimeError(compileAndRun("10 PRINT LEFT$(\"ABCD\", -1)"));
		assertRuntimeError(compileAndRun("10 PRINT LEFT$(\"ABCD\", 256)"));
	}

	@Test
	public void testLEN() {
		assertEquals(compileAndRun("10 PRINT LEN(\"\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT LEN(\"A\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT LEN(\"ABCD\")"), " 4 ");
		assertEquals(compileAndRun("10 PRINT LEN(\"ABCD\" + \"EFGH\")"), " 8 ");

		assertCompileError(compileAndRun("10 PRINT LEN(1)"));
	}

	@Test
	public void testLOG() {
		assertEquals(compileAndRun("10 PRINT LOG(1)"), " 0 ");

		assertRuntimeError(compileAndRun("10 PRINT LOG(0)"));
		assertRuntimeError(compileAndRun("10 PRINT LOG(-1)"));
	}

	@Test
	public void testMID() {
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3, 1)"), "C");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3, 1.2)"), "C");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3, 1.5)"), "CD");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3, 1.7)"), "CD");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3, 2)"), "CD");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3, 3)"), "CD");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3, 255)"), "CD");

		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3.2, 1)"), "C");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3.5, 1)"), "D");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3.7, 1)"), "D");

		assertEquals(compileAndRun("10 PRINT MID$(\"\", 3, 1)"), "");
		assertRuntimeError(compileAndRun("10 PRINT MID$(\"ABCD\", -1, 1)"));
		assertRuntimeError(compileAndRun("10 PRINT MID$(\"ABCD\", 0, 1)"));
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 255, 1)"), "");
		assertRuntimeError(compileAndRun("10 PRINT MID$(\"ABCD\", 256, 1)"));
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 5, 1)"), "");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 5, 2)"), "");
		assertRuntimeError(compileAndRun("10 PRINT MID$(\"ABCD\", 5, -1)"));

		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 3)"), "CD");
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 4)"), "D");

		assertEquals(compileAndRun("10 PRINT MID$(\"\", 3)"), "");
		assertRuntimeError(compileAndRun("10 PRINT MID$(\"ABCD\", -1)"));
		assertRuntimeError(compileAndRun("10 PRINT MID$(\"ABCD\", 0)"));
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 255)"), "");
		assertRuntimeError(compileAndRun("10 PRINT MID$(\"ABCD\", 256)"));
		assertEquals(compileAndRun("10 PRINT MID$(\"ABCD\", 5)"), "");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT MID$( , 1)"));
		assertCompileError(compileAndRun("10 PRINT MID$(1, 1)"));
		assertCompileError(compileAndRun("10 PRINT MID$(\"ABC\" 1, 1)"));
		assertCompileError(compileAndRun("10 PRINT MID$(\"ABC\", \"XXX\", 1)"));
		assertCompileError(compileAndRun("10 PRINT MID$(\"ABC\", 1, \"XXX\")"));
		assertCompileError(compileAndRun("10 PRINT MID$(\"ABC\", 1, 1"));
	}

	@Test
	public void testMOD() {
		assertEquals(compileAndRun("10 PRINT 6 MOD 4"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 6 MOD 1"), " 0 ");
		assertEquals(compileAndRun("10 PRINT 6.2 MOD 4"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 6.5 MOD 4"), " 3 ");
		assertEquals(compileAndRun("10 PRINT 6.7 MOD 4"), " 3 ");
		assertEquals(compileAndRun("10 PRINT 6 MOD 4.2"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 6 MOD 4.5"), " 1 ");
		assertEquals(compileAndRun("10 PRINT 6 MOD 4.7"), " 1 ");

		assertEquals(compileAndRun("10 PRINT -6 MOD 4"), "-2 ");
		assertEquals(compileAndRun("10 PRINT 6 MOD -4"), " 2 ");
		assertEquals(compileAndRun("10 PRINT -6 MOD -4"), "-2 ");

		assertEquals(compileAndRun("10 PRINT 1 MOD 0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT 1 MOD -0"), "Division by zero" + CR + " Infinity ");
		assertEquals(compileAndRun("10 PRINT -1 MOD 0"), "Division by zero" + CR + "-Infinity ");
		assertEquals(compileAndRun("10 PRINT -1 MOD -0"), "Division by zero" + CR + "-Infinity ");

		assertRuntimeError(compileAndRun("10 PRINT 32768 MOD 10"));
		assertRuntimeError(compileAndRun("10 PRINT 10 MOD 32768"));

		assertEquals(compileAndRun("10 PRINT 10.4 MOD 4"), " 2 ");
		assertEquals(compileAndRun("10 PRINT 25.68 MOD 6.99"), " 5 ");

		assertEquals(compileAndRun("10 PRINT 14 \\ 2 MOD 6 \\ 3"), " 1 ");

		// code coverage
		assertCompileError(compileAndRun("10 PRINT 1 MOD : PRINT"));
		assertCompileError(compileAndRun("10 PRINT 1 MOD"));
	}

	@Test
	public void testPOS() {
		assertEquals(compileAndRun("10 PRINT POS(0)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT \" \";POS(0)"), "  2 ");
		assertEquals(compileAndRun("10 PRINT ,POS(0)"), "               15 ");
	}

	@Test
	public void testRIGHT() {
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 0)"), "");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", -0)"), "");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 1)"), "D");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 1.4)"), "D");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 1.5)"), "CD");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 1.8)"), "CD");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 2)"), "CD");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 4)"), "ABCD");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 10)"), "ABCD");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"\", 0)"), "");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"\", 1)"), "");
		assertEquals(compileAndRun("10 PRINT RIGHT$(\"\", 2)"), "");

		assertRuntimeError(compileAndRun("10 PRINT RIGHT$(\"ABCD\", -1)"));
		assertRuntimeError(compileAndRun("10 PRINT RIGHT$(\"ABCD\", 256)"));
	}

	@Test
	public void testRND() {
		assertEquals(compileAndRun("10 A = RND(1) : B = RND(0) : PRINT A = B"), "-1 ");

		assertCompileError(compileAndRun("10 PRINT RND(\"XXX\")"));
		assertRuntimeError(compileAndRun("10 PRINT RND(-1)")); // not supported, but MS-BASIC does
	}

	@Test
	public void testSGN() {
		assertEquals(compileAndRun("10 PRINT SGN(1)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT SGN(0.1)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT SGN(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT SGN(-0.1)"), "-1 ");
		assertEquals(compileAndRun("10 PRINT SGN(-1)"), "-1 ");

		assertCompileError(compileAndRun("10 PRINT SGN(\"XXX\")"));
	}

	@Test
	public void testSPACE() {
		assertEquals(compileAndRun("10 PRINT SPACE$(0);\"X\""), "X");
		assertEquals(compileAndRun("10 PRINT SPACE$(0.2);\"X\""), "X");
		assertEquals(compileAndRun("10 PRINT SPACE$(0.5);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT SPACE$(0.7);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT SPACE$(1);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT SPACE$(4);\"X\""), "    X");
		assertEquals(compileAndRun("10 PRINT \"X\";SPACE$(4);\"Y\""), "X    Y");
		assertRuntimeError(compileAndRun("10 PRINT SPACE$(-1);\"X\""));
		assertRuntimeError(compileAndRun("10 PRINT SPACE$(256);\"X\""));
	}

	@Test
	public void testSPC() {
		assertEquals(compileAndRun("10 PRINT SPC(0);\"X\""), "X");
		assertEquals(compileAndRun("10 PRINT SPC(0.2);\"X\""), "X");
		assertEquals(compileAndRun("10 PRINT SPC(0.5);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT SPC(0.7);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT SPC(1);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT SPC(4);\"X\""), "    X");
		assertEquals(compileAndRun("10 PRINT \"X\";SPC(4);\"Y\""), "X    Y");
		assertRuntimeError(compileAndRun("10 PRINT SPC(-1);\"X\""));
		assertRuntimeError(compileAndRun("10 PRINT SPC(256);\"X\""));
	}

	@Test
	public void testSQR() {
		assertEquals(compileAndRun("10 PRINT SQR(0)"), " 0 ");
		assertEquals(compileAndRun("10 PRINT SQR(1)"), " 1 ");
		assertEquals(compileAndRun("10 PRINT SQR(4)"), " 2 ");
		assertEquals(compileAndRun("10 PRINT SQR(65536)"), " 256 ");

		assertRuntimeError(compileAndRun("10 PRINT SQR(-1)"));
	}

	@Test
	public void testSTOP() {
		assertEquals(compileAndRun("10 X = 0" + CR + "20 IF X = 5 THEN STOP" + CR + "30 PRINT X; : X = X + 1 : GOTO 20"), " 0  1  2  3  4 ");
	}

	@Test
	public void testSTR() {
		assertEquals(compileAndRun("10 PRINT STR$(0)"), " 0");
		assertEquals(compileAndRun("10 PRINT STR$(-0)"), " 0");
		assertEquals(compileAndRun("10 PRINT STR$(1)"), " 1");
		assertEquals(compileAndRun("10 PRINT STR$(-1)"), "-1");
		assertEquals(compileAndRun("10 PRINT STR$(1.2345)"), " 1.2345");
		assertEquals(compileAndRun("10 PRINT STR$(-1.2345)"), "-1.2345");
		assertEquals(compileAndRun("10 PRINT STR$(123456)"), " 123456");
		assertEquals(compileAndRun("10 PRINT STR$(-123456)"), "-123456");

		assertCompileError(compileAndRun("10 PRINT STR$(\"X\")"));
	}

	@Test
	public void testTAB() {
		assertEquals(compileAndRun("10 PRINT TAB(2);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT TAB(2.2);\"X\""), " X");
		assertEquals(compileAndRun("10 PRINT TAB(2.5);\"X\""), "  X");
		assertEquals(compileAndRun("10 PRINT TAB(2.7);\"X\""), "  X");
		assertEquals(compileAndRun("10 PRINT TAB(2);\"X\";TAB(10);\"Y\""), " X       Y");
		assertEquals(compileAndRun("10 PRINT TAB(2);\"X\";TAB(10);\"Y\";TAB(2);\"Z\""), " X       Y" + CR + " Z");

		assertRuntimeError(compileAndRun("10 PRINT TAB(-1);\"X\""));
		assertRuntimeError(compileAndRun("10 PRINT TAB(0);\"X\""));
		assertRuntimeError(compileAndRun("10 PRINT TAB(256);\"X\""));
		assertRuntimeError(compileAndRun("10 PRINT TAB(1000);\"X\""));
	}

	@Test
	public void testVAL() {
		assertEquals(compileAndRun("10 PRINT VAL(\"0\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1\")"), "-1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\" 1\")"), " 1 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"1.2345\")"), " 1.2345 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-1.2345\")"), "-1.2345 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"123456\")"), " 123456 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-123456\")"), "-123456 ");

		assertEquals(compileAndRun("10 PRINT VAL(CHR$(9)+CHR$(10)+CHR$(13)+\" 1\")"), " 1 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT VAL(\" \")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT VAL(\" 1A\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"ABC\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT VAL(\".\")"), " 0 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"-.\")"), " 0 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"123EX\")"), " 123 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"123E-\")"), " 123 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"123E-1\")"), " 12.3 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"1.23E--\")"), " 1.23 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.234XXX\")"), " 1.234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.234E\")"), " 1.234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.234EXX\")"), " 1.234 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.23XX\")"), " 1.23 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.XX\")"), " 1 ");

		assertEquals(compileAndRun("10 PRINT VAL(\"1.23\")"), " 1.23 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1.2.3\")"), " 1.2 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"1..2\")"), " 1 ");
		assertEquals(compileAndRun("10 PRINT VAL(\"..12\")"), " 0 ");

		assertCompileError(compileAndRun("10 PRINT VAL(1)"));
	}

	// Utility Methods ///////////////////////////////////////////////////////////

	private static void assertEquals(Object actual, Object expected) {
		org.junit.Assert.assertEquals(expected, actual);
	}

	private static void assertCompileError(String actual) {
		assertTrue(actual.startsWith("COMPILE ERROR"));
	}

	private static void assertRuntimeError(String actual) {
		assertTrue(actual.startsWith("" + CR + "ERROR:"));
	}

	private static String compileAndRun(String strStatements) {
		return compileAndRun(strStatements, null);
	}

	private static String compileAndRun(String strStatements, String lineOfInput) {
		testCount++;

		BufferedReader inReader = null;
		OutputStream outStream = null;

		String output = null;
		Process p = null;
		try {
			StringBuffer sb = new StringBuffer();
			inReader = new BufferedReader(new StringReader(strStatements));
			outStream = new FileOutputStream(TEST_CLASS_FULLFILENAME);

			// compile
			BASICCompiler.exec(inReader, outStream, TEST_CLASS_NAME);

			// execute
			ProcessBuilder pb = new ProcessBuilder("java", TEST_CLASS_NAME);
			pb.directory(new File(TEST_FOLDER_PATH));
			pb.redirectErrorStream(true);
			p = pb.start();

			if (lineOfInput != null) {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
				writer.write(lineOfInput);
				writer.newLine();
				writer.flush();
				writer.close();

				// Hint: Handles single-line responses only
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String lineOfOutput = reader.readLine();
				reader.close();
				sb.append(lineOfOutput);
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				boolean isFirstLine = true;
				while (true) {
					String lineOfOutput = reader.readLine();
					if (lineOfOutput == null) {
						break;
					}
					if (isFirstLine) {
						isFirstLine = false;
					} else {
						sb.append(CR);
					}
					sb.append(lineOfOutput);
				}
				reader.close();
			}
			output = sb.toString();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (CompileException ex) {
			output = ex.getFullMessage();
		} finally {
			closeGracefully(outStream);
			closeGracefully(inReader);
			File classFile = new File(TEST_CLASS_FULLFILENAME);
			if (classFile.exists()) {
				// TODO: Delete commented!
				// classFile.delete();
			}
			if (p != null) {
				p.destroy();
			}
		}
		return output;
	}

	private static void closeGracefully(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
