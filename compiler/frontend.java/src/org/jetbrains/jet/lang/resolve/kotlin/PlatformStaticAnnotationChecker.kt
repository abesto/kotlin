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

package org.jetbrains.jet.lang.resolve.kotlin

import org.jetbrains.jet.lang.resolve.AnnotationChecker
import org.jetbrains.jet.lang.psi.JetDeclaration
import org.jetbrains.jet.lang.resolve.BindingTrace
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor
import org.jetbrains.jet.lang.psi.JetClassOrObject
import org.jetbrains.jet.lang.resolve.AnnotationUtil
import org.jetbrains.jet.lang.psi.JetNamedFunction
import org.jetbrains.jet.lang.psi.JetProperty
import org.jetbrains.jet.lang.resolve.DescriptorUtils
import org.jetbrains.jet.lang.descriptors.MemberDescriptor
import org.jetbrains.jet.lang.descriptors.ClassKind
import org.jetbrains.jet.lang.psi.JetDeclarationModifierList
import org.jetbrains.jet.lang.resolve.java.diagnostics.ErrorsJvm
import org.jetbrains.jet.lexer.JetModifierKeywordToken
import org.jetbrains.jet.lexer.JetTokens
import org.jetbrains.jet.lang.descriptors.PropertyDescriptor

public class PlatformStaticAnnotationChecker : AnnotationChecker {

    override fun check(declaration: JetDeclaration, descriptor: MemberDescriptor, trace: BindingTrace) {
        val hasAnnotation = hasAnnotation(descriptor);
        if (hasAnnotation) {
            if (declaration is JetNamedFunction) {
                val insideObject = DescriptorUtils.isInside(descriptor, ClassKind.OBJECT)
                val isInsideClassObject = DescriptorUtils.isInside(descriptor, ClassKind.CLASS_OBJECT)

                if (!insideObject && !(isInsideClassObject && DescriptorUtils.isInside(descriptor.getContainingDeclaration(), ClassKind.CLASS))) {
                    trace.report(ErrorsJvm.PLATFORM_STATIC_NOT_IN_OBJECT.on(declaration, descriptor));
                }

                if (insideObject && declaration.hasModifier(JetTokens.OVERRIDE_KEYWORD)) {
                    //else cause we will not need to report this error
                    trace.report(ErrorsJvm.OVERRIDE_CANNOT_BE_STATIC.on(declaration, descriptor));
                }
            } else {
                //TODO: there should be general mechanism
                trace.report(ErrorsJvm.PLATFORM_STATIC_ILLEGAL_USAGE.on(declaration, descriptor));
            }
        }

        if (declaration is JetProperty) {
            val getter = declaration.getGetter()
            if (getter != null) {
                check(getter, (descriptor as PropertyDescriptor).getGetter()!!, trace)
            }
            val setter = declaration.getSetter()
            if (setter != null) {
                check(setter, (descriptor as PropertyDescriptor).getSetter()!!, trace)
            }
        }
    }

    private fun hasAnnotation(descriptor: DeclarationDescriptor): Boolean {
        return AnnotationUtil.hasPlatformStaticAnnotation(descriptor)
    }
}