package somdudewillson.create_soul_harvest.content.spell.planetoid;

import java.util.List;

/**
 * Encapsulates the overall specification of a planetoid variant.
 */
public record PlanetoidTemplate(
		String id,
		double maxRadius,
		double heightBuffer,
		int chargeTicks,
		int shellBlocksPerTick,
		int generationInterval,
		int generationBlocksPerInterval,
		List<PlanetoidLayerDefinition> layers
) {
	/**
	 * Radius within which another thinker casting this spell triggers a mutual fizzle.
	 * Configured as 3x the max planetoid radius.
	 */
	public double getConflictRadius() {
		return 3.0 * maxRadius;
	}

	/**
	 * Total elevation above the casting position for the planetoid's origin.
	 */
	public double getSpawnElevation() {
		return maxRadius + heightBuffer;
	}
}
