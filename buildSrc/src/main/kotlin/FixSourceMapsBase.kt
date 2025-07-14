import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class FixSourceMapsBase : DefaultTask() {
    private val jsonBuilder = Json {
        explicitNulls = false
    }

    protected enum class PathPrefix(val value: String) {
        ABSOLUTE("/"),
        RELATIVE("../"),
    }

    @Internal
    protected val compilationDirectory: DirectoryProperty = project.objects.directoryProperty()

    protected abstract fun getFixedSourcesList(
        fileSources: List<JsonElement>,
        baseFile: File,
    ): List<String>

    @TaskAction
    fun action() {
        val sourceMaps = compilationDirectory.get().asFileTree
            .filter { it.extension == "map" }
            .files
            .toList()

        print("${sourceMaps.size} source maps found")

        for (sourceMap in sourceMaps) {
            val content = sourceMap.readText()
            val parsedSource = jsonBuilder.decodeFromString<JsonObject>(content)
            val fileSources = parsedSource["sources"]?.jsonArray?.toList()
                ?: continue

            val fixedFileSources = getFixedSourcesList(fileSources, sourceMap)
            val fixedSourceMapContent = content.replace(
                oldValue = fileSources.toNormalizedString(content.contains(", ")),
                newValue = fixedFileSources.toString(),
            )

            sourceMap.writeText(fixedSourceMapContent)
        }
    }

    private fun List<JsonElement>.toNormalizedString(
        containsBlankSpaces: Boolean,
    ): String {
        val stringValue = toString()

        if (containsBlankSpaces) {
            return stringValue
        }

        return stringValue.replace(" ", "")
    }
}
