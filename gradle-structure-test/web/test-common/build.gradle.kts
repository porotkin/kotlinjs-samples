plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    js {
        browser()
        nodejs()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(commonProjects.client)
            }
        }
    }
}
