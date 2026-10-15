package somdudewillson.create_soul_harvest.content.spell.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.AnimaGlassStructureSpell;

import java.util.ArrayList;
import java.util.List;

public class InstantShelterSpell extends AnimaGlassStructureSpell {

	public static final int LIFESPAN = 20 * 30; // 30 seconds
	public static final double BUBBLE_RADIUS = 6.0;
	public static final double BUBBLE_THICKNESS = 0.9;

	public InstantShelterSpell(ResourceLocation id) {
		super(id);
	}

	@Override
	protected int getLifespan() {
		return LIFESPAN;
	}

	@Override
	protected int getPlaceInterval() {
		return 1;
	}

	@Override
	protected int getBlocksPerInterval() {
		return 24;
	}

	@Override
	protected Vec3 getOrigin(ServerLevel level, LivingEntity caster) {
		// Sort from the bottom of the dome upwards to give an upward rising dome effect
		return caster.position().subtract(0, BUBBLE_RADIUS, 0);
	}

	@Override
	protected List<BlockPlacement> supplyBlockPlacements(ServerLevel level, LivingEntity caster, Vec3 origin) {
		BlockPos center = caster.blockPosition();
		int extent = (int) Math.ceil(BUBBLE_RADIUS + BUBBLE_THICKNESS);
		List<BlockPlacement> placements = new ArrayList<>();

		for (BlockPos testPos : BlockPos.betweenClosed(center.offset(-extent, -extent, -extent), center.offset(extent, extent, extent))) {
			double distFromCenter = Math.sqrt(testPos.distToCenterSqr(center.getCenter()));
			double distFromEdge = Math.abs(distFromCenter - BUBBLE_RADIUS);
			if (distFromEdge <= BUBBLE_THICKNESS) {
				placements.add(new BlockPlacement(testPos.immutable()));
			}
		}

		return placements;
	}
}
