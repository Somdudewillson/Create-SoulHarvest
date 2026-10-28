package somdudewillson.create_soul_harvest.config;

import java.util.function.BooleanSupplier;

//? neoforge {
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
//?} forge {
/*import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
*///?}

import somdudewillson.create_soul_harvest.compat.jei.SoulHarvestJeiHelper;
import somdudewillson.create_soul_harvest.config.graph.ItemDependencyGraph;
import somdudewillson.create_soul_harvest.config.graph.ModDependencyGraph;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModCreativeTabs;

public class SoulHarvestConfig {
	//? neoforge {
	public static ModConfigSpec COMMON_SPEC;
	public static ModConfigSpec.BooleanValue ENABLE_SPELLS;
	public static ModConfigSpec.BooleanValue ENABLE_ENHANCED_SPELLS;
	public static ModConfigSpec.BooleanValue ENABLE_ABSOLUTE_ICE;
	//?} forge {
	/*public static ForgeConfigSpec COMMON_SPEC;
	public static ForgeConfigSpec.BooleanValue ENABLE_SPELLS;
	public static ForgeConfigSpec.BooleanValue ENABLE_ENHANCED_SPELLS;
	public static ForgeConfigSpec.BooleanValue ENABLE_ABSOLUTE_ICE;
	*///?}

	public static void register(ItemDependencyGraph graph) {
		//? neoforge {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		//?} forge {
		/*ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		*///?}

		builder.comment("Create: Soul Harvest Item & Feature Toggles").push("features");

		ENABLE_SPELLS = builder
				.comment("Enable spell scroll items")
				.define("enableSpells", true);

		ENABLE_ENHANCED_SPELLS = builder
				.comment("Enable enhanced spell scroll items (requires spell scrolls in general to be enabled)")
				.define("enableEnhancedSpells", true);

		ENABLE_ABSOLUTE_ICE = builder
				.comment("Enable Absolute Ice block")
				.define("enableAbsoluteIce", true);

		builder.pop();

		COMMON_SPEC = builder.build();

		// Bind config options to items and groups
		graph.bindConfigToGroup(safe(ENABLE_SPELLS), "spells");
		graph.bindConfigToGroup(safe(ENABLE_ENHANCED_SPELLS), "spells_t2");
		graph.bindConfig(safe(ENABLE_ABSOLUTE_ICE), ModBlocks.ABSOLUTE_ICE.getId());
	}

	//? neoforge {
	private static BooleanSupplier safe(ModConfigSpec.BooleanValue configValue) {
		return () -> isLoaded() ? configValue.get() : true;
	}
	//?} forge {
	/*private static BooleanSupplier safe(ForgeConfigSpec.BooleanValue configValue) {
		return () -> isLoaded() ? configValue.get() : true;
	}
	*///?}

	public static void onLoad(ModConfigEvent.Loading event) {
		if (event.getConfig().getSpec() == COMMON_SPEC) {
			onConfigChanged();
		}
	}

	public static void onReload(ModConfigEvent.Reloading event) {
		if (event.getConfig().getSpec() == COMMON_SPEC) {
			onConfigChanged();
		}
	}

	public static void onConfigChanged() {
		ModDependencyGraph.get().invalidateCache();
		try {
			SoulHarvestJeiHelper.updateHiddenIngredients();
		} catch (Throwable ignored) {
		}
		try {
			ModCreativeTabs.refreshCreativeTabs();
		} catch (Throwable ignored) {
		}
	}
	
	public static boolean isLoaded() {
		return SoulHarvestConfig.COMMON_SPEC != null && SoulHarvestConfig.COMMON_SPEC.isLoaded();
	}
}
