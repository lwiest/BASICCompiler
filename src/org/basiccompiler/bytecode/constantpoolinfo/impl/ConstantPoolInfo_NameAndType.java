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

package org.basiccompiler.bytecode.constantpoolinfo.impl;

import java.util.List;

import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;

public class ConstantPoolInfo_NameAndType extends ConstantPoolInfo {
	private final int nameIndex;       // u2
	private final int descriptorIndex; // u2

	public ConstantPoolInfo_NameAndType(int nameIndex, int descriptorIndex) {
		super(TAG_NAME_AND_TYPE);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public int getNameIndex() {
		return this.nameIndex;
	}

	public int getDescriptorIndex() {
		return this.descriptorIndex;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);
		o.write_u2(this.nameIndex + 1); // NOTE: serialized constant pool indexes are 1-based
		o.write_u2(this.descriptorIndex + 1); // NOTE: serialized constant pool indexes are 1-based
	}

	public static int addAndReturnIndex(List<ConstantPoolInfo> constantPool, String name, String descriptor) {
		if (contains(constantPool, name, descriptor) == false) {
			add(constantPool, name, descriptor);
		}
		return getIndex(constantPool, name, descriptor);
	}

	private static boolean contains(List<ConstantPoolInfo> constantPool, String name, String descriptor) {
		return getIndex(constantPool, name, descriptor) > -1;
	}

	private static void add(List<ConstantPoolInfo> constantPool, String name, String descriptor) {
		int nameIndex = ConstantPoolInfo_Utf8.addAndReturnIndex(constantPool, name);
		int descriptorIndex = ConstantPoolInfo_Utf8.addAndReturnIndex(constantPool, descriptor);
		constantPool.add(new ConstantPoolInfo_NameAndType(nameIndex, descriptorIndex));
	}

	private static int getIndex(List<ConstantPoolInfo> constantPool, String name, String descriptor) {
		for (int i = 0; i < constantPool.size(); i++) {  // TODO: implement faster lookup, although speed not an issue yet
			ConstantPoolInfo info = constantPool.get(i);
			if (info.getTag() == TAG_NAME_AND_TYPE) {
				ConstantPoolInfo_NameAndType infoNameAndType = (ConstantPoolInfo_NameAndType) info;

				int nameIndex = infoNameAndType.getNameIndex();
				String nameToCompare = ConstantPoolInfo_Utf8.getString(constantPool, nameIndex);
				boolean isSameName = name.equals(nameToCompare);

				int descriptorIndex = infoNameAndType.getDescriptorIndex();
				String descriptorToCompare = ConstantPoolInfo_Utf8.getString(constantPool, descriptorIndex);
				boolean isSameDescriptor = descriptor.equals(descriptorToCompare);

				if (isSameName && isSameDescriptor) {
					return i;
				}
			}
		}
		return -1;
	}
}
