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

//TODO: rewrite as enum?

public class Token {
	public final static Token ADD = new Token("+");
	public final static Token SUBTRACT = new Token("-");
	public final static Token MULTIPLY = new Token("*");
	public final static Token DIVIDE = new Token("/");
	public final static Token UNARY_MINUS = new Token("-");
	public final static Token INT_DIVIDE = new Token("\\");
	public final static Token MOD = new Token("MOD");
	public final static Token POWER = new Token("^");

	public final static Token STRING_ADD = new Token("+");

	public final static Token AND = new Token("AND");
	public final static Token OR = new Token("OR");
	public final static Token XOR = new Token("XOR");
	public final static Token NOT = new Token("NOT");

	public final static Token LESS = new Token("<");
	public final static Token LESS_OR_EQUAL = new Token("<=");
	public final static Token EQUAL = new Token("=");
	public final static Token GREATER_OR_EQUAL = new Token(">=");
	public final static Token GREATER = new Token(">");
	public final static Token NOT_EQUAL = new Token("<>");

	public final static Token STRING_LESS = new Token("<");
	public final static Token STRING_LESS_OR_EQUAL = new Token("<=");
	public final static Token STRING_EQUAL = new Token("=");
	public final static Token STRING_GREATER_OR_EQUAL = new Token(">=");
	public final static Token STRING_GREATER = new Token(">");
	public final static Token STRING_NOT_EQUAL = new Token("<>");

	public final static Token OPEN = new Token("(");
	public final static Token CLOSE = new Token(")");

	public final static Token COMMA = new Token(",");
	public final static Token SEMICOLON = new Token(";");
	public final static Token COLON = new Token(":");

	public final static Token DATA = new Token("DATA");
	public final static Token DEF = new Token("DEF");
	public final static Token DIM = new Token("DIM");
	public final static Token ELSE = new Token("ELSE");
	public final static Token END = new Token("END");
	public final static Token FOR = new Token("FOR");
	public final static Token GOTO = new Token("GOTO");
	public final static Token GOSUB = new Token("GOSUB");
	public final static Token IF = new Token("IF");
	public final static Token INPUT = new Token("INPUT");
	public final static Token LET = new Token("LET");
	public final static Token NEXT = new Token("NEXT");
	public final static Token NEWINPUT = new Token("NEWINPUT");
	public final static Token ON = new Token("ON");
	public final static Token PRINT = new Token("PRINT");
	public final static Token READ = new Token("READ");
	public final static Token REM = new Token("REM");
	public final static Token RESTORE = new Token("RESTORE");
	public final static Token RETURN = new Token("RETURN");
	public final static Token STEP = new Token("STEP");
	public final static Token STOP = new Token("STOP");
	public final static Token SWAP = new Token("SWAP");
	public final static Token THEN = new Token("THEN");
	public final static Token TO = new Token("TO");
	public final static Token WEND = new Token("WEND");
	public final static Token WHILE = new Token("WHILE");

	private final String chars;

	protected Token(String chars) {
		this.chars = chars;
	}

	public String getChars() {
		return this.chars;
	}
}
