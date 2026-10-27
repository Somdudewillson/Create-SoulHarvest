package somdudewillson.create_soul_harvest.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import somdudewillson.create_soul_harvest.content.chunk.AbsoluteIceBiomeHelper;

@Mixin(BiomeManager.class)
public class BiomeManagerMixin {

	@Shadow
	@Final
	private BiomeManager.NoiseBiomeSource noiseBiomeSource;

	@Inject(method = "getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;", at = @At("HEAD"), cancellable = true)
	private void create_soul_harvest$getAbsoluteIceBiome(BlockPos pos, CallbackInfoReturnable<Holder<Biome>> cir) {
		if (this.noiseBiomeSource instanceof Level level) {
			Holder<Biome> iceBiome = AbsoluteIceBiomeHelper.getOverriddenBiome(level, pos);
			if (iceBiome != null) {
				cir.setReturnValue(iceBiome);
			}
		}
	}

	@Inject(method = "getNoiseBiomeAtQuart", at = @At("HEAD"), cancellable = true)
	private void create_soul_harvest$getAbsoluteIceNoiseBiome(int quartX, int quartY, int quartZ, CallbackInfoReturnable<Holder<Biome>> cir) {
		if (this.noiseBiomeSource instanceof Level level) {
			BlockPos pos = new BlockPos(QuartPos.toBlock(quartX), QuartPos.toBlock(quartY), QuartPos.toBlock(quartZ));
			Holder<Biome> iceBiome = AbsoluteIceBiomeHelper.getOverriddenBiome(level, pos);
			if (iceBiome != null) {
				cir.setReturnValue(iceBiome);
			}
		}
	}
}
