package somdudewillson.create_soul_harvest.content.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import somdudewillson.redsparklib.common.utils.ExtNbtUtils;
import somdudewillson.redsparklib.common.utils.OptNbtUtils;

//? >= 1.21 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

public class ItemStackSoulHolder implements ISoulHolder {

	public static final String TAG_HELD_SOUL = "soul_harvest:held_soul";
	public static final String TAG_SOUL_PORTION = "soul_harvest:soul_portion";
	public static final String TAG_ENHANCED = "soul_harvest:enhanced";

	private final ItemStack stack;

	public ItemStackSoulHolder(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public ResourceLocation getHeldSoul() {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			return OptNbtUtils.get(customData.copyTag(), TAG_HELD_SOUL)
					.map(tag -> ResourceLocation.tryParse(tag.getAsString()))
					.orElse(null);
		}
		return null;
		//?} else {
		/*CompoundTag tag = stack.getTag();
		if (tag != null) {
			return OptNbtUtils.get(tag, TAG_HELD_SOUL)
					.map(t -> ResourceLocation.tryParse(t.getAsString()))
					.orElse(null);
		}
		return null;
		*///?}
	}

	@Override
	public void setHeldSoul(ResourceLocation entityId) {
		//? >= 1.21 {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (entityId == null) {
			tag.remove(TAG_HELD_SOUL);
		} else {
			OptNbtUtils.put(tag, TAG_HELD_SOUL, ExtNbtUtils.serializeResourceLocation(entityId));
		}
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		if (entityId == null) {
			tag.remove(TAG_HELD_SOUL);
		} else {
			OptNbtUtils.put(tag, TAG_HELD_SOUL, ExtNbtUtils.serializeResourceLocation(entityId));
		}
		*///?}
	}

	@Override
	public int getSoulPortion() {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			return customData.copyTag().getInt(TAG_SOUL_PORTION);
		}
		return 0;
		//?} else {
		/*CompoundTag tag = stack.getTag();
		return tag != null ? tag.getInt(TAG_SOUL_PORTION) : 0;
		*///?}
	}

	@Override
	public void setSoulPortion(int portion) {
		//? >= 1.21 {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.putInt(TAG_SOUL_PORTION, portion);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		tag.putInt(TAG_SOUL_PORTION, portion);
		*///?}
	}

	@Override
	public boolean isEnhanced() {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			return customData.copyTag().getBoolean(TAG_ENHANCED);
		}
		return false;
		//?} else {
		/*CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean(TAG_ENHANCED);
		*///?}
	}

	@Override
	public void setEnhanced(boolean enhanced) {
		//? >= 1.21 {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (enhanced) {
			tag.putBoolean(TAG_ENHANCED, true);
		} else {
			tag.remove(TAG_ENHANCED);
		}
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		if (enhanced) {
			tag.putBoolean(TAG_ENHANCED, true);
		} else {
			tag.remove(TAG_ENHANCED);
		}
		*///?}
	}
}
