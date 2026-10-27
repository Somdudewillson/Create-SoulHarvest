package somdudewillson.create_soul_harvest.content.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;

public class AbsoluteIceRecipe implements Recipe<RecipeInput> {
//?} else {
/*import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;

public class AbsoluteIceRecipe implements Recipe<Container> {
	private ResourceLocation id;

	@Override
	public ResourceLocation getId() {
		return id;
	}
*///?}

	private final BlockCondition target;
	private final Optional<BlockCondition> above;
	private final ConversionResult result;
	private final Optional<Double> minEffectStrength;
	private final Optional<Double> maxEffectStrength;
	private final Optional<Boolean> canSeeSky;
	private final Optional<Integer> minBlockLight;
	private final Optional<Integer> maxBlockLight;

	public AbsoluteIceRecipe(
			BlockCondition target,
			Optional<BlockCondition> above,
			ConversionResult result,
			Optional<Double> minEffectStrength,
			Optional<Double> maxEffectStrength,
			Optional<Boolean> canSeeSky,
			Optional<Integer> minBlockLight,
			Optional<Integer> maxBlockLight) {
		this.target = target;
		this.above = above;
		this.result = result;
		this.minEffectStrength = minEffectStrength;
		this.maxEffectStrength = maxEffectStrength;
		this.canSeeSky = canSeeSky;
		this.minBlockLight = minBlockLight;
		this.maxBlockLight = maxBlockLight;
	}

	//? < 1.21 {
	/*public AbsoluteIceRecipe(
			ResourceLocation id,
			BlockCondition target,
			Optional<BlockCondition> above,
			ConversionResult result,
			Optional<Double> minEffectStrength,
			Optional<Double> maxEffectStrength,
			Optional<Boolean> canSeeSky,
			Optional<Integer> minBlockLight,
			Optional<Integer> maxBlockLight) {
		this(target, above, result, minEffectStrength, maxEffectStrength, canSeeSky, minBlockLight, maxBlockLight);
		this.id = id;
	}
	*///?}

	public BlockCondition getTarget() {
		return target;
	}

	public Optional<BlockCondition> getAbove() {
		return above;
	}

	public ConversionResult getResult() {
		return result;
	}

	public Optional<Double> getMinEffectStrength() {
		return minEffectStrength;
	}

	public Optional<Double> getMaxEffectStrength() {
		return maxEffectStrength;
	}

	public Optional<Boolean> getCanSeeSky() {
		return canSeeSky;
	}

	public Optional<Integer> getMinBlockLight() {
		return minBlockLight;
	}

	public Optional<Integer> getMaxBlockLight() {
		return maxBlockLight;
	}

	public boolean matches(BlockPos pos, BlockState targetState, BlockState aboveState, boolean seesSky, int blockLight, double effectStrength) {
		if (minEffectStrength.isPresent() && effectStrength < minEffectStrength.get()) {
			return false;
		}
		if (maxEffectStrength.isPresent() && effectStrength > maxEffectStrength.get()) {
			return false;
		}
		if (!target.test(targetState)) {
			return false;
		}
		if (above.isPresent() && !above.get().test(aboveState)) {
			return false;
		}
		if (canSeeSky.isPresent()) {
			if (seesSky != canSeeSky.get()) {
				return false;
			}
		}
		if (minBlockLight.isPresent() || maxBlockLight.isPresent()) {
			if (minBlockLight.isPresent() && blockLight < minBlockLight.get()) {
				return false;
			}
			if (maxBlockLight.isPresent() && blockLight > maxBlockLight.get()) {
				return false;
			}
		}
		return true;
	}

	public void apply(Level level, BlockPos pos) {
		result.apply(level, pos);
	}

	//? >= 1.21 {
	@Override
	public boolean matches(RecipeInput inv, Level level) {
		return true;
	}

	@Override
	public ItemStack assemble(RecipeInput inv, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}
	//?} else {
	/*@Override
	public boolean matches(Container inv, Level level) {
		return true;
	}

	@Override
	public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}
	*///?}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.ABSOLUTE_ICE_CONVERSION.getSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.ABSOLUTE_ICE_CONVERSION.getType();
	}

	public void toNetwork(FriendlyByteBuf buf) {
		target.toNetwork(buf);
		buf.writeBoolean(above.isPresent());
		above.ifPresent(cond -> cond.toNetwork(buf));
		result.toNetwork(buf);
		buf.writeBoolean(minEffectStrength.isPresent());
		minEffectStrength.ifPresent(buf::writeDouble);
		buf.writeBoolean(maxEffectStrength.isPresent());
		maxEffectStrength.ifPresent(buf::writeDouble);
		buf.writeBoolean(canSeeSky.isPresent());
		canSeeSky.ifPresent(buf::writeBoolean);
		buf.writeBoolean(minBlockLight.isPresent());
		minBlockLight.ifPresent(buf::writeVarInt);
		buf.writeBoolean(maxBlockLight.isPresent());
		maxBlockLight.ifPresent(buf::writeVarInt);
	}

	public static AbsoluteIceRecipe fromNetwork(FriendlyByteBuf buf) {
		BlockCondition target = BlockCondition.fromNetwork(buf);
		Optional<BlockCondition> above = buf.readBoolean() ? Optional.of(BlockCondition.fromNetwork(buf)) : Optional.empty();
		ConversionResult result = ConversionResult.fromNetwork(buf);
		Optional<Double> minEffectStrength = buf.readBoolean() ? Optional.of(buf.readDouble()) : Optional.empty();
		Optional<Double> maxEffectStrength = buf.readBoolean() ? Optional.of(buf.readDouble()) : Optional.empty();
		Optional<Boolean> canSeeSky = buf.readBoolean() ? Optional.of(buf.readBoolean()) : Optional.empty();
		Optional<Integer> minBlockLight = buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
		Optional<Integer> maxBlockLight = buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
		return new AbsoluteIceRecipe(target, above, result, minEffectStrength, maxEffectStrength, canSeeSky, minBlockLight, maxBlockLight);
	}

	//? < 1.21 {
	/*public static AbsoluteIceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		AbsoluteIceRecipe recipe = fromNetwork(buf);
		recipe.id = id;
		return recipe;
	}
	*///?}

	public static class Serializer implements RecipeSerializer<AbsoluteIceRecipe> {
		//? >= 1.21 {
		public static final MapCodec<AbsoluteIceRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				BlockCondition.CODEC.fieldOf("target").forGetter(AbsoluteIceRecipe::getTarget),
				BlockCondition.CODEC.optionalFieldOf("above").forGetter(AbsoluteIceRecipe::getAbove),
				ConversionResult.CODEC.fieldOf("result").forGetter(AbsoluteIceRecipe::getResult),
				Codec.DOUBLE.optionalFieldOf("min_effect_strength").forGetter(AbsoluteIceRecipe::getMinEffectStrength),
				Codec.DOUBLE.optionalFieldOf("max_effect_strength").forGetter(AbsoluteIceRecipe::getMaxEffectStrength),
				Codec.BOOL.optionalFieldOf("can_see_sky").forGetter(AbsoluteIceRecipe::getCanSeeSky),
				Codec.INT.optionalFieldOf("min_block_light").forGetter(AbsoluteIceRecipe::getMinBlockLight),
				Codec.INT.optionalFieldOf("max_block_light").forGetter(AbsoluteIceRecipe::getMaxBlockLight)
		).apply(inst, AbsoluteIceRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, AbsoluteIceRecipe> STREAM_CODEC = StreamCodec.of(
				(buf, recipe) -> recipe.toNetwork(buf),
				AbsoluteIceRecipe::fromNetwork
		);

		@Override
		public MapCodec<AbsoluteIceRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AbsoluteIceRecipe> streamCodec() {
			return STREAM_CODEC;
		}
		//?} else {
		/*@Override
		public AbsoluteIceRecipe fromJson(ResourceLocation id, JsonObject json) {
			BlockCondition target = BlockCondition.fromJson(GsonHelper.getAsJsonObject(json, "target"));
			Optional<BlockCondition> above = json.has("above")
					? Optional.of(BlockCondition.fromJson(GsonHelper.getAsJsonObject(json, "above")))
					: Optional.empty();
			ConversionResult result = ConversionResult.fromJson(json.get("result"));
			Optional<Double> minEffectStrength = json.has("min_effect_strength")
					? Optional.of(GsonHelper.getAsDouble(json, "min_effect_strength"))
					: Optional.empty();
			Optional<Double> maxEffectStrength = json.has("max_effect_strength")
					? Optional.of(GsonHelper.getAsDouble(json, "max_effect_strength"))
					: Optional.empty();
			Optional<Boolean> canSeeSky = json.has("can_see_sky")
					? Optional.of(GsonHelper.getAsBoolean(json, "can_see_sky"))
					: Optional.empty();
			Optional<Integer> minBlockLight = json.has("min_block_light")
					? Optional.of(GsonHelper.getAsInt(json, "min_block_light"))
					: Optional.empty();
			Optional<Integer> maxBlockLight = json.has("max_block_light")
					? Optional.of(GsonHelper.getAsInt(json, "max_block_light"))
					: Optional.empty();

			return new AbsoluteIceRecipe(id, target, above, result, minEffectStrength, maxEffectStrength, canSeeSky, minBlockLight, maxBlockLight);
		}

		@Override
		public AbsoluteIceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return AbsoluteIceRecipe.fromNetwork(id, buf);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, AbsoluteIceRecipe recipe) {
			recipe.toNetwork(buf);
		}
		*///?}
	}

	public static class BlockCondition {
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

	public interface ConversionResult {
		void apply(Level level, BlockPos pos);
		void toNetwork(FriendlyByteBuf buf);

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

				return DataResult.error(() -> "Invalid absolute ice conversion result: " + input);
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
				throw new JsonSyntaxException("Unknown block in absolute ice conversion result: " + str);
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
					throw new JsonSyntaxException("Unknown block in absolute ice conversion result: " + blockId);
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

			throw new JsonSyntaxException("Invalid absolute ice conversion result: " + element);
		}

		private static <T extends Comparable<T>> BlockState applyPropertyValue(BlockState state, Property<T> prop, String valStr) {
			Optional<T> val = prop.getValue(valStr);
			return val.map(t -> state.setValue(prop, t)).orElse(state);
		}
		*///?}
	}

	public record SetBlockResult(BlockState state) implements ConversionResult {
		@Override
		public void apply(Level level, BlockPos pos) {
			level.setBlockAndUpdate(pos, state);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeByte(0);
			buf.writeVarInt(Block.getId(state));
		}
	}

	public record DestroyResult(boolean dropItems) implements ConversionResult {
		@Override
		public void apply(Level level, BlockPos pos) {
			level.destroyBlock(pos, dropItems);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeByte(1);
			buf.writeBoolean(dropItems);
		}
	}
}
