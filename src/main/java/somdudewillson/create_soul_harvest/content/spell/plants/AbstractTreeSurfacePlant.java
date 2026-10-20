package somdudewillson.create_soul_harvest.content.spell.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractTreeSurfacePlant implements SurfacePlant {
	protected static final int MIN_VERTICAL_CLEARANCE = 7;
	protected static final int MIN_TREE_SPACING = 3;

	@Override
	public boolean isApplicable(ServerLevel level, BlockPos groundPos, BlockState groundState) {
		if (!groundState.is(BlockTags.DIRT)) {
			return false;
		}

		BlockPos plantPos = groundPos.above();
		if (!SurfacePlant.isReplaceable(level, plantPos)) {
			return false;
		}

		// Ensure vertical headroom
		for (int dy = 1; dy <= MIN_VERTICAL_CLEARANCE; dy++) {
			BlockPos checkPos = groundPos.above(dy);
			if (!SurfacePlant.isReplaceable(level, checkPos)) {
				return false;
			}
		}

		// Spacing check: ensure no existing logs or saplings nearby to avoid dense clustering
		for (BlockPos nearPos : BlockPos.betweenClosed(
				groundPos.offset(-MIN_TREE_SPACING, 0, -MIN_TREE_SPACING),
				groundPos.offset(MIN_TREE_SPACING, 4, MIN_TREE_SPACING))) {
			BlockState nearState = level.getBlockState(nearPos);
			if (nearState.is(BlockTags.LOGS) || nearState.is(BlockTags.SAPLINGS)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean plant(ServerLevel level, BlockPos groundPos, BlockState groundState, RandomSource random) {
		BlockPos plantPos = groundPos.above();
		BlockState saplingState = pickSapling(level, groundPos, random);
		if (saplingState == null || !(saplingState.getBlock() instanceof SaplingBlock saplingBlock)) {
			return false;
		}

		level.setBlock(plantPos, saplingState, 3);
		saplingBlock.advanceTree(level, plantPos, saplingState.setValue(SaplingBlock.STAGE, 1), random);

		SoundType sound = saplingState.getSoundType(level, plantPos, null);
		level.playSound(null, plantPos, sound.getPlaceSound(), SoundSource.BLOCKS, 0.8F, 1.0F);
		level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plantPos.getX() + 0.5, plantPos.getY() + 0.5, plantPos.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.02);
		return true;
	}

	protected abstract BlockState pickSapling(ServerLevel level, BlockPos groundPos, RandomSource random);
}
