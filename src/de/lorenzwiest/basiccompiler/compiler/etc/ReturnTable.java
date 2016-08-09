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

package de.lorenzwiest.basiccompiler.compiler.etc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnTable {
	private final Map<Integer, Integer> mapToReturn = new HashMap<Integer, Integer>();
	private final List<Integer> toTableSwitch = new ArrayList<Integer>();

	public boolean isUsed() {
		boolean isMapUsed = (this.mapToReturn.size() > 0);
		boolean isListUsed = (this.toTableSwitch.size() > 0);
		return isMapUsed || isListUsed;
	}

	public int nextIndex() {
		return this.mapToReturn.size();
	}

	public void addReturnPos(int index, int returnPos) {
		this.mapToReturn.put(index, returnPos);
	}

	public void patchToTableSwitch(int pos) {
		this.toTableSwitch.add(pos);
	}

	public void flush(ByteOutStream o) {
		if (this.mapToReturn.isEmpty()) {
			return;
		}

		int posBeforeTableSwitch = o.pos();
		o.tableswitch();
		int posAfterTableSwitch = o.pos();

		o.pad4ByteBoundary();
		o.write_u4(branchOffset(posAfterTableSwitch, posBeforeTableSwitch - 1));
		o.write_u4(0x00000000); // min
		o.write_u4(this.mapToReturn.size() - 1); // max

		for (int i = 0; i < this.mapToReturn.size(); i++) {
			int posToReturn = this.mapToReturn.get(i);
			o.write_u4(branchOffset(posAfterTableSwitch, posToReturn));
		}

		// patch emitReturn()'s goto <tableswitch> occurrences
		for (int pos : this.toTableSwitch) {
			o.patch_u2(pos, branchOffset(pos, posBeforeTableSwitch));
		}
	}

	private static int branchOffset(int from, int to) {
		return (to - from) + 1;
	}
}
