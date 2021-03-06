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

package org.jetbrains.jet.plugin.codeInsight;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiModificationTrackerImpl;
import org.jetbrains.jet.InTextDirectivesUtils;
import org.jetbrains.jet.plugin.JetLightCodeInsightFixtureTestCase;
import org.jetbrains.jet.plugin.PluginTestCaseBase;

import java.io.File;
import java.io.IOException;

public abstract class AbstractOutOfBlockModificationTest extends JetLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.setTestDataPath(PluginTestCaseBase.getTestDataPathBase() + "/codeInsight/outOfBlock");
    }

    @Override
    protected String getTestDataPath() {
        return PluginTestCaseBase.getTestDataPathBase() + "/codeInsight/outOfBlock";
    }

    protected void doTest(String path) throws IOException {
        myFixture.configureByFile(path);

        boolean expectedOutOfBlock = getExpectedOutOfBlockResult();

        PsiModificationTrackerImpl tracker =
                (PsiModificationTrackerImpl) PsiManager.getInstance(myFixture.getProject()).getModificationTracker();

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assertNotNull("Should be valid element", element);

        long oobBeforeType = tracker.getOutOfCodeBlockModificationCount();
        long modificationCountBeforeType = tracker.getModificationCount();

        myFixture.type(getStringToType());
        PsiDocumentManager.getInstance(myFixture.getProject()).commitDocument(myFixture.getDocument(myFixture.getFile()));

        long oobAfterCount = tracker.getOutOfCodeBlockModificationCount();
        long modificationCountAfterType = tracker.getModificationCount();

        assertTrue("Modification tracker should always be changed after type", modificationCountBeforeType != modificationCountAfterType);

        assertEquals("Result for out of block test is differs from expected on element in file:\n" + FileUtil.loadFile(new File(path)),
                     expectedOutOfBlock, oobBeforeType != oobAfterCount);
    }

    private String getStringToType() {
        String text = myFixture.getDocument(myFixture.getFile()).getText();
        String typeDirectives = InTextDirectivesUtils.findStringWithPrefixes(text, "TYPE:");

        return typeDirectives != null ? StringUtil.unescapeStringCharacters(typeDirectives) : "a";
    }

    private boolean getExpectedOutOfBlockResult() {
        String text = myFixture.getDocument(myFixture.getFile()).getText();

        boolean expectedOutOfBlock = false;
        if (text.startsWith("// TRUE")) {
            expectedOutOfBlock = true;
        }
        else if (text.startsWith("// FALSE")) {
            expectedOutOfBlock = false;
        }
        else {
            fail("Expectation of code block result test should be configured with " +
                 "\"// TRUE\" or \"// FALSE\" directive in the beginning of the file");
        }
        return expectedOutOfBlock;
    }
}
