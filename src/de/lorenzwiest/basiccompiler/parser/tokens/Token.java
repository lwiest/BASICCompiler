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

	public final static Token ADD = new Token(TokenType.SPECIAL_CHARACTER, "+");
	public final static Token SUBTRACT = new Token(TokenType.SPECIAL_CHARACTER, "-");
	public final static Token MULTIPLY = new Token(TokenType.SPECIAL_CHARACTER, "*");
	public final static Token DIVIDE = new Token(TokenType.SPECIAL_CHARACTER, "/");
	public final static Token INT_DIVIDE = new Token(TokenType.SPECIAL_CHARACTER, "\\");
	public final static Token MOD = new Token(TokenType.KEYWORD, "MOD");
	public final static Token POWER = new Token(TokenType.SPECIAL_CHARACTER, "^");

	public final static Token AND = new Token(TokenType.KEYWORD, "AND");
	public final static Token OR = new Token(TokenType.KEYWORD, "OR");
	public final static Token XOR = new Token(TokenType.KEYWORD, "XOR");
	public final static Token NOT = new Token(TokenType.KEYWORD, "NOT");

	public final static Token LESS = new Token(TokenType.SPECIAL_CHARACTER, "<");
	public final static Token LESS_OR_EQUAL = new Token(TokenType.SPECIAL_CHARACTER, "<=");
	public final static Token EQUAL = new Token(TokenType.SPECIAL_CHARACTER, "=");
	public final static Token GREATER_OR_EQUAL = new Token(TokenType.SPECIAL_CHARACTER, ">=");
	public final static Token GREATER = new Token(TokenType.SPECIAL_CHARACTER, ">");
	public final static Token NOT_EQUAL = new Token(TokenType.SPECIAL_CHARACTER, "<>");

	public final static Token OPEN = new Token(TokenType.SPECIAL_CHARACTER, "(");
	public final static Token CLOSE = new Token(TokenType.SPECIAL_CHARACTER, ")");
	public final static Token COMMA = new Token(TokenType.SPECIAL_CHARACTER, ",");
	public final static Token SEMICOLON = new Token(TokenType.SPECIAL_CHARACTER, ";");
	public final static Token COLON = new Token(TokenType.SPECIAL_CHARACTER, ":");
	public final static Token DOLLAR_SIGN = new Token(TokenType.SPECIAL_CHARACTER, "$");

	public final static Token DATA = new Token(TokenType.KEYWORD, "DATA");
	public final static Token DEF = new Token(TokenType.KEYWORD, "DEF");
	public final static Token DIM = new Token(TokenType.KEYWORD, "DIM");
	public final static Token ELSE = new Token(TokenType.KEYWORD, "ELSE");
	public final static Token END = new Token(TokenType.KEYWORD, "END");
	public final static Token FOR = new Token(TokenType.KEYWORD, "FOR");
	public final static Token GOTO = new Token(TokenType.KEYWORD, "GOTO");
	public final static Token GOSUB = new Token(TokenType.KEYWORD, "GOSUB");
	public final static Token IF = new Token(TokenType.KEYWORD, "IF");
	public final static Token INPUT = new Token(TokenType.KEYWORD, "INPUT");
	public final static Token LET = new Token(TokenType.KEYWORD, "LET");
	public final static Token NEXT = new Token(TokenType.KEYWORD, "NEXT");
	public final static Token NEWINPUT = new Token(TokenType.KEYWORD, "NEWINPUT");
	public final static Token ON = new Token(TokenType.KEYWORD, "ON");
	public final static Token PRINT = new Token(TokenType.KEYWORD, "PRINT");
	public final static Token READ = new Token(TokenType.KEYWORD, "READ");
	public final static Token REM = new Token(TokenType.KEYWORD, "REM");
	public final static Token RESTORE = new Token(TokenType.KEYWORD, "RESTORE");
	public final static Token RETURN = new Token(TokenType.KEYWORD, "RETURN");
	public final static Token STEP = new Token(TokenType.KEYWORD, "STEP");
	public final static Token STOP = new Token(TokenType.KEYWORD, "STOP");
	public final static Token SWAP = new Token(TokenType.KEYWORD, "SWAP");
	public final static Token THEN = new Token(TokenType.KEYWORD, "THEN");
	public final static Token TO = new Token(TokenType.KEYWORD, "TO");
	public final static Token WEND = new Token(TokenType.KEYWORD, "WEND");
	public final static Token WHILE = new Token(TokenType.KEYWORD, "WHILE");

	public final static Token END_OF_INPUT = new Token(TokenType.END_OF_INPUT, "<END OF INPUT>");

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
