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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LineNumberTable {
	private final Map<String /* line number */, Integer /* pos */> lineNumberMap = new HashMap<String, Integer>();

	public void add(int pos, String lineNumber) {
		if (this.lineNumberMap.containsKey(lineNumber)) {
			throw new CompileException("Duplicate line number \"" + lineNumber + "\".");
		}
		this.lineNumberMap.put(lineNumber, pos);
	}

	private final Map<Integer /* fromPos */, String /* toLineNumber */> patchHereMap = new HashMap<Integer, String>();

	private static class PatchInfo {
		private final int fromPos;
		private final String toLineNumber;

		public PatchInfo(int fromPos, String toLineNumber) {
			this.fromPos = fromPos;
			this.toLineNumber = toLineNumber;
		}

		public int getFromPos() {
			return this.fromPos;
		}

		public String getToLineNumber() {
			return this.toLineNumber;
		}
	}

	private final Map<Integer /* patchPos */, PatchInfo /* patchInfo */> patchThereMap = new HashMap<Integer, PatchInfo>();

	public void patchHere_u2(int fromPos, String toLineNumber) {
		this.patchHereMap.put(fromPos, toLineNumber);
	}

	public void patchThere_u4(int patchPos, int fromPos, String lineNumberTo) {
		this.patchThereMap.put(patchPos, new PatchInfo(fromPos, lineNumberTo));
	}

	public void flush(ByteOutStream o) {
		Set<Entry<Integer, String>> patchHereEntrySet = this.patchHereMap.entrySet();
		for (Entry<Integer, String> patchHereEntry : patchHereEntrySet) {
			int fromPos = patchHereEntry.getKey();
			String toLineNumber = patchHereEntry.getValue();

			if (this.lineNumberMap.containsKey(toLineNumber)) {
				int toPos = this.lineNumberMap.get(toLineNumber);
				o.patch_u2(fromPos, (toPos - fromPos) + 1);
			} else {
				throw new CompileException("Cannot find label \"" + toLineNumber + "\".");
			}
		}

		Set<Entry<Integer, PatchInfo>> patchThereEntrySet = this.patchThereMap.entrySet();
		for (Entry<Integer, PatchInfo> patchThereEntry : patchThereEntrySet) {
			int patchPos = patchThereEntry.getKey();
			int fromPos = patchThereEntry.getValue().getFromPos();
			String toLineNumber = patchThereEntry.getValue().getToLineNumber();

			if (this.lineNumberMap.containsKey(toLineNumber)) {
				int posTo = this.lineNumberMap.get(toLineNumber);
				o.patch_u4(patchPos, (posTo - fromPos) + 1);
			} else {
				throw new CompileException("Cannot find label \"" + toLineNumber + "\".");
			}
		}
	}
}