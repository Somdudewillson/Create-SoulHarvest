package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlockEntity;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class InstantWallSpell implements GlyphSpell {

	public static final int LIFESPAN = 20 * 8; // 8 seconds (shorter lifespan than shelter)
	public static final double ANGLE_INCREMENT = 45.0; // degrees to snap the wall's orientation to

	public InstantWallSpell() {}

	@SuppressWarnings("unused")
	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			final double wallRadius = 3.5;
			final double wallThickness = 0.9;

			Vec3 eyePos = caster.getEyePosition();
			Vec3 look = caster.getLookAngle().normalize();

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
				normal = look;
			}

			HitResult hit = caster.pick(8.0, 0.0f, false);
			Vec3 centerVec;
			if (hit.getType() == HitResult.Type.BLOCK) {
				double hitDist = hit.getLocation().distanceTo(eyePos);
				double distance = Math.max(2.5, Math.min(hitDist, 8.0));
				centerVec = eyePos.add(look.scale(distance)).add(0,1,0);
			} else {
				centerVec = eyePos.add(look.scale(4.5));
			}

			BlockPos centerPos = BlockPos.containing(centerVec.x, centerVec.y, centerVec.z);
			int extent = (int) Math.ceil(wallRadius + wallThickness);

			AABB casterBox = caster.getBoundingBox().inflate(0.1);

			for (BlockPos testPos : BlockPos.betweenClosed(centerPos.offset(-extent, -extent, -extent), centerPos.offset(extent, extent, extent))) {
				if (casterBox.intersects(new AABB(testPos))) continue;

				BlockState cur = serverLevel.getBlockState(testPos);
				if (!cur.canBeReplaced()) continue;

				Vec3 blockCenter = testPos.getCenter();
				Vec3 diff = blockCenter.subtract(centerVec);
				double distAlongNormal = diff.dot(normal);
				if (Math.abs(distAlongNormal) > wallThickness) continue;

				double radialDistSqr = diff.lengthSqr() - distAlongNormal * distAlongNormal;
				if (radialDistSqr <= wallRadius * wallRadius) {
					serverLevel.setBlockAndUpdate(testPos, ModBlocks.MAGIC_GLASS.get().defaultBlockState());
					BlockEntity be = serverLevel.getBlockEntity(testPos);
					if (be instanceof AnimaGlassBlockEntity animaBe) {
						animaBe.setOwnerId(caster.getUUID());
						animaBe.setMaxLifespan(LIFESPAN);
						animaBe.setCurrentLifespan(0);
					}
				}
			}
		}
	}
}
