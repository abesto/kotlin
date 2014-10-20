package org.jetbrains.kotlin.gradle

import org.junit.Test
import org.jetbrains.kotlin.gradle.BaseGradleIT.Project


class Kotlin2JsGradlePluginIT: BaseGradleIT() {
    val project = Project("kotlin2JsProject", "1.6")

    Test fun testBuild() {
        project.build("build") {
            assertSuccessful()
            assertReportExists()

            assertContains(":libraryProject:jarSources",
                           ":mainProject:compileKotlin2Js",    ":mainProject:copyKotlinJs",
                           ":libraryProject:compileKotlin2Js", ":libraryProject:copyKotlinJs")

            listOf("mainProject/web/js/app.js",                  "mainProject/web/js/lib/kotlin.js",
                   "libraryProject/build/kotlin2js/main/app.js", "libraryProject/build/kotlin2js/main/kotlin.js"
            ).forEach{assertFileExists(it)}

            assertFileContains("libraryProject/build/kotlin2js/main/app.js", "Counter: Kotlin.createClass")
            // Should be updated to `new _.example.library.Counter` once namespaced imports from libraryFiles are implemented
            assertFileContains("mainProject/web/js/app.js", "var counter = new Counter(counterText);")
        }

        project.build("build") {
            assertSuccessful()
            assertContains(":mainProject:compileKotlin2Js UP-TO-DATE", ":libraryProject:compileTestKotlin2Js UP-TO-DATE")
        }
    }
}

