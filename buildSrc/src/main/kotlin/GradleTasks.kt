val bundleTasks
    get() = listOf(
        // Vite
        "jsBrowserDevelopmentVite",
        "jsBrowserProductionVite",
    )

val compileTasks
    get() = listOf(
        "compileDevelopmentExecutableKotlinJs",
        "compileProductionExecutableKotlinJs",
    )

val compileSyncTasks
    get() = listOf(
        "jsDevelopmentExecutableCompileSync",
        "jsProductionExecutableCompileSync",
    )
