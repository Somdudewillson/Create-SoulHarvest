package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;

public class AbsoluteIceBlock extends Block {

	public AbsoluteIceBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		final int maxDist = 50;
		final int maxDistY = 15;
		final int maxPerTick = 360;
		int sampled = 0;

		// Snow & freezing environmental aura
		for (int i = 0; i < maxPerTick * 2; i++) {
			int dx = random.nextInt(maxDist * 2 + 1) - maxDist;
			int dy = random.nextInt(maxDistY * 2 + 1) - maxDistY;
			int dz = random.nextInt(maxDist * 2 + 1) - maxDist;
			BlockPos testPos = pos.offset(dx, dy, dz);

			double distSqr = pos.distSqr(testPos);
			if (distSqr > maxDist * maxDist) continue;
			if (!level.isLoaded(testPos)) continue;

			double effectStrength = 1.0 - (Math.sqrt(distSqr) / maxDist);
			sampled++;
			if (sampled > maxPerTick) break;

			BlockState testState = level.getBlockState(testPos);
			BlockState aboveState = level.getBlockState(testPos.above());

			if (testState.is(Blocks.LAVA) && effectStrength >= 0.9) {
				level.setBlockAndUpdate(testPos, Blocks.OBSIDIAN.defaultBlockState());
				continue;
			}
			if (testState.is(Blocks.WATER) && !aboveState.is(Blocks.WATER)) {
				level.setBlockAndUpdate(testPos, Blocks.ICE.defaultBlockState());
				continue;
			}
			if (!testState.is(BlockTags.ICE) && !testState.is(BlockTags.SNOW) && !testState.is(this)
					&& !aboveState.is(Blocks.SNOW) && aboveState.getFluidState().isEmpty()
					&& aboveState.canBeReplaced() && testState.isFaceSturdy(level, testPos, Direction.UP, SupportType.FULL)) {
				level.setBlockAndUpdate(testPos.above(), Blocks.SNOW.defaultBlockState());
			}
		}

		// Blue ice spire generation around the block
		final int spirePerTick = 5;
		final int spireHeight = maxDistY;
		int spireCount = 0;

		for (BlockPos spirePos : BlockPos.withinManhattan(pos, 2, spireHeight, 2)) {
			if (spirePos.equals(pos)) continue;
			int horizontalDist = Math.abs(spirePos.getX() - pos.getX()) + Math.abs(spirePos.getZ() - pos.getZ());
			int maxYDist = spireHeight - (int) Math.round(horizontalDist * (spireHeight / 3.2));
			if (Math.abs(spirePos.getY() - pos.getY()) > maxYDist) continue;

			BlockState spireState = level.getBlockState(spirePos);
			if (spireState.is(Blocks.BLUE_ICE)) continue;
			if (!spireState.canBeReplaced()) continue;

			level.setBlockAndUpdate(spirePos, Blocks.BLUE_ICE.defaultBlockState());
			spireCount++;
			if (spireCount >= spirePerTick) break;
		}
	}
}
