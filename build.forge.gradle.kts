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
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven("https://maven.createmod.net") { name = "Create" }
	maven("https://maven.ithundxr.dev/mirror") { name = "Registrate" }
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") { name = "Fuzs" }
	maven("https://maven.blamejared.com") { name = "BlameJared" }
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")

	implementation("com.simibubi.create:create-${prop("deps.minecraft")}:${prop("deps.create")}:slim") { isTransitive = false }
	implementation("net.createmod.ponder:Ponder-Forge-${prop("deps.minecraft")}:${prop("deps.ponder")}")
	compileOnly("dev.engine-room.flywheel:flywheel-forge-api-${prop("deps.minecraft")}:${prop("deps.flywheel")}")
	runtimeOnly("dev.engine-room.flywheel:flywheel-forge-${prop("deps.minecraft")}:${prop("deps.flywheel")}")
	implementation("com.tterrag.registrate:Registrate:${prop("deps.registrate")}")
	implementation(fileTree(mapOf(
		"dir" to "${rootDir}/../../RedSparkLib/RedSparkLib/versions/${sc.current.project}/build/libs",
		"include" to listOf("redsparklib-*-SNAPSHOT.jar"),
		"exclude" to listOf("*-sources.jar", "*-javadoc.jar")
	)))

	compileOnly("mezz.jei:jei-${prop("deps.minecraft")}-common-api:${prop("deps.jei")}")
	compileOnly("mezz.jei:jei-${prop("deps.minecraft")}-forge-api:${prop("deps.jei")}")
	runtimeOnly("mezz.jei:jei-${prop("deps.minecraft")}-forge:${prop("deps.jei")}")
}

sourceSets {
	main {
		resources.srcDir(
			file("${rootDir}/versions/datagen/1.21.1/src/main/generated")
		)
	}
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
