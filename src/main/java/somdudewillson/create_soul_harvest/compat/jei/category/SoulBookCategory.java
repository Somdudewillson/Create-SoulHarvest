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
import somdudewillson.create_soul_harvest.compat.jei.recipe.SoulBookJeiRecipe;
import somdudewillson.create_soul_harvest.registry.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SoulBookCategory implements IRecipeCategory<SoulBookJeiRecipe> {

	//? >= 1.21 {
	public static final RecipeType<SoulBookJeiRecipe> TYPE =
			RecipeType.create(SoulHarvestMod.MOD_ID, "soul_book", SoulBookJeiRecipe.class);
	//?} else {
	/*public static final RecipeType<SoulBookJeiRecipe> TYPE =
			new RecipeType<>(SoulHarvestMod.id("soul_book"), SoulBookJeiRecipe.class);
	*///?}

	public static final int WIDTH = 178;
	public static final int HEIGHT = 72;

	private final IDrawable background;
	private final IDrawable icon;

	public SoulBookCategory() {
		this.background = new EmptyBackground(WIDTH, HEIGHT);
		this.icon = new ItemIcon(() -> new ItemStack(ModItems.SOUL_BOOK_STANDARD.get()));
	}

	@Override
	public RecipeType<SoulBookJeiRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("create_soul_harvest.jei.soul_book");
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
	public void setRecipe(IRecipeLayoutBuilder builder, SoulBookJeiRecipe recipe, IFocusGroup focuses) {
		boolean hasEgg = !recipe.spawnEgg().isEmpty();
		int bookX = hasEgg ? 16 : 28;
		int bookY = 26;

		builder.addSlot(RecipeIngredientRole.INPUT, bookX, bookY)
				.addItemStacks(recipe.inputBooks())
				.addRichTooltipCallback((recipeSlotView, tooltip) -> {
					tooltip.add(Component.translatable("create_soul_harvest.jei.soul_book.offhand_tip")
							.withStyle(ChatFormatting.AQUA));
				});

		if (hasEgg) {
			int eggX = 40;
			int eggY = 26;
			builder.addSlot(RecipeIngredientRole.INPUT, eggX, eggY)
					.addItemStack(recipe.spawnEgg())
					.addRichTooltipCallback((recipeSlotView, tooltip) -> {
						tooltip.add(Component.translatable("create_soul_harvest.jei.soul_book.target_mob")
								.withStyle(ChatFormatting.GRAY));
					});
		}

		int outX = 142;
		int outY = 26;
		builder.addSlot(RecipeIngredientRole.OUTPUT, outX, outY)
				.addItemStacks(recipe.outputBooks());
	}

	@Override
	public void draw(SoulBookJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		boolean hasEgg = !recipe.spawnEgg().isEmpty();
		int bookX = hasEgg ? 16 : 28;
		int bookY = 26;
		int arrowX = 72;
		int arrowY = 30;
		int outX = 142;
		int outY = 26;

		// Draw input slot backgrounds
		AllGuiTextures.JEI_SLOT.render(guiGraphics, bookX - 1, bookY - 1);
		if (hasEgg) {
			AllGuiTextures.JEI_SLOT.render(guiGraphics, 39, bookY - 1);
		}

		// Draw arrow
		AllGuiTextures.JEI_ARROW.render(guiGraphics, arrowX, arrowY);

		// Draw output slot background
		AllGuiTextures.JEI_SLOT.render(guiGraphics, outX - 1, outY - 1);

		Font font = Minecraft.getInstance().font;

		// Draw mob display name (top-left)
		Component displayName = recipe.displayName();
		if (displayName != null) {
			int maxWidth = 120;
			int nameWidth = font.width(displayName);
			if (nameWidth > maxWidth) {
				String raw = displayName.getString();
				String trimmed = font.plainSubstrByWidth(raw, maxWidth - 8) + "...";
				guiGraphics.drawString(font, trimmed, 12, 10, 0x303030, false);
			} else {
				guiGraphics.drawString(font, displayName, 12, 10, 0x303030, false);
			}
		}

		// Draw tier badge (top-right)
		int tierColor = switch (recipe.tier()) {
			case 1 -> 0x2e7d32; // Forest Green
			case 2 -> 0xb8860b; // Dark Goldenrod
			default -> 0x7b1fa2; // Deep Purple
		};
		Component tierText = Component.translatable("create_soul_harvest.jei.soul_book.tier", recipe.tier());
		int tierWidth = font.width(tierText);
		guiGraphics.drawString(font, tierText, WIDTH - 12 - tierWidth, 10, tierColor, false);

		// Draw kills info (above arrow)
		Component killsText = Component.translatable(
				recipe.killsNeeded() == 1
						? "create_soul_harvest.jei.soul_book.kill_single"
						: "create_soul_harvest.jei.soul_book.kill_plural",
				recipe.killsNeeded()
		);
		int killsWidth = font.width(killsText);
		int killsX = 90 - killsWidth / 2;
		guiGraphics.drawString(font, killsText, killsX, 18, 0x8b0000, false);

		// Draw progress per kill (below arrow)
		Component progressText = Component.translatable("create_soul_harvest.jei.soul_book.progress_per_kill", recipe.progressPerKill());
		int progressWidth = font.width(progressText);
		int progressX = 90 - progressWidth / 2;
		guiGraphics.drawString(font, progressText, progressX, 42, 0x666666, false);

		// Draw offhand instruction (bottom, centered)
		Component instruction = Component.translatable("create_soul_harvest.jei.soul_book.offhand_instruction");
		int instrWidth = font.width(instruction);
		int instrX = Math.max(4, (WIDTH - instrWidth) / 2);
		guiGraphics.drawString(font, instruction, instrX, 58, 0x777777, false);
	}
}
