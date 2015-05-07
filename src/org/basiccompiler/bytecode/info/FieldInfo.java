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

package org.basiccompiler.bytecode.info;

import org.basiccompiler.compiler.etc.ByteOutStream;

public class FieldInfo {
	// u2 access_flags;
	// u2 name_index;
	// u2 descriptor_index;
	// u2 attributes_count;
	// attribute_info attributes[attributes_count];

	private final int accessFlags;     // u2
	private final int nameIndex;       // u2
	private final int descriptorIndex; // u2
	// private int attributeCount;     // u2
	private final Object[] attributes; // attribute_info[]

	public FieldInfo(int nameIndex, int descriptorIndex, int accessFlags) {
		this.accessFlags = accessFlags;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		// this.attributeCount calculated implicitly in write()
		this.attributes = new Object[0];
	}

	public void write(ByteOutStream o) {
		o.write_u2(this.accessFlags);
		o.write_u2(this.nameIndex);
		o.write_u2(this.descriptorIndex);
		o.write_u2(this.attributes.length);
		for (int i = 0; i < this.attributes.length; i++) {
			// not implemented, not needed yet
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FieldInfo) {
			FieldInfo toCompare = (FieldInfo) obj;
			if ((this.nameIndex == toCompare.nameIndex) && (this.descriptorIndex == toCompare.descriptorIndex)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode += 37 * this.nameIndex;
		hashCode += 37 * this.descriptorIndex;
		return hashCode;
	}
}