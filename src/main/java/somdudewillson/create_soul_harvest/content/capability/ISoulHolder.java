package somdudewillson.create_soul_harvest.content.capability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import somdudewillson.create_soul_harvest.registry.ModCapabilities;

import java.util.Optional;

public interface ISoulHolder {

	ResourceLocation getHeldSoul();

	void setHeldSoul(ResourceLocation entityId);

	int getSoulPortion();

	void setSoulPortion(int portion);

	default boolean isComplete() {
		return getSoulPortion() >= 100;
	}

	default boolean hasSoul() {
		ResourceLocation soul = getHeldSoul();
		return soul != null;
	}
	
	default Optional<EntityType<?>> tryGetHeldSoulEntityType() {
		ResourceLocation soul = getHeldSoul();
		if (soul == null) {
			return Optional.empty();
		}
		return BuiltInRegistries.ENTITY_TYPE.getOptional(soul);
	}

	static Optional<ISoulHolder> get(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return Optional.empty();
		}

		//? neoforge {
		ISoulHolder holder = stack.getCapability(ModCapabilities.SOUL_HOLDER);
		if (holder != null) {
			return Optional.of(holder);
		}
		//?} forge {
		/*var opt = stack.getCapability(ModCapabilities.SOUL_HOLDER);
		if (opt.isPresent()) {
			return opt.resolve();
		}
		*///?}

		// Direct fallback wrapper if capability hasn't been attached yet
		return Optional.of(new ItemStackSoulHolder(stack));
	}

	public static ResourceLocation getHeldSoul(ItemStack stack) {
		return ISoulHolder.get(stack)
				.map(ISoulHolder::getHeldSoul)
				.orElse(null);
	}

	public static int getSoulPortion(ItemStack stack) {
		return ISoulHolder.get(stack).map(ISoulHolder::getSoulPortion).orElse(0);
	}

	public static Optional<EntityType<?>> tryGetHeldSoulEntityType(ItemStack stack) {
		return ISoulHolder.get(stack)
				.flatMap(ISoulHolder::tryGetHeldSoulEntityType);
	}
}
