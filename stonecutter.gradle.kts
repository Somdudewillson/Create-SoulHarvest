plugins {
	alias(libs.plugins.stonecutter)
	alias(libs.plugins.mod.publish.plugin)
	alias(libs.plugins.loom.back.compat).apply(false)
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.jsonlang.postprocess).apply(false)
	alias(libs.plugins.kotlin.jvm).apply(false)
	alias(libs.plugins.devtools.ksp).apply(false)
	alias(libs.plugins.fletching.table).apply(false)
	alias(libs.plugins.legacyforge.moddev).apply(false)
	id("mod-root")
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
	constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
	swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
	swaps["mod_id"] = "\"${properties.get<String>("mod.id")}\";"
	swaps["mod_name"] = "\"${properties.get<String>("mod.name")}\";"
	swaps["mod_group"] = "\"${properties.get<String>("mod.group")}\";"
	swaps["minecraft"] = "\"${current.version}\";"
	constants["release"] = properties.get<String>("mod.id") != "modtemplate"

	// Forge replacements when active version is < 1.21 (1.20.1-forge)
	replacements.string(current.parsed < "1.21") {
		replace("import net.neoforged.neoforge.registries.DeferredRegister;", "import net.minecraftforge.registries.DeferredRegister;")
		replace("import net.neoforged.neoforge.registries.DeferredHolder;", "import net.minecraftforge.registries.RegistryObject;")
		replace("import net.neoforged.neoforge.registries.NeoForgeRegistries;", "import net.minecraftforge.registries.ForgeRegistries;")
		replace("import net.neoforged.neoforge.fluids.FluidType;", "import net.minecraftforge.fluids.FluidType;")
		replace("import net.neoforged.neoforge.fluids.BaseFlowingFluid;", "import net.minecraftforge.fluids.ForgeFlowingFluid;")
		replace("NeoForgeRegistries.Keys.FLUID_TYPES", "ForgeRegistries.Keys.FLUID_TYPES")
		replace("net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions", "net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions")
		replace("BaseFlowingFluid", "ForgeFlowingFluid")
		replace("DeferredHolder<Block, ", "RegistryObject/*B*/<")
		replace("DeferredHolder<Item, ", "RegistryObject/*I*/<")
		replace("DeferredHolder<BlockEntityType<?>, ", "RegistryObject/*BE*/<")
		replace("DeferredHolder<CreativeModeTab, ", "RegistryObject/*T*/<")
		replace("DeferredHolder<FluidType, ", "RegistryObject/*FT*/<")
		replace("DeferredHolder<Fluid, ", "RegistryObject/*F*/<")
		replace("DeferredHolder<EntityType<?>, ", "RegistryObject/*ET*/<")
		replace("(DeferredHolder)", "(RegistryObject)")
		replace("TransparentBlock", "AbstractGlassBlock")
	}
}

