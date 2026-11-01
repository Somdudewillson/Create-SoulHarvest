package somdudewillson.create_soul_harvest.content.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

//? >= 1.21 {
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
//?} else {
/*import java.util.Map;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.properties.Property;
*///?}

public interface ConversionResult {
	void apply(Level level, BlockPos pos);
	void toNetwork(FriendlyByteBuf buf);
	default BlockState getResultingState() {
		return Blocks.AIR.defaultBlockState();
	}

	static ConversionResult setBlock(BlockState state) {
		return new SetBlockResult(state);
	}

	static ConversionResult setBlock(Block block) {
		return new SetBlockResult(block.defaultBlockState());
	}

	static ConversionResult destroy() {
		return new DestroyResult(true);
	}

	static ConversionResult destroy(boolean dropItems) {
		return new DestroyResult(dropItems);
	}

	static ConversionResult fromNetwork(FriendlyByteBuf buf) {
		byte type = buf.readByte();
		if (type == 0) {
			return new SetBlockResult(Block.stateById(buf.readVarInt()));
		} else {
			return new DestroyResult(buf.readBoolean());
		}
	}

	record SetBlockResult(BlockState state) implements ConversionResult {
		@Override
		public void apply(Level level, BlockPos pos) {
			level.setBlockAndUpdate(pos, state);
		}

		@Override
		public BlockState getResultingState() {
			return state;
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeByte(0);
			buf.writeVarInt(Block.getId(state));
		}
	}

	record DestroyResult(boolean dropItems) implements ConversionResult {
		@Override
		public void apply(Level level, BlockPos pos) {
			level.destroyBlock(pos, dropItems);
		}

		@Override
		public BlockState getResultingState() {
			return Blocks.AIR.defaultBlockState();
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeByte(1);
			buf.writeBoolean(dropItems);
		}
	}

	//? >= 1.21 {
	Codec<ConversionResult> CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<ConversionResult, T>> decode(DynamicOps<T> ops, T input) {
			Optional<String> str = ops.getStringValue(input).result();
			if (str.isPresent()) {
				if (str.get().equalsIgnoreCase("destroy")) {
					return DataResult.success(Pair.of(ConversionResult.destroy(true), ops.empty()));
				}
				ResourceLocation blockId = ResourceLocation.tryParse(str.get());
				if (blockId != null && BuiltInRegistries.BLOCK.containsKey(blockId)) {
					return DataResult.success(Pair.of(ConversionResult.setBlock(BuiltInRegistries.BLOCK.get(blockId)), ops.empty()));
				}
			}

			Optional<String> action = ops.get(input, "action").result()
					.or(() -> ops.get(input, "type").result())
					.flatMap(t -> ops.getStringValue(t).result());
			if (action.isPresent() && action.get().equalsIgnoreCase("destroy")) {
				boolean dropItems = ops.get(input, "drop_items").flatMap(ops::getBooleanValue).result().orElse(true);
				return DataResult.success(Pair.of(ConversionResult.destroy(dropItems), ops.empty()));
			}

			Optional<String> blockStr = ops.get(input, "block").flatMap(ops::getStringValue).result();
			if (blockStr.isPresent()) {
				ResourceLocation blockId = ResourceLocation.tryParse(blockStr.get());
				if (blockId != null && BuiltInRegistries.BLOCK.containsKey(blockId)) {
					Block block = BuiltInRegistries.BLOCK.get(blockId);
					BlockState state = block.defaultBlockState();
					Optional<Map<String, String>> props = ops.get(input, "properties").flatMap(propsElement -> {
						return ops.getMap(propsElement).map(map -> {
							Map<String, String> res = new HashMap<>();
							map.entries().forEach(pair -> {
								ops.getStringValue(pair.getFirst()).result().ifPresent(k -> {
									ops.getStringValue(pair.getSecond()).result().ifPresent(v -> res.put(k, v));
								});
							});
							return res;
						});
					}).result();
					if (props.isPresent()) {
						for (Map.Entry<String, String> entry : props.get().entrySet()) {
							Property<?> prop = state.getBlock().getStateDefinition().getProperty(entry.getKey());
							if (prop != null) {
								state = applyPropertyValue(state, prop, entry.getValue());
							}
						}
					}
					return DataResult.success(Pair.of(ConversionResult.setBlock(state), ops.empty()));
				}
			}

			return DataResult.error(() -> "Invalid conversion result: " + input);
		}

		private static <T extends Comparable<T>> BlockState applyPropertyValue(BlockState state, Property<T> prop, String valStr) {
			Optional<T> val = prop.getValue(valStr);
			return val.map(t -> state.setValue(prop, t)).orElse(state);
		}

		@Override
		public <T> DataResult<T> encode(ConversionResult input, DynamicOps<T> ops, T prefix) {
			if (input instanceof DestroyResult d) {
				if (d.dropItems()) {
					return DataResult.success(ops.createString("destroy"));
				}
				Map<T, T> map = new HashMap<>();
				map.put(ops.createString("action"), ops.createString("destroy"));
				map.put(ops.createString("drop_items"), ops.createBoolean(false));
				return DataResult.success(ops.createMap(map));
			} else if (input instanceof SetBlockResult s) {
				Map<T, T> map = new HashMap<>();
				map.put(ops.createString("block"), ops.createString(BuiltInRegistries.BLOCK.getKey(s.state().getBlock()).toString()));
				return DataResult.success(ops.createMap(map));
			}
			return DataResult.error(() -> "Unknown conversion result type");
		}
	};
	//?} else {
	/*static ConversionResult fromJson(JsonElement element) {
		if (element.isJsonPrimitive()) {
			String str = element.getAsString();
			if (str.equalsIgnoreCase("destroy")) {
				return ConversionResult.destroy(true);
			}
			ResourceLocation blockId = ResourceLocation.tryParse(str);
			if (blockId != null && BuiltInRegistries.BLOCK.containsKey(blockId)) {
				return ConversionResult.setBlock(BuiltInRegistries.BLOCK.get(blockId));
			}
			throw new JsonSyntaxException("Unknown block in conversion result: " + str);
		}

		JsonObject obj = element.getAsJsonObject();
		if (obj.has("action") || obj.has("type")) {
			String action = obj.has("action") ? GsonHelper.getAsString(obj, "action") : GsonHelper.getAsString(obj, "type");
			if (action.equalsIgnoreCase("destroy")) {
				boolean dropItems = GsonHelper.getAsBoolean(obj, "drop_items", true);
				return ConversionResult.destroy(dropItems);
			}
		}

		if (obj.has("block")) {
			ResourceLocation blockId = ResourceLocation.tryParse(GsonHelper.getAsString(obj, "block"));
			if (blockId == null || !BuiltInRegistries.BLOCK.containsKey(blockId)) {
				throw new JsonSyntaxException("Unknown block in conversion result: " + blockId);
			}
			Block block = BuiltInRegistries.BLOCK.get(blockId);
			BlockState state = block.defaultBlockState();
			if (obj.has("properties")) {
				JsonObject props = GsonHelper.getAsJsonObject(obj, "properties");
				for (Map.Entry<String, JsonElement> entry : props.entrySet()) {
					Property<?> prop = state.getBlock().getStateDefinition().getProperty(entry.getKey());
					if (prop != null) {
						state = applyPropertyValue(state, prop, entry.getValue().getAsString());
					}
				}
			}
			return ConversionResult.setBlock(state);
		}

		throw new JsonSyntaxException("Invalid conversion result: " + element);
	}

	private static <T extends Comparable<T>> BlockState applyPropertyValue(BlockState state, Property<T> prop, String valStr) {
		Optional<T> val = prop.getValue(valStr);
		return val.map(t -> state.setValue(prop, t)).orElse(state);
	}
	*///?}
}
