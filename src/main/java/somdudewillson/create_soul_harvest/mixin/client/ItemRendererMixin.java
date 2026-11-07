package somdudewillson.create_soul_harvest.mixin.client;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import somdudewillson.create_soul_harvest.client.texture.dynamic.ItemCompositeRegistry;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Inject(method = "hasAnimatedTexture", at = @At("HEAD"), cancellable = true)
	private static void create_soul_harvest$hasAnimatedTexture(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (ItemCompositeRegistry.isTargetItem(stack)) {
			cir.setReturnValue(true);
		}
	}
}
