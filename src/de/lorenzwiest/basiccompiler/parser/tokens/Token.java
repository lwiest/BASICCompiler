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

package de.lorenzwiest.basiccompiler.parser.tokens;

import java.util.HashMap;
import java.util.Map;

public class Token {

	public enum TokenType {
		NUM_CONSTANT,
		NUM_VAR_ID,
		NUM_ARRAY_VAR_ID,
		NUM_FN_ID,
		STR_CONSTANT,
		STR_VAR_ID,
		STR_ARRAY_VAR_ID,
		STR_FN_ID,
		KEYWORD,
		SPECIAL_CHARACTER,
		END_OF_INPUT,
		UNKNOWN
	}

	// maps must be listed before tokens
	private static Map<String, Token> KEYWORDS = new HashMap<String, Token>();
	private static Map<String, Token> SPECIAL_CHARACTERS = new HashMap<String, Token>();

	public static final Token ADD = new Token(TokenType.SPECIAL_CHARACTER, "+");
	public static final Token SUBTRACT = new Token(TokenType.SPECIAL_CHARACTER, "-");
	public static final Token MULTIPLY = new Token(TokenType.SPECIAL_CHARACTER, "*");
	public static final Token DIVIDE = new Token(TokenType.SPECIAL_CHARACTER, "/");
	public static final Token INT_DIVIDE = new Token(TokenType.SPECIAL_CHARACTER, "\\");
	public static final Token MOD = new Token(TokenType.KEYWORD, "MOD");
	public static final Token POWER = new Token(TokenType.SPECIAL_CHARACTER, "^");

	public static final Token AND = new Token(TokenType.KEYWORD, "AND");
	public static final Token OR = new Token(TokenType.KEYWORD, "OR");
	public static final Token XOR = new Token(TokenType.KEYWORD, "XOR");
	public static final Token NOT = new Token(TokenType.KEYWORD, "NOT");

	public static final Token LESS = new Token(TokenType.SPECIAL_CHARACTER, "<");
	public static final Token LESS_OR_EQUAL = new Token(TokenType.SPECIAL_CHARACTER, "<=");
	public static final Token EQUAL = new Token(TokenType.SPECIAL_CHARACTER, "=");
	public static final Token GREATER_OR_EQUAL = new Token(TokenType.SPECIAL_CHARACTER, ">=");
	public static final Token GREATER = new Token(TokenType.SPECIAL_CHARACTER, ">");
	public static final Token NOT_EQUAL = new Token(TokenType.SPECIAL_CHARACTER, "<>");

	public static final Token OPEN = new Token(TokenType.SPECIAL_CHARACTER, "(");
	public static final Token CLOSE = new Token(TokenType.SPECIAL_CHARACTER, ")");
	public static final Token COMMA = new Token(TokenType.SPECIAL_CHARACTER, ",");
	public static final Token SEMICOLON = new Token(TokenType.SPECIAL_CHARACTER, ";");
	public static final Token COLON = new Token(TokenType.SPECIAL_CHARACTER, ":");
	public static final Token DOLLAR_SIGN = new Token(TokenType.SPECIAL_CHARACTER, "$");

	public static final Token DATA = new Token(TokenType.KEYWORD, "DATA");
	public static final Token DEF = new Token(TokenType.KEYWORD, "DEF");
	public static final Token DIM = new Token(TokenType.KEYWORD, "DIM");
	public static final Token ELSE = new Token(TokenType.KEYWORD, "ELSE");
	public static final Token END = new Token(TokenType.KEYWORD, "END");
	public static final Token FOR = new Token(TokenType.KEYWORD, "FOR");
	public static final Token GOTO = new Token(TokenType.KEYWORD, "GOTO");
	public static final Token GOSUB = new Token(TokenType.KEYWORD, "GOSUB");
	public static final Token IF = new Token(TokenType.KEYWORD, "IF");
	public static final Token INPUT = new Token(TokenType.KEYWORD, "INPUT");
	public static final Token LET = new Token(TokenType.KEYWORD, "LET");
	public static final Token NEXT = new Token(TokenType.KEYWORD, "NEXT");
	public static final Token NEWINPUT = new Token(TokenType.KEYWORD, "NEWINPUT");
	public static final Token ON = new Token(TokenType.KEYWORD, "ON");
	public static final Token PRINT = new Token(TokenType.KEYWORD, "PRINT");
	public static final Token READ = new Token(TokenType.KEYWORD, "READ");
	public static final Token REM = new Token(TokenType.KEYWORD, "REM");
	public static final Token RESTORE = new Token(TokenType.KEYWORD, "RESTORE");
	public static final Token RETURN = new Token(TokenType.KEYWORD, "RETURN");
	public static final Token STEP = new Token(TokenType.KEYWORD, "STEP");
	public static final Token STOP = new Token(TokenType.KEYWORD, "STOP");
	public static final Token SWAP = new Token(TokenType.KEYWORD, "SWAP");
	public static final Token THEN = new Token(TokenType.KEYWORD, "THEN");
	public static final Token TO = new Token(TokenType.KEYWORD, "TO");
	public static final Token WEND = new Token(TokenType.KEYWORD, "WEND");
	public static final Token WHILE = new Token(TokenType.KEYWORD, "WHILE");

	public static final Token END_OF_INPUT = new Token(TokenType.END_OF_INPUT, "<END OF INPUT>");

	public static boolean isKeyword(String name) {
		return KEYWORDS.containsKey(name);
	}

	public static Token getKeywordToken(String name) {
		return KEYWORDS.get(name);
	}

	public static boolean isSpecialCharacter(String name) {
		return SPECIAL_CHARACTERS.containsKey(name);
	}

	public static Token getSpecialCharacterToken(String name) {
		return SPECIAL_CHARACTERS.get(name);
	}

	private final TokenType type;
	private final String chars;

	public Token(TokenType type, String chars) {
		this.type = type;
		this.chars = chars;

		if (type == TokenType.KEYWORD) {
			if (KEYWORDS == null) {
				KEYWORDS = new HashMap<String, Token>();
			}
			KEYWORDS.put(chars, this);
		}

		if (type == TokenType.SPECIAL_CHARACTER) {
			if (SPECIAL_CHARACTERS == null) {
				SPECIAL_CHARACTERS = new HashMap<String, Token>();
			}
			SPECIAL_CHARACTERS.put(chars, this);
		}
	}

	public TokenType getType() {
		return this.type;
	}

	public String getChars() {
		return this.chars;
	}
}
