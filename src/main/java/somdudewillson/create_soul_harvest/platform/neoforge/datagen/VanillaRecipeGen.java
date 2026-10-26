package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {
import com.simibubi.create.AllItems;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModTags;

public final class VanillaRecipeGen {

	private VanillaRecipeGen() {
	}

	public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(item);
	}

	public static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag));
	}

	public static void register(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		registerBiofuelRecipes(recipeOutput);
		registerLapisRecipes(recipeOutput);
		registerMagicGoldRecipes(recipeOutput);
		registerPolyclayRecipes(recipeOutput);
		registerSoulBookRecipes(recipeOutput);
		registerSoulCrystalRecipes(recipeOutput);
		registerSoulDepositSeedRecipes(recipeOutput);
		registerCookingRecipes(recipeOutput);
		registerNecroniumRecipes(recipeOutput);
	}

	public static void registerBiofuelRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BIOFUEL_BLOCK.get())
				.pattern("###")
				.pattern("###")
				.pattern("###")
				.define('#', ModItems.BIOFUEL_CHUNK.get())
				.unlockedBy("has_biofuel_chunk", has(ModItems.BIOFUEL_CHUNK.get()))
				.save(recipeOutput, SoulHarvestMod.id("biofuel_block"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BIOFUEL_CHUNK.get(), 9)
				.requires(ModBlocks.BIOFUEL_BLOCK.get())
				.unlockedBy("has_biofuel_block", has(ModBlocks.BIOFUEL_BLOCK.get()))
				.save(recipeOutput, SoulHarvestMod.id("biofuel_block_uncraft"));
	}

	public static void registerLapisRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INLAID_LAPIS.get(), 2)
				.pattern("LG")
				.pattern("GL")
				.define('L', Items.LAPIS_LAZULI)
				.define('G', Items.GOLD_NUGGET)
				.unlockedBy("has_lapis", has(Items.LAPIS_LAZULI))
				.save(recipeOutput, SoulHarvestMod.id("inlaid_lapis"));
	}

	public static void registerMagicGoldRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.COPPER_NUGGET, 2)
				.pattern("M M")
				.pattern(" M ")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_copper"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.IRON_NUGGET, 2)
				.pattern("M M")
				.pattern("   ")
				.pattern("M M")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_iron"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGIC_GOLD_STRING.get())
				.pattern(" M ")
				.pattern("M M")
				.pattern(" M ")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_stringify"));
		
		
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.COPPER_INGOT, 2)
				.pattern("M M")
				.pattern(" M ")
				.define('M', ModItems.MAGIC_GOLD_CHUNK.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_chunk_to_copper"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.IRON_INGOT, 2)
				.pattern("M M")
				.pattern("   ")
				.pattern("M M")
				.define('M', ModItems.MAGIC_GOLD_CHUNK.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_chunk_to_iron"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GOLD_INGOT, 2)
				.pattern("M M")
				.pattern(" M ")
				.pattern("M M")
				.define('M', ModItems.MAGIC_GOLD_CHUNK.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_chunk_to_gold"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllItems.ZINC_INGOT, 2)
				.pattern("M M")
				.pattern("   ")
				.pattern("MMM")
				.define('M', ModItems.MAGIC_GOLD_CHUNK.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_chunk_to_zinc"));
	}

	public static void registerPolyclayRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POLYCLAY.get(), CompatRecipeGen.POLYCLAY_PER_CRAFT)
				.pattern("NC")
				.pattern("CN")
				.define('N', com.simibubi.create.AllItems.EXP_NUGGET.get())
				.define('C', Items.CLAY_BALL)
				.unlockedBy("has_xp_nugget", has(com.simibubi.create.AllItems.EXP_NUGGET.get()))
				.save(recipeOutput, SoulHarvestMod.id("polyclay"));
		
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POLYCLAY_ANDESITE.get(), 1)
				.pattern("+C+")
				.pattern("N|N")
				.pattern("+C+")
				.define('N', ModItems.POLYCLAY.get())
				.define('C', Items.CLAY_BALL)
				.define('+', com.simibubi.create.AllItems.ANDESITE_ALLOY.get())
				.define('|', ModItems.SOUL_SHARD.get())
				.unlockedBy("has_soul_shard", has(ModItems.SOUL_SHARD.get()))
				.save(recipeOutput, SoulHarvestMod.id("polyclay_andesite"));
		
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EXPERIENCE_EFFIGY.get(), 1)
				.pattern(" L ")
				.pattern("|+|")
				.pattern(" | ")
				.define('L', Blocks.LAPIS_BLOCK)
				.define('|', Items.STICK)
				.define('+', Blocks.HAY_BLOCK)
				.unlockedBy("has_soul_shard", has(ModItems.SOUL_SHARD.get()))
				.save(recipeOutput, SoulHarvestMod.id("experience_effigy"));
	}

	public static void registerSoulBookRecipes(RecipeOutput recipeOutput) {
		TagKey<Item> glyphPagesTag = ModTags.Items.GLYPH_PAGES;

		// Soul Book Covers
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOUL_BOOK_COVER_SIMPLE.get())
				.pattern("SB")
				.pattern("BD")
				.define('S', Tags.Items.STRINGS)
				.define('B', ModTags.Items.BOOK_COVER_INGREDIENTS)
				.define('D', Tags.Items.DYES_BLUE)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_cover_simple"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOUL_BOOK_COVER_STANDARD.get())
				.pattern("SBG")
				.pattern("BD ")
				.define('S', Tags.Items.STRINGS)
				.define('B', ModTags.Items.BOOK_COVER_INGREDIENTS)
				.define('G', ModItems.MAGIC_GOLD.get())
				.define('D', Tags.Items.DYES_BLUE)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_cover_standard"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOUL_BOOK_COVER_SUPERIOR.get())
				.pattern("SBG")
				.pattern("BD ")
				.define('S', ModItems.MAGIC_GOLD_STRING.get())
				.define('B', ModTags.Items.BOOK_COVER_INGREDIENTS)
				.define('G', ModItems.MAGIC_GOLD.get())
				.define('D', Tags.Items.DYES_BLUE)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_cover_superior"));

		// Soul Books
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_BOOK_SIMPLE.get())
				.requires(ModItems.SOUL_BOOK_COVER_SIMPLE.get())
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_simple"));
	}

	public static void registerSoulCrystalRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOUL_CRYSTAL_BLOCK.get())
				.pattern("##")
				.pattern("##")
				.define('#', ModItems.SOUL_SHARD.get())
				.unlockedBy("has_soul_shard", has(ModItems.SOUL_SHARD.get()))
				.save(recipeOutput, SoulHarvestMod.id("soul_crystal_block"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_SHARD.get(), 4)
				.requires(ModBlocks.SOUL_CRYSTAL_BLOCK.get())
				.unlockedBy("has_soul_crystal_block", has(ModBlocks.SOUL_CRYSTAL_BLOCK.get()))
				.save(recipeOutput, SoulHarvestMod.id("soul_crystal_block_uncraft"));
	}

	public static void registerSoulDepositSeedRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOUL_DEPOSIT_SEED.get())
				.pattern(" A ")
				.pattern("ALA")
				.pattern(" A ")
				.define('A', Items.AMETHYST_SHARD)
				.define('L', ModItems.INLAID_LAPIS.get())
				.unlockedBy("has_inlaid_lapis", has(ModItems.INLAID_LAPIS.get()))
				.save(recipeOutput, SoulHarvestMod.id("soul_deposit_seed"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_DEPOSIT_SEED.get(), 2)
				.requires(ModItems.SOUL_SHARD.get())
				.requires(Items.SAND)
				.requires(Items.SAND)
				.unlockedBy("has_soul_shard", has(ModItems.SOUL_SHARD.get()))
				.save(recipeOutput, SoulHarvestMod.id("soul_deposit_seed_from_shard"));
	}

	public static void registerCookingRecipes(RecipeOutput recipeOutput) {
		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(ModTags.Items.GLYPH_PAGES),
						RecipeCategory.MISC,
						ModItems.INLAID_LAPIS.get(),
						0.0f,
						200
				)
				.unlockedBy("has_glyph_pages", has(ModTags.Items.GLYPH_PAGES))
				.save(recipeOutput, SoulHarvestMod.id("recycle_glyph_page"));
	}

	public static void registerNecroniumRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NECRONIUM_WASTE_BOTTLE.get())
				.pattern("###")
				.pattern("#B#")
				.pattern("###")
				.define('#', ModItems.NECRONIUM_WASTE.get())
				.define('B', Items.GLASS_BOTTLE)
				.unlockedBy("has_bottle_or_waste", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLASS_BOTTLE, ModItems.NECRONIUM_WASTE.get()))
				.save(recipeOutput, SoulHarvestMod.id("fill_necronium_waste_bottle"));
	}
}
//?}
