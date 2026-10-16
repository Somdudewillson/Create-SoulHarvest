package somdudewillson.create_soul_harvest.content.spell.planetoid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface PlanetoidTemplateSelector {

	/**
	 * Selects an appropriate planetoid template for the given casting context before Phase 2 begins.
	 *
	 * @param level the server level
	 * @param origin the spell's origin point
	 * @param random the random source
	 * @return the selected PlanetoidTemplate
	 */
	PlanetoidTemplate select(ServerLevel level, Vec3 origin, RandomSource random);
}
