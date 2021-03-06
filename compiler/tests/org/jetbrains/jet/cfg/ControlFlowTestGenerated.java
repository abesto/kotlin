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

package org.jetbrains.jet.cfg;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.jet.JUnit3RunnerWithInners;
import org.jetbrains.jet.JetTestUtils;
import org.jetbrains.jet.test.InnerTestClasses;
import org.jetbrains.jet.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.jet.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/testData/cfg")
@TestDataPath("$PROJECT_ROOT")
@InnerTestClasses({ControlFlowTestGenerated.Arrays.class, ControlFlowTestGenerated.Basic.class, ControlFlowTestGenerated.Bugs.class, ControlFlowTestGenerated.ControlStructures.class, ControlFlowTestGenerated.Conventions.class, ControlFlowTestGenerated.DeadCode.class, ControlFlowTestGenerated.Declarations.class, ControlFlowTestGenerated.Expressions.class, ControlFlowTestGenerated.Functions.class, ControlFlowTestGenerated.TailCalls.class})
@RunWith(JUnit3RunnerWithInners.class)
public class ControlFlowTestGenerated extends AbstractControlFlowTest {
    public void testAllFilesPresentInCfg() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg"), Pattern.compile("^(.+)\\.kt$"), true);
    }

    @TestMetadata("compiler/testData/cfg/arrays")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Arrays extends AbstractControlFlowTest {
        public void testAllFilesPresentInArrays() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/arrays"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("ArrayAccess.kt")
        public void testArrayAccess() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/ArrayAccess.kt");
            doTest(fileName);
        }

        @TestMetadata("arrayAccessExpression.kt")
        public void testArrayAccessExpression() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/arrayAccessExpression.kt");
            doTest(fileName);
        }

        @TestMetadata("arrayInc.kt")
        public void testArrayInc() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/arrayInc.kt");
            doTest(fileName);
        }

        @TestMetadata("arrayIncUnresolved.kt")
        public void testArrayIncUnresolved() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/arrayIncUnresolved.kt");
            doTest(fileName);
        }

        @TestMetadata("ArrayOfFunctions.kt")
        public void testArrayOfFunctions() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/ArrayOfFunctions.kt");
            doTest(fileName);
        }

        @TestMetadata("arraySet.kt")
        public void testArraySet() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/arraySet.kt");
            doTest(fileName);
        }

        @TestMetadata("arraySetPlusAssign.kt")
        public void testArraySetPlusAssign() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/arraySetPlusAssign.kt");
            doTest(fileName);
        }

        @TestMetadata("arraySetPlusAssignUnresolved.kt")
        public void testArraySetPlusAssignUnresolved() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/arraySetPlusAssignUnresolved.kt");
            doTest(fileName);
        }

        @TestMetadata("arraySetUnresolved.kt")
        public void testArraySetUnresolved() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/arrays/arraySetUnresolved.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/basic")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Basic extends AbstractControlFlowTest {
        public void testAllFilesPresentInBasic() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/basic"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("Basic.kt")
        public void testBasic() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/basic/Basic.kt");
            doTest(fileName);
        }

        @TestMetadata("EmptyFunction.kt")
        public void testEmptyFunction() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/basic/EmptyFunction.kt");
            doTest(fileName);
        }

        @TestMetadata("ShortFunction.kt")
        public void testShortFunction() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/basic/ShortFunction.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/bugs")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Bugs extends AbstractControlFlowTest {
        public void testAllFilesPresentInBugs() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/bugs"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("jumpToOuterScope.kt")
        public void testJumpToOuterScope() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/bugs/jumpToOuterScope.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/controlStructures")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class ControlStructures extends AbstractControlFlowTest {
        public void testAllFilesPresentInControlStructures() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/controlStructures"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("continueInDoWhile.kt")
        public void testContinueInDoWhile() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/continueInDoWhile.kt");
            doTest(fileName);
        }

        @TestMetadata("continueInFor.kt")
        public void testContinueInFor() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/continueInFor.kt");
            doTest(fileName);
        }

        @TestMetadata("continueInWhile.kt")
        public void testContinueInWhile() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/continueInWhile.kt");
            doTest(fileName);
        }

        @TestMetadata("Finally.kt")
        public void testFinally() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/Finally.kt");
            doTest(fileName);
        }

        @TestMetadata("FinallyTestCopy.kt")
        public void testFinallyTestCopy() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/FinallyTestCopy.kt");
            doTest(fileName);
        }

        @TestMetadata("For.kt")
        public void testFor() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/For.kt");
            doTest(fileName);
        }

        @TestMetadata("If.kt")
        public void testIf() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/If.kt");
            doTest(fileName);
        }

        @TestMetadata("OnlyWhileInFunctionBody.kt")
        public void testOnlyWhileInFunctionBody() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/OnlyWhileInFunctionBody.kt");
            doTest(fileName);
        }

        @TestMetadata("returnsInWhen.kt")
        public void testReturnsInWhen() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/returnsInWhen.kt");
            doTest(fileName);
        }

        @TestMetadata("whenConditions.kt")
        public void testWhenConditions() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/controlStructures/whenConditions.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/conventions")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Conventions extends AbstractControlFlowTest {
        public void testAllFilesPresentInConventions() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/conventions"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("bothReceivers.kt")
        public void testBothReceivers() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/conventions/bothReceivers.kt");
            doTest(fileName);
        }

        @TestMetadata("equals.kt")
        public void testEquals() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/conventions/equals.kt");
            doTest(fileName);
        }

        @TestMetadata("incrementAtTheEnd.kt")
        public void testIncrementAtTheEnd() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/conventions/incrementAtTheEnd.kt");
            doTest(fileName);
        }

        @TestMetadata("invoke.kt")
        public void testInvoke() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/conventions/invoke.kt");
            doTest(fileName);
        }

        @TestMetadata("notEqual.kt")
        public void testNotEqual() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/conventions/notEqual.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/deadCode")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class DeadCode extends AbstractControlFlowTest {
        public void testAllFilesPresentInDeadCode() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/deadCode"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("DeadCode.kt")
        public void testDeadCode() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/deadCode/DeadCode.kt");
            doTest(fileName);
        }

        @TestMetadata("returnInElvis.kt")
        public void testReturnInElvis() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/deadCode/returnInElvis.kt");
            doTest(fileName);
        }

        @TestMetadata("stringTemplate.kt")
        public void testStringTemplate() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/deadCode/stringTemplate.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/declarations")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({Declarations.ClassesAndObjects.class, Declarations.FunctionLiterals.class, Declarations.Functions.class, Declarations.Local.class, Declarations.MultiDeclaration.class, Declarations.Properties.class})
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Declarations extends AbstractControlFlowTest {
        public void testAllFilesPresentInDeclarations() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/declarations"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("compiler/testData/cfg/declarations/classesAndObjects")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassesAndObjects extends AbstractControlFlowTest {
            public void testAllFilesPresentInClassesAndObjects() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/declarations/classesAndObjects"), Pattern.compile("^(.+)\\.kt$"), true);
            }

            @TestMetadata("AnonymousInitializers.kt")
            public void testAnonymousInitializers() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/classesAndObjects/AnonymousInitializers.kt");
                doTest(fileName);
            }

            @TestMetadata("delegationByExpression.kt")
            public void testDelegationByExpression() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/classesAndObjects/delegationByExpression.kt");
                doTest(fileName);
            }

            @TestMetadata("delegationBySuperCall.kt")
            public void testDelegationBySuperCall() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/classesAndObjects/delegationBySuperCall.kt");
                doTest(fileName);
            }
        }

        @TestMetadata("compiler/testData/cfg/declarations/functionLiterals")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class FunctionLiterals extends AbstractControlFlowTest {
            public void testAllFilesPresentInFunctionLiterals() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/declarations/functionLiterals"), Pattern.compile("^(.+)\\.kt$"), true);
            }

            @TestMetadata("unusedFunctionLiteral.kt")
            public void testUnusedFunctionLiteral() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/functionLiterals/unusedFunctionLiteral.kt");
                doTest(fileName);
            }
        }

        @TestMetadata("compiler/testData/cfg/declarations/functions")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class Functions extends AbstractControlFlowTest {
            public void testAllFilesPresentInFunctions() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/declarations/functions"), Pattern.compile("^(.+)\\.kt$"), true);
            }

            @TestMetadata("FailFunction.kt")
            public void testFailFunction() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/functions/FailFunction.kt");
                doTest(fileName);
            }

            @TestMetadata("typeParameter.kt")
            public void testTypeParameter() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/functions/typeParameter.kt");
                doTest(fileName);
            }
        }

        @TestMetadata("compiler/testData/cfg/declarations/local")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class Local extends AbstractControlFlowTest {
            public void testAllFilesPresentInLocal() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/declarations/local"), Pattern.compile("^(.+)\\.kt$"), true);
            }

            @TestMetadata("localClass.kt")
            public void testLocalClass() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/local/localClass.kt");
                doTest(fileName);
            }

            @TestMetadata("LocalDeclarations.kt")
            public void testLocalDeclarations() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/local/LocalDeclarations.kt");
                doTest(fileName);
            }

            @TestMetadata("localProperty.kt")
            public void testLocalProperty() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/local/localProperty.kt");
                doTest(fileName);
            }

            @TestMetadata("ObjectExpression.kt")
            public void testObjectExpression() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/local/ObjectExpression.kt");
                doTest(fileName);
            }
        }

        @TestMetadata("compiler/testData/cfg/declarations/multiDeclaration")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class MultiDeclaration extends AbstractControlFlowTest {
            public void testAllFilesPresentInMultiDeclaration() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/declarations/multiDeclaration"), Pattern.compile("^(.+)\\.kt$"), true);
            }

            @TestMetadata("MultiDecl.kt")
            public void testMultiDecl() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/multiDeclaration/MultiDecl.kt");
                doTest(fileName);
            }

            @TestMetadata("multiDeclarationWithError.kt")
            public void testMultiDeclarationWithError() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/multiDeclaration/multiDeclarationWithError.kt");
                doTest(fileName);
            }
        }

        @TestMetadata("compiler/testData/cfg/declarations/properties")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class Properties extends AbstractControlFlowTest {
            public void testAllFilesPresentInProperties() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/declarations/properties"), Pattern.compile("^(.+)\\.kt$"), true);
            }

            @TestMetadata("backingFieldAccess.kt")
            public void testBackingFieldAccess() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/properties/backingFieldAccess.kt");
                doTest(fileName);
            }

            @TestMetadata("backingFieldQualifiedWithThis.kt")
            public void testBackingFieldQualifiedWithThis() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/properties/backingFieldQualifiedWithThis.kt");
                doTest(fileName);
            }

            @TestMetadata("DelegatedProperty.kt")
            public void testDelegatedProperty() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/properties/DelegatedProperty.kt");
                doTest(fileName);
            }

            @TestMetadata("unreachableDelegation.kt")
            public void testUnreachableDelegation() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/declarations/properties/unreachableDelegation.kt");
                doTest(fileName);
            }
        }
    }

    @TestMetadata("compiler/testData/cfg/expressions")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Expressions extends AbstractControlFlowTest {
        public void testAllFilesPresentInExpressions() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/expressions"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("assignmentToThis.kt")
        public void testAssignmentToThis() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/assignmentToThis.kt");
            doTest(fileName);
        }

        @TestMetadata("Assignments.kt")
        public void testAssignments() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/Assignments.kt");
            doTest(fileName);
        }

        @TestMetadata("callableReferences.kt")
        public void testCallableReferences() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/callableReferences.kt");
            doTest(fileName);
        }

        @TestMetadata("casts.kt")
        public void testCasts() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/casts.kt");
            doTest(fileName);
        }

        @TestMetadata("chainedQualifiedExpression.kt")
        public void testChainedQualifiedExpression() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/chainedQualifiedExpression.kt");
            doTest(fileName);
        }

        @TestMetadata("expressionAsFunction.kt")
        public void testExpressionAsFunction() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/expressionAsFunction.kt");
            doTest(fileName);
        }

        @TestMetadata("incdec.kt")
        public void testIncdec() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/incdec.kt");
            doTest(fileName);
        }

        @TestMetadata("invalidVariableCall.kt")
        public void testInvalidVariableCall() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/invalidVariableCall.kt");
            doTest(fileName);
        }

        @TestMetadata("LazyBooleans.kt")
        public void testLazyBooleans() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/LazyBooleans.kt");
            doTest(fileName);
        }

        @TestMetadata("nothingExpr.kt")
        public void testNothingExpr() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/nothingExpr.kt");
            doTest(fileName);
        }

        @TestMetadata("parenthesizedSelector.kt")
        public void testParenthesizedSelector() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/parenthesizedSelector.kt");
            doTest(fileName);
        }

        @TestMetadata("propertySafeCall.kt")
        public void testPropertySafeCall() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/propertySafeCall.kt");
            doTest(fileName);
        }

        @TestMetadata("qualifiedExpressionWithoutSelector.kt")
        public void testQualifiedExpressionWithoutSelector() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/qualifiedExpressionWithoutSelector.kt");
            doTest(fileName);
        }

        @TestMetadata("ReturnFromExpression.kt")
        public void testReturnFromExpression() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/ReturnFromExpression.kt");
            doTest(fileName);
        }

        @TestMetadata("thisExpression.kt")
        public void testThisExpression() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/thisExpression.kt");
            doTest(fileName);
        }

        @TestMetadata("unresolvedCall.kt")
        public void testUnresolvedCall() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/unresolvedCall.kt");
            doTest(fileName);
        }

        @TestMetadata("unresolvedProperty.kt")
        public void testUnresolvedProperty() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/unresolvedProperty.kt");
            doTest(fileName);
        }

        @TestMetadata("unresolvedWriteLHS.kt")
        public void testUnresolvedWriteLHS() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/unresolvedWriteLHS.kt");
            doTest(fileName);
        }

        @TestMetadata("unsupportedReturns.kt")
        public void testUnsupportedReturns() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/unsupportedReturns.kt");
            doTest(fileName);
        }

        @TestMetadata("unusedExpressionSimpleName.kt")
        public void testUnusedExpressionSimpleName() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/expressions/unusedExpressionSimpleName.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/functions")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Functions extends AbstractControlFlowTest {
        public void testAllFilesPresentInFunctions() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/functions"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("DefaultValuesForArguments.kt")
        public void testDefaultValuesForArguments() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/functions/DefaultValuesForArguments.kt");
            doTest(fileName);
        }

        @TestMetadata("unmappedArgs.kt")
        public void testUnmappedArgs() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/functions/unmappedArgs.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("compiler/testData/cfg/tailCalls")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class TailCalls extends AbstractControlFlowTest {
        public void testAllFilesPresentInTailCalls() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("compiler/testData/cfg/tailCalls"), Pattern.compile("^(.+)\\.kt$"), true);
        }

        @TestMetadata("finally.kt")
        public void testFinally() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/tailCalls/finally.kt");
            doTest(fileName);
        }

        @TestMetadata("finallyWithReturn.kt")
        public void testFinallyWithReturn() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/tailCalls/finallyWithReturn.kt");
            doTest(fileName);
        }

        @TestMetadata("sum.kt")
        public void testSum() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/tailCalls/sum.kt");
            doTest(fileName);
        }

        @TestMetadata("try.kt")
        public void testTry() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/tailCalls/try.kt");
            doTest(fileName);
        }

        @TestMetadata("tryCatchFinally.kt")
        public void testTryCatchFinally() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("compiler/testData/cfg/tailCalls/tryCatchFinally.kt");
            doTest(fileName);
        }
    }
}
