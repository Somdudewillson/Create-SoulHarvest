package somdudewillson.create_soul_harvest.content.spell.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class CactusSurfacePlant implements SurfacePlant {

	@Override
	public boolean isApplicable(ServerLevel level, BlockPos groundPos, BlockState groundState) {
		if (!groundState.is(BlockTags.SAND)) {
			return false;
		}

		Holder<Biome> biome = level.getBiome(groundPos);
		boolean isDesertOrArid = biome.is(Biomes.DESERT)
				|| biome.is(Biomes.BADLANDS)
				|| biome.is(Biomes.ERODED_BADLANDS)
				|| biome.is(Biomes.WOODED_BADLANDS)
				|| (!biome.value().hasPrecipitation() && biome.value().getBaseTemperature() >= 1.5F);

		if (!isDesertOrArid) {
			return false;
		}

		BlockPos plantPos = groundPos.above();
		if (!SurfacePlant.isReplaceable(level, plantPos)) {
			return false;
		}

		return Blocks.CACTUS.defaultBlockState().canSurvive(level, plantPos);
	}

	@Override
	public boolean plant(ServerLevel level, BlockPos groundPos, BlockState groundState, RandomSource random) {
		BlockPos plantPos = groundPos.above();
		int height = 1 + random.nextInt(4);
		int placed = 0;

		for (int h = 0; h < height; h++) {
			BlockPos currentPos = plantPos.above(h);
			if (SurfacePlant.isReplaceable(level, currentPos) && Blocks.CACTUS.defaultBlockState().canSurvive(level, currentPos)) {
				level.setBlock(currentPos, Blocks.CACTUS.defaultBlockState(), 3);
				placed++;
			} else {
				break;
			}
		}

		if (placed > 0) {
			SoundType sound = Blocks.CACTUS.defaultBlockState().getSoundType(level, plantPos, null);
			level.playSound(null, plantPos, sound.getPlaceSound(), SoundSource.BLOCKS, 0.8F, 1.0F);
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plantPos.getX() + 0.5, plantPos.getY() + 0.5, plantPos.getZ() + 0.5, 5, 0.2, 0.4, 0.2, 0.02);
			return true;
		}

		return false;
	}
}
