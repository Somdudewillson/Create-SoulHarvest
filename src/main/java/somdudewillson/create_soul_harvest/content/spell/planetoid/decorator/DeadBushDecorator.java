package somdudewillson.create_soul_harvest.content.spell.planetoid.decorator;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;

@Getter
public class DeadBushDecorator implements SurfaceDecorator {

	private final float chance;

	public DeadBushDecorator() {
		this(0.12f);
	}

	public DeadBushDecorator(float chance) {
		this.chance = chance;
	}

	@Override
	public boolean canDecorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState) {
		if (!surfaceState.is(BlockTags.SAND) 
				&& !surfaceState.is(Blocks.SAND) 
				&& !surfaceState.is(Blocks.RED_SAND) 
				&& !surfaceState.is(BlockTags.DIRT) 
				&& !surfaceState.is(Blocks.GRASS_BLOCK)) {
			return false;
		}
		
		BlockPos above = surfacePos.above();
		BlockState aboveState = generation.getBlockState(above);
		return (aboveState == null || aboveState.isAir() || aboveState.canBeReplaced());
	}

	@Override
	public void decorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState, RandomSource random) {
		BlockPos above = surfacePos.above();
		generation.setBlockState(above, Blocks.DEAD_BUSH.defaultBlockState());
	}
}
