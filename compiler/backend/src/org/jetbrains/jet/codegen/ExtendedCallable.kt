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

package org.jetbrains.jet.codegen

import org.jetbrains.annotations.ReadOnly
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.jet.lang.descriptors.CallableDescriptor
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.jet.codegen.state.GenerationState
import org.jetbrains.jet.lang.resolve.calls.model.ResolvedCall

public trait ExtendedCallable: Callable {
    ReadOnly
    public fun getValueParameterTypes(): List<Type>

    public fun invokeWithNotNullAssertion(v: InstructionAdapter, state: GenerationState, resolvedCall: ResolvedCall<out CallableDescriptor>)

    public fun invokeWithoutAssertions(v: InstructionAdapter)

    public fun invokeDefaultWithNotNullAssertion(v: InstructionAdapter, state: GenerationState, resolvedCall: ResolvedCall<out CallableDescriptor>)

    public fun getReturnType(): Type

    public fun getThisType(): Type?

    public fun getReceiverClass(): Type?

    public fun getGenerateCalleeType(): Type?

    public fun getOwner() : Type
}