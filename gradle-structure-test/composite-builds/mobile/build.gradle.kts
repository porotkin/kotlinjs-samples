plugins {
    base

    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

group = "dev.gradle-structure-test.mobile"
version = libs.versions.project.get()

subprojects {
    group = rootProject.group
    version = rootProject.version
}

gradle.projectsEvaluated {
    tasks.named("build") {
        dependsOn(
            subprojects.map { it.tasks.named("build") },
        )
    }
}
