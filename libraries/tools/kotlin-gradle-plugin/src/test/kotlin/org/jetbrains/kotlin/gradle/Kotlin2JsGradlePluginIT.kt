package org.jetbrains.kotlin.gradle

import org.junit.Test
import org.jetbrains.kotlin.gradle.BaseGradleIT.Project


class Kotlin2JsGradlePluginIT: BaseGradleIT() {
    val project = Project("kotlin2JsProject", "1.6")

    Test fun testBuildAndClean() {
        project.build("build") {
            assertSuccessful()
            assertReportExists()

            assertContains(":libraryProject:jarSources\n",
                           ":mainProject:copyKotlinSourcesForSourceMap\n", ":mainProject:rewritePathsInSourceMap\n",
                           ":mainProject:compileKotlin2Js\n",    ":mainProject:copyKotlinJs\n",
                           ":libraryProject:compileKotlin2Js\n", ":libraryProject:copyKotlinJs\n")

            listOf("mainProject/web/js/app.js",                  "mainProject/web/js/lib/kotlin.js",
                   "libraryProject/build/kotlin2js/main/app.js", "libraryProject/build/kotlin2js/main/kotlin.js",
                   "mainProject/web/js/app.js.map", "mainProject/web/js/example/main.kt"
            ).forEach{assertFileExists(it)}

            assertFileContains("libraryProject/build/kotlin2js/main/app.js", "Counter: Kotlin.createClass")
            // Should be updated to `new _.example.library.Counter` once namespaced imports from libraryFiles are implemented
            assertFileContains("mainProject/web/js/app.js", "var counter = new Counter(counterText);")
            assertFileContains("mainProject/web/js/app.js.map", "sources\":[\"example/main.kt\"]")
        }

        project.build("build") {
            assertSuccessful()
            assertContains(":mainProject:compileKotlin2Js UP-TO-DATE", ":libraryProject:compileTestKotlin2Js UP-TO-DATE",
                           ":mainProject:copyKotlinSourcesForSourceMap UP-TO-DATE")
        }

        project.build("clean") {
            assertSuccessful()
            assertReportExists()
            assertContains(":mainProject:cleanCompileKotlin2Js\n", ":mainProject:cleanCopyKotlinJs\n",
                           ":mainProject:cleanCopyKotlinSourcesForSourceMap\n")
            assertNoSuchFile("mainProject/web/js/app.js")
            assertNoSuchFile("mainProject/web/js/lib/kotlin.js")

            // Test that we don't accidentally remove the containing directory
            // This would fail if we used the default clean task of the copy task
            assertFileExists("mainProject/web/js/lib")

            assertNoSuchFile("main/project/web/js/app.js.map")
            assertNoSuchFile("main/project/web/js/example/main.kt")
        }

        project.build("clean") {
            assertSuccessful()
            assertReportExists()
            assertContains(":mainProject:cleanCompileKotlin2Js UP-TO-DATE", ":mainProject:cleanCopyKotlinJs UP-TO-DATE",
                    ":mainProject:cleanCopyKotlinSourcesForSourceMap UP-TO-DATE")
        }
    }
}

