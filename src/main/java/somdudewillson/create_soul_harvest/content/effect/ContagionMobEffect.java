package somdudewillson.create_soul_harvest.content.effect;

//? >= 1.21 {
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
//?} else {
/*import net.minecraft.core.particles.DustParticleOptions;
*///?}
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.registry.ModDamageSources;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class ContagionMobEffect extends MobEffect {
	public static final int AMPLIFIER_THRESHOLD = 1;

	public ContagionMobEffect() {
		// Color: 0x02380E (dark green)
		//? >= 1.21 {
		super(MobEffectCategory.HARMFUL, 0x02380E, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 2f / 255f, 56f / 255f, 14f / 255f));
		//?} else {
		/*super(MobEffectCategory.HARMFUL, 0x02380E);
		*///?}
	}

	//? >= 1.21 {
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		tickContagion(entity, amplifier);
		return true;
	}
	//?} else {
	/*@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		tickContagion(entity, amplifier);
	}
	*///?}

	/**
	 * Checks an entity's 'infection priority':
	 * - 0: Non-infectable (dead, spectator/creative player, immune tag, or already infected with stronger effect / equal effect without at least 5s duration deficit)
	 * - 1: Equal strength effect but duration is lesser by at least 5 seconds (100 ticks)
	 * - 2: Infected with a weaker effect
	 * - 3: All other infectable entities (uninfected)
	 */
	public static int getInfectionPriority(@Nullable LivingEntity target, int currentAmplifier, int currentDuration) {
		if (target == null || !target.isAlive() || target.isRemoved()) {
			return 0;
		}

		if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
			return 0;
		}

		if (target.getType().is(ModTags.EntityTypes.CONTAGION_IMMUNE) || VaccinationMobEffect.hasVaccination(target)) {
			return 0;
		}

		MobEffectInstance existing = getContagionEffect(target);

		if (existing == null) {
			return 3;
		}

		if (existing.getAmplifier() > currentAmplifier) {
			return 0;
		}

		if (existing.getAmplifier() < currentAmplifier) {
			return 2;
		}

		// Equal amplifier: check if duration is lesser by at least 5 seconds
		if (currentDuration - existing.getDuration() >= 20 * 5) {
			return 1;
		}

		return 0;
	}

	/**
	 * Creates an area infection cloud at the specified location and size, spreading contagion to valid entities.
	 */
	public static void createInfectionCloud(Level level, Vec3 center, Vec3 size, int duration, int amplifier, @Nullable LivingEntity source) {
		if (level instanceof ServerLevel serverLevel) {
			int particleCount = (int) Math.max(5, (size.x * size.y * size.z) / 2);
			//? >= 1.21 {
			serverLevel.sendParticles(
					ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 2f / 255f, 56f / 255f, 14f / 255f),
					center.x, center.y, center.z,
					particleCount, size.x / 2.0, size.y / 4.0, size.z / 2.0, 0.05
			);
			//?} else {
			/*serverLevel.sendParticles(
					new DustParticleOptions(new org.joml.Vector3f(2f / 255f, 56f / 255f, 14f / 255f), 1.0f),
					center.x, center.y, center.z,
					particleCount, size.x / 2.0, size.y / 4.0, size.z / 2.0, 0.05
			);
			*///?}
		}

		AABB spreadBox = AABB.ofSize(center, size.x, size.y, size.z);
		for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, spreadBox)) {
			if (nearby == source) continue;
			int priority = getInfectionPriority(nearby, amplifier, duration);
			if (priority > 0) {
				//? >= 1.21 {
				nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION, duration, amplifier), source);
				//?} else {
				/*nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION.get(), duration, amplifier), source);
				*///?}
			}
		}
	}
	@Nullable
	protected static MobEffectInstance getContagionEffect(LivingEntity entity) {
		//? >= 1.21 {
		return entity.getEffect(ModMobEffects.CONTAGION);
		//?} else {
		/*return entity.getEffect(ModMobEffects.CONTAGION.get());
		*///?}
	}

	public static boolean hasValidContagion(LivingEntity entity) {
		MobEffectInstance effect = getContagionEffect(entity);
		return effect != null && effect.getAmplifier() >= AMPLIFIER_THRESHOLD;
	}

	public static boolean isValidContagion(MobEffectInstance effect) {
		return effect != null && effect.getAmplifier() >= AMPLIFIER_THRESHOLD;
	}

	private void tickContagion(LivingEntity entity, int amplifier) {
		// If entity is immune, purge effect immediately
		if (entity.getType().is(ModTags.EntityTypes.CONTAGION_IMMUNE)) {
			//? >= 1.21 {
			entity.removeEffect(ModMobEffects.CONTAGION);
			//?} else {
			/*entity.removeEffect(ModMobEffects.CONTAGION.get());
			*///?}
			return;
		}

		// Above threshold, attach AI goal to pathfinder mobs on server
		if (amplifier >= AMPLIFIER_THRESHOLD && entity instanceof PathfinderMob mob && !mob.level().isClientSide()) {
			if (mob.goalSelector.getAvailableGoals().stream().noneMatch(wg -> wg.getGoal() instanceof ContagionSpreadGoal)) {
				mob.goalSelector.addGoal(2, new ContagionSpreadGoal(mob, 1.2D, 0.5D));
			}
		}

		if (entity.tickCount % 20 != 0) {
			return;
		}
		MobEffectInstance existing = getContagionEffect(entity);
		int duration = existing.getDuration();

		// Pulse area infection cloud in 3x3x3 box
		createInfectionCloud(
				entity.level(), 
				entity.position(), 
				new Vec3(1,1,1).scale(3 + Math.min(5, amplifier*0.5)), 
				Math.max(duration, 25 * 20), amplifier, 
				entity);

		// Deal contagion damage
		if (!VaccinationMobEffect.hasVaccination(entity) && (amplifier < AMPLIFIER_THRESHOLD || duration < 20 * 20)) {
			entity.hurt(ModDamageSources.contagion(entity.level()), 2.5f + (entity.getMaxHealth() * 0.015f * amplifier));
		}
	}
}
