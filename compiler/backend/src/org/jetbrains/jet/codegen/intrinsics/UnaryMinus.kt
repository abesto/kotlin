/*
 * Copyright 2010-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.codegen.intrinsics

import com.intellij.psi.PsiElement
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.jet.codegen.ExpressionCodegen
import org.jetbrains.jet.codegen.StackValue
import org.jetbrains.jet.lang.psi.JetExpression

import org.jetbrains.jet.codegen.AsmUtil.*
import org.jetbrains.jet.codegen.state.GenerationState
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor
import org.jetbrains.jet.codegen.context.CodegenContext
import org.jetbrains.jet.codegen.ExtendedCallable

public class UnaryMinus : IntrinsicMethod() {
    override fun generateImpl(codegen: ExpressionCodegen, v: InstructionAdapter, returnType: Type, element: PsiElement?, arguments: List<JetExpression>?, receiver: StackValue?): Type {
        assert(isPrimitive(returnType)) { "Return type of UnaryMinus intrinsic should be of primitive type: " + returnType }

        val operandType = numberFunctionOperandType(returnType)

        if (arguments!!.size() == 1) {
            codegen.gen(arguments.get(0), operandType)
        }
        else {
            receiver!!.put(operandType, v)
        }
        return genNegate(returnType, v)
    }


    override fun toCallable(state: GenerationState, fd: FunctionDescriptor, context: CodegenContext<out DeclarationDescriptor?>): ExtendedCallable {
        val method = state.getTypeMapper().mapToCallableMethod(fd, false, context)
        return UnaryIntrinsic(method, numberFunctionOperandType(method.getReturnType()), needPrimitiveCheck = true) {
            genNegate(getReturnType(), it)
        }
    }
    override fun supportCallable(): Boolean {
        return true
    }
}
