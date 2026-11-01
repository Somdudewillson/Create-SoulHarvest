package somdudewillson.create_soul_harvest.content.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;

//? >= 1.21 {
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
//?} else {
/*import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
*///?}

public class BlockCondition {
	private final Optional<Boolean> isAir;
	private final Optional<Block> block;
	private final Optional<TagKey<Block>> blockTag;
	private final Optional<Fluid> fluid;
	private final Optional<TagKey<Fluid>> fluidTag;
	private final Optional<Map<String, String>> properties;
	private final boolean inverted;

	public BlockCondition(
			Optional<Boolean> isAir,
			Optional<Block> block,
			Optional<TagKey<Block>> blockTag,
			Optional<Fluid> fluid,
			Optional<TagKey<Fluid>> fluidTag,
			Optional<Map<String, String>> properties,
			boolean inverted) {
		this.isAir = isAir;
		this.block = block;
		this.blockTag = blockTag;
		this.fluid = fluid;
		this.fluidTag = fluidTag;
		this.properties = properties;
		this.inverted = inverted;
	}

	public static BlockCondition of(Block block) {
		return new BlockCondition(Optional.empty(), Optional.of(block), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), false);
	}

	public static BlockCondition of(TagKey<Block> tag) {
		return new BlockCondition(Optional.empty(), Optional.empty(), Optional.of(tag), Optional.empty(), Optional.empty(), Optional.empty(), false);
	}

	public static BlockCondition ofFluid(Fluid fluid) {
		return new BlockCondition(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(fluid), Optional.empty(), Optional.empty(), false);
	}

	public static BlockCondition ofFluid(TagKey<Fluid> tag) {
		return new BlockCondition(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(tag), Optional.empty(), false);
	}

	public static BlockCondition air() {
		return new BlockCondition(Optional.of(true), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), false);
	}

	public BlockCondition inverted() {
		return new BlockCondition(isAir, block, blockTag, fluid, fluidTag, properties, !inverted);
	}

	public Optional<Boolean> getIsAir() {
		return isAir;
	}

	public Optional<Block> getBlock() {
		return block;
	}

	public Optional<TagKey<Block>> getBlockTag() {
		return blockTag;
	}

	public Optional<Fluid> getFluid() {
		return fluid;
	}

	public Optional<TagKey<Fluid>> getFluidTag() {
		return fluidTag;
	}

	public Optional<Map<String, String>> getProperties() {
		return properties;
	}

	public boolean isInverted() {
		return inverted;
	}

	public boolean test(BlockState state) {
		boolean matches = true;
		if (isAir.isPresent() && state.isAir() != isAir.get()) {
			matches = false;
		}
		if (block.isPresent() && !state.is(block.get())) {
			matches = false;
		}
		if (blockTag.isPresent() && !state.is(blockTag.get())) {
			matches = false;
		}
		if (fluid.isPresent() && !state.getFluidState().is(fluid.get())) {
			matches = false;
		}
		if (fluidTag.isPresent() && !state.getFluidState().is(fluidTag.get())) {
			matches = false;
		}
		if (properties.isPresent() && !properties.get().isEmpty()) {
			for (Map.Entry<String, String> entry : properties.get().entrySet()) {
				Property<?> prop = state.getBlock().getStateDefinition().getProperty(entry.getKey());
				if (prop == null || !matchesProperty(state, prop, entry.getValue())) {
					matches = false;
					break;
				}
			}
		}
		return inverted ? !matches : matches;
	}

	private static <T extends Comparable<T>> boolean matchesProperty(BlockState state, Property<T> prop, String expectedValue) {
		T val = state.getValue(prop);
		return prop.getName(val).equals(expectedValue);
	}

	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeBoolean(isAir.isPresent());
		isAir.ifPresent(buf::writeBoolean);
		buf.writeBoolean(block.isPresent());
		block.ifPresent(b -> buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(b)));
		buf.writeBoolean(blockTag.isPresent());
		blockTag.ifPresent(tag -> buf.writeResourceLocation(tag.location()));
		buf.writeBoolean(fluid.isPresent());
		fluid.ifPresent(f -> buf.writeResourceLocation(BuiltInRegistries.FLUID.getKey(f)));
		buf.writeBoolean(fluidTag.isPresent());
		fluidTag.ifPresent(tag -> buf.writeResourceLocation(tag.location()));
		buf.writeBoolean(properties.isPresent());
		properties.ifPresent(props -> {
			buf.writeVarInt(props.size());
			props.forEach((k, v) -> {
				buf.writeUtf(k);
				buf.writeUtf(v);
			});
		});
		buf.writeBoolean(inverted);
	}

	public static BlockCondition fromNetwork(FriendlyByteBuf buf) {
		Optional<Boolean> isAir = buf.readBoolean() ? Optional.of(buf.readBoolean()) : Optional.empty();
		Optional<Block> block = buf.readBoolean() ? Optional.of(BuiltInRegistries.BLOCK.get(buf.readResourceLocation())) : Optional.empty();
		Optional<TagKey<Block>> blockTag = buf.readBoolean() ? Optional.of(TagKey.create(Registries.BLOCK, buf.readResourceLocation())) : Optional.empty();
		Optional<Fluid> fluid = buf.readBoolean() ? Optional.of(BuiltInRegistries.FLUID.get(buf.readResourceLocation())) : Optional.empty();
		Optional<TagKey<Fluid>> fluidTag = buf.readBoolean() ? Optional.of(TagKey.create(Registries.FLUID, buf.readResourceLocation())) : Optional.empty();
		Optional<Map<String, String>> properties;
		if (buf.readBoolean()) {
			int size = buf.readVarInt();
			Map<String, String> map = new HashMap<>(size);
			for (int i = 0; i < size; i++) {
				map.put(buf.readUtf(), buf.readUtf());
			}
			properties = Optional.of(map);
		} else {
			properties = Optional.empty();
		}
		boolean inverted = buf.readBoolean();
		return new BlockCondition(isAir, block, blockTag, fluid, fluidTag, properties, inverted);
	}

	//? >= 1.21 {
	public static final Codec<BlockCondition> CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<BlockCondition, T>> decode(DynamicOps<T> ops, T input) {
			Optional<Boolean> isAir = ops.get(input, "is_air").flatMap(ops::getBooleanValue).result();
			Optional<Block> block = ops.get(input, "block").flatMap(ops::getStringValue).result()
					.map(ResourceLocation::tryParse)
					.map(BuiltInRegistries.BLOCK::get);
			Optional<TagKey<Block>> blockTag = ops.get(input, "block_tag").result()
					.or(() -> ops.get(input, "tag").result())
					.flatMap(t -> ops.getStringValue(t).result())
					.map(ResourceLocation::tryParse)
					.map(id -> TagKey.create(Registries.BLOCK, id));
			Optional<Fluid> fluid = ops.get(input, "fluid").flatMap(ops::getStringValue).result()
					.map(ResourceLocation::tryParse)
					.map(BuiltInRegistries.FLUID::get);
			Optional<TagKey<Fluid>> fluidTag = ops.get(input, "fluid_tag").flatMap(ops::getStringValue).result()
					.map(ResourceLocation::tryParse)
					.map(id -> TagKey.create(Registries.FLUID, id));
			Optional<Map<String, String>> properties = ops.get(input, "properties").flatMap(propsElement -> {
				return ops.getMap(propsElement).map(map -> {
					Map<String, String> result = new HashMap<>();
					map.entries().forEach(pair -> {
						ops.getStringValue(pair.getFirst()).result().ifPresent(k -> {
							ops.getStringValue(pair.getSecond()).result().ifPresent(v -> result.put(k, v));
						});
					});
					return result;
				});
			}).result();
			boolean inverted = ops.get(input, "inverted").flatMap(ops::getBooleanValue).result().orElse(false);

			return DataResult.success(Pair.of(new BlockCondition(isAir, block, blockTag, fluid, fluidTag, properties, inverted), ops.empty()));
		}

		@Override
		public <T> DataResult<T> encode(BlockCondition input, DynamicOps<T> ops, T prefix) {
			Map<T, T> map = new HashMap<>();
			input.isAir.ifPresent(air -> map.put(ops.createString("is_air"), ops.createBoolean(air)));
			input.block.ifPresent(b -> map.put(ops.createString("block"), ops.createString(BuiltInRegistries.BLOCK.getKey(b).toString())));
			input.blockTag.ifPresent(tag -> map.put(ops.createString("block_tag"), ops.createString(tag.location().toString())));
			input.fluid.ifPresent(f -> map.put(ops.createString("fluid"), ops.createString(BuiltInRegistries.FLUID.getKey(f).toString())));
			input.fluidTag.ifPresent(tag -> map.put(ops.createString("fluid_tag"), ops.createString(tag.location().toString())));
			input.properties.ifPresent(props -> {
				Map<T, T> propMap = new HashMap<>();
				props.forEach((k, v) -> propMap.put(ops.createString(k), ops.createString(v)));
				map.put(ops.createString("properties"), ops.createMap(propMap));
			});
			if (input.inverted) {
				map.put(ops.createString("inverted"), ops.createBoolean(true));
			}
			return DataResult.success(ops.createMap(map));
		}
	};
	//?} else {
	/*public static BlockCondition fromJson(JsonObject json) {
		Optional<Boolean> isAir = json.has("is_air") ? Optional.of(GsonHelper.getAsBoolean(json, "is_air")) : Optional.empty();
		Optional<Block> block = Optional.empty();
		if (json.has("block")) {
			ResourceLocation id = ResourceLocation.tryParse(GsonHelper.getAsString(json, "block"));
			if (id != null && BuiltInRegistries.BLOCK.containsKey(id)) {
				block = Optional.of(BuiltInRegistries.BLOCK.get(id));
			}
		}
		Optional<TagKey<Block>> blockTag = Optional.empty();
		if (json.has("block_tag") || json.has("tag")) {
			String tagName = json.has("block_tag") ? GsonHelper.getAsString(json, "block_tag") : GsonHelper.getAsString(json, "tag");
			ResourceLocation id = ResourceLocation.tryParse(tagName);
			if (id != null) {
				blockTag = Optional.of(TagKey.create(Registries.BLOCK, id));
			}
		}
		Optional<Fluid> fluid = Optional.empty();
		if (json.has("fluid")) {
			ResourceLocation id = ResourceLocation.tryParse(GsonHelper.getAsString(json, "fluid"));
			if (id != null && BuiltInRegistries.FLUID.containsKey(id)) {
				fluid = Optional.of(BuiltInRegistries.FLUID.get(id));
			}
		}
		Optional<TagKey<Fluid>> fluidTag = Optional.empty();
		if (json.has("fluid_tag")) {
			ResourceLocation id = ResourceLocation.tryParse(GsonHelper.getAsString(json, "fluid_tag"));
			if (id != null) {
				fluidTag = Optional.of(TagKey.create(Registries.FLUID, id));
			}
		}
		Optional<Map<String, String>> properties = Optional.empty();
		if (json.has("properties")) {
			JsonObject propsObj = GsonHelper.getAsJsonObject(json, "properties");
			Map<String, String> map = new HashMap<>();
			propsObj.entrySet().forEach(entry -> map.put(entry.getKey(), entry.getValue().getAsString()));
			properties = Optional.of(map);
		}
		boolean inverted = GsonHelper.getAsBoolean(json, "inverted", false);

		return new BlockCondition(isAir, block, blockTag, fluid, fluidTag, properties, inverted);
	}
	*///?}
}
