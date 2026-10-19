package somdudewillson.create_soul_harvest.content.spell.spells;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.effect.ContagionMobEffect;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;

public class ContagionCarrierSpell implements GlyphSpell {

	public static final int CONTAGION_DURATION = 20 * 180; // 3 minutes
	public static final int VACCINATION_DURATION = 20 * 195; // 3 minutes 15 seconds (safety margin)
	public static final int CONTAGION_AMPLIFIER = 1;

	public ContagionCarrierSpell() {}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (!level.isClientSide()) {
			// Inoculate and infect the caster
			//? >= 1.21 {
			caster.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION, CONTAGION_DURATION, CONTAGION_AMPLIFIER));
			caster.addEffect(new MobEffectInstance(ModMobEffects.VACCINATION, VACCINATION_DURATION, 0));
			//?} else {
			/*caster.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION.get(), CONTAGION_DURATION, CONTAGION_AMPLIFIER));
			caster.addEffect(new MobEffectInstance(ModMobEffects.VACCINATION.get(), VACCINATION_DURATION, 0));
			*///?}

			// Burst an immediate contagion infection cloud around the caster
			ContagionMobEffect.createInfectionCloud(
					level,
					caster.position(),
					new Vec3(5.5, 3.5, 5.5),
					CONTAGION_DURATION,
					CONTAGION_AMPLIFIER,
					caster
			);
		}
	}
}
