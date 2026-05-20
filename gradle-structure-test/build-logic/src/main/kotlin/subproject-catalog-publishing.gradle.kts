import structure.catalog.AliasHelper
import structure.catalog.SubprojectCatalogService

plugins {
    base
    `version-catalog`
    `maven-publish`
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

publishing {
    publications {
        create<MavenPublication>("subprojectCatalog") {
            from(components["versionCatalog"])

            groupId = "${project.group}.catalog"
            artifactId = "projects"
            version = project.version.toString()
        }
    }

    repositories {
        maven {
            name = "subprojectCatalogLocal"
            url = rootProject.layout.projectDirectory.dir("../build/local-maven").asFile.toURI()
        }
    }
}
