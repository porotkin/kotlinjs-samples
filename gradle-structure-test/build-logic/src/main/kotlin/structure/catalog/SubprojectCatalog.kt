package structure.catalog

import org.gradle.api.Project
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.File

data class PublishedLibrary(
    val alias: String,
    val group: String,
    val name: String,
    val version: String,
)

class AliasHelper(
    private val rootDir: File,
) {
    fun getAlias(name: String): String =
        name
            .removePrefix(rootDir.name)
            .split(Regex("[^A-Za-z0-9]+"))
            .filter(String::isNotBlank)
            .joinToString("-") { it.lowercase() }
}

abstract class SubprojectCatalogService : BuildService<BuildServiceParameters.None> {
    fun libraries(
        projects: Iterable<Project>,
        projectResolver: (String) -> Project,
        aliasHelper: AliasHelper,
    ): List<PublishedLibrary> =
        projects
            .map { projectResolver(it.path) }
            .sortedBy(Project::getPath)
            .map { project ->
                PublishedLibrary(
                    alias = aliasHelper.getAlias(project.name),
                    group = project.group.toString(),
                    name = project.name,
                    version = project.publishVersion(),
                )
            }
}

fun Project.publishVersion(): String =
    version.toString()
