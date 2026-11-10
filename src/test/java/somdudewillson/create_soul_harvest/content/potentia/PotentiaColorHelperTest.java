package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPalette;
import somdudewillson.create_soul_harvest.client.texture.palette.PaletteColor;
import somdudewillson.create_soul_harvest.content.potentia.client.PotentiaColorHelper;
import somdudewillson.create_soul_harvest.content.potentia.client.PotentiaColorHelper.PotentiaColors;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PotentiaColorHelperTest {

	@Test
	public void testUntypedPotentiaColors() {
		PotentiaColors colors = PotentiaColorHelper.getColors((ResourceLocation) null);
		assertEquals(PotentiaColorHelper.DEFAULT_PRIMARY, colors.primary());
		assertEquals(PotentiaColorHelper.DEFAULT_SECONDARY, colors.secondary());
		assertEquals(PotentiaColorHelper.DEFAULT_ACCENT, colors.accent());

		assertEquals(colors.primary(), colors.getByTintIndex(0));
		assertEquals(colors.secondary(), colors.getByTintIndex(1));
		assertEquals(colors.accent(), colors.getByTintIndex(2));
		assertEquals(0xFFFFFF, colors.getByTintIndex(-1));
	}

	@Test
	public void testColorsFromMobPalette() {
		PaletteColor c1 = PaletteColor.fromRgb(0xFF0000, 100);
		PaletteColor c2 = PaletteColor.fromRgb(0x00FF00, 20);
		PaletteColor c3 = PaletteColor.fromRgb(0x0000FF, 10);

		MobPalette palette = new MobPalette(
				List.of(c1, c2, c3),
				List.of(c1),
				List.of(c2, c3),
				130
		);

		PotentiaColors colors = PotentiaColorHelper.getColorsFromPalette(palette);
		assertEquals(0xFF0000, colors.primary());
		assertEquals(0x00FF00, colors.secondary());
		assertEquals(0x0000FF, colors.accent());
	}
}
