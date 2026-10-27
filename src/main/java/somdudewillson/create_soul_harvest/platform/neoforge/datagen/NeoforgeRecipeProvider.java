package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.CompatRecipeOutput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class NeoforgeRecipeProvider extends RecipeProvider {

	private final CompletableFuture<HolderLookup.Provider> lookupProvider;

	public NeoforgeRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries);
		this.lookupProvider = registries;
	}

	@Override
	protected CompletableFuture<?> run(final CachedOutput output, final HolderLookup.Provider lookup) {
		Set<ResourceLocation> registeredRecipes = new HashSet<>();
		List<CompletableFuture<?>> futures = new ArrayList<>();
		CompatRecipeOutput compatRecipeOutput = new CompatRecipeOutput(
				output,
				lookup,
				registeredRecipes,
				futures,
				this.recipePathProvider,
				this.advancementPathProvider
		);
		this.buildRecipes(compatRecipeOutput);
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		HolderLookup.Provider lookup = this.lookupProvider.join();

		VanillaRecipeGen.register(recipeOutput, lookup);
		CreateRecipeGen.register(recipeOutput, lookup);
		CompatRecipeGen.register(recipeOutput, lookup);
		SpecialRecipeGen.register(recipeOutput, lookup);
	}
}
//?}
