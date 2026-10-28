plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
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
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("forge") {
			forgeLikeVersionRange.set("[47,)")
		}
		required("create") {
			forgeLikeVersionRange.set("[${prop("deps.create").substringBefore('-')},)")
		}
		optional("jei") {
			forgeLikeVersionRange.set("[${prop("deps.jei").substringBefore('.')},)")
			slug("jei")
		}
		optional("mekanism") {
			forgeLikeVersionRange.set("[${prop("deps.mekanism").substringBefore('.')},)")
			slug("mekanism")
		}
	}
}

legacyForge {
	version = "${prop("deps.minecraft")}-${prop("deps.forge")}"

	validateAccessTransformers = true

	accessTransformers.from(
		rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
	)

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${sc.current.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${sc.current.version})"
		}
	}


	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

mixin {
	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
	config("${prop("mod.id")}.mixins.json")
}

repositories {
	mavenCentral()
	mavenLocal()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven("https://maven.createmod.net") { name = "Create" }
	maven("https://maven.ithundxr.dev/mirror") { name = "Registrate" }
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") { name = "Fuzs" }
	maven("https://maven.blamejared.com") { name = "BlameJared" }
	maven("https://modmaven.dev") { name = "ModMaven" }
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	
    modImplementation("com.simibubi.create:create-${property("deps.minecraft")}:${property("deps.create")}:slim") { isTransitive = false }
    modImplementation("net.createmod.ponder:Ponder-Forge-${property("deps.minecraft")}:${property("deps.ponder")}")
    modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-${property("deps.minecraft")}:${property("deps.flywheel")}")
    modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-${property("deps.minecraft")}:${property("deps.flywheel")}")
    modImplementation("com.tterrag.registrate:Registrate:${property("deps.registrate")}")
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
    implementation("io.github.llamalad7:mixinextras-forge:0.4.1")
	
	modImplementation("com.github.somdudewillson:redsparklib:${prop("deps.redsparklib")}-forge+${prop("deps.minecraft")}-SNAPSHOT")

	modCompileOnly("mezz.jei:jei-${prop("deps.minecraft")}-common-api:${prop("deps.jei")}")
	modCompileOnly("mezz.jei:jei-${prop("deps.minecraft")}-forge-api:${prop("deps.jei")}")
	modRuntimeOnly("mezz.jei:jei-${prop("deps.minecraft")}-forge:${prop("deps.jei")}")

	modCompileOnly("mekanism:Mekanism:${prop("deps.minecraft")}-${prop("deps.mekanism")}:api")
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

sourceSets {
	main {
		resources.srcDir(
			file("${rootDir}/versions/datagen/1.21.1/src/main/generated")
		)
		resources.srcDir(
			file("${rootDir}/versions/datagen/1.20.1/src/main/generated")
		)
	}
}

tasks.named<ProcessResources>("processResources") {
	dependsOn(backportForgeRecipes, backportForgeTags)
	exclude("data/create_soul_harvest/recipe/**")
	exclude("data/create_soul_harvest/advancement/**")
	exclude("data/**/tags/item/**")
	exclude("data/**/tags/block/**")
	exclude("data/**/tags/entity_type/**")
	exclude("data/**/tags/fluid/**")
}

tasks.named("sourcesJar") {
	dependsOn(backportForgeRecipes, backportForgeTags)
}


tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

val generateEmptyTsrg = tasks.register("generateEmptyTsrg") {
	val tsrgFile = layout.buildDirectory.file("mixin/${prop("mod.id")}.mixins.refmap.json.mappings.tsrg")
	outputs.file(tsrgFile)
	mustRunAfter("jar", "compileJava")
	doLast {
		val file = tsrgFile.get().asFile
		if (!file.exists()) {
			file.parentFile.mkdirs()
			file.createNewFile()
		}
	}
}

tasks.named("reobfJar") {
	dependsOn(generateEmptyTsrg)
}
