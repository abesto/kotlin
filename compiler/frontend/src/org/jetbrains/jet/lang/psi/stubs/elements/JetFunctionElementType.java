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

package org.jetbrains.jet.lang.psi.stubs.elements;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.jet.lang.psi.psiUtil.PsiUtilPackage;
import org.jetbrains.jet.lang.psi.stubs.KotlinFunctionStub;
import org.jetbrains.jet.lang.psi.stubs.impl.KotlinFunctionStubImpl;
import org.jetbrains.jet.lang.resolve.lazy.ResolveSessionUtils;
import org.jetbrains.jet.lang.resolve.name.FqName;

import java.io.IOException;

public class JetFunctionElementType extends JetStubElementType<KotlinFunctionStub, JetNamedFunction> {

    public JetFunctionElementType(@NotNull @NonNls String debugName) {
        super(debugName, JetNamedFunction.class, KotlinFunctionStub.class);
    }

    @Override
    public KotlinFunctionStub createStub(@NotNull JetNamedFunction psi, @NotNull StubElement parentStub) {
        boolean isTopLevel = psi.getParent() instanceof JetFile;
        boolean isExtension = psi.getReceiverTypeReference() != null;
        FqName fqName = ResolveSessionUtils.safeFqNameForLazyResolve(psi);
        boolean hasBlockBody = psi.hasBlockBody();
        boolean hasBody = psi.hasBody();
        return new KotlinFunctionStubImpl(parentStub, StringRef.fromString(psi.getName()), isTopLevel, fqName,
                                          isExtension, hasBlockBody, hasBody, psi.hasTypeParameterListBeforeFunctionName(),
                                          PsiUtilPackage.isProbablyNothing(psi.getTypeReference()));
    }

    @Override
    public void serialize(@NotNull KotlinFunctionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeBoolean(stub.isTopLevel());

        FqName fqName = stub.getFqName();
        dataStream.writeName(fqName != null ? fqName.asString() : null);

        dataStream.writeBoolean(stub.isExtension());
        dataStream.writeBoolean(stub.hasBlockBody());
        dataStream.writeBoolean(stub.hasBody());
        dataStream.writeBoolean(stub.hasTypeParameterListBeforeFunctionName());
        dataStream.writeBoolean(stub.isProbablyNothingType());
    }

    @NotNull
    @Override
    public KotlinFunctionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        boolean isTopLevel = dataStream.readBoolean();

        StringRef fqNameAsString = dataStream.readName();
        FqName fqName = fqNameAsString != null ? new FqName(fqNameAsString.toString()) : null;

        boolean isExtension = dataStream.readBoolean();
        boolean hasBlockBody = dataStream.readBoolean();
        boolean hasBody = dataStream.readBoolean();
        boolean hasTypeParameterListBeforeFunctionName = dataStream.readBoolean();
        boolean probablyNothingType = dataStream.readBoolean();

        return new KotlinFunctionStubImpl(parentStub, name, isTopLevel, fqName, isExtension, hasBlockBody, hasBody,
                                          hasTypeParameterListBeforeFunctionName, probablyNothingType);
    }

    @Override
    public void indexStub(@NotNull KotlinFunctionStub stub, @NotNull IndexSink sink) {
        StubIndexServiceFactory.getInstance().indexFunction(stub, sink);
    }
}
