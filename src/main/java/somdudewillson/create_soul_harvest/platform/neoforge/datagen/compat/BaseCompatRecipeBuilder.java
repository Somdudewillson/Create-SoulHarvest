package somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat;

//? neoforge {

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCompatRecipeBuilder<T extends BaseCompatRecipeBuilder<T>> {
	protected final String targetModId;
	protected final List<ICondition> extraConditions = new ArrayList<>();
	protected boolean autoModLoadedCondition = true;

	protected BaseCompatRecipeBuilder(String targetModId) {
		this.targetModId = targetModId;
	}

	@SuppressWarnings("unchecked")
	protected T self() {
		return (T) this;
	}

	public T whenModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public T withCondition(ICondition condition) {
		this.extraConditions.add(condition);
		return self();
	}

	public T withoutAutoModLoadedCondition() {
		this.autoModLoadedCondition = false;
		return self();
	}

	public abstract JsonObject serializeRecipe();

	public abstract ResourceLocation getDefaultId();

	public void save(RecipeOutput recipeOutput) {
		save(recipeOutput, getDefaultId());
	}

	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		CompatRecipeOutput compatOutput = CompatRecipeOutput.from(recipeOutput);
		JsonObject json = serializeRecipe();

		List<ICondition> allConditions = new ArrayList<>();
		if (this.autoModLoadedCondition && this.targetModId != null && !this.targetModId.isBlank()) {
			boolean alreadyPresent = this.extraConditions.stream().anyMatch(c ->
					c instanceof ModLoadedCondition mlc && mlc.modid().equals(this.targetModId));
			if (!alreadyPresent) {
				allConditions.add(new ModLoadedCondition(this.targetModId));
			}
		}
		allConditions.addAll(this.extraConditions);

		compatOutput.acceptRaw(id, json, allConditions.toArray(ICondition[]::new));
	}
}
//?}
