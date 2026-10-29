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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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

	public static final int WIDTH = 178;
	public static final int HEIGHT = 86;
	public static final int MAX_COLS = 4;
	public static final int SLOT_SIZE = 18;

	private final IDrawable background;
	private final IDrawable icon;

	public LootTokenCategory() {
		this.background = new EmptyBackground(WIDTH, HEIGHT);
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
		int inputX = 20;
		int inputY = 34;
		builder.addSlot(RecipeIngredientRole.INPUT, inputX, inputY)
				.addItemStack(recipe.tokenStack());

		int dropCount = Math.max(1, recipe.drops().size());
		int rows = Math.max(1, (dropCount + MAX_COLS - 1) / MAX_COLS);
		int cols = rows > 1 ? MAX_COLS : Math.min(MAX_COLS, dropCount);
		int outputStartX = 96 + (MAX_COLS - cols) * SLOT_SIZE / 2;
		int startY = (HEIGHT - (rows * SLOT_SIZE)) / 2;

		for (int i = 0; i < recipe.drops().size(); i++) {
			LootTokenJeiRecipe.DropInfo drop = recipe.drops().get(i);
			int col = i % MAX_COLS;
			int row = i / MAX_COLS;
			int x = outputStartX + col * SLOT_SIZE;
			int y = startY + row * SLOT_SIZE;

			builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
					.addItemStack(drop.stack())
					.addTooltipCallback((recipeSlotView, tooltip) -> {
						if (drop.min() != drop.max()) {
							tooltip.add(Component.translatable("create_soul_harvest.jei.loot_token.count_range", drop.min(), drop.max())
									.withStyle(ChatFormatting.GRAY));
						}
						float percent = drop.getChance() * 100.0f;
						String chanceStr = percent < 0.01f && percent > 0
								? "< 0.01%"
								: percent < 1.0f
										? String.format(Locale.ROOT, "%.2f%%", percent)
										: String.format(Locale.ROOT, "%.1f%%", percent);
						tooltip.add(Component.translatable("create_soul_harvest.jei.loot_token.chance", chanceStr)
								.withStyle(ChatFormatting.GOLD));
					});
		}
	}

	@Override
	public void draw(LootTokenJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		int inputX = 20;
		int inputY = 34;
		int arrowX = 46;
		int arrowY = 38;

		// Draw input slot background
		AllGuiTextures.JEI_SLOT.render(guiGraphics, inputX - 1, inputY - 1);
		// Draw arrow
		AllGuiTextures.JEI_ARROW.render(guiGraphics, arrowX, arrowY);

		int dropCount = Math.max(1, recipe.drops().size());
		int rows = Math.max(1, (dropCount + MAX_COLS - 1) / MAX_COLS);
		int cols = rows > 1 ? MAX_COLS : Math.min(MAX_COLS, dropCount);
		int outputStartX = 96 + (MAX_COLS - cols) * SLOT_SIZE / 2;
		int startY = (HEIGHT - (rows * SLOT_SIZE)) / 2;

		for (int i = 0; i < recipe.drops().size(); i++) {
			LootTokenJeiRecipe.DropInfo drop = recipe.drops().get(i);
			int col = i % MAX_COLS;
			int row = i / MAX_COLS;
			int x = outputStartX + col * SLOT_SIZE - 1;
			int y = startY + row * SLOT_SIZE - 1;
			(drop.getChance() < 1.0f ? AllGuiTextures.JEI_CHANCE_SLOT : AllGuiTextures.JEI_SLOT).render(guiGraphics, x, y);
		}

		Font font = Minecraft.getInstance().font;

		// Draw mob display name above input slot/arrow
		Component displayName = recipe.displayName();
		if (displayName != null) {
			int maxWidth = 86;
			int nameWidth = font.width(displayName);
			if (nameWidth > maxWidth) {
				String raw = displayName.getString();
				String trimmed = font.plainSubstrByWidth(raw, maxWidth - 8) + "...";
				guiGraphics.drawString(font, trimmed, 6, 14, 0x404040, false);
			} else {
				int nameX = Math.max(4, 46 - nameWidth / 2);
				guiGraphics.drawString(font, displayName, nameX, 14, 0x404040, false);
			}
		}

		// Draw page info or total drop count below input slot/arrow
		if (recipe.totalPages() > 1) {
			Component pageText = Component.translatable(
					"create_soul_harvest.jei.loot_token.page",
					recipe.pageIndex() + 1,
					recipe.totalPages()
			);
			int textWidth = font.width(pageText);
			int textX = Math.max(4, 46 - textWidth / 2);
			guiGraphics.drawString(font, pageText, textX, 64, 0x555555, false);
		} else if (recipe.totalDropCount() > 0) {
			Component countText = Component.translatable(
					"create_soul_harvest.jei.loot_token.drops_count",
					recipe.totalDropCount()
			);
			int textWidth = font.width(countText);
			int textX = Math.max(4, 46 - textWidth / 2);
			guiGraphics.drawString(font, countText, textX, 64, 0x555555, false);
		}
	}
}
