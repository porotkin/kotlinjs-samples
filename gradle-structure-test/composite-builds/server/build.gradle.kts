plugins {
    base
}

group = "dev.gradle-structure-test.server"
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
