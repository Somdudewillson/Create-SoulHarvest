package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

//? >= 1.21 {

//?} else {
/*import net.minecraft.world.level.Level;
*///?}

import java.util.List;

public class NecroniumWasteBottleItem extends Item {

	public NecroniumWasteBottleItem(Properties properties) {
		super(properties);
	}

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		addNecroniumWasteBottleTooltip(stack, tooltipComponents);
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		addNecroniumWasteBottleTooltip(stack, tooltipComponents);
	}
	*///?}

	private void addNecroniumWasteBottleTooltip(ItemStack stack, List<Component> tooltipComponents) {
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.necronium_despawn_warning").withStyle(ChatFormatting.YELLOW));
	}
}
