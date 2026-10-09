package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlockEntity;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

import static somdudewillson.create_soul_harvest.registry.ModTags.Blocks.*;

public class RunicSpells {

	public static void castSpell1_Lightning(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			ArmorStand effector = EntityType.ARMOR_STAND.spawn(serverLevel, caster.blockPosition(), MobSpawnType.MOB_SUMMONED);
			if (effector != null) {
				effector.setInvisible(true);
				effector.setInvulnerable(true);
				effector.setNoGravity(true);
				CompoundTag data = new CompoundTag();
				data.putInt("lightning_call_ticks", 20 * 15);
				effector.getPersistentData().put("soul_harvest_data", data);
			}
		}
	}

	public static void castSpell2_Fireball(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			Vec3 look = caster.getLookAngle();
			Vec3 spawnPos = caster.getEyePosition().add(look);
			//? >= 1.21 {
			LargeFireball fireball = new LargeFireball(serverLevel, caster, look, 15);
			//?} else {
			/*LargeFireball fireball = new LargeFireball(level, caster, look.x, look.y, look.z, 15);
			*///?}
			fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
			level.addFreshEntity(fireball);
		}
	}

	public static void castSpell3_Bonemeal(Level level, LivingEntity caster) {
		BlockPos center = caster.blockPosition();
		for (BlockPos pos : BlockPos.withinManhattan(center, 15, 3, 15)) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof BonemealableBlock bonemealable) {
				//? >= 1.21 {
				if (!bonemealable.isValidBonemealTarget(level, pos, state)) continue;
				//?} else {
				/*if (!bonemealable.isValidBonemealTarget(level, pos, state, level.isClientSide())) continue;
				*///?}
				if (state.is(Blocks.GRASS_BLOCK) && level.random.nextFloat() < 0.25f) continue;

				if (level instanceof ServerLevel serverLevel) {
					bonemealable.performBonemeal(serverLevel, level.random, pos, state);
					if (level.random.nextFloat() < 0.5f) {
						serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2, 0.5, 0.5, 0.5, 0);
					}
				}
			}
		}
	}

	public static void castSpell4_ContagionBlast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			Vec3 look = caster.getLookAngle();
			Vec3 spawnPos = caster.getEyePosition().add(look);
			//? >= 1.21 {
			SmallFireball fireball = new SmallFireball(serverLevel, caster, look);
			//?} else {
			/*SmallFireball fireball = new SmallFireball(level, caster, look.x, look.y, look.z);
			*///?}
			fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
			fireball.setInvisible(true);
			fireball.setInvulnerable(true);

			CompoundTag nbt = new CompoundTag();
			nbt.putBoolean("is_contagion_blast", true);
			fireball.getPersistentData().put("soul_harvest_data", nbt);

			level.addFreshEntity(fireball);
		}
	}

	public static void castSpell5_Fortitude(Level level, LivingEntity caster) {
		if (!level.isClientSide()) {
			caster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 4));
			caster.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 2));
			caster.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 30, 5));
		}
	}

	public static void castSpell6_Updraft(Level level, LivingEntity caster) {
		AABB area = AABB.ofSize(caster.position(), 5, 3, 5);
		for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (level instanceof ServerLevel serverLevel) {
				//? >= 1.21 {
				serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, nearby.getX(), nearby.getY(), nearby.getZ(), 1, 0, 0, 0, 0);
				//?} else {
				/*serverLevel.sendParticles(ParticleTypes.CLOUD, nearby.getX(), nearby.getY(), nearby.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
				*///?}
			}
			nearby.setDeltaMovement(new Vec3(0, 7, 0));
			nearby.hurtMarked = true;

			if (nearby instanceof OwnableEntity ownable && caster.getUUID().equals(ownable.getOwnerUUID())) {
				nearby.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 25, 0));
			}
		}
		if (!level.isClientSide()) {
			caster.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 25, 0));
		}
	}

	public static void castSpell7_InstantShelter(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			final double bubbleRadius = 6.0;
			final double bubbleThickness = 0.9;
			BlockPos center = caster.blockPosition();
			int extent = (int) Math.ceil(bubbleRadius + bubbleThickness);

			for (BlockPos testPos : BlockPos.betweenClosed(center.offset(-extent, -extent, -extent), center.offset(extent, extent, extent))) {
				BlockState cur = serverLevel.getBlockState(testPos);
				if (!cur.canBeReplaced()) continue;

				double distFromCenter = Math.sqrt(testPos.distToCenterSqr(center.getCenter()));
				double distFromEdge = Math.abs(distFromCenter - bubbleRadius);
				if (distFromEdge <= bubbleThickness) {
					serverLevel.setBlockAndUpdate(testPos, ModBlocks.MAGIC_GLASS.get().defaultBlockState());
					BlockEntity be = serverLevel.getBlockEntity(testPos);
					if (be instanceof AnimaGlassBlockEntity animaBe) {
						animaBe.setOwnerId(caster.getUUID());
					}
				}
			}
		}
	}

	public static void castSpell8_TerrainFlatten(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			BlockPos spawnPos = caster.blockPosition();
			while (serverLevel.getBlockState(spawnPos.below()).canBeReplaced() || serverLevel.getBlockState(spawnPos.below()).is(CONSIDER_REPLACEABLE)) {
				spawnPos = spawnPos.below();
				if (serverLevel.isOutsideBuildHeight(spawnPos) || serverLevel.isOutsideBuildHeight(spawnPos.above())) {
					return;
				}
			}

			ArmorStand effector = EntityType.ARMOR_STAND.spawn(serverLevel, spawnPos, MobSpawnType.MOB_SUMMONED);
			if (effector != null) {
				effector.setInvisible(true);
				effector.setInvulnerable(true);
				effector.setNoGravity(true);
				CompoundTag data = new CompoundTag();
				data.putInt("flatten_step", 0);
				effector.getPersistentData().put("soul_harvest_data", data);
			}
		}
	}
}
