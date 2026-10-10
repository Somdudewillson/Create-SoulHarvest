package somdudewillson.create_soul_harvest.compat.jei.category;

import javax.annotation.ParametersAreNonnullByDefault;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import somdudewillson.create_soul_harvest.content.block.SoulBudBlock;
import somdudewillson.create_soul_harvest.content.recipe.SoulInfusionRecipe;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

@ParametersAreNonnullByDefault
public class FanSoulInfusionCategory extends ProcessingViaFanCategory.MultiOutput<SoulInfusionRecipe> {

	public FanSoulInfusionCategory(Info<SoulInfusionRecipe> info) {
		super(info);
	}

	@Override
	protected AllGuiTextures getBlockShadow() {
		return AllGuiTextures.JEI_LIGHT;
	}

	@Override
	protected void renderAttachedBlock(GuiGraphics graphics) {
		GuiGameElement.of(ModBlocks.SOUL_CLUSTER.get().defaultBlockState().setValue(SoulBudBlock.ACTIVE, true))
				.scale(SCALE)
				.atLocal(0, 0, 2)
				.lighting(AnimatedKinetics.DEFAULT_LIGHTING)
				.render(graphics);
	}
}
