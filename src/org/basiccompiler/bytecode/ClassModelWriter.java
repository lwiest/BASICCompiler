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

package org.basiccompiler.bytecode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_Class;
import org.basiccompiler.bytecode.info.AttributeInfo;
import org.basiccompiler.bytecode.info.FieldInfo;
import org.basiccompiler.bytecode.info.InterfaceInfo;
import org.basiccompiler.bytecode.info.MethodInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;

public class ClassModelWriter {
	private final static int MAGIC_NUMBER = 0xCAFEBABE;
	private final static int MAJOR_VERSION = 45; // equivalent to JDK 1.0.2
	private final static int MINOR_VERSION = 3;

	private final String className;
	private final String superClassName;
	private final ConstantPool constantPool;
	private final List<InterfaceInfo> interfaces;
	private final List<FieldInfo> fields;
	private final List<MethodInfo> methods;
	private final List<AttributeInfo> attributes;

	public ClassModelWriter(String className, String superClassName, ConstantPool constantPool, List<InterfaceInfo> interfaces, List<FieldInfo> fields, List<MethodInfo> methods, List<AttributeInfo> attributes) {
		this.className = className;
		this.superClassName = superClassName;
		this.constantPool = constantPool;
		this.interfaces = interfaces;
		this.fields = fields;
		this.methods = methods;
		this.attributes = attributes;
	}

	public void write(OutputStream outStream) throws IOException {
		ByteOutStream o = new ByteOutStream();

		writeMagicNumber(o, MAGIC_NUMBER);
		writeMinorVersion(o, MINOR_VERSION);
		writeMajorVersion(o, MAJOR_VERSION);
		writeConstantPoolCount(o, this.constantPool);
		writeConstantPool(o, this.constantPool);
		writeAccessFlags(o, ClassModel.ACC_PUBLIC | ClassModel.ACC_SUPER);
		writeThisClass(o, this.constantPool, this.className);
		writeSuperClass(o, this.constantPool, this.superClassName);
		writeInterfacesCount(o, this.interfaces);
		writeInterfaces(o, this.interfaces);
		writeFieldsCount(o, this.fields);
		writeFields(o, this.fields);
		writeMethodsCount(o, this.methods);
		writeMethods(o, this.methods);
		writeAttributesCount(o, this.attributes);
		writeAttributes(o, this.attributes);

		o.flushAndCloseGracefully();
		outStream.write(o.toByteArray());
	}

	private void writeMagicNumber(ByteOutStream o, int magicNumber) {
		o.write_u4(magicNumber);
	}

	private void writeMinorVersion(ByteOutStream o, int minorVersion) {
		o.write_u2(minorVersion);
	}

	private void writeMajorVersion(ByteOutStream o, int majorVersion) {
		o.write_u2(majorVersion);
	}

	private void writeConstantPoolCount(ByteOutStream o, ConstantPool constantPool) {
		o.write_u2(constantPool.size() + 1); // NOTE: serialized constant pool indexes are 1-based
	}

	private void writeConstantPool(ByteOutStream o, ConstantPool constantPool) {
		ConstantPoolInfo[] constantPoolInfos = constantPool.getConstantPoolInfos();
		for (ConstantPoolInfo constantPoolInfo : constantPoolInfos) {
			constantPoolInfo.write(o);
		}
	}

	private void writeAccessFlags(ByteOutStream o, int accessFlags) {
		o.write_u2(accessFlags);
	}

	private void writeThisClass(ByteOutStream o, ConstantPool constantPool, String className) {
		int classIndex = ConstantPoolInfo_Class.getIndex(constantPool, className);
		o.write_u2(classIndex + 1); // NOTE: serialized constant pool indexes are 1-based
	}

	private void writeSuperClass(ByteOutStream o, ConstantPool constantPool, String superClassName) {
		int superClassIndex = ConstantPoolInfo_Class.getIndex(constantPool, superClassName);
		o.write_u2(superClassIndex + 1); // NOTE: serialized constant pool indexes are 1-based
	}

	private void writeInterfacesCount(ByteOutStream o, List<InterfaceInfo> interfaceInfos) {
		o.write_u2(interfaceInfos.size());
	}

	private void writeInterfaces(ByteOutStream o, List<InterfaceInfo> interfaceInfos) {
		for (InterfaceInfo info : interfaceInfos) {
			// not implemented
		}
	}

	private void writeFieldsCount(ByteOutStream o, List<FieldInfo> fieldInfos) {
		o.write_u2(fieldInfos.size());
	}

	private void writeFields(ByteOutStream o, List<FieldInfo> fieldInfos) {
		for (FieldInfo info : fieldInfos) {
			info.write(o);
		}
	}

	private void writeMethodsCount(ByteOutStream o, List<MethodInfo> methodInfos) {
		o.write_u2(methodInfos.size());
	}

	private void writeMethods(ByteOutStream o, List<MethodInfo> methodInfos) {
		for (MethodInfo info : methodInfos) {
			info.write(o);
		}
	}

	private void writeAttributesCount(ByteOutStream o, List<AttributeInfo> attributeInfos) {
		o.write_u2(attributeInfos.size());
	}

	private void writeAttributes(ByteOutStream o, List<AttributeInfo> attributeInfos) {
		for (AttributeInfo info : attributeInfos) {
			// not implemented
		}
	}
}
