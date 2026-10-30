plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm()

    js {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
        }
    }
}

if (providers.systemProperty("build.mobile.enabled").orNull == "true") {
    apply(plugin = "android-library-conventions")
}
