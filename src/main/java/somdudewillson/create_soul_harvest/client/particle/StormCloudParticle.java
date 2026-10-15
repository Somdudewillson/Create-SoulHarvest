package somdudewillson.create_soul_harvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class StormCloudParticle extends TextureSheetParticle {

	protected StormCloudParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z);
		this.scale(24.0F + this.random.nextFloat() * 5F);
		this.setSize(4F, 4F);
		this.lifetime = this.random.nextInt(50) + 100;
		this.gravity = 3.0E-6F;
		this.xd = xSpeed;
		this.yd = ySpeed + (double)(this.random.nextFloat() / 500.0F);
		this.zd = zSpeed;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
			this.xd += (double)((this.random.nextFloat() / 5000.0F) * (float)(this.random.nextBoolean() ? 1 : -1));
			this.zd += (double)((this.random.nextFloat() / 5000.0F) * (float)(this.random.nextBoolean() ? 1 : -1));
			this.yd -= (double)this.gravity;
			this.move(this.xd, this.yd, this.zd);
		} else {
			this.remove();
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			StormCloudParticle particle = new StormCloudParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}
}
