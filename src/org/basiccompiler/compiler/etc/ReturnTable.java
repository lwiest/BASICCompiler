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

package org.basiccompiler.compiler.etc;

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
