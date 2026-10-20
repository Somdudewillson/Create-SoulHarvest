package somdudewillson.create_soul_harvest.content.spell.spells;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;

@RequiredArgsConstructor
public class UpdraftSpell implements GlyphSpell {

	private final double launchHeight;
	private final Supplier<MobEffectInstance> effect;

	@Override
	public boolean cast(Level level, LivingEntity caster) {
		AABB area = AABB.ofSize(caster.position(), 5, 3, 5);
		
		for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, area)) {
			if (level instanceof ServerLevel serverLevel) {
				//? >= 1.21 {
				serverLevel.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, nearby.getX(), nearby.getY(), nearby.getZ(), 1, 0, 0, 0, 0);
				//?} else {
				/*serverLevel.sendParticles(ParticleTypes.CLOUD, nearby.getX(), nearby.getY(), nearby.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
				*///?}
			}
			nearby.setDeltaMovement(new Vec3(0, launchHeight, 0));
			nearby.hurtMarked = true;

			if (nearby instanceof OwnableEntity ownable && caster.getUUID().equals(ownable.getOwnerUUID())) {
				nearby.addEffect(effect.get());
			}
		}
		
		if (!level.isClientSide()) {
			caster.addEffect(effect.get());
		}
		return true;
	}
}
