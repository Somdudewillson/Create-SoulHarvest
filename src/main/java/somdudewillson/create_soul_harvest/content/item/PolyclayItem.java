package somdudewillson.create_soul_harvest.content.item;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? neoforge {
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
//?} forge {
/*import net.minecraftforge.event.entity.player.ItemTooltipEvent;
*///?}
import somdudewillson.create_soul_harvest.util.SimplePalettizedTooltipModifier;

import java.util.ArrayList;

//? >= 1.21 {

//?} else {
/*import net.minecraft.world.level.Level;
*///?}

import java.util.List;
import java.util.function.Supplier;

import com.simibubi.create.foundation.item.TooltipHelper;

public class PolyclayItem extends Item {

	public static final float LOWER_TIER_DROP_MULTIPLIER = 0.5f;
	public static final float HIGHER_TIER_DROP_BONUS_PER_TIER = 0.25f;

	private final int tier;
	private final Supplier<? extends Item> transitionalItemSupplier;
	private final Supplier<? extends Item> attunedItemSupplier;

	public PolyclayItem(int tier, Supplier<? extends Item> transitionalItemSupplier, Supplier<? extends Item> attunedItemSupplier, Properties properties) {
		super(properties);
		this.tier = tier;
		this.transitionalItemSupplier = transitionalItemSupplier;
		this.attunedItemSupplier = attunedItemSupplier;
	}

	public PolyclayItem(int tier, Item transitionalItem, Item attunedItem, Properties properties) {
		this(tier, () -> transitionalItem, () -> attunedItem, properties);
	}

	public int getTier() {
		return tier;
	}

	public Item getTransitionalItem() {
		return transitionalItemSupplier != null ? transitionalItemSupplier.get() : this;
	}

	public Item getAttunedItem() {
		return attunedItemSupplier != null ? attunedItemSupplier.get() : null;
	}

	public static float getDropMultiplier(int polyclayTier, int soulTier) {
		if (soulTier == polyclayTier + 1) {
			return LOWER_TIER_DROP_MULTIPLIER;
		} else if (soulTier == polyclayTier) {
			return 1.0f;
		} else if (polyclayTier > soulTier) {
			int diff = polyclayTier - soulTier;
			return 1.0f + (diff * HIGHER_TIER_DROP_BONUS_PER_TIER);
		}
		return 0.0f;
	}
	
	public static class PolyclayTooltipModifier extends SimplePalettizedTooltipModifier {
		protected final Item item;

		public PolyclayTooltipModifier(FontHelper.Palette palette, Item item) {
			super(palette);
			this.item = item;
		}

		@Override
		public void modify(ItemTooltipEvent context) {
			List<Component> tooltip = context.getToolTip();
			tooltip.addAll(getPolyclayInfo(context.getItemStack()));
		}
		
		public List<Component> getPolyclayInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();
			
			if (item instanceof PolyclayItem polyclayItem) {
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.soul_item_tier", polyclayItem.getTier()), palette));
			}
			
			return tooltipComponents;
		}
	}
}
