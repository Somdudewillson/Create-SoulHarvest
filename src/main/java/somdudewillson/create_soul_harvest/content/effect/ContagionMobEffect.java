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
import net.minecraft.world.phys.AABB;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;

public class ContagionMobEffect extends MobEffect {

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

	private void tickContagion(LivingEntity entity, int amplifier) {
		// Spawn particles
		if (entity.level() instanceof ServerLevel serverLevel) {
			//? >= 1.21 {
			serverLevel.sendParticles(
					ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 2f / 255f, 56f / 255f, 14f / 255f),
					entity.getX(), entity.getY(), entity.getZ(),
					5, 1.5, 1.5, 1.5, 0.05
			);
			//?} else {
			/*serverLevel.sendParticles(
					new DustParticleOptions(new org.joml.Vector3f(2f / 255f, 56f / 255f, 14f / 255f), 1.0f),
					entity.getX(), entity.getY(), entity.getZ(),
					5, 1.5, 1.5, 1.5, 0.05
			);
			*///?}
		}

		if (entity.tickCount % 20 != 0) {
			return;
		}

		// Spread to nearby living entities in 3x3x3 box
		AABB spreadBox = AABB.ofSize(entity.position(), 3, 3, 3);
		for (LivingEntity nearby : entity.level().getEntitiesOfClass(LivingEntity.class, spreadBox)) {
			if (nearby == entity) continue;
			//? >= 1.21 {
			MobEffectInstance existing = nearby.getEffect(ModMobEffects.CONTAGION);
			if (existing == null || existing.getAmplifier() < amplifier) {
				nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION, 20 * 30, amplifier), entity);
			}
			//?} else {
			/*MobEffectInstance existing = nearby.getEffect(ModMobEffects.CONTAGION.get());
			if (existing == null || existing.getAmplifier() < amplifier) {
				nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION.get(), 20 * 30, amplifier), entity);
			}
			*///?}
		}

		// Deal wither damage
		entity.hurt(entity.damageSources().wither(), 2.5f * (amplifier + 1));
	}
}
