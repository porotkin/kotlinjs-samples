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
        commonMain {
            dependencies {
                implementation(projects.client)
            }
        }
    }
}
