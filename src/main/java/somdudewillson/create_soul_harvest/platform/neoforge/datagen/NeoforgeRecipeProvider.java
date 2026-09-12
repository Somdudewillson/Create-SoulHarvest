package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import somdudewillson.create_soul_harvest.ModTemplate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NeoforgeRecipeProvider extends RecipeProvider {

	public NeoforgeRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_CHICKEN)
				.requires(Items.LAVA_BUCKET)
				.requires(Items.CHICKEN)
				.unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
				.unlockedBy("has_chicken", has(Items.CHICKEN))
				.save(recipeOutput, ModTemplate.id("lava_chicken_recipe"));
	}
}
//?}
