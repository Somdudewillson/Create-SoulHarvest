package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
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

public class PolyclayItem extends Item {

	public PolyclayItem(Properties properties) {
		super(properties);
	}

	public static String getHeldSoul(ItemStack stack) {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			return customData.copyTag().getString("soul_harvest:held_soul");
		}
		return "";
		//?} else {
		/*net.minecraft.nbt.CompoundTag tag = stack.getTag();
		return tag != null ? tag.getString("soul_harvest:held_soul") : "";
		*///?}
	}

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		addPolyclayTooltip(stack, tooltipComponents);
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		addPolyclayTooltip(stack, tooltipComponents);
	}
	*///?}

	private void addPolyclayTooltip(ItemStack stack, List<Component> tooltipComponents) {
		String heldSoul = getHeldSoul(stack);
		if (!heldSoul.isEmpty()) {
			Optional<EntityType<?>> optionalType = EntityType.byString(heldSoul);
			MutableComponent nameComp;
			if (optionalType.isPresent()) {
				nameComp = Component.translatable(optionalType.get().getDescriptionId()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
			} else {
				nameComp = Component.literal(heldSoul).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
			}
			nameComp.append(Component.literal(" Tuned").withStyle(ChatFormatting.GRAY));
			tooltipComponents.add(nameComp);
		}
	}
}
