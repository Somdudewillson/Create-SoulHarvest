package somdudewillson.create_soul_harvest.content.item;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.network.chat.CommonComponents;
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
import java.util.List;
import java.util.function.Supplier;

import com.simibubi.create.foundation.item.TooltipHelper;

public class ExperienceDropItem extends Item {

	public static final float DEFAULT_SCALING_EXPONENT = 0.75f;

	private final float yieldMultiplier;
	private final float scalingExponent;
	private final Supplier<? extends Item> transitionalItemSupplier;
	private final Supplier<? extends Item> attunedItemSupplier;

	public ExperienceDropItem(float yieldMultiplier, float scalingExponent, Supplier<? extends Item> transitionalItemSupplier, Supplier<? extends Item> attunedItemSupplier, Properties properties) {
		super(properties);
		this.yieldMultiplier = yieldMultiplier;
		this.scalingExponent = scalingExponent;
		this.transitionalItemSupplier = transitionalItemSupplier;
		this.attunedItemSupplier = attunedItemSupplier;
	}

	public ExperienceDropItem(float yieldMultiplier, Supplier<? extends Item> transitionalItemSupplier, Supplier<? extends Item> attunedItemSupplier, Properties properties) {
		this(yieldMultiplier, DEFAULT_SCALING_EXPONENT, transitionalItemSupplier, attunedItemSupplier, properties);
	}

	public ExperienceDropItem(float yieldMultiplier, float scalingExponent, Item transitionalItem, Item attunedItem, Properties properties) {
		this(yieldMultiplier, scalingExponent, () -> transitionalItem, () -> attunedItem, properties);
	}

	public ExperienceDropItem(float yieldMultiplier, Item transitionalItem, Item attunedItem, Properties properties) {
		this(yieldMultiplier, DEFAULT_SCALING_EXPONENT, () -> transitionalItem, () -> attunedItem, properties);
	}

	public float getYieldMultiplier() {
		return yieldMultiplier;
	}

	public float getScalingExponent() {
		return scalingExponent;
	}

	public Item getTransitionalItem() {
		return transitionalItemSupplier != null ? transitionalItemSupplier.get() : this;
	}

	public Item getAttunedItem() {
		return attunedItemSupplier != null ? attunedItemSupplier.get() : null;
	}

	public static float calculateExpectedNuggets(int xp, float yieldMultiplier, float scalingExponent) {
		if (xp <= 0) return 0.0f;
		float baseNuggets = xp / 3.0f;
		return (float) (yieldMultiplier * Math.pow(baseNuggets, scalingExponent));
	}

	public static class ExperienceDropTooltipModifier extends SimplePalettizedTooltipModifier {
		protected final Item item;

		public ExperienceDropTooltipModifier(FontHelper.Palette palette, Item item) {
			super(palette);
			this.item = item;
		}

		@Override
		public void modify(ItemTooltipEvent context) {
			List<Component> tooltip = context.getToolTip();
			List<Component> mainInfo = getExperienceDropInfo(context.getItemStack());
			if (!mainInfo.isEmpty()) {
				tooltip.addAll(mainInfo);
			}
			tooltip.add(CommonComponents.EMPTY);
			tooltip.addAll(wrapShiftConditionComponents(getExperienceDropDetailInfo(context.getItemStack())));
		}

		public List<Component> getExperienceDropInfo(ItemStack stack) {
			return new ArrayList<>();
		}

		public List<Component> getExperienceDropDetailInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();
			tooltipComponents.addAll(TooltipHelper.cutTextComponent(
					Component.translatable("tooltip.create_soul_harvest.experience_drop_desc"), palette));

			if (item instanceof ExperienceDropItem expItem) {
				tooltipComponents.add(CommonComponents.EMPTY);

				String multStr = String.format("%.2f", expItem.getYieldMultiplier()).replaceAll("\\.?0+$", "");
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(
						Component.translatable("tooltip.create_soul_harvest.experience_drop.yield", multStr), palette));

				int effPercent = Math.round(expItem.getScalingExponent() * 100);
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(
						Component.translatable("tooltip.create_soul_harvest.experience_drop.efficiency", effPercent), palette));
			}

			return tooltipComponents;
		}
	}
}
