package somdudewillson.create_soul_harvest.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import somdudewillson.create_soul_harvest.registry.ModTags;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

	@Shadow
	private int spawnDelay;

	@Shadow
	protected abstract void delay(Level level, BlockPos pos);

	@Inject(method = "serverTick", at = @At("HEAD"))
	private void create_soul_harvest$inhibitSpawner(ServerLevel serverLevel, BlockPos pos, CallbackInfo ci) {
		if (this.spawnDelay == 0) {
			for (Direction direction : Direction.values()) {
				if (serverLevel.getBlockState(pos.relative(direction)).is(ModTags.Blocks.C_INHIBITS_SPAWNERS)) {
					this.delay(serverLevel, pos);
					Vec3 particlePos = pos.getCenter();
					serverLevel.sendParticles(
							ParticleTypes.REVERSE_PORTAL,
							particlePos.x, particlePos.y, particlePos.z,
							15,
							0.5, 0.5, 0.5,
							0.02
						);
					break;
				}
			}
		}
	}
}
