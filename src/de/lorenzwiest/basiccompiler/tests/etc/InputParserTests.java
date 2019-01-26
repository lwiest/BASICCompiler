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

public class InputParserTests {

	@Test
	public void testAdHoc() {
		// place ad-hoc tests here...
	}

	@Test
	public void testSuite() {
		assertEquals(exec("S", ""), "||");
		assertEquals(exec("S", " "), "||");
		assertEquals(exec("S", "  "), "||");

		assertEquals(exec("S", "ABC"), "|ABC|");
		assertEquals(exec("S", "ABC "), "|ABC|");
		assertEquals(exec("S", " ABC"), "|ABC|");
		assertEquals(exec("S", " ABC "), "|ABC|");

		assertEquals(exec("S", "ABC DEF"), "|ABC DEF|");
		assertEquals(exec("S", " ABC DEF"), "|ABC DEF|");
		assertEquals(exec("S", "ABC DEF "), "|ABC DEF|");
		assertEquals(exec("S", " ABC DEF "), "|ABC DEF|");

		assertEquals(exec("S", "\"ABC DEF\""), "|ABC DEF|");
		assertEquals(exec("S", " \"ABC DEF\""), "|ABC DEF|");
		assertEquals(exec("S", "\"ABC DEF\" "), "|ABC DEF|");
		assertEquals(exec("S", " \"ABC DEF\" "), "|ABC DEF|");

		assertEquals(exec("S", "\"ABC"), "|ABC|");
		assertEquals(exec("S", " \"ABC"), "|ABC|");
		assertEquals(exec("S", "\"ABC "), "|ABC |");
		assertEquals(exec("S", " \"ABC "), "|ABC |");

		assertEquals(exec("S", "ABC\""), "|ABC\"|");
		assertEquals(exec("S", " ABC\""), "|ABC\"|");
		assertEquals(exec("S", "ABC\" "), "|ABC\"|");
		assertEquals(exec("S", " ABC\" "), "|ABC\"|");

		assertEquals(exec("S", "\"ABC\""), "|ABC|");
		assertEquals(exec("S", " \"ABC\""), "|ABC|");
		assertEquals(exec("S", "\"ABC\" "), "|ABC|");
		assertEquals(exec("S", " \"ABC\" "), "|ABC|");

		assertEquals(exec("SS", "ABC,"), "|ABC||");
		assertEquals(exec("SS", "ABC ,"), "|ABC||");
		assertEquals(exec("SS", " ABC,"), "|ABC||");
		assertEquals(exec("SS", " ABC ,"), "|ABC||");

		assertEquals(exec("SS", ",ABC"), "||ABC|");
		assertEquals(exec("SS", ", ABC"), "||ABC|");
		assertEquals(exec("SS", ",ABC "), "||ABC|");
		assertEquals(exec("SS", ", ABC "), "||ABC|");

		assertEquals(exec("SS", "ABC,DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", " ABC,DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC ,DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", " ABC ,DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC, DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC,DEF "), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC, DEF "), "|ABC|DEF|");

		assertEquals(exec("SS", " ABC, DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", " ABC,DEF "), "|ABC|DEF|");
		assertEquals(exec("SS", " ABC, DEF "), "|ABC|DEF|");

		assertEquals(exec("SS", "ABC , DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC ,DEF "), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC , DEF "), "|ABC|DEF|");

		assertEquals(exec("SS", " ABC , DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", " ABC ,DEF "), "|ABC|DEF|");
		assertEquals(exec("SS", " ABC , DEF "), "|ABC|DEF|");

		assertEquals(exec("SS", "\"ABC\",DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", "\"ABC\" ,DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", "\"ABC\",DEF"), "|ABC|DEF|");
		assertEquals(exec("SS", "\"ABC\" ,DEF"), "|ABC|DEF|");

		assertEquals(exec("SS", "ABC,\"DEF\""), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC, \"DEF\""), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC,\"DEF\" "), "|ABC|DEF|");
		assertEquals(exec("SS", "ABC, \"DEF\" "), "|ABC|DEF|");

		assertEquals(exec("SS", "\"ABC\",\"DEF\""), "|ABC|DEF|");
		assertEquals(exec("SS", " \"ABC\",\"DEF\""), "|ABC|DEF|");
		assertEquals(exec("SS", "\"ABC\" ,\"DEF\""), "|ABC|DEF|");
		assertEquals(exec("SS", " \"ABC\" ,\"DEF\""), "|ABC|DEF|");
		assertEquals(exec("SS", "\"ABC\" , \"DEF\""), "|ABC|DEF|");
		assertEquals(exec("SS", "\"ABC\" ,\"DEF\" "), "|ABC|DEF|");
		assertEquals(exec("SS", "\"ABC\" , \"DEF\" "), "|ABC|DEF|");

		assertEquals(exec("SS", "\"ABC\"xxx,DEF"), "|" + InputParser.BAD_CHARACTER_AFTER_QUOTE + "|");
		assertEquals(exec("SS", "\"ABC\" xxx,DEF"), "|" + InputParser.BAD_CHARACTER_AFTER_QUOTE + "|");
		assertEquals(exec("SS", "\"ABC\" xxx"), "|" + InputParser.BAD_CHARACTER_AFTER_QUOTE + "|");

		assertEquals(exec("SS", ","), "|||");
		assertEquals(exec("SS", " ,"), "|||");
		assertEquals(exec("SS", ", "), "|||");
		assertEquals(exec("SS", " , "), "|||");

		assertEquals(exec("SS", "\"A,C\","), "|A,C||");
		assertEquals(exec("S", "\"A,C,"), "|A,C,|");
		assertEquals(exec("S", "\"A,,C,,"), "|A,,C,,|");
		assertEquals(exec("S", "xx\"xx\"xx"), "|xx\"xx\"xx|");

		assertEquals(exec("SSS", ",\"A,C\","), "||A,C||");
		assertEquals(exec("SS", ",\"A,C,"), "||A,C,|");

		assertEquals(exec("S", "A"), "|A|");

		assertEquals(exec("S", "\"\""), "||");
		assertEquals(exec("S", "\"\"\""), "|" + InputParser.BAD_CHARACTER_AFTER_QUOTE + "|");
		assertEquals(exec("S", "\"\"\"\""), "|" + InputParser.BAD_CHARACTER_AFTER_QUOTE + "|");
	}

	private static void assertEquals(Object actual, Object expected) {
		org.junit.Assert.assertEquals(expected, actual);
	}

	private String exec(String argType, String input) {
		return InputParser.parseAndFormat(argType, input);
	}
}
