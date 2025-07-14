import io.github.turansky.kfc.gradle.plugin.BundlerEnvironmentExtension

plugins {
    alias(kfc.plugins.application)

    `fix-source-maps`
}

fun property(name: String): String =
    System.getenv(name)
        ?: findProperty(name)?.toString()
        ?: ""

fun BundlerEnvironmentExtension.setProperty(name: String) {
    set(name, property(name))
}

bundlerEnvironment {
    setProperty("SENTRY_AUTH_TOKEN")
    setProperty("SENTRY_PROJECT")
    setProperty("SENTRY_ORG")
    setProperty("SENTRY_DSN")
}

dependencies {
    jsMainImplementation(kotlinWrappers.emotion.react)
    jsMainImplementation(kotlinWrappers.browser)
    jsMainImplementation(kotlinWrappers.js)
    jsMainImplementation(kotlinWrappers.react)
    jsMainImplementation(kotlinWrappers.reactDom)

    jsMainImplementation(devNpm("@sentry/vite-plugin", "latest"))
    jsMainImplementation(devNpm("@sentry/react", "latest"))
}
