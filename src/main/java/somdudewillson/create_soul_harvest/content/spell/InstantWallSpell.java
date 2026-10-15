package somdudewillson.create_soul_harvest.content.spell;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class InstantWallSpell extends AnimaGlassStructureSpell {

	public static final ResourceLocation ID = SoulHarvestMod.id("instant_wall");
	public static final int LIFESPAN = 20 * 8; // 8 seconds
	public static final double ANGLE_INCREMENT = 45.0; // degrees to snap the wall's orientation to
	public static final double WALL_RADIUS = 3.5;
	public static final double WALL_THICKNESS = 0.9;

	public InstantWallSpell() {
	}

	@Override
	public ResourceLocation getId() {
		return ID;
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
		return 4;
	}

	@Override
	protected Vec3 getOrigin(ServerLevel level, LivingEntity caster) {
		Vec3 eyePos = caster.getEyePosition();
		Vec3 look = caster.getLookAngle().normalize();

		HitResult hit = caster.pick(8.0, 0.0f, false);
		if (hit.getType() == HitResult.Type.BLOCK) {
			double hitDist = hit.getLocation().distanceTo(eyePos);
			double distance = Math.max(2.5, Math.min(hitDist, 8.0));
			return eyePos.add(look.scale(distance)).add(0, 1, 0);
		} else {
			return eyePos.add(look.scale(4.5));
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected List<BlockPlacement> supplyBlockPlacements(ServerLevel level, LivingEntity caster, Vec3 origin) {
		Vec3 normal;
		if (ANGLE_INCREMENT > 0.0) {
			double snappedYaw = Math.round(caster.getYRot() / ANGLE_INCREMENT) * ANGLE_INCREMENT;
			double snappedPitch = Math.round(caster.getXRot() / ANGLE_INCREMENT) * ANGLE_INCREMENT;

			double yawRad = Math.toRadians(snappedYaw);
			double pitchRad = Math.toRadians(snappedPitch);

			double x = -Math.sin(yawRad) * Math.cos(pitchRad);
			double y = -Math.sin(pitchRad);
			double z = Math.cos(yawRad) * Math.cos(pitchRad);
			normal = new Vec3(x, y, z).normalize();
		} else {
			normal = caster.getLookAngle().normalize();
		}

		BlockPos centerPos = BlockPos.containing(origin.x, origin.y, origin.z);
		int extent = (int) Math.ceil(WALL_RADIUS + WALL_THICKNESS);

		AABB casterBox = caster.getBoundingBox().inflate(0.1);
		List<BlockPlacement> placements = new ArrayList<>();

		for (BlockPos testPos : BlockPos.betweenClosed(centerPos.offset(-extent, -extent, -extent), centerPos.offset(extent, extent, extent))) {
			if (casterBox.intersects(new AABB(testPos))) continue;

			Vec3 blockCenter = testPos.getCenter();
			Vec3 diff = blockCenter.subtract(origin);
			double distAlongNormal = diff.dot(normal);
			if (Math.abs(distAlongNormal) > WALL_THICKNESS) continue;

			double radialDistSqr = diff.lengthSqr() - distAlongNormal * distAlongNormal;
			if (radialDistSqr <= WALL_RADIUS * WALL_RADIUS) {
				placements.add(new BlockPlacement(testPos.immutable()));
			}
		}

		return placements;
	}
}
