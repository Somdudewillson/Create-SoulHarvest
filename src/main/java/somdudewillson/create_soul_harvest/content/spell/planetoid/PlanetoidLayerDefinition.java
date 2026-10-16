package somdudewillson.create_soul_harvest.content.spell.planetoid;

import net.minecraft.world.phys.Vec3;

/**
 * Definition of a single layer within a planetoid.
 *
 * @param name identifier or debug name for this layer
 * @param centerOffset offset from the planetoid's origin (used to stack eccentric layers)
 * @param radius the radius of the sphere governing this layer
 * @param builder the builder that populates blocks and handles decoration
 */
public record PlanetoidLayerDefinition(
		String name,
		Vec3 centerOffset,
		double radius,
		PlanetoidLayerBuilder builder
) {
	public Vec3 getCenter(Vec3 origin) {
		return origin.add(centerOffset);
	}

	public double getRadiusSqr() {
		return radius * radius;
	}
}
