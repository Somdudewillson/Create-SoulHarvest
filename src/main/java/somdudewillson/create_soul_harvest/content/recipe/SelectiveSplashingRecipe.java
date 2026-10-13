package somdudewillson.create_soul_harvest.content.recipe;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
//?} else {
/*import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
*///?}

@ParametersAreNonnullByDefault
public class SelectiveSplashingRecipe extends SplashingRecipe implements ISelectiveOutputRecipe {

	@Getter
	@Setter
	private OutputSelectionMode outputSelectionMode = OutputSelectionMode.INDEPENDENT;

	public SelectiveSplashingRecipe(ProcessingRecipeParams params) {
		super(params);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.SPLASHING.getSerializer();
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
}
