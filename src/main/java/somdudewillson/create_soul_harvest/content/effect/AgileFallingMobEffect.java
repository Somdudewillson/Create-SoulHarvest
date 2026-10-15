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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AgileFallingMobEffect extends MobEffect {

	public AgileFallingMobEffect() {
		//? >= 1.21 {
		super(MobEffectCategory.BENEFICIAL, 0x7BE4E8, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.48f, 0.89f, 0.91f));
		//?} else {
		/*super(MobEffectCategory.BENEFICIAL, 0x7BE4E8);
		*///?}
	}

	//? >= 1.21 {
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		tickAgileFalling(entity, amplifier);
		return true;
	}
	//?} else {
	/*@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		tickAgileFalling(entity, amplifier);
	}
	*///?}

	private void tickAgileFalling(LivingEntity entity, int amplifier) {
		if (entity.onGround() || entity.isInWater() || entity.isInLava() || entity.isPassenger()) {
			return;
		}

		// Prevent fall damage while falling with agile falling
		entity.resetFallDistance();

		Vec3 motion = entity.getDeltaMovement();

		// Slow falling descent: cap terminal downward velocity to -0.15
		double newY = motion.y;
		if (!entity.isNoGravity() && motion.y < 0.0) {
			newY = Math.max(motion.y, -0.15);
		}

		// Horizontal mobility boost while airborne
		double newX = motion.x;
		double newZ = motion.z;

		float forward = entity.zza;
		float strafe = entity.xxa;

		if (Math.abs(forward) > 0.01f || Math.abs(strafe) > 0.01f) {
			float yaw = entity.getYRot() * ((float) Math.PI / 180F);
			float sin = (float) Math.sin(yaw);
			float cos = (float) Math.cos(yaw);

			// Forward direction is (-sin, cos); Strafe direction is (cos, sin)
			double dx = (strafe * cos - forward * sin);
			double dz = (forward * cos + strafe * sin);

			double len = Math.sqrt(dx * dx + dz * dz);
			if (len > 1.0E-5) {
				dx /= len;
				dz /= len;
			}

			double accel = 0.04 * (amplifier + 1);
			newX += dx * accel;
			newZ += dz * accel;

			// Cap maximum air gliding speed
			double currentHorizSpeed = Math.sqrt(newX * newX + newZ * newZ);
			double maxSpeed = 0.55 + amplifier * 0.15;
			if (currentHorizSpeed > maxSpeed) {
				double scale = maxSpeed / currentHorizSpeed;
				newX *= scale;
				newZ *= scale;
			}
		} else {
			// Reduce drag
			double horizSpeed = Math.sqrt(newX * newX + newZ * newZ);
			if (horizSpeed > 0.05 && horizSpeed < 0.55) {
				newX *= 1.04;
				newZ *= 1.04;
			}
		}

		entity.setDeltaMovement(new Vec3(newX, newY, newZ));

		// Visual breeze particles occasionally on server
		if (entity.tickCount % 5 == 0 && entity.level() instanceof ServerLevel serverLevel) {
			//? >= 1.21 {
			serverLevel.sendParticles(
					ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.48f, 0.89f, 0.91f),
					entity.getX(), entity.getY() + 0.2, entity.getZ(),
					2, 0.2, 0.1, 0.2, 0.02
			);
			//?} else {
			/*serverLevel.sendParticles(
					new DustParticleOptions(new org.joml.Vector3f(0.48f, 0.89f, 0.91f), 1.0f),
					entity.getX(), entity.getY() + 0.2, entity.getZ(),
					2, 0.2, 0.1, 0.2, 0.02
			);
			*///?}
		}
	}
}
