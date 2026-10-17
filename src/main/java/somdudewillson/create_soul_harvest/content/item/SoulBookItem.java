package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

//? >= 1.21 {

//?} else {
/*import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
*///?}

import java.util.List;
import java.util.Optional;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;

public class SoulBookItem extends Item {

	private final int tier;

	public SoulBookItem(int tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	public int getTier() {
		return tier;
	}

	public static void setSoulData(ItemStack stack, ResourceLocation heldSoul, int portion) {
		ISoulHolder.get(stack).ifPresent(holder -> {
			holder.setHeldSoul(heldSoul);
			holder.setSoulPortion(portion);
		});
	}

	public static ItemStack createCompletedSoulBook(Item item, ResourceLocation entityId) {
		ItemStack stack = new ItemStack(item);
		setSoulData(stack, entityId, 100);
		return stack;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return ISoulHolder.getSoulPortion(stack) >= 100 || super.isFoil(stack);
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
		ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
		int soulPortion = ISoulHolder.getSoulPortion(stack);

		if (heldSoul == null || soulPortion <= 0) {
			tooltipComponents.add(Component.literal("Hold in offhand to capture the souls of your enemies!").withStyle(ChatFormatting.GRAY));
		} else {
			Optional<EntityType<?>> optionalType = ISoulHolder.tryGetHeldSoulEntityType(stack);
			MutableComponent nameComp;
			if (optionalType.isPresent()) {
				nameComp = Component.translatable(optionalType.get().getDescriptionId()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
			} else {
				nameComp = Component.literal(heldSoul.toString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
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
