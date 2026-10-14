package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class FortitudeSpell {

	private FortitudeSpell() {}

	public static void cast(Level level, LivingEntity caster) {
		if (!level.isClientSide()) {
			caster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 4));
			caster.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 2));
			caster.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 30, 5));
		}
	}
}
