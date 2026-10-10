package somdudewillson.create_soul_harvest.compat.jei;

import java.util.Collections;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.compat.jei.category.FanSoulInfusionCategory;
import somdudewillson.create_soul_harvest.content.recipe.SoulInfusionRecipe;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
import net.minecraft.world.item.crafting.RecipeHolder;
//?}

@JeiPlugin
@ParametersAreNonnullByDefault
public class SoulHarvestJEI implements IModPlugin {
	public static final ResourceLocation ID = SoulHarvestMod.id("jei_plugin");

	//? >= 1.21 {
	public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<SoulInfusionRecipe>> SOUL_INFUSION_TYPE =
			mezz.jei.api.recipe.RecipeType.createRecipeHolderType(SoulHarvestMod.id("soul_infusion"));
	//?} else {
	/*public static final mezz.jei.api.recipe.RecipeType<SoulInfusionRecipe> SOUL_INFUSION_TYPE =
			new mezz.jei.api.recipe.RecipeType<>(SoulHarvestMod.id("soul_infusion"), SoulInfusionRecipe.class);
	*///?}

	private FanSoulInfusionCategory category;

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		CreateRecipeCategory.Info<SoulInfusionRecipe> info = new CreateRecipeCategory.Info<>(
				SOUL_INFUSION_TYPE,
				Component.translatable("create_soul_harvest.recipe.soul_infusion"),
				registration.getJeiHelpers().getGuiHelper().createBlankDrawable(178, 72),
				new DoubleItemIcon(() -> new ItemStack(AllItems.PROPELLER.get()), () -> new ItemStack(ModBlocks.SOUL_CLUSTER.get())),
				() -> {
					ClientPacketListener connection = Minecraft.getInstance().getConnection();
					if (connection != null) {
						return connection.getRecipeManager().getAllRecipesFor(ModRecipeTypes.SOUL_INFUSION.getType());
					}
					return Collections.emptyList();
				},
				List.of(
						ProcessingViaFanCategory.getFan("soul_infusion"),
						() -> new ItemStack(ModBlocks.SOUL_CLUSTER.get())
				)
		);
		this.category = new FanSoulInfusionCategory(info);
		registration.addRecipeCategories(category);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (category != null) {
			category.registerRecipes(registration);
		}
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		if (category != null) {
			category.registerCatalysts(registration);
		}
	}
}
