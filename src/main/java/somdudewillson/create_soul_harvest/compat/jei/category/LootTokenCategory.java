package somdudewillson.create_soul_harvest.compat.jei.category;

import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.compat.jei.recipe.LootTokenJeiRecipe;
import somdudewillson.create_soul_harvest.registry.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
public class LootTokenCategory implements IRecipeCategory<LootTokenJeiRecipe> {

	//? >= 1.21 {
	public static final RecipeType<LootTokenJeiRecipe> TYPE =
			RecipeType.create(SoulHarvestMod.MOD_ID, "loot_token", LootTokenJeiRecipe.class);
	//?} else {
	/*public static final RecipeType<LootTokenJeiRecipe> TYPE =
			new RecipeType<>(SoulHarvestMod.id("loot_token"), LootTokenJeiRecipe.class);
	*///?}

	private final IDrawable background;
	private final IDrawable icon;

	public LootTokenCategory() {
		this.background = new EmptyBackground(178, 72);
		this.icon = new ItemIcon(() -> new ItemStack(ModItems.LOOT_TOKEN.get()));
	}

	@Override
	public RecipeType<LootTokenJeiRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("create_soul_harvest.jei.loot_token");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, LootTokenJeiRecipe recipe, IFocusGroup focuses) {
		int inputX = 24;
		int inputY = 27;
		builder.addSlot(RecipeIngredientRole.INPUT, inputX, inputY)
				.addItemStack(recipe.tokenStack());

		int dropCount = Math.max(1, recipe.drops().size());
		int maxCols = 4;
		int slotWidth = 19;
		int slotHeight = 19;
		int rows = (dropCount + maxCols - 1) / maxCols;
		int outputStartX = 100;
		int startY = (72 - (rows * slotHeight - 1)) / 2;

		for (int i = 0; i < recipe.drops().size(); i++) {
			LootTokenJeiRecipe.DropInfo drop = recipe.drops().get(i);
			int col = i % maxCols;
			int row = i / maxCols;
			int x = outputStartX + col * slotWidth;
			int y = startY + row * slotHeight;

			builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
					.addItemStack(drop.stack())
					.addTooltipCallback((recipeSlotView, tooltip) -> {
						if (drop.min() != drop.max()) {
							tooltip.add(Component.translatable("create_soul_harvest.jei.loot_token.count_range", drop.min(), drop.max())
									.withStyle(ChatFormatting.GRAY));
						}
						float percent = drop.getChance() * 100.0f;
						tooltip.add(Component.translatable("create_soul_harvest.jei.loot_token.chance",
								String.format(Locale.ROOT, "%.1f%%", percent))
								.withStyle(ChatFormatting.GOLD));
					});
		}
	}

	@Override
	public void draw(LootTokenJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		int inputX = 24;
		int inputY = 27;
		int arrowX = 50;
		int arrowY = 31;

		// Draw input slot background
		AllGuiTextures.JEI_SLOT.render(guiGraphics, inputX - 1, inputY - 1);
		// Draw arrow
		AllGuiTextures.JEI_ARROW.render(guiGraphics, arrowX, arrowY);

		int dropCount = Math.max(1, recipe.drops().size());
		int maxCols = 4;
		int slotWidth = 19;
		int slotHeight = 19;
		int rows = (dropCount + maxCols - 1) / maxCols;
		int outputStartX = 100;
		int startY = (72 - (rows * slotHeight - 1)) / 2;

		for (int i = 0; i < recipe.drops().size(); i++) {
			int col = i % maxCols;
			int row = i / maxCols;
			int x = outputStartX + col * slotWidth - 1;
			int y = startY + row * slotHeight - 1;
			AllGuiTextures.JEI_SLOT.render(guiGraphics, x, y);
		}
	}
}
