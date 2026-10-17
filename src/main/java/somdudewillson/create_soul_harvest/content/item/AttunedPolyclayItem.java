package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;

//? >= 1.21 {

//?} else {
/*import net.minecraft.world.level.Level;
*///?}

import java.util.List;
import java.util.Optional;

public class AttunedPolyclayItem extends Item {

	public AttunedPolyclayItem(Properties properties) {
		super(properties);
	}

	public static ItemStack createAttunedPolyclay(Item item, ResourceLocation entityId) {
		ItemStack stack = new ItemStack(item);
		ISoulHolder.get(stack).ifPresent(holder -> {
			holder.setHeldSoul(entityId);
			holder.setSoulPortion(100);
		});
		return stack;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
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
		ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
		if (heldSoul != null) {
			Optional<EntityType<?>> optionalType = ISoulHolder.tryGetHeldSoulEntityType(stack);
			MutableComponent nameComp;
			if (optionalType.isPresent()) {
				nameComp = Component.translatable(optionalType.get().getDescriptionId()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
			} else {
				nameComp = Component.literal(heldSoul.toString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
			}
			nameComp.append(Component.literal(" Tuned").withStyle(ChatFormatting.GRAY));
			tooltipComponents.add(nameComp);
		}
	}
}
