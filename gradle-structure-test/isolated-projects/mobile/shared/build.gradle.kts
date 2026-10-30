plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val iosMain by creating {
            dependsOn(commonMain.get())
        }
        iosArm64Main { dependsOn(iosMain) }
        iosSimulatorArm64Main { dependsOn(iosMain) }

        androidMain.dependencies {
            implementation(projects.common.client)
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
