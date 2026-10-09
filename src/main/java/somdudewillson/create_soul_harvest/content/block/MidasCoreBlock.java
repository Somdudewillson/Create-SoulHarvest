package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class MidasCoreBlock extends Block {

	public static final TagKey<Block> MIDAS_CAN_CONVERT = TagKey.create(Registries.BLOCK, SoulHarvestMod.id("midas_can_convert"));

	public MidasCoreBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		final int brancherCount = 3;
		final int brancherMaxNewBlocks = 10;
		final int brancherMaxSteps = 250;
		final int brancherMaxDistance = 150;

		Block goldFoam = ModBlocks.GOLD_FOAM.get();

		for (int b = 0; b < brancherCount; b++) {
			BlockPos brancherPos = pos;
			Direction brancherDir = null;
			int brancherNewBlocks = 0;

			for (int step = 0; step < brancherMaxSteps; step++) {
				List<Direction> turnOptions = new ArrayList<>();
				for (Direction possibleDir : Direction.values()) {
					BlockPos possibleNextPos = brancherPos.relative(possibleDir);
					if (possibleNextPos.distManhattan(pos) > brancherMaxDistance) continue;
					if (brancherDir != null && possibleDir == brancherDir.getOpposite()) continue;
					if (!level.isLoaded(possibleNextPos)) continue;

					BlockState targetState = level.getBlockState(possibleNextPos);
					if (!targetState.is(goldFoam) && !targetState.is(MIDAS_CAN_CONVERT)) {
						continue;
					}

					int weight = 1;
					if (step > 0 && targetState.is(goldFoam)) {
						weight += 4;
					}
					if (possibleDir == brancherDir) {
						weight *= 2;
					}

					for (int w = 0; w < weight; w++) {
						turnOptions.add(possibleDir);
					}
				}

				if (turnOptions.isEmpty()) break;

				brancherDir = turnOptions.get(random.nextInt(turnOptions.size()));
				brancherPos = brancherPos.relative(brancherDir);

				BlockState blockAtNewPos = level.getBlockState(brancherPos);
				if (!blockAtNewPos.is(goldFoam)) {
					level.setBlockAndUpdate(brancherPos, goldFoam.defaultBlockState());
					brancherNewBlocks++;
					if (brancherNewBlocks >= brancherMaxNewBlocks) break;
				}
			}
		}
	}
}
