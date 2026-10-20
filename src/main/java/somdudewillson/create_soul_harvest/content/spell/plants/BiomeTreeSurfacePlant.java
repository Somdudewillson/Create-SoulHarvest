package somdudewillson.create_soul_harvest.content.spell.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BiomeTreeSurfacePlant extends AbstractTreeSurfacePlant {

	@Override
	protected BlockState pickSapling(ServerLevel level, BlockPos groundPos, RandomSource random) {
		Holder<Biome> biome = level.getBiome(groundPos);

		if (biome.is(BiomeTags.IS_TAIGA)) {
			return Blocks.SPRUCE_SAPLING.defaultBlockState();
		}
		if (biome.is(BiomeTags.IS_SAVANNA)) {
			return Blocks.ACACIA_SAPLING.defaultBlockState();
		}
		if (biome.is(BiomeTags.IS_JUNGLE)) {
			return Blocks.JUNGLE_SAPLING.defaultBlockState();
		}
		if (biome.is(Biomes.CHERRY_GROVE)) {
			return Blocks.CHERRY_SAPLING.defaultBlockState();
		}
		if (biome.is(Biomes.BIRCH_FOREST) || biome.is(Biomes.OLD_GROWTH_BIRCH_FOREST)) {
			return Blocks.BIRCH_SAPLING.defaultBlockState();
		}
		if (biome.is(Biomes.DARK_FOREST)) {
			return random.nextBoolean() ? Blocks.DARK_OAK_SAPLING.defaultBlockState() : Blocks.OAK_SAPLING.defaultBlockState();
		}
		if (biome.is(Biomes.MANGROVE_SWAMP)) {
			return Blocks.MANGROVE_PROPAGULE.defaultBlockState();
		}

		// Plains, regular forests, and default biomes
		return random.nextFloat() < 0.2F
				? Blocks.BIRCH_SAPLING.defaultBlockState()
				: Blocks.OAK_SAPLING.defaultBlockState();
	}
}
