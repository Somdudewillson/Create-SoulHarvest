package somdudewillson.create_soul_harvest.content.potentia.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import somdudewillson.create_soul_harvest.client.ModPartialModels;
import somdudewillson.create_soul_harvest.content.potentia.client.PotentiaColorHelper;
import somdudewillson.create_soul_harvest.content.potentia.client.PotentiaColorHelper.PotentiaColors;

public class PotentiaReservoirRenderer extends SmartBlockEntityRenderer<PotentiaReservoirBlockEntity> {
	private static final float TANK_VERTICAL_HEIGHT = 2f;
	private static final float TARGET_BASE_Y = (TANK_VERTICAL_HEIGHT+0.01f) / 16.0f;
	private static final float MODEL_BASE_Y = 2.0f / 16.0f;
	private static final float MODEL_HEIGHT = 14.0f / 16.0f;
	private static final float MAX_INNER_HEIGHT = ((16f - TANK_VERTICAL_HEIGHT - 0.05f) - (TANK_VERTICAL_HEIGHT+0.01f)) / 16.0f;

	public PotentiaReservoirRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(PotentiaReservoirBlockEntity be, float partialTicks, PoseStack ms,
							  MultiBufferSource buffer, int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		float level = be.getCrystalLevel().getValue(partialTicks);
		if (level <= 0.001f) {
			return;
		}

		BakedModel crystalModel = ModPartialModels.POTENTIA_CRYSTAL.get();
		if (crystalModel == null) {
			return;
		}

		PotentiaColors colors = PotentiaColorHelper.getColors(be.getPotentia());
		RandomSource random = RandomSource.create(42L);
		VertexConsumer vb = buffer.getBuffer(RenderType.translucent());

		float scaleY = level * (MAX_INNER_HEIGHT / MODEL_HEIGHT);

		ms.pushPose();
		ms.translate(0, TARGET_BASE_Y, 0);
		ms.scale(1.0f, scaleY, 1.0f);
		ms.translate(0, -MODEL_BASE_Y, 0);

		PoseStack.Pose pose = ms.last();

		renderQuads(crystalModel.getQuads(null, null, random), pose, vb, colors, light, overlay);
		for (Direction d : Direction.values()) {
			renderQuads(crystalModel.getQuads(null, d, random), pose, vb, colors, light, overlay);
		}

		ms.popPose();
	}

	private void renderQuads(java.util.List<BakedQuad> quads, PoseStack.Pose pose, VertexConsumer vb,
							 PotentiaColors colors, int light, int overlay) {
		if (quads == null || quads.isEmpty()) {
			return;
		}
		for (BakedQuad quad : quads) {
			int rgb = colors.getByTintIndex(quad.getTintIndex());
			float r = ((rgb >> 16) & 0xFF) / 255.0f;
			float g = ((rgb >> 8) & 0xFF) / 255.0f;
			float b = (rgb & 0xFF) / 255.0f;

			//? >= 1.21 {
			vb.putBulkData(pose, quad, r, g, b, 1.0f, light, overlay);
			//?} else {
			/*vb.putBulkData(pose, quad, r, g, b, 1.0f, light, overlay, true);
			*///?}
		}
	}
}
