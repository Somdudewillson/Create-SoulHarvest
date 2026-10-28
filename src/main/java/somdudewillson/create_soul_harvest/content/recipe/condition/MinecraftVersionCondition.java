package somdudewillson.create_soul_harvest.content.recipe.condition;

import net.minecraft.SharedConstants;

import java.util.Optional;

//? neoforge {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;
//?} forge {
/*import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
*///?}

public record MinecraftVersionCondition(Optional<String> minVersion, Optional<String> maxVersion) implements ICondition {
	//? neoforge {
	public static final MapCodec<MinecraftVersionCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder.group(
					Codec.STRING.optionalFieldOf("min_version").forGetter(MinecraftVersionCondition::minVersion),
					Codec.STRING.optionalFieldOf("max_version").forGetter(MinecraftVersionCondition::maxVersion)
			).apply(builder, MinecraftVersionCondition::new)
	);
	//?} forge {
	/*public static final ResourceLocation NAME = SoulHarvestMod.id("minecraft_version");
	*///?}

	public MinecraftVersionCondition(String minVersion) {
		this(Optional.ofNullable(minVersion), Optional.empty());
	}

	public MinecraftVersionCondition(String minVersion, String maxVersion) {
		this(Optional.ofNullable(minVersion), Optional.ofNullable(maxVersion));
	}

	@Override
	public boolean test(IContext context) {
		String current = SharedConstants.getCurrentVersion().getName();
		boolean minFails = minVersion.filter(v -> !v.isEmpty()).map(v -> compareVersions(current, v) < 0).orElse(false);
		boolean maxFails = maxVersion.filter(v -> !v.isEmpty()).map(v -> compareVersions(current, v) > 0).orElse(false);
		return !minFails && !maxFails;
	}

	//? neoforge {
	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}
	//?} forge {
	/*@Override
	public ResourceLocation getID() {
		return NAME;
	}

	public static class Serializer implements IConditionSerializer<MinecraftVersionCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, MinecraftVersionCondition value) {
			value.minVersion().ifPresent(v -> json.addProperty("min_version", v));
			value.maxVersion().ifPresent(v -> json.addProperty("max_version", v));
		}

		@Override
		public MinecraftVersionCondition read(JsonObject json) {
			Optional<String> min = json.has("min_version") ? Optional.of(GsonHelper.getAsString(json, "min_version")) : Optional.empty();
			Optional<String> max = json.has("max_version") ? Optional.of(GsonHelper.getAsString(json, "max_version")) : Optional.empty();
			return new MinecraftVersionCondition(min, max);
		}

		@Override
		public ResourceLocation getID() {
			return NAME;
		}
	}
	*///?}

	@Override
	public String toString() {
		return "minecraft_version(min=" + minVersion.orElse("*") + ", max=" + maxVersion.orElse("*") + ")";
	}

	public static int compareVersions(String v1, String v2) {
		String[] parts1 = v1.split("[.-]");
		String[] parts2 = v2.split("[.-]");
		int length = Math.max(parts1.length, parts2.length);
		for (int i = 0; i < length; i++) {
			int diff = Integer.compare(parsePart(parts1, i), parsePart(parts2, i));
			if (diff != 0) return diff;
		}
		return 0;
	}

	private static int parsePart(String[] parts, int index) {
		if (index >= parts.length) return 0;
		String s = parts[index].replaceAll("\\D", "");
		if (s.isEmpty()) return 0;
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ignored) {
			return 0;
		}
	}
}
