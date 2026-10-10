package somdudewillson.create_soul_harvest.content.recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
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
public class SoulInfusionRecipe extends StandardProcessingRecipe<SingleRecipeInput> {

	public SoulInfusionRecipe(ProcessingRecipeParams params) {
		super(ModRecipeTypes.SOUL_INFUSION, params);
	}

	@Override
	public boolean matches(SingleRecipeInput inv, Level level) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0).test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 12;
	}
}
//?} else {
/*@ParametersAreNonnullByDefault
public class SoulInfusionRecipe extends ProcessingRecipe<SoulInfusionRecipe.SoulInfusionWrapper> {

	public SoulInfusionRecipe(ProcessingRecipeParams params) {
		super(ModRecipeTypes.SOUL_INFUSION, params);
	}

	@Override
	public boolean matches(SoulInfusionWrapper inv, Level level) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0).test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 12;
	}

	public static class SoulInfusionWrapper extends RecipeWrapper {
		public SoulInfusionWrapper() {
			super(new ItemStackHandler(1));
		}
	}
}
*///?}
