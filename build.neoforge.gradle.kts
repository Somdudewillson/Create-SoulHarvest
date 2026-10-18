plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

stonecutter {
	val (version, loader) = current.project.split('-', limit = 2)
	properties.tags(version, loader)

	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("neoforge") {
			forgeLikeVersionRange.set("[21.1,)")
		}
		required("create") {
			forgeLikeVersionRange.set("[${prop("deps.create").substringBefore('-')},)")
		}
	}
}

neoForge {
	version = prop("deps.neoforge")
	accessTransformers.from(rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg"))
	validateAccessTransformers = true

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = prop("deps.parchment").split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.current.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.current.version})"
		}
		register("data") {
			data()
			gameDirectory = file("run/")
			ideName = "NeoForge Data (${stonecutter.current.version})"
			programArguments.addAll(
				"--mod", prop("mod.id"),
				"--all",
				"--output", file("${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated").absolutePath,
				"--existing", file("${rootDir}/src/main/resources").absolutePath
			)
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
	sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated")
}

repositories {
	mavenCentral()
	mavenLocal()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven("https://maven.createmod.net") { name = "Create" }
	maven("https://maven.ithundxr.dev/snapshots") { name = "Registrate" }
	maven("https://maven.blamejared.com") { name = "BlameJared" }
}

dependencies {
	implementation("com.simibubi.create:create-${prop("deps.minecraft")}:${prop("deps.create")}") { isTransitive = false }
	implementation("net.createmod.ponder:ponder-neoforge:${prop("deps.ponder")}+mc${prop("deps.minecraft")}")
	compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-${prop("deps.minecraft")}:${prop("deps.flywheel")}")
	runtimeOnly("dev.engine-room.flywheel:flywheel-neoforge-${prop("deps.minecraft")}:${prop("deps.flywheel")}")
	implementation("com.tterrag.registrate:Registrate:${prop("deps.registrate")}")
	
	implementation("com.github.somdudewillson:redsparklib:${prop("deps.redsparklib")}-neoforge+${prop("deps.minecraft")}-SNAPSHOT")

	compileOnly("mezz.jei:jei-${prop("deps.minecraft")}-common-api:${prop("deps.jei")}")
	compileOnly("mezz.jei:jei-${prop("deps.minecraft")}-neoforge-api:${prop("deps.jei")}")
	runtimeOnly("mezz.jei:jei-${prop("deps.minecraft")}-neoforge:${prop("deps.jei")}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

val backportForgeRecipes = tasks.register<BackportForgeRecipesTask>("backportForgeRecipes") {
	group = "stonecutter"
	description = "Backports 1.21 datagen recipes to 1.20.1 Forge recipes format"
	inputDirectory.set(file("${rootDir}/versions/datagen/1.21.1/src/main/generated/data/create_soul_harvest/recipe"))
	outputDirectory.set(file("${rootDir}/versions/datagen/1.20.1/src/main/generated/data/create_soul_harvest/recipes"))
}

val backportForgeTags = tasks.register<BackportForgeTagsTask>("backportForgeTags") {
	group = "stonecutter"
	description = "Backports 1.21 datagen tags to 1.20.1 Forge tags format"
	inputDirectory.set(file("${rootDir}/versions/datagen/1.21.1/src/main/generated/data"))
	outputDirectory.set(file("${rootDir}/versions/datagen/1.20.1/src/main/generated/data"))
}

tasks.named("runData") {
	finalizedBy(backportForgeRecipes, backportForgeTags)
}

tasks.register("runDatagen") {
	group = "stonecutter"
	description = "Runs the NeoForge data generation"
	dependsOn(tasks.named("runData"))
	finalizedBy(backportForgeRecipes, backportForgeTags)
}

