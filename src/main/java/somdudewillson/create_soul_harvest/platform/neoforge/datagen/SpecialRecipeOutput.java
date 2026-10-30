package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SpecialRecipeOutput implements RecipeOutput {
	private final CachedOutput output;
	private final HolderLookup.Provider registries;
	private final Set<ResourceLocation> registeredRecipes;
	private final List<CompletableFuture<?>> futures;
	private final PackOutput.PathProvider recipePathProvider;
	private final PackOutput.PathProvider advancementPathProvider;
	private final PackOutput.PathProvider soulDataPathProvider;
	private final List<ICondition> defaultConditions;

	public SpecialRecipeOutput(
			CachedOutput output,
			HolderLookup.Provider registries,
			Set<ResourceLocation> registeredRecipes,
			List<CompletableFuture<?>> futures,
			PackOutput.PathProvider recipePathProvider,
			PackOutput.PathProvider advancementPathProvider
	) {
		this(output, registries, registeredRecipes, futures, recipePathProvider, advancementPathProvider, null, Collections.emptyList());
	}

	public SpecialRecipeOutput(
			CachedOutput output,
			HolderLookup.Provider registries,
			Set<ResourceLocation> registeredRecipes,
			List<CompletableFuture<?>> futures,
			PackOutput.PathProvider recipePathProvider,
			PackOutput.PathProvider advancementPathProvider,
			@Nullable PackOutput.PathProvider soulDataPathProvider
	) {
		this(output, registries, registeredRecipes, futures, recipePathProvider, advancementPathProvider, soulDataPathProvider, Collections.emptyList());
	}

	public SpecialRecipeOutput(
			CachedOutput output,
			HolderLookup.Provider registries,
			Set<ResourceLocation> registeredRecipes,
			List<CompletableFuture<?>> futures,
			PackOutput.PathProvider recipePathProvider,
			PackOutput.PathProvider advancementPathProvider,
			List<ICondition> defaultConditions
	) {
		this(output, registries, registeredRecipes, futures, recipePathProvider, advancementPathProvider, null, defaultConditions);
	}

	public SpecialRecipeOutput(
			CachedOutput output,
			HolderLookup.Provider registries,
			Set<ResourceLocation> registeredRecipes,
			List<CompletableFuture<?>> futures,
			PackOutput.PathProvider recipePathProvider,
			PackOutput.PathProvider advancementPathProvider,
			@Nullable PackOutput.PathProvider soulDataPathProvider,
			List<ICondition> defaultConditions
	) {
		this.output = output;
		this.registries = registries;
		this.registeredRecipes = registeredRecipes;
		this.futures = futures;
		this.recipePathProvider = recipePathProvider;
		this.advancementPathProvider = advancementPathProvider;
		this.soulDataPathProvider = soulDataPathProvider;
		this.defaultConditions = defaultConditions;
	}

	public HolderLookup.Provider registries() {
		return registries;
	}

	@Override
	public Advancement.Builder advancement() {
		return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
	}

	@Override
	public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
		if (!registeredRecipes.add(id)) {
			throw new IllegalStateException("Duplicate recipe " + id);
		}

		ICondition[] allConditions = mergeConditions(conditions);
		futures.add(DataProvider.saveStable(
				output,
				registries,
				Recipe.CONDITIONAL_CODEC,
				Optional.of(new WithConditions<>(recipe, allConditions)),
				recipePathProvider.json(id)
		));

		if (advancement != null) {
			futures.add(DataProvider.saveStable(
					output,
					registries,
					Advancement.CONDITIONAL_CODEC,
					Optional.of(new WithConditions<>(advancement.value(), allConditions)),
					advancementPathProvider.json(advancement.id())
			));
		}
	}

	public void acceptRaw(ResourceLocation id, JsonObject json, ICondition... conditions) {
		if (!registeredRecipes.add(id)) {
			throw new IllegalStateException("Duplicate recipe " + id);
		}

		ICondition[] allConditions = mergeConditions(conditions);
		if (allConditions.length > 0) {
			ICondition.writeConditions(registries, json, allConditions);
		}

		futures.add(DataProvider.saveStable(output, json, recipePathProvider.json(id)));
	}

	public void acceptSoulData(ResourceLocation id, JsonObject json) {
		if (soulDataPathProvider == null) {
			throw new IllegalStateException("soulDataPathProvider is not configured on SpecialRecipeOutput");
		}
		futures.add(DataProvider.saveStable(output, json, soulDataPathProvider.json(id)));
	}

	@Override
	public SpecialRecipeOutput withConditions(ICondition... conditions) {
		List<ICondition> combined = new ArrayList<>(this.defaultConditions);
		Collections.addAll(combined, conditions);
		return new SpecialRecipeOutput(
				output,
				registries,
				registeredRecipes,
				futures,
				recipePathProvider,
				advancementPathProvider,
				soulDataPathProvider,
				combined
		);
	}

	private ICondition[] mergeConditions(ICondition... conditions) {
		if (defaultConditions.isEmpty()) {
			return conditions;
		}
		if (conditions.length == 0) {
			return defaultConditions.toArray(ICondition[]::new);
		}
		List<ICondition> combined = new ArrayList<>(defaultConditions);
		Collections.addAll(combined, conditions);
		return combined.toArray(ICondition[]::new);
	}

	public static SpecialRecipeOutput from(RecipeOutput recipeOutput) {
		if (recipeOutput instanceof SpecialRecipeOutput compatOutput) {
			return compatOutput;
		}
		throw new IllegalArgumentException("Expected recipeOutput to be an instance of SpecialRecipeOutput, but got: " + recipeOutput.getClass().getName());
	}
}
//?}
