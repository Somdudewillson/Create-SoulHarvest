package somdudewillson.create_soul_harvest.content.spell.planetoid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface PlanetoidLayerBuilder {

	/**
	 * Populates blocks for this layer into the provided partial planetoid generation container.
	 *
	 * @param level the server level
	 * @param layerDef this layer's definition
	 * @param origin the origin point of the planetoid
	 * @param generation the partial generation container wrapping the placement map
	 * @param random the random source
	 */
	void populate(ServerLevel level, PlanetoidLayerDefinition layerDef, Vec3 origin, PartialPlanetoidGeneration generation, RandomSource random);
}
