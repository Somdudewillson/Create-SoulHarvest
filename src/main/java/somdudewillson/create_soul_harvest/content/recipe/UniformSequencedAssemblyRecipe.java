package somdudewillson.create_soul_harvest.content.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

import java.util.ArrayList;
import java.util.List;

//? >= 1.21 {
public class UniformSequencedAssemblyRecipe extends SequencedAssemblyRecipe {
	private String tooltipKey;

	public UniformSequencedAssemblyRecipe(SequencedAssemblyRecipeSerializer serializer) {
		super(serializer);
	}

	public UniformSequencedAssemblyRecipe(SequencedAssemblyRecipe base) {
		this(base, null);
	}

	public UniformSequencedAssemblyRecipe(SequencedAssemblyRecipe base, String tooltipKey) {
		super((SequencedAssemblyRecipeSerializer) ModRecipeTypes.UNIFORM_SEQUENCED_ASSEMBLY.getSerializer());
		this.ingredient = base.getIngredient();
		this.sequence = base.getSequence();
		this.loops = base.getLoops();
		this.transitionalItem = new ProcessingOutput(base.getTransitionalItem(), 1.0f);// TODO: Might not be ideal; consider AW or reflection usage.
		this.resultPool.addAll(base.resultPool);
		this.tooltipKey = tooltipKey;
	}
//?} else {

/*public class UniformSequencedAssemblyRecipe extends SequencedAssemblyRecipe {
	private String tooltipKey;

	public UniformSequencedAssemblyRecipe(net.minecraft.resources.ResourceLocation recipeId, SequencedAssemblyRecipeSerializer serializer) {
		super(recipeId, serializer);
	}

	public UniformSequencedAssemblyRecipe(SequencedAssemblyRecipe base) {
		this(base, null);
	}

	public UniformSequencedAssemblyRecipe(SequencedAssemblyRecipe base, String tooltipKey) {
		super(base.getId(), (SequencedAssemblyRecipeSerializer) ModRecipeTypes.UNIFORM_SEQUENCED_ASSEMBLY.getSerializer());
		this.ingredient = base.getIngredient();
		this.sequence = base.getSequence();
		this.loops = base.getLoops();
		this.transitionalItem = new ProcessingOutput(base.getTransitionalItem(), 1.0f);// TODO: Might not be ideal; consider AW or reflection usage.
		this.resultPool.addAll(base.resultPool);
		this.tooltipKey = tooltipKey;
	}
*///?}

	@Override
	public RecipeType<?> getType() {
		return AllRecipeTypes.SEQUENCED_ASSEMBLY.getType();
	}

	@Override
	public float getOutputChance() {
		return 1.0f;
	}

	public List<ItemStack> getOutputs() {
		List<ItemStack> list = new ArrayList<>();
		for (ProcessingOutput output : this.resultPool) {
			list.add(output.getStack());
		}
		return list;
	}

	public String getTooltipKey() {
		return this.tooltipKey;
	}

	public void setTooltipKey(String tooltipKey) {
		this.tooltipKey = tooltipKey;
	}

	public Component getChanceTooltip() {
		int count = this.resultPool.size();
		if (count <= 0) {
			return Component.literal("100%");
		}
		float percent = 100.0f / count;
		return Component.translatable("create_soul_harvest.jei.uniform_chance", String.format("%.1f%%", percent), count);
	}
}
