package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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

		private static TagKey<Block> modTag(String path) {
			return TagKey.create(Registries.BLOCK, SoulHarvestMod.id(path));
		}
	}

	public static final class Items {
		private Items() {}

		public static final TagKey<Item> GLYPH_PAGES = modTag("glyph_pages");
		public static final TagKey<Item> GLYPH_SCROLLS = modTag("glyph_scrolls");
		public static final TagKey<Item> BOOK_COVER_INGREDIENTS = modTag("book_cover_ingredients");
		public static final TagKey<Item> SOUL_BOOKS = modTag("soul_books");
		public static final TagKey<Item> SOUL_BOOK_COVERS = modTag("soul_book_covers");

		public static final TagKey<Item> BIOCIDE_GAS_PROTECTION = modTag("biocide_gas_protection");
		public static final TagKey<Item> COMPOSTABLES = modTag("compostables");

		public static TagKey<Item> compostableTag(int percent) {
			return modTag("compostables/" + percent);
		}

		private static TagKey<Item> modTag(String path) {
			return TagKey.create(Registries.ITEM, SoulHarvestMod.id(path));
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

		private static TagKey<EntityType<?>> modTag(String path) {
			return TagKey.create(Registries.ENTITY_TYPE, SoulHarvestMod.id(path));
		}
	}
}
