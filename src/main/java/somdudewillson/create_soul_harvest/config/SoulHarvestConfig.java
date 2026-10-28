package somdudewillson.create_soul_harvest.config;

import java.util.function.BooleanSupplier;

//? neoforge {
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import static net.neoforged.neoforge.common.ModConfigSpec.*;
//?} forge {
/*import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import static net.minecraftforge.common.ForgeConfigSpec.*;
*///?}

import somdudewillson.create_soul_harvest.compat.jei.SoulHarvestJeiHelper;
import somdudewillson.create_soul_harvest.config.graph.ItemDependencyGraph;
import somdudewillson.create_soul_harvest.config.graph.ModDependencyGraph;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModCreativeTabs;

public class SoulHarvestConfig {
	//? neoforge {
	public static ModConfigSpec COMMON_SPEC;
	//?} forge {
	/*public static ForgeConfigSpec COMMON_SPEC;
	*///?}
	
	public static BooleanValue ENABLE_SPELLS;
	public static BooleanValue ENABLE_ENHANCED_SPELLS;
	public static BooleanValue ENABLE_ABSOLUTE_ICE;
	
	public static IntValue SCANNING_DEFAULT_ROLLS;
	public static DoubleValue SCANNING_ROLLS_MULT;
	
	public static DoubleValue ENHANCED_ONLY_DROPS_MULT;
	public static DoubleValue XP_DROP_MULT;

	public static void register(ItemDependencyGraph graph) {
		//? neoforge {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		//?} forge {
		/*ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		*///?}

		builder.comment("Item & Feature Toggles").push("features");

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

		
		builder.comment("Entity Scanning Values").push("entityScanning");

		SCANNING_DEFAULT_ROLLS = builder
				.comment("Default number of sampling rolls per entity")
				.defineInRange("scanDefaultRolls", 100, 1, 10000);

		SCANNING_ROLLS_MULT = builder
				.comment("Multiplier on number of entity sampling rolls")
				.defineInRange("scanRollsMult", 1., 0.1, 100.);

		builder.pop();

		
		builder.comment("Mob Drop Values").push("mobDrops");
		
		builder.comment("Item Drop Values").push("mobDropsItems");

		ENHANCED_ONLY_DROPS_MULT = builder
				.comment("Multiplier on number of player-only drops")
				.defineInRange("enhancedDropsMult", 0.25, 0., 10.);

		builder.pop();
		
		builder.comment("XP Drop Values").push("mobDropsExperience");

		XP_DROP_MULT = builder
				.comment("Multiplier on amount of xp produced")
				.defineInRange("experienceDropsMult", 1., 0., 10.);

		builder.pop();

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
