package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
*///?}

import somdudewillson.create_soul_harvest.registry.ModMobEffects;

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

	@SubscribeEvent
	public static void onLivingFall(LivingFallEvent event) {
		//? >= 1.21 {
		if (event.getEntity().hasEffect(ModMobEffects.AGILE_FALLING)) {
			event.setCanceled(true);
			event.setDistance(0);
		}
		//?} else {
		/*if (event.getEntity().hasEffect(ModMobEffects.AGILE_FALLING.get())) {
			event.setCanceled(true);
			event.setDistance(0);
		}
		*///?}
	}
}
