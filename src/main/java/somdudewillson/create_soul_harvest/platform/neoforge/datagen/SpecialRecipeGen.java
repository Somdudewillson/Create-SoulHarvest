package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.AbsoluteIceRecipe;
import somdudewillson.create_soul_harvest.content.recipe.BiocideConversionRecipe;
import somdudewillson.create_soul_harvest.content.recipe.BlockCondition;
import somdudewillson.create_soul_harvest.content.recipe.ConversionResult;
import somdudewillson.create_soul_harvest.content.recipe.IntRange;
import somdudewillson.create_soul_harvest.content.recipe.MidasFluidBoostRecipe;
import somdudewillson.create_soul_harvest.content.soul.DropRestriction;
import somdudewillson.create_soul_harvest.content.soul.SoulDropEntry;
import somdudewillson.create_soul_harvest.content.soul.SoulDropPool;
import somdudewillson.create_soul_harvest.registry.ModFluids;
import somdudewillson.create_soul_harvest.registry.ModTags;

public final class SpecialRecipeGen {
	private SpecialRecipeGen() {
	}

	public static void register(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		registerMidasFluidBoostRecipes(recipeOutput);
		registerAbsoluteIceRecipes(recipeOutput);
		registerBiocideRecipes(recipeOutput);
		registerPartialSoulData(recipeOutput);
	}

	public static void registerMidasFluidBoostRecipes(RecipeOutput recipeOutput) {
		recipeOutput.accept(
				SoulHarvestMod.id("midas_fluid_boost/liquid_soul_crystal"),
				new MidasFluidBoostRecipe(
						SizedFluidIngredient.of(ModFluids.LIQUID_SOUL_CRYSTAL.get(), 12),
						12000,
						4.0f
				),
				null
		);
	}

	public static void registerAbsoluteIceRecipes(RecipeOutput recipeOutput) {
		recipeOutput.accept(
				SoulHarvestMod.id("absolute_ice_conversion/lava_to_obsidian"),
				new AbsoluteIceRecipe(
						BlockCondition.of(Blocks.LAVA),
						Optional.empty(),
						ConversionResult.setBlock(Blocks.OBSIDIAN),
						Optional.of(0.9),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty()
				),
				null
		);

		recipeOutput.accept(
				SoulHarvestMod.id("absolute_ice_conversion/water_to_ice"),
				new AbsoluteIceRecipe(
						BlockCondition.of(Blocks.WATER),
						Optional.of(BlockCondition.ofFluid(Fluids.WATER).inverted()),
						ConversionResult.setBlock(Blocks.ICE),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty()
				),
				null
		);

		recipeOutput.accept(
				SoulHarvestMod.id("absolute_ice_conversion/break_small_plants"),
				new AbsoluteIceRecipe(
						BlockCondition.of(ModTags.Blocks.C_SMALL_PLANTS_THAT_FREEZE),
						Optional.empty(),
						ConversionResult.destroy(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty()
				),
				null
		);
	}

	public static void registerBiocideRecipes(RecipeOutput recipeOutput) {
		recipeOutput.accept(
				SoulHarvestMod.id("biocide_conversion/convert_to_dirt"),
				new BiocideConversionRecipe(
						BlockCondition.of(ModTags.Blocks.BIOCIDE_CONVERT_TO_DIRT),
						Optional.of(ConversionResult.setBlock(Blocks.DIRT)),
						Optional.of(1),
						IntRange.of(0, 2),
						false,
						IntRange.ZERO,
						Optional.of(List.of(Direction.DOWN))
				),
				null
		);

		recipeOutput.accept(
				SoulHarvestMod.id("biocide_conversion/consume_high"),
				new BiocideConversionRecipe(
						BlockCondition.of(ModTags.Blocks.BIOCIDE_HIGH_CONSUME),
						Optional.empty(),
						Optional.of(1),
						IntRange.ZERO,
						true,
						IntRange.of(4, 9),
						Optional.empty()
				),
				null
		);

		recipeOutput.accept(
				SoulHarvestMod.id("biocide_conversion/consume_medium"),
				new BiocideConversionRecipe(
						BlockCondition.of(ModTags.Blocks.BIOCIDE_MEDIUM_CONSUME),
						Optional.empty(),
						Optional.of(1),
						IntRange.ZERO,
						true,
						IntRange.of(2, 6),
						Optional.empty()
				),
				null
		);

		recipeOutput.accept(
				SoulHarvestMod.id("biocide_conversion/consume_low"),
				new BiocideConversionRecipe(
						BlockCondition.of(ModTags.Blocks.BIOCIDE_LOW_CONSUME),
						Optional.empty(),
						Optional.of(1),
						IntRange.ZERO,
						true,
						IntRange.of(0, 4),
						Optional.empty()
				),
				null
		);
	}

	public static void registerPartialSoulData(RecipeOutput recipeOutput) {
		SpecialRecipeOutput compatOutput = SpecialRecipeOutput.from(recipeOutput);

		JsonObject witherJson = new JsonObject();
		witherJson.addProperty("partial", true);
		witherJson.addProperty("xp", 50);

		SoulDropEntry netherStarEntry = new SoulDropEntry(
				BuiltInRegistries.ITEM.getKey(Items.NETHER_STAR),
				100,
				1,
				1,
				DropRestriction.STANDARD
		);
		SoulDropPool pool = new SoulDropPool("main", 1, List.of(netherStarEntry));

		JsonArray poolsArray = new JsonArray();
		poolsArray.add(pool.toJson());
		witherJson.add("pools", poolsArray);

		compatOutput.acceptSoulData(SoulHarvestMod.id("minecraft/wither"), witherJson);
	}
}
//?}
