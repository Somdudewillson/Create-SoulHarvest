package somdudewillson.create_soul_harvest.content.recipe;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
*///?}

//? >= 1.21 {
public class AbsoluteIceRecipe implements Recipe<RecipeInput> {
//?} else {
/*public class AbsoluteIceRecipe implements Recipe<Container> {
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
}
