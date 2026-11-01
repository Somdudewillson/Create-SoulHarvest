package somdudewillson.create_soul_harvest.content.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;

public class BiocideConversionRecipe implements Recipe<RecipeInput> {
//?} else {
/*import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;

public class BiocideConversionRecipe implements Recipe<Container> {
	private ResourceLocation id;

	@Override
	public ResourceLocation getId() {
		return id;
	}
*///?}

	private final BlockCondition target;
	private final Optional<ConversionResult> result;
	private final Optional<Integer> minGas;
	private final IntRange densityChange;
	private final boolean createsGasBlock;
	private final IntRange baseGasDensity;
	private final Optional<List<Direction>> directions;

	public BiocideConversionRecipe(
			BlockCondition target,
			Optional<ConversionResult> result,
			Optional<Integer> minGas,
			IntRange densityChange,
			boolean createsGasBlock,
			IntRange baseGasDensity,
			Optional<List<Direction>> directions) {
		this.target = target;
		this.result = result;
		this.minGas = minGas;
		this.densityChange = densityChange;
		this.createsGasBlock = createsGasBlock;
		this.baseGasDensity = baseGasDensity;
		this.directions = directions;
	}

	//? < 1.21 {
	/*public BiocideConversionRecipe(
			ResourceLocation id,
			BlockCondition target,
			Optional<ConversionResult> result,
			Optional<Integer> minGas,
			IntRange densityChange,
			boolean createsGasBlock,
			IntRange baseGasDensity,
			Optional<List<Direction>> directions) {
		this(target, result, minGas, densityChange, createsGasBlock, baseGasDensity, directions);
		this.id = id;
	}
	*///?}

	public BlockCondition getTarget() {
		return target;
	}

	public Optional<ConversionResult> getResult() {
		return result;
	}

	public Optional<Integer> getMinGas() {
		return minGas;
	}

	public IntRange getDensityChange() {
		return densityChange;
	}

	public boolean createsGasBlock() {
		return createsGasBlock;
	}

	public IntRange getBaseGasDensity() {
		return baseGasDensity;
	}

	public Optional<List<Direction>> getDirections() {
		return directions;
	}

	public boolean matches(Direction dir, int originatingDensity, BlockState targetState) {
		if (minGas.isPresent() && originatingDensity < minGas.get()) {
			return false;
		}
		if (directions.isPresent() && !directions.get().contains(dir)) {
			return false;
		}
		if (!target.test(targetState)) {
			return false;
		}
		return true;
	}

	public int rollDensityChange(RandomSource random) {
		return densityChange.roll(random);
	}

	public int rollBaseGasDensity(RandomSource random) {
		return baseGasDensity.roll(random);
	}

	public void apply(Level level, BlockPos pos) {
		result.ifPresent(r -> r.apply(level, pos));
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
		return ModRecipeTypes.BIOCIDE_CONVERSION.getSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.BIOCIDE_CONVERSION.getType();
	}

	public void toNetwork(FriendlyByteBuf buf) {
		target.toNetwork(buf);
		buf.writeBoolean(result.isPresent());
		result.ifPresent(r -> r.toNetwork(buf));
		buf.writeBoolean(minGas.isPresent());
		minGas.ifPresent(buf::writeVarInt);
		densityChange.toNetwork(buf);
		buf.writeBoolean(createsGasBlock);
		baseGasDensity.toNetwork(buf);
		buf.writeBoolean(directions.isPresent());
		directions.ifPresent(dirs -> {
			buf.writeVarInt(dirs.size());
			for (Direction d : dirs) {
				buf.writeVarInt(d.get3DDataValue());
			}
		});
	}

	public static BiocideConversionRecipe fromNetwork(FriendlyByteBuf buf) {
		BlockCondition target = BlockCondition.fromNetwork(buf);
		Optional<ConversionResult> result = buf.readBoolean() ? Optional.of(ConversionResult.fromNetwork(buf)) : Optional.empty();
		Optional<Integer> minGas = buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
		IntRange densityChange = IntRange.fromNetwork(buf);
		boolean createsGasBlock = buf.readBoolean();
		IntRange baseGasDensity = IntRange.fromNetwork(buf);
		Optional<List<Direction>> directions;
		if (buf.readBoolean()) {
			int count = buf.readVarInt();
			List<Direction> dirs = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				dirs.add(Direction.from3DDataValue(buf.readVarInt()));
			}
			directions = Optional.of(dirs);
		} else {
			directions = Optional.empty();
		}
		return new BiocideConversionRecipe(target, result, minGas, densityChange, createsGasBlock, baseGasDensity, directions);
	}

	//? < 1.21 {
	/*public static BiocideConversionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		BiocideConversionRecipe recipe = fromNetwork(buf);
		recipe.id = id;
		return recipe;
	}
	*///?}

	public static class Serializer implements RecipeSerializer<BiocideConversionRecipe> {
		//? >= 1.21 {
		private static final Codec<List<Direction>> DIRECTIONS_CODEC = Codec.either(
				Direction.CODEC,
				Direction.CODEC.listOf()
		).xmap(
				either -> either.map(List::of, list -> list),
				list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list)
		);

		public static final MapCodec<BiocideConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				BlockCondition.CODEC.fieldOf("target").forGetter(BiocideConversionRecipe::getTarget),
				ConversionResult.CODEC.optionalFieldOf("result").forGetter(BiocideConversionRecipe::getResult),
				Codec.INT.optionalFieldOf("min_gas").forGetter(BiocideConversionRecipe::getMinGas),
				IntRange.CODEC.optionalFieldOf("density_change", IntRange.ZERO).forGetter(BiocideConversionRecipe::getDensityChange),
				Codec.BOOL.optionalFieldOf("creates_gas_block", false).forGetter(BiocideConversionRecipe::createsGasBlock),
				IntRange.CODEC.optionalFieldOf("base_gas_density", IntRange.ZERO).forGetter(BiocideConversionRecipe::getBaseGasDensity),
				DIRECTIONS_CODEC.optionalFieldOf("directions").forGetter(BiocideConversionRecipe::getDirections)
		).apply(inst, BiocideConversionRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BiocideConversionRecipe> STREAM_CODEC = StreamCodec.of(
				(buf, recipe) -> recipe.toNetwork(buf),
				BiocideConversionRecipe::fromNetwork
		);

		@Override
		public MapCodec<BiocideConversionRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BiocideConversionRecipe> streamCodec() {
			return STREAM_CODEC;
		}
		//?} else {
		/*@Override
		public BiocideConversionRecipe fromJson(ResourceLocation id, JsonObject json) {
			BlockCondition target = BlockCondition.fromJson(GsonHelper.getAsJsonObject(json, "target"));
			Optional<ConversionResult> result = json.has("result")
					? Optional.of(ConversionResult.fromJson(json.get("result")))
					: Optional.empty();
			Optional<Integer> minGas = json.has("min_gas")
					? Optional.of(GsonHelper.getAsInt(json, "min_gas"))
					: (json.has("min_density") ? Optional.of(GsonHelper.getAsInt(json, "min_density")) : Optional.empty());

			IntRange densityChange = IntRange.ZERO;
			if (json.has("density_change")) {
				densityChange = IntRange.fromJson(json.get("density_change"));
			} else if (json.has("density_cost")) {
				int cost = GsonHelper.getAsInt(json, "density_cost");
				densityChange = IntRange.of(-cost);
			} else if (json.has("density_gain")) {
				int gain = GsonHelper.getAsInt(json, "density_gain");
				densityChange = IntRange.of(gain);
			}

			boolean createsGasBlock = GsonHelper.getAsBoolean(json, "creates_gas_block", false);
			IntRange baseGasDensity = json.has("base_gas_density")
					? IntRange.fromJson(json.get("base_gas_density"))
					: IntRange.ZERO;

			Optional<List<Direction>> directions = Optional.empty();
			if (json.has("directions")) {
				JsonElement dEl = json.get("directions");
				if (dEl.isJsonArray()) {
					List<Direction> dirs = new ArrayList<>();
					for (JsonElement e : dEl.getAsJsonArray()) {
						Direction d = Direction.byName(e.getAsString());
						if (d != null) dirs.add(d);
					}
					directions = Optional.of(dirs);
				} else if (dEl.isJsonPrimitive()) {
					Direction d = Direction.byName(dEl.getAsString());
					if (d != null) directions = Optional.of(List.of(d));
				}
			} else if (json.has("direction")) {
				Direction d = Direction.byName(GsonHelper.getAsString(json, "direction"));
				if (d != null) directions = Optional.of(List.of(d));
			}

			return new BiocideConversionRecipe(id, target, result, minGas, densityChange, createsGasBlock, baseGasDensity, directions);
		}

		@Override
		public BiocideConversionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			return BiocideConversionRecipe.fromNetwork(id, buf);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, BiocideConversionRecipe recipe) {
			recipe.toNetwork(buf);
		}
		*///?}
	}
}
