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

package org.jetbrains.jet.plugin.actions.internal

import com.intellij.ide.util.PropertiesComponent

public class KotlinInternalMode {
    public class object {
        val INTERNAL_MODE_PROPERTY = "kotlin.internal.mode.enabled"

        public var enabled: Boolean
            get() = PropertiesComponent.getInstance()!!.getBoolean(
                    INTERNAL_MODE_PROPERTY,
                    System.getProperty(INTERNAL_MODE_PROPERTY) == "true"
            )
            set(value) {
                PropertiesComponent.getInstance()!!.setValue(INTERNAL_MODE_PROPERTY, value.toString())
            }
    }
}
