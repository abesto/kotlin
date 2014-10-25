package org.jetbrains.kotlin.gradle.tasks

import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.plugin.KSpec
import java.io.File
import org.gradle.api.GradleException
import org.jetbrains.jet.cli.common.ExitCode
import org.gradle.api.tasks.SourceTask
import org.jetbrains.kotlin.doc.KDocArguments
import java.util.HashMap
import java.util.HashSet
import org.jetbrains.kotlin.doc.KDocCompiler
import org.gradle.api.tasks.TaskAction
import org.gradle.api.file.SourceDirectorySet
import java.util.ArrayList
import org.apache.commons.io.FilenameUtils
import org.jetbrains.jet.cli.jvm.K2JVMCompiler
import org.jetbrains.jet.cli.common.arguments.K2JVMCompilerArguments;
import org.jetbrains.jet.cli.common.messages.MessageCollector
import org.jetbrains.jet.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.jet.cli.common.messages.CompilerMessageLocation
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.apache.commons.lang.StringUtils
import org.gradle.api.initialization.dsl.ScriptHandler
import org.apache.commons.io.FileUtils
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.doc.KDocConfig
import java.util.concurrent.Callable
import org.gradle.api.Project
import org.jetbrains.jet.config.Services
import org.jetbrains.jet.cli.js.K2JSCompiler
import org.jetbrains.jet.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.k2js.config.MetaInfServices
import org.jetbrains.k2js.config.ClassPathLibraryDefintionsConfig
import org.jetbrains.jet.cli.common.CLIConfigurationKeys
import org.jetbrains.jet.config.CompilerConfiguration
import org.jetbrains.jet.cli.jvm.compiler.CompileEnvironmentUtil
import org.jetbrains.jet.cli.jvm.compiler.JetCoreEnvironment
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import org.jetbrains.k2js.config.EcmaVersion
import org.gradle.api.tasks.Copy


abstract class AbstractKotlinCompile: AbstractCompile() {
    val srcDirsSources = HashSet<SourceDirectorySet>()

    public var kotlinDestinationDir : File? = getDestinationDir()

    private val logger = Logging.getLogger(this.javaClass)
    override fun getLogger() = logger

    // override setSource to track source directory sets
    override fun setSource(source: Any?) {
        srcDirsSources.clear()
        if (source is SourceDirectorySet) {
            srcDirsSources.add(source)
        }
        super.setSource(source)
    }

    // override source to track source directory sets
    override fun source(vararg sources: Any?): SourceTask? {
        for (source in sources) {
            if (source is SourceDirectorySet) {
                srcDirsSources.add(source)
            }
        }
        return super.source(sources)
    }

    fun findSrcDirRoot(file: File): File? {
        val absPath = file.getAbsolutePath()
        for (source in srcDirsSources) {
            for (root in source.getSrcDirs()) {
                val rootAbsPath = root.getAbsolutePath()
                if (FilenameUtils.directoryContains(rootAbsPath, absPath)) {
                    return root
                }
            }
        }
        return null
    }


}

public open class KotlinCompile(): AbstractKotlinCompile() {
    val compiler = K2JVMCompiler()
    public var kotlinOptions: K2JVMCompilerArguments = K2JVMCompilerArguments();

    [TaskAction]
    override fun compile() {

        getLogger().debug("Starting Kotlin compilation task")

        val args = K2JVMCompilerArguments()

        val javaSrcRoots = HashSet<File>()
        val sources = ArrayList<File>()

        // collect source directory roots for all java files to allow cross compilation
        for (file in getSource()) {
            if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("java")) {
                val javaRoot = findSrcDirRoot(file)
                if (javaRoot != null) {
                    javaSrcRoots.add(javaRoot)
                }
            } else {
                sources.add(file)
            }
        }

        if (sources.empty) {
            getLogger().warn("No Kotlin files found, skipping Kotlin compiler task")
            return
        }

        args.freeArgs = sources.map { it.getAbsolutePath() }

        if (StringUtils.isEmpty(kotlinOptions.classpath)) {
            val existingClasspathEntries =  getClasspath().filter(KSpec<File?>({ it != null && it.exists() }))
            val effectiveClassPath = (javaSrcRoots + existingClasspathEntries).makeString(File.pathSeparator)
            args.classpath = effectiveClassPath
        }

        args.destination = if (StringUtils.isEmpty(kotlinOptions.destination)) { kotlinDestinationDir?.getPath() } else { kotlinOptions.destination }

        val embeddedAnnotations = getAnnotations(getProject(), getLogger())
        val userAnnotations = (kotlinOptions.annotations ?: "").split(File.pathSeparatorChar).toList()
        val allAnnotations = if (kotlinOptions.noJdkAnnotations) userAnnotations else userAnnotations.plus(embeddedAnnotations.map {it.getPath()})
        args.annotations = allAnnotations.makeString(File.pathSeparator)

        args.noStdlib = true
        args.noJdkAnnotations = true
        args.noInline = kotlinOptions.noInline
        args.noOptimize = kotlinOptions.noOptimize
        args.noCallAssertions = kotlinOptions.noCallAssertions
        args.noParamAssertions = kotlinOptions.noParamAssertions

        val messageCollector = GradleMessageCollector(getLogger())
        getLogger().debug("Calling compiler")
        val exitCode = compiler.exec(messageCollector, Services.EMPTY, args)

        when (exitCode) {
            ExitCode.COMPILATION_ERROR -> throw GradleException("Compilation error. See log for more details")
            ExitCode.INTERNAL_ERROR -> throw GradleException("Internal compiler error. See log for more details")
        }

        getLogger().debug("Copying resulting files to classes")
        // Copy kotlin classes to all classes directory
        val outputDirFile = File(args.destination!!)
        if (outputDirFile.exists()) {
            FileUtils.copyDirectory(outputDirFile, getDestinationDir())
        }
    }
}

public open class Kotlin2JsCompile(): AbstractKotlinCompile() {
    val compiler = K2JSCompiler()
    public var kotlinOptions: K2JSCompilerArguments = K2JSCompilerArguments()

    fun addLibraryFiles(vararg fs: String) {
        kotlinOptions.libraryFiles = (kotlinOptions.libraryFiles + fs).copyToArray()
    }

    fun addLibraryFiles(vararg fs: File) {
        val strs = fs.map{it.getPath()}.copyToArray()
        addLibraryFiles(*strs)
    }

    [TaskAction]
    override fun compile() {
        getLogger().debug("Starting Kotlin to JavaScript compilation task")

        val args = K2JSCompilerArguments()

        val sources = getSource().filterNot{FilenameUtils.getExtension(it.getName()).equalsIgnoreCase("java")}

        if (sources.empty) {
            getLogger().warn("No Kotlin files found, skipping Kotlin compiler task")
            return
        }

        args.freeArgs = sources.map { it.getAbsolutePath() }
        args.outputFile = if (StringUtils.isEmpty(kotlinOptions.outputFile)) { "${kotlinDestinationDir}/app.js" } else { kotlinOptions.outputFile }
        args.outputPrefix = kotlinOptions.outputPrefix
        args.outputPostfix = kotlinOptions.outputPostfix
        args.libraryFiles = kotlinOptions.libraryFiles
        args.noInline = kotlinOptions.noInline
        args.sourceMap = kotlinOptions.sourceMap
        args.suppressWarnings = kotlinOptions.suppressWarnings
        args.target = kotlinOptions.target

        // TODO: verbose, version flags are ignored or the message collector drops those lines
        args.verbose = kotlinOptions.verbose
        args.version = kotlinOptions.version

        val outputDir = File(args.outputFile).directory
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
               throw GradleException("Failed to create output directory ${outputDir} or one of its ancestors")
            }
        }

        val messageCollector = GradleMessageCollector(getLogger())
        getLogger().debug("Calling compiler")
        val exitCode = compiler.exec(messageCollector, Services.EMPTY, args)

        when (exitCode) {
            ExitCode.COMPILATION_ERROR -> throw GradleException("Compilation error. See log for more details")
            ExitCode.INTERNAL_ERROR -> throw GradleException("Internal compiler error. See log for more details")
        }
    }
}

public open class KDoc(): SourceTask() {

    private val logger = Logging.getLogger(this.javaClass)
    override fun getLogger() = logger

    public var kdocArgs: KDocArguments = KDocArguments()

    public var destinationDir: File? = null;

    {
        // by default, output dir is not defined in options
        kdocArgs.docConfig.docOutputDir = ""
    }

    TaskAction fun generateDocs() {
        val args = KDocArguments()
        val cfg = args.docConfig

        val kdocOptions = kdocArgs.docConfig

        cfg.docOutputDir = if ((kdocOptions.docOutputDir.length == 0) && (destinationDir != null)) { destinationDir!!.path } else { kdocOptions.docOutputDir }
        cfg.title = kdocOptions.title
        cfg.sourceRootHref = kdocOptions.sourceRootHref
        cfg.projectRootDir = kdocOptions.projectRootDir
        cfg.warnNoComments = kdocOptions.warnNoComments

        cfg.packagePrefixToUrls.putAll(kdocOptions.packagePrefixToUrls)
        cfg.ignorePackages.addAll(kdocOptions.ignorePackages)
        cfg.packageDescriptionFiles.putAll(kdocOptions.packageDescriptionFiles)
        cfg.packageSummaryText.putAll(kdocOptions.packageSummaryText)

        // KDoc compiler does not accept list of files as input. Try to pass directories instead.
        args.freeArgs = getSource().map { it.getParentFile()!!.getAbsolutePath() }
        // Drop compiled sources to temp. Why KDoc compiles anything after all?!
        args.destination = getTemporaryDir()?.getAbsolutePath()

        getLogger().warn(args.freeArgs.toString())
        val embeddedAnnotations = getAnnotations(getProject(), getLogger())
        val userAnnotations = (kdocArgs.annotations ?: "").split(File.pathSeparatorChar).toList()
        val allAnnotations = if (kdocArgs.noJdkAnnotations) userAnnotations else userAnnotations.plus(embeddedAnnotations.map {it.getPath()})
        args.annotations = allAnnotations.makeString(File.pathSeparator)

        args.noStdlib = true
        args.noJdkAnnotations = true


        val compiler = KDocCompiler()

        val messageCollector = GradleMessageCollector(getLogger())
        val exitCode = compiler.exec(messageCollector, Services.EMPTY, args);

        when (exitCode) {
            ExitCode.COMPILATION_ERROR -> throw GradleException("Failed to generate kdoc. See log for more details")
            ExitCode.INTERNAL_ERROR -> throw GradleException("Internal generation error. See log for more details")
        }

    }
}

fun getAnnotations(project: Project, logger: Logger): Collection<File> {
    val annotations = project.getExtensions().getByName(DEFAULT_ANNOTATIONS) as Collection<File>

    if (!annotations.isEmpty()) {
        logger.info("using default annontations from [${annotations.map {it.getPath()}}]")
        return annotations
    } else {
        throw GradleException("Default annotations not found in Kotlin gradle plugin classpath")
    }
}

class GradleMessageCollector(val logger : Logger): MessageCollector {
    public override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation) {
        val text = with(StringBuilder()) {
            append(when (severity) {
                in CompilerMessageSeverity.VERBOSE -> "v"
                in CompilerMessageSeverity.ERRORS -> "e"
                CompilerMessageSeverity.INFO -> "i"
                CompilerMessageSeverity.WARNING -> "w"
                else -> throw IllegalArgumentException("Unknown CompilerMessageSeverity: $severity")
            })
            append(": ")

            val path = location.getPath()
            if (path != null) {
                append(path)
                append(": ")
                append("(")
                append(location.getLine())
                append(", ")
                append(location.getColumn())
                append("): ")
            }

            append(message)

            toString()
        }
        when (severity) {
            in CompilerMessageSeverity.VERBOSE -> logger.debug(text)
            in CompilerMessageSeverity.ERRORS -> logger.error(text)
            CompilerMessageSeverity.INFO -> logger.info(text)
            CompilerMessageSeverity.WARNING -> logger.warn(text)
            else -> throw IllegalArgumentException("Unknown CompilerMessageSeverity: $severity")
        }
    }
}
