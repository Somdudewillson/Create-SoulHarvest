package somdudewillson.create_soul_harvest.content.spell.planetoid.decorator;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;

@Getter
public class CactusDecorator implements SurfaceDecorator {

	private final float chance;

	public CactusDecorator() {
		this(0.08f);
	}

	public CactusDecorator(float chance) {
		this.chance = chance;
	}

	@Override
	public boolean canDecorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState) {
		if (!surfaceState.is(BlockTags.SAND) && !surfaceState.is(Blocks.SAND) && !surfaceState.is(Blocks.RED_SAND)) {
			return false;
		}
		
		BlockPos above = surfacePos.above();
		BlockState aboveState = generation.getBlockState(above);
		return (aboveState == null || aboveState.isAir() || aboveState.canBeReplaced())
				&& canCactusSurvive(generation, above);
	}

	@Override
	public void decorate(PartialPlanetoidGeneration generation, BlockPos surfacePos, BlockState surfaceState, RandomSource random) {
		int height = 1 + random.nextInt(3);
		BlockPos current = surfacePos.above();
		for (int i = 0; i < height; i++) {
			BlockState currentAbove = generation.getBlockState(current);
			if ((currentAbove == null || currentAbove.isAir() || currentAbove.canBeReplaced())
					&& canCactusSurvive(generation, current)) {
				generation.setBlockState(current, Blocks.CACTUS.defaultBlockState());
				current = current.above();
			} else {
				break;
			}
		}
	}
	
	protected boolean canCactusSurvive(PartialPlanetoidGeneration generation, BlockPos testPos) {
		return generation.isAir(testPos.north())
				&& generation.isAir(testPos.east())
				&& generation.isAir(testPos.south())
				&& generation.isAir(testPos.west());
	}
}
