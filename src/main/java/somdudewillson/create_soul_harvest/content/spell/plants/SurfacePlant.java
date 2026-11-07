package somdudewillson.create_soul_harvest.content.spell.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.create_soul_harvest.registry.ModTags;

public interface SurfacePlant {

	/**
	 * Checks if this plant strategy can plant at the specified ground position.
	 *
	 * @param level       The server level
	 * @param groundPos   The surface ground block position (e.g. grass, dirt, sand)
	 * @param groundState The block state of groundPos
	 * @return true if applicable to this spot
	 */
	boolean isApplicable(ServerLevel level, BlockPos groundPos, BlockState groundState);

	/**
	 * Executes planting at the given ground position.
	 *
	 * @param level       The server level
	 * @param groundPos   The surface ground block position
	 * @param groundState The block state of groundPos
	 * @param random      RandomSource for variation
	 * @return true if planting was successfully performed
	 */
	boolean plant(ServerLevel level, BlockPos groundPos, BlockState groundState, RandomSource random);

	static boolean isReplaceable(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.isAir() || state.canBeReplaced() || state.is(ModTags.Blocks.CONSIDER_REPLACEABLE);
	}
	
	static int getDepthUnderwater(ServerLevel level, BlockPos pos, int limit) {
		int depth = 0;
		do {
			FluidState state = level.getBlockState(pos.above(depth)).getFluidState();
			if (!state.is(Fluids.WATER)) {
				return depth;
			}
			depth++;
		} while (depth <= limit);
		
		return depth;
	}
}
