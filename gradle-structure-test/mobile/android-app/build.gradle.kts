plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
}

kotlin {
    androidTarget()

    sourceSets {
        androidMain.dependencies {
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "structure.mobile.androidapp"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}
