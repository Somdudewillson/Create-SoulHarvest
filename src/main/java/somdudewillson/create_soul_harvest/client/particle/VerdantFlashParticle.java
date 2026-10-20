package somdudewillson.create_soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class VerdantFlashParticle extends TextureSheetParticle {

	protected VerdantFlashParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z);
		this.hasPhysics = false;
		this.lifetime = 16 + this.random.nextInt(10);
		this.quadSize = 0.35F + this.random.nextFloat() * 0.15F;

		// Upward rise and slight horizontal drift
		this.xd = xSpeed != 0 ? xSpeed : (this.random.nextDouble() - 0.5) * 0.02;
		this.yd = ySpeed != 0 ? ySpeed : (0.035 + this.random.nextDouble() * 0.025);
		this.zd = zSpeed != 0 ? zSpeed : (this.random.nextDouble() - 0.5) * 0.02;

		// Vibrant verdant green tint
		this.rCol = 0.25F + this.random.nextFloat() * 0.1F;
		this.gCol = 0.90F + this.random.nextFloat() * 0.1F;
		this.bCol = 0.35F + this.random.nextFloat() * 0.15F;
		this.alpha = 0.9F;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			this.move(this.xd, this.yd, this.zd);
			this.yd += 0.001;
			this.xd *= 0.96;
			this.zd *= 0.96;

			float progress = (float) this.age / (float) this.lifetime;
			this.setAlpha(Math.max(0.0F, 0.9F - progress * 0.4F));
		}
	}

	@Override
	public float getQuadSize(float partialTick) {
		float progress = ((float) this.age + partialTick) / (float) this.lifetime;
		return this.quadSize * Math.max(0.0F, 1.0F - progress);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 15728880; // Full emissive brightness
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			VerdantFlashParticle particle = new VerdantFlashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}
}
