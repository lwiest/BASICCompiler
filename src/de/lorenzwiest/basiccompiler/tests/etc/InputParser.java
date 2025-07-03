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

public class InputParser {
	public static final String BAD_CHARACTER_AFTER_QUOTE = "Bad character after quote!";

	public static String parseAndFormat(String argType, String input) {
		String[] args = parse(argType, input);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			if (i == 0) {
				sb.append("|");
			}
			sb.append(args[i] + "|");
		}
		return sb.toString();
	}

	public static String[] parse(String argType, String input) {
		char[][] charArray = input(argType.toCharArray(), input.toCharArray());
		String[] args = new String[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			args[i] = String.valueOf(charArray[i]);
		}
		return args;
	}

	public static char[][] input(char[] argTypes, char[] inputChrs) {
		int argLen = argTypes.length;
		char[][] resultArray = new char[argLen][0];

		int argIndex = 0;

		int start = -1;
		int end = -1;
		boolean isQuoting = false;

		int UNDEFINED = 0;
		int QUOTE = 1;
		int VERBATIM = 2;
		int dataType = UNDEFINED;

		for (int i = 0; i < inputChrs.length; i++) {
			char chr = inputChrs[i];
			if ((chr == ' ') || (chr == '\t')) {
				if (isQuoting == false) {
					continue;
				}
			}
			if ((chr == ',') && (isQuoting == false)) {
				if (argIndex >= argLen) {
					return error("Too many parsed args");
				}

				resultArray[argIndex++] = substring(inputChrs, start, end);

				start = -1;
				end = -1;
				dataType = UNDEFINED;
				continue;
			}
			if (chr == '"') {
				if (dataType == UNDEFINED) {
					dataType = QUOTE;
					isQuoting = true;
					continue;
				} else if (isQuoting) {
					isQuoting = false;
					continue;
				}
			}
			if ((dataType == QUOTE) && (isQuoting == false)) {
				return error(BAD_CHARACTER_AFTER_QUOTE);
			}
			if (dataType == UNDEFINED) {
				dataType = VERBATIM;
			}
			if (start == -1) {
				start = i;
			}
			end = i + 1;
		}

		if (argIndex >= argLen) {
			return error("Too many parsed args");
		}
		resultArray[argIndex++] = substring(inputChrs, start, end);

		if (argIndex != argLen) {
			return error("Too few args parsed");
		}

		for (int i = 0; i < argLen; i++) {
			if (argTypes[i] == 'F') {
				if (resultArray[i].length != 0) {
					try {
						Float.parseFloat(String.valueOf(resultArray[i]));
					} catch (NumberFormatException e) {
						return error("Is not a number");
					}
				}
			}
		}

		return resultArray;
	}

	private static char[] substring(char[] chrs, int start, int end) {
		int len = end - start;
		char[] result = new char[len];
		for (int i = 0; i < len; i++) {
			result[i] = chrs[start + i];
		}
		return result;
	}

	private static char[][] error(String message) {
		char[][] result = new char[1][];
		result[0] = message.toCharArray();
		return result;
	}
}
