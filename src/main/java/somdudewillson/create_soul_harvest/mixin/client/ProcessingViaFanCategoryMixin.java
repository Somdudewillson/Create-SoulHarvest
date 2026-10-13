package somdudewillson.create_soul_harvest.mixin.client;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import somdudewillson.create_soul_harvest.content.recipe.ISelectiveOutputRecipe;

//? >= 1.21 {
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
//?} else {
/*import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
*///?}

@Mixin(value = ProcessingViaFanCategory.MultiOutput.class, remap = false)
public abstract class ProcessingViaFanCategoryMixin {

	//? >= 1.21 {
	@Unique
	private StandardProcessingRecipe<?> create_soul_harvest$currentRecipe;

	@Inject(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/recipe/StandardProcessingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At("HEAD")
	)
	private void create_soul_harvest$captureRecipe(IRecipeLayoutBuilder builder, StandardProcessingRecipe<?> recipe, IFocusGroup focuses, CallbackInfo ci) {
		this.create_soul_harvest$currentRecipe = recipe;
	}

	@Inject(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/recipe/StandardProcessingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At("RETURN")
	)
	private void create_soul_harvest$clearRecipe(IRecipeLayoutBuilder builder, StandardProcessingRecipe<?> recipe, IFocusGroup focuses, CallbackInfo ci) {
		this.create_soul_harvest$currentRecipe = null;
	}

	@Redirect(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/recipe/StandardProcessingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;addSlot(Lmezz/jei/api/recipe/RecipeIngredientRole;II)Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;")
	)
	private IRecipeSlotBuilder create_soul_harvest$redirectAddSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y) {
		IRecipeSlotBuilder slot = builder.addSlot(role, x, y);
		if (role == RecipeIngredientRole.OUTPUT && this.create_soul_harvest$currentRecipe instanceof ISelectiveOutputRecipe selective) {
			if (selective.isSingleOutput()) {
				slot.addTooltipCallback((recipeSlotView, tooltip) -> {
					tooltip.add(Component.translatable("create_soul_harvest.jei.output_mode.single").withStyle(ChatFormatting.YELLOW));
				});
			}
		}
		return slot;
	}

	@Inject(
		method = "renderWidgets(Lnet/minecraft/client/gui/GuiGraphics;Lcom/simibubi/create/content/processing/recipe/StandardProcessingRecipe;DD)V",
		at = @At("TAIL")
	)
	private void create_soul_harvest$renderModeBadge(GuiGraphics graphics, StandardProcessingRecipe<?> recipe, double mouseX, double mouseY, CallbackInfo ci) {
		create_soul_harvest$drawBadge(graphics, recipe, mouseX, mouseY);
	}
	//?} else {
	/*@Unique
	private ProcessingRecipe<?> create_soul_harvest$currentRecipe;

	@Inject(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At("HEAD")
	)
	private void create_soul_harvest$captureRecipe(IRecipeLayoutBuilder builder, ProcessingRecipe<?> recipe, IFocusGroup focuses, CallbackInfo ci) {
		this.create_soul_harvest$currentRecipe = recipe;
	}

	@Inject(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At("RETURN")
	)
	private void create_soul_harvest$clearRecipe(IRecipeLayoutBuilder builder, ProcessingRecipe<?> recipe, IFocusGroup focuses, CallbackInfo ci) {
		this.create_soul_harvest$currentRecipe = null;
	}

	@Redirect(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;addSlot(Lmezz/jei/api/recipe/RecipeIngredientRole;II)Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;")
	)
	private IRecipeSlotBuilder create_soul_harvest$redirectAddSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y) {
		IRecipeSlotBuilder slot = builder.addSlot(role, x, y);
		if (role == RecipeIngredientRole.OUTPUT && this.create_soul_harvest$currentRecipe instanceof ISelectiveOutputRecipe selective) {
			if (selective.isSingleOutput()) {
				slot.addTooltipCallback((recipeSlotView, tooltip) -> {
					tooltip.add(Component.translatable("create_soul_harvest.jei.output_mode.single").withStyle(ChatFormatting.YELLOW));
				});
			}
		}
		return slot;
	}

	@Inject(
		method = "renderWidgets(Lnet/minecraft/client/gui/GuiGraphics;Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;DD)V",
		at = @At("TAIL")
	)
	private void create_soul_harvest$renderModeBadge(GuiGraphics graphics, ProcessingRecipe<?> recipe, double mouseX, double mouseY, CallbackInfo ci) {
		create_soul_harvest$drawBadge(graphics, recipe, mouseX, mouseY);
	}
	*///?}

	@Unique
	private void create_soul_harvest$drawBadge(GuiGraphics graphics, Object recipe, double mouseX, double mouseY) {
		if (!(recipe instanceof ISelectiveOutputRecipe selective))
			return;

		Font font = Minecraft.getInstance().font;
		int badgeX = 4;
		int badgeY = 4;
		int badgeWidth = 16;
		int badgeHeight = 11;

		boolean single = selective.isSingleOutput();
		int bgColor = single ? 0x904A235A : 0x70333333;
		int borderColor = single ? 0xFFBF55EC : 0xFF888888;
		String text = single ? "1" : "All";
		int textColor = single ? 0xFFFFD700 : 0xFFCCCCCC;

		graphics.fill(badgeX, badgeY, badgeX + badgeWidth, badgeY + badgeHeight, bgColor);
		graphics.fill(badgeX, badgeY, badgeX + badgeWidth, badgeY + 1, borderColor);
		graphics.fill(badgeX, badgeY + badgeHeight - 1, badgeX + badgeWidth, badgeY + badgeHeight, borderColor);
		graphics.fill(badgeX, badgeY, badgeX + 1, badgeY + badgeHeight, borderColor);
		graphics.fill(badgeX + badgeWidth - 1, badgeY, badgeX + badgeWidth, badgeY + badgeHeight, borderColor);

		int textX = badgeX + (badgeWidth - font.width(text)) / 2;
		int textY = badgeY + 2;
		graphics.drawString(font, text, textX, textY, textColor, false);

		if (mouseX >= badgeX && mouseX <= badgeX + badgeWidth && mouseY >= badgeY && mouseY <= badgeY + badgeHeight) {
			List<Component> tooltip = new ArrayList<>();
			tooltip.add(Component.translatable("create_soul_harvest.jei.output_mode.title").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
			if (single) {
				tooltip.add(Component.translatable("create_soul_harvest.jei.output_mode.single").withStyle(ChatFormatting.YELLOW));
				create_soul_harvest$appendWrappedTooltipComponent(
							tooltip, 
							Component.translatable("create_soul_harvest.jei.output_mode.single.desc").withStyle(ChatFormatting.GRAY),
							200
						);
			} else {
				tooltip.add(Component.translatable("create_soul_harvest.jei.output_mode.independent").withStyle(ChatFormatting.AQUA));
				create_soul_harvest$appendWrappedTooltipComponent(
							tooltip, 
							Component.translatable("create_soul_harvest.jei.output_mode.independent.desc").withStyle(ChatFormatting.GRAY),
							200
						);
			}
			graphics.renderComponentTooltip(font, tooltip, (int) mouseX, (int) mouseY);
		}
	}
	
	private void create_soul_harvest$appendWrappedTooltipComponent(List<Component> tooltip, Component component, int width) {
		Minecraft mc = Minecraft.getInstance();
		
	    if (mc.font != null) {
	        List<MutableComponent> splitLines = mc.font.getSplitter().splitLines(component, width, net.minecraft.network.chat.Style.EMPTY)
	            .stream()
	            .map(formattedText -> Component.literal(formattedText.getString()).withStyle(component.getStyle()))
	            .toList();
	        
	        tooltip.addAll(splitLines);
	    } else {
	        tooltip.add(component);
	    }
	}
}
