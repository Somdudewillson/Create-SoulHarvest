package somdudewillson.create_soul_harvest.content.item;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? neoforge {
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
//?} forge {
/*import net.minecraftforge.event.entity.player.ItemTooltipEvent;
*///?}

import java.util.ArrayList;

//? >= 1.21 {

//?} else {
/*import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
*///?}

import java.util.List;
import java.util.Optional;

import com.simibubi.create.foundation.item.TooltipHelper;

import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.util.SimplePalettizedTooltipModifier;

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
	
	public static class SoulBookTooltipModifier extends SimplePalettizedTooltipModifier {
		protected final Item item;

		public SoulBookTooltipModifier(FontHelper.Palette palette, Item item) {
			super(palette);
			this.item = item;
		}

		@Override
		public void modify(ItemTooltipEvent context) {
			List<Component> tooltip = context.getToolTip();
			tooltip.addAll(getSoulbookInfo(context.getItemStack()));
		}
		
		public List<Component> getSoulbookInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();
			
			ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
			int soulPortion = ISoulHolder.getSoulPortion(stack);

			if (heldSoul != null && soulPortion > 0) {				
				Optional<EntityType<?>> optionalType = ISoulHolder.tryGetHeldSoulEntityType(stack);
				Component nameComponent;
				if (optionalType.isPresent()) {
					nameComponent = Component.translatable(optionalType.get().getDescriptionId());
				} else {
					nameComponent = Component.literal(heldSoul.toString());
				}
				
				Component soulComponent;
				if (soulPortion < 100) {
					soulComponent = Component.translatable("tooltip.create_soul_harvest.soul_partial", nameComponent, soulPortion);
				} else {
					soulComponent = Component.translatable("tooltip.create_soul_harvest.soul_complete", nameComponent);
				}
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(soulComponent, palette));
				tooltipComponents.add(CommonComponents.EMPTY);
			}
			
			List<Component> summaryComponents = new ArrayList<>();
			if (item instanceof SoulBookItem soulBookItem) {
				summaryComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.soul_item_tier", soulBookItem.getTier()), palette));
				summaryComponents.add(CommonComponents.EMPTY);
			}
			summaryComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.soul_book_desc"), palette));
			tooltipComponents.addAll(SimplePalettizedTooltipModifier.wrapShiftConditionComponents(summaryComponents));
			
			return tooltipComponents;
		}
	}
}
