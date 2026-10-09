package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.GlyphScrollItem;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class NeoforgeRecipeProvider extends RecipeProvider {

	public NeoforgeRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		// Anima moss
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ANIMA_MOSS.get(), 8)
				.pattern("MMM")
				.pattern("MSM")
				.pattern("MMM")
				.define('M', Items.MOSS_BLOCK)
				.define('S', ModItems.SOUL_SHARD.get())
				.unlockedBy("has_soul_shard", has(ModItems.SOUL_SHARD.get()))
				.save(recipeOutput, SoulHarvestMod.id("anima_moss"));

		// Biofuel block & uncraft
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

		// Charcoal block & uncraft
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK.get())
				.pattern("###")
				.pattern("###")
				.pattern("###")
				.define('#', Items.CHARCOAL)
				.unlockedBy("has_charcoal", has(Items.CHARCOAL))
				.save(recipeOutput, SoulHarvestMod.id("charcoal_block"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CHARCOAL, 9)
				.requires(ModBlocks.CHARCOAL_BLOCK.get())
				.unlockedBy("has_charcoal_block", has(ModBlocks.CHARCOAL_BLOCK.get()))
				.save(recipeOutput, SoulHarvestMod.id("charcoal_block_uncraft"));

		// Fuel pellets
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FUEL_PELLET.get(), 8)
				.requires(Items.COAL)
				.unlockedBy("has_coal", has(Items.COAL))
				.save(recipeOutput, SoulHarvestMod.id("fuel_pellet"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FUEL_PELLET.get(), 8)
				.requires(ModItems.BIOFUEL_CHUNK.get())
				.unlockedBy("has_biofuel_chunk", has(ModItems.BIOFUEL_CHUNK.get()))
				.save(recipeOutput, SoulHarvestMod.id("fuel_pellet_from_biofuel"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FUEL_PELLET.get(), 8)
				.requires(Items.CHARCOAL)
				.unlockedBy("has_charcoal", has(Items.CHARCOAL))
				.save(recipeOutput, SoulHarvestMod.id("fuel_pellet_from_charcoal"));

		// Inlaid lapis
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INLAID_LAPIS.get(), 2)
				.pattern("LG")
				.pattern("GL")
				.define('L', Items.LAPIS_LAZULI)
				.define('G', Items.GOLD_NUGGET)
				.unlockedBy("has_lapis", has(Items.LAPIS_LAZULI))
				.save(recipeOutput, SoulHarvestMod.id("inlaid_lapis"));

		// Glyph Scrolls 1..8
		DeferredHolder<Item, Item>[] glyphs = new DeferredHolder[] {
				ModItems.GLYPH_1, ModItems.GLYPH_2, ModItems.GLYPH_3, ModItems.GLYPH_4,
				ModItems.GLYPH_5, ModItems.GLYPH_6, ModItems.GLYPH_7, ModItems.GLYPH_8
		};
		DeferredHolder<Item, GlyphScrollItem>[] scrolls = new DeferredHolder[] {
				ModItems.GLYPH_SCROLL_1, ModItems.GLYPH_SCROLL_2, ModItems.GLYPH_SCROLL_3, ModItems.GLYPH_SCROLL_4,
				ModItems.GLYPH_SCROLL_5, ModItems.GLYPH_SCROLL_6, ModItems.GLYPH_SCROLL_7, ModItems.GLYPH_SCROLL_8
		};
		for (int i = 0; i < 8; i++) {
			int index = i + 1;
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, scrolls[i].get())
					.pattern("GSG")
					.pattern(" P ")
					.pattern("GSG")
					.define('G', Items.GOLD_NUGGET)
					.define('S', Items.STICK)
					.define('P', glyphs[i].get())
					.unlockedBy("has_glyph_" + index, has(glyphs[i].get()))
					.save(recipeOutput, SoulHarvestMod.id("glyph_scroll_" + index));
		}

		// Magic gold transmutations
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.COPPER_INGOT)
				.pattern("M M")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_copper"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GOLD_INGOT)
				.pattern("MMM")
				.pattern("M M")
				.pattern(" M ")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_gold"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.IRON_INGOT)
				.pattern(" M ")
				.pattern("M M")
				.pattern(" M ")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_iron"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, com.simibubi.create.AllItems.ZINC_INGOT.get())
				.pattern("M M")
				.pattern("   ")
				.pattern("M M")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_zinc"));

		// Polyclay
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POLYCLAY.get(), 8)
				.pattern("NC")
				.pattern("CN")
				.define('N', com.simibubi.create.AllItems.EXP_NUGGET.get())
				.define('C', Items.CLAY_BALL)
				.unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
				.save(recipeOutput, SoulHarvestMod.id("polyclay"));

		// Soul Books
		TagKey<Item> glyphPagesTag = TagKey.create(Registries.ITEM, SoulHarvestMod.id("glyph_pages"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_BOOK_SIMPLE.get())
				.requires(Items.BOOK)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_simple"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_BOOK_STANDARD.get())
				.requires(Items.BOOK)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_standard"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_BOOK_SUPERIOR.get())
				.requires(Items.BOOK)
				.requires(ModItems.GLYPH_1.get())
				.requires(ModItems.GLYPH_2.get())
				.requires(ModItems.GLYPH_3.get())
				.requires(ModItems.GLYPH_4.get())
				.requires(ModItems.GLYPH_5.get())
				.requires(ModItems.GLYPH_6.get())
				.requires(ModItems.GLYPH_7.get())
				.requires(ModItems.GLYPH_8.get())
				.unlockedBy("has_glyph_1", has(ModItems.GLYPH_1.get()))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_superior"));

		// Soul crystal block & uncraft
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOUL_CRYSTAL_BLOCK.get())
				.pattern("###")
				.pattern("###")
				.pattern("###")
				.define('#', ModItems.SOUL_SHARD.get())
				.unlockedBy("has_soul_shard", has(ModItems.SOUL_SHARD.get()))
				.save(recipeOutput, SoulHarvestMod.id("soul_crystal_block"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_SHARD.get(), 9)
				.requires(ModBlocks.SOUL_CRYSTAL_BLOCK.get())
				.unlockedBy("has_soul_crystal_block", has(ModBlocks.SOUL_CRYSTAL_BLOCK.get()))
				.save(recipeOutput, SoulHarvestMod.id("soul_crystal_block_uncraft"));

		// Soul deposit seed & from shard
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
}
//?}
