package somdudewillson.create_soul_harvest.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.particle.CapturedSoulParticleOption;

public class CapturedSoulParticle extends TextureSheetParticle {
	private static final int DELAY_TIME = 6;

	private final SpriteSet sprites;
	private final int targetEntityId;
	private final double swirlOffset;

	protected CapturedSoulParticle(
			ClientLevel level,
			double x,
			double y,
			double z,
			double xSpeed,
			double ySpeed,
			double zSpeed,
			int targetEntityId,
			SpriteSet sprites
	) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		this.sprites = sprites;
		this.targetEntityId = targetEntityId;
		this.hasPhysics = false;
		this.lifetime = 20 * 10;
		this.swirlOffset = this.random.nextDouble() * Math.PI * 2.0;
		this.quadSize = 0.3F + this.random.nextFloat() * 0.15F;

		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;

		// Emissive glowing cyan-blue soul hue
		this.rCol = 0.45F + this.random.nextFloat() * 0.15F;
		this.gCol = 0.85F + this.random.nextFloat() * 0.15F;
		this.bCol = 1.0F;
		this.alpha = 0.9F;

		this.setSpriteFromAge(sprites);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
			return;
		}

		this.setSpriteFromAge(this.sprites);
		
		if (this.age < DELAY_TIME) {
			// Delay to let particles spread from initial positions
		} else if (this.targetEntityId != -1) {
			Entity target = this.level.getEntity(this.targetEntityId);
			if (target instanceof Player player && player.isAlive()) {
				Vec3 targetPos = getOffhandPos(player);
				double dx = targetPos.x - this.x;
				double dy = targetPos.y - this.y;
				double dz = targetPos.z - this.z;
				double distSq = dx * dx + dy * dy + dz * dz;

				// Consumed into the book upon reaching offhand
				if (distSq < 0.12) {
					this.remove();
					return;
				}

				double dist = Math.sqrt(distSq);
				double dirX = dx / dist;
				double dirY = dy / dist;
				double dirZ = dz / dist;

				int adjAge = Math.max(0, this.age - DELAY_TIME);
				float pullFactor = Math.min(1.0F, (float) adjAge / 16.0F);
				double targetSpeed = Math.min(0.9, 0.1 + (adjAge * 0.02) + (dist > 5.0 ? 0.15 : 0.0));

				double swirlSpeed = (1.0 - pullFactor) * 0.06;
				double perpX = -dirZ * Math.sin(this.age * 0.35 + this.swirlOffset);
				double perpZ = dirX * Math.sin(this.age * 0.35 + this.swirlOffset);

				double targetVx = dirX * targetSpeed + perpX * swirlSpeed;
				double targetVy = dirY * targetSpeed;
				double targetVz = dirZ * targetSpeed + perpZ * swirlSpeed;

				double lerpStrength = 0.16 + pullFactor * 0.16;
				this.xd = Mth.lerp(lerpStrength, this.xd, targetVx);
				this.yd = Mth.lerp(lerpStrength, this.yd, targetVy);
				this.zd = Mth.lerp(lerpStrength, this.zd, targetVz);
			} else {
				// Target no longer exists or dead, drift upward
				this.yd += 0.01;
				this.xd *= 0.95;
				this.zd *= 0.95;
			}
		}

		this.move(this.xd, this.yd, this.zd);

		float remaining = (float) (this.lifetime - this.age) / (float) this.lifetime;
		if (remaining < 0.2F) {
			this.alpha = remaining * 5.0F * 0.9F;
		}
	}

	@Override
	public float getQuadSize(float partialTick) {
		float progress = ((float) this.age + partialTick) / (float) this.lifetime;
		float scale = 1.0F;
		if (progress < 0.15F) {
			scale = progress / 0.15F;
		} else if (progress > 0.8F) {
			scale = (1.0F - progress) / 0.2F;
		}
		return this.quadSize * Math.max(0.0F, scale);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 15728880; // Full emissive brightness
	}

	public static Vec3 getOffhandPos(Player player) {
		boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson()
				&& player == Minecraft.getInstance().player;
		int armSign = player.getMainArm() == HumanoidArm.RIGHT ? -1 : 1;

		if (isFirstPerson) {
			Vec3 eyePos = player.getEyePosition();
			Vec3 look = player.getLookAngle();
			Vec3 up = player.getUpVector(1.0F);
			Vec3 right = look.cross(up).normalize();

			return eyePos.add(look.scale(0.35))
					.add(right.scale(armSign * 0.35))
					.subtract(0, 0.25, 0);
		} else {
			float bodyYaw = player.yBodyRot * ((float) Math.PI / 180F);
			double sin = Math.sin(bodyYaw);
			double cos = Math.cos(bodyYaw);

			double side = armSign * 0.35;
			double forward = 0.15;
			double dx = -cos * side - sin * forward;
			double dz = -sin * side + cos * forward;
			double dy = player.getEyeHeight() * 0.6;

			return player.position().add(dx, dy, dz);
		}
	}

	public static class Provider implements ParticleProvider<CapturedSoulParticleOption> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(
				CapturedSoulParticleOption type,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xSpeed,
				double ySpeed,
				double zSpeed
		) {
			return new CapturedSoulParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type.getTargetEntityId(), this.sprites);
		}
	}
}
