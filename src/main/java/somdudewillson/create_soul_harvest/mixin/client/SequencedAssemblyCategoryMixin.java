package somdudewillson.create_soul_harvest.mixin.client;

import com.simibubi.create.compat.jei.category.SequencedAssemblyCategory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import somdudewillson.create_soul_harvest.content.recipe.UniformSequencedAssemblyRecipe;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Mixin(value = SequencedAssemblyCategory.class, remap = false)
public abstract class SequencedAssemblyCategoryMixin {

	@Unique
	private SequencedAssemblyRecipe create_soul_harvest$currentRecipe;

	@Inject(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At("HEAD")
	)
	private void create_soul_harvest$captureRecipe(IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
		this.create_soul_harvest$currentRecipe = recipe;
	}

	@Inject(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At("RETURN")
	)
	private void create_soul_harvest$clearRecipe(IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
		this.create_soul_harvest$currentRecipe = null;
	}

	@Redirect(
		method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
		at = @At(value = "INVOKE", target = "Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;addSlot(Lmezz/jei/api/recipe/RecipeIngredientRole;II)Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;")
	)
	private IRecipeSlotBuilder create_soul_harvest$redirectAddSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y) {
		IRecipeSlotBuilder slot = builder.addSlot(role, x, y);
		if (role == RecipeIngredientRole.OUTPUT && this.create_soul_harvest$currentRecipe instanceof UniformSequencedAssemblyRecipe uniformRecipe) {
			return create_soul_harvest$wrapOutputSlot(slot, uniformRecipe);
		}
		return slot;
	}

	@Unique
	private IRecipeSlotBuilder create_soul_harvest$wrapOutputSlot(IRecipeSlotBuilder realSlot, UniformSequencedAssemblyRecipe recipe) {
		InvocationHandler handler = new InvocationHandler() {
			private boolean itemsAdded = false;

			@SuppressWarnings("removal")
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String name = method.getName();
				if (name.startsWith("addItem") || name.startsWith("addIngredient")) {
					if (!itemsAdded) {
						itemsAdded = true;
						realSlot.addItemStacks(recipe.getOutputs());
						realSlot.addTooltipCallback((recipeSlotView, tooltip) -> {
							tooltip.add(recipe.getChanceTooltip().copy().withStyle(ChatFormatting.GOLD));
							if (recipe.getTooltipKey() != null && !recipe.getTooltipKey().isEmpty()) {
								tooltip.add(Component.translatable(recipe.getTooltipKey()).withStyle(ChatFormatting.GRAY));
							}
						});
					}
					return proxy;
				}
				if (name.equals("addTooltipCallback")) {
					return proxy;
				}
				Object result = method.invoke(realSlot, args);
				if (result == realSlot) {
					return proxy;
				}
				return result;
			}
		};

		return (IRecipeSlotBuilder) Proxy.newProxyInstance(
			IRecipeSlotBuilder.class.getClassLoader(),
			new Class<?>[]{IRecipeSlotBuilder.class},
			handler
		);
	}
}
