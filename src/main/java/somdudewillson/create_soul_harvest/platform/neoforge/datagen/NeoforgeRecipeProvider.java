package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

import net.minecraft.advancements.Advancement;

//? neoforge {
import net.minecraft.advancements.AdvancementHolder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.GlyphScrollItem;
import somdudewillson.create_soul_harvest.content.recipe.SoulInfusionRecipe;
import somdudewillson.create_soul_harvest.content.recipe.UniformSequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.concurrent.CompletableFuture;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.create_soul_harvest.registry.ModFluids;

public class NeoforgeRecipeProvider extends RecipeProvider {

	public NeoforgeRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		buildVanillaRecipes(recipeOutput);

		buildCreateRecipes(recipeOutput);
	}

	private void buildVanillaRecipes(RecipeOutput recipeOutput) {
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

		// Inlaid lapis
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INLAID_LAPIS.get(), 2)
				.pattern("LG")
				.pattern("GL")
				.define('L', Items.LAPIS_LAZULI)
				.define('G', Items.GOLD_NUGGET)
				.unlockedBy("has_lapis", has(Items.LAPIS_LAZULI))
				.save(recipeOutput, SoulHarvestMod.id("inlaid_lapis"));

		// Magic gold transmutations
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.COPPER_INGOT)
				.pattern("M M")
				.pattern(" M ")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_copper"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.IRON_INGOT)
				.pattern("M M")
				.pattern("   ")
				.pattern("M M")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_to_iron"));

		// Magic gold string
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGIC_GOLD_STRING.get())
				.pattern(" M ")
				.pattern("M M")
				.pattern(" M ")
				.define('M', ModItems.MAGIC_GOLD.get())
				.unlockedBy("has_magic_gold", has(ModItems.MAGIC_GOLD.get()))
				.save(recipeOutput, SoulHarvestMod.id("magic_gold_stringify"));

		// Polyclay
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POLYCLAY.get(), 8)
				.pattern("NC")
				.pattern("CN")
				.define('N', AllItems.EXP_NUGGET.get())
				.define('C', Items.CLAY_BALL)
				.unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
				.save(recipeOutput, SoulHarvestMod.id("polyclay"));
		
		// Soul Book Covers
		TagKey<Item> glyphPagesTag = ModTags.Items.GLYPH_PAGES;
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
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_simple"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_BOOK_STANDARD.get())
				.requires(ModItems.SOUL_BOOK_COVER_STANDARD.get())
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.requires(glyphPagesTag)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_standard"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL_BOOK_SUPERIOR.get())
				.requires(ModItems.SOUL_BOOK_COVER_SUPERIOR.get())
				.requires(ModItems.GLYPH_1.get())
				.requires(ModItems.GLYPH_2.get())
				.requires(ModItems.GLYPH_3.get())
				.requires(ModItems.GLYPH_4.get())
				.requires(ModItems.GLYPH_5.get())
				.requires(ModItems.GLYPH_6.get())
				.requires(ModItems.GLYPH_7.get())
				.requires(ModItems.GLYPH_8.get())
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("soul_book_superior"));

		// Soul crystal block & uncraft
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
		
		// Recycle glyph pages
		SimpleCookingRecipeBuilder.smelting(
					Ingredient.of(ModTags.Items.GLYPH_PAGES), 
					RecipeCategory.MISC, 
					ModItems.INLAID_LAPIS.get(), 
					0.0f, 
					200
				)
				.unlockedBy("has_glyph_pages", has(glyphPagesTag))
				.save(recipeOutput, SoulHarvestMod.id("recycle_glyph_page"));
	}

	private void buildCreateRecipes(RecipeOutput recipeOutput) {
		// Mechanical Crafting: Loot Token Blank
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModItems.LOOT_TOKEN_BLANK.get(), 1)
				.key('N', Items.NETHERITE_INGOT)
				.key('G', Items.GOLD_NUGGET)
				.patternLine(" GGG ")
				.patternLine("GGNGG")
				.patternLine("GNNNG")
				.patternLine("GGNGG")
				.patternLine(" GGG ")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_loot_token_blank"));
		
		// Mechanical Crafting: Midas Core
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModBlocks.MIDAS_CORE.get(), 1)
				.key('I', ModItems.ENCHANTED_INLAID_LAPIS.get())
				.key('G', Items.GOLD_BLOCK)
				.key('B', com.simibubi.create.AllItems.BRASS_INGOT)
				.patternLine(" BBB ")
				.patternLine("BGGGB")
				.patternLine("BGIGB")
				.patternLine("BGGGB")
				.patternLine(" BBB ")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_midas_core"));

		// Mechanical Crafting: Glyph Scrolls 1..8
		@SuppressWarnings("unchecked")
		DeferredHolder<Item, Item>[] glyphs = new DeferredHolder[] {
				ModItems.GLYPH_1, ModItems.GLYPH_2, ModItems.GLYPH_3, ModItems.GLYPH_4,
				ModItems.GLYPH_5, ModItems.GLYPH_6, ModItems.GLYPH_7, ModItems.GLYPH_8
		};
		@SuppressWarnings("unchecked")
		DeferredHolder<Item, GlyphScrollItem>[] scrolls = new DeferredHolder[] {
				ModItems.GLYPH_SCROLL_1, ModItems.GLYPH_SCROLL_2, ModItems.GLYPH_SCROLL_3, ModItems.GLYPH_SCROLL_4,
				ModItems.GLYPH_SCROLL_5, ModItems.GLYPH_SCROLL_6, ModItems.GLYPH_SCROLL_7, ModItems.GLYPH_SCROLL_8
		};
		for (int i = 0; i < 8; i++) {
			int index = i + 1;
			MechanicalCraftingRecipeBuilder.shapedRecipe(scrolls[i].get(), 1)
				.key('G', ModItems.MAGIC_GOLD.get())
				.key('-', Items.STICK)
				.key('O', glyphs[i].get())
				.key('+', ModItems.GLYPH_OVERLOADED.get())
				.key('P', Items.PAPER)
				.patternLine("G---G")
				.patternLine(" P+P ")
				.patternLine(" POP ")
				.patternLine(" P+P ")
				.patternLine("G---G")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("glyph_scroll_" + index));
		}

		// Compacting: Absolute Ice
		new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id("create_compact_absolute_ice"))
				.require(ModItems.ENCHANTED_INLAID_LAPIS.get())
				.require(Blocks.BLUE_ICE)
				.require(Blocks.BLUE_ICE)
				.require(Blocks.BLUE_ICE)
				.require(Blocks.BLUE_ICE)
				.require(Blocks.BLUE_ICE)
				.require(Blocks.BLUE_ICE)
				.require(Blocks.BLUE_ICE)
				.require(Blocks.BLUE_ICE)
				.output(ModBlocks.ABSOLUTE_ICE.get())
				.build(recipeOutput);

		// Compacting: Biofuel Block (Heated)
		new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id("create_compact_bioslurry_to_biofuel"))
				.require(ModFluids.BIOSLURRY.get(), 1000)
				.requiresHeat(HeatCondition.HEATED)
				.output(ModBlocks.BIOFUEL_BLOCK.get())
				.build(recipeOutput);

		// Deploying: Carve Bone Block
		new ItemApplicationRecipe.Builder<>(DeployerApplicationRecipe::new, SoulHarvestMod.id("create_deploy_carve_bone_block"))
				.require(Blocks.BONE_BLOCK)
				.require(ItemTags.AXES)
				.toolNotConsumed()
				.output(ModBlocks.CARVED_BONE_BLOCK.get())
				.build(recipeOutput);

		// Mixing: Bioslurry to Bonemeal
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_bioslurry_to_bonemeal"))
				.require(Items.SAND)
				.require(ModFluids.BIOSLURRY.get(), 500)
				.output(Items.BONE_MEAL, 8)
				.build(recipeOutput);

		// Mixing: Bioslurry to Wither Slurry
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_bioslurry_to_wither_slurry"))
				.require(Items.WITHER_ROSE)
				.require(ModFluids.BIOSLURRY.get(), 250)
				.output(ModFluids.WITHER_SLURRY.get(), 250)
				.build(recipeOutput);

		// Mixing: Silica Solution from Sand
		TagKey<Item> smeltsToGlass = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("smelts_to_glass"));
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_silica_solution_from_sand"))
				.require(smeltsToGlass)
				.require(smeltsToGlass)
				.require(smeltsToGlass)
				.require(smeltsToGlass)
				.require(smeltsToGlass)
				.require(smeltsToGlass)
				.require(smeltsToGlass)
				.require(smeltsToGlass)
				.require(Fluids.WATER, 1000)
				.output(ModFluids.SILICA_SOLUTION.get(), 1000)
				.build(recipeOutput);

		// Mixing: Silica Solution from Quartz
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_silica_solution_from_quartz"))
				.require(Items.QUARTZ)
				.require(Fluids.WATER, 1000)
				.output(ModFluids.SILICA_SOLUTION.get(), 1000)
				.build(recipeOutput);

		// Mixing: Mulch to Bioslurry
		var mulchMix = new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_mulch_to_bioslurry"));
		for (int i = 0; i < 8; i++) {
			mulchMix.require(ModItems.ORGANIC_MULCH.get());
		}
		mulchMix.require(Fluids.WATER, 250)
				.output(ModFluids.BIOSLURRY.get(), 250)
				.build(recipeOutput);

		// Mixing: Bioslurry to Dirt
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_dirt_from_bioslurry"))
			.require(ItemTags.SAND)
			.require(ModFluids.BIOSLURRY.get(), 250)
			.output(Items.DIRT, 1)
			.build(recipeOutput);

		// Mixing: Bioslurry to Bone Meal
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_bone_meal_from_bioslurry"))
			.require(ItemTags.DIRT)
			.require(ModFluids.BIOSLURRY.get(), 1000)
			.requiresHeat(HeatCondition.HEATED)
			.output(Items.BONE_MEAL, 9)
			.build(recipeOutput);

		// Mixing: Wither Slurry to Wither Rose
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_wither_slurry_to_wither_rose"))
				.require(ItemTags.FLOWERS)
				.require(ModFluids.WITHER_SLURRY.get(), 125)
				.require(Fluids.LAVA, 125)
				.output(Items.WITHER_ROSE, 1)
				.build(recipeOutput);

		// Mixing: Wither Slurry to Wither Skull
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_wither_slurry_to_wither_skull"))
				.require(ModBlocks.CARVED_BONE_BLOCK.get())
				.require(ModFluids.WITHER_SLURRY.get(), 1000)
				.require(Fluids.LAVA, 1000)
				.output(Items.WITHER_SKELETON_SKULL, 1)
				.build(recipeOutput);

		// Mixing: Soul Shards to Liquid Soul Crystal
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_soul_shards_to_liquid_soul_crystal"))
				.require(ModItems.SOUL_SHARD.get())
				.require(ModFluids.SILICA_SOLUTION.get(), 125)
				.requiresHeat(HeatCondition.HEATED)
				.output(ModFluids.LIQUID_SOUL_CRYSTAL.get(), 250)
				.build(recipeOutput);

		// Sandpaper Polishing: Inlaid Lapis
		new StandardProcessingRecipe.Builder<>(SandPaperPolishingRecipe::new, SoulHarvestMod.id("create_polish_inlaid_lapis"))
				.require(ModItems.INLAID_LAPIS.get())
				.output(ModItems.POLISHED_INLAID_LAPIS.get())
				.build(recipeOutput);

		// Sequenced Assembly: Assemble Loot Token
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_assemble_loot_token"))
				.require(ModItems.LOOT_TOKEN_BLANK.get())
				.transitionTo(ModItems.LOOT_TOKEN_BLANK.get())
				.loops(2)
				.addStep(FillingRecipe::new, rb -> rb.require(Fluids.LAVA, 1000))
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ModItems.ENCHANTED_INLAID_LAPIS.get()))
				.addStep(PressingRecipe::new, rb -> rb)
				.addStep(PressingRecipe::new, rb -> rb)
				.addOutput(ModItems.LOOT_TOKEN.get(), 1.0f)
				.build(recipeOutput);

		// Sequenced Assembly: Print Glyph Page
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_print_glyph_page"))
				.require(Items.PAPER)
				.transitionTo(Items.PAPER)
				.loops(2)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ModItems.INLAID_LAPIS.get()))
				.addStep(PressingRecipe::new, rb -> rb)
				.addOutput(ModItems.GLYPH_1.get(), 0.125f)
				.addOutput(ModItems.GLYPH_2.get(), 0.125f)
				.addOutput(ModItems.GLYPH_3.get(), 0.125f)
				.addOutput(ModItems.GLYPH_4.get(), 0.125f)
				.addOutput(ModItems.GLYPH_5.get(), 0.125f)
				.addOutput(ModItems.GLYPH_6.get(), 0.125f)
				.addOutput(ModItems.GLYPH_7.get(), 0.125f)
				.addOutput(ModItems.GLYPH_8.get(), 0.125f)
				.build(withUniformSerializer(recipeOutput, "create_soul_harvest.jei.glyph_page_random_output"));

		// Sequenced Assembly: Molten Lapis Core
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_sequenced_molten_lapis_core"))
				.require(ModItems.POLISHED_INLAID_LAPIS.get())
				.transitionTo(ModItems.POLISHED_INLAID_LAPIS.get())
				.loops(8)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.BLAZE_POWDER))
				.addStep(FillingRecipe::new, rb -> rb.require(Fluids.LAVA, 250))
				.addOutput(ModItems.MOLTEN_LAPIS_CORE.get(), 1.0f)
				.build(recipeOutput);

		// Crushing: Soul Shard to Experience Nugget
		new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, SoulHarvestMod.id("crush_soul_shard"))
				.require(ModItems.SOUL_SHARD.get())
				.output(0.05f, com.simibubi.create.AllItems.EXP_NUGGET)
				.build(recipeOutput);

		// Splashing: Wash Overloaded Glyph
		new StandardProcessingRecipe.Builder<>(SplashingRecipe::new, SoulHarvestMod.id("create_splashing_wash_overloaded_glyph"))
				.require(ModItems.GLYPH_OVERLOADED.get())
				.output(0.12f, ModItems.GLYPH_1.get())
				.output(0.12f, ModItems.GLYPH_2.get())
				.output(0.12f, ModItems.GLYPH_3.get())
				.output(0.12f, ModItems.GLYPH_4.get())
				.output(0.12f, ModItems.GLYPH_5.get())
				.output(0.12f, ModItems.GLYPH_6.get())
				.output(0.12f, ModItems.GLYPH_7.get())
				.output(0.12f, ModItems.GLYPH_8.get())
				.output(0.02f, com.simibubi.create.AllItems.EXP_NUGGET)
				.build(recipeOutput);

		// Soul Infusion: Soul Shard from Amethyst Shard
		new StandardProcessingRecipe.Builder<>(SoulInfusionRecipe::new, SoulHarvestMod.id("soul_infusion_soul_shard"))
				.require(Items.AMETHYST_SHARD)
				.output(0.35f, ModItems.SOUL_SHARD.get())
				.build(recipeOutput);

		// Soul Infusion: Enchant Inlaid Lapis Lazuli
		new StandardProcessingRecipe.Builder<>(SoulInfusionRecipe::new, SoulHarvestMod.id("soul_infusion_polished_inlaid_lapis"))
				.require(ModItems.POLISHED_INLAID_LAPIS.get())
				.output(0.25f, ModItems.ENCHANTED_INLAID_LAPIS.get())
				.output(0.75f, ModItems.POLISHED_INLAID_LAPIS.get())
				.build(recipeOutput);

		// Soul Infusion: Overload Glyph Page
		new StandardProcessingRecipe.Builder<>(SoulInfusionRecipe::new, SoulHarvestMod.id("soul_infusion_overload_glyph_page"))
				.require(ModTags.Items.GLYPH_PAGES)
				.output(0.95f, ModItems.GLYPH_OVERLOADED.get())
				.output(0.025f, com.simibubi.create.AllItems.EXP_NUGGET)
				.build(recipeOutput);
	}

	private RecipeOutput withUniformSerializer(final RecipeOutput output) {
		return withUniformSerializer(output, null);
	}

	private RecipeOutput withUniformSerializer(final RecipeOutput output, @Nullable final String tooltipKey) {
		return new RecipeOutput() { 
			@Override
			public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
				if (recipe instanceof SequencedAssemblyRecipe saRecipe) {
					output.accept(id, new UniformSequencedAssemblyRecipe(saRecipe, tooltipKey), advancement, conditions);
				} else {
					output.accept(id, recipe, advancement, conditions);
				}
			}

            @Override
            public Advancement.Builder advancement() {
                return output.advancement();
            }
		};
	}
}
//?}
