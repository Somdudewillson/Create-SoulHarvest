package somdudewillson.create_soul_harvest.client.texture.palette;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MobPatternTextureGenerator {
	public static final int TEXTURE_SIZE = 16;

	public record NamedMask(String name, BufferedImage image) {}

	public record AppliedPattern(String maskName, boolean flipX, boolean flipY) {}

	public record GenerationResult(
			BufferedImage image,
			List<PaletteColor> arrangedCommon,
			List<PaletteColor> arrangedRare,
			AppliedPattern appliedPattern
	) {}

	/**
	 * Generates the final 16x16 representative texture for an entity using default pattern masks.
	 *
	 * @param palette The extracted mob palette
	 * @param seed    RNG seed
	 * @return A 16x16 ARGB BufferedImage
	 */
	public BufferedImage generateTexture(MobPalette palette, long seed) {
		return generateTextureWithDetails(palette, seed, Collections.emptyList()).image();
	}

	/**
	 * Generates the final 16x16 representative texture for an entity using the given raw mask images.
	 *
	 * @param palette      The extracted mob palette
	 * @param seed         RNG seed
	 * @param patternMasks List of pattern mask images to select from for rare colors
	 * @return A 16x16 ARGB BufferedImage
	 */
	public BufferedImage generateTexture(MobPalette palette, long seed, List<BufferedImage> patternMasks) {
		List<NamedMask> named = new ArrayList<>();
		if (patternMasks != null) {
			for (int i = 0; i < patternMasks.size(); i++) {
				named.add(new NamedMask("mask_" + i, patternMasks.get(i)));
			}
		}
		return generateTextureWithDetails(palette, seed, named).image();
	}

	/**
	 * Generates the final 16x16 representative texture and details about the applied masks.
	 *
	 * @param palette      The extracted mob palette
	 * @param seed         RNG seed
	 * @param patternMasks List of named pattern mask images
	 * @return A GenerationResult containing the final image
	 */
	public GenerationResult generateTextureWithDetails(MobPalette palette, long seed, List<NamedMask> patternMasks) {
		BufferedImage image = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);

		List<PaletteColor> arrangedCommon = arrangeCommonColors(palette.commonColors());
		renderCommonGradient(image, arrangedCommon, seed);

		List<PaletteColor> rareColors = palette.rareColors();
		List<PaletteColor> arrangedRare = Collections.emptyList();
		AppliedPattern appliedPattern = null;

		if (!rareColors.isEmpty() && patternMasks != null && !patternMasks.isEmpty()) {
			arrangedRare = arrangeCommonColors(rareColors);
			BufferedImage rareGradient = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
			long rareSeed = seed + 31L;
			renderCommonGradient(rareGradient, arrangedRare, rareSeed);

			Random rng = new Random(rareSeed);
			NamedMask mask = patternMasks.get(rng.nextInt(patternMasks.size()));
			boolean flipX = rng.nextBoolean();
			boolean flipY = rng.nextBoolean();

			applyPatternMask(image, rareGradient, mask.image(), flipX, flipY);
			appliedPattern = new AppliedPattern(mask.name(), flipX, flipY);
		}

		return new GenerationResult(image, arrangedCommon, Collections.unmodifiableList(arrangedRare), appliedPattern);
	}

	/**
	 * Overlays an overlay image onto the target image using a pattern mask,
	 * applying optional horizontal and vertical flips to the mask.
	 */
	public static void applyPatternMask(BufferedImage targetImage, BufferedImage overlayImage, BufferedImage mask, boolean flipX, boolean flipY) {
		int maskW = mask.getWidth();
		int maskH = mask.getHeight();
		int targetW = targetImage.getWidth();
		int targetH = targetImage.getHeight();
		int overlayW = overlayImage.getWidth();
		int overlayH = overlayImage.getHeight();

		for (int y = 0; y < targetH; y++) {
			int maskY = (y * maskH) / targetH;
			if (flipY) {
				maskY = maskH - 1 - maskY;
			}
			int overlayY = (y * overlayH) / targetH;

			for (int x = 0; x < targetW; x++) {
				int maskX = (x * maskW) / targetW;
				if (flipX) {
					maskX = maskW - 1 - maskX;
				}
				int overlayX = (x * overlayW) / targetW;

				int maskPixel = mask.getRGB(maskX, maskY);
				int alpha = (maskPixel >>> 24) & 0xFF;
				if (alpha == 255) {
					targetImage.setRGB(x, y, overlayImage.getRGB(overlayX, overlayY));
				} else if (alpha > 0) {
					int overlayPixel = overlayImage.getRGB(overlayX, overlayY);
					int overlayR = (overlayPixel >> 16) & 0xFF;
					int overlayG = (overlayPixel >> 8) & 0xFF;
					int overlayB = overlayPixel & 0xFF;

					int basePixel = targetImage.getRGB(x, y);
					int baseR = (basePixel >> 16) & 0xFF;
					int baseG = (basePixel >> 8) & 0xFF;
					int baseB = basePixel & 0xFF;

					float a = alpha / 255.0f;
					int outR = Math.round(overlayR * a + baseR * (1.0f - a));
					int outG = Math.round(overlayG * a + baseG * (1.0f - a));
					int outB = Math.round(overlayB * a + baseB * (1.0f - a));
					int outArgb = 0xFF000000 | (outR << 16) | (outG << 8) | outB;
					targetImage.setRGB(x, y, outArgb);
				}
			}
		}
	}

	/**
	 * Orders common colors:
	 * Top (index 0) = least frequent common color
	 * Bottom (index M-1) = most frequent common color
	 * Intermediate colors = permutation minimizing total color transition distance.
	 */
	public static List<PaletteColor> arrangeCommonColors(List<PaletteColor> commonColors) {
		if (commonColors.isEmpty()) {
			return List.of(new PaletteColor(128, 128, 128, 1));
		}
		if (commonColors.size() == 1) {
			return List.of(commonColors.get(0));
		}

		// Sort ascending by frequency so least frequent is at index 0, most frequent is at last index
		List<PaletteColor> sortedByFreqAsc = new ArrayList<>(commonColors);
		sortedByFreqAsc.sort(Comparator.comparingInt(PaletteColor::getCount));

		PaletteColor topColor = sortedByFreqAsc.get(0); // least frequent
		PaletteColor bottomColor = sortedByFreqAsc.get(sortedByFreqAsc.size() - 1); // most frequent

		if (commonColors.size() == 2) {
			return List.of(topColor, bottomColor);
		}

		List<PaletteColor> intermediates = new ArrayList<>(sortedByFreqAsc.subList(1, sortedByFreqAsc.size() - 1));
		List<PaletteColor> bestOrder = new ArrayList<>(intermediates);
		double bestDistance = Double.MAX_VALUE;

		List<List<PaletteColor>> permutations = new ArrayList<>();
		permute(intermediates, 0, permutations);

		// Exhaustive search is 'slow', but even O(n!) complexity is plenty fast for sufficiently small n
		for (List<PaletteColor> perm : permutations) {
			double totalDist = topColor.deltaE(perm.get(0));
			for (int i = 0; i < perm.size() - 1; i++) {
				totalDist += perm.get(i).deltaE(perm.get(i + 1));
			}
			totalDist += perm.get(perm.size() - 1).deltaE(bottomColor);

			if (totalDist < bestDistance) {
				bestDistance = totalDist;
				bestOrder = perm;
			}
		}

		List<PaletteColor> result = new ArrayList<>(commonColors.size());
		result.add(topColor);
		result.addAll(bestOrder);
		result.add(bottomColor);
		return result;
	}

	private static void permute(List<PaletteColor> list, int k, List<List<PaletteColor>> result) {
		for (int i = k; i < list.size(); i++) {
			Collections.swap(list, i, k);
			permute(list, k + 1, result);
			Collections.swap(list, k, i);
		}
		if (k == list.size() - 1) {
			result.add(new ArrayList<>(list));
		}
	}

	private void renderCommonGradient(BufferedImage image, List<PaletteColor> colors, long seed) {
		if (colors.isEmpty()) {
			return;
		}

		int numColors = colors.size();
		if (numColors == 1) {
			int rgb = colors.get(0).toArgbInt();
			for (int y = 0; y < TEXTURE_SIZE; y++) {
				for (int x = 0; x < TEXTURE_SIZE; x++) {
					image.setRGB(x, y, rgb);
				}
			}
			return;
		}

		Random rng = new Random(seed);

		for (int y = 0; y < TEXTURE_SIZE; y++) {
			double normalizedY = (y + 0.5) / (double) TEXTURE_SIZE;
			double scaledPos = normalizedY * (numColors - 1);
			int index = Math.min(numColors - 2, Math.max(0, (int) Math.floor(scaledPos)));
			double t = Math.max(0.0, Math.min(1.0, scaledPos - index));

			PaletteColor colorA = colors.get(index);
			PaletteColor colorB = colors.get(index + 1);

			int mergedSum = colorA.getMergedFrom() + colorB.getMergedFrom();
			int numIntervals = mergedSum + 2;
			int k = Math.min(mergedSum + 1, Math.max(0, (int) Math.floor(t * numIntervals)));

			double pB;
			if (mergedSum == 0) {
				pB = (k == 0) ? 0.0 : 1.0;
			} else {
				if (k == 0) {
					pB = 0.0;
				} else if (k == mergedSum + 1) {
					pB = 1.0;
				} else {
					pB = (double) k / (double) (mergedSum + 1);
				}
			}

			int argbA = colorA.toArgbInt();
			int argbB = colorB.toArgbInt();

			for (int x = 0; x < TEXTURE_SIZE; x++) {
				int rgb;
				if (pB <= 0.0) {
					rgb = argbA;
				} else if (pB >= 1.0) {
					rgb = argbB;
				} else {
					rgb = (rng.nextDouble() < pB) ? argbB : argbA;
				}
				image.setRGB(x, y, rgb);
			}
		}
	}
}
