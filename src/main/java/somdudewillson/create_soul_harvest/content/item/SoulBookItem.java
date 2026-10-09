package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

//? >= 1.21 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

import java.util.List;
import java.util.Optional;

public class SoulBookItem extends Item {

	private final int tier;

	public SoulBookItem(int tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	public int getTier() {
		return tier;
	}

	public static String getHeldSoul(ItemStack stack) {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			return customData.copyTag().getString("soul_harvest:held_soul");
		}
		return "";
		//?} else {
		/*CompoundTag tag = stack.getTag();
		return tag != null ? tag.getString("soul_harvest:held_soul") : "";
		*///?}
	}

	public static int getSoulPortion(ItemStack stack) {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			return customData.copyTag().getInt("soul_harvest:soul_portion");
		}
		return 0;
		//?} else {
		/*CompoundTag tag = stack.getTag();
		return tag != null ? tag.getInt("soul_harvest:soul_portion") : 0;
		*///?}
	}

	public static void setSoulData(ItemStack stack, String heldSoul, int portion) {
		//? >= 1.21 {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.putString("soul_harvest:held_soul", heldSoul);
		tag.putInt("soul_harvest:soul_portion", portion);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		if (portion >= 100) {
			stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
		}
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		tag.putString("soul_harvest:held_soul", heldSoul);
		tag.putInt("soul_harvest:soul_portion", portion);
		*///?}
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return getSoulPortion(stack) >= 100 || super.isFoil(stack);
	}

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		addSoulBookTooltip(stack, tooltipComponents);
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		addSoulBookTooltip(stack, tooltipComponents);
	}
	*///?}

	private void addSoulBookTooltip(ItemStack stack, List<Component> tooltipComponents) {
		String heldSoul = getHeldSoul(stack);
		int soulPortion = getSoulPortion(stack);

		if (heldSoul.isEmpty() || soulPortion <= 0) {
			tooltipComponents.add(Component.literal("Hold in offhand to capture the souls of your enemies!").withStyle(ChatFormatting.GRAY));
		} else {
			Optional<EntityType<?>> optionalType = EntityType.byString(heldSoul);
			MutableComponent nameComp;
			if (optionalType.isPresent()) {
				nameComp = Component.translatable(optionalType.get().getDescriptionId()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
			} else {
				nameComp = Component.literal(heldSoul).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
			}
			nameComp.append(Component.literal(" Soul").withStyle(ChatFormatting.GRAY));

			if (soulPortion < 100) {
				nameComp.append(Component.literal(" (" + soulPortion + "%)").withStyle(ChatFormatting.DARK_GRAY));
			} else {
				nameComp.append(Component.literal(" (Complete)").withStyle(ChatFormatting.DARK_GRAY));
			}
			tooltipComponents.add(nameComp);
		}
	}
}
