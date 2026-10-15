package somdudewillson.create_soul_harvest.content.spell.spells;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;

public class ContagionBlastSpell implements GlyphSpell {

	public ContagionBlastSpell() {}

	//? forge {
	/*@SuppressWarnings("unused")
	*///?}
	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			Vec3 look = caster.getLookAngle();
			Vec3 spawnPos = caster.getEyePosition().add(look);
			//? >= 1.21 {
			SmallFireball fireball = new SmallFireball(serverLevel, caster, look);
			//?} else {
			/*SmallFireball fireball = new SmallFireball(level, caster, look.x, look.y, look.z);
			*///?}
			fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
			fireball.setInvisible(true);
			fireball.setInvulnerable(true);

			CompoundTag nbt = new CompoundTag();
			nbt.putBoolean("is_contagion_blast", true);
			fireball.getPersistentData().put("soul_harvest_data", nbt);

			level.addFreshEntity(fireball);
		}
	}
}
