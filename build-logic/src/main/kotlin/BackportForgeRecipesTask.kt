import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class BackportForgeRecipesTask : DefaultTask() {

	@get:InputDirectory
	abstract val inputDirectory: DirectoryProperty

	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	@TaskAction
	fun backport() {
		val inDir = inputDirectory.get().asFile
		val outDir = outputDirectory.get().asFile
		if (!inDir.exists()) {
			logger.warn("Input recipe directory does not exist: {}", inDir)
			return
		}
		if (outDir.exists()) {
			outDir.deleteRecursively()
		}
		outDir.mkdirs()

		val slurper = JsonSlurper()
		var count = 0

		inDir.walkTopDown().filter { it.isFile && it.extension == "json" }.forEach { file ->
			val relative = file.relativeTo(inDir)
			val targetFile = File(outDir, relative.path)
			targetFile.parentFile.mkdirs()

			@Suppress("UNCHECKED_CAST")
			val parsed = slurper.parse(file) as? Map<String, Any?> ?: return@forEach

			val converted = convertRecipe(parsed)
			val jsonText = JsonOutput.prettyPrint(JsonOutput.toJson(converted))
			targetFile.writeText(jsonText)
			count++
		}

		logger.lifecycle("Backported {} recipes to {}", count, outDir)
	}

	companion object {
		@Suppress("UNCHECKED_CAST")
		fun convertCondition(cond: Any?): Any? {
			if (cond !is Map<*, *>) return cond
			val map = cond as Map<String, Any?>
			val result = mutableMapOf<String, Any?>()
			for ((k, v) in map) {
				when (k) {
					"type" -> {
						if (v is String && v.startsWith("neoforge:")) {
							result[k] = "forge:" + v.substring("neoforge:".length)
						} else {
							result[k] = v
						}
					}
					"conditions", "values" -> {
						val list = v as? List<*> ?: emptyList<Any>()
						result[k] = list.map { convertCondition(it) }
					}
					"value" -> {
						result[k] = convertCondition(v)
					}
					else -> result[k] = v
				}
			}
			return result
		}

		@Suppress("UNCHECKED_CAST")
		fun convertIngredient(ing: Any?): Any? {
			if (ing !is Map<*, *>) return ing
			val map = ing as Map<String, Any?>
			val result = mutableMapOf<String, Any?>()
			for ((k, v) in map) {
				if (k == "type" && v == "neoforge:single") continue
				result[k] = v
			}
			return result
		}

		@Suppress("UNCHECKED_CAST")
		fun convertResult(res: Any?): Any? {
			if (res !is Map<*, *>) return res
			val map = res as Map<String, Any?>
			val out = mutableMapOf<String, Any?>()
			if ("amount" in map) {
				out["amount"] = map["amount"]
				if ("id" in map) {
					out["fluid"] = map["id"]
				} else if ("fluid" in map) {
					out["fluid"] = map["fluid"]
				}
			} else {
				if ("id" in map) {
					out["item"] = map["id"]
				} else if ("item" in map) {
					out["item"] = map["item"]
				}
				if ("count" in map) out["count"] = map["count"]
				if ("chance" in map) out["chance"] = map["chance"]
			}
			for ((k, v) in map) {
				if (k !in setOf("id", "item", "fluid", "amount", "count", "chance")) {
					out[k] = v
				}
			}
			return out
		}

		@Suppress("UNCHECKED_CAST")
		fun convertRecipe(data: Map<String, Any?>): Map<String, Any?> {
			val converted = convertRecipeData(data)
			val rawConditions = data["neoforge:conditions"] ?: data["conditions"]
			if (rawConditions is List<*>) {
				converted["conditions"] = rawConditions.map { convertCondition(it) }
				converted.remove("neoforge:conditions")
			}
			return converted
		}

		@Suppress("UNCHECKED_CAST")
		private fun convertRecipeData(data: Map<String, Any?>): MutableMap<String, Any?> {
			val rtype = data["type"] as? String ?: ""
			val converted = mutableMapOf<String, Any?>()

			if (rtype == "minecraft:crafting_shaped" || rtype == "minecraft:crafting_shapeless") {
				for ((k, v) in data) {
					if (k == "result") {
						val r = v as? Map<String, Any?>
						if (r != null) {
							converted["result"] = mapOf(
								"item" to (r["id"] ?: r["item"]),
								"count" to (r["count"] ?: 1)
							)
						} else {
							converted[k] = v
						}
					} else {
						converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "minecraft:smelting") {
				for ((k, v) in data) {
					if (k == "result") {
						val r = v as? Map<String, Any?>
						converted["result"] = r?.get("id") ?: r?.get("item") ?: v
					} else {
						converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "create:mechanical_crafting") {
				for ((k, v) in data) {
					when (k) {
						"accept_mirrored" -> converted["acceptMirrored"] = v
						"show_notification" -> converted["showNotification"] = v
						"result" -> {
							val r = v as? Map<String, Any?>
							if (r != null) {
								converted["result"] = mapOf(
									"item" to (r["id"] ?: r["item"]),
									"count" to (r["count"] ?: 1)
								)
							} else {
								converted[k] = v
							}
						}
						else -> converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "create:sequenced_assembly" || rtype == "create_soul_harvest:uniform_sequenced_assembly") {
				for ((k, v) in data) {
					when (k) {
						"transitional_item", "transitionalItem" -> {
							val ti = v as? Map<String, Any?>
							if (ti != null) {
								converted["transitionalItem"] = mapOf(
									"item" to (ti["id"] ?: ti["item"])
								)
							} else {
								converted[k] = v
							}
						}
						"ingredient" -> converted["ingredient"] = convertIngredient(v)
						"results" -> {
							val list = v as? List<*> ?: emptyList<Any>()
							converted["results"] = list.map { convertResult(it) }
						}
						"sequence" -> {
							val seq = v as? List<*> ?: emptyList<Any>()
							converted["sequence"] = seq.map {
								if (it is Map<*, *>) convertRecipe(it as Map<String, Any?>) else it
							}
						}
						else -> converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "enderio:alloy_smelting") {
				for ((k, v) in data) {
					when (k) {
						"inputs" -> {
							val list = v as? List<*> ?: emptyList<Any>()
							converted["inputs"] = list.map { rawInput ->
								val map = rawInput as? Map<String, Any?> ?: return@map rawInput
								val count = map["count"] ?: 1
								val ingredient = mutableMapOf<String, Any?>()
								if ("tag" in map) {
									var tag = map["tag"] as? String ?: ""
									if (tag.startsWith("c:")) {
										tag = "forge:" + tag.substring("c:".length)
									}
									if (tag == "forge:obsidians") {
										tag = "forge:obsidian"
									}
									ingredient["tag"] = tag
								} else if ("item" in map) {
									ingredient["item"] = map["item"]
								} else if ("ingredient" in map) {
									ingredient.putAll(map["ingredient"] as Map<String, Any?>)
								}
								mapOf(
									"count" to count,
									"ingredient" to ingredient
								)
							}
						}
						"output" -> {
							val outMap = v as? Map<String, Any?>
							val item = outMap?.get("id") ?: outMap?.get("item")
							val count = (outMap?.get("count") as? Number)?.toInt() ?: 1
							val resultMap = mutableMapOf<String, Any?>("item" to item)
							if (count > 1) {
								resultMap["count"] = count
							}
							converted["result"] = resultMap
						}
						else -> converted[k] = v
					}
				}
				return converted
			}

			for ((k, v) in data) {
				when (k) {
					"heat_requirement" -> converted["heatRequirement"] = v
					"keep_held_item" -> converted["keepHeldItem"] = v
					"ingredients" -> {
						val list = v as? List<*> ?: emptyList<Any>()
						converted["ingredients"] = list.map { convertIngredient(it) }
					}
					"results" -> {
						val list = v as? List<*> ?: emptyList<Any>()
						converted["results"] = list.map { convertResult(it) }
					}
					else -> converted[k] = v
				}
			}
			return converted
		}
	}
}
