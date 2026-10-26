package somdudewillson.create_soul_harvest.content.item;

import net.createmod.catnip.lang.FontHelper;
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
import somdudewillson.create_soul_harvest.util.SimplePalettizedTooltipModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.simibubi.create.foundation.item.TooltipHelper;

public class ExperienceDropAttunedItem extends Item {

	public static final float DEFAULT_SCALING_EXPONENT = 0.75f;

	private final float yieldMultiplier;
	private final float scalingExponent;

	public ExperienceDropAttunedItem(float yieldMultiplier, float scalingExponent, Properties properties) {
		super(properties);
		this.yieldMultiplier = yieldMultiplier;
		this.scalingExponent = scalingExponent;
	}

	public ExperienceDropAttunedItem(float yieldMultiplier, Properties properties) {
		this(yieldMultiplier, DEFAULT_SCALING_EXPONENT, properties);
	}

	public float getYieldMultiplier() {
		return yieldMultiplier;
	}

	public float getScalingExponent() {
		return scalingExponent;
	}

	public static ItemStack createAttunedExperienceDrop(Item item, ResourceLocation entityId) {
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

	public static class ExperienceDropAttunedTooltipModifier extends SimplePalettizedTooltipModifier {
		protected final Item item;

		public ExperienceDropAttunedTooltipModifier(FontHelper.Palette palette, Item item) {
			super(palette);
			this.item = item;
		}

		@Override
		public void modify(ItemTooltipEvent context) {
			List<Component> tooltip = context.getToolTip();
			List<Component> mainInfo = getExperienceDropAttunedInfo(context.getItemStack());
			if (!mainInfo.isEmpty()) {
				tooltip.addAll(mainInfo);
			}
			tooltip.add(CommonComponents.EMPTY);
			tooltip.addAll(wrapShiftConditionComponents(getExperienceDropAttunedDetailInfo(context.getItemStack())));
		}

		public List<Component> getExperienceDropAttunedInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();

			ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
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

			MutableComponent tunedComp = Component.translatable("tooltip.create_soul_harvest.experience_drop_tuned", nameComponent).withStyle(palette.primary());
			tooltipComponents.add(tunedComp);

			if (item instanceof ExperienceDropAttunedItem expItem) {
				SoulData soulData = SoulDataManager.get(heldSoul);
				if (soulData != null && soulData.xp() > 0) {
					float expected = ExperienceDropItem.calculateExpectedNuggets(soulData.xp(), expItem.getYieldMultiplier(), expItem.getScalingExponent());
					String expectedStr = String.format("%.1f", expected).replaceAll("\\.?0+$", "");
					tooltipComponents.addAll(TooltipHelper.cutTextComponent(
							Component.translatable("tooltip.create_soul_harvest.experience_drop_attuned.expected_nuggets", expectedStr), palette));
				}
			}

			return tooltipComponents;
		}

		public List<Component> getExperienceDropAttunedDetailInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();
			tooltipComponents.addAll(TooltipHelper.cutTextComponent(Component.translatable("tooltip.create_soul_harvest.experience_drop_attuned_desc"), palette));

			if (item instanceof ExperienceDropAttunedItem expItem) {
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
