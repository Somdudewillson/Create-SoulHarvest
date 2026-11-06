package somdudewillson.create_soul_harvest.client.texture.palette;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class AttunedSoulBookCompositorTest {

	private static final float EPSILON = 1e-4f;

	@Test
	public void testFindMostFrequentColor() {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Fill 50 pixels with color A (#4D76A9)
		int colorA = 0x4D76A9;
		int argbA = 0xFF000000 | colorA;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				img.setRGB(x, y, argbA);
			}
		}

		// Fill 30 pixels with color B (#263C56)
		int colorB = 0x263C56;
		int argbB = 0xFF000000 | colorB;
		for (int y = 5; y < 8; y++) {
			for (int x = 0; x < 10; x++) {
				img.setRGB(x, y, argbB);
			}
		}

		// Fill 100 transparent pixels with color C
		int transparentC = 0x00FF0000;
		for (int y = 8; y < 16; y++) {
			for (int x = 0; x < 12; x++) {
				img.setRGB(x, y, transparentC);
			}
		}

		int found = AttunedSoulBookCompositor.findMostFrequentColor(img);
		assertEquals(colorA, found, "Expected colorA to be the most frequent non-transparent color");
	}

	@Test
	public void testRotate45Clockwise() {
		BufferedImage src = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		// Fill top-left with red, bottom-right with blue, center with green
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				src.setRGB(x, y, 0xFF000000 | (x << 16) | (y << 8));
			}
		}
		// Set 2x2 center block to green
		src.setRGB(7, 7, 0xFF00FF00);
		src.setRGB(7, 8, 0xFF00FF00);
		src.setRGB(8, 7, 0xFF00FF00);
		src.setRGB(8, 8, 0xFF00FF00);

		BufferedImage rotated = AttunedSoulBookCompositor.rotate45Clockwise(src);
		assertNotNull(rotated);
		assertEquals(16, rotated.getWidth());
		assertEquals(16, rotated.getHeight());

		// Center area should map to the green center
		assertEquals(0xFF00FF00, rotated.getRGB(7, 7));
		assertEquals(0xFF00FF00, rotated.getRGB(8, 8));

		// All pixels must have alpha = 255
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int alpha = (rotated.getRGB(x, y) >>> 24) & 0xFF;
				assertEquals(255, alpha, "Every pixel in rotated 16x16 grid should be validly sampled");
			}
		}
	}

	@Test
	public void testContinuousMaskWeights() {
		BufferedImage base = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		int targetRgb = 0x4D76A9; // Target color: R=77, G=118, B=169

		// Pixel (0, 0): Exact match (d = 0)
		base.setRGB(0, 0, 0xFF000000 | targetRgb);

		// Pixel (1, 1): Transparent
		base.setRGB(1, 1, 0x004D76A9);

		// Pixel (2, 2): Halfway to cutoff (d = 34.0, maxDistance = 68.0)
		// Distance = 34.0: (R=77, G=118+34=152, B=169)
		base.setRGB(2, 2, 0xFF000000 | (77 << 16) | (152 << 8) | 169);

		// Pixel (3, 3): Exact cutoff (d = 68.0)
		// Distance = 68.0: (R=77, G=118, B=169-68=101)
		base.setRGB(3, 3, 0xFF000000 | (77 << 16) | (118 << 8) | 101);

		// Pixel (4, 4): Beyond cutoff (d = 100.0)
		base.setRGB(4, 4, 0xFF000000 | (177 << 16) | (118 << 8) | 169);

		float[][] weights = AttunedSoulBookCompositor.computeMaskWeights(base, targetRgb, 68.0, 0.5f, 1.0f);

		// Exact match should be 1.0 (100%)
		assertEquals(1.0f, weights[0][0], EPSILON);

		// Transparent pixel should have 0.0 weight
		assertEquals(0.0f, weights[1][1], EPSILON);

		// Halfway distance should have 0.75 weight (midpoint between 1.0 and 0.5)
		assertEquals(0.75f, weights[2][2], EPSILON);

		// Cutoff distance should have 0.5 weight (50% minimum)
		assertEquals(0.5f, weights[3][3], EPSILON);

		// Beyond cutoff distance should have 0.0 weight
		assertEquals(0.0f, weights[4][4], EPSILON);
	}

	@Test
	public void testComposeTransparencyAndNonCoverPreservation() {
		BufferedImage base = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		BufferedImage pattern = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		int coverColor = 0x4D76A9;
		int borderBlack = 0x041608;
		int mobPatternColor = 0x00FF00; // Pure green

		// Fill pattern with pure green
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				pattern.setRGB(x, y, 0xFF000000 | mobPatternColor);
			}
		}

		// Base book:
		// Fill a 5x5 region with cover color (so it is undeniably the most frequent color)
		for (int y = 4; y <= 8; y++) {
			for (int x = 4; x <= 8; x++) {
				base.setRGB(x, y, 0xFF000000 | coverColor);
			}
		}
		// (0, 0) is border outline (distance > 200, beyond cutoff)
		base.setRGB(0, 0, 0xFF000000 | borderBlack);
		// (15, 15) is transparent
		base.setRGB(15, 15, 0x00000000);

		BufferedImage result = AttunedSoulBookCompositor.compose(base, pattern);

		// Transparent pixel remains transparent
		assertEquals(0, result.getRGB(15, 15));

		// Border pixel beyond cutoff remains untouched
		assertEquals(0xFF000000 | borderBlack, result.getRGB(0, 0));

		// Exact cover pixel (5, 5) with weight 1.0 is 100% mob pattern (green)
		assertEquals(0xFF000000 | mobPatternColor, result.getRGB(5, 5));
	}

	@Test
	public void testComposeNullPatternReturnsBaseCopy() {
		BufferedImage base = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		base.setRGB(2, 2, 0xFFAABBCC);

		BufferedImage result = AttunedSoulBookCompositor.compose(base, null);
		assertNotNull(result);
		assertEquals(0xFFAABBCC, result.getRGB(2, 2));
	}
}
