import structure.catalog.AliasHelper
import structure.catalog.SubprojectCatalogService

plugins {
    base
    `version-catalog`
}

val aliasHelper = AliasHelper(rootDir)
val subprojectCatalogService = gradle.sharedServices.registerIfAbsent(
    "${rootProject.name}SubprojectCatalogService",
    SubprojectCatalogService::class,
) {}

(rootProject.allprojects - project).forEach {
    evaluationDependsOn(it.path)
}

val publishedLibraries = subprojectCatalogService.get()
    .libraries(subprojects, { path -> project(path) }, aliasHelper)

catalog {
    versionCatalog {
        publishedLibraries.forEach { publishedLibrary ->
            library(
                publishedLibrary.alias,
                publishedLibrary.group,
                publishedLibrary.name,
            ).version(publishedLibrary.version)
        }
    }
}

val exportSubprojectCatalog = tasks.register<Copy>("exportSubprojectCatalog") {
    from(tasks.named("generateCatalogAsToml"))
    into(layout.projectDirectory.dir("gradle"))
    rename { "projects.versions.toml" }
}
