package somdudewillson.create_soul_harvest.content.spell.planetoid;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Wraps the planetoid placement map during precomputation.
 * Automatically tracks the active layer and ensures out-of-bounds placements no-op.
 */
@Getter
@Setter
public class PartialPlanetoidGeneration {

	private final Map<BlockPos, PlannedPlanetoidBlockPlacement> placements;
	private final Vec3 origin;
	private final double maxRadius;
	private final double maxRadiusSqr;
	private PlanetoidLayerDefinition currentLayer;

	public PartialPlanetoidGeneration(Vec3 origin, double maxRadius, Map<BlockPos, PlannedPlanetoidBlockPlacement> placements) {
		this.origin = origin;
		this.maxRadius = maxRadius;
		this.maxRadiusSqr = maxRadius * maxRadius;
		this.placements = placements;
		initializeBoundingVolume();
	}

	private void initializeBoundingVolume() {
		BlockPos center = BlockPos.containing(origin.x, origin.y, origin.z);
		int maxR = (int) Math.ceil(maxRadius);
		BlockState air = Blocks.AIR.defaultBlockState();
		for (int x = -maxR; x <= maxR; x++) {
			for (int y = -maxR; y <= maxR; y++) {
				for (int z = -maxR; z <= maxR; z++) {
					if (x * x + y * y + z * z <= maxRadiusSqr) {
						BlockPos p = center.offset(x, y, z);
						placements.put(p, new PlannedPlanetoidBlockPlacement(air, null));
					}
				}
			}
		}
	}

	public boolean isWithinBounds(BlockPos pos) {
		return placements.containsKey(pos);
	}

	public BlockState getBlockState(BlockPos pos) {
		PlannedPlanetoidBlockPlacement placement = placements.get(pos);
		return placement != null ? placement.blockState() : null;
	}

	public PlannedPlanetoidBlockPlacement getPlacement(BlockPos pos) {
		return placements.get(pos);
	}

	/**
	 * Sets the block state at the specified position.
	 * Automatically associates the placement with the currently working layer.
	 * Placements outside the bounding volume are no-ops.
	 */
	public void setBlockState(BlockPos pos, BlockState state) {
		if (!isWithinBounds(pos)) {
			return;
		}

		placements.put(pos, new PlannedPlanetoidBlockPlacement(state, currentLayer));
	}

	/**
	 * Sets the block state at the specified position only if the position currently contains air.
	 * Placements outside the bounding volume or onto existing non-air blocks are no-ops.
	 *
	 * @return true if the block state was placed, false otherwise
	 */
	public boolean setBlockStateIfAir(BlockPos pos, BlockState state) {
		if (!isWithinBounds(pos)) {
			return false;
		}

		BlockState current = getBlockState(pos);
		if (current != null && !current.isAir()) {
			return false;
		}

		placements.put(pos, new PlannedPlanetoidBlockPlacement(state, currentLayer));
		return true;
	}

	public boolean isAir(BlockPos pos) {
		BlockState current = getBlockState(pos);
		return current == null || current.isAir();
	}
}
