package somdudewillson.create_soul_harvest.content.spell.planetoid.decorator;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;

public class FloraDecorator implements SurfaceDecorator {

	//? >= 1.21 {
	private static final Block GRASS_BLOCK_VEG = Blocks.SHORT_GRASS;
	//?} else {
	/*private static final Block GRASS_BLOCK_VEG = Blocks.GRASS;
	*///?}

	private static final Block[] FLOWERS = new Block[] {
			Blocks.POPPY,
			Blocks.DANDELION,
			Blocks.CORNFLOWER,
			Blocks.OXEYE_DAISY,
			Blocks.ALLIUM,
			Blocks.AZURE_BLUET,
			Blocks.FERN
	};

	private final float chance;

	public FloraDecorator() {
		this(0.5f);
	}

	public FloraDecorator(float chance) {
		this.chance = chance;
	}

	@Override
	public float getChance() {
		return chance;
	}

	@Override
	public boolean canDecorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState) {
		if (!surfaceState.is(BlockTags.DIRT) && !surfaceState.is(Blocks.GRASS_BLOCK)) {
			return false;
		}
		BlockPos above = surfacePos.above();
		BlockState aboveState = generation.getBlockState(above);
		return (aboveState == null || aboveState.isAir() || aboveState.canBeReplaced());
	}

	@Override
	public void decorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState, RandomSource random) {
		BlockPos above = surfacePos.above();
		Block toPlace;
		if (random.nextFloat() < 0.65f) {
			toPlace = GRASS_BLOCK_VEG;
		} else {
			toPlace = FLOWERS[random.nextInt(FLOWERS.length)];
		}
		generation.setBlockState(above, toPlace.defaultBlockState());
	}
}
