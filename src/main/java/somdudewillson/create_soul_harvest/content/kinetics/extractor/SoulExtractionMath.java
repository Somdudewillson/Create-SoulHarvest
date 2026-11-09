package somdudewillson.create_soul_harvest.content.kinetics.extractor;

public class SoulExtractionMath {

	public static final float MIN_RPM = 16.0f;
	public static final float MAX_RPM = 256.0f;
	public static final float MAX_POINTS_PER_EXTRACTOR = 100.0f;
	public static final float DIMINISHING_EXPONENT = 0.6f;
	public static final float EXTRACTION_POINT_LIMIT = 2.5f * MAX_POINTS_PER_EXTRACTOR; // 250.0f
	public static final long DEFAULT_MAX_POTENTIA_PER_TICK = 100L;

	/**
	 * Calculates extraction points for a single extractor based on input RPM.
	 * Returns 0 if |RPM| < MIN_RPM. Scales with diminishing returns up to MAX_RPM.
	 */
	public static float calculatePoints(float rpm) {
		float absRpm = Math.abs(rpm);
		if (absRpm < MIN_RPM) {
			return 0.0f;
		}
		float clampedRpm = Math.min(absRpm, MAX_RPM);
		float progress = (clampedRpm - MIN_RPM) / (MAX_RPM - MIN_RPM);
		return (float) (MAX_POINTS_PER_EXTRACTOR * Math.pow(progress, DIMINISHING_EXPONENT));
	}

	/**
	 * Calculates total potentia extracted from a growth block per tick across all extractors.
	 * Reaches maxPotentiaPerTick when totalPoints reaches EXTRACTION_POINT_LIMIT (250).
	 */
	public static long calculateTotalPotentia(float totalPoints, long maxPotentiaPerTick) {
		if (totalPoints <= 0.0f || maxPotentiaPerTick <= 0) {
			return 0L;
		}
		float ratio = Math.min(1.0f, totalPoints / EXTRACTION_POINT_LIMIT);
		return Math.round(maxPotentiaPerTick * ratio);
	}

	/**
	 * Calculates an extractor's proportional share of the extracted potentia pool.
	 */
	public static long calculateShare(float individualPoints, float totalPoints, long totalExtracted) {
		if (individualPoints <= 0.0f || totalPoints <= 0.0f || totalExtracted <= 0) {
			return 0L;
		}
		return Math.round(totalExtracted * ((double) individualPoints / (double) totalPoints));
	}
}
