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