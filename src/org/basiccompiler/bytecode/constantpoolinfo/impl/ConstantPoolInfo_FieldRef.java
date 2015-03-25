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

public class ConstantPoolInfo_FieldRef extends ConstantPoolInfo {
	private final int classIndex;       // u2
	private final int nameAndTypeIndex; // u2

	public ConstantPoolInfo_FieldRef(int classIndex, int nameAndTypeIndex) {
		super(TAG_FIELDREF);
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public int getClassIndex() {
		return this.classIndex;
	}

	public int getNameAndTypeIndex() {
		return this.nameAndTypeIndex;
	}

	@Override
	public void write(ByteOutStream o) {
		super.write(o);
		o.write_u2(this.classIndex + 1); // NOTE: serialized constant pool indexes are 1-based
		o.write_u2(this.nameAndTypeIndex + 1); // NOTE: serialized constant pool indexes are 1-based
	}

	public static int addAndReturnIndex(List<ConstantPoolInfo> constantPool, String className, String fieldName, String descriptor) {
		if (contains(constantPool, className, fieldName, descriptor) == false) {
			add(constantPool, className, fieldName, descriptor);
		}
		return getIndex(constantPool, className, fieldName, descriptor);
	}

	private static boolean contains(List<ConstantPoolInfo> constantPool, String className, String fieldName, String descriptor) {
		return getIndex(constantPool, className, fieldName, descriptor) > -1;
	}

	private static void add(List<ConstantPoolInfo> constantPool, String className, String fieldName, String descriptor) {
		int classIndex = ConstantPoolInfo_Class.addAndReturnIndex(constantPool, className);
		int nameAndTypeIndex = ConstantPoolInfo_NameAndType.addAndReturnIndex(constantPool, fieldName, descriptor);
		constantPool.add(new ConstantPoolInfo_FieldRef(classIndex, nameAndTypeIndex));
	}

	private static int getIndex(List<ConstantPoolInfo> constantPool, String className, String fieldName, String descriptor) {
		for (int i = 0; i < constantPool.size(); i++) {  // TODO: implement faster lookup, although speed not an issue yet
			ConstantPoolInfo info = constantPool.get(i);
			if (info.getTag() == TAG_FIELDREF) {
				ConstantPoolInfo_FieldRef infoField = (ConstantPoolInfo_FieldRef) info;

				int classIndex = infoField.getClassIndex();
				ConstantPoolInfo_Class classInfo = (ConstantPoolInfo_Class) constantPool.get(classIndex);
				int classNameIndex = classInfo.getNameIndex();
				String classNameToCompare = ConstantPoolInfo_Utf8.getString(constantPool, classNameIndex);
				boolean isSameClass = className.equals(classNameToCompare);

				int nameAndTypeIndex = infoField.getNameAndTypeIndex();
				ConstantPoolInfo_NameAndType nameAndTypeInfo = (ConstantPoolInfo_NameAndType) constantPool.get(nameAndTypeIndex);
				int nameIndex = nameAndTypeInfo.getNameIndex();
				String fieldNameToCompare = ConstantPoolInfo_Utf8.getString(constantPool, nameIndex);
				boolean isSameFieldName = fieldName.equals(fieldNameToCompare);

				int descriptorIndex = nameAndTypeInfo.getDescriptorIndex();
				String descriptorToCompare = ConstantPoolInfo_Utf8.getString(constantPool, descriptorIndex);
				boolean isSameDescriptor = descriptor.equals(descriptorToCompare);

				if (isSameClass && isSameFieldName && isSameDescriptor) {
					return i;
				}
			}
		}
		return -1;
	}
}