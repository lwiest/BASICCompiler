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

package de.lorenzwiest.basiccompiler.compiler.library.methods.functions;

import static de.lorenzwiest.basiccompiler.bytecode.ClassModel.JavaMethod.MATH_RANDOM;

import java.util.List;

import de.lorenzwiest.basiccompiler.bytecode.info.ExceptionTableInfo;
import de.lorenzwiest.basiccompiler.compiler.Compiler;
import de.lorenzwiest.basiccompiler.compiler.etc.ByteOutStream;
import de.lorenzwiest.basiccompiler.compiler.library.LibraryManager;
import de.lorenzwiest.basiccompiler.compiler.library.methods.Method;

public class Method_Rnd extends Method {
  private final static String METHOD_NAME = "Rnd";
  private final static String DESCRIPTOR = "(F)F";
  private final static int NUM_LOCALS = 1;

  public Method_Rnd(LibraryManager libraryManager) {
    super(libraryManager, METHOD_NAME, DESCRIPTOR, NUM_LOCALS);
  }

  @Override
  public void addMethodByteCode(ByteOutStream o, List<ExceptionTableInfo> e) {

    // TODO: Implement RND(X) with X < 0

    // local 0: F argument

    int lastRndFieldRef = this.classModel.addFieldAndGetFieldRefIndex(Compiler.FIELD_LAST_RND, "F");

    o.fload_0();
    o.fconst_0();
    o.fcmpg();
    o.istore_0();

    o.iload_0();
    o.ifge("isZeroOrPositiveArg");

    emitThrowRuntimeException(o, "RND(): Argument < 0.");

    o.label("isZeroOrPositiveArg");
    o.iload_0();
    o.ifgt("newRandomNumber");

    o.getstatic(lastRndFieldRef); // HACK: initializes last random number
    o.fconst_0();
    o.fcmpg();
    o.ifeq("newRandomNumber");

    o.getstatic(lastRndFieldRef);
    o.freturn();

    o.label("newRandomNumber");
    o.invokestatic(this.classModel.getJavaMethodRefIndex(MATH_RANDOM));
    o.d2f();
    o.dup();
    o.putstatic(lastRndFieldRef);
    o.freturn();
  }
}
