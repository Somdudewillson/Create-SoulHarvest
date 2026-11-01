package somdudewillson.create_soul_harvest.content.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;

//? >= 1.21 {
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?} else {
/*import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.GsonHelper;
*///?}

public record IntRange(int min, int max) {
	public static final IntRange ZERO = new IntRange(0, 0);

	public static IntRange of(int value) {
		return new IntRange(value, value);
	}

	public static IntRange of(int min, int max) {
		return new IntRange(min, max);
	}

	public int roll(RandomSource random) {
		if (min >= max) {
			return min;
		}
		return min + random.nextInt(max - min + 1);
	}

	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeVarInt(min);
		buf.writeVarInt(max);
	}

	public static IntRange fromNetwork(FriendlyByteBuf buf) {
		return new IntRange(buf.readVarInt(), buf.readVarInt());
	}

	//? >= 1.21 {
	public static final Codec<IntRange> RECORD_CODEC = RecordCodecBuilder.create(inst -> inst.group(
			Codec.INT.fieldOf("min").forGetter(IntRange::min),
			Codec.INT.fieldOf("max").forGetter(IntRange::max)
	).apply(inst, IntRange::new));

	public static final Codec<IntRange> CODEC = Codec.either(Codec.INT, RECORD_CODEC).xmap(
			either -> either.map(IntRange::of, r -> r),
			range -> range.min == range.max ? Either.left(range.min) : Either.right(range)
	);
	//?} else {
	/*public static IntRange fromJson(JsonElement element) {
		if (element.isJsonPrimitive()) {
			int val = element.getAsInt();
			return new IntRange(val, val);
		}
		JsonObject obj = element.getAsJsonObject();
		int min = GsonHelper.getAsInt(obj, "min");
		int max = GsonHelper.getAsInt(obj, "max", min);
		return new IntRange(min, max);
	}

	public JsonElement toJson() {
		if (min == max) {
			return new JsonPrimitive(min);
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("min", min);
		obj.addProperty("max", max);
		return obj;
	}
	*///?}
}
