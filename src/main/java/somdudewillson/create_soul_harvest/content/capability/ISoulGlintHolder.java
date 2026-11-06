package somdudewillson.create_soul_harvest.content.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureManager;
import somdudewillson.create_soul_harvest.content.item.LootTokenItem;
import somdudewillson.create_soul_harvest.registry.ModCapabilities;

import java.util.Optional;

public interface ISoulGlintHolder {

	boolean hasSoulGlint();

	@Nullable
	ResourceLocation getSoulGlintEntityId();

	default float getSoulGlintIntensity() {
		return 1.0f;
	}

	static Optional<ISoulGlintHolder> get(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return Optional.empty();
		}

		//? neoforge {
		ISoulGlintHolder holder = stack.getCapability(ModCapabilities.SOUL_GLINT_HOLDER);
		if (holder != null) {
			return Optional.of(holder);
		}
		//?} forge {
		/*var opt = stack.getCapability(ModCapabilities.SOUL_GLINT_HOLDER);
		if (opt.isPresent()) {
			return opt.resolve();
		}
		*///?}

		return Optional.empty();
	}

	static boolean hasGlint(ItemStack stack) {
		return get(stack).map(ISoulGlintHolder::hasSoulGlint).orElse(false);
	}

	@Nullable
	static ResourceLocation getEntityId(ItemStack stack) {
		return get(stack).map(ISoulGlintHolder::getSoulGlintEntityId).orElse(null);
	}

	static float getIntensity(ItemStack stack) {
		return get(stack).map(ISoulGlintHolder::getSoulGlintIntensity).orElse(0f);
	}

	static boolean hasValidSoulGlint(ItemStack stack) {
		if (!hasGlint(stack)) {
			return false;
		}
		if (SoulHarvestMod.xplat().isClient()) {
			ResourceLocation entityId = getEntityId(stack);
			return entityId != null && ClientGlintCheck.isTextureLoaded(entityId);
		}
		return false;
	}

	class ClientGlintCheck {
		static boolean isTextureLoaded(ResourceLocation entityId) {
			return MobPatternTextureManager.getTextureLocation(entityId) != null;
		}
	}
}
