package somdudewillson.create_soul_harvest.client.texture.palette;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds 3D extruded BakedQuads from the 16x16 silhouette of Sprite 0 (the base attuned polyclay).
 * The quads use normalized UV coordinates [0.0..1.0] matching the 16x16 composited DynamicTexture.
 */
public class AttunedPolyclayQuadBuilder {

	private static final float Z_FRONT = 8.5f / 16.0f;
	private static final float Z_BACK = 7.5f / 16.0f;
	private static final int DEFAULT_COLOR = 0xFFFFFFFF; // White, no vertex tint

	/**
	 * Generates front, back, and extruded edge quads for the 16x16 shape of Sprite 0.
	 */
	public static List<BakedQuad> buildQuads(BufferedImage sprite0, @Nullable TextureAtlasSprite particleSprite) {
		List<BakedQuad> quads = new ArrayList<>();

		// 1. South face (front)
		quads.add(createQuad(
				0.0f, 0.0f, Z_FRONT, 0.0f, 1.0f,
				1.0f, 0.0f, Z_FRONT, 1.0f, 1.0f,
				1.0f, 1.0f, Z_FRONT, 1.0f, 0.0f,
				0.0f, 1.0f, Z_FRONT, 0.0f, 0.0f,
				Direction.SOUTH, particleSprite
		));

		// 2. North face (back)
		quads.add(createQuad(
				1.0f, 0.0f, Z_BACK, 1.0f, 1.0f,
				0.0f, 0.0f, Z_BACK, 0.0f, 1.0f,
				0.0f, 1.0f, Z_BACK, 0.0f, 0.0f,
				1.0f, 1.0f, Z_BACK, 1.0f, 0.0f,
				Direction.NORTH, particleSprite
		));

		// Build opacity grid (16x16)
		boolean[][] solid = new boolean[16][16];
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int a = (sprite0.getRGB(x, y) >> 24) & 0xFF;
				solid[x][y] = a > 0;
			}
		}

		// 3. UP edges (facing positive Y, normal = (0, 1, 0)) - merge contiguous horizontal spans
		for (int y = 0; y < 16; y++) {
			int spanStart = -1;
			for (int x = 0; x < 16; x++) {
				boolean hasUpEdge = solid[x][y] && (y == 0 || !solid[x][y - 1]);
				if (hasUpEdge) {
					if (spanStart == -1) {
						spanStart = x;
					}
				} else if (spanStart != -1) {
					addUpEdgeSpan(quads, spanStart, x, y, particleSprite);
					spanStart = -1;
				}
			}
			if (spanStart != -1) {
				addUpEdgeSpan(quads, spanStart, 16, y, particleSprite);
			}
		}

		// 4. DOWN edges (facing negative Y, normal = (0, -1, 0)) - merge contiguous horizontal spans
		for (int y = 0; y < 16; y++) {
			int spanStart = -1;
			for (int x = 0; x < 16; x++) {
				boolean hasDownEdge = solid[x][y] && (y == 15 || !solid[x][y + 1]);
				if (hasDownEdge) {
					if (spanStart == -1) {
						spanStart = x;
					}
				} else if (spanStart != -1) {
					addDownEdgeSpan(quads, spanStart, x, y, particleSprite);
					spanStart = -1;
				}
			}
			if (spanStart != -1) {
				addDownEdgeSpan(quads, spanStart, 16, y, particleSprite);
			}
		}

		// 5. WEST edges (facing negative X, normal = (-1, 0, 0)) - merge contiguous vertical spans
		for (int x = 0; x < 16; x++) {
			int spanStart = -1;
			for (int y = 0; y < 16; y++) {
				boolean hasWestEdge = solid[x][y] && (x == 0 || !solid[x - 1][y]);
				if (hasWestEdge) {
					if (spanStart == -1) {
						spanStart = y;
					}
				} else if (spanStart != -1) {
					addWestEdgeSpan(quads, x, spanStart, y, particleSprite);
					spanStart = -1;
				}
			}
			if (spanStart != -1) {
				addWestEdgeSpan(quads, x, spanStart, 16, particleSprite);
			}
		}

		// 6. EAST edges (facing positive X, normal = (1, 0, 0)) - merge contiguous vertical spans
		for (int x = 0; x < 16; x++) {
			int spanStart = -1;
			for (int y = 0; y < 16; y++) {
				boolean hasEastEdge = solid[x][y] && (x == 15 || !solid[x + 1][y]);
				if (hasEastEdge) {
					if (spanStart == -1) {
						spanStart = y;
					}
				} else if (spanStart != -1) {
					addEastEdgeSpan(quads, x, spanStart, y, particleSprite);
					spanStart = -1;
				}
			}
			if (spanStart != -1) {
				addEastEdgeSpan(quads, x, spanStart, 16, particleSprite);
			}
		}

		return quads;
	}

	private static void addUpEdgeSpan(List<BakedQuad> quads, int xStart, int xEnd, int y, @Nullable TextureAtlasSprite sprite) {
		float x0 = xStart / 16.0f;
		float x1 = xEnd / 16.0f;
		float yVal = (16 - y) / 16.0f;
		float u0 = xStart / 16.0f;
		float u1 = xEnd / 16.0f;
		float v = (y + 0.5f) / 16.0f;

		quads.add(createQuad(
				x0, yVal, Z_BACK, u0, v,
				x0, yVal, Z_FRONT, u0, v,
				x1, yVal, Z_FRONT, u1, v,
				x1, yVal, Z_BACK, u1, v,
				Direction.UP, sprite
		));
	}

	private static void addDownEdgeSpan(List<BakedQuad> quads, int xStart, int xEnd, int y, @Nullable TextureAtlasSprite sprite) {
		float x0 = xStart / 16.0f;
		float x1 = xEnd / 16.0f;
		float yVal = (15 - y) / 16.0f;
		float u0 = xStart / 16.0f;
		float u1 = xEnd / 16.0f;
		float v = (y + 0.5f) / 16.0f;

		quads.add(createQuad(
				x0, yVal, Z_FRONT, u0, v,
				x0, yVal, Z_BACK, u0, v,
				x1, yVal, Z_BACK, u1, v,
				x1, yVal, Z_FRONT, u1, v,
				Direction.DOWN, sprite
		));
	}

	private static void addWestEdgeSpan(List<BakedQuad> quads, int x, int yStart, int yEnd, @Nullable TextureAtlasSprite sprite) {
		float xVal = x / 16.0f;
		float y0 = (16 - yEnd) / 16.0f;
		float y1 = (16 - yStart) / 16.0f;
		float u = (x + 0.5f) / 16.0f;
		float v0 = yEnd / 16.0f;
		float v1 = yStart / 16.0f;

		quads.add(createQuad(
				xVal, y0, Z_BACK, u, v0,
				xVal, y0, Z_FRONT, u, v0,
				xVal, y1, Z_FRONT, u, v1,
				xVal, y1, Z_BACK, u, v1,
				Direction.WEST, sprite
		));
	}

	private static void addEastEdgeSpan(List<BakedQuad> quads, int x, int yStart, int yEnd, @Nullable TextureAtlasSprite sprite) {
		float xVal = (x + 1) / 16.0f;
		float y0 = (16 - yEnd) / 16.0f;
		float y1 = (16 - yStart) / 16.0f;
		float u = (x + 0.5f) / 16.0f;
		float v0 = yEnd / 16.0f;
		float v1 = yStart / 16.0f;

		quads.add(createQuad(
				xVal, y0, Z_FRONT, u, v0,
				xVal, y0, Z_BACK, u, v0,
				x1_val(xVal), y1, Z_BACK, u, v1,
				x1_val(xVal), y1, Z_FRONT, u, v1,
				Direction.EAST, sprite
		));
	}

	private static float x1_val(float xVal) {
		return xVal;
	}

	private static BakedQuad createQuad(
			float x0, float y0, float z0, float u0, float v0,
			float x1, float y1, float z1, float u1, float v1,
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3,
			Direction direction,
			@Nullable TextureAtlasSprite sprite
	) {
		int[] vertices = new int[32];
		int packedNormal = packNormal(direction);

		putVertex(vertices, 0, x0, y0, z0, u0, v0, packedNormal);
		putVertex(vertices, 1, x1, y1, z1, u1, v1, packedNormal);
		putVertex(vertices, 2, x2, y2, z2, u2, v2, packedNormal);
		putVertex(vertices, 3, x3, y3, z3, u3, v3, packedNormal);

		return new BakedQuad(vertices, -1, direction, sprite, true);
	}

	private static void putVertex(int[] vertices, int index, float x, float y, float z, float u, float v, int normal) {
		int offset = index * 8;
		vertices[offset] = Float.floatToRawIntBits(x);
		vertices[offset + 1] = Float.floatToRawIntBits(y);
		vertices[offset + 2] = Float.floatToRawIntBits(z);
		vertices[offset + 3] = DEFAULT_COLOR;
		vertices[offset + 4] = Float.floatToRawIntBits(u);
		vertices[offset + 5] = Float.floatToRawIntBits(v);
		vertices[offset + 6] = 0; // Lightmap
		vertices[offset + 7] = normal;
	}

	private static int packNormal(Direction direction) {
		int nx = direction.getStepX();
		int ny = direction.getStepY();
		int nz = direction.getStepZ();
		return ((byte) (nx * 127) & 0xFF) | (((byte) (ny * 127) & 0xFF) << 8) | (((byte) (nz * 127) & 0xFF) << 16);
	}
}
