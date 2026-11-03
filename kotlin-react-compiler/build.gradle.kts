import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kfc.application)
    alias(libs.plugins.seskar)
    alias(libs.plugins.kotlin.jsPlainObjects)
}

val irPluginCompilerClasspath = configurations.create("irPluginCompilerClasspath") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val compileReactCompilerIrPlugin = tasks.register<JavaExec>("compileReactCompilerIrPlugin") {
    val sourceDir = layout.projectDirectory.dir("compiler-plugin/src/main/kotlin")
    val classesDir = layout.buildDirectory.dir("compiler-plugin/classes")
    val compilerCp = objects.fileCollection().from(irPluginCompilerClasspath)

    inputs.dir(sourceDir)
    outputs.dir(classesDir)

    classpath = compilerCp
    mainClass = "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler"

    doFirst {
        val outDir = classesDir.get().asFile
        outDir.deleteRecursively()
        outDir.mkdirs()
    }

    argumentProviders.add(
        CommandLineArgumentProvider {
            val ktFiles = sourceDir.asFileTree.matching { include("**/*.kt") }.files.map { it.absolutePath }
            listOf(
                "-no-stdlib",
                "-classpath",
                compilerCp.asPath,
                "-d",
                classesDir.get().asFile.absolutePath,
            ) + ktFiles
        },
    )
}

val reactCompilerIrPluginJar = tasks.register<Jar>("reactCompilerIrPluginJar") {
    archiveBaseName = "react-compiler-ir-plugin"
    destinationDirectory = layout.buildDirectory.dir("compiler-plugin/libs")
    from(compileReactCompilerIrPlugin)
    from(layout.projectDirectory.dir("compiler-plugin/src/main/resources"))
}

dependencies {
    irPluginCompilerClasspath(kotlin("compiler-embeddable"))
    irPluginCompilerClasspath(kotlin("stdlib"))

    "kotlinCompilerPluginClasspathJsMain"(files(reactCompilerIrPluginJar))
    "kotlinCompilerPluginClasspathJsTest"(files(reactCompilerIrPluginJar))

    jsMainImplementation(libs.coroutines.core)
    jsMainImplementation(kotlinWrappers.js)
    jsMainImplementation(kotlinWrappers.react)
    jsMainImplementation(kotlinWrappers.reactDom)

    jsMainImplementation(devNpm("@babel/core", "^7.26.0"))
    jsMainImplementation(devNpm("babel-plugin-react-compiler", "^1.0.0"))

    jsTestImplementation(libs.kotlin.testJs)
}
