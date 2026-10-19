package somdudewillson.create_soul_harvest.content.spell.spells;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.entity.ContagionChargeEntity;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;

public class ContagionBlastSpell implements GlyphSpell {

	public ContagionBlastSpell() {}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (!level.isClientSide()) {
			Vec3 look = caster.getLookAngle();
			Vec3 spawnPos = caster.getEyePosition().add(look);
			ContagionChargeEntity charge = new ContagionChargeEntity(level, caster, look);
			charge.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
			charge.addDeltaMovement(look);
			level.addFreshEntity(charge);
		}
	}
}

