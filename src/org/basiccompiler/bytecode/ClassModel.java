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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_Class;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_FieldRef;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_Float;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_MethodRef;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_NameAndType;
import org.basiccompiler.bytecode.constantpoolinfo.impl.ConstantPoolInfo_String;
import org.basiccompiler.bytecode.info.AttributeInfo;
import org.basiccompiler.bytecode.info.CodeAttributeInfo;
import org.basiccompiler.bytecode.info.ExceptionTableInfo;
import org.basiccompiler.bytecode.info.FieldInfo;
import org.basiccompiler.bytecode.info.InterfaceInfo;
import org.basiccompiler.bytecode.info.MethodInfo;
import org.basiccompiler.compiler.etc.ByteOutStream;

public class ClassModel {
	public static final int ACC_PUBLIC = 0x0001;
	public static final int ACC_STATIC = 0x0008;
	public static final int ACC_SUPER = 0x0020;

	public static final int MAX_METHOD_LENGTH = 65536;

	private final static String SUPER_CLASS_NAME = "java/lang/Object";

	private static final String MAIN_METHOD_NAME = "main";
	private static final String MAIN_METHOD_DESCRIPTOR = "([Ljava/lang/String;)V";

	private static final String CONSTRUCTOR_METHOD_NAME = "<init>";
	private static final String CONSTRUCTOR_METHOD_DESCRIPTOR = "()V";

	private final String className;
	private final List<ConstantPoolInfo> constantPool = new ArrayList<ConstantPoolInfo>();
	private final List<InterfaceInfo> interfaces = new ArrayList<InterfaceInfo>();
	private final List<FieldInfo> fields = new ArrayList<FieldInfo>();
	private final List<MethodInfo> methods = new ArrayList<MethodInfo>();
	private final List<AttributeInfo> attributes = new ArrayList<AttributeInfo>();

	public ClassModel(String className) {
		this.className = className;
		init();
	}

	public String getClassName() {
		return this.className;
	}

	public void addMainMethod(int numLocals, byte[] bytecode, ExceptionTableInfo[] exceptionTable) {
		addMethod(MAIN_METHOD_NAME, MAIN_METHOD_DESCRIPTOR, numLocals + 1 /* String args[] */, bytecode, exceptionTable);
	}

	public void addMethod(String methodName, String descriptor, int numLocals, byte[] bytecode) {
		addMethod(methodName, descriptor, numLocals, bytecode, new ExceptionTableInfo[0]);
	}

	public void addMethod(String methodName, String descriptor, int numLocals, byte[] bytecode, ExceptionTableInfo[] exceptionTable) {
		int methodRefIndex = ConstantPoolInfo_MethodRef.addAndReturnIndex(this.constantPool, this.className, methodName, descriptor);
		ConstantPoolInfo_MethodRef methodRef = (ConstantPoolInfo_MethodRef) this.constantPool.get(methodRefIndex);
		ConstantPoolInfo_NameAndType nameAndTypeRef = (ConstantPoolInfo_NameAndType) this.constantPool.get(methodRef.getNameAndTypeIndex());
		int nameIndex = nameAndTypeRef.getNameIndex();
		int descriptorIndex = nameAndTypeRef.getDescriptorIndex();
		int maxLocals = numLocals + 0; // NOTE: static methods have no "this" field => offset 0!
		CodeAttributeInfo codeAttributeInfo = new CodeAttributeInfo(this.constantPool, maxLocals, bytecode, exceptionTable);
		MethodInfo methodInfo = new MethodInfo(nameIndex, descriptorIndex, ACC_PUBLIC | ACC_STATIC, codeAttributeInfo);
		this.methods.add(methodInfo);
	}

	public void addField(String fieldName, String descriptor) {
		FieldInfo fieldInfo = createFieldInfo(fieldName, descriptor);
		this.fields.add(fieldInfo);
	}

	private FieldInfo createFieldInfo(String fieldName, String descriptor) {
		int fieldRefIndex = ConstantPoolInfo_FieldRef.addAndReturnIndex(this.constantPool, this.className, fieldName, descriptor);
		ConstantPoolInfo_FieldRef fieldRef = (ConstantPoolInfo_FieldRef) this.constantPool.get(fieldRefIndex);
		ConstantPoolInfo_NameAndType nameAndTypeRef = (ConstantPoolInfo_NameAndType) this.constantPool.get(fieldRef.getNameAndTypeIndex());
		int nameIndex = nameAndTypeRef.getNameIndex();
		int descriptorIndex = nameAndTypeRef.getDescriptorIndex();
		FieldInfo fieldInfo = new FieldInfo(nameIndex, descriptorIndex, ACC_PUBLIC | ACC_STATIC);
		return fieldInfo;
	}

	public int addFieldAndGetFieldRefIndex(String fieldName, String descriptor) {
		FieldInfo fieldInfo = createFieldInfo(fieldName, descriptor);
		if (this.fields.contains(fieldInfo) == false) {
			addField(fieldName, descriptor);
		}
		return getFieldRefIndex(fieldName, descriptor);
	}

	public int getFieldRefIndex(String fieldName, String descriptor) {
		int fieldRefIndex = ConstantPoolInfo_FieldRef.addAndReturnIndex(this.constantPool, this.className, fieldName, descriptor);
		return fieldRefIndex + 1; // NOTE: serialized constant pool indexes are 1-based
	}

	public int getMethodRefIndex(String methodName, String descriptor) {
		int methodRefIndex = ConstantPoolInfo_MethodRef.addAndReturnIndex(this.constantPool, this.className, methodName, descriptor);
		return methodRefIndex + 1; // NOTE: serialized constant pool indexes are 1-based
	}

	public int getStringIndex(String string) {
		int stringConstIndex = ConstantPoolInfo_String.addAndReturnIndex(this.constantPool, string);
		return stringConstIndex + 1; // NOTE: serialized constant pool indexes are 1-based
	}

	public int getFloatIndex(float aFloat) {
		int floatConstIndex = ConstantPoolInfo_Float.addAndReturnIndex(this.constantPool, aFloat);
		return floatConstIndex + 1; // NOTE: serialized constant pool indexes are 1-based
	}

	public int getFloatIndexOfNaN() {
		return getFloatIndex(Float.NaN);
	}

	public int getClassIndex(String className) {
		int classIndex = ConstantPoolInfo_Class.addAndReturnIndex(this.constantPool, className);
		return classIndex + 1; // NOTE: serialized constant pool indexes are 1-based
	}

	public void write(OutputStream outStream) throws IOException {
		new ClassModelWriter(this.className, SUPER_CLASS_NAME, this.constantPool, this.interfaces, this.fields, this.methods, this.attributes).write(outStream);
	}

	private void init() {
		// fill class structure

		ConstantPoolInfo_Class.addAndReturnIndex(this.constantPool, SUPER_CLASS_NAME);
		ConstantPoolInfo_Class.addAndReturnIndex(this.constantPool, this.className);

		// add constructor

		final int CONSTRUCTOR_ACCESS_FLAGS = ACC_PUBLIC;

		int methodConstructorIndex = ConstantPoolInfo_MethodRef.addAndReturnIndex(this.constantPool, SUPER_CLASS_NAME, CONSTRUCTOR_METHOD_NAME, CONSTRUCTOR_METHOD_DESCRIPTOR);
		ConstantPoolInfo_MethodRef constructorMethodRef = (ConstantPoolInfo_MethodRef) this.constantPool.get(methodConstructorIndex);
		ConstantPoolInfo_NameAndType constructorNameAndTypeRef = (ConstantPoolInfo_NameAndType) this.constantPool.get(constructorMethodRef.getNameAndTypeIndex());
		int constructorNameIndex = constructorNameAndTypeRef.getNameIndex();
		int constructorDescriptorIndex = constructorNameAndTypeRef.getDescriptorIndex();

		ByteOutStream o = new ByteOutStream();

		o.aload_0();
		o.invokespecial(methodConstructorIndex + 1); // NOTE: serialized constant pool indexes are 1-based
		o.return_();

		o.flush();
		byte[] constructorByteCode = o.toByteArray();
		try {
			o.close();
		} catch (IOException e) {
			// ignore
		}
		CodeAttributeInfo constructorCodeAttributeInfo = new CodeAttributeInfo(this.constantPool, 1, constructorByteCode, new ExceptionTableInfo[0]);

		MethodInfo methodConstructorInfo = new MethodInfo(constructorNameIndex, constructorDescriptorIndex, CONSTRUCTOR_ACCESS_FLAGS, constructorCodeAttributeInfo);
		this.methods.add(methodConstructorInfo);
	}

	//////////////////////////////////////////////////////////////////////////////

	public enum JavaClass {
		RUNTIME_EXCEPTION("java/lang/RuntimeException");

		private String fullClassName;

		JavaClass(String fullClassName) {
			this.fullClassName = fullClassName;
		}

		public String getFullClassName() {
			return this.fullClassName;
		}
	}

	private final Map<JavaClass, Integer> JAVA_CLASS_MAP = new HashMap<JavaClass, Integer>();

	public int getJavaClassRefIndex(JavaClass javaClass) {
		if (this.JAVA_CLASS_MAP.containsKey(javaClass) == false) {
			String fullClassName = javaClass.getFullClassName();
			this.JAVA_CLASS_MAP.put(javaClass, ConstantPoolInfo_Class.addAndReturnIndex(this.constantPool, fullClassName));
		}
		return this.JAVA_CLASS_MAP.get(javaClass) + 1; // NOTE: serialized constant pool indexes are 1-based
	}

	//////////////////////////////////////////////////////////////////////////////

	public enum JavaField {
		SYSTEM_OUT("java/lang/System", "out", "Ljava/io/PrintStream;"),
		SYSTEM_IN("java/lang/System", "in", "Ljava/io/InputStream;");

		private String fullClassName;
		private String fieldName;
		private String descriptor;

		JavaField(String fullClassName, String fieldName, String descriptor) {
			this.fullClassName = fullClassName;
			this.fieldName = fieldName;
			this.descriptor = descriptor;
		}

		public String getFullClassName() {
			return this.fullClassName;
		}

		public String getFieldName() {
			return this.fieldName;
		}

		public String getDescriptor() {
			return this.descriptor;
		}
	}

	private final Map<JavaField, Integer> JAVA_FIELD_MAP = new HashMap<JavaField, Integer>();

	public int getJavaFieldRefIndex(JavaField javaField) {
		if (this.JAVA_FIELD_MAP.containsKey(javaField) == false) {
			String fullClassName = javaField.getFullClassName();
			String fieldName = javaField.getFieldName();
			String descriptor = javaField.getDescriptor();
			int fieldRefIndex = ConstantPoolInfo_FieldRef.addAndReturnIndex(this.constantPool, fullClassName, fieldName, descriptor);
			this.JAVA_FIELD_MAP.put(javaField, fieldRefIndex);
		}
		return this.JAVA_FIELD_MAP.get(javaField) + 1; // NOTE: serialized constant pool indexes are 1-based
	}

	//////////////////////////////////////////////////////////////////////////////

	public enum JavaMethod {
		STRING_TO_CHAR_ARRAY("java/lang/String", "toCharArray", "()[C"),

		PRINT_STREAM_PRINT("java/io/PrintStream", "print", "(C)V"),
		INPUT_STREAM_READ("java/io/InputStream", "read", "([B)I"),

		MATH_ATAN("java/lang/Math", "atan", "(D)D"),
		MATH_CEIL("java/lang/Math", "ceil", "(D)D"),
		MATH_COS("java/lang/Math", "cos", "(D)D"),
		MATH_EXP("java/lang/Math", "exp", "(D)D"),
		MATH_FLOOR("java/lang/Math", "floor", "(D)D"),
		MATH_LOG("java/lang/Math", "log", "(D)D"),
		MATH_LOG10("java/lang/Math", "log10", "(D)D"),
		MATH_POW("java/lang/Math", "pow", "(DD)D"),
		MATH_RANDOM("java/lang/Math", "random", "()D"),
		MATH_SIN("java/lang/Math", "sin", "(D)D"),
		MATH_SQRT("java/lang/Math", "sqrt", "(D)D"),

		RUNTIME_EXCEPTION_INIT("java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V"),
		EXCEPTION_GET_MESSAGE("java/lang/Exception", "getMessage", "()Ljava/lang/String;");

		private String fullClassName;
		private String methodName;
		private String descriptor;

		JavaMethod(String fullClassName, String methodName, String descriptor) {
			this.fullClassName = fullClassName;
			this.methodName = methodName;
			this.descriptor = descriptor;
		}

		public String getFullClassName() {
			return this.fullClassName;
		}

		public String getMethodName() {
			return this.methodName;
		}

		public String getDescriptor() {
			return this.descriptor;
		}
	}

	private final Map<JavaMethod, Integer> JAVA_METHOD_MAP = new HashMap<JavaMethod, Integer>();

	public int getJavaMethodRefIndex(JavaMethod javaMethod) {
		if (this.JAVA_METHOD_MAP.containsKey(javaMethod) == false) {
			String fullClassName = javaMethod.getFullClassName();
			String methodName = javaMethod.getMethodName();
			String descriptor = javaMethod.getDescriptor();
			int methodRefIndex = ConstantPoolInfo_MethodRef.addAndReturnIndex(this.constantPool, fullClassName, methodName, descriptor);
			this.JAVA_METHOD_MAP.put(javaMethod, methodRefIndex);
		}
		return this.JAVA_METHOD_MAP.get(javaMethod) + 1; // NOTE: serialized constant pool indexes are 1-based
	}
}
