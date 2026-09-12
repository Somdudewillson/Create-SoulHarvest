package somdudewillson.create_soul_harvest.platform.fabric.datagen;

//? fabric {

/*import somdudewillson.create_soul_harvest.ModTemplate;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

//? if <= 1.20.1 {
/^import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

public class ModRecipeProvider extends FabricRecipeProvider {

	public ModRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> exporter) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_CHICKEN)
				.requires(Items.LAVA_BUCKET)
				.requires(Items.CHICKEN)
				.unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
				.unlockedBy("has_chicken", has(Items.CHICKEN))
				.save(exporter, new ResourceLocation(ModTemplate.MOD_ID, "lava_chicken_recipe"));
	}

	@Override
	public @NotNull String getName() {
		return ModTemplate.MOD_ID + ":recipe_provider";
	}
}
^///?} else {
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

	private final CompletableFuture<HolderLookup.Provider> registriesFuture;

	public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
		this.registriesFuture = registriesFuture;
	}

	@Override
	public void buildRecipes(RecipeOutput recipeOutput) {
		IntRecipeProvider provider = new IntRecipeProvider(this.output, this.registriesFuture);
		provider.buildRecipes(recipeOutput);
	}

	@Override
	public @NotNull String getName() {
		return ModTemplate.MOD_ID + ":recipe_provider";
	}

	static class IntRecipeProvider extends RecipeProvider {

		public IntRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(packOutput, completableFuture);
		}

		@Override
		public void buildRecipes(RecipeOutput recipeOutput) {
			buildLavaChickenRecipe(ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_CHICKEN))
					.save(recipeOutput, "lava_chicken_recipe");
		}

		private ShapelessRecipeBuilder buildLavaChickenRecipe(ShapelessRecipeBuilder builder) {
			return builder.requires(Items.LAVA_BUCKET)
					.requires(Items.CHICKEN)
					.unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
					.unlockedBy("has_chicken", has(Items.CHICKEN));
		}
	}
}
//?}
*///?}
