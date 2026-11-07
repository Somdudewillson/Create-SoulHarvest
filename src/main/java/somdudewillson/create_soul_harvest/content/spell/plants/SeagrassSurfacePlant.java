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

public class SeagrassSurfacePlant implements SurfacePlant {

	@Override
	public boolean isApplicable(ServerLevel level, BlockPos groundPos, BlockState groundState) {
		if (!groundState.is(Blocks.SAND) && !groundState.is(Blocks.GRAVEL) && !groundState.is(Blocks.CLAY) && !groundState.is(BlockTags.CORAL_BLOCKS)) {
			return false;
		}

		BlockPos plantPos = groundPos.above();
		if (!SurfacePlant.isReplaceable(level, plantPos)) {
			return false;
		}
		if (SurfacePlant.getDepthUnderwater(level, plantPos, 6) < 4) {
			return false;
		}

		return Blocks.SEAGRASS.defaultBlockState().canSurvive(level, plantPos);
	}

	@Override
	public boolean plant(ServerLevel level, BlockPos groundPos, BlockState groundState, RandomSource random) {
		BlockPos plantPos = groundPos.above();
		if (random.nextFloat() < 0.334) {
			level.setBlock(plantPos, Blocks.TALL_SEAGRASS.defaultBlockState(), 3);
		} else {
			level.setBlock(plantPos, Blocks.SEAGRASS.defaultBlockState(), 3);
		}
		
		SoundType sound = Blocks.SEAGRASS.defaultBlockState().getSoundType(level, plantPos, null);
		level.playSound(null, plantPos, sound.getPlaceSound(), SoundSource.BLOCKS, 0.8F, 1.0F);
		level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plantPos.getX() + 0.5, plantPos.getY() + 0.5, plantPos.getZ() + 0.5, 5, 0.2, 0.4, 0.2, 0.02);
		return true;
	}
}
