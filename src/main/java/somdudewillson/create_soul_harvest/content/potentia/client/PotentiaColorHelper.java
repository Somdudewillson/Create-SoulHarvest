package somdudewillson.create_soul_harvest.content.potentia.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPalette;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureManager;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;

public class PotentiaColorHelper {

	public static final int DEFAULT_PRIMARY = 0x00E5FF;    // Vibrant Soul Cyan
	public static final int DEFAULT_SECONDARY = 0x9D4EDD;  // Amethyst Purple
	public static final int DEFAULT_ACCENT = 0x72EFDD;     // Soul Fire Teal

	public record PotentiaColors(int primary, int secondary, int accent) {
		public int getByTintIndex(int tintIndex) {
			return switch (tintIndex) {
				case 0 -> primary;
				case 1 -> secondary;
				case 2 -> accent;
				default -> 0xFFFFFF;
			};
		}

		public float getRed(int color) {
			return ((color >> 16) & 0xFF) / 255.0f;
		}

		public float getGreen(int color) {
			return ((color >> 8) & 0xFF) / 255.0f;
		}

		public float getBlue(int color) {
			return (color & 0xFF) / 255.0f;
		}
	}

	public static PotentiaColors getColors(@Nullable SoulPotentiaStack stack) {
		if (stack == null || stack.isEmpty() || !stack.isTyped()) {
			return getColors((ResourceLocation) null);
		}
		return getColors(stack.getEntityType());
	}

	public static PotentiaColors getColors(@Nullable ResourceLocation entityType) {
		if (entityType == null) {
			return new PotentiaColors(DEFAULT_PRIMARY, DEFAULT_SECONDARY, DEFAULT_ACCENT);
		}

		MobPalette palette = MobPatternTextureManager.getPalette(entityType);
		if (palette != null) {
			return getColorsFromPalette(palette);
		}

		// Fallback to SpawnEggItem colors if palette is not yet extracted/ready
		try {
			EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(entityType).orElse(null);
			if (type != null) {
				SpawnEggItem egg = SpawnEggItem.byId(type);
				if (egg != null) {
					int eggPrimary = egg.getColor(0);
					int eggSecondary = egg.getColor(1);
					int eggAccent = eggSecondary;
					return new PotentiaColors(eggPrimary, eggSecondary, eggAccent);
				}
			}
		} catch (Throwable ignored) {
		}

		// Fallback to deterministic hash if no egg or palette exists
		int hash = entityType.hashCode();
		int r = (hash & 0xFF0000) >> 16;
		int g = (hash & 0x00FF00) >> 8;
		int b = hash & 0x0000FF;
		int primary = (r << 16) | (g << 8) | b;
		return new PotentiaColors(primary, DEFAULT_SECONDARY, DEFAULT_ACCENT);
	}

	public static PotentiaColors getColorsFromPalette(MobPalette palette) {
		int primary = DEFAULT_PRIMARY;
		int secondary = DEFAULT_SECONDARY;
		int accent = DEFAULT_ACCENT;

		if (palette != null) {
			if (palette.getMostFrequentCommonColor() != null) {
				primary = palette.getMostFrequentCommonColor().toRgbInt();
			}
			if (!palette.rareColors().isEmpty()) {
				secondary = palette.rareColors().get(0).toRgbInt();
			} else if (palette.getLeastFrequentCommonColor() != null) {
				secondary = palette.getLeastFrequentCommonColor().toRgbInt();
			}
			if (palette.rareColors().size() > 1) {
				accent = palette.rareColors().get(1).toRgbInt();
			} else {
				accent = primary;
			}
		}

		return new PotentiaColors(primary, secondary, accent);
	}
}
