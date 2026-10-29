package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public final class ModTags {

	private ModTags() {}

	public static final class Blocks {
		private Blocks() {}

		public static final TagKey<Block> MIDAS_CAN_CONVERT = modTag("midas_can_convert");
		
		public static final TagKey<Block> FLATTEN_CAN_CRUSH = modTag("flatten_can_crush");
		public static final TagKey<Block> FLATTEN_CAN_GENERATE = modTag("flatten_can_generate");
		public static final TagKey<Block> FLATTEN_CAN_DISPLACE = modTag("flatten_can_displace");
		public static final TagKey<Block> CONSIDER_REPLACEABLE = modTag("consider_replaceable");

		public static final TagKey<Block> BIOCIDE_CONVERT_TO_DIRT = modTag("biocide_convert_to_dirt");
		public static final TagKey<Block> BIOCIDE_LOW_CONSUME = modTag("biocide_low_consume");
		public static final TagKey<Block> BIOCIDE_MEDIUM_CONSUME = modTag("biocide_medium_consume");
		public static final TagKey<Block> BIOCIDE_HIGH_CONSUME = modTag("biocide_high_consume");

		public static final TagKey<Block> SOUL_BUD_GROWTH_BLOCKS = modTag("soul_bud_growth_blocks");
		public static final TagKey<Block> FAN_PROCESSING_CATALYSTS_SOUL_INFUSION = modTag("fan_processing_catalysts/soul_infusion");

		public static final TagKey<Block> PLANETOID_DEEPSLATE_ORES_COMMON = modTag("planetoid_deepslate_ores_common");
		public static final TagKey<Block> PLANETOID_DEEPSLATE_ORES_UNCOMMON = modTag("planetoid_deepslate_ores_uncommon");
		public static final TagKey<Block> PLANETOID_DEEPSLATE_ORES_RARE = modTag("planetoid_deepslate_ores_rare");

		public static final TagKey<Block> PLANETOID_STONE_ORES_COMMON = modTag("planetoid_stone_ores_common");
		public static final TagKey<Block> PLANETOID_STONE_ORES_UNCOMMON = modTag("planetoid_stone_ores_uncommon");
		public static final TagKey<Block> PLANETOID_STONE_ORES_RARE = modTag("planetoid_stone_ores_rare");

		public static final TagKey<Block> PLANETOID_STONE_VARIANTS = modTag("planetoid_stone_variants");

		public static final TagKey<Block> SMALL_PLANTS_THAT_FREEZE = modTag("small_plants_that_freeze");

		private static TagKey<Block> modTag(String path) {
			return TagKey.create(Registries.BLOCK, SoulHarvestMod.id(path));
		}
	}

	public static final class Items {
		private Items() {}

		public static final TagKey<Item> GLYPH_PAGES = modTag("glyph_pages");
		public static final TagKey<Item> GLYPH_SCROLLS = modTag("glyph_scrolls");
		public static final TagKey<Item> GLYPH_SCROLLS_T1 = modTag("glyph_scrolls_t1");
		public static final TagKey<Item> GLYPH_SCROLLS_T2 = modTag("glyph_scrolls_t2");
		public static final TagKey<Item> BOOK_COVER_INGREDIENTS = modTag("book_cover_ingredients");
		public static final TagKey<Item> SOUL_BOOKS = modTag("soul_books");
		public static final TagKey<Item> SOUL_BOOK_COVERS = modTag("soul_book_covers");
		
		public static final TagKey<Item> SCUTES = modTag("scutes");

		public static final TagKey<Item> BIOCIDE_GAS_PROTECTION = modTag("biocide_gas_protection");
		public static final TagKey<Item> COMPOSTABLES = modTag("compostables");
		
		public static final TagKey<Item> LOOT_TOKEN_ONLY = modTag("loot_token_only");
		public static final TagKey<Item> GRIND_DROP_FORBIDDEN = modTag("grind_drop_forbidden");
		public static final TagKey<Item> ENHANCED_GRIND_DROP_FORBIDDEN = modTag("enhanced_grind_drop_forbidden");
		public static final TagKey<Item> STANDARD_GRIND_DROP_FORBIDDEN = modTag("standard_grind_drop_forbidden");
		
		public static final TagKey<Item> C_NECRONIUM_INGOTS = commonTag("ingots/necronium");
		public static final TagKey<Item> FORGE_NECRONIUM_INGOTS = forgeTag("ingots/necronium");
		public static final TagKey<Item> C_NECRONIUM_RODS = commonTag("rods/necronium");
		public static final TagKey<Item> FORGE_NECRONIUM_RODS = forgeTag("rods/necronium");
		public static final TagKey<Item> C_NECRONIUM_PLATES = commonTag("plates/necronium");
		public static final TagKey<Item> FORGE_NECRONIUM_PLATES = forgeTag("plates/necronium");
		
		public static final TagKey<Item> C_MAGIC_WOOD_RODS = commonTag("rods/magic_wood");
		public static final TagKey<Item> FORGE_MAGIC_WOOD_RODS = forgeTag("rods/magic_wood");
		public static final TagKey<Item> C_MAGIC_NECRONIUM_RODS = commonTag("rods/magic_necronium");
		public static final TagKey<Item> FORGE_MAGIC_NECRONIUM_RODS = forgeTag("rods/magic_necronium");
		
		public static final TagKey<Item> C_DUSTS_OBSIDIAN = commonTag("dusts/obsidian");
		public static final TagKey<Item> FORGE_DUSTS_OBSIDIAN = forgeTag("dusts/obsidian");
		
		public static final TagKey<Item> C_INGOTS_BRASS = commonTag("ingots/brass");
		public static final TagKey<Item> FORGE_INGOTS_BRASS = forgeTag("ingots/brass");
		
		public static final TagKey<Item> CURIO_CHARM = curioTag("charm");
		public static final TagKey<Item> CURIO_BELT = curioTag("belt");

		public static TagKey<Item> compostableTag(int percent) {
			return modTag("compostables/" + percent);
		}
		
		private static TagKey<Item> tag(String namespace, String path) {
			return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
		}

		private static TagKey<Item> modTag(String path) {
			return TagKey.create(Registries.ITEM, SoulHarvestMod.id(path));
		}

		private static TagKey<Item> commonTag(String path) {
			return tag("c", path);
		}

		private static TagKey<Item> forgeTag(String path) {
			return tag("forge", path);
		}

		private static TagKey<Item> curioTag(String path) {
			return tag("curio", path);
		}
	}

	public static final class Fluids {
		private Fluids() {}

		public static final TagKey<Fluid> BIOSLURRY_LIKE = modTag("bioslurry_like");
		
		private static TagKey<Fluid> tag(String namespace, String path) {
			return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(namespace, path));
		}

		private static TagKey<Fluid> modTag(String path) {
			return TagKey.create(Registries.FLUID, SoulHarvestMod.id(path));
		}

		private static TagKey<Fluid> commonTag(String path) {
			return tag("c", path);
		}

		private static TagKey<Fluid> forgeTag(String path) {
			return tag("forge", path);
		}
	}

	public static final class EntityTypes {
		private EntityTypes() {}

		public static final TagKey<EntityType<?>> T1_MOB = modTag("t1_mob");
		public static final TagKey<EntityType<?>> T2_MOB = modTag("t2_mob");
		public static final TagKey<EntityType<?>> T3_MOB = modTag("t3_mob");
		public static final TagKey<EntityType<?>> REQUIRES_LOW = modTag("requires_low_kills");
		public static final TagKey<EntityType<?>> REQUIRES_MEDIUM = modTag("requires_medium_kills");
		public static final TagKey<EntityType<?>> REQUIRES_HIGH = modTag("requires_high_kills");
		public static final TagKey<EntityType<?>> SOUL_CAN_BE_CAPTURED = modTag("soul_can_be_captured");
		public static final TagKey<EntityType<?>> SOUL_CAPTURE_FORBIDDEN = modTag("soul_capture_forbidden");
		public static final TagKey<EntityType<?>> SOUL_SAMPLING_FORBIDDEN = modTag("soul_sampling_forbidden");
		public static final TagKey<EntityType<?>> CONTAGION_IMMUNE = modTag("contagion_immune");

		private static TagKey<EntityType<?>> modTag(String path) {
			return TagKey.create(Registries.ENTITY_TYPE, SoulHarvestMod.id(path));
		}
	}
}
