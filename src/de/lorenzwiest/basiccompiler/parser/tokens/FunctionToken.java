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

import de.lorenzwiest.basiccompiler.parser.nodes.NodeType;

public class FunctionToken extends Token {
	public static final FunctionToken ABS = new FunctionToken("ABS", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken ASC = new FunctionToken("ASC", NodeType.NUM, NodeType.STR);
	public static final FunctionToken ATN = new FunctionToken("ATN", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken CHR = new FunctionToken("CHR$", NodeType.STR, NodeType.NUM);
	public static final FunctionToken COS = new FunctionToken("COS", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken EXP = new FunctionToken("EXP", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken FIX = new FunctionToken("FIX", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken INSTR = new FunctionToken("INSTR", NodeType.NUM, NodeType.NUM, NodeType.STR, NodeType.STR);
	public static final FunctionToken INT = new FunctionToken("INT", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken LEFT = new FunctionToken("LEFT$", NodeType.STR, NodeType.STR, NodeType.NUM);
	public static final FunctionToken LEN = new FunctionToken("LEN", NodeType.NUM, NodeType.STR);
	public static final FunctionToken LOG = new FunctionToken("LOG", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken MID = new FunctionToken("MID$", NodeType.STR, NodeType.STR, NodeType.NUM, NodeType.NUM);
	public static final FunctionToken POS = new FunctionToken("POS", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken RIGHT = new FunctionToken("RIGHT$", NodeType.STR, NodeType.STR, NodeType.NUM);
	public static final FunctionToken RND = new FunctionToken("RND", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken SGN = new FunctionToken("SGN", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken SIN = new FunctionToken("SIN", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken SPACE = new FunctionToken("SPACE$", NodeType.STR, NodeType.NUM);
	public static final FunctionToken SPC = new FunctionToken("SPC", NodeType.VOID, NodeType.NUM);
	public static final FunctionToken SQR = new FunctionToken("SQR", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken STR = new FunctionToken("STR$", NodeType.STR, NodeType.NUM);
	public static final FunctionToken TAB = new FunctionToken("TAB", NodeType.VOID, NodeType.NUM);
	public static final FunctionToken TAN = new FunctionToken("TAN", NodeType.NUM, NodeType.NUM);
	public static final FunctionToken VAL = new FunctionToken("VAL", NodeType.NUM, NodeType.STR);

	private final NodeType returnType;

	private final NodeType[] argTypes;

	private FunctionToken(String token, NodeType returnType, NodeType... argTypes) {
		super(TokenType.KEYWORD, token);
		this.returnType = returnType;
		this.argTypes = argTypes;
	}

	public NodeType[] getArgTypes() {
		return this.argTypes;
	}

	public NodeType getReturnType() {
		return this.returnType;
	}
}
