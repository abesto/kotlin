package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.internal.KotlinSourceSetImpl
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.internal.KotlinSourceSet
import org.gradle.api.specs.Spec
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KDoc
import java.io.File
import java.util.concurrent.Callable
import org.gradle.api.Action
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.logging.Logging
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.internal.DefaultDomainObjectSet
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.jet.cli.common.arguments.K2JVMCompilerArguments;
import java.util.ArrayList
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.api.ApkVariant
import com.android.builder.model.BuildType
import com.android.build.gradle.api.TestVariant
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import java.util.HashSet
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.initialization.dsl.ScriptHandler
import org.jetbrains.kotlin.gradle.plugin.android.AndroidGradleWrapper
import javax.inject.Inject
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.gradle.api.tasks.Copy
import org.gradle.api.file.SourceDirectorySet
import kotlin.properties.Delegates
import org.gradle.api.tasks.Delete
import org.codehaus.groovy.runtime.MethodClosure
import org.jetbrains.kotlin.gradle.tasks.RewritePathsInSourceMap
import groovy.lang.Closure

val DEFAULT_ANNOTATIONS = "org.jebrains.kotlin.gradle.defaultAnnotations"


abstract class KotlinSourceSetProcessor<T: AbstractCompile>(val project: ProjectInternal,
                                                            val javaBasePlugin: JavaBasePlugin,
                                                            val sourceSet: SourceSet,
                                                            val pluginName: String,
                                                            val compileTaskNameSuffix: String,
                                                            val taskDescription: String,
                                                            val compilerClass: Class<T>) {
    abstract protected fun doTargetSpecificProcessing()
    val logger = Logging.getLogger(this.javaClass)

    protected val sourceSetName: String = sourceSet.getName()
    protected val sourceRootDir: String = "src/${sourceSetName}/kotlin"
    protected val absoluteSourceRootDir: String = project.getProjectDir().getPath() + "/" + sourceRootDir
    protected val kotlinSourceSet: KotlinSourceSet? by Delegates.lazy { createKotlinSourceSet() }
    protected val kotlinDirSet: SourceDirectorySet? by Delegates.lazy { createKotlinDirSet() }
    protected val kotlinTask: T by Delegates.lazy { createKotlinCompileTask() }
    protected val kotlinTaskName: String by Delegates.lazy { kotlinTask.getName() }

    public fun run() {
        if (kotlinSourceSet == null || kotlinDirSet == null) {
            return
        }
        addSourcesToKotlinDirSet()
        commonTaskConfiguration()
        doTargetSpecificProcessing()
    }

    open protected fun createKotlinSourceSet(): KotlinSourceSet? =
            if (sourceSet is HasConvention) {
                logger.debug("Creating KotlinSourceSet for source set ${sourceSet}")
                val kotlinSourceSet = KotlinSourceSetImpl(sourceSet.getName(), project.getFileResolver())
                sourceSet.getConvention().getPlugins().put(pluginName, kotlinSourceSet)
                kotlinSourceSet
            } else {
                null
            }

    open protected fun createKotlinDirSet(): SourceDirectorySet? {
        val srcDir = project.file(sourceRootDir)
        logger.debug("Creating Kotlin SourceDirectorySet for source set ${kotlinSourceSet} with src dir ${srcDir}")
        val kotlinDirSet = kotlinSourceSet?.getKotlin()
        kotlinDirSet?.srcDir(srcDir)
        return kotlinDirSet
    }

    open protected fun addSourcesToKotlinDirSet() {
        logger.debug("Adding Kotlin SourceDirectorySet ${kotlinDirSet} to source set ${sourceSet}")
        sourceSet.getAllJava()?.source(kotlinDirSet)
        sourceSet.getAllSource()?.source(kotlinDirSet)
        sourceSet.getResources()?.getFilter()?.exclude(KSpec({ elem ->
            kotlinDirSet!!.contains(elem.getFile())
        }))
    }

    open protected fun createKotlinCompileTask(): T {
        val name = sourceSet.getCompileTaskName(compileTaskNameSuffix)
        logger.debug("Creating kotlin compile task $name with class $compilerClass")
        return project.getTasks().create(name, compilerClass)
    }

    open protected fun commonTaskConfiguration() {
        javaBasePlugin.configureForSourceSet(sourceSet, kotlinTask)
        kotlinTask.setDescription(taskDescription)
        kotlinTask.source(kotlinDirSet)
    }
}

abstract class AbstractKotlinPlugin [Inject] (val scriptHandler: ScriptHandler): Plugin<Project> {
    abstract fun buildSourceSetProcessor(project: ProjectInternal, javaBasePlugin: JavaBasePlugin, sourceSet: SourceSet): KotlinSourceSetProcessor<*>

    public override fun apply(project: Project) {
        val javaBasePlugin = project.getPlugins().apply(javaClass<JavaBasePlugin>())
        val javaPluginConvention = project.getConvention().getPlugin(javaClass<JavaPluginConvention>())

        project.getPlugins().apply(javaClass<JavaPlugin>())

        configureSourceSetDefaults(project as ProjectInternal, javaBasePlugin, javaPluginConvention)
        configureKDoc(project, javaPluginConvention)

        val version = project.getProperties()["kotlin.gradle.plugin.version"] as String
        project.getExtensions().add(DEFAULT_ANNOTATIONS, GradleUtils(scriptHandler).resolveDependencies("org.jetbrains.kotlin:kotlin-jdk-annotations:$version"))
    }

    open protected fun configureSourceSetDefaults(project: ProjectInternal,
                                           javaBasePlugin: JavaBasePlugin,
                                           javaPluginConvention: JavaPluginConvention) {
        javaPluginConvention.getSourceSets()?.all(object : Action<SourceSet> {
            override fun execute(sourceSet: SourceSet?) {
                if (sourceSet != null) {
                    buildSourceSetProcessor(project, javaBasePlugin, sourceSet).run()
                }
            }
        })
    }

    open protected fun configureKDoc(project: Project, javaPluginConvention: JavaPluginConvention) {
        val mainSourceSet = javaPluginConvention.getSourceSets()?.findByName(SourceSet.MAIN_SOURCE_SET_NAME) as HasConvention?

        if (mainSourceSet != null) {
            val kdoc = project.getTasks().create(KDOC_TASK_NAME, javaClass<KDoc>())!!

            kdoc.setDescription("Generates KDoc API documentation for the main source code.")
            kdoc.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP)
            kdoc.setSource(mainSourceSet.getConvention().getExtensionsAsDynamicObject().getProperty("kotlin"))
        }

        project.getTasks().withType(javaClass<KDoc>(), object : Action<KDoc> {
            override fun execute(task: KDoc?) {
                task!!.destinationDir = File(javaPluginConvention.getDocsDir(), "kdoc")
            }
        })
    }

    public val KDOC_TASK_NAME: String = "kdoc"
}


open class KotlinPlugin [Inject] (scriptHandler: ScriptHandler):AbstractKotlinPlugin(scriptHandler) {
    override fun buildSourceSetProcessor(project: ProjectInternal, javaBasePlugin: JavaBasePlugin, sourceSet: SourceSet): KotlinSourceSetProcessor<KotlinCompile> =

        object : KotlinSourceSetProcessor<KotlinCompile>(
                project, javaBasePlugin, sourceSet,
                pluginName = "kotlin",
                compileTaskNameSuffix = "kotlin",
                taskDescription = "Compiles the $sourceSet.kotlin.",
                compilerClass = javaClass()) {

            override fun doTargetSpecificProcessing() {
                // store kotlin classes in separate directory. They will serve as class-path to java compiler
                val kotlinOutputDir = File(project.getBuildDir(), "kotlin-classes/${sourceSetName}")
                kotlinTask.kotlinDestinationDir = kotlinOutputDir

                val javaTask = project.getTasks().findByName(sourceSet.getCompileJavaTaskName()) as AbstractCompile?

                if (javaTask != null) {
                    javaTask.dependsOn(kotlinTaskName)
                    val javacClassPath = javaTask.getClasspath() + project.files(kotlinTask.kotlinDestinationDir);
                    javaTask.setClasspath(javacClassPath)
                }
            }
        }
}


open class Kotlin2JsPlugin [Inject] (scriptHandler: ScriptHandler): AbstractKotlinPlugin(scriptHandler) {
    override fun buildSourceSetProcessor(project: ProjectInternal, javaBasePlugin: JavaBasePlugin, sourceSet: SourceSet): KotlinSourceSetProcessor<Kotlin2JsCompile> =
            object : KotlinSourceSetProcessor<Kotlin2JsCompile>(
                    project, javaBasePlugin, sourceSet,
                    pluginName = "kotlin2js",
                    taskDescription = "Compiles the kotlin sources in $sourceSet to JavaScript.",
                    compileTaskNameSuffix = "kotlin2Js",
                    compilerClass = javaClass<Kotlin2JsCompile>()) {

                val copyKotlinJsTaskName = sourceSet.getTaskName("copy", "kotlinJs")
                val clean = project.getTasks().findByName("clean")
                val compileJava = project.getTasks().findByName(sourceSet.getCompileJavaTaskName()) as AbstractCompile?

                val defaultKotlinDestinationDir = File(project.getBuildDir(), "kotlin2js/${sourceSetName}")
                private fun kotlinTaskDestinationDir(): File? = kotlinTask.kotlinDestinationDir

                private fun kotlinJsTargetPath(): String {
                    val copyTask = project.getTasks().getByName(copyKotlinJsTaskName) as Copy
                    return "${copyTask.getDestinationDir()}/kotlin.js"
                }

                private fun kotlinSourcePathsForSourceMap() = sourceSet.getAllSource()
                        .map { it.path }
                        .filter { it.endsWith(".kt") }
                        .map { it.replace(absoluteSourceRootDir, kotlinTask.sourceMapDestinationDir().getPath()) }

                private fun shouldGenerateSourceMap() = kotlinTask.kotlinOptions.sourceMap

                override fun doTargetSpecificProcessing() {
                    val version = project.getProperties()["kotlin.gradle.plugin.version"] as String
                    val jsLibraryJar = GradleUtils(scriptHandler).resolveDependencies("org.jetbrains.kotlin:kotlin-js-library:$version").map{it.getAbsolutePath()}[0]

                    kotlinTask.kotlinOptions.libraryFiles = array(jsLibraryJar)
                    kotlinTask.kotlinDestinationDir = defaultKotlinDestinationDir

                    compileJava?.dependsOn(kotlinTaskName)
                    clean?.dependsOn("clean" + kotlinTaskName.capitalize())

                    createCopyKotlinJsTask(jsLibraryJar)
                    createCopyKotlinSourcesForSourceMapTask()
                    createRewritePathsInSourceMapTask()
                    createCleanSourceMapTask()
                }

                private fun createCopyKotlinJsTask(jsLibraryJar: String) {
                    val copyKotlinJsTaskName = sourceSet.getTaskName("copy", "kotlinJs")

                    val copyKotlinJsTask = project.getTasks().create(copyKotlinJsTaskName, javaClass<Copy>())
                    copyKotlinJsTask.from(project.zipTree(jsLibraryJar))
                    copyKotlinJsTask.into(MethodClosure(this, "kotlinTaskDestinationDir"))
                    copyKotlinJsTask.include("kotlin.js")
                    compileJava?.dependsOn(copyKotlinJsTaskName)

                    val cleanTaskName = "clean" + copyKotlinJsTaskName.capitalize()
                    val cleanTask = project.getTasks().create(cleanTaskName, javaClass<Delete>())
                    cleanTask.delete(MethodClosure(this, "kotlinJsTargetPath"))
                    clean?.dependsOn(cleanTaskName)
                }

                private fun createCopyKotlinSourcesForSourceMapTask() {
                    val copyTaskName = sourceSet.getTaskName("copy", "kotlinSourcesForSourceMap")
                    val copyTask = project.getTasks().create(copyTaskName, javaClass<Copy>())
                    copyTask.onlyIf { kotlinTask.kotlinOptions.sourceMap }
                    sourceSet.getAllSource()
                    copyTask.from(sourceRootDir)
                    copyTask.into(MethodClosure(kotlinTask, "sourceMapDestinationDir"))
                    copyTask.getOutputs().file(MethodClosure(this, "kotlinSourcePathsForSourceMap"))
                    compileJava?.dependsOn(copyTask)

                    val cleanTask = project.getTasks().create("clean" + copyTaskName.capitalize(), javaClass<Delete>())
                    cleanTask.onlyIf { kotlinTask.kotlinOptions.sourceMap }
                    cleanTask.delete(MethodClosure(this, "kotlinSourcePathsForSourceMap"))
                    clean?.dependsOn(cleanTask.getName())
                 }

                private fun createRewritePathsInSourceMapTask() {
                    val taskName = sourceSet.getTaskName("rewrite", "pathsInSourceMap")
                    val task = project.getTasks().create(taskName, javaClass<RewritePathsInSourceMap>())
                    task.sourceMapPath = { kotlinTask.outputFile() + ".map" }
                    task.sourceRootDir = { absoluteSourceRootDir }
                    task.onlyIf { kotlinTask.kotlinOptions.sourceMap }
                    task.dependsOn(kotlinTaskName)
                    compileJava?.dependsOn(taskName)
                    project.getTasks().add(task)
                }

                private fun createCleanSourceMapTask() {
                    val taskName = sourceSet.getTaskName("clean", "sourceMap")
                    val task = project.getTasks().create(taskName, javaClass<Delete>())
                    task.onlyIf { kotlinTask.kotlinOptions.sourceMap }
                    task.delete(object : Closure<String>(this) {
                        override fun call(): String? = kotlinTask.outputFile() + ".map"
                    })
                    clean?.dependsOn(taskName)
                }
            }
}


open class KotlinAndroidPlugin [Inject] (val scriptHandler: ScriptHandler): Plugin<Project> {

    val log = Logging.getLogger(this.javaClass)

    public override fun apply(p0: Project) {

        val project = p0 as ProjectInternal
        val ext = project.getExtensions().getByName("android") as BaseExtension

        ext.getSourceSets().all(object : Action<AndroidSourceSet> {
            override fun execute(sourceSet: AndroidSourceSet?) {
                if (sourceSet is HasConvention) {
                    val sourceSetName = sourceSet.getName()
                    val kotlinSourceSet = KotlinSourceSetImpl( sourceSetName, project.getFileResolver())
                    sourceSet.getConvention().getPlugins().put("kotlin", kotlinSourceSet)
                    val kotlinDirSet = kotlinSourceSet.getKotlin()
                    kotlinDirSet.srcDir(project.file("src/${sourceSetName}/kotlin"))


                    /*TODO: before 0.11 gradle android plugin there was:
                      sourceSet.getAllJava().source(kotlinDirSet)
                      sourceSet.getAllSource().source(kotlinDirSet)
                      AndroidGradleWrapper.getResourceFilter(sourceSet)?.exclude(KSpec({ elem ->
                        kotlinDirSet.contains(elem.getFile())
                      }))
                     but those methods were removed so commented as temporary hack*/

                    project.getLogger().debug("Created kotlin sourceDirectorySet at ${kotlinDirSet.getSrcDirs()}")
                }
            }
        })

        (ext as ExtensionAware).getExtensions().add("kotlinOptions", K2JVMCompilerArguments())

        project.afterEvaluate( { (project: Project?): Unit ->
            if (project != null) {
                val testVariants = ext.getTestVariants()!!
                processVariants(testVariants, project, ext)
                if (ext is AppExtension) {
                    val appVariants = ext.getApplicationVariants()!!
                    processVariants(appVariants, project, ext)
                }

                if (ext is LibraryExtension) {
                    val libVariants = ext.getLibraryVariants()!!;
                    processVariants(libVariants, project, ext)
                }
            }

        })
        val version = project.getProperties()["kotlin.gradle.plugin.version"] as String
        project.getExtensions().add(DEFAULT_ANNOTATIONS, GradleUtils(scriptHandler!!).resolveDependencies("org.jetbrains.kotlin:kotlin-android-sdk-annotations:$version"));
    }

    private fun processVariants(variants: DefaultDomainObjectSet<out BaseVariant>, project: Project, androidExt: BaseExtension): Unit {
        val logger = project.getLogger()
        val kotlinOptions = getExtention<K2JVMCompilerArguments>(androidExt, "kotlinOptions")
        val sourceSets = androidExt.getSourceSets()
        //TODO: change to BuilderConstants.MAIN - it was relocated in 0.11 plugin
        val mainSourceSet = sourceSets.getByName("main")
        val testSourceSet = try {
            sourceSets.getByName("instrumentTest")
        } catch (e: UnknownDomainObjectException) {
            sourceSets.getByName("androidTest")
        }

        for (variant in variants) {
            if (variant is LibraryVariant || variant is ApkVariant) {
                val buildTypeSourceSetName = AndroidGradleWrapper.getVariantName(variant)

                logger.debug("Variant build type is [$buildTypeSourceSetName]")
                val buildTypeSourceSet : AndroidSourceSet? = sourceSets.findByName(buildTypeSourceSetName)

                val javaTask = variant.getJavaCompile()!!
                val variantName = variant.getName()

                val kotlinTaskName = "compile${variantName.capitalize()}Kotlin"
                val kotlinTask = project.getTasks().create(kotlinTaskName, javaClass<KotlinCompile>())
                kotlinTask.kotlinOptions = kotlinOptions


                // store kotlin classes in separate directory. They will serve as class-path to java compiler
                val kotlinOutputDir = File(project.getBuildDir(), "tmp/kotlin-classes/${variantName}")
                kotlinTask.kotlinDestinationDir = kotlinOutputDir;
                kotlinTask.setDestinationDir(javaTask.getDestinationDir())
                kotlinTask.setDescription("Compiles the ${variantName} kotlin.")
                kotlinTask.setClasspath(javaTask.getClasspath())
                kotlinTask.setDependsOn(javaTask.getDependsOn())

                val javaSourceList = ArrayList<Any?>()

                if (variant is TestVariant) {
                    javaSourceList.addAll(AndroidGradleWrapper.getJavaSrcDirs(testSourceSet))
                    val testKotlinSource = getExtention<KotlinSourceSet>(testSourceSet, "kotlin")
                    kotlinTask.source(testKotlinSource.getKotlin())
                } else {
                    javaSourceList.addAll(AndroidGradleWrapper.getJavaSrcDirs(mainSourceSet))
                    val mainKotlinSource = getExtention<KotlinSourceSet>(mainSourceSet, "kotlin")
                    kotlinTask.source(mainKotlinSource.getKotlin())
                }

                if (null != buildTypeSourceSet) {
                    javaSourceList.add(AndroidGradleWrapper.getJavaSrcDirs(buildTypeSourceSet))
                    kotlinTask.source(getExtention<KotlinSourceSet>(buildTypeSourceSet, "kotlin").getKotlin())
                }
                javaSourceList.add(Callable<File?>{ variant.getProcessResources().getSourceOutputDir() })
                javaSourceList.add(Callable<File?>{ variant.getGenerateBuildConfig()?.getSourceOutputDir() })
                javaSourceList.add(Callable<File?>{ variant.getAidlCompile().getSourceOutputDir() })
                javaSourceList.add(Callable<File?>{ variant.getRenderscriptCompile().getSourceOutputDir() })

                if (variant is ApkVariant) {
                    for (flavourName in AndroidGradleWrapper.getProductFlavorsNames(variant)) {
                       val flavourSourceSetName = buildTypeSourceSetName + flavourName
                        val flavourSourceSet : AndroidSourceSet? = sourceSets.findByName(flavourSourceSetName)
                        if (flavourSourceSet != null) {
                            javaSourceList.add(AndroidGradleWrapper.getJavaSrcDirs(flavourSourceSet))
                            kotlinTask.source((buildTypeSourceSet as ExtensionAware).getExtensions().getByName("kotlin"))
                        }
                    }
                }

                kotlinTask.doFirst({ task  ->
                    var plugin = project.getPlugins().findPlugin("android")
                    if (null == plugin) {
                        plugin = project.getPlugins().findPlugin("android-library")
                    }
                    val basePlugin : BasePlugin = plugin as BasePlugin
                    val javaSources = project.files(javaSourceList)
                    val androidRT = project.files(AndroidGradleWrapper.getRuntimeJars(basePlugin))
                    val fullClasspath = (javaTask.getClasspath() + (javaSources + androidRT)) - project.files(kotlinTask.kotlinDestinationDir)
                    (task as AbstractCompile).setClasspath(fullClasspath)
                })

                javaTask.dependsOn(kotlinTaskName)
                val javacClassPath = javaTask.getClasspath() + project.files(kotlinTask.kotlinDestinationDir);
                javaTask.setClasspath(javacClassPath)
            }
        }
    }

    fun <T> getExtention(obj: Any, extensionName: String): T {
        if (obj is ExtensionAware) {
            val result = obj.getExtensions().findByName(extensionName)
            if (result != null) {
                return result as T
            }
        }
        val result = (obj as HasConvention).getConvention().getPlugins()[extensionName]
        return result as T
    }

}

open class KSpec<T: Any?>(val predicate: (T) -> Boolean): Spec<T> {
    public override fun isSatisfiedBy(p0: T?): Boolean {
        return p0 != null && predicate(p0)
    }
}

open class GradleUtils(val scriptHandler: ScriptHandler) {
    public fun resolveDependencies(vararg coordinates: String): Collection<File> {
        val dependencyHandler : DependencyHandler = scriptHandler.getDependencies()
        val configurationsContainer : ConfigurationContainer = scriptHandler.getConfigurations()

        val deps = coordinates.map { dependencyHandler.create(it) }
        val configuration = configurationsContainer.detachedConfiguration(*deps.copyToArray())

        return configuration.getResolvedConfiguration().getFiles(KSpec({ dep -> true }))!!
    }
}

