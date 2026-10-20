package somdudewillson.create_soul_harvest.content.spell.spells;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;

@RequiredArgsConstructor
public class FortitudeSpell implements GlyphSpell {
	protected final boolean enhanced;

	@Override
	public boolean cast(Level level, LivingEntity caster) {
		if (!level.isClientSide()) {
			if (enhanced) {
				//? >= 1.21 {
				caster.addEffect(new MobEffectInstance(ModMobEffects.STONESKIN, 20 * 60, 1));
				//?} else {
				/*caster.addEffect(new MobEffectInstance(ModMobEffects.STONESKIN.get(), 20 * 60, 1));
				*///?}
				caster.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 60, 1));
			} else {
				caster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 4));
				caster.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 2));
				caster.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 30, 5));
			}
		}
		return true;
	}
}
