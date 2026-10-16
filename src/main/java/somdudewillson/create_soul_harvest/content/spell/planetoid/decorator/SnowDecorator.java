package somdudewillson.create_soul_harvest.content.spell.planetoid.decorator;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;

@Getter
public class SnowDecorator implements SurfaceDecorator {
	private final int minHeight;
	private final int maxHeight;

	public SnowDecorator() {
		this(1, 3);
	}

	public SnowDecorator(int minHeight, int maxHeight) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	@Override
	public float getChance() {
		return 1;
	}

	@Override
	public boolean canDecorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState) {
		if (surfaceState.isAir()) return false;
		
		BlockPos above = surfacePos.above();
		BlockState aboveState = generation.getBlockState(above);
		return (aboveState == null || aboveState.isAir() || aboveState.canBeReplaced());
	}

	@Override
	public void decorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState, RandomSource random) {
		BlockPos above = surfacePos.above();
		
		generation.setBlockState(
					above, 
					Blocks.SNOW.defaultBlockState()
						.setValue(SnowLayerBlock.LAYERS, random.nextIntBetweenInclusive(minHeight, maxHeight))
				);
	}
}
