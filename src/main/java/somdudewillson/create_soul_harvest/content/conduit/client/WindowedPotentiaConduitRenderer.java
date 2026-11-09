package somdudewillson.create_soul_harvest.content.conduit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPalette;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureManager;
import somdudewillson.create_soul_harvest.content.conduit.WindowedPotentiaConduitBlock;
import somdudewillson.create_soul_harvest.content.conduit.WindowedPotentiaConduitBlockEntity;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;

public class WindowedPotentiaConduitRenderer implements BlockEntityRenderer<WindowedPotentiaConduitBlockEntity> {

	private static final int SEGMENTS = 10;
	private static final float BOLT_RADIUS = 0.13f;
	private static final float BOLT_WIDTH = 0.025f;

	public WindowedPotentiaConduitRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(WindowedPotentiaConduitBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
					   int light, int overlay) {
		int activeTicks = be.getActiveTicksRemaining();
		if (activeTicks <= 0 && be.getLevel() != null && be.getLevel().isClientSide() && be.getFlowIntensity() <= 0.0f) {
			return;
		}

		BlockState state = be.getBlockState();
		if (!state.hasProperty(WindowedPotentiaConduitBlock.AXIS)) {
			return;
		}

		Axis axis = state.getValue(WindowedPotentiaConduitBlock.AXIS);
		float alpha = Math.min(1.0f, (activeTicks + partialTicks) / 6.0f);
		if (alpha <= 0.0f) {
			return;
		}

		// Determine bolt colors based on entity palette
		ResourceLocation entityType = be.getCurrentEntityType();
		MobPalette palette = null;
		if (entityType != null) {
			palette = MobPatternTextureManager.INSTANCE.getPalette(entityType);
		}

		int[] boltColors = getBoltColors(palette);

		ms.pushPose();
		ms.translate(0.5f, 0.5f, 0.5f);

		// Orient coordinate system so Z is the conduit axis
		if (axis == Axis.Y) {
			ms.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0f));
		} else if (axis == Axis.X) {
			ms.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0f));
		}

		Direction flowDir = be.getCurrentFlowDirection();
		boolean invert = flowDir != null && (flowDir == Direction.SOUTH || flowDir == Direction.UP || flowDir == Direction.EAST);
		float speedMult = invert ? -1.0f : 1.0f;

		float gameTime = (be.getLevel() != null ? be.getLevel().getGameTime() : 0) + partialTicks;
		float time = gameTime * 0.35f * speedMult;

		Matrix4f matrix = ms.last().pose();
		VertexConsumer builder = buffer.getBuffer(RenderType.lightning());

		long blockSeed = be.getBlockPos().asLong();
		Level level = be.getLevel();
		Direction negDir = getLocalNegWorldDir(axis);
		Direction posDir = getLocalPosWorldDir(axis);
		boolean isNegTransport = isTransportNeighbor(level, be.getBlockPos(), negDir);
		boolean isPosTransport = isTransportNeighbor(level, be.getBlockPos(), posDir);

		// Render 3 intertwined lightning bolts
		for (int b = 0; b < 3; b++) {
			int color = boltColors[b];
			int red = (color >> 16) & 0xFF;
			int green = (color >> 8) & 0xFF;
			int blue = color & 0xFF;
			int alphaInt = (int) (alpha * 240.0f);

			float baseAngle = (float) (b * (2.0 * Math.PI / 3.0));
			renderSingleBolt(builder, matrix, b, baseAngle, time, blockSeed, red, green, blue, alphaInt, isNegTransport, isPosTransport);
		}

		ms.popPose();
	}

	private void renderSingleBolt(VertexConsumer builder, Matrix4f matrix, int boltIndex, float baseAngle,
								  float time, long blockSeed, int r, int g, int b, int a,
								  boolean isNegTransport, boolean isPosTransport) {
		float[] xPoints = new float[SEGMENTS + 1];
		float[] yPoints = new float[SEGMENTS + 1];
		float[] zPoints = new float[SEGMENTS + 1];

		long quantizedTime = (long) (time * 2.5f);

		for (int i = 0; i <= SEGMENTS; i++) {
			float progress = (float) i / (float) SEGMENTS;
			float z = -0.48f + progress * 0.96f;

			// Taper endpoints: spread out if neighbor is another transport block, pinch to center if supplier/consumer or closed
			float startTaper = isNegTransport ? 1.0f : (float) Math.sin(Math.min(1.0f, progress / 0.25f) * (Math.PI / 2));
			float endTaper = isPosTransport ? 1.0f : (float) Math.sin(Math.min(1.0f, (1.0f - progress) / 0.25f) * (Math.PI / 2));
			float taper = Math.min(startTaper, endTaper);

			float angle = baseAngle + time * 2.5f + (float) Math.sin(progress * 7.0f + time * 3.0f) * 1.2f;

			long jitterSeed = blockSeed ^ (boltIndex * 31337L) ^ (i * 1013L) ^ quantizedTime;
			float jitterX = (hash(jitterSeed, 1) - 0.5f) * 0.08f * taper;
			float jitterY = (hash(jitterSeed, 2) - 0.5f) * 0.08f * taper;

			xPoints[i] = (float) Math.cos(angle) * (BOLT_RADIUS * taper) + jitterX;
			yPoints[i] = (float) Math.sin(angle) * (BOLT_RADIUS * taper) + jitterY;
			zPoints[i] = z;
		}

		// Render segments as cross-quad ribbons (double-sided)
		for (int i = 0; i < SEGMENTS; i++) {
			float x1 = xPoints[i];
			float y1 = yPoints[i];
			float z1 = zPoints[i];
			float x2 = xPoints[i + 1];
			float y2 = yPoints[i + 1];
			float z2 = zPoints[i + 1];

			// Vertical plane
			putQuad(builder, matrix,
					x1, y1 - BOLT_WIDTH, z1,
					x1, y1 + BOLT_WIDTH, z1,
					x2, y2 + BOLT_WIDTH, z2,
					x2, y2 - BOLT_WIDTH, z2,
					r, g, b, a);

			// Horizontal plane
			putQuad(builder, matrix,
					x1 - BOLT_WIDTH, y1, z1,
					x1 + BOLT_WIDTH, y1, z1,
					x2 + BOLT_WIDTH, y2, z2,
					x2 - BOLT_WIDTH, y2, z2,
					r, g, b, a);
		}
	}

	private static void putQuad(VertexConsumer builder, Matrix4f matrix,
								float x1, float y1, float z1,
								float x2, float y2, float z2,
								float x3, float y3, float z3,
								float x4, float y4, float z4,
								int r, int g, int b, int a) {
		// Front face
		addVertex(builder, matrix, x1, y1, z1, r, g, b, a);
		addVertex(builder, matrix, x2, y2, z2, r, g, b, a);
		addVertex(builder, matrix, x3, y3, z3, r, g, b, a);
		addVertex(builder, matrix, x4, y4, z4, r, g, b, a);

		// Back face (reverse winding for double-sided visibility from all angles)
		addVertex(builder, matrix, x4, y4, z4, r, g, b, a);
		addVertex(builder, matrix, x3, y3, z3, r, g, b, a);
		addVertex(builder, matrix, x2, y2, z2, r, g, b, a);
		addVertex(builder, matrix, x1, y1, z1, r, g, b, a);
	}

	private static Direction getLocalNegWorldDir(Axis axis) {
		return switch (axis) {
			case X -> Direction.WEST;
			case Y -> Direction.UP;
			case Z -> Direction.NORTH;
		};
	}

	private static Direction getLocalPosWorldDir(Axis axis) {
		return switch (axis) {
			case X -> Direction.EAST;
			case Y -> Direction.DOWN;
			case Z -> Direction.SOUTH;
		};
	}

	private static boolean isTransportNeighbor(Level level, BlockPos pos, Direction dir) {
		if (level == null) return false;
		BlockPos neighborPos = pos.relative(dir);
		BlockState state = level.getBlockState(neighborPos);
		if (state.getBlock() instanceof IPotentiaTransporter transporter) {
			return transporter.canConnect(state, dir.getOpposite());
		}
		return false;
	}

	private static void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, int r, int g, int b, int a) {
		//? >= 1.21 {
		builder.addVertex(matrix, x, y, z).setColor(r, g, b, a);
		//?} else {
		/*builder.vertex(matrix, x, y, z).color(r, g, b, a).endVertex();
		*///?}
	}

	private static int[] getBoltColors(MobPalette palette) {
		int primary = 0x00E5FF;    // Vibrant Soul Cyan
		int secondary = 0x9D4EDD;  // Amethyst Purple
		int accent = 0x72EFDD;     // Soul Fire Teal

		if (palette != null) {
			if (palette.getMostFrequentCommonColor() != null) {
				primary = palette.getMostFrequentCommonColor().toRgbInt();
			}
			if (!palette.rareColors().isEmpty()) {
				secondary = palette.rareColors().get(0).toRgbInt();
			} else if (palette.getLeastFrequentCommonColor() != null) {
				secondary = palette.getLeastFrequentCommonColor().toRgbInt();
			}
			if (palette.rareColors().size() > 1) {
				accent = palette.rareColors().get(1).toRgbInt();
			} else {
				accent = primary;
			}
		}

		return new int[] { primary, secondary, accent };
	}

	private static float hash(long seed, int salt) {
		long n = seed * 6364136223846793005L + (salt * 1442695040888963407L);
		n = (n ^ (n >> 21));
		n = (n ^ (n << 35));
		n = (n ^ (n >> 4));
		return (float) (Math.abs(n % 10000L)) / 10000.0f;
	}
}
