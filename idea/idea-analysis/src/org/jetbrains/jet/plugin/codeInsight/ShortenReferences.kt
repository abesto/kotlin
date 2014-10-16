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

package org.jetbrains.jet.plugin.codeInsight

import com.intellij.psi.PsiElement;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.plugin.project.ResolveSessionForBodies;
import org.jetbrains.jet.plugin.quickfix.ImportInsertHelper;
import org.jetbrains.jet.renderer.DescriptorRenderer;
import java.util.HashSet;
import com.intellij.psi.util.PsiTreeUtil
import java.util.ArrayList
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import org.jetbrains.jet.plugin.caches.resolve.getLazyResolveSession
import org.jetbrains.jet.lang.resolve.calls.model.ResolvedCall
import org.jetbrains.jet.renderer.DescriptorRenderer.FQ_NAMES_IN_TYPES
import org.jetbrains.jet.lang.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.jet.lang.resolve.DescriptorUtils
import org.jetbrains.jet.lang.resolve.ImportPath
import org.jetbrains.jet.lang.psi.psiUtil.getQualifiedElementSelector
import java.util.Collections
import org.jetbrains.jet.analyzer.analyzeInContext
import org.jetbrains.jet.lang.resolve.calls.callUtil.getCalleeExpressionIfAny
import java.util.LinkedHashSet
import com.intellij.psi.search.SearchScope
import org.jetbrains.jet.plugin.search.usagesSearch.DefaultSearchHelper
import org.jetbrains.jet.plugin.search.usagesSearch.UsagesSearchTarget
import com.intellij.psi.PsiNamedElement
import org.jetbrains.jet.plugin.search.usagesSearch.search
import org.jetbrains.jet.lang.psi.psiUtil.parents
import com.intellij.openapi.project.Project
import org.jetbrains.jet.lang.psi.psiUtil.getQualifiedElement
import org.jetbrains.jet.lang.types.JetType

public object ShortenReferences {
    public fun process(element: JetElement) {
        process(listOf(element))
    }

    public fun process(elements: Iterable<JetElement>) {
        process(elements, { FilterResult.PROCESS })
    }

    public fun process(file: JetFile, startOffset: Int, endOffset: Int) {
        val documentManager = PsiDocumentManager.getInstance(file.getProject())
        val document = documentManager.getDocument(file)!!
        if (!documentManager.isCommitted(document)) {
            throw IllegalStateException("Document should be committed to shorten references in range")
        }

        val rangeMarker = document.createRangeMarker(startOffset, endOffset)
        rangeMarker.setGreedyToLeft(true)
        rangeMarker.setGreedyToRight(true)
        try {
            process(listOf(file), { element ->
                if (rangeMarker.isValid()) {
                    val range = TextRange(rangeMarker.getStartOffset(), rangeMarker.getEndOffset())

                    val elementRange = element.getTextRange()!!
                    when {
                        range.contains(elementRange) -> FilterResult.PROCESS

                        range.intersects(elementRange) -> {
                            // for qualified call expression allow to shorten only the part without parenthesis
                            val calleeExpression = ((element as? JetDotQualifiedExpression)
                                    ?.getSelectorExpression() as? JetCallExpression)
                                    ?.getCalleeExpression()
                            if (calleeExpression != null) {
                                val rangeWithoutParenthesis = TextRange(elementRange.getStartOffset(), calleeExpression.getTextRange()!!.getEndOffset())
                                if (range.contains(rangeWithoutParenthesis)) FilterResult.PROCESS else FilterResult.GO_INSIDE
                            }
                            else {
                                FilterResult.GO_INSIDE
                            }
                        }

                        else -> FilterResult.SKIP
                    }
                }
                else {
                    FilterResult.SKIP
                }
            })
        }
        finally {
            rangeMarker.dispose()
        }
    }

    fun processAllReferencesInFile(descriptors: List<DeclarationDescriptor>, file: JetFile) {
        val renderedDescriptors = descriptors.map { it.asString() }
        val referenceToContext = resolveReferencesInFile(file).filter { e ->
            val (ref, context) = e
            val renderedTargets = ref.getTargets(context).map { it.asString() }
            ref is JetSimpleNameExpression && renderedDescriptors.any { it in renderedTargets }
        }
        shortenReferencesInFile(
                file,
                referenceToContext.keySet().map { (it as JetSimpleNameExpression).getQualifiedElement() },
                referenceToContext,
                { FilterResult.PROCESS }
        )
    }

    private enum class FilterResult {
        SKIP
        GO_INSIDE
        PROCESS
    }

    private fun resolveReferencesInFile(
            file: JetFile,
            fileElements: List<JetElement> = Collections.singletonList(file)
    ): Map<JetReferenceExpression, BindingContext> {
        ImportInsertHelper.getInstance().optimizeImportsOnTheFly(file)
        return JetFileReferencesResolver.resolve(file, fileElements, resolveShortNames = false)
    }

    private fun shortenReferencesInFile(
            file: JetFile,
            elements: List<JetElement>,
            referenceToContext: Map<JetReferenceExpression, BindingContext>,
            elementFilter: (PsiElement) -> FilterResult
    ) {
        processElements(elements, ShortenTypesVisitor(file, elementFilter, referenceToContext))
        processElements(elements, ShortenQualifiedExpressionsVisitor(file, elementFilter, referenceToContext))
    }

    private fun process(elements: Iterable<JetElement>, elementFilter: (PsiElement) -> FilterResult) {
        for ((file, fileElements) in elements.groupBy { element -> element.getContainingJetFile() }) {
            // first resolve all qualified references - optimization
            val referenceToContext = resolveReferencesInFile(file, fileElements)
            shortenReferencesInFile(file, fileElements, referenceToContext, elementFilter)
        }
    }

    private fun processElements(elements: Iterable<JetElement>, visitor: ShorteningVisitor<*>) {
        for (element in elements) {
            element.accept(visitor)
        }
        visitor.finish()
    }

    private abstract class ShorteningVisitor<T : JetElement>(
            val file: JetFile,
            val elementFilter: (PsiElement) -> FilterResult,
            val resolveMap: Map<JetReferenceExpression, BindingContext>) : JetVisitorVoid() {
        protected val resolveSession: ResolveSessionForBodies
            get() = file.getLazyResolveSession()

        protected val elementsToShorten: MutableSet<T> = LinkedHashSet()

        protected fun bindingContext(element: JetElement): BindingContext
                = resolveMap[element] ?: resolveSession.resolveToElement(element)

        protected abstract fun getShortenedElement(element: T): JetElement?

        override fun visitElement(element: PsiElement) {
            if (elementFilter(element) != FilterResult.SKIP) {
                element.acceptChildren(this)
            }
        }

        public fun finish() {
            for (element in elementsToShorten) {
                getShortenedElement(element)?.let { element.replace(it) }
            }
        }
    }

    private class ShortenTypesVisitor(
            file: JetFile,
            elementFilter: (PsiElement) -> FilterResult,
            resolveMap: Map<JetReferenceExpression, BindingContext>
    ) : ShorteningVisitor<JetUserType>(file, elementFilter, resolveMap) {
        private fun canShortenType(userType: JetUserType): Boolean {
            if (userType.getQualifier() == null) return false
            val referenceExpression = userType.getReferenceExpression()
            if (referenceExpression == null) return false

            val target = bindingContext(referenceExpression)[BindingContext.REFERENCE_TARGET, referenceExpression]?.let { desc ->
                if (desc is ConstructorDescriptor) desc.getContainingDeclaration() else desc
            }
            if (target == null) return false

            val typeReference = PsiTreeUtil.getParentOfType(userType, javaClass<JetTypeReference>())!!
            val scope = resolveSession.resolveToElement(typeReference)[BindingContext.TYPE_RESOLUTION_SCOPE, typeReference]!!
            val name = target.getName()
            val targetByName = scope.getClassifier(name)
            if (targetByName == null) {
                if (target.getContainingDeclaration() is ClassDescriptor) return false

                addImport(target, file)
                return true
            }
            else if (target.asString() == targetByName.asString()) {
                return true
            }
            else {
                // leave FQ name
                return false
            }
        }

        override fun visitUserType(userType: JetUserType) {
            val filterResult = elementFilter(userType)
            if (filterResult == FilterResult.SKIP) return

            userType.getTypeArgumentList()?.accept(this)

            if (filterResult == FilterResult.PROCESS && canShortenType(userType)) {
                elementsToShorten.add(userType)
            }
            else{
                userType.getQualifier()?.accept(this)
            }
        }

        override fun getShortenedElement(element: JetUserType): JetElement? {
            val referenceExpression = element.getReferenceExpression() ?: return null
            val typeArgumentList = element.getTypeArgumentList()
            val text = referenceExpression.getText() + (if (typeArgumentList != null) typeArgumentList.getText() else "")
            return JetPsiFactory(element).createType(text).getTypeElement()!!
        }
    }

    private class ShortenQualifiedExpressionsVisitor(
            file: JetFile,
            elementFilter: (PsiElement) -> FilterResult,
            resolveMap: Map<JetReferenceExpression, BindingContext>
    ) : ShorteningVisitor<JetQualifiedExpression>(file, elementFilter, resolveMap) {
        private fun canShorten(qualifiedExpression: JetDotQualifiedExpression): Boolean {
            val context = bindingContext(qualifiedExpression)

            if (context[BindingContext.QUALIFIER, qualifiedExpression.getReceiverExpression()] == null) return false

            if (PsiTreeUtil.getParentOfType(
                    qualifiedExpression,
                    javaClass<JetImportDirective>(), javaClass<JetPackageDirective>()) != null) return false

            val selector = qualifiedExpression.getSelectorExpression() ?: return false
            val callee = selector.getCalleeExpressionIfAny() as? JetReferenceExpression ?: return false
            val targetBefore = callee.getTargets(context).singleOrNull() ?: return false
            val isClassMember = targetBefore.getContainingDeclaration() is ClassDescriptor
            val isClassOrPackage = targetBefore is ClassDescriptor || targetBefore is PackageViewDescriptor

            val scope = context[BindingContext.RESOLUTION_SCOPE, qualifiedExpression] ?: return false
            val selectorCopy = selector.copy() as JetReferenceExpression
            val newContext = selectorCopy.analyzeInContext(scope)
            val targetsAfter = (selectorCopy.getCalleeExpressionIfAny() as JetReferenceExpression).getTargets(newContext)

            return when (targetsAfter.size) {
                0 -> {
                    if (!isClassMember && isClassOrPackage) {
                        addImport(targetBefore, file)
                        return true
                    }
                    false
                }

                1 -> targetBefore == targetsAfter.first()

                else -> false
            }
        }

        override fun visitDotQualifiedExpression(expression: JetDotQualifiedExpression) {
            val filterResult = elementFilter(expression)
            if (filterResult == FilterResult.SKIP) return

            expression.getSelectorExpression()?.acceptChildren(this)

            if (filterResult == FilterResult.PROCESS && canShorten(expression)) {
                elementsToShorten.add(expression)
            }
            else {
                expression.getReceiverExpression().accept(this)
            }
        }

        override fun getShortenedElement(element: JetQualifiedExpression): JetElement = element.getSelectorExpression()!!
    }

    private fun DeclarationDescriptor.asString()
            = DescriptorRenderer.FQ_NAMES_IN_TYPES.render(this)

    private fun JetReferenceExpression.getTargets(context: BindingContext): Collection<DeclarationDescriptor> {
        fun adjustDescriptor(it: DeclarationDescriptor): DeclarationDescriptor =
                (it as? ConstructorDescriptor)?.getContainingDeclaration() ?: it

        return context[BindingContext.REFERENCE_TARGET, this]?.let { Collections.singletonList(adjustDescriptor(it)) }
               ?: context[BindingContext.AMBIGUOUS_REFERENCE_TARGET, this]?.mapTo(HashSet<DeclarationDescriptor>()) { adjustDescriptor(it) }
               ?: Collections.emptyList()
    }

    private fun addImport(descriptor: DeclarationDescriptor, file: JetFile) {
        ImportInsertHelper.getInstance().writeImportToFile(ImportPath(DescriptorUtils.getFqNameSafe(descriptor), false), file)
    }
}
