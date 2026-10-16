package somdudewillson.create_soul_harvest.content.spell.planetoid.builder;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PlanetoidLayerBuilder;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PlanetoidLayerDefinition;
import somdudewillson.create_soul_harvest.content.spell.planetoid.decorator.SurfaceDecorator;

@Getter
public class SurfaceLayerBuilder implements PlanetoidLayerBuilder {

	private final BlockState baseSurfaceState;
	private final BlockState topperSurfaceState;
	private final double thickness;
	private final List<SurfaceDecorator> decorators;

	public SurfaceLayerBuilder(BlockState baseSurfaceState, double thickness, List<SurfaceDecorator> decorators) {
		this(baseSurfaceState, baseSurfaceState, thickness, decorators);
	}

	public SurfaceLayerBuilder(BlockState baseSurfaceState, BlockState topperSurfaceState, double thickness, List<SurfaceDecorator> decorators) {
		this.baseSurfaceState = baseSurfaceState;
		this.topperSurfaceState = topperSurfaceState;
		this.thickness = thickness;
		this.decorators = new ArrayList<>(decorators);
	}

	@Override
	public void populate(ServerLevel level, PlanetoidLayerDefinition layerDef, Vec3 origin, PartialPlanetoidGeneration generation, RandomSource random) {
		Vec3 center = layerDef.getCenter(origin);
		BlockPos centerPos = BlockPos.containing(center.x, center.y, center.z);
		int r = (int) Math.ceil(layerDef.radius());
		
		List<BlockPos> surfacePositions = new ArrayList<>();

		for (int x = -r; x <= r; x++) {
			for (int z = -r; z <= r; z++) {
				for (int y = r; y >= -r; y--) {
					BlockPos pos = centerPos.offset(x, y, z);
					if (!generation.isWithinBounds(pos)) continue;
					if (!generation.isAir(pos)) continue;
					if (generation.isAir(pos.below())) continue;
					
					boolean surfaceSuccesful = false;
					BlockPos lastSuccessfulPos = null;
					int surfacePlaced = 0;
					do {
						surfacePlaced++;
						if (generation.setBlockStateIfAir(pos, baseSurfaceState)) {
							surfaceSuccesful = true;
							lastSuccessfulPos = pos;
							pos = pos.above();
						}
					} while (surfaceSuccesful && surfacePlaced < thickness);
					
					if (lastSuccessfulPos != null) {
						generation.setBlockState(lastSuccessfulPos, topperSurfaceState);
						surfacePositions.add(lastSuccessfulPos);
					}
					break;
				}
			}
		}
		
		for (BlockPos surfacePos : surfacePositions) {
			placeDecoration(generation, surfacePos, generation.getBlockState(surfacePos), random);
		}
	}

	protected void placeDecoration(PartialPlanetoidGeneration generation, BlockPos pos, BlockState state, RandomSource random) {
		BlockPos above = pos.above();
		BlockState aboveState = generation.getBlockState(above);
		if (aboveState != null && !aboveState.isAir() && !aboveState.canBeReplaced()) {
			return;
		}

		for (SurfaceDecorator decorator : decorators) {
			if (decorator.canDecorate(generation, pos, state) && random.nextFloat() < decorator.getChance()) {
				decorator.decorate(generation, pos, state, random);
				break;
			}
		}
	}
}
