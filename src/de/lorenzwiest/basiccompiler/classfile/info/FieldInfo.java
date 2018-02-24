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

package de.lorenzwiest.basiccompiler.classfile.info;

import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

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