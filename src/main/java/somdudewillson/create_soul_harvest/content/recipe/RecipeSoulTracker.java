package somdudewillson.create_soul_harvest.content.recipe;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;

import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.item.PolyclayAttunedItem;
import somdudewillson.create_soul_harvest.content.recipe.grinding.MobGrindingDynamicData;
import somdudewillson.create_soul_harvest.content.recipe.ingredient.AttunedPolyclayIngredient;

//? >= 1.21 {
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
//?}

public class RecipeSoulTracker {

	private static final Set<ResourceLocation> USED_SOULS = ConcurrentHashMap.newKeySet();
	private static final Set<ResourceLocation> ENHANCED_SOULS = ConcurrentHashMap.newKeySet();

	public static boolean isSoulUsedInRecipes(ResourceLocation entityId) {
		return entityId != null && USED_SOULS.contains(entityId);
	}

	public static boolean requiresEnhanced(ResourceLocation entityId) {
		return entityId != null && ENHANCED_SOULS.contains(entityId);
	}

	public static Set<ResourceLocation> getUsedSouls() {
		return Collections.unmodifiableSet(USED_SOULS);
	}

	public static void registerUsedSoul(ResourceLocation soul, boolean enhanced) {
		if (soul == null) return;
		USED_SOULS.add(soul);
		if (enhanced) {
			ENHANCED_SOULS.add(soul);
		}
	}

	public static void clear() {
		USED_SOULS.clear();
		ENHANCED_SOULS.clear();
	}

	//? >= 1.21 {
	public static void scan(RecipeManager recipeManager) {
		if (recipeManager == null) return;
		clear();
		for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
			if (isIgnoredRecipe(holder.id())) {
				continue;
			}
			scanRecipe(holder.value());
		}
	}
	//?} else {
	/*public static void scan(RecipeManager recipeManager) {
		if (recipeManager == null) return;
		clear();
		for (Recipe<?> recipe : recipeManager.getRecipes()) {
			if (isIgnoredRecipe(recipe.getId())) {
				continue;
			}
			scanRecipe(recipe);
		}
	}
	*///?}

	public static boolean isIgnoredRecipe(ResourceLocation id) {
		return MobGrindingDynamicData.isGeneratedProcessingRecipe(id);
	}

	public static void scanRecipe(ResourceLocation id, Recipe<?> recipe) {
		if (isIgnoredRecipe(id)) return;
		scanRecipe(recipe);
	}

	public static void scanRecipe(Recipe<?> recipe) {
		if (recipe == null) return;
		for (Ingredient ingredient : recipe.getIngredients()) {
			scanIngredient(ingredient);
		}
		if (recipe instanceof SequencedAssemblyRecipe seq) {
			for (SequencedRecipe<?> step : seq.getSequence()) {
				if (step != null && step.getRecipe() != null) {
					scanRecipe(step.getRecipe());
				}
			}
		}
	}

	private static void scanIngredient(Ingredient ingredient) {
		if (ingredient == null) return;

		//? >= 1.21 {
		ICustomIngredient custom = ingredient.getCustomIngredient();
		if (custom instanceof AttunedPolyclayIngredient polyclayIng) {
			recordPolyclayIngredient(polyclayIng);
			return;
		}
		//?} else {
		/*if (ingredient instanceof AttunedPolyclayIngredient polyclayIng) {
			recordPolyclayIngredient(polyclayIng);
			return;
		}
		*///?}

		// Fallback: check items in standard ingredient for PolyclayAttunedItem stacks
		try {
			for (ItemStack stack : ingredient.getItems()) {
				if (stack != null && !stack.isEmpty() && stack.getItem() instanceof PolyclayAttunedItem) {
					ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
					if (heldSoul != null) {
						boolean isEnhanced = ISoulHolder.isEnhanced(stack);
						registerUsedSoul(heldSoul, isEnhanced);
					}
				}
			}
		} catch (Throwable ignored) {
		}
	}

	private static void recordPolyclayIngredient(AttunedPolyclayIngredient polyclayIng) {
		boolean enhanced = Boolean.TRUE.equals(polyclayIng.getEnhanced());
		if (polyclayIng.getSoul() != null) {
			registerUsedSoul(polyclayIng.getSoul(), enhanced);
		} else if (polyclayIng.getSoulTag() != null) {
			Optional<HolderSet.Named<EntityType<?>>> tag = BuiltInRegistries.ENTITY_TYPE.getTag(polyclayIng.getSoulTag());
			if (tag.isPresent()) {
				for (Holder<EntityType<?>> holder : tag.get()) {
					holder.unwrapKey().ifPresent(key -> registerUsedSoul(key.location(), enhanced));
				}
			}
		}
	}
}
