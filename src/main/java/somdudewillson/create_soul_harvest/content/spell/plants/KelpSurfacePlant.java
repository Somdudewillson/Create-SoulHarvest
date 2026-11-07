package somdudewillson.create_soul_harvest.content.spell.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class KelpSurfacePlant implements SurfacePlant {

	@Override
	public boolean isApplicable(ServerLevel level, BlockPos groundPos, BlockState groundState) {
		if (!groundState.is(Blocks.SAND) && !groundState.is(Blocks.GRAVEL) && !groundState.is(Blocks.CLAY)) {
			return false;
		}

		BlockPos plantPos = groundPos.above();
		if (!SurfacePlant.isReplaceable(level, plantPos)) {
			return false;
		}
		if (SurfacePlant.getDepthUnderwater(level, plantPos, 8) < 6) {
			return false;
		}

		return Blocks.KELP_PLANT.defaultBlockState().canSurvive(level, plantPos);
	}

	@Override
	public boolean plant(ServerLevel level, BlockPos groundPos, BlockState groundState, RandomSource random) {
		BlockPos plantPos = groundPos.above();
		int height = 3 + random.nextInt(8);
		int placed = 0;
		int waterDepth = SurfacePlant.getDepthUnderwater(level, plantPos, height+3);
		height = Math.min(height, waterDepth-1);

		for (int h = 0; h < height; h++) {
			BlockPos currentPos = plantPos.above(h);
			if (SurfacePlant.isReplaceable(level, currentPos) && Blocks.KELP_PLANT.defaultBlockState().canSurvive(level, currentPos)) {
				level.setBlock(currentPos, Blocks.KELP_PLANT.defaultBlockState(), 3);
				placed++;
			} else {
				break;
			}
		}

		if (placed > 0) {
			SoundType sound = Blocks.KELP_PLANT.defaultBlockState().getSoundType(level, plantPos, null);
			level.playSound(null, plantPos, sound.getPlaceSound(), SoundSource.BLOCKS, 0.8F, 1.0F);
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plantPos.getX() + 0.5, plantPos.getY() + 0.5, plantPos.getZ() + 0.5, 5, 0.2, 0.4, 0.2, 0.02);
			return true;
		}

		return false;
	}
}
