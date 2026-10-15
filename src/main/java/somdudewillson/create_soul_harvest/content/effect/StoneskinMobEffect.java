package somdudewillson.create_soul_harvest.content.effect;

import java.time.Duration;
import java.util.UUID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;
//? >= 1.21 {
import net.minecraft.core.particles.ColorParticleOption;
//?} else {
/*import net.minecraft.core.particles.DustParticleOptions;
*///?}

public class StoneskinMobEffect extends MobEffect {

	private static final Cache<UUID, Long> RECENT_HITS = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofSeconds(5))
			.maximumSize(1000)
			.build();

	public StoneskinMobEffect() {
		//? >= 1.21 {
		super(MobEffectCategory.BENEFICIAL, 0x8D8477, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.55f, 0.52f, 0.47f));
		//?} else {
		/*super(MobEffectCategory.BENEFICIAL, 0x8D8477);
		*///?}
	}

	//? >= 1.21 {
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		tickStoneskin(entity, amplifier);
		return true;
	}
	//?} else {
	/*@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		tickStoneskin(entity, amplifier);
	}
	*///?}

	private void tickStoneskin(LivingEntity entity, int amplifier) {
		if (entity.level().isClientSide() || !entity.onGround()) {
			return;
		}

		// Weak regeneration when on the ground
		int interval = Math.max(20, 100 / (amplifier + 1));
		if (entity.tickCount % interval == 0 && entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(1.0F);

			if (entity.level() instanceof ServerLevel serverLevel) {
				//? >= 1.21 {
				serverLevel.sendParticles(
						ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.55f, 0.52f, 0.47f),
						entity.getX(), entity.getY() + 0.1, entity.getZ(),
						3, 0.2, 0.1, 0.2, 0.01
				);
				//?} else {
				/*serverLevel.sendParticles(
						new DustParticleOptions(new org.joml.Vector3f(0.55f, 0.52f, 0.47f), 1.0f),
						entity.getX(), entity.getY() + 0.1, entity.getZ(),
						3, 0.2, 0.1, 0.2, 0.01
				);
				*///?}
			}
		}
	}

	/**
	 * Modifies incoming damage for an entity with the Stoneskin effect.
	 */
	public static float modifyDamage(LivingEntity entity, DamageSource source, float damage) {
		if (damage <= 0) {
			return damage;
		}

		//? >= 1.21 {
		MobEffectInstance effect = entity.getEffect(ModMobEffects.STONESKIN);
		//?} else {
		/*MobEffectInstance effect = entity.getEffect(ModMobEffects.STONESKIN.get());
		*///?}

		if (effect == null) {
			return damage;
		}
		
		long currentTime = entity.level().getGameTime();
		Long lastHitTime = RECENT_HITS.getIfPresent(entity.getUUID());
		if (lastHitTime == null) lastHitTime = 0l;
		long timeSinceLastHit = currentTime - lastHitTime;
		if (timeSinceLastHit < 10 && entity.getRandom().nextFloat() * 10 > timeSinceLastHit) {// hurt-time-bypassing damage is sometimes negated
			return 0;
		}

		// Reduces incoming damage like resistance (bypassed if damage source bypasses resistance)
		if (source.is(DamageTypeTags.BYPASSES_RESISTANCE) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return damage;
		}

		int amplifier = effect.getAmplifier();

		// Base resistance
		float baseReduction = Math.min(0.70f, 0.1f * (amplifier + 1));

		// Asymptotic curve giving additional mitigation for heavy blows
		float heavyDamageLimit = Math.min(0.7f, 0.35f * (amplifier + 1));
		float damageCurve = heavyDamageLimit * (damage / (damage + 15.0f));

		// Combined reduction factor, capped to prevent absolute immunity
		float maxCap = Math.min(0.95f, 0.80f + 0.05f * amplifier);
		float totalReduction = Math.min(maxCap, baseReduction + damageCurve);

		float newDamage = damage * (1.0f - totalReduction);
		System.out.println(String.format("%.2f in; %.2f base reduction %.2f curve reduction %.2f final reduction; %.2f out", damage, baseReduction, damageCurve, totalReduction, newDamage));
		
		if (newDamage > 0) {
			RECENT_HITS.put(entity.getUUID(), currentTime);
		}
		return newDamage;
	}
}
