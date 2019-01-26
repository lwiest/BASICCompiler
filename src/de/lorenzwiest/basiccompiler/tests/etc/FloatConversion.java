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

package de.lorenzwiest.basiccompiler.tests.etc;

import org.junit.Test;

public class FloatConversion {

	public static void main(String[] args) {
		FloatConversion instance = new FloatConversion();

		System.out.println("*** testAdHoc ***");
		instance.testAdHoc();

		System.out.println("*** testCharsToFloat ***");
		instance.testCharsToFloat();
		System.out.println("*** testCharsToFloat_NearZero ***");
		instance.testCharsToFloat_NearZero();

		System.out.println("*** testFloatToChars_1 ***");
		instance.testFloatToChars_1();
		System.out.println("*** testFloatToChars_2 ***");
		instance.testFloatToChars_2();
		System.out.println("*** testFloatToChars_NearZero ***");
		instance.testFloatToChars_NearZero();
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testAdHoc() {
		equal(charsToFloat("1E+38"), 1E+38f);
		equal(floatToChars(1E+38f), " 1E+38");
		int exp = (int) Math.floor((float) (Math.log(1E+38f) / Math.log(10f)));
		System.out.println(exp);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testCharsToFloat() {
		equal(charsToFloat("123"), 123.0f);
		equal(charsToFloat("123456"), 123456.0f);
		equal(charsToFloat("12345678"), 12345678.0f);

		equal(charsToFloat("123EX"), 123.0f);
		equal(charsToFloat("123E-"), 123.0f);
		equal(charsToFloat("123E-1"), 12.3f);

		equal(charsToFloat("  1.0"), 1.0f);
		equal(charsToFloat("-1.0"), -1.0f);

		equal(charsToFloat("12.345"), 12.345f);
		equal(charsToFloat("0.1"), 0.1f);
		equal(charsToFloat("0.12345678"), 0.12345678f);

		equal(charsToFloat(""), Float.NaN);
		equal(charsToFloat(" "), Float.NaN);
		equal(charsToFloat("ABC"), Float.NaN);
		equal(charsToFloat("."), Float.NaN);
		equal(charsToFloat("-."), Float.NaN);
		equal(charsToFloat("1.23"), 1.23f);
		equal(charsToFloat("1.2.3"), 1.2f);
		equal(charsToFloat("1..2"), 1.0f);
		equal(charsToFloat("..12"), Float.NaN);

		equal(charsToFloat("1E1"), 10f);
		equal(charsToFloat("1e1"), 10f);

		equal(charsToFloat("1.E1"), 10f);
		equal(charsToFloat("1.0E1"), 10f);
		equal(charsToFloat("1.0E+1"), 10f);
		equal(charsToFloat("1.0E+01"), 10f);
		equal(charsToFloat("1.0E+001"), 10f);

		equal(charsToFloat("1E-1"), 0.1f);
		equal(charsToFloat("1.E-1"), 0.1f);
		equal(charsToFloat("1.0E-1"), 0.1f);
		equal(charsToFloat("1.0E-1"), 0.1f);
		equal(charsToFloat("1.0E-01"), 0.1f);
		equal(charsToFloat("1.0E-001"), 0.1f);

		equal(charsToFloat("1.234XXX"), 1.234f);
		equal(charsToFloat("1.234E--"), 1.234f);
		equal(charsToFloat("1.234EXX"), 1.234f);
		equal(charsToFloat("1.234E"), 1.234f);

		equal(charsToFloat("1.23XX"), 1.23f);
		equal(charsToFloat("1.XX"), 1.0f);
		equal(charsToFloat("0.3333"), 0.3333f);
		equal(charsToFloat("0.33333"), 0.33333f);
		equal(charsToFloat("0.333333"), 0.333333f);
		equal(charsToFloat("0.3333333"), 0.3333333f);
		equal(charsToFloat("0.123456789"), 0.123456789f);

		// equal(toFloat(c("0.12345678901")), 0.12345678901f);
	}

	@Test
	public void testCharsToFloat_NearZero() {
		equal(floatToChars(charsToFloat("100000E-49")), "XXX");
		equal(floatToChars(charsToFloat("1E-33")), " 1E-33");
		equal(floatToChars(charsToFloat("1E-34")), " 1E-34");
		equal(floatToChars(charsToFloat("1E-35")), " 1E-35");
		equal(floatToChars(charsToFloat("1E-36")), " 1E-36");
		equal(floatToChars(charsToFloat("1E-37")), " 1E-37");
		equal(floatToChars(charsToFloat("4E-38")), " 4E-38");
		equal(floatToChars(charsToFloat("3.5E-38")), " 3.5E-38");
		equal(floatToChars(charsToFloat("3.4E-38")), " 3.4E-38");
		equal(floatToChars(charsToFloat("3E-38")), " 3E-38");
		equal(floatToChars(charsToFloat("1.2E-38")), " 1.2E-38");
		equal(floatToChars(charsToFloat("1.1E-38")), " 1.1E-38");
		equal(floatToChars(charsToFloat("1E-38")), " 1E-38"); // ?
		equal(floatToChars(charsToFloat("10E-39")), " 10E-39"); // ?
		equal(floatToChars(charsToFloat("1E-39")), " 1E-39");
		equal(floatToChars(charsToFloat("1E-45")), " 1E-45");
		equal(floatToChars(charsToFloat("0.9E-45")), " 0.9E-45");
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testFloatToChars_1() {
		equal(floatToChars(1E-38f), "XXX");
	}

	@Test
	public void testFloatToChars_2() {
		equal(floatToChars(1f / 2f), " .5");
		equal(floatToChars(1f / 3f), " .3333333");
		equal(floatToChars(1f / 4f), " .25");
		equal(floatToChars(1f / 5f), " .2");
		equal(floatToChars(1f / 6f), " .1666666");
		equal(floatToChars(1f / 7f), " .1428571");
		equal(floatToChars(1f / 8f), " .125");
		equal(floatToChars(1f / 9f), " .1111111");
		equal(floatToChars(1f / 10f), " .1");
		equal(floatToChars(1f / 11f), " .0909090");
		equal(floatToChars(1f / 12f), " .0833333");

		equal(floatToChars(0), " 0");

		equal(floatToChars(1), " 1");
		equal(floatToChars(12), " 12");
		equal(floatToChars(123), " 123");
		equal(floatToChars(1234), " 1234");
		equal(floatToChars(12345), " 12345");
		equal(floatToChars(123456), " 123456");
		equal(floatToChars(1234567), " 1234567");
		equal(floatToChars(12345678), " 1.234567E+07");

		equal(floatToChars(1.2f), " 1.2");
		equal(floatToChars(1.23f), " 1.23");
		equal(floatToChars(1.234f), " 1.234");
		equal(floatToChars(1.2345f), " 1.2345");
		equal(floatToChars(1.23456f), " 1.23456");
		equal(floatToChars(1.234567f), " 1.234567");
		equal(floatToChars(1.2345678f), " 1.234567");

		equal(floatToChars(12.3f), " 12.3");
		equal(floatToChars(12.34f), " 12.34");
		equal(floatToChars(12.345f), " 12.345");
		equal(floatToChars(12.3456f), " 12.3456");
		equal(floatToChars(12.34567f), " 12.34567");
		equal(floatToChars(12.345678f), " 12.34567");

		equal(floatToChars(0.1f), " .1");
		equal(floatToChars(0.01f), " .01");
		equal(floatToChars(0.001f), " .001");
		equal(floatToChars(0.0001f), " .0001");
		equal(floatToChars(0.00001f), " .00001");
		equal(floatToChars(0.000001f), " .000001");
		equal(floatToChars(0.0000001f), " .0000001");
		equal(floatToChars(0.00000001f), " 1E-08");

		equal(floatToChars(0.1234f), " .1234");
		equal(floatToChars(0.12345678f), " .1234567");

		equal(floatToChars(0.01f), " .01");
		equal(floatToChars(0.01234f), " .01234");
		equal(floatToChars(0.012345678f), " .0123456");

		equal(floatToChars(0.001f), " .001");
		equal(floatToChars(0.001234f), " .001234");
		equal(floatToChars(0.001234567f), " .0012345");

		equal(floatToChars(0.00000123456789f), " .0000012");
		equal(floatToChars(0.00000001234f), " 1.234E-08");
		equal(floatToChars(0.0000000123456789f), " 1.234567E-08");

		equal(floatToChars(0.000000123456789f), " .0000001");
		equal(floatToChars(0.00000001f), " 1E-08");
		equal(floatToChars(0.00000001234f), " 1.234E-08");
		equal(floatToChars(0.0000000123456789f), " 1.234567E-08");

		equal(floatToChars(0.00000003f), " 3E-08");
		equal(floatToChars(0.000000033f), " 3.3E-08");
		equal(floatToChars(0.0000000333f), " 3.33E-08");
		equal(floatToChars(0.00000003333f), " 3.333E-08");
		equal(floatToChars(0.000000033333f), " 3.3333E-08");
		equal(floatToChars(0.0000000333333f), " 3.33333E-08");
		equal(floatToChars(0.00000003333333f), " 3.333333E-08");

		equal(floatToChars(13000000f), " 1.3E+07");
		equal(floatToChars(10000000f), " 1E+07");
		equal(floatToChars(12340000f), " 1.234E+07");
		equal(floatToChars(12345600f), " 1.23456E+07");
		equal(floatToChars(12345678f), " 1.234567E+07");

		equal(floatToChars(1300000000f), " 1.3E+09");
		equal(floatToChars(3000000000f), " 3E+09");

		equal(floatToChars(10000000000f), " 1E+10");
		equal(floatToChars(13000000000f), " 1.3E+10");
		equal(floatToChars(13300000000f), " 1.33E+10");
		equal(floatToChars(13330000000f), " 1.333E+10");
		equal(floatToChars(13333000000f), " 1.3333E+10");
		equal(floatToChars(13333300000f), " 1.33333E+10");
		equal(floatToChars(13333330000f), " 1.333333E+10");
		equal(floatToChars(13333333000f), " 1.333333E+10");

		equal(floatToChars(12340000000f), " 1.234E+10");
		equal(floatToChars(12345600000f), " 1.23456E+10");
		equal(floatToChars(12345678900f), " 1.234567E+10");

		equal(floatToChars(-0f), " 0");
		equal(floatToChars(-1f), "-1");
		equal(floatToChars(-1.234f), "-1.234");
		equal(floatToChars(-3000000000f), "-3E+09");

		// BASIC tests
		System.out.println("BASIC Tests");
		equal(floatToChars(1 / 3f), " .3333333"); // ".3333334"
		equal(floatToChars(1 / 7f), " .1428571"); // ".1428572"
		equal(floatToChars(1000000f), " 1000000");
		equal(floatToChars(10000000f), " 1E+07");
		equal(floatToChars(12345678f), " 1.234567E+07"); // "1.234568E+07"
		equal(floatToChars(0.123456789f), " .1234567"); // ".1234568"
		equal(floatToChars(0.0000001f), " .0000001");
		equal(floatToChars(0.00000012f), " 1.2E-07");
		equal(floatToChars(0.00000001f), " 1E-08");
		equal(floatToChars(0.0000000123f), " 1.23E-08");
		equal(floatToChars(0.0000000123456789f), " 1.234567E-08"); // "1.234568E-08"
		equal(floatToChars(-0.0f), " 0");
	}

	@Test
	public void testFloatToChars_NearZero() {
		equal(floatToChars(1E-44f), " 1E-44");
		equal(floatToChars(1E-32f), " 1E-32");
		equal(floatToChars(1.5E-32f), " 1.5E-32");
		equal(floatToChars(1E-33f), " 1E-33");
		equal(floatToChars(1.5E-33f), " 1.5E-33");
		equal(floatToChars(1E-34f), " 1E-34");
		equal(floatToChars(1E-35f), " 1E-35");
		equal(floatToChars(1E-36f), " 1E-36");
		equal(floatToChars(1E-37f), " 1E-37");
		equal(floatToChars(4E-38f), " 4E-38");
		equal(floatToChars(3.5E-38f), " 3.5E-38");
		equal(floatToChars(3.4E-38f), " 3.4E-38");
		equal(floatToChars(3E-38f), " 3E-38");
		equal(floatToChars(1.2E-38f), " 1.2E-38");
		equal(floatToChars(1.1E-38f), " 1.1E-38");
		equal(floatToChars(1E-38f), " 1E-38"); // ?
		equal(floatToChars(1E-39f), " 1E-39");
		equal(floatToChars(1E-44f), " 1E-44");
		equal(floatToChars(1E-45f), " 1E-45");
		equal(floatToChars(0.9E-45f), " 0.9E-45");
	}

	///////////////////////////////////////////////////////////////////////////

	private static float charsToFloat(String str) {
		return charsToFloat(str.toCharArray());
	}

	private static float charsToFloat(char[] s) {
		int pos = 0;

		int chr;
		for (; pos < s.length; pos++) {
			chr = s[pos];
			if ((chr == ' ') || (chr == '\t') || (chr == '\r') || (chr == '\n')) {
				continue;
			}
			break;
		}

		if (pos >= s.length) {
			return Float.NaN;
		}

		boolean isNeg = false;
		chr = s[pos];
		if (chr == '-') {
			isNeg = true;
			pos++;
		} else if (chr == '+') {
			pos++;
		}

		float m = 0f;
		int mExp = -1;
		int dotExp = 0;

		boolean seenMantissa = false;
		boolean seenDot = false;

		for (; pos < s.length; pos++) {
			chr = s[pos];
			if ((chr >= '0') && (chr <= '9')) {
				m = (10 * m) + (chr - '0');
				mExp++;
				seenMantissa = true;
			} else if (chr == '.') {
				if (seenDot) {
					break;
				}
				dotExp = mExp;
				seenDot = true;
			} else {
				break;
			}
		}

		if (seenMantissa == false) {
			return Float.NaN;
		}

		boolean isExpNeg = false;
		int exp = 0;

		if ((pos + 1) < s.length) {
			chr = s[pos];
			if ((chr == 'E') || (chr == 'e')) {
				pos++;
				chr = s[pos];
				if (chr == '-') {
					isExpNeg = true;
					pos++;
				} else if (chr == '+') {
					pos++;
				}

				for (; pos < s.length; pos++) {
					chr = s[pos];
					if ((chr >= '0') && (chr <= '9')) {
						exp = (10 * exp) + (chr - '0');
					} else {
						break;
					}
				}
			}
		}

		if (isExpNeg) {
			exp = -exp;
		}

		int effExp = seenDot ? mExp - dotExp : 0;
		// was: float f = m / (float) Math.pow(10.0f, effExp - exp);
		float f = m * (float) Math.pow(10.0f, exp - effExp);
		if (isNeg) {
			f = -f;
		}
		return f;
	}

	private static char[] floatToChars(float f) {
		final int DIGITS_PRECISION = 7;
		final int MAX_EXPONENT = 38;

		if (f == 0.0) {
			return " 0".toCharArray();
		}
		if (f != f) {
			return " NaN".toCharArray();
		}
		if (f == Float.POSITIVE_INFINITY) {
			return " Infinity".toCharArray();
		}
		if (f == Float.NEGATIVE_INFINITY) {
			return "-Infinity".toCharArray();
		}

		char buf[] = new char[13]; // enough space for floats like "-1.234567E+00"
		int pos = 0;

		if (f >= 0f) {
			buf[pos++] = ' ';
		} else {
			buf[pos++] = '-';
			f = -f;
		}

		int exp = (int) Math.floor(((float) Math.log(f) / (float) Math.log(10)));
		int currExp = (f >= 1) ? exp : -1;

		int m;
		if (exp > ((-MAX_EXPONENT + (DIGITS_PRECISION - 1)) - 1)) {
			m = (int) (f * (float) Math.pow(10, DIGITS_PRECISION - 1 - exp));
		} else { // handle IEEE 754-1985 denormalization and gradual underflow
			m = (int) (f * (float) Math.pow(10, MAX_EXPONENT) * (float) Math.pow(10, (-MAX_EXPONENT + (DIGITS_PRECISION - 1)) - exp));
		}

		int tmpExp = exp;
		boolean useExp = (f < 1E-07f) || (f >= 1E+07f);
		if (useExp) {
			currExp = 0;
			tmpExp = 0;
		}

		int pow10 = (int) Math.pow(10, DIGITS_PRECISION - 1);
		int digits = 0;
		while (digits < DIGITS_PRECISION) {
			if ((m <= 0) && (currExp < 0)) {
				break;
			}

			if (currExp == -1) {
				buf[pos++] = '.';
			}
			if (currExp > tmpExp) {
				buf[pos++] = '0';
			} else {
				int digit = m / pow10;
				m = m - (digit * pow10);
				buf[pos++] = (char) ('0' + digit);
				pow10 /= 10;
			}
			currExp--;
			digits++;
		}

		if (useExp) {
			buf[pos++] = 'E';
			if (exp >= 0) {
				buf[pos++] = '+';
			} else {
				buf[pos++] = '-';
				exp = -exp;
			}
			if (exp > 9) {
				buf[pos++] = (char) ('0' + (exp / 10));
			} else {
				buf[pos++] = '0';
			}
			buf[pos++] = (char) ('0' + (exp % 10));
		}

		char[] result = new char[pos];
		for (--pos; pos >= 0; pos--) {
			result[pos] = buf[pos];
		}
		return result;
	}

	///////////////////////////////////////////////////////////////////////////

	private static void equal(char[] cActual, String strExpected) {
		String strActual = new String(cActual);
		System.out.print(strActual.equals(strExpected) ? " " : "!");
		System.out.println(" | act: \"" + strActual + "\" | exp: \"" + strExpected + "\"");
	}

	private static void equal(float fActual, float fExpected) {
		boolean isSame = false;

		boolean isFActualNaN = (fActual != fActual);
		boolean isFExpectedNaN = (fExpected != fExpected);
		if (isFActualNaN == isFExpectedNaN) {
			isSame = true;
		} else {
			isSame = (fActual == fExpected);
		}

		System.out.print(isSame ? " " : "!");
		System.out.println(" | act: \"" + fActual + "\" | exp: \"" + fExpected + "\"");
	}
}
