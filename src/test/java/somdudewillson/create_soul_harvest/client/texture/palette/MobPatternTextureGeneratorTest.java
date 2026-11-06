package somdudewillson.create_soul_harvest.client.texture.palette;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MobPatternTextureGeneratorTest {

	@Test
	public void testHardEdgeWhenMergedFromIsZero() {
		PaletteColor colorA = new PaletteColor(255, 0, 0, 10); // Red, mergedFrom = 0
		PaletteColor colorB = new PaletteColor(0, 0, 255, 20); // Blue, mergedFrom = 0

		assertEquals(0, colorA.getMergedFrom());
		assertEquals(0, colorB.getMergedFrom());

		MobPalette palette = new MobPalette(
				List.of(colorA, colorB),
				List.of(colorA, colorB),
				List.of(),
				30
		);

		MobPatternTextureGenerator generator = new MobPatternTextureGenerator();
		BufferedImage image = generator.generateTexture(palette, 12345L);

		int expectedRed = colorA.toArgbInt();
		int expectedBlue = colorB.toArgbInt();

		// Rows 0..7 must be strictly Red (colorA)
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 16; x++) {
				assertEquals(expectedRed, image.getRGB(x, y), "Row " + y + " should be solid Color A");
			}
		}

		// Rows 8..15 must be strictly Blue (colorB)
		for (int y = 8; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				assertEquals(expectedBlue, image.getRGB(x, y), "Row " + y + " should be solid Color B");
			}
		}
	}

	@Test
	public void testDitherBandWhenMergedFromSumIsOne() {
		PaletteColor baseA = new PaletteColor(255, 0, 0, 10);
		PaletteColor baseB1 = new PaletteColor(0, 0, 200, 10);
		PaletteColor baseB2 = new PaletteColor(0, 0, 255, 10);
		PaletteColor colorB = baseB1.merge(baseB2); // mergedFrom = 1

		assertEquals(0, baseA.getMergedFrom());
		assertEquals(1, colorB.getMergedFrom());

		MobPalette palette = new MobPalette(
				List.of(baseA, colorB),
				List.of(baseA, colorB),
				List.of(),
				30
		);

		MobPatternTextureGenerator generator = new MobPatternTextureGenerator();
		BufferedImage image = generator.generateTexture(palette, 98765L);

		int expectedA = baseA.toArgbInt();
		int expectedB = colorB.toArgbInt();

		// Rows 0..4 should be solid Color A
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 16; x++) {
				assertEquals(expectedA, image.getRGB(x, y), "Row " + y + " should be solid Color A");
			}
		}

		// Rows 11..15 should be solid Color B
		for (int y = 11; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				assertEquals(expectedB, image.getRGB(x, y), "Row " + y + " should be solid Color B");
			}
		}

		// Rows 5..10 is the 50/50 band: both colors should be present
		int countA = 0;
		int countB = 0;
		for (int y = 5; y <= 10; y++) {
			for (int x = 0; x < 16; x++) {
				int rgb = image.getRGB(x, y);
				if (rgb == expectedA) countA++;
				else if (rgb == expectedB) countB++;
				else fail("Unexpected pixel color in dither band: " + rgb);
			}
		}

		assertTrue(countA > 0, "Color A should appear in 50/50 band");
		assertTrue(countB > 0, "Color B should appear in 50/50 band");
		double bRatio = (double) countB / (countA + countB);
		// With 96 pixels (6 rows * 16), 50% probability should be approximately balanced (0.35 - 0.65)
		assertTrue(bRatio >= 0.35 && bRatio <= 0.65, "Expected ~50% B pixels in transition band, got: " + bRatio);
	}

	@Test
	public void testSteppedDownsampleWhenMergedFromIsLarge() {
		PaletteColor colorA = new PaletteColor(10, 10, 10, 10);
		PaletteColor colorB = new PaletteColor(200, 200, 200, 10);

		// Manually create mergedFrom count by merging
		PaletteColor mergedA = colorA;
		for (int i = 0; i < 50; i++) {
			mergedA = mergedA.merge(new PaletteColor(10, 10, 10, 1));
		}
		PaletteColor mergedB = colorB;
		for (int i = 0; i < 50; i++) {
			mergedB = mergedB.merge(new PaletteColor(200, 200, 200, 1));
		}

		assertTrue(mergedA.getMergedFrom() + mergedB.getMergedFrom() >= 100);

		MobPalette palette = new MobPalette(
				List.of(mergedA, mergedB),
				List.of(mergedA, mergedB),
				List.of(),
				100
		);

		MobPatternTextureGenerator generator = new MobPatternTextureGenerator();
		BufferedImage image = generator.generateTexture(palette, 42L);

		int expectedA = mergedA.toArgbInt();
		int expectedB = mergedB.toArgbInt();

		int topBCount = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 16; x++) {
				if (image.getRGB(x, y) == expectedB) topBCount++;
			}
		}

		int bottomBCount = 0;
		for (int y = 12; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				if (image.getRGB(x, y) == expectedB) bottomBCount++;
			}
		}

		assertTrue(topBCount < bottomBCount, "Top rows should have fewer B pixels than bottom rows");
	}

	@Test
	public void testSinglePatternMaskForMultipleRareColorsGradient() {
		PaletteColor gray = new PaletteColor(50, 50, 50, 80);
		PaletteColor rareYellow = new PaletteColor(255, 255, 0, 5); // least frequent rare -> top
		PaletteColor rarePurple = new PaletteColor(128, 0, 128, 15); // most frequent rare -> bottom

		MobPalette palette = new MobPalette(
				List.of(gray, rareYellow, rarePurple),
				List.of(gray),
				List.of(rareYellow, rarePurple),
				100
		);

		// Symmetrical vertical line mask across all rows at column 8 (immune to vertical/horizontal flip displacement)
		BufferedImage mask = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < 16; y++) {
			mask.setRGB(7, y, 0xFFFFFFFF);
			mask.setRGB(8, y, 0xFFFFFFFF);
		}

		MobPatternTextureGenerator generator = new MobPatternTextureGenerator();
		MobPatternTextureGenerator.GenerationResult result = generator.generateTextureWithDetails(
				palette,
				1234L,
				List.of(new MobPatternTextureGenerator.NamedMask("center_stripe", mask))
		);

		BufferedImage img = result.image();
		assertNotNull(result.appliedPattern());
		assertEquals("center_stripe", result.appliedPattern().maskName());
		assertEquals(2, result.arrangedRare().size());

		// In column 7 & 8:
		// Top rows (0..7) should be rareYellow (since mergedFrom sum is 0, hard edge in rare gradient)
		// Bottom rows (8..15) should be rarePurple
		int yellowRgb = rareYellow.toArgbInt();
		int purpleRgb = rarePurple.toArgbInt();
		int grayRgb = gray.toArgbInt();

		for (int y = 0; y < 8; y++) {
			assertEquals(yellowRgb, img.getRGB(7, y), "Top of rare gradient should be rareYellow");
			assertEquals(yellowRgb, img.getRGB(8, y), "Top of rare gradient should be rareYellow");
		}
		for (int y = 8; y < 16; y++) {
			assertEquals(purpleRgb, img.getRGB(7, y), "Bottom of rare gradient should be rarePurple");
			assertEquals(purpleRgb, img.getRGB(8, y), "Bottom of rare gradient should be rarePurple");
		}

		// Non-masked pixels (e.g. column 0) must remain common gray
		for (int y = 0; y < 16; y++) {
			assertEquals(grayRgb, img.getRGB(0, y), "Non-masked pixel should remain common gray");
		}
	}
}
