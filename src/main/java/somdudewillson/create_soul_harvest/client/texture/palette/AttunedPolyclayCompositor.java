package somdudewillson.create_soul_harvest.client.texture.palette;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

/**
 * Handles software compositing of the 3-part horizontal attuned polyclay spritesheet
 * with mob pattern textures.
 *
 * Spritesheet layout (48x16 px):
 * - Sprite 0 (x: 0..15): Base item background
 * - Sprite 1 (x: 16..31): Center mask for pattern exposure
 * - Sprite 2 (x: 32..47): Highlight and shine layer
 *
 * Final rendering from bottom to top:
 * 1. Sprite 0
 * 2. If present, mob pattern texture using 'overlay' color mode, masked using Sprite 1
 * 3. Sprite 2 using 'vivid light' color mode
 */
public class AttunedPolyclayCompositor {

	public static final int SPRITE_SIZE = 16;
	public static final int SHEET_WIDTH = 48;
	public static final int SHEET_HEIGHT = 16;

	public record Slices(
			BufferedImage sprite0,
			BufferedImage sprite1,
			BufferedImage sprite2
	) {}

	/**
	 * Extracts the three 16x16 sprites from a 48x16 spritesheet.
	 */
	public static Slices sliceSpritesheet(BufferedImage sheet) {
		if (sheet == null || sheet.getWidth() < SHEET_WIDTH || sheet.getHeight() < SHEET_HEIGHT) {
			throw new IllegalArgumentException("Spritesheet must be at least 48x16 pixels.");
		}
		BufferedImage s0 = sheet.getSubimage(0, 0, SPRITE_SIZE, SPRITE_SIZE);
		BufferedImage s1 = sheet.getSubimage(SPRITE_SIZE, 0, SPRITE_SIZE, SPRITE_SIZE);
		BufferedImage s2 = sheet.getSubimage(SPRITE_SIZE * 2, 0, SPRITE_SIZE, SPRITE_SIZE);
		return new Slices(s0, s1, s2);
	}

	/**
	 * Composites the attuned polyclay texture for a given mob pattern (or null for default/empty).
	 */
	public static BufferedImage compose(
			BufferedImage sprite0,
			@Nullable BufferedImage mobPattern,
			BufferedImage sprite1,
			BufferedImage sprite2
	) {
		BufferedImage result = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < SPRITE_SIZE; y++) {
			for (int x = 0; x < SPRITE_SIZE; x++) {
				int argb0 = sprite0.getRGB(x, y);
				int a0 = (argb0 >> 24) & 0xFF;

				if (a0 == 0) {
					result.setRGB(x, y, 0);
					continue;
				}

				float bR = ((argb0 >> 16) & 0xFF) / 255.0f;
				float bG = ((argb0 >> 8) & 0xFF) / 255.0f;
				float bB = (argb0 & 0xFF) / 255.0f;

				// -- Mob pattern texture with 'overlay' mode, masked by Sprite 1
				if (mobPattern != null) {
					int argb1 = sprite1.getRGB(x, y);
					int a1 = (argb1 >> 24) & 0xFF;

					if (a1 > 1) {
						float r1 = ((argb1 >> 16) & 0xFF) / 255.0f;
						float g1 = ((argb1 >> 8) & 0xFF) / 255.0f;
						float b1 = (argb1 & 0xFF) / 255.0f;
						float lum1 = 0.2126f*r1 + 0.7152f*g1 + 0.0722f*b1;
						float maskWeight = (a1 / 255.0f) * lum1;

						if (maskWeight > 0.0f) {
							int argbP = mobPattern.getRGB(x, y);
							float pR = ((argbP >> 16) & 0xFF) / 255.0f;
							float pG = ((argbP >> 8) & 0xFF) / 255.0f;
							float pB = (argbP & 0xFF) / 255.0f;

							float oR = overlay(bR, pR);
							float oG = overlay(bG, pG);
							float oB = overlay(bB, pB);

							bR = bR + (oR - bR) * maskWeight;
							bG = bG + (oG - bG) * maskWeight;
							bB = bB + (oB - bB) * maskWeight;
						}
					}
				}

				// -- Sprite 2 with 'vivid light' color mode
				int argb2 = sprite2.getRGB(x, y);
				int a2 = (argb2 >> 24) & 0xFF;
				if (a2 > 0) {
					float blendWeight = a2 / 255.0f;
					float sR = ((argb2 >> 16) & 0xFF) / 255.0f;
					float sG = ((argb2 >> 8) & 0xFF) / 255.0f;
					float sB = (argb2 & 0xFF) / 255.0f;

					float vR = vividLight(bR, sR);
					float vG = vividLight(bG, sG);
					float vB = vividLight(bB, sB);

					bR = bR + (vR - bR) * blendWeight;
					bG = bG + (vG - bG) * blendWeight;
					bB = bB + (vB - bB) * blendWeight;
				}

				int fR = Math.min(255, Math.max(0, Math.round(bR * 255.0f)));
				int fG = Math.min(255, Math.max(0, Math.round(bG * 255.0f)));
				int fB = Math.min(255, Math.max(0, Math.round(bB * 255.0f)));
				int finalArgb = (a0 << 24) | (fR << 16) | (fG << 8) | fB;
				result.setRGB(x, y, finalArgb);
			}
		}

		return result;
	}

	/**
	 * Photoshop / W3C standard Overlay blend function:
	 * f(B, S) = 2 * B * S if B <= 0.5, else 1 - 2 * (1 - B) * (1 - S).
	 */
	public static float overlay(float b, float s) {
		if (b <= 0.5f) {
			return 2.0f * b * s;
		} else {
			return 1.0f - 2.0f * (1.0f - b) * (1.0f - s);
		}
	}

	/**
	 * Photoshop / W3C standard Vivid Light blend function:
	 * Combines Color Burn for S <= 0.5 and Color Dodge for S > 0.5.
	 */
	public static float vividLight(float b, float s) {
		if (s <= 0.5f) {
			if (s <= 1e-6f) {
				return 0.0f;
			}
			float burn = 1.0f - (1.0f - b) / (2.0f * s);
			return Math.max(0.0f, burn);
		} else {
			if (s >= 1.0f - 1e-6f) {
				return 1.0f;
			}
			float dodge = b / (2.0f * (1.0f - s));
			return Math.min(1.0f, dodge);
		}
	}
}
