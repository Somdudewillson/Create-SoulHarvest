package somdudewillson.create_soul_harvest.content.spell.spells;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.entity.FireboltEntity;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;

public class FireboltSpell implements GlyphSpell {

	public FireboltSpell() {}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (!level.isClientSide()) {
			Vec3 look = caster.getLookAngle();
			Vec3 spawnPos = caster.getEyePosition().add(look.scale(0.5));
			FireboltEntity firebolt = new FireboltEntity(level, caster, look);
			firebolt.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
			firebolt.setDeltaMovement(look.scale(1.5));
			level.addFreshEntity(firebolt);
		}
	}
}
