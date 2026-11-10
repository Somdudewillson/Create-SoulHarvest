package somdudewillson.create_soul_harvest.content.potentia;

import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.util.List;
import java.util.Locale;

public class PotentiaTooltipHelper {

	public static LangBuilder langBuilder() {
		return new LangBuilder(SoulHarvestMod.MOD_ID);
	}

	/**
	 * Appends shared tank fullness and potentia type information to the goggle tooltip using LangBuilder.
	 */
	public static void appendTankTooltip(List<Component> tooltip, PotentiaTank tank) {
		if (tank == null) return;
		appendTankTooltip(tooltip, tank.getPotentia(), tank.getTankCapacity(0));
	}

	/**
	 * Appends shared potentia stack and capacity details to the goggle tooltip.
	 */
	public static void appendTankTooltip(List<Component> tooltip, SoulPotentiaStack potentia, long capacity) {
		addToGoggles(langBuilder().translate("gui.goggles.potentia_contents_heading")
				.style(ChatFormatting.GRAY), tooltip, 0);

		if (potentia == null || potentia.isEmpty()) {
			addToGoggles(langBuilder().translate("gui.goggles.potentia_empty", capacity)
					.style(ChatFormatting.DARK_GRAY), tooltip, 1);
			return;
		}

		addToGoggles(langBuilder().text("")
				.text(ChatFormatting.AQUA, String.format(Locale.ROOT, "%d", potentia.getAmount()))
				.text(ChatFormatting.GRAY, " / " + capacity + " mB"), tooltip, 1);

		addToGoggles(langBuilder().translate("gui.goggles.potentia_type_heading")
				.style(ChatFormatting.GRAY)
				.space()
				.text(ChatFormatting.GOLD, getPotentiaTypeDisplayName(potentia)), tooltip, 1);
	}

	public static void addToGoggles(LangBuilder builder, List<Component> tooltip, int indent) {
		try {
			if (Minecraft.getInstance() != null && Minecraft.getInstance().font != null) {
				if (indent > 0) {
					builder.forGoggles(tooltip, indent);
				} else {
					builder.forGoggles(tooltip);
				}
				return;
			}
		} catch (Throwable ignored) {
		}
		tooltip.add(builder.component());
	}

	public static String getPotentiaTypeDisplayName(SoulPotentiaStack potentia) {
		if (potentia == null || potentia.isEmpty() || !potentia.isTyped()) {
			return Component.translatable("create_soul_harvest.gui.goggles.potentia_type_untyped").getString();
		}
		ResourceLocation entityType = potentia.getEntityType();
		try {
			return BuiltInRegistries.ENTITY_TYPE.getOptional(entityType)
					.map(type -> type.getDescription().getString())
					.orElse(entityType.toString());
		} catch (Throwable ignored) {
			return entityType.toString();
		}
	}
}
