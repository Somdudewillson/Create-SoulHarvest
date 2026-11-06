package somdudewillson.create_soul_harvest.client.texture.palette;

import org.junit.jupiter.api.Test;

import somdudewillson.create_soul_harvest.client.texture.dynamic.AttunedPolyclayCompositor;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class AttunedPolyclayCompositorTest {

	private static final float EPSILON = 1e-5f;

	@Test
	public void testOverlayFormula() {
		assertEquals(0.0f, AttunedPolyclayCompositor.overlay(0.0f, 0.5f), EPSILON);
		assertEquals(1.0f, AttunedPolyclayCompositor.overlay(1.0f, 0.5f), EPSILON);
		assertEquals(0.5f, AttunedPolyclayCompositor.overlay(0.5f, 0.5f), EPSILON);
		assertEquals(0.25f, AttunedPolyclayCompositor.overlay(0.25f, 0.5f), EPSILON);
		assertEquals(0.75f, AttunedPolyclayCompositor.overlay(0.75f, 0.5f), EPSILON);
		assertEquals(0.125f, AttunedPolyclayCompositor.overlay(0.25f, 0.25f), EPSILON);
	}

	@Test
	public void testVividLightFormula() {
		assertEquals(0.0f, AttunedPolyclayCompositor.vividLight(0.5f, 0.0f), EPSILON);
		assertEquals(1.0f, AttunedPolyclayCompositor.vividLight(0.5f, 1.0f), EPSILON);
		assertEquals(0.5f, AttunedPolyclayCompositor.vividLight(0.5f, 0.5f), EPSILON);

		// Color burn side (S <= 0.5)
		float burned = AttunedPolyclayCompositor.vividLight(0.5f, 0.25f);
		assertEquals(0.0f, burned, EPSILON); // 1 - (1 - 0.5)/(2 * 0.25) = 1 - 1 = 0

		// Color dodge side (S > 0.5)
		float dodged = AttunedPolyclayCompositor.vividLight(0.25f, 0.75f);
		assertEquals(0.5f, dodged, EPSILON); // 0.25 / (2 * (1 - 0.75)) = 0.25 / 0.5 = 0.5
	}

	@Test
	public void testSliceSpritesheet() {
		BufferedImage sheet = new BufferedImage(48, 16, BufferedImage.TYPE_INT_ARGB);
		sheet.setRGB(0, 0, 0xFFFF0000); // S0
		sheet.setRGB(16, 0, 0xFF00FF00); // S1
		sheet.setRGB(32, 0, 0xFF0000FF); // S2

		AttunedPolyclayCompositor.Slices slices = AttunedPolyclayCompositor.sliceSpritesheet(sheet);
		assertNotNull(slices.sprite0());
		assertNotNull(slices.sprite1());
		assertNotNull(slices.sprite2());
		assertEquals(16, slices.sprite0().getWidth());
		assertEquals(16, slices.sprite1().getWidth());
		assertEquals(16, slices.sprite2().getWidth());

		assertEquals(0xFFFF0000, slices.sprite0().getRGB(0, 0));
		assertEquals(0xFF00FF00, slices.sprite1().getRGB(0, 0));
		assertEquals(0xFF0000FF, slices.sprite2().getRGB(0, 0));
	}

	@Test
	public void testCompositionTransparencyPreserved() {
		BufferedImage s0 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		BufferedImage s1 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		BufferedImage s2 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		BufferedImage pattern = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Fill pattern, mask, and s2 with fully opaque white
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				s1.setRGB(x, y, 0xFFFFFFFF);
				s2.setRGB(x, y, 0xFFFFFFFF);
				pattern.setRGB(x, y, 0xFFFFFFFF);
			}
		}

		// Only pixel (5, 5) is non-transparent in Sprite 0
		s0.setRGB(5, 5, 0xFF808080);

		BufferedImage comp = AttunedPolyclayCompositor.compose(s0, pattern, s1, s2);

		// Every pixel except (5, 5) must be 0x00000000
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				if (x == 5 && y == 5) {
					int alpha = (comp.getRGB(x, y) >> 24) & 0xFF;
					assertEquals(255, alpha);
				} else {
					assertEquals(0, comp.getRGB(x, y), "Pixel at (" + x + "," + y + ") should be transparent");
				}
			}
		}
	}
}
