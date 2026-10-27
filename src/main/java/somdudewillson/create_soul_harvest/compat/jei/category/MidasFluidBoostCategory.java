package somdudewillson.create_soul_harvest.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import somdudewillson.create_soul_harvest.content.block.MidasCoreBlockEntity;
import somdudewillson.create_soul_harvest.content.recipe.MidasFluidBoostRecipe;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

//? < 1.21 {
/*import mezz.jei.api.forge.ForgeTypes;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
*///?}

@ParametersAreNonnullByDefault
public class MidasFluidBoostCategory extends CreateRecipeCategory<MidasFluidBoostRecipe> {

	public MidasFluidBoostCategory(Info<MidasFluidBoostRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MidasFluidBoostRecipe recipe, IFocusGroup focuses) {
		int fluidX = 8;
		int fluidY = 27;

		//? >= 1.21 {
		addFluidSlot(builder, fluidX, fluidY, recipe.getFluidIngredient());
		//?} else {
		/*List<FluidStack> fluids = new ArrayList<>();
		recipe.getFluidOrTag().ifLeft(tag -> {
			for (Fluid f : ForgeRegistries.FLUIDS.tags().getTag(tag)) {
				fluids.add(new FluidStack(f, recipe.getAmount()));
			}
		}).ifRight(f -> fluids.add(new FluidStack(f, recipe.getAmount())));
		addFluidSlot(builder, fluidX, fluidY, RecipeIngredientRole.INPUT)
				.addIngredients(ForgeTypes.FLUID_STACK, fluids)
				.setFluidRenderer(recipe.getAmount(), false, 16, 16);
		*///?}

		int catalystX = 76;
		int catalystY = 27;
		builder.addSlot(RecipeIngredientRole.CATALYST, catalystX, catalystY)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(new ItemStack(ModBlocks.MIDAS_CORE.get()));
	}

	@Override
	public void draw(MidasFluidBoostRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		int arrowX = 30;
		int arrowY = 31;
		AllGuiTextures.JEI_ARROW.render(guiGraphics, arrowX, arrowY);

		float mult = recipe.getMultiplier();
		String multStr = String.format(Locale.ROOT, "%.1fx Speed", mult);
		guiGraphics.drawString(Minecraft.getInstance().font,
				Component.literal(multStr).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
				106, 20, 0xFFFFFF, false);

		int totalSecs = recipe.getDuration() / 20;
		int mins = totalSecs / 60;
		int secs = totalSecs % 60;
		String durStr = mins > 0 ? (mins + "m" + (secs > 0 ? " " + secs + "s" : "")) : (secs + "s");
		guiGraphics.drawString(Minecraft.getInstance().font,
				Component.literal("Duration: " + durStr).withStyle(ChatFormatting.GRAY),
				106, 33, 0xFFFFFF, false);

		float waveInterval = (MidasCoreBlockEntity.DEFAULT_CONVERSION_INTERVAL / mult) / 20.0f;
		String rateStr = String.format(Locale.ROOT, "Every %.1fs", waveInterval);
		guiGraphics.drawString(Minecraft.getInstance().font,
				Component.literal(rateStr).withStyle(ChatFormatting.DARK_GREEN),
				106, 45, 0xFFFFFF, false);
	}
}
