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

package org.jetbrains.jet.plugin.quickfix;

import com.intellij.testFramework.TestDataPath;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.jetbrains.jet.JetTestUtils;
import org.jetbrains.jet.test.InnerTestClasses;
import org.jetbrains.jet.test.TestMetadata;
import org.jetbrains.jet.JUnit3RunnerWithInners;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.jet.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/testData/quickfix")
@TestDataPath("$PROJECT_ROOT")
@InnerTestClasses({QuickFixMultiFileTestGenerated.AddStarProjections.class, QuickFixMultiFileTestGenerated.AutoImports.class, QuickFixMultiFileTestGenerated.CreateFromUsage.class, QuickFixMultiFileTestGenerated.Modifiers.class, QuickFixMultiFileTestGenerated.Nullables.class, QuickFixMultiFileTestGenerated.Override.class, QuickFixMultiFileTestGenerated.Suppress.class, QuickFixMultiFileTestGenerated.TypeImports.class, QuickFixMultiFileTestGenerated.TypeMismatch.class, QuickFixMultiFileTestGenerated.Variables.class})
@RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
public class QuickFixMultiFileTestGenerated extends AbstractQuickFixMultiFileTest {
    public void testAllFilesPresentInQuickfix() throws Exception {
        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
    }
    
    @TestMetadata("idea/testData/quickfix/addStarProjections")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class AddStarProjections extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInAddStarProjections() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/addStarProjections"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/autoImports")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class AutoImports extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInAutoImports() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/autoImports"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
        @TestMetadata("classImport.before.Main.kt")
        public void testClassImport() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/classImport.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("divOperator.before.Main.kt")
        public void testDivOperator() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/divOperator.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("extensionFunctionImport.before.Main.kt")
        public void testExtensionFunctionImport() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/extensionFunctionImport.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("extensionFunctionImportImplicitReceiver.before.Main.kt")
        public void testExtensionFunctionImportImplicitReceiver() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/extensionFunctionImportImplicitReceiver.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("extensionPropertyImport.before.Main.kt")
        public void testExtensionPropertyImport() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/extensionPropertyImport.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("functionImport.before.Main.kt")
        public void testFunctionImport() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/functionImport.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("importInFirstPartInQualifiedExpression.before.Main.kt")
        public void testImportInFirstPartInQualifiedExpression() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/importInFirstPartInQualifiedExpression.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("importInFirstPartInUserType.before.Main.kt")
        public void testImportInFirstPartInUserType() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/importInFirstPartInUserType.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("importTrait.before.Main.kt")
        public void testImportTrait() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/importTrait.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("infixCall.before.Main.kt")
        public void testInfixCall() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/infixCall.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("minusOperator.before.Main.kt")
        public void testMinusOperator() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/minusOperator.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("noImportForFunInQualifiedNotFirst.before.Main.kt")
        public void testNoImportForFunInQualifiedNotFirst() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/noImportForFunInQualifiedNotFirst.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("noImportForPrivateClass.before.Main.kt")
        public void testNoImportForPrivateClass() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/noImportForPrivateClass.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("noImportInImports.before.Main.kt")
        public void testNoImportInImports() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/noImportInImports.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("noImportInQualifiedExpressionNotFirst.before.Main.kt")
        public void testNoImportInQualifiedExpressionNotFirst() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/noImportInQualifiedExpressionNotFirst.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("noImportInQualifiedUserTypeNotFirst.before.Main.kt")
        public void testNoImportInQualifiedUserTypeNotFirst() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/noImportInQualifiedUserTypeNotFirst.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("noImportInSafeQualifiedExpressionNotFirst.before.Main.kt")
        public void testNoImportInSafeQualifiedExpressionNotFirst() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/noImportInSafeQualifiedExpressionNotFirst.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("objectImport.before.Main.kt")
        public void testObjectImport() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/objectImport.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("packageClass.before.Main.kt")
        public void testPackageClass() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/packageClass.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("plusOperator.before.Main.kt")
        public void testPlusOperator() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/plusOperator.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("postfixOperator.before.Main.kt")
        public void testPostfixOperator() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/postfixOperator.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("propertyImport.before.Main.kt")
        public void testPropertyImport() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/propertyImport.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("sameModuleImportPriority.before.Main.kt")
        public void testSameModuleImportPriority() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/sameModuleImportPriority.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("timesAssign.before.Main.kt")
        public void testTimesAssign() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/timesAssign.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("unaryMinusOperator.before.Main.kt")
        public void testUnaryMinusOperator() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/unaryMinusOperator.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("unaryPlusOperator.before.Main.kt")
        public void testUnaryPlusOperator() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/unaryPlusOperator.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
        @TestMetadata("withSmartCastQualifier.before.Main.kt")
        public void testWithSmartCastQualifier() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/autoImports/withSmartCastQualifier.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/createFromUsage")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({CreateFromUsage.CreateFunction.class, CreateFromUsage.CreateVariable.class})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class CreateFromUsage extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInCreateFromUsage() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/createFromUsage"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
        @TestMetadata("idea/testData/quickfix/createFromUsage/createFunction")
        @TestDataPath("$PROJECT_ROOT")
        @InnerTestClasses({CreateFunction.Call.class})
        @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
        public static class CreateFunction extends AbstractQuickFixMultiFileTest {
            public void testAllFilesPresentInCreateFunction() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/createFromUsage/createFunction"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
            }
            
            @TestMetadata("idea/testData/quickfix/createFromUsage/createFunction/call")
            @TestDataPath("$PROJECT_ROOT")
            @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
            public static class Call extends AbstractQuickFixMultiFileTest {
                public void testAllFilesPresentInCall() throws Exception {
                    JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/createFromUsage/createFunction/call"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
                }
                
                @TestMetadata("funOnJavaType.before.Main.kt")
                public void testFunOnJavaType() throws Exception {
                    String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/createFromUsage/createFunction/call/funOnJavaType.before.Main.kt");
                    doTestWithExtraFile(fileName);
                }
                
            }
            
        }
        
        @TestMetadata("idea/testData/quickfix/createFromUsage/createVariable")
        @TestDataPath("$PROJECT_ROOT")
        @InnerTestClasses({})
        @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
        public static class CreateVariable extends AbstractQuickFixMultiFileTest {
            public void testAllFilesPresentInCreateVariable() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/createFromUsage/createVariable"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
            }
            
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/modifiers")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({Modifiers.AddOpenToClassDeclaration.class})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class Modifiers extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInModifiers() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/modifiers"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
        @TestMetadata("idea/testData/quickfix/modifiers/addOpenToClassDeclaration")
        @TestDataPath("$PROJECT_ROOT")
        @InnerTestClasses({AddOpenToClassDeclaration.FinalJavaClass.class})
        @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
        public static class AddOpenToClassDeclaration extends AbstractQuickFixMultiFileTest {
            public void testAllFilesPresentInAddOpenToClassDeclaration() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/modifiers/addOpenToClassDeclaration"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
            }
            
            @TestMetadata("idea/testData/quickfix/modifiers/addOpenToClassDeclaration/finalJavaClass")
            @TestDataPath("$PROJECT_ROOT")
            @InnerTestClasses({FinalJavaClass.JavaCode.class})
            @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
            public static class FinalJavaClass extends AbstractQuickFixMultiFileTest {
                public void testAllFilesPresentInFinalJavaClass() throws Exception {
                    JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/modifiers/addOpenToClassDeclaration/finalJavaClass"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
                }
                
                @TestMetadata("idea/testData/quickfix/modifiers/addOpenToClassDeclaration/finalJavaClass/javaCode")
                @TestDataPath("$PROJECT_ROOT")
                @InnerTestClasses({})
                @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
                public static class JavaCode extends AbstractQuickFixMultiFileTest {
                    public void testAllFilesPresentInJavaCode() throws Exception {
                        JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/modifiers/addOpenToClassDeclaration/finalJavaClass/javaCode"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
                    }
                    
                }
                
            }
            
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/nullables")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class Nullables extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInNullables() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/nullables"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/override")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({Override.NothingToOverride.class})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class Override extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInOverride() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/override"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
        @TestMetadata("idea/testData/quickfix/override/nothingToOverride")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
        public static class NothingToOverride extends AbstractQuickFixMultiFileTest {
            public void testAllFilesPresentInNothingToOverride() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/override/nothingToOverride"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
            }
            
            @TestMetadata("import.before.Main.kt")
            public void testImport() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/override/nothingToOverride/import.before.Main.kt");
                doTestWithExtraFile(fileName);
            }
            
            @TestMetadata("twoPackages.before.Main.kt")
            public void testTwoPackages() throws Exception {
                String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/override/nothingToOverride/twoPackages.before.Main.kt");
                doTestWithExtraFile(fileName);
            }
            
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/suppress")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({Suppress.ForStatement.class})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class Suppress extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInSuppress() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/suppress"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
        @TestMetadata("idea/testData/quickfix/suppress/forStatement")
        @TestDataPath("$PROJECT_ROOT")
        @InnerTestClasses({})
        @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
        public static class ForStatement extends AbstractQuickFixMultiFileTest {
            public void testAllFilesPresentInForStatement() throws Exception {
                JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/suppress/forStatement"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
            }
            
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/typeImports")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class TypeImports extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInTypeImports() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/typeImports"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
        @TestMetadata("importFromAnotherFile.before.Main.kt")
        public void testImportFromAnotherFile() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("idea/testData/quickfix/typeImports/importFromAnotherFile.before.Main.kt");
            doTestWithExtraFile(fileName);
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/typeMismatch")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class TypeMismatch extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInTypeMismatch() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/typeMismatch"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
    }
    
    @TestMetadata("idea/testData/quickfix/variables")
    @TestDataPath("$PROJECT_ROOT")
    @InnerTestClasses({})
    @RunWith(org.jetbrains.jet.JUnit3RunnerWithInners.class)
    public static class Variables extends AbstractQuickFixMultiFileTest {
        public void testAllFilesPresentInVariables() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/quickfix/variables"), Pattern.compile("^(\\w+)\\.before\\.Main\\.kt$"), true);
        }
        
    }
    
}
