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
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;
import somdudewillson.create_soul_harvest.registry.ModTags;

//? >= 1.21 {

//?} else {
/*import net.minecraft.world.level.Level;
*///?}

import java.util.List;
import java.util.Optional;

public class PolyclayAttunedItem extends Item {

	private final int tier;

	public PolyclayAttunedItem(int tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	public int getTier() {
		return tier;
	}

	public static ItemStack createAttunedPolyclay(Item item, ResourceLocation entityId, boolean enhanced) {
		ItemStack stack = new ItemStack(item);
		ISoulHolder.get(stack).ifPresent(holder -> {
			holder.setHeldSoul(entityId);
			holder.setSoulPortion(100);
			holder.setEnhanced(enhanced);
		});
		return stack;
	}

	public static ItemStack createAttunedPolyclay(Item item, ResourceLocation entityId) {
		return createAttunedPolyclay(item, entityId, false);
	}

	public static boolean isEnhanced(ItemStack stack) {
		return ISoulHolder.isEnhanced(stack);
	}

	public static void setEnhanced(ItemStack stack, boolean enhanced) {
		ISoulHolder.get(stack).ifPresent(holder -> holder.setEnhanced(enhanced));
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

			SoulData soulData = SoulDataManager.get(heldSoul);
			int soulTier = soulData != null ? soulData.tier() : 0;
			if (soulTier <= 0 && optionalType.isPresent()) {
				EntityType<?> type = optionalType.get();
				if (type.is(ModTags.EntityTypes.T1_MOB)) soulTier = 1;
				else if (type.is(ModTags.EntityTypes.T2_MOB)) soulTier = 2;
				else if (type.is(ModTags.EntityTypes.T3_MOB)) soulTier = 3;
			}

			nameComp.append(Component.translatable("tooltip.create_soul_harvest.polyclay_tuned", soulTier).withStyle(ChatFormatting.GRAY));
			tooltipComponents.add(nameComp);

			MutableComponent tierComp = Component.translatable("tooltip.create_soul_harvest.polyclay_tier", tier).withStyle(ChatFormatting.GRAY);
			if (soulTier > 0) {
				int diff = tier - soulTier;
				if (diff > 0) {
					tierComp.append(Component.literal(" " + "\u25B2".repeat(diff)).withStyle(ChatFormatting.GREEN));
				} else if (diff < 0) {
					tierComp.append(Component.literal(" " + "\u25BC".repeat(-diff)).withStyle(ChatFormatting.RED));
				}
			}
			tooltipComponents.add(tierComp);

			if (isEnhanced(stack)) {
				tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.polyclay_enhanced").withStyle(ChatFormatting.GOLD));
			}
		}
	}
}
