plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    android {
        namespace = "structure.common.${project.name}"
        compileSdk = 36
        minSdk = 24
    }
}
