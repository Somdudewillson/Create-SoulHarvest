package somdudewillson.create_soul_harvest.client.texture.palette;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles software compositing of attuned soul book textures with mob pattern textures.</br>
 *</br>
 * Procedure:</br>
 * 1. Find the most frequent color in the base book texture (which is the leather book cover).</br>
 * 2. Rotate the mob pattern texture 45 degrees clockwise around its center.</br>
 * 3. Calculate distance from the most frequent color to construct a continuous mask:</br>
 *    - Exact match: 100% mob pattern (weight = {@code DEFAULT_MAX_WEIGHT})</br>
 *    - Cutoff maximum distance: 50% mob pattern (weight = {@code DEFAULT_MIN_WEIGHT})</br>
 *    - Beyond cutoff: 0% mob pattern
 */
public class AttunedSoulBookCompositor {

	public static final int TEXTURE_SIZE = 16;
	public static final double DEFAULT_MAX_DISTANCE = 68.0;
	public static final float DEFAULT_MIN_WEIGHT = 0.5f;
	public static final float DEFAULT_MAX_WEIGHT = 1.0f;

	private static final double SQRT2_OVER_2 = Math.sqrt(2.0) / 2.0;

	/**
	 * Scans the base image for the most frequent visible RGB color.
	 */
	public static int findMostFrequentColor(BufferedImage image) {
		Map<Integer, Integer> counts = new HashMap<>();
		int width = image.getWidth();
		int height = image.getHeight();

		int mostFrequentColor = 0;
		int maxCount = -1;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = image.getRGB(x, y);
				int alpha = (argb >>> 24) & 0xFF;
				if (alpha >= 128) {
					int rgb = argb & 0x00FFFFFF;
					int count = counts.merge(rgb, 1, Integer::sum);
					if (count > maxCount) {
						maxCount = count;
						mostFrequentColor = rgb;
					}
				}
			}
		}

		return mostFrequentColor;
	}

	/**
	 * Rotates a texture 45 degrees clockwise around its center using inverse nearest-neighbor sampling.
	 */
	public static BufferedImage rotate45Clockwise(BufferedImage source) {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage rotated = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		double cx = (width - 1) / 2.0;
		double cy = (height - 1) / 2.0;

		for (int y = 0; y < height; y++) {
			double dy = y - cy;
			for (int x = 0; x < width; x++) {
				double dx = x - cx;
				double u = cx + SQRT2_OVER_2 * (dx + dy);
				double v = cy + SQRT2_OVER_2 * (-dx + dy);

				int sampleX = Math.max(0, Math.min(width - 1, (int) Math.round(u)));
				int sampleY = Math.max(0, Math.min(height - 1, (int) Math.round(v)));

				rotated.setRGB(x, y, source.getRGB(sampleX, sampleY));
			}
		}

		return rotated;
	}

	/**
	 * Computes continuous mask weights for each pixel in baseImage.</br>
	 * Weight is maxWeight at distance 0, linearly scaling down to minWeight
	 * at maxDistance, and 0.0 beyond maxDistance.
	 */
	public static float[][] computeMaskWeights(
			BufferedImage baseImage,
			int targetRgb,
			double maxDistance,
			float minWeight,
			float maxWeight
	) {
		int width = baseImage.getWidth();
		int height = baseImage.getHeight();
		float[][] weights = new float[width][height];

		int targetR = (targetRgb >> 16) & 0xFF;
		int targetG = (targetRgb >> 8) & 0xFF;
		int targetB = targetRgb & 0xFF;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = baseImage.getRGB(x, y);
				int alpha = (argb >>> 24) & 0xFF;
				if (alpha < 128) {
					weights[x][y] = 0.0f;
					continue;
				}

				int r = (argb >> 16) & 0xFF;
				int g = (argb >> 8) & 0xFF;
				int b = argb & 0xFF;

				double dr = r - targetR;
				double dg = g - targetG;
				double db = b - targetB;
				double dist = Math.sqrt(dr * dr + dg * dg + db * db);

				if (dist <= maxDistance) {
					float t = (float) (dist / maxDistance);
					weights[x][y] = maxWeight - (maxWeight - minWeight) * t;
				} else {
					weights[x][y] = 0.0f;
				}
			}
		}

		return weights;
	}

	/**
	 * Composites the base book texture with a mob pattern using default parameters.
	 */
	public static BufferedImage compose(BufferedImage baseImage, @Nullable BufferedImage mobPattern) {
		return compose(baseImage, mobPattern, DEFAULT_MAX_DISTANCE, DEFAULT_MIN_WEIGHT, DEFAULT_MAX_WEIGHT);
	}

	/**
	 * Composites the base book texture with a mob pattern using custom distance and weight parameters.
	 */
	public static BufferedImage compose(
			BufferedImage baseImage,
			@Nullable BufferedImage mobPattern,
			double maxDistance,
			float minWeight,
			float maxWeight
	) {
		int width = baseImage.getWidth();
		int height = baseImage.getHeight();
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		if (mobPattern == null) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					result.setRGB(x, y, baseImage.getRGB(x, y));
				}
			}
			return result;
		}

		int mostFrequentColor = findMostFrequentColor(baseImage);
		BufferedImage rotatedPattern = rotate45Clockwise(mobPattern);
		float[][] weights = computeMaskWeights(baseImage, mostFrequentColor, maxDistance, minWeight, maxWeight);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int baseArgb = baseImage.getRGB(x, y);
				int baseAlpha = (baseArgb >>> 24) & 0xFF;

				if (baseAlpha == 0) {
					result.setRGB(x, y, 0);
					continue;
				}

				float weight = weights[x][y];
				if (weight <= 0.0f) {
					result.setRGB(x, y, baseArgb);
					continue;
				}

				int baseR = (baseArgb >> 16) & 0xFF;
				int baseG = (baseArgb >> 8) & 0xFF;
				int baseB = baseArgb & 0xFF;

				int patternArgb = rotatedPattern.getRGB(x, y);
				int patternR = (patternArgb >> 16) & 0xFF;
				int patternG = (patternArgb >> 8) & 0xFF;
				int patternB = patternArgb & 0xFF;

				int outR = Math.min(255, Math.max(0, Math.round(baseR * (1.0f - weight) + patternR * weight)));
				int outG = Math.min(255, Math.max(0, Math.round(baseG * (1.0f - weight) + patternG * weight)));
				int outB = Math.min(255, Math.max(0, Math.round(baseB * (1.0f - weight) + patternB * weight)));

				int finalArgb = (baseAlpha << 24) | (outR << 16) | (outG << 8) | outB;
				result.setRGB(x, y, finalArgb);
			}
		}

		return result;
	}
}
