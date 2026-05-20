plugins {
    id("subproject-catalog-publishing") apply false
}

group = "dev.gradle-structure-test.common"
version = libs.versions.project.get()

subprojects {
    group = rootProject.group
    version = rootProject.version
}

apply(plugin = "subproject-catalog-publishing")

gradle.projectsEvaluated {
    tasks.named("build") {
        dependsOn(
            tasks.named("publishSubprojectCatalogPublicationToSubprojectCatalogLocalRepository"),
            subprojects.map { it.tasks.named("build") },
        )
    }
}
