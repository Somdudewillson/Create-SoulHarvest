package somdudewillson.create_soul_harvest.content.spell.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class SugarcaneSurfacePlant implements SurfacePlant {

	@Override
	public boolean isApplicable(ServerLevel level, BlockPos groundPos, BlockState groundState) {
		// Applies to sand next to water
		if (!groundState.is(BlockTags.SAND)) {
			return false;
		}

		BlockPos plantPos = groundPos.above();
		if (!SurfacePlant.isReplaceable(level, plantPos)) {
			return false;
		}

		return Blocks.SUGAR_CANE.defaultBlockState().canSurvive(level, plantPos);
	}

	@Override
	public boolean plant(ServerLevel level, BlockPos groundPos, BlockState groundState, RandomSource random) {
		BlockPos plantPos = groundPos.above();
		int height = 1 + random.nextInt(3);
		int placed = 0;

		for (int h = 0; h < height; h++) {
			BlockPos currentPos = plantPos.above(h);
			if (SurfacePlant.isReplaceable(level, currentPos) && Blocks.SUGAR_CANE.defaultBlockState().canSurvive(level, currentPos)) {
				level.setBlock(currentPos, Blocks.SUGAR_CANE.defaultBlockState(), 3);
				placed++;
			} else {
				break;
			}
		}

		if (placed > 0) {
			SoundType sound = Blocks.SUGAR_CANE.defaultBlockState().getSoundType(level, plantPos, null);
			level.playSound(null, plantPos, sound.getPlaceSound(), SoundSource.BLOCKS, 0.8F, 1.0F);
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plantPos.getX() + 0.5, plantPos.getY() + 0.5, plantPos.getZ() + 0.5, 5, 0.2, 0.4, 0.2, 0.02);
			return true;
		}

		return false;
	}
}
