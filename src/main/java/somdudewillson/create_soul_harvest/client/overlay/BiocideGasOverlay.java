package somdudewillson.create_soul_harvest.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.BiocideGasBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class BiocideGasOverlay {

	public static final ResourceLocation OVERLAY_TEXTURE = SoulHarvestMod.id("textures/misc/biocide_gas_overlay.png");

	public static void render(GuiGraphics guiGraphics) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.level == null || mc.options.hideGui) {
			return;
		}

		// Check if camera or player eye position is inside biocide gas
		Camera camera = mc.gameRenderer.getMainCamera();
		BlockPos camPos = camera.getBlockPosition();
		BlockPos eyePos = BlockPos.containing(player.getEyePosition());

		BlockState state = mc.level.getBlockState(camPos);
		if (!state.is(ModBlocks.BIOCIDE_GAS.get())) {
			state = mc.level.getBlockState(eyePos);
		}

		if (!state.is(ModBlocks.BIOCIDE_GAS.get())) {
			return;
		}

		int density = state.getValue(BiocideGasBlock.DENSITY);
		// Scale opacity with density
		float progress = (float) (density - BiocideGasBlock.MIN_DENSITY) / (float) (BiocideGasBlock.MAX_DENSITY - BiocideGasBlock.MIN_DENSITY);
		float alpha = 0.35F + 0.55F * Math.max(0.0F, Math.min(1.0F, progress));

		int width = guiGraphics.guiWidth();
		int height = guiGraphics.guiHeight();

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
		guiGraphics.blit(OVERLAY_TEXTURE, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
