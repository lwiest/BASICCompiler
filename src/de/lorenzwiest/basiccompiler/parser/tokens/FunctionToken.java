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

package de.lorenzwiest.basiccompiler.parser.tokens;

import de.lorenzwiest.basiccompiler.parser.nodes.NodeType;

// TODO: rewrite as enum? But cannot extend enum from Token...

public class FunctionToken extends Token {
	public final static FunctionToken ABS = new FunctionToken("ABS(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken ASC = new FunctionToken("ASC(", NodeType.NUM, NodeType.STR);
	public final static FunctionToken ATN = new FunctionToken("ATN(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken CHR = new FunctionToken("CHR$(", NodeType.STR, NodeType.NUM);
	public final static FunctionToken COS = new FunctionToken("COS(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken EXP = new FunctionToken("EXP(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken FIX = new FunctionToken("FIX(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken INSTR = new FunctionToken("INSTR(", NodeType.NUM, NodeType.NUM, NodeType.STR, NodeType.STR);
	public final static FunctionToken INT = new FunctionToken("INT(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken LEFT = new FunctionToken("LEFT$(", NodeType.STR, NodeType.STR, NodeType.NUM);
	public final static FunctionToken LEN = new FunctionToken("LEN(", NodeType.NUM, NodeType.STR);
	public final static FunctionToken LOG = new FunctionToken("LOG(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken MID = new FunctionToken("MID$(", NodeType.STR, NodeType.STR, NodeType.NUM, NodeType.NUM);
	public final static FunctionToken POS = new FunctionToken("POS(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken RIGHT = new FunctionToken("RIGHT$(", NodeType.STR, NodeType.STR, NodeType.NUM);
	public final static FunctionToken RND = new FunctionToken("RND(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken SGN = new FunctionToken("SGN(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken SIN = new FunctionToken("SIN(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken SPACE = new FunctionToken("SPACE$(", NodeType.STR, NodeType.NUM);
	public final static FunctionToken SPC = new FunctionToken("SPC(", NodeType.VOID, NodeType.NUM);
	public final static FunctionToken SQR = new FunctionToken("SQR(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken STR = new FunctionToken("STR$(", NodeType.STR, NodeType.NUM);
	public final static FunctionToken TAB = new FunctionToken("TAB(", NodeType.VOID, NodeType.NUM);
	public final static FunctionToken TAN = new FunctionToken("TAN(", NodeType.NUM, NodeType.NUM);
	public final static FunctionToken VAL = new FunctionToken("VAL(", NodeType.NUM, NodeType.STR);

	private final NodeType returnType;

	private final NodeType[] argTypes;

	private FunctionToken(String token, NodeType returnType, NodeType... argTypes) {
		super(token);
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
