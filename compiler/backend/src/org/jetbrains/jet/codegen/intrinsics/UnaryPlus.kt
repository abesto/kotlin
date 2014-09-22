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
import org.jetbrains.jet.codegen.CallableMethod
import org.jetbrains.jet.codegen.ExtendedCallable
import org.jetbrains.jet.codegen.context.CodegenContext
import org.jetbrains.jet.codegen.state.GenerationState
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.jet.codegen.ExpressionCodegen
import org.jetbrains.jet.codegen.StackValue
import org.jetbrains.jet.lang.psi.JetExpression

import org.jetbrains.jet.codegen.AsmUtil.isPrimitive

public class UnaryPlus : IntrinsicMethod() {
    override fun generateImpl(codegen: ExpressionCodegen, v: InstructionAdapter, returnType: Type, element: PsiElement?, arguments: List<JetExpression>?, receiver: StackValue?): Type {
        assert(isPrimitive(returnType)) { "Return type of UnaryPlus intrinsic should be of primitive type : " + returnType }

        if (receiver != null && receiver != StackValue.none()) {
            receiver.put(returnType, v)
        }
        else {
            assert(arguments != null)
            codegen.gen(arguments!!.get(0), returnType)
        }
        return returnType
    }

    override fun supportCallable(): Boolean {
        return true
    }

    override fun toCallable(state: GenerationState, fd: FunctionDescriptor, context: CodegenContext<*>): ExtendedCallable {
        val method = state.getTypeMapper().mapToCallableMethod(fd, false, context)
        return UnaryIntrinsic(method, needPrimitiveCheck = true) {
            //nothing
        }
    }
}
