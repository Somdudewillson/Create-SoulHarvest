package somdudewillson.create_soul_harvest.content.spell.planetoid.decorator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;

public interface SurfaceDecorator {

	/**
	 * Chance (0.0 to 1.0) that this decorator will be considered for a surface block.
	 */
	float getChance();

	/**
	 * Checks whether decoration can legally occur above this surface block.
	 *
	 * @param generation the partial planetoid generation
	 * @param surfacePos the position of the solid surface block
	 * @param surfaceState the state of the solid surface block
	 * @return true if valid
	 */
	boolean canDecorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState);

	/**
	 * Places the decoration into the world (usually at surfacePos.above()).
	 *
	 * @param generation the partial planetoid generation
	 * @param surfacePos the position of the solid surface block
	 * @param surfaceState the state of the solid surface block
	 * @param random the random source
	 */
	void decorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState, RandomSource random);
}
