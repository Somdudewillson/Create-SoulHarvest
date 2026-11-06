package somdudewillson.create_soul_harvest.client.texture.palette;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MobPaletteExtractor {
	public static final int DEFAULT_MAX_PALETTE_SIZE = 8;
	public static final double DEFAULT_RARE_PIVOT_FREQUENCY = 0.05;

	private final int maxPaletteSize;
	private final double rarePivotFrequency;

	public MobPaletteExtractor() {
		this(DEFAULT_MAX_PALETTE_SIZE, DEFAULT_RARE_PIVOT_FREQUENCY);
	}

	public MobPaletteExtractor(int maxPaletteSize, double rarePivotFrequency) {
		this.maxPaletteSize = Math.max(1, maxPaletteSize);
		this.rarePivotFrequency = rarePivotFrequency;
	}

	/**
	 * Extracts and segments a MobPalette from one or more textures.
	 */
	public Optional<MobPalette> tryExtractPalette(List<BufferedImage> images) {
		Map<Integer, Integer> rawCounts = new HashMap<>();

		for (BufferedImage img : images) {
			if (img == null) continue;
			int width = img.getWidth();
			int height = img.getHeight();

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int argb = img.getRGB(x, y);
					int alpha = (argb >> 24) & 0xFF;
					
					if (alpha < 128) continue;

					int rgb = argb & 0x00FFFFFF;
					rawCounts.merge(rgb, 1, Integer::sum);
				}
			}
		}

		if (rawCounts.isEmpty()) return Optional.empty();

		List<PaletteColor> palette = new ArrayList<>(rawCounts.size());
		long totalPixels = 0;
		for (Map.Entry<Integer, Integer> entry : rawCounts.entrySet()) {
			palette.add(PaletteColor.fromRgb(entry.getKey(), entry.getValue()));
			totalPixels += entry.getValue();
		}

		while (palette.size() > maxPaletteSize) {
			palette = mergeClosestPair(palette);
		}

		palette.sort(Comparator.comparingInt(PaletteColor::getCount).reversed());
		
		List<PaletteColor> commonColors = new ArrayList<>();
		List<PaletteColor> rareColors = new ArrayList<>();

		for (PaletteColor color : palette) {
			double freq = (double) color.getCount() / (double) totalPixels;
			if (freq < rarePivotFrequency) {
				rareColors.add(color);
			} else {
				commonColors.add(color);
			}
		}

		if (commonColors.isEmpty() && !palette.isEmpty()) {
			PaletteColor primary = palette.get(0);
			rareColors.remove(primary);
			commonColors.add(primary);
		}

		return Optional.of(new MobPalette(
				Collections.unmodifiableList(palette),
				Collections.unmodifiableList(commonColors),
				Collections.unmodifiableList(rareColors),
				totalPixels
		));
	}

	private static List<PaletteColor> mergeClosestPair(List<PaletteColor> list) {
		if (list.size() <= 1) return list;

		int bestI = 0;
		int bestJ = 1;
		double minDistance = Double.MAX_VALUE;

		for (int i = 0; i < list.size(); i++) {
			PaletteColor ci = list.get(i);
			for (int j = i + 1; j < list.size(); j++) {
				PaletteColor cj = list.get(j);
				double dist = ci.deltaE(cj);
				if (dist < minDistance) {
					minDistance = dist;
					bestI = i;
					bestJ = j;
				}
			}
		}

		PaletteColor merged = list.get(bestI).merge(list.get(bestJ));
		List<PaletteColor> next = new ArrayList<>(list);
		next.remove(Math.max(bestI, bestJ));
		next.remove(Math.min(bestI, bestJ));
		next.add(merged);
		return next;
	}
}
