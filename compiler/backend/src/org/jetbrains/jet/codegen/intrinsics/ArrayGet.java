/*
 * Copyright 2010-2013 JetBrains s.r.o.
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

package org.jetbrains.jet.codegen.intrinsics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.org.objectweb.asm.Type;
import org.jetbrains.jet.codegen.ExpressionCodegen;
import org.jetbrains.jet.codegen.StackValue;
import org.jetbrains.jet.lang.psi.JetExpression;

import java.util.List;

import static org.jetbrains.jet.codegen.AsmUtil.correctElementType;

public class ArrayGet extends LazyIntrinsicMethod {
    @NotNull
    @Override
    public StackValue generateImpl(
            @NotNull ExpressionCodegen codegen,
            @NotNull Type returnType,
            PsiElement element,
            @NotNull List<JetExpression> arguments,
            @NotNull StackValue receiver
    ) {
        int argumentIndex;
        if (receiver == StackValue.none()) {
            receiver = codegen.gen(arguments.get(0));
            argumentIndex = 1;
        } else {
            argumentIndex = 0;
        }

        Type type = correctElementType(receiver.type);
        return StackValue.arrayElement(type, receiver, StackValue.coercion(codegen.gen(arguments.get(argumentIndex)), Type.INT_TYPE));
    }
}
