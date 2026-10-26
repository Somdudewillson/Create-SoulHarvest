package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlocks;
//? neoforge {
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.GlyphScrollItem;
import somdudewillson.create_soul_harvest.content.recipe.ISelectiveOutputRecipe;
import somdudewillson.create_soul_harvest.content.recipe.SelectiveSplashingRecipe;
import somdudewillson.create_soul_harvest.content.recipe.SoulInfusionRecipe;
import somdudewillson.create_soul_harvest.content.recipe.UniformSequencedAssemblyRecipe;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModFluids;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModTags;

public final class CreateRecipeGen {
	public static final Logger LOGGER = LogUtils.getLogger();
	private static final int GLYPH_COUNT = 8;

	private CreateRecipeGen() {
	}

	public static void register(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		registerMechanicalCraftingRecipes(recipeOutput);
		registerCompactingRecipes(recipeOutput);
		registerDeployingRecipes(recipeOutput);
		registerMixingRecipes(recipeOutput);
		registerSandpaperPolishingRecipes(recipeOutput);
		registerSequencedAssemblyRecipes(recipeOutput);
		registerMillingRecipes(recipeOutput);
		registerCrushingRecipes(recipeOutput);
		registerSplashingRecipes(recipeOutput);
		registerCuttingRecipes(recipeOutput);
		registerPressingRecipes(recipeOutput);
		registerSoulInfusionRecipes(recipeOutput);
		
		registerSoulBookRecipes(recipeOutput);
		registerGlyphFoldingRecipes(recipeOutput);
		registerEnhancedSpellRecipes(recipeOutput);
	}

	public static void registerMechanicalCraftingRecipes(RecipeOutput recipeOutput) {
		// Loot Token Blank
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModItems.LOOT_TOKEN_BLANK.get(), 1)
				.key('N', Items.NETHERITE_INGOT)
				.key('G', ModItems.MAGIC_GOLD_CHUNK.get())
				.patternLine(" GGG ")
				.patternLine("GGNGG")
				.patternLine("GNNNG")
				.patternLine("GGNGG")
				.patternLine(" GGG ")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_loot_token_blank_netherite"));
		
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModItems.LOOT_TOKEN_BLANK.get(), 1)
				.key('N', ModItems.NECRONIUM_PLATE.get())
				.key('G', ModItems.MAGIC_GOLD_CHUNK.get())
				.patternLine(" GGG ")
				.patternLine("GGNGG")
				.patternLine("GNNNG")
				.patternLine("GGNGG")
				.patternLine(" GGG ")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_loot_token_blank_necronium"));

		// Midas Core
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModBlocks.MIDAS_CORE.get(), 1)
				.key('I', ModItems.ENCHANTED_INLAID_LAPIS.get())
				.key('G', Items.GOLD_BLOCK)
				.key('B', AllItems.BRASS_INGOT)
				.patternLine(" BBB ")
				.patternLine("BGGGB")
				.patternLine("BGIGB")
				.patternLine("BGGGB")
				.patternLine(" BBB ")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_midas_core"));

		// Brass Polyclay
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModItems.POLYCLAY_BRASS.get(), 1)
				.key('N', ModItems.POLYCLAY.get())
				.key('C', Items.CLAY_BALL)
				.key('|', ModItems.SOUL_SHARD.get())
				.key('B', AllItems.BRASS_INGOT)
				.patternLine("BC CB")
				.patternLine("BC|CB")
				.patternLine("NN|NN")
				.patternLine("BC|CB")
				.patternLine("BBCBB")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_polyclay_brass"));

		// Glyph Scrolls 1..8
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
		for (int i = 0; i < GLYPH_COUNT; i++) {
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

		// Necronium Charm
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModItems.NECRONIUM_CHARM.get(), 1)
				.key('=', ModItems.NECRONIUM_PLATE.get())
				.key('C', ModItems.MAGIC_GOLD.get())
				.key('S', ModItems.ENCHANTED_INLAID_LAPIS.get())
				.key('|', AllBlocks.FLUID_PIPE)
				.key('+', AllItems.PRECISION_MECHANISM)
				.patternLine("C= =C")
				.patternLine("C|S|C")
				.patternLine("C+=+C")
				.patternLine(" CCC ")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_necronium_charm"));
	}

	public static void registerCompactingRecipes(RecipeOutput recipeOutput) {
		// Absolute Ice
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

		// Biofuel Block
		new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id("create_compact_bioslurry_to_biofuel"))
				.require(ModFluids.BIOSLURRY.get(), 1000)
				.requiresHeat(HeatCondition.HEATED)
				.output(ModBlocks.BIOFUEL_BLOCK.get())
				.build(recipeOutput);

		// Inlaid Lapis Plate
		new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id("create_compact_inlaid_lapis_to_plate"))
				.require(Items.PAPER)
				.require(ModItems.INLAID_LAPIS.get())
				.require(Items.PAPER)
				.output(ModItems.INLAID_LAPIS_PLATE.get())
				.build(recipeOutput);

		// Magic Gold Chunk
		new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id("create_compact_magic_gold"))
				.require(ModItems.MAGIC_GOLD.get())
				.require(ModItems.MAGIC_GOLD.get())
				.require(ModItems.MAGIC_GOLD.get())
				.require(ModItems.MAGIC_GOLD.get())
				.require(ModItems.MAGIC_GOLD.get())
				.require(ModItems.MAGIC_GOLD.get())
				.require(ModItems.MAGIC_GOLD.get())
				.require(ModItems.MAGIC_GOLD.get())
				.requiresHeat(HeatCondition.SUPERHEATED)
				.output(ModItems.MAGIC_GOLD_CHUNK.get())
				.build(recipeOutput);

		// Necronium Ingot
		new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id("create_compact_alloy_necronium"))
				.require(ModFluids.WITHER_SLURRY.get(), 1000)
				.require(Fluids.LAVA, 1000)
				.require(AllItems.BRASS_INGOT)
				.require(AllItems.POWDERED_OBSIDIAN)
				.require(AllItems.POWDERED_OBSIDIAN)
				.require(AllItems.POWDERED_OBSIDIAN)
				.require(AllItems.POWDERED_OBSIDIAN)
				.requiresHeat(HeatCondition.SUPERHEATED)
				.output(0.9f, ModItems.NECRONIUM_INGOT.get())
				.output(0.2f, ModItems.NECRONIUM_WASTE.get())
				.build(recipeOutput);
		new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id("create_compact_alloy_necronium_reuse_waste"))
				.require(ModFluids.WITHER_SLURRY.get(), 1000)
				.require(Fluids.LAVA, 1000)
				.require(AllItems.BRASS_INGOT)
				.require(ModItems.NECRONIUM_WASTE.get())
				.require(ModItems.NECRONIUM_WASTE.get())
				.require(ModItems.NECRONIUM_WASTE.get())
				.require(ModItems.NECRONIUM_WASTE.get())
				.require(ModItems.NECRONIUM_WASTE.get())
				.require(ModItems.NECRONIUM_WASTE.get())
				.require(ModItems.NECRONIUM_WASTE.get())
				.require(ModItems.NECRONIUM_WASTE.get())
				.requiresHeat(HeatCondition.SUPERHEATED)
				.output(0.9f, ModItems.NECRONIUM_INGOT.get())
				.output(0.2f, ModItems.NECRONIUM_WASTE.get())
				.build(recipeOutput);
	}

	public static void registerDeployingRecipes(RecipeOutput recipeOutput) {
		// Carve Bone Block
		new ItemApplicationRecipe.Builder<>(DeployerApplicationRecipe::new, SoulHarvestMod.id("create_deploy_carve_bone_block"))
				.require(Blocks.BONE_BLOCK)
				.require(ItemTags.AXES)
				.toolNotConsumed()
				.output(ModBlocks.CARVED_BONE_BLOCK.get())
				.build(recipeOutput);
	}

	public static void registerMixingRecipes(RecipeOutput recipeOutput) {
		// Bioslurry to Bonemeal
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_bioslurry_to_bonemeal"))
				.require(Items.SAND)
				.require(ModFluids.BIOSLURRY.get(), 500)
				.output(Items.BONE_MEAL, 8)
				.build(recipeOutput);

		// Bioslurry to Wither Slurry
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_bioslurry_to_wither_slurry"))
				.require(Items.WITHER_ROSE)
				.require(ModFluids.BIOSLURRY.get(), 250)
				.output(ModFluids.WITHER_SLURRY.get(), 250)
				.build(recipeOutput);

		// Silica Solution from Sand
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

		// Silica Solution from Quartz
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_silica_solution_from_quartz"))
				.require(Items.QUARTZ)
				.require(Fluids.WATER, 1000)
				.output(ModFluids.SILICA_SOLUTION.get(), 120)
				.build(recipeOutput);

		// Mulch to Bioslurry
		var mulchMix = new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_mulch_to_bioslurry"));
		for (int i = 0; i < 8; i++) {
			mulchMix.require(ModItems.ORGANIC_MULCH.get());
		}
		mulchMix.require(Fluids.WATER, 250)
				.output(ModFluids.BIOSLURRY.get(), 250)
				.build(recipeOutput);

		// Bioslurry to Dirt
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_dirt_from_bioslurry"))
				.require(ItemTags.SAND)
				.require(ModFluids.BIOSLURRY.get(), 250)
				.output(Items.DIRT, 1)
				.build(recipeOutput);

		// Bioslurry to Bone Meal (Heated)
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_bone_meal_from_bioslurry"))
				.require(ItemTags.DIRT)
				.require(ModFluids.BIOSLURRY.get(), 1000)
				.requiresHeat(HeatCondition.HEATED)
				.output(Items.BONE_MEAL, 9)
				.build(recipeOutput);

		// Wither Slurry to Wither Rose
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_wither_slurry_to_wither_rose"))
				.require(ItemTags.FLOWERS)
				.require(ModFluids.WITHER_SLURRY.get(), 125)
				.require(Fluids.LAVA, 125)
				.output(Items.WITHER_ROSE, 1)
				.build(recipeOutput);

		// Wither Slurry to Wither Skull
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_wither_slurry_to_wither_skull"))
				.require(ModBlocks.CARVED_BONE_BLOCK.get())
				.require(ModFluids.WITHER_SLURRY.get(), 1000)
				.require(Fluids.LAVA, 1000)
				.output(Items.WITHER_SKELETON_SKULL, 1)
				.build(recipeOutput);

		// Soul Shards to Liquid Soul Crystal
		new StandardProcessingRecipe.Builder<>(MixingRecipe::new, SoulHarvestMod.id("create_mix_soul_shards_to_liquid_soul_crystal"))
				.require(ModItems.SOUL_SHARD.get())
				.require(ModFluids.SILICA_SOLUTION.get(), 24)
				.requiresHeat(HeatCondition.HEATED)
				.output(ModFluids.LIQUID_SOUL_CRYSTAL.get(), 12)
				.build(recipeOutput);
	}

	public static void registerSandpaperPolishingRecipes(RecipeOutput recipeOutput) {
		new StandardProcessingRecipe.Builder<>(SandPaperPolishingRecipe::new, SoulHarvestMod.id("create_polish_inlaid_lapis"))
				.require(ModItems.INLAID_LAPIS.get())
				.output(ModItems.POLISHED_INLAID_LAPIS.get())
				.build(recipeOutput);
	}

	public static void registerSequencedAssemblyRecipes(RecipeOutput recipeOutput) {
		// Print Glyph Page
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_print_glyph_page"))
				.require(Items.PAPER)
				.transitionTo(Items.PAPER)
				.loops(2)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.POWDERED_OBSIDIAN))
				.addStep(PressingRecipe::new, rb -> rb)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ModItems.INLAID_LAPIS_PLATE.get()))
				.addStep(PressingRecipe::new, rb -> rb)
				.addStep(FillingRecipe::new, rb -> rb.require(ModFluids.LIQUID_SOUL_CRYSTAL.get(), 96))
				.addOutput(ModItems.GLYPH_1.get(), 0.125f)
				.addOutput(ModItems.GLYPH_2.get(), 0.125f)
				.addOutput(ModItems.GLYPH_3.get(), 0.125f)
				.addOutput(ModItems.GLYPH_4.get(), 0.125f)
				.addOutput(ModItems.GLYPH_5.get(), 0.125f)
				.addOutput(ModItems.GLYPH_6.get(), 0.125f)
				.addOutput(ModItems.GLYPH_7.get(), 0.125f)
				.addOutput(ModItems.GLYPH_8.get(), 0.125f)
				.build(withUniformSerializer(recipeOutput, "create_soul_harvest.jei.glyph_page_random_output"));

		// Molten Lapis Core
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_sequenced_molten_lapis_core"))
				.require(ModItems.POLISHED_INLAID_LAPIS.get())
				.transitionTo(ModItems.POLISHED_INLAID_LAPIS.get())
				.loops(8)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.BLAZE_POWDER))
				.addStep(FillingRecipe::new, rb -> rb.require(Fluids.LAVA, 250))
				.addOutput(ModItems.MOLTEN_LAPIS_CORE.get(), 1.0f)
				.build(recipeOutput);

		// Necronium Waste Bottle
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_sequenced_fill_necronium_waste_bottle"))
				.require(Items.GLASS_BOTTLE)
				.transitionTo(Items.GLASS_BOTTLE)
				.loops(8)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ModItems.NECRONIUM_WASTE.get()))
				.addOutput(ModItems.NECRONIUM_WASTE_BOTTLE.get(), 1.0f)
				.build(recipeOutput);

		// Soul Mechanism
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_sequenced_soul_mechanism"))
				.require(ModItems.NECRONIUM_PLATE.get())
				.transitionTo(ModItems.SOUL_MECHANISM_INCOMPLETE.get())
				.loops(4)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.COGWHEEL))
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ModItems.ENCHANTED_INLAID_LAPIS.get()))
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ModItems.SOUL_SHARD.get()))
				.addOutput(ModItems.SOUL_MECHANISM.get(), 0.7f)
				.addOutput(ModItems.NECRONIUM_WASTE.get(), 0.1f)
				.addOutput(ModItems.ENCHANTED_INLAID_LAPIS.get(), 0.05f)
				.addOutput(ModItems.POLISHED_INLAID_LAPIS.get(), 0.03f)
				.addOutput(ModItems.INLAID_LAPIS.get(), 0.07f)
				.addOutput(AllBlocks.COGWHEEL, 0.023f)
				.addOutput(ModItems.SOUL_SHARD.get(), 0.02f)
				.addOutput(ModItems.NECRONIUM_PLATE.get(), 0.01f)
				.build(recipeOutput);
	}
	
	public static void registerSoulBookRecipes(RecipeOutput recipeOutput) {
		TagKey<Item> glyphPagesTag = ModTags.Items.GLYPH_PAGES;
		
		// Glyph Page Sheaf
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModItems.GLYPH_SHEAF.get(), 1)
				.key('1', ModItems.GLYPH_1.get())
				.key('2', ModItems.GLYPH_2.get())
				.key('3', ModItems.GLYPH_3.get())
				.key('4', ModItems.GLYPH_4.get())
				.key('5', ModItems.GLYPH_5.get())
				.key('6', ModItems.GLYPH_6.get())
				.key('7', ModItems.GLYPH_7.get())
				.key('8', ModItems.GLYPH_8.get())
				.patternLine("12345678")
				.build(recipeOutput, SoulHarvestMod.id("craft_glyph_sheaf_horizontal"));
		MechanicalCraftingRecipeBuilder.shapedRecipe(ModItems.GLYPH_SHEAF.get(), 1)
				.key('1', ModItems.GLYPH_1.get())
				.key('2', ModItems.GLYPH_2.get())
				.key('3', ModItems.GLYPH_3.get())
				.key('4', ModItems.GLYPH_4.get())
				.key('5', ModItems.GLYPH_5.get())
				.key('6', ModItems.GLYPH_6.get())
				.key('7', ModItems.GLYPH_7.get())
				.key('8', ModItems.GLYPH_8.get())
				.patternLine("8")
				.patternLine("7")
				.patternLine("6")
				.patternLine("5")
				.patternLine("4")
				.patternLine("3")
				.patternLine("2")
				.patternLine("1")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("craft_glyph_sheaf_vertical"));
		
		// Simple
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_assemble_soul_book_simple"))
				.require(ModItems.SOUL_BOOK_COVER_SIMPLE.get())
				.transitionTo(ModItems.SOUL_BOOK_INCOMPLETE_SIMPLE.get())
				.loops(8)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(glyphPagesTag))
				.addOutput(ModItems.SOUL_BOOK_SIMPLE.get(), 1.0f)
				.build(recipeOutput);
		
		// Standard
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_assemble_soul_book_standard"))
				.require(ModItems.SOUL_BOOK_COVER_STANDARD.get())
				.transitionTo(ModItems.SOUL_BOOK_INCOMPLETE_STANDARD.get())
				.loops(8)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(glyphPagesTag))
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(glyphPagesTag))
				.addStep(PressingRecipe::new, rb -> rb)
				.addOutput(ModItems.SOUL_BOOK_STANDARD.get(), 1.0f)
				.build(recipeOutput);
		
		// Superior
		new SequencedAssemblyRecipeBuilder(SoulHarvestMod.id("create_assemble_soul_book_superior"))
				.require(ModItems.SOUL_BOOK_COVER_SUPERIOR.get())
				.transitionTo(ModItems.SOUL_BOOK_INCOMPLETE_SUPERIOR.get())
				.loops(4)
				.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ModItems.GLYPH_SHEAF.get()))
				.addStep(FillingRecipe::new, rb -> rb.require(ModFluids.LIQUID_SOUL_CRYSTAL.get(), 24))
				.addStep(PressingRecipe::new, rb -> rb)
				.addOutput(ModItems.SOUL_BOOK_SUPERIOR.get(), 1.0f)
				.build(recipeOutput);
	}

	public static void registerMillingRecipes(RecipeOutput recipeOutput) {
		new StandardProcessingRecipe.Builder<>(MillingRecipe::new, SoulHarvestMod.id("mill_soul_shard"))
				.require(ModItems.SOUL_SHARD.get())
				.output(0.05f, AllItems.EXP_NUGGET)
				.build(recipeOutput);
		
		new StandardProcessingRecipe.Builder<>(MillingRecipe::new, SoulHarvestMod.id("mill_necronium_waste_bottle"))
				.require(ModItems.NECRONIUM_WASTE_BOTTLE.get())
				.output(1f, ModItems.NECRONIUM_WASTE.get(), 8)
				.build(recipeOutput);
	}

	public static void registerCrushingRecipes(RecipeOutput recipeOutput) {
		new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, SoulHarvestMod.id("crush_necronium_ingot"))
				.require(ModItems.NECRONIUM_INGOT.get())
				.output(1f, ModItems.NECRONIUM_WASTE.get())
				.output(0.438f, ModItems.NECRONIUM_WASTE.get(), 3)
				.output(0.438f, ModItems.NECRONIUM_WASTE.get())
				.output(0.438f, ModItems.NECRONIUM_WASTE.get())
				.output(0.438f, ModItems.NECRONIUM_WASTE.get())
				.output(0.438f, ModItems.NECRONIUM_WASTE.get())
				.output(0.438f, ModItems.NECRONIUM_WASTE.get())
				.build(recipeOutput);
		
		new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, SoulHarvestMod.id("crush_necronium_rod"))
				.require(ModItems.NECRONIUM_ROD.get())
				.output(1f, ModItems.NECRONIUM_WASTE.get())
				.output(0.4f, ModItems.NECRONIUM_WASTE.get())
				.output(0.4f, ModItems.NECRONIUM_WASTE.get())
				.output(0.4f, ModItems.NECRONIUM_WASTE.get())
				.output(0.4f, ModItems.NECRONIUM_WASTE.get())
				.output(0.4f, ModItems.NECRONIUM_WASTE.get())
				.output(0.4f, ModItems.NECRONIUM_WASTE.get())
				.build(recipeOutput);
		
		new StandardProcessingRecipe.Builder<>(CrushingRecipe::new, SoulHarvestMod.id("crush_necronium_plate"))
				.require(ModItems.NECRONIUM_PLATE.get())
				.output(1f, ModItems.NECRONIUM_WASTE.get())
				.output(0.421f, ModItems.NECRONIUM_WASTE.get(), 2)
				.output(0.421f, ModItems.NECRONIUM_WASTE.get())
				.output(0.421f, ModItems.NECRONIUM_WASTE.get())
				.output(0.421f, ModItems.NECRONIUM_WASTE.get())
				.output(0.421f, ModItems.NECRONIUM_WASTE.get())
				.output(0.421f, ModItems.NECRONIUM_WASTE.get())
				.build(recipeOutput);
	}

	public static void registerSplashingRecipes(RecipeOutput recipeOutput) {
		new ISelectiveOutputRecipe.Builder<>(SelectiveSplashingRecipe::new, SoulHarvestMod.id("create_splashing_wash_overloaded_glyph"))
				.singleOutput()
				.require(ModItems.GLYPH_OVERLOADED.get())
				.output(0.12f, ModItems.GLYPH_1.get())
				.output(0.12f, ModItems.GLYPH_2.get())
				.output(0.12f, ModItems.GLYPH_3.get())
				.output(0.12f, ModItems.GLYPH_4.get())
				.output(0.12f, ModItems.GLYPH_5.get())
				.output(0.12f, ModItems.GLYPH_6.get())
				.output(0.12f, ModItems.GLYPH_7.get())
				.output(0.12f, ModItems.GLYPH_8.get())
				.output(0.02f, AllItems.EXP_NUGGET)
				.build(recipeOutput);
	}

	public static void registerCuttingRecipes(RecipeOutput recipeOutput) {
		new StandardProcessingRecipe.Builder<>(CuttingRecipe::new, SoulHarvestMod.id("cut_necronium_ingot_to_sticks"))
				.duration(250)
				.require(ModItems.NECRONIUM_INGOT.get())
				.output(ModItems.NECRONIUM_ROD.get(), 2)
				.output(0.75f, ModItems.NECRONIUM_WASTE.get())
				.output(0.35f, ModItems.NECRONIUM_WASTE.get())
				.build(recipeOutput);
	}

	public static void registerPressingRecipes(RecipeOutput recipeOutput) {
		new StandardProcessingRecipe.Builder<>(PressingRecipe::new, SoulHarvestMod.id("press_necronium_ingot_to_plate"))
				.require(ModItems.NECRONIUM_INGOT.get())
				.output(ModItems.NECRONIUM_PLATE.get(), 1)
				.output(0.55f, ModItems.NECRONIUM_WASTE.get())
				.build(recipeOutput);
	}

	public static void registerSoulInfusionRecipes(RecipeOutput recipeOutput) {
		// Soul Shard from Amethyst Shard
		new ISelectiveOutputRecipe.Builder<>(SoulInfusionRecipe::new, SoulHarvestMod.id("soul_infusion_soul_shard"))
				.require(Items.AMETHYST_SHARD)
				.output(0.35f, ModItems.SOUL_SHARD.get())
				.build(recipeOutput);

		// Enchant Inlaid Lapis Lazuli
		new ISelectiveOutputRecipe.Builder<>(SoulInfusionRecipe::new, SoulHarvestMod.id("soul_infusion_polished_inlaid_lapis"))
				.singleOutput()
				.require(ModItems.POLISHED_INLAID_LAPIS.get())
				.output(0.25f, ModItems.ENCHANTED_INLAID_LAPIS.get())
				.output(0.5f, ModItems.POLISHED_INLAID_LAPIS.get())
				.output(0.25f, ModItems.INLAID_LAPIS.get())
				.build(recipeOutput);

		// Overload Glyph Page
		new ISelectiveOutputRecipe.Builder<>(SoulInfusionRecipe::new, SoulHarvestMod.id("soul_infusion_overload_glyph_page"))
				.singleOutput()
				.require(ModTags.Items.GLYPH_PAGES)
				.output(0.5f, ModItems.GLYPH_OVERLOADED.get())
				.output(0.0625f, AllItems.EXP_NUGGET)
				.output(0.0625f, Items.PAPER)
				.output(0.125f, ModItems.INLAID_LAPIS_PLATE.get())
				.build(recipeOutput);
	}
	
	private static final record FoldingPair(int hi, int lo) {
		private FoldingPair(int hi, int lo) {
			this.hi = hi;
			this.lo = lo;
		}
		
		public static FoldingPair of(int a, int b) {
			a %= GLYPH_COUNT;
			if (a<0) a += GLYPH_COUNT;
			b %= GLYPH_COUNT;
			if (b<0) b += GLYPH_COUNT;
			
			return new FoldingPair(Math.max(a, b), Math.min(a, b));
		}
		
		public FoldingPair applyFolds() {
			return applyFold(hi).applyFold(lo);
		}
		
		public FoldingPair applyFold(int foldingGlyphIndex) {
			boolean targetHi = (foldingGlyphIndex & 4) > 0;
			int offset = (foldingGlyphIndex & 2) == 0 ? 3 : 7;
			offset += (foldingGlyphIndex & 1) > 0 ? 1 : 0;
			
			if (targetHi) {
				return FoldingPair.of(hi + offset, lo);
			} else {
				return FoldingPair.of(hi, lo + offset);
			}
		}
		
		private static Item idxToGlyphItem(int idx) {
			switch(idx % GLYPH_COUNT) {
			case 0:
				return ModItems.GLYPH_1.get();
			case 1:
				return ModItems.GLYPH_2.get();
			case 2:
				return ModItems.GLYPH_3.get();
			case 3:
				return ModItems.GLYPH_4.get();
			case 4:
				return ModItems.GLYPH_5.get();
			case 5:
				return ModItems.GLYPH_6.get();
			case 6:
				return ModItems.GLYPH_7.get();
			case 7:
				return ModItems.GLYPH_8.get();
			}
			throw new IllegalArgumentException("Index "+idx+" is out of bounds for glyph id!");
		}
		
		public Item hiItem() {
			return idxToGlyphItem(hi);
		}
		public Item loItem() {
			return idxToGlyphItem(lo);
		}
		
		public static String prettyFoldString(FoldingPair in, FoldingPair out) {
			return String.format(
					"%d+%d->%d+%d", 
					in.hi()+1,
					in.lo()+1,
					out.hi()+1,
					out.lo()+1
					);
		}
	}
	public static void registerGlyphFoldingRecipes(RecipeOutput recipeOutput) {
		Map<FoldingPair, FoldingPair> handled = new HashMap<>();
		for (int a=0; a<GLYPH_COUNT; a++) {
			for (int b=0; b<GLYPH_COUNT; b++) {
				FoldingPair in = FoldingPair.of(a, b);
				FoldingPair out = in.applyFolds();
				
				if (handled.put(in, out) != null) continue;
				if (in.equals(out)) continue;
				
				new StandardProcessingRecipe.Builder<>(CompactingRecipe::new, SoulHarvestMod.id(String.format(
							"create_compact_glyph_fold_%d_%d_to_%d_%d",
							in.hi()+1,
							in.lo()+1,
							out.hi()+1,
							out.lo()+1
						)))
						.require(in.hiItem())
						.require(in.loItem())
						.requiresHeat(HeatCondition.HEATED)
						.output(out.hiItem())
						.output(out.loItem())
						.build(recipeOutput);
			}
		}
		
		for (int o=0; o<GLYPH_COUNT; o++) {
			List<Entry<FoldingPair, FoldingPair>> found = new ArrayList<>();
			for (Entry<FoldingPair, FoldingPair> pair : handled.entrySet()) {
				int score = pair.getValue().hi() == o ? 1 : 0;
				score += pair.getValue().lo() == o ? 1 : 0;
				
				score -= pair.getKey().hi() == o ? 1 : 0;
				score -= pair.getKey().lo() == o ? 1 : 0;
				
				if (score < 1) continue;
				
				found.add(pair);
				break;
			}
			
			if (found.isEmpty()) {
				LOGGER.warn("No folding can generate glyph " + (o+1));
			} else {
				LOGGER.info( "Positive folds for glyph "+(o+1)+": "+found.stream()
						.map(p->FoldingPair.prettyFoldString(p.getKey(), p.getValue()))
						.collect(Collectors.joining(", "))
					);
			}
		}
	}
	
	public static void registerEnhancedSpellRecipe(RecipeOutput recipeOutput, int glyphId, ItemLike extraIngredientOne, ItemLike extraIngredientTwo) {
		@SuppressWarnings("unchecked")
		DeferredHolder<Item, Item>[] glyphs = new DeferredHolder[] {
				ModItems.GLYPH_1, ModItems.GLYPH_2, ModItems.GLYPH_3, ModItems.GLYPH_4,
				ModItems.GLYPH_5, ModItems.GLYPH_6, ModItems.GLYPH_7, ModItems.GLYPH_8
		};
		@SuppressWarnings("unchecked")
		DeferredHolder<Item, GlyphScrollItem>[] scrolls = new DeferredHolder[] {
				ModItems.GLYPH_SCROLL_ENHANCED_1, ModItems.GLYPH_SCROLL_ENHANCED_2, ModItems.GLYPH_SCROLL_ENHANCED_3, ModItems.GLYPH_SCROLL_ENHANCED_4,
				ModItems.GLYPH_SCROLL_ENHANCED_5, ModItems.GLYPH_SCROLL_ENHANCED_6, ModItems.GLYPH_SCROLL_ENHANCED_7, ModItems.GLYPH_SCROLL_ENHANCED_8
		};
		
		int i = glyphId - 1;
		MechanicalCraftingRecipeBuilder.shapedRecipe(scrolls[i].get(), 1)
				.key('G', ModItems.MAGIC_GOLD_CHUNK.get())
				.key('-', ModTags.Items.C_NECRONIUM_RODS)
				.key('O', glyphs[i].get())
				.key('+', ModItems.SOUL_SHARD.get())
				.key('|', ModItems.INLAID_LAPIS_PLATE.get())
				.key('P', ModItems.NECRONIUM_PLATE.get())
				.key('1', extraIngredientOne)
				.key('2', extraIngredientTwo)
				.patternLine("G----G")
				.patternLine(" P11P ")
				.patternLine(" P++P ")
				.patternLine(" |OO| ")
				.patternLine(" |OO| ")
				.patternLine(" P++P ")
				.patternLine(" P22P ")
				.patternLine("G----G")
				.disallowMirrored()
				.build(recipeOutput, SoulHarvestMod.id("glyph_scroll_enhanced_" + glyphId));
	}
	
	public static void registerEnhancedSpellRecipes(RecipeOutput recipeOutput) {
		registerEnhancedSpellRecipe(recipeOutput, 1, Items.LIGHTNING_ROD, Items.COPPER_BLOCK);
		registerEnhancedSpellRecipe(recipeOutput, 2, Items.FIRE_CHARGE, Items.TNT);
		registerEnhancedSpellRecipe(recipeOutput, 3, Items.BONE_BLOCK, Items.BONE_BLOCK);
		registerEnhancedSpellRecipe(recipeOutput, 4, Items.FERMENTED_SPIDER_EYE, ModItems.NECRONIUM_WASTE.get());
		registerEnhancedSpellRecipe(recipeOutput, 5, Items.TURTLE_SCUTE, Items.IRON_BLOCK);
		registerEnhancedSpellRecipe(recipeOutput, 6, Items.FEATHER, Items.PHANTOM_MEMBRANE);
		registerEnhancedSpellRecipe(recipeOutput, 7, Items.GLASS, Items.OBSIDIAN);
	}

	@SuppressWarnings("unused")
	public static RecipeOutput withUniformSerializer(final RecipeOutput output) {
		return withUniformSerializer(output, null);
	}

	public static RecipeOutput withUniformSerializer(final RecipeOutput output, @Nullable final String tooltipKey) {
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
