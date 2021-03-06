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

package org.jetbrains.jet.lang.resolve.java.lazy.descriptors

import org.jetbrains.jet.lang.descriptors.*
import org.jetbrains.jet.lang.resolve.name.FqName
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass
import org.jetbrains.jet.lang.descriptors.impl.ClassDescriptorBase
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.lang.types.JetType
import org.jetbrains.jet.lang.types.TypeConstructor
import org.jetbrains.jet.lang.resolve.java.lazy.LazyJavaResolverContext
import org.jetbrains.jet.lang.resolve.java.lazy.child
import org.jetbrains.jet.lang.resolve.java.resolver.TypeUsage
import org.jetbrains.jet.lang.resolve.java.lazy.resolveAnnotations
import org.jetbrains.jet.lang.resolve.java.lazy.types.toAttributes
import org.jetbrains.jet.lang.resolve.scopes.InnerClassesScopeWrapper
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns
import org.jetbrains.jet.utils.*
import org.jetbrains.jet.lang.resolve.java.descriptor.JavaClassDescriptor
import org.jetbrains.jet.lang.descriptors.annotations.Annotations
import org.jetbrains.jet.lang.types.AbstractClassTypeConstructor

class LazyJavaClassDescriptor(
        private val outerC: LazyJavaResolverContext,
        containingDeclaration: DeclarationDescriptor,
        internal val fqName: FqName,
        private val jClass: JavaClass
) : ClassDescriptorBase(outerC.storageManager, containingDeclaration, fqName.shortName(),
                        outerC.sourceElementFactory.source(jClass)), JavaClassDescriptor {

    private val c: LazyJavaResolverContext = outerC.child(this, jClass);

    {
        c.javaResolverCache.recordClass(jClass, this)
    }

    private val kind = when {
        jClass.isAnnotationType() -> ClassKind.ANNOTATION_CLASS
        jClass.isInterface() -> ClassKind.TRAIT
        jClass.isEnum() -> ClassKind.ENUM_CLASS
        else -> ClassKind.CLASS
    }

    private val modality = if (jClass.isAnnotationType())
                               Modality.FINAL
                           else Modality.convertFromFlags(jClass.isAbstract() || jClass.isInterface(), !jClass.isFinal())

    private val visibility = jClass.getVisibility()
    private val isInner = jClass.getOuterClass() != null && !jClass.isStatic()

    override fun getKind() = kind
    override fun getModality() = modality
    override fun getVisibility() = visibility
    override fun isInner() = isInner

    private val typeConstructor = c.storageManager.createLazyValue { LazyJavaClassTypeConstructor() }
    override fun getTypeConstructor(): TypeConstructor = typeConstructor()

    private val scopeForMemberLookup = LazyJavaClassMemberScope(c, this, jClass)
    override fun getScopeForMemberLookup() = scopeForMemberLookup

    private val innerClassesScope = InnerClassesScopeWrapper(getScopeForMemberLookup())
    override fun getUnsubstitutedInnerClassesScope(): JetScope = innerClassesScope

    private val staticScope = LazyJavaStaticClassScope(c, jClass, this)
    override fun getStaticScope(): JetScope = staticScope

    override fun getUnsubstitutedPrimaryConstructor(): ConstructorDescriptor? = null

    override fun getClassObjectDescriptor(): ClassDescriptor? = null
    override fun getClassObjectType(): JetType? = getClassObjectDescriptor()?.let { d -> d.getDefaultType() }

    override fun getConstructors() = scopeForMemberLookup.constructors()

    private val annotations = c.storageManager.createLazyValue { c.resolveAnnotations(jClass) }
    override fun getAnnotations() = annotations()

    private val functionTypeForSamInterface = c.storageManager.createNullableLazyValue {
        c.samConversionResolver.resolveFunctionTypeIfSamInterface(this) { method ->
            scopeForMemberLookup.resolveMethodToFunctionDescriptor(method, false)
        }
    }

    override fun getFunctionTypeForSamInterface(): JetType? = functionTypeForSamInterface()

    override fun toString() = "lazy java class $fqName"

    private inner class LazyJavaClassTypeConstructor : AbstractClassTypeConstructor() {

        private val parameters = c.storageManager.createLazyValue {
            jClass.getTypeParameters().map {
                p ->
                c.typeParameterResolver.resolveTypeParameter(p)
                    ?: throw AssertionError("Parameter $p surely belongs to class ${jClass}, so it must be resolved")
            }
        }

        override fun getParameters(): List<TypeParameterDescriptor> = parameters()

        private val supertypes = c.storageManager.createLazyValue<Collection<JetType>> {
            jClass.getSupertypes().stream()
                    .map {
                        supertype ->
                        c.typeResolver.transformJavaType(supertype, TypeUsage.SUPERTYPE.toAttributes())
                    }
                    .filter { supertype -> !supertype.isError() && !KotlinBuiltIns.getInstance().isAnyOrNullableAny(supertype) }
                    .toList()
                    .ifEmpty {
                        listOf(KotlinBuiltIns.getInstance().getAnyType())
                    }
        }

        override fun getSupertypes(): Collection<JetType> = supertypes()

        override fun getAnnotations() = Annotations.EMPTY

        override fun isFinal() = !getModality().isOverridable()

        override fun isDenotable() = true

        override fun getDeclarationDescriptor() = this@LazyJavaClassDescriptor

        override fun toString(): String = getName().asString()
    }
}