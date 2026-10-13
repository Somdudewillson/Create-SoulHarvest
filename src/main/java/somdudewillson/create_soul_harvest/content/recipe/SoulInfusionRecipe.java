package somdudewillson.create_soul_harvest.content.recipe;

import java.util.List;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

import lombok.Getter;
import lombok.Setter;

//? >= 1.21 {
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
//?} else {
/*import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
*///?}

//? >= 1.21 {
@ParametersAreNonnullByDefault
public class SoulInfusionRecipe extends StandardProcessingRecipe<SingleRecipeInput> implements ISelectiveOutputRecipe {
//?} else {
/*public class SoulInfusionRecipe extends ProcessingRecipe<SoulInfusionRecipe.SoulInfusionWrapper> implements ISelectiveOutputRecipe {
*///?}
	@Getter
	@Setter
	private OutputSelectionMode outputSelectionMode = OutputSelectionMode.INDEPENDENT;

	public SoulInfusionRecipe(ProcessingRecipeParams params) {
		super(ModRecipeTypes.SOUL_INFUSION, params);
	}

	@Override
	public List<ItemStack> rollResults(List<ProcessingOutput> rollableResults, RandomSource randomSource) {
		return ISelectiveOutputRecipe.super.rollResults(rollableResults, randomSource);
	}
	
	//? < 1.21 {
	/*@Override
	public List<ItemStack> rollResults(List<ProcessingOutput> rollableResults) {
		return ISelectiveOutputRecipe.super.rollResults(rollableResults);
	}
	*///?}

	//? >= 1.21 {
	@Override
	public boolean matches(SingleRecipeInput inv, Level level) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0).test(inv.getItem(0));
	}
	//?} else {
	/*@Override
	public boolean matches(SoulInfusionWrapper inv, Level level) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0).test(inv.getItem(0));
	}
	*///?}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 12;
	}
	
	//? < 1.21 {
	/*public static class SoulInfusionWrapper extends RecipeWrapper {
		public SoulInfusionWrapper() {
			super(new ItemStackHandler(1));
		}
	}
	*///?}
}
