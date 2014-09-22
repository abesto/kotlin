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
import org.jetbrains.jet.codegen.state.GenerationState
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor
import org.jetbrains.jet.codegen.context.CodegenContext
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor
import org.jetbrains.jet.codegen.ExtendedCallable
import org.jetbrains.jet.codegen.AsmUtil.numberFunctionOperandType
import org.jetbrains.jet.codegen.AsmUtil.genNegate

public class StringLength : IntrinsicMethod() {
    override fun generateImpl(codegen: ExpressionCodegen, v: InstructionAdapter, returnType: Type, element: PsiElement?, arguments: List<JetExpression>?, receiver: StackValue?): Type {
        receiver!!.put(receiver.`type`, v)
        v.invokeinterface("java/lang/CharSequence", "length", "()I")
        return Type.INT_TYPE
    }

    override fun toCallable(state: GenerationState, fd: FunctionDescriptor, context: CodegenContext<out DeclarationDescriptor?>): ExtendedCallable {
        val method = state.getTypeMapper().mapToCallableMethod(fd, false, context)
        return UnaryIntrinsic(method) {
            it.invokeinterface("java/lang/CharSequence", "length", "()I")
        }
    }
    override fun supportCallable(): Boolean {
        return true
    }
}
