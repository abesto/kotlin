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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.codegen.Callable;
import org.jetbrains.jet.codegen.ExpressionCodegen;
import org.jetbrains.jet.codegen.ExtendedCallable;
import org.jetbrains.jet.codegen.StackValue;
import org.jetbrains.jet.codegen.context.CodegenContext;
import org.jetbrains.jet.codegen.state.GenerationState;
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor;
import org.jetbrains.jet.lang.psi.JetExpression;
import org.jetbrains.org.objectweb.asm.Type;
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter;

import java.util.List;

import static org.jetbrains.jet.codegen.AsmUtil.numberFunctionOperandType;

public abstract class IntrinsicMethod implements Callable {
    public final void generate(
            @NotNull ExpressionCodegen codegen,
            @NotNull InstructionAdapter v,
            @NotNull Type returnType,
            @Nullable PsiElement element,
            @Nullable List<JetExpression> arguments,
            @Nullable StackValue receiver
    ) {
        Type actualType = generateImpl(codegen, v, returnType, element, arguments, receiver);
        StackValue.coerce(actualType, returnType, v);
    }

    @NotNull
    protected abstract Type generateImpl(
            @NotNull ExpressionCodegen codegen,
            @NotNull InstructionAdapter v,
            @NotNull Type returnType,
            @Nullable PsiElement element,
            @Nullable List<JetExpression> arguments,
            @Nullable StackValue receiver
    );

    public boolean supportCallable() {
        return false;
    }

    @NotNull
    public ExtendedCallable toCallable(@NotNull GenerationState state, @NotNull FunctionDescriptor fd, @NotNull CodegenContext<?> context) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<Type> transformTypes(List<Type> types) {
        return Lists.transform(types, new Function<Type, Type>() {
            @Override
            public Type apply(Type input) {
                return numberFunctionOperandType(input);
            }
        });
    }
}
