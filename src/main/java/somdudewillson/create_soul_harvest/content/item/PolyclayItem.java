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
import java.util.function.Supplier;

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
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.polyclay_tier", tier).withStyle(ChatFormatting.GRAY));
	}
}
