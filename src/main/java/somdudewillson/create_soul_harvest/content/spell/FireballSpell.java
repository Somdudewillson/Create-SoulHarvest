package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class FireballSpell {

	private FireballSpell() {}

	//? forge {
	/*@SuppressWarnings("unused")
	*///?}
	public static void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			Vec3 look = caster.getLookAngle();
			Vec3 spawnPos = caster.getEyePosition().add(look);
			//? >= 1.21 {
			LargeFireball fireball = new LargeFireball(serverLevel, caster, look, 15);
			//?} else {
			/*LargeFireball fireball = new LargeFireball(level, caster, look.x, look.y, look.z, 15);
			*///?}
			fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
			level.addFreshEntity(fireball);
		}
	}
}
