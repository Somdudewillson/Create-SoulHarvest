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
		fun convertMekanismItemInput(map: Map<String, Any?>): Map<String, Any?> {
			val count = map["count"] ?: 1
			val ing = mutableMapOf<String, Any?>()
			if ("tag" in map) {
				var tag = map["tag"] as? String ?: ""
				if (tag.startsWith("c:")) {
					tag = "forge:" + tag.substring("c:".length)
				}
				if (tag == "forge:obsidians") {
					tag = "forge:obsidian"
				}
				ing["tag"] = tag
			} else if ("item" in map) {
				ing["item"] = map["item"]
			} else if ("ingredient" in map) {
				ing.putAll(map["ingredient"] as Map<String, Any?>)
			}
			return mapOf(
				"amount" to count,
				"ingredient" to ing
			)
		}

		@Suppress("UNCHECKED_CAST")
		fun convertMekanismItemOutput(map: Map<String, Any?>): Map<String, Any?> {
			val item = map["id"] ?: map["item"]
			val count = (map["count"] as? Number)?.toInt() ?: 1
			val out = mutableMapOf<String, Any?>("item" to item)
			if (count > 1) {
				out["count"] = count
			}
			return out
		}

		@Suppress("UNCHECKED_CAST")
		fun convertMekanismFluidInput(map: Map<String, Any?>): Map<String, Any?> {
			val amount = map["amount"] ?: 1
			val out = mutableMapOf<String, Any?>("amount" to amount)
			if ("fluid" in map) {
				out["fluid"] = map["fluid"]
			} else if ("tag" in map) {
				var tag = map["tag"] as? String ?: ""
				if (tag.startsWith("c:")) tag = "forge:" + tag.substring("c:".length)
				out["tag"] = tag
			}
			return out
		}

		@Suppress("UNCHECKED_CAST")
		fun convertMekanismGasInput(map: Map<String, Any?>): Map<String, Any?> {
			val amount = map["amount"] ?: 1
			val gas = map["chemical"] ?: map["gas"]
			return mapOf(
				"amount" to amount,
				"gas" to gas
			)
		}

		@Suppress("UNCHECKED_CAST")
		fun convertMekanismGasOutput(map: Map<String, Any?>): Map<String, Any?> {
			val amount = map["amount"] ?: 1
			val gas = map["id"] ?: map["gas"] ?: map["chemical"]
			return mapOf(
				"amount" to amount,
				"gas" to gas
			)
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

			if (rtype == "create_soul_harvest:midas_fluid_boost") {
				for ((k, v) in data) {
					if (k == "fluid") {
						val f = v as? Map<String, Any?>
						if (f != null) {
							val convertedFluid = mutableMapOf<String, Any?>()
							convertedFluid.putAll(f)
							if ("tag" in f) {
								var tag = f["tag"] as? String ?: ""
								if (tag.startsWith("c:")) {
									tag = "forge:" + tag.substring("c:".length)
								}
								convertedFluid["tag"] = tag
							}
							converted[k] = convertedFluid
						} else {
							converted[k] = v
						}
					} else {
						converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "create_soul_harvest:absolute_ice_conversion") {
				fun convertBlockCondition(cond: Any?): Any? {
					val m = cond as? Map<String, Any?> ?: return cond
					val res = mutableMapOf<String, Any?>()
					res.putAll(m)
					if ("tag" in m) {
						var tag = m["tag"] as? String ?: ""
						if (tag.startsWith("c:")) tag = "forge:" + tag.substring("c:".length)
						res["tag"] = tag
					}
					if ("block_tag" in m) {
						var tag = m["block_tag"] as? String ?: ""
						if (tag.startsWith("c:")) tag = "forge:" + tag.substring("c:".length)
						res["block_tag"] = tag
					}
					if ("fluid_tag" in m) {
						var tag = m["fluid_tag"] as? String ?: ""
						if (tag.startsWith("c:")) tag = "forge:" + tag.substring("c:".length)
						res["fluid_tag"] = tag
					}
					return res
				}

				for ((k, v) in data) {
					when (k) {
						"target" -> converted["target"] = convertBlockCondition(v)
						"above" -> converted["above"] = convertBlockCondition(v)
						else -> converted[k] = v
					}
				}
				return converted
			}

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

			if (rtype == "mekanism:reaction") {
				for ((k, v) in data) {
					when (k) {
						"item_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["itemInput"] = convertMekanismItemInput(m)
						}
						"fluid_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["fluidInput"] = convertMekanismFluidInput(m)
						}
						"chemical_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["gasInput"] = convertMekanismGasInput(m)
						}
						"energy_required" -> converted["energyRequired"] = v
						"item_output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["itemOutput"] = convertMekanismItemOutput(m)
						}
						"chemical_output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["gasOutput"] = convertMekanismGasOutput(m)
						}
						else -> converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "mekanism:dissolution") {
				for ((k, v) in data) {
					when (k) {
						"item_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["itemInput"] = convertMekanismItemInput(m)
						}
						"chemical_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["gasInput"] = convertMekanismGasInput(m)
						}
						"output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) {
								val amount = m["amount"] ?: 1
								val id = m["id"] ?: m["gas"] ?: m["slurry"]
								converted["output"] = mapOf(
									"amount" to amount,
									"gas" to id,
									"slurry" to id
								)
							}
						}
						"per_tick_usage" -> converted["perTickUsage"] = v
						else -> converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "mekanism:enriching" || rtype == "mekanism:crushing") {
				for ((k, v) in data) {
					when (k) {
						"input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["input"] = convertMekanismItemInput(m)
						}
						"output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["output"] = convertMekanismItemOutput(m)
						}
						else -> converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "mekanism:combining") {
				for ((k, v) in data) {
					when (k) {
						"main_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["mainInput"] = convertMekanismItemInput(m)
						}
						"extra_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["extraInput"] = convertMekanismItemInput(m)
						}
						"output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["output"] = convertMekanismItemOutput(m)
						}
						else -> converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "mekanism:metallurgic_infusing") {
				for ((k, v) in data) {
					when (k) {
						"item_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["itemInput"] = convertMekanismItemInput(m)
						}
						"chemical_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) {
								val amount = m["amount"] ?: 1
								val infuse = m["chemical"] ?: m["tag"]
								converted["infusionInput"] = mapOf(
									"amount" to amount,
									"infuse_type" to infuse
								)
							}
						}
						"per_tick_usage" -> converted["perTickUsage"] = v
						"output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["output"] = convertMekanismItemOutput(m)
						}
						else -> converted[k] = v
					}
				}
				return converted
			}

			if (rtype == "mekanism:rotary") {
				for ((k, v) in data) {
					when (k) {
						"fluid_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["fluidInput"] = convertMekanismFluidInput(m)
						}
						"chemical_output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["gasOutput"] = convertMekanismGasOutput(m)
						}
						"chemical_input" -> {
							val m = v as? Map<String, Any?>
							if (m != null) converted["gasInput"] = convertMekanismGasInput(m)
						}
						"fluid_output" -> {
							val m = v as? Map<String, Any?>
							if (m != null) {
								val amount = m["amount"] ?: 1
								val fluid = m["id"] ?: m["fluid"]
								converted["fluidOutput"] = mapOf(
									"amount" to amount,
									"fluid" to fluid
								)
							}
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
