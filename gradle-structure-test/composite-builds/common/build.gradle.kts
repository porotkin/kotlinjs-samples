plugins {
    id("subproject-catalog-generation") apply false
}

group = "dev.gradle-structure-test.common"
version = libs.versions.project.get()

subprojects {
    group = rootProject.group
    version = rootProject.version
}

apply(plugin = "subproject-catalog-generation")

gradle.projectsEvaluated {
    tasks.named("build") {
        dependsOn(
            tasks.named("exportSubprojectCatalog"),
            subprojects.map { it.tasks.named("build") },
        )
    }
}
