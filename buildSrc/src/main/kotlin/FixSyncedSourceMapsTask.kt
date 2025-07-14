import kotlinx.serialization.json.JsonElement
import java.io.File

open class FixSyncedSourceMapsTask : FixSourceMapsBase() {
    init {
        compilationDirectory.set(
            project.rootProject.layout.buildDirectory.dir("js/packages/${project.name}/kotlin")
        )
    }

    override fun getFixedSourcesList(
        fileSources: List<JsonElement>,
        baseFile: File,
    ): List<String> =
        fileSources.asSequence()
            .map { it.toString() }
            .map { it.removeSurrounding("\"") }
            .map { it.toRelativePath(baseFile) }
            .map { "\"$it\"" }
            .toList()

    private fun String.toRelativePath(
        baseFile: File,
    ): String {
        if (!this.startsWith(PathPrefix.ABSOLUTE.value)) {
            return this
        }

        val resolvedFile = File(this)
        if (!resolvedFile.exists()) {
            return this
        }

        return resolvedFile.toRelativeString(baseFile.parentFile)
    }
}
