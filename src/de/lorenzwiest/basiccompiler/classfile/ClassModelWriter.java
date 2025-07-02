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

package de.lorenzwiest.basiccompiler.classfile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.ConstantPoolInfo;
import de.lorenzwiest.basiccompiler.classfile.constantpoolinfo.impl.ConstantPoolInfo_Class;
import de.lorenzwiest.basiccompiler.classfile.info.AttributeInfo;
import de.lorenzwiest.basiccompiler.classfile.info.FieldInfo;
import de.lorenzwiest.basiccompiler.classfile.info.InterfaceInfo;
import de.lorenzwiest.basiccompiler.classfile.info.MethodInfo;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;

public class ClassModelWriter {
	private static final int MAGIC_NUMBER = 0xCAFEBABE;
	private static final int MAJOR_VERSION = 49; // equivalent to JDK 1.5 - we use Math.log10() introduced with this JDK
	private static final int MINOR_VERSION = 0;

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
		o.write_u2(constantPool.getCount());
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
		o.write_u2(classIndex);
	}

	private void writeSuperClass(ByteOutStream o, ConstantPool constantPool, String superClassName) {
		int superClassIndex = ConstantPoolInfo_Class.getIndex(constantPool, superClassName);
		o.write_u2(superClassIndex);
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
