package somdudewillson.create_soul_harvest.content.item;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? neoforge {
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
//?} forge {
/*import net.minecraftforge.event.entity.player.ItemTooltipEvent;
*///?}
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;
import somdudewillson.create_soul_harvest.registry.ModTags;
import somdudewillson.create_soul_harvest.util.SimplePalettizedTooltipModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.simibubi.create.foundation.item.TooltipHelper;

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
	
	public static class PolyclayAttunedTooltipModifier extends SimplePalettizedTooltipModifier {
		protected final Item item;

		public PolyclayAttunedTooltipModifier(FontHelper.Palette palette, Item item) {
			super(palette);
			this.item = item;
		}

		@Override
		public void modify(ItemTooltipEvent context) {
			List<Component> tooltip = context.getToolTip();
			tooltip.addAll(getPolyclayAttunedInfo(context.getItemStack()));
			tooltip.add(CommonComponents.EMPTY);
			tooltip.addAll(wrapShiftConditionComponents(getPolyclayAttunedDetailInfo(context.getItemStack())));
		}
		
		protected static int getSoulTier(ResourceLocation heldSoul, Optional<EntityType<?>> optionalType) {
			SoulData soulData = SoulDataManager.get(heldSoul);
			
			if (soulData != null) {
				return soulData.tier();
			}
			if (optionalType.isPresent()) {
				EntityType<?> type = optionalType.get();
				if (type.is(ModTags.EntityTypes.T1_MOB)) return 1;
				else if (type.is(ModTags.EntityTypes.T2_MOB)) return 2;
				else if (type.is(ModTags.EntityTypes.T3_MOB)) return 3;
			}
			
			return 0;
		}
		
		public List<Component> getPolyclayAttunedInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();
			
			ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
			int tier = ((PolyclayAttunedItem) item).tier;
			
			tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.soul_item_tier", tier), palette));
			
			if (heldSoul == null) {
				return tooltipComponents;
			}

			Optional<EntityType<?>> optionalType = ISoulHolder.tryGetHeldSoulEntityType(stack);
			Component nameComponent;
			if (optionalType.isPresent()) {
				nameComponent = Component.translatable(optionalType.get().getDescriptionId()).withStyle(palette.highlight());
			} else {
				nameComponent = Component.literal(heldSoul.toString()).withStyle(palette.highlight());
			}
			int soulTier = getSoulTier(heldSoul, optionalType);
			Component soulTierComp = Component.literal(Integer.toString(soulTier)).withStyle(palette.highlight());

			MutableComponent tierComp = Component.translatable("tooltip.create_soul_harvest.polyclay_tuned", nameComponent, soulTierComp).withStyle(palette.primary());
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
				tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.polyclay_enhanced").withStyle(palette.highlight()));
			}
			
			return tooltipComponents;
		}
		
		public List<Component> getPolyclayAttunedDetailInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();
			
			tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.polyclay_attuned_desc"), palette));
			
			ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
			int tier = ((PolyclayAttunedItem) item).tier;
			if (heldSoul == null) {
				return tooltipComponents;
			}
			Optional<EntityType<?>> optionalType = ISoulHolder.tryGetHeldSoulEntityType(stack);
			int soulTier = getSoulTier(heldSoul, optionalType);
			
			tooltipComponents.add(CommonComponents.EMPTY);
			
			if (tier < soulTier) {
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.polyclay_attuned.inferior_polyclay_tier"), palette));
			} else if (tier > soulTier) {
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.polyclay_attuned.superior_polyclay_tier"), palette));
			} else {
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.polyclay_attuned.equal_polyclay_tier"), palette));
			}

			if (isEnhanced(stack)) {
				tooltipComponents.add(CommonComponents.EMPTY);
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.polyclay_attuned.enhanced_desc"), palette));
			}
			
			return tooltipComponents;
		}
	}
}
