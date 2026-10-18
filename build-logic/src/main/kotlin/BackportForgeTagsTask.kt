import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class BackportForgeTagsTask : DefaultTask() {

	@get:InputDirectory
	abstract val inputDirectory: DirectoryProperty

	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	@TaskAction
	fun backport() {
		val inDir = inputDirectory.get().asFile
		val outDir = outputDirectory.get().asFile
		if (!inDir.exists()) {
			logger.warn("Input tags directory does not exist: {}", inDir)
			return
		}

		var count = 0

		inDir.walkTopDown().filter { it.isFile && it.extension == "json" }.forEach { file ->
			val relative = file.relativeTo(inDir)
			val parts = relative.path.split(File.separatorChar).toMutableList()
			val tagsIdx = parts.indexOf("tags")
			if (tagsIdx != -1 && tagsIdx + 1 < parts.size) {
				parts[tagsIdx + 1] = when (parts[tagsIdx + 1]) {
					"item" -> "items"
					"block" -> "blocks"
					"entity_type" -> "entity_types"
					"fluid" -> "fluids"
					else -> parts[tagsIdx + 1]
				}
				val targetRelativePath = parts.joinToString(File.separator)
				val targetFile = File(outDir, targetRelativePath)
				targetFile.parentFile.mkdirs()
				file.copyTo(targetFile, overwrite = true)
				count++
			}
		}

		logger.lifecycle("Backported {} tags to {}", count, outDir)
	}
}
