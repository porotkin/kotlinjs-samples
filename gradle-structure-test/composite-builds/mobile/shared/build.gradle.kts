plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(commonProjects.client)
        }
    }
}

android {
    namespace = "structure.mobile.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}
