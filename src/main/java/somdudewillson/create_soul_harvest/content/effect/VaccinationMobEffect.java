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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;

public class VaccinationMobEffect extends MobEffect {

	public VaccinationMobEffect() {
		//? >= 1.21 {
		super(MobEffectCategory.BENEFICIAL, 0x2DE082, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 45f / 255f, 224f / 255f, 130f / 255f));
		//?} else {
		/*super(MobEffectCategory.BENEFICIAL, 0x2DE082);
		*///?}
	}

	//? >= 1.21 {
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		tickVaccination(entity, amplifier);
		return true;
	}
	//?} else {
	/*@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		tickVaccination(entity, amplifier);
	}
	*///?}

	public static boolean hasVaccination(LivingEntity entity) {
		//? >= 1.21 {
		return entity.hasEffect(ModMobEffects.VACCINATION);
		//?} else {
		/*return entity.hasEffect(ModMobEffects.VACCINATION.get());
		*///?}
	}

	private void tickVaccination(LivingEntity entity, int amplifier) {
		if (entity.level().isClientSide()) {
			return;
		}

		if (entity.hasEffect(MobEffects.POISON)) {
			entity.removeEffect(MobEffects.POISON);
		}
		if (entity.hasEffect(MobEffects.WITHER)) {
			entity.removeEffect(MobEffects.WITHER);
		}

		// Cleansing particles
		if (entity.tickCount % 20 == 0 && entity.level() instanceof ServerLevel serverLevel) {
			//? >= 1.21 {
			serverLevel.sendParticles(
					ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 45f / 255f, 224f / 255f, 130f / 255f),
					entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
					2, 0.25, 0.25, 0.25, 0.02
			);
			//?} else {
			/*serverLevel.sendParticles(
					new DustParticleOptions(new org.joml.Vector3f(45f / 255f, 224f / 255f, 130f / 255f), 1.0f),
					entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
					2, 0.25, 0.25, 0.25, 0.02
			);
			*///?}
		}
	}
}
