package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {
import java.util.Optional;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.AbsoluteIceRecipe;
import somdudewillson.create_soul_harvest.content.recipe.AbsoluteIceRecipe.BlockCondition;
import somdudewillson.create_soul_harvest.content.recipe.AbsoluteIceRecipe.ConversionResult;
import somdudewillson.create_soul_harvest.content.recipe.MidasFluidBoostRecipe;
import somdudewillson.create_soul_harvest.registry.ModFluids;

public final class SpecialRecipeGen {
	private SpecialRecipeGen() {
	}

	public static void register(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		registerMidasFluidBoostRecipes(recipeOutput);
		registerAbsoluteIceRecipes(recipeOutput);
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
	}
}
//?}
