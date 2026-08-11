plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { target ->
        target.binaries.framework {
            baseName = "MobileUmbrella"
            isStatic = true
            export(projects.shared)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.shared)
        }
    }
}
