package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
*///?}

import somdudewillson.create_soul_harvest.registry.ModMobEffects;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.ArrayList;
import java.util.List;

public class SpellGameEvents {

	//? forge {
	/*@SuppressWarnings("removal")
	*///?}
	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event) {
		Entity entity = event.getEntity();
		CompoundTag data = entity.getPersistentData();
		if (data.contains("soul_harvest_data")) {
			CompoundTag shData = data.getCompound("soul_harvest_data");
			if (shData.getBoolean("is_contagion_blast")) {
				Level level = entity.level();
				if (level instanceof ServerLevel serverLevel) {
					double cloudSize = 5.0;
					// Particles
					//? >= 1.21 {
					serverLevel.sendParticles(
							net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 2f / 255f, 56f / 255f, 14f / 255f),
							entity.getX(), entity.getY(), entity.getZ(), 500, cloudSize / 2, cloudSize / 4, cloudSize / 2, 0.1);
					//?} else {
					/*serverLevel.sendParticles(
							new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(2f / 255f, 56f / 255f, 14f / 255f), 1.0f),
							entity.getX(), entity.getY(), entity.getZ(), 500, cloudSize / 2, cloudSize / 4, cloudSize / 2, 0.1);
					*///?}

					AABB aabb = AABB.ofSize(entity.position(), cloudSize * 2 + 1, cloudSize + 1, cloudSize * 2 + 1);
					for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
						//? >= 1.21 {
						MobEffectInstance inst = nearby.getEffect(ModMobEffects.CONTAGION);
						if (inst == null || inst.getAmplifier() <= 0) {
							nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION, 20 * 30, 0), null);
						}
						//?} else {
						/*MobEffectInstance inst = nearby.getEffect(ModMobEffects.CONTAGION.get());
						if (inst == null || inst.getAmplifier() <= 0) {
							nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION.get(), 20 * 30, 0), null);
						}
						*///?}
					}
				}

				event.setCanceled(true);
				entity.discard();
			}
		}
	}

	//? neoforge {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		tickSoulHarvestEffector(event.getEntity());
	}
	//?} forge {
	/*@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingTickEvent event) {
		tickSoulHarvestEffector(event.getEntity());
	}
	*///?}

	private static void tickSoulHarvestEffector(Entity entity) {
		CompoundTag data = entity.getPersistentData();
		if (!data.contains("soul_harvest_data")) return;
		CompoundTag shData = data.getCompound("soul_harvest_data");

		Level level = entity.level();
		if (!(level instanceof ServerLevel serverLevel)) return;

		// Spell 1: Lightning Effector
		if (shData.contains("lightning_call_ticks")) {
			int ticks = shData.getInt("lightning_call_ticks");
			if (serverLevel.random.nextFloat() < 0.2f) {
				BlockPos center = entity.blockPosition();
				BlockPos strikePos = center.offset(
						serverLevel.random.nextInt(31) - 15,
						0,
						serverLevel.random.nextInt(31) - 15
				);
				strikePos = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, strikePos);
				EntityType.LIGHTNING_BOLT.spawn(serverLevel, strikePos, MobSpawnType.TRIGGERED);
			}

			BlockPos surfacePos = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, entity.blockPosition());
			serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, surfacePos.getX(), surfacePos.getY() + 35, surfacePos.getZ(), 50, 15, 1.5, 15, 0);

			ticks--;
			if (ticks <= 0) {
				entity.discard();
			} else {
				shData.putInt("lightning_call_ticks", ticks);
			}
		}

		// Spell 8: Terrain Flatten Effector
		if (shData.contains("flatten_step")) {
			final int effectSize = 15;
			final int maxVerticalSearch = 15;
			final int downshiftMaxSize = 10;

			int flattenStep = shData.getInt("flatten_step");
			BlockPos entityPos = entity.blockPosition();
			serverLevel.sendParticles(ParticleTypes.WITCH, entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5, 20, effectSize / 2.0, 1, effectSize / 2.0, 0);

			List<BlockPos> flattenPositions = generateSpiral(entityPos.below(), effectSize);
			if (flattenStep >= flattenPositions.size()) {
				entity.discard();
				return;
			}

			BlockPos currentPos = flattenPositions.get(flattenStep);
			BlockState currentState = serverLevel.getBlockState(currentPos);
			serverLevel.sendParticles(ParticleTypes.POOF, currentPos.getX() + 0.5, currentPos.getY() + 1.5, currentPos.getZ() + 0.5, 1, 0.5, 1, 0.5, 0);

			if (isFlattenReplaceable(currentState)) {
				// Raising height
				boolean findingGround = true;
				BlockPos raisePos = currentPos;
				BlockState raiseState = null;
				BlockPos searchPos = raisePos;
				for (int i = 0; i < maxVerticalSearch; i++) {
					searchPos = searchPos.below();
					BlockState searchState = serverLevel.getBlockState(searchPos);

					if (findingGround && !isFlattenReplaceable(searchState)) {
						findingGround = false;
						raisePos = searchPos.above();
					}
					if (!isFlattenReplaceable(searchState) && searchState.is(ModTags.Blocks.FLATTEN_CAN_GENERATE)) {
						raiseState = searchState;
					}
					if (!findingGround && raiseState != null) break;
				}

				BlockState raiseReplacedState = serverLevel.getBlockState(raisePos);
				if (!findingGround && raiseState != null && (raiseReplacedState.is(ModTags.Blocks.FLATTEN_CAN_DISPLACE) || isFlattenReplaceable(raiseReplacedState))) {
					serverLevel.setBlockAndUpdate(raisePos, raiseState);
				} else {
					flattenStep++;
				}
			} else {
				// Reducing height
				BlockPos abovePos = currentPos.above();
				BlockState aboveState = serverLevel.getBlockState(abovePos);
				if (isFlattenReplaceable(aboveState)) {
					flattenStep++;
				} else {
					BlockPos pullPos = abovePos;
					BlockState pullState = aboveState;
					boolean foundTop = false;

					for (int i = 0; i < maxVerticalSearch; i++) {
						BlockPos nextAbove = pullPos.above();
						BlockState nextAboveState = serverLevel.getBlockState(nextAbove);
						if (isFlattenReplaceable(nextAboveState)) {
							foundTop = true;
							break;
						} else {
							pullPos = nextAbove;
							pullState = nextAboveState;
						}
					}

					if (!foundTop || !pullState.is(ModTags.Blocks.FLATTEN_CAN_DISPLACE)) {
						flattenStep++;
					} else {
						List<BlockState> pullStates = new ArrayList<>();
						pullStates.add(pullState);
						BlockPos crushedPos = pullPos.below();
						for (int i = 0; i < downshiftMaxSize; i++) {
							BlockState crushedState = serverLevel.getBlockState(crushedPos);
							if (isFlattenReplaceable(crushedState) || crushedState.is(ModTags.Blocks.FLATTEN_CAN_CRUSH)) {
								for (int s = pullStates.size() - 1; s >= 0; s--) {
									serverLevel.setBlockAndUpdate(crushedPos, pullStates.get(s));
									crushedPos = crushedPos.above();
								}
								serverLevel.setBlockAndUpdate(pullPos, Blocks.AIR.defaultBlockState());
								break;
							} else {
								crushedPos = crushedPos.below();
								pullStates.add(crushedState);
							}

							if (i >= downshiftMaxSize - 1) {
								if (isFlattenReplaceable(pullState) || pullState.is(ModTags.Blocks.FLATTEN_CAN_CRUSH)) {
									serverLevel.setBlockAndUpdate(pullPos, Blocks.AIR.defaultBlockState());
								} else {
									flattenStep++;
								}
							}
						}
					}
				}
			}

			shData.putInt("flatten_step", flattenStep);
		}
	}

	private static boolean isFlattenReplaceable(BlockState state) {
		return state.canBeReplaced() || state.is(ModTags.Blocks.CONSIDER_REPLACEABLE);
	}

	private static List<BlockPos> generateSpiral(BlockPos center, int radius) {
		List<BlockPos> positions = new ArrayList<>();
		int x = 0, z = 0;
		int dx = 0, dz = -1;
		int max = (2 * radius + 1) * (2 * radius + 1);
		for (int i = 0; i < max; i++) {
			if ((-radius <= x) && (x <= radius) && (-radius <= z) && (z <= radius)) {
				positions.add(center.offset(x, 0, z));
			}
			if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
				int t = dx;
				dx = -dz;
				dz = t;
			}
			x += dx;
			z += dz;
		}
		return positions;
	}
}
