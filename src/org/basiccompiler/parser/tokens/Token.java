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

package org.basiccompiler.parser.tokens;

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
