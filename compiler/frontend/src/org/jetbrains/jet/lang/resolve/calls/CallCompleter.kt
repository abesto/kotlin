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

package org.jetbrains.jet.lang.resolve.calls

import javax.inject.Inject
import kotlin.properties.Delegates
import java.util.ArrayList
import org.jetbrains.jet.lang.descriptors.CallableDescriptor
import org.jetbrains.jet.lang.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.jet.lang.resolve.calls.results.OverloadResolutionResultsImpl
import org.jetbrains.jet.lang.resolve.calls.tasks.TracingStrategy
import org.jetbrains.jet.lang.resolve.calls.context.CheckValueArgumentsMode
import org.jetbrains.jet.lang.resolve.calls.model.MutableResolvedCall
import org.jetbrains.jet.lang.types.JetType
import org.jetbrains.jet.lang.resolve.BindingTrace
import org.jetbrains.jet.lang.resolve.calls.inference.ConstraintSystem
import org.jetbrains.jet.lang.resolve.calls.inference.ConstraintPosition.EXPECTED_TYPE_POSITION
import org.jetbrains.jet.lang.types.TypeUtils
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns
import org.jetbrains.jet.lang.resolve.BindingContext.CONSTRAINT_SYSTEM_COMPLETER
import org.jetbrains.jet.lang.resolve.calls.inference.ConstraintPosition
import org.jetbrains.jet.lang.resolve.calls.inference.ConstraintSystemImpl
import org.jetbrains.jet.lang.resolve.calls.context.CallCandidateResolutionContext
import org.jetbrains.jet.lang.resolve.calls.results.ResolutionStatus
import org.jetbrains.jet.lang.resolve.calls.inference.InferenceErrorData
import org.jetbrains.jet.lang.psi.ValueArgument
import org.jetbrains.jet.lang.resolve.calls.model.ArgumentMapping
import org.jetbrains.jet.lang.resolve.calls.autocasts.DataFlowInfo
import org.jetbrains.jet.lang.resolve.calls.model.ArgumentUnmapped
import org.jetbrains.jet.lang.resolve.calls.util.CallMaker
import org.jetbrains.jet.lang.resolve.calls.model.ArgumentMatch
import org.jetbrains.jet.lang.psi.JetWhenExpression
import org.jetbrains.jet.lang.resolve.BindingContext
import org.jetbrains.jet.lang.resolve.calls.context.ResolutionContext
import org.jetbrains.jet.lang.resolve.BindingContextUtils
import org.jetbrains.jet.lang.resolve.calls.context.CallResolutionContext
import org.jetbrains.jet.lang.types.expressions.DataFlowUtils
import org.jetbrains.jet.lang.psi.JetExpression
import org.jetbrains.jet.lang.psi.Call
import org.jetbrains.jet.lang.types.expressions.ExpressionTypingUtils
import org.jetbrains.jet.lang.psi.JetBlockExpression
import org.jetbrains.jet.lang.psi.JetPsiUtil
import org.jetbrains.jet.lang.resolve.bindingContextUtil.getCorrespondingCall
import org.jetbrains.jet.lang.psi.JetSafeQualifiedExpression
import org.jetbrains.jet.lang.resolve.calls.CallResolverUtil.ResolveArgumentsMode.RESOLVE_FUNCTION_ARGUMENTS
import org.jetbrains.jet.lang.resolve.TemporaryBindingTrace

public class CallCompleter(
        val argumentTypeResolver: ArgumentTypeResolver,
        val candidateResolver: CandidateResolver
) {
    fun <D : CallableDescriptor> completeCall(
            context: BasicCallResolutionContext,
            results: OverloadResolutionResultsImpl<D>,
            tracing: TracingStrategy
    ): OverloadResolutionResultsImpl<D> {
        // for the case 'foo(a)' where 'foo' is a variable, the call 'foo.invoke(a)' shouldn't be completed separately,
        // it's completed when the outer (variable as function call) is completed
        if (CallResolverUtil.isInvokeCallOnVariable(context.call)) return results

        if (results.isSingleResult()) {
            completeResolvedCall(results.getResultingCall(), context, tracing)
        }
        if (context.checkArguments == CheckValueArgumentsMode.ENABLED) {
            completeArguments(context, results)
        }

        completeAllCandidates(context, results)
        if (results.isSingleResult() && results.getResultingCall().getStatus().isSuccess()) {
            return results.changeStatusToSuccess()
        }
        return results
    }

    private fun <D : CallableDescriptor> completeAllCandidates(
            context: BasicCallResolutionContext,
            results: OverloadResolutionResultsImpl<D>
    ) {
        [suppress("UNCHECKED_CAST")]
        val candidates = (if (context.collectAllCandidates) {
            results.getAllCandidates()!!
        }
        else {
            results.getResultingCalls()
        }) as Collection<MutableResolvedCall<D>>

        candidates.filter { resolvedCall -> !resolvedCall.isCompleted() }.forEach {
            resolvedCall ->

            val temporaryBindingTrace = TemporaryBindingTrace.create(context.trace, "Trace to complete a candidate that is not a resulting call")
            completeResolvedCall(resolvedCall, context.replaceBindingTrace(temporaryBindingTrace), TracingStrategy.EMPTY)
        }
    }

    private fun <D : CallableDescriptor> completeResolvedCall(
            resolvedCall: MutableResolvedCall<D>,
            context: BasicCallResolutionContext,
            tracing: TracingStrategy
    ) {
        if (resolvedCall.isCompleted() || resolvedCall.getConstraintSystem() == null) {
            resolvedCall.markCallAsCompleted()
            return
        }

        resolvedCall.completeConstraintSystem(context.expectedType, context.trace)
        resolvedCall.updateResolutionStatusFromConstraintSystem(context, tracing)
        resolvedCall.markCallAsCompleted()
    }

    private fun <D : CallableDescriptor> MutableResolvedCall<D>.completeConstraintSystem(
            expectedType: JetType,
            trace: BindingTrace
    ) {
        fun updateSystemIfSuccessful(update: (ConstraintSystem) -> Boolean) {
            val copy = getConstraintSystem()!!.copy()
            if (update(copy)) {
                setConstraintSystem(copy)
            }
        }

        val returnType = getCandidateDescriptor().getReturnType()
        if (returnType != null) {
            getConstraintSystem()!!.addSupertypeConstraint(expectedType, returnType, EXPECTED_TYPE_POSITION)

            if (expectedType === TypeUtils.UNIT_EXPECTED_TYPE) {
                updateSystemIfSuccessful {
                    system ->
                    system.addSupertypeConstraint(KotlinBuiltIns.getInstance().getUnitType(), returnType, EXPECTED_TYPE_POSITION)
                    system.getStatus().isSuccessful()
                }
            }
        }

        val constraintSystemCompleter = trace[CONSTRAINT_SYSTEM_COMPLETER, getCall().getCalleeExpression()]
        if (constraintSystemCompleter != null) {
            //todo improve error reporting with errors in constraints from completer
            updateSystemIfSuccessful {
                system ->
                constraintSystemCompleter.completeConstraintSystem(system, this)
                !system.getStatus().hasOnlyErrorsFromPosition(ConstraintPosition.FROM_COMPLETER)
            }
        }

        (getConstraintSystem() as ConstraintSystemImpl).processDeclaredBoundConstraints()

        setResultingSubstitutor(getConstraintSystem()!!.getResultingSubstitutor())
    }

    private fun <D : CallableDescriptor> MutableResolvedCall<D>.updateResolutionStatusFromConstraintSystem(
            context: BasicCallResolutionContext,
            tracing: TracingStrategy
    ) {
        val contextWithResolvedCall = CallCandidateResolutionContext.createForCallBeingAnalyzed(this, context, tracing)
        val valueArgumentsCheckingResult = candidateResolver.checkAllValueArguments(
                contextWithResolvedCall, context.trace, RESOLVE_FUNCTION_ARGUMENTS)

        val status = getStatus()
        if (getConstraintSystem()!!.getStatus().isSuccessful()) {
            if (status == ResolutionStatus.UNKNOWN_STATUS || status == ResolutionStatus.INCOMPLETE_TYPE_INFERENCE) {
                setStatusToSuccess()
            }
            return
        }

        val receiverType = if (getReceiverArgument().exists()) getReceiverArgument().getType() else null
        val errorData = InferenceErrorData.create(
                getCandidateDescriptor(), getConstraintSystem()!!, valueArgumentsCheckingResult.argumentTypes,
                receiverType, context.expectedType)
        tracing.typeInferenceFailed(context.trace, errorData)

        addStatus(ResolutionStatus.OTHER_ERROR)
    }

    private fun <D : CallableDescriptor> completeArguments(
            context: BasicCallResolutionContext,
            results: OverloadResolutionResultsImpl<D>
    ) {
        if (context.checkArguments == CheckValueArgumentsMode.DISABLED) return

        val getArgumentMapping: (ValueArgument) -> ArgumentMapping
        val getDataFlowInfoForArgument: (ValueArgument) -> DataFlowInfo
        if (results.isSingleResult()) {
            val resolvedCall = results.getResultingCall()
            getArgumentMapping = { argument -> resolvedCall.getArgumentMapping(argument) }
            getDataFlowInfoForArgument = {argument -> resolvedCall.getDataFlowInfoForArguments().getInfo(argument) }
        }
        else {
            getArgumentMapping = { ArgumentUnmapped }
            getDataFlowInfoForArgument = { context.dataFlowInfo }
        }

        val arguments = ArrayList(context.call.getValueArguments())
        arguments.addAll(context.call.getFunctionLiteralArguments().map { functionLiteral -> CallMaker.makeValueArgument(functionLiteral) })

        for (valueArgument in arguments) {
            val argumentMapping = getArgumentMapping(valueArgument!!)
            val expectedType = when (argumentMapping) {
                is ArgumentMatch -> CandidateResolver.getEffectiveExpectedType(argumentMapping.valueParameter, valueArgument)
                else -> TypeUtils.NO_EXPECTED_TYPE
            }
            val newContext = context.replaceDataFlowInfo(getDataFlowInfoForArgument(valueArgument)).replaceExpectedType(expectedType)
            completeOneArgument(valueArgument, newContext)
        }
    }

    private fun completeOneArgument(
            valueArgument: ValueArgument,
            context: BasicCallResolutionContext
    ) {
        val expression = valueArgument.getArgumentExpression()
        if (expression == null) return

        // for the 'in' call 'when (b) { in 1..10 -> true }' 'b' is an argument, but an error shouldn't be generated on it
        // todo add special call type for such a case, and check this call type instead
        val parent = expression.getParent()
        if (parent is JetWhenExpression && expression == parent.getSubjectExpression()) return

        val recordedType = context.trace[BindingContext.EXPRESSION_TYPE, expression]
        var updatedType: JetType? = recordedType

        val results = completeCallForArgument(expression, context)
        if (results != null && results.isSingleResult()) {
            val resolvedCall = results.getResultingCall()
            updatedType = if (resolvedCall.hasInferredReturnType()) resolvedCall.getResultingDescriptor()?.getReturnType() else null
        }

        // For the cases like 'foo(1)' the type of '1' depends on expected type (it can be Int, Byte, etc.),
        // so while the expected type is not known, it's IntegerValueType(1), and should be updated when the expected type is known.
        if (recordedType != null && !recordedType.getConstructor().isDenotable()) {
            updatedType = ArgumentTypeResolver.updateResultArgumentTypeIfNotDenotable(context as ResolutionContext<*>, expression)
        }

        BindingContextUtils.updateRecordedType(updatedType, expression, context.trace, hasNecessarySafeCall(expression, context.trace))

        // While the expected type is not known, the function literal arguments are not analyzed (to analyze function literal bodies once),
        // but they should be analyzed when the expected type is known (during the call completion).
        if (ArgumentTypeResolver.isFunctionLiteralArgument(expression)) {
            argumentTypeResolver.getFunctionLiteralTypeInfo(
                    expression, ArgumentTypeResolver.getFunctionLiteralArgument(expression),
                    context as CallResolutionContext<*>, RESOLVE_FUNCTION_ARGUMENTS)
        }

        DataFlowUtils.checkType(updatedType, expression, context as ResolutionContext<*>)
    }

    private fun completeCallForArgument(
            expression: JetExpression,
            context: BasicCallResolutionContext
    ): OverloadResolutionResultsImpl<*>? {
        val argumentCall = getCallForArgument(expression, context.trace.getBindingContext())
        if (argumentCall == null) return null

        val cachedDataForCall = context.resolutionResultsCache[argumentCall]
        if (cachedDataForCall == null) return null

        val (cachedResolutionResults, cachedContext, tracing) = cachedDataForCall
        [suppress("UNCHECKED_CAST")]
        val cachedResults = cachedResolutionResults as OverloadResolutionResultsImpl<CallableDescriptor>
        val contextForArgument = cachedContext.replaceBindingTrace(context.trace)
                .replaceExpectedType(context.expectedType).replaceCollectAllCandidates(false)

        return completeCall(contextForArgument, cachedResults, tracing)
    }

    private fun getCallForArgument(argument: JetExpression?, bindingContext: BindingContext): Call? {
        if (!ExpressionTypingUtils.dependsOnExpectedType(argument)) {
            return null
        }
        if (argument is JetBlockExpression) {
            val lastStatement = JetPsiUtil.getLastStatementInABlock(argument)
            return getCallForArgument(lastStatement as? JetExpression, bindingContext)
        }
        return argument?.getCorrespondingCall(bindingContext)
    }

    private fun hasNecessarySafeCall(expression: JetExpression, trace: BindingTrace): Boolean {
        // We are interested in type of the last call:
        // 'a.b?.foo()' is safe call, but 'a?.b.foo()' is not.
        // Since receiver is 'a.b' and selector is 'foo()',
        // we can only check if an expression is safe call.
        if (expression !is JetSafeQualifiedExpression) return false

        //If a receiver type is not null, then this safe expression is useless, and we don't need to make the result type nullable.
        val expressionType = trace[BindingContext.EXPRESSION_TYPE, expression.getReceiverExpression()]
        return expressionType != null && expressionType.isNullable()
    }    
}