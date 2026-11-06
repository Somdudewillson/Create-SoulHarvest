package somdudewillson.create_soul_harvest.client.texture.palette;

import java.util.List;

public record MobPalette(
		List<PaletteColor> allColors,
		List<PaletteColor> commonColors,
		List<PaletteColor> rareColors,
		long totalPixels
) {
	public boolean hasRareColors() {
		return !rareColors.isEmpty();
	}

	public PaletteColor getMostFrequentCommonColor() {
		return commonColors.isEmpty() ? null : commonColors.get(0);
	}

	public PaletteColor getLeastFrequentCommonColor() {
		return commonColors.isEmpty() ? null : commonColors.get(commonColors.size() - 1);
	}
}
