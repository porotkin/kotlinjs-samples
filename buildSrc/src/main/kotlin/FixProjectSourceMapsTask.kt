import kotlinx.serialization.json.JsonElement
import org.gradle.api.file.DirectoryProperty
import java.io.File

open class FixProjectSourceMapsTask : FixSourceMapsBase() {
    init {
        compilationDirectory.set(
            project.layout.buildDirectory.dir("compileSync")
        )
    }

    private val projectDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.projectDirectory)

    private val rootProjectDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.rootProject.layout.projectDirectory)

    override fun getFixedSourcesList(
        fileSources: List<JsonElement>,
        baseFile: File,
    ): List<String> =
        fileSources.asSequence()
            .map { it.toString() }
            .map { it.removeSurrounding("\"") }
            .filter { it.startsWith("../") }
            .map { it.removeLeadingDirectories() }
            .map(::toAbsolutePath)
            .map { "\"$it\"" }
            .toList()

    private fun toAbsolutePath(source: String): String {
        if (source.startsWith(PathPrefix.RELATIVE.value)) {
            return source
        }

        val resolvedFile = when {
            source.startsWith("src") -> projectDir.file(source)
            else -> projectDir.file("../$source")
        }

        return resolvedFile.get().asFile.absolutePath
    }
}

private val SKIPPED_THIRDPARTIES = arrayOf(
    "mnt",
    "atomicfu",
)

private fun String.removeLeadingDirectories(): String {
    if (!this.startsWith("../") || SKIPPED_THIRDPARTIES.any { contains(it) }) {
        return this
    }

    val trimmed = removePrefix("../")

    return trimmed.removeLeadingDirectories()
}
