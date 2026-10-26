package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NeoforgeRecipeProvider extends RecipeProvider {

	private final CompletableFuture<HolderLookup.Provider> lookupProvider;

	public NeoforgeRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries);
		this.lookupProvider = registries;
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		HolderLookup.Provider lookup = this.lookupProvider.join();

		VanillaRecipeGen.register(recipeOutput, lookup);
		CreateRecipeGen.register(recipeOutput, lookup);
		CompatRecipeGen.register(recipeOutput, lookup);
	}
}
//?}
