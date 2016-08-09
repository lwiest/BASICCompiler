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

package de.lorenzwiest.basiccompiler.bytecode.info;

import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

public class ExceptionTableInfo {
	// u2 start_pc;
	// u2 end_pc;
	// u2 handler_pc;
	// u2 catch_type;

	private final int start_pc;   // u2
	private final int end_pc;     // u2
	private final int handler_pc; // u2
	private final int catch_type; // u2

	public ExceptionTableInfo(int start_pc, int end_pc, int handler_pc, int catch_type) {
		this.start_pc = start_pc;
		this.end_pc = end_pc;
		this.handler_pc = handler_pc;
		this.catch_type = catch_type;
	}

	public void write(ByteOutStream o) {
		o.write_u2(this.start_pc);
		o.write_u2(this.end_pc);
		o.write_u2(this.handler_pc);
		o.write_u2(this.catch_type);
	}
}
