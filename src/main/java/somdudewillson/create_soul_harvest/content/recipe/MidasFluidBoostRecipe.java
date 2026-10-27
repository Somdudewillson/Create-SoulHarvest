package somdudewillson.create_soul_harvest.content.recipe;

import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class MidasFluidBoostRecipe implements Recipe<RecipeInput> {

	private final SizedFluidIngredient fluidIngredient;
	private final int duration;
	private final float multiplier;

	public MidasFluidBoostRecipe(SizedFluidIngredient fluidIngredient, int duration, float multiplier) {
		this.fluidIngredient = fluidIngredient;
		this.duration = duration;
		this.multiplier = multiplier;
	}

	public SizedFluidIngredient getFluidIngredient() {
		return fluidIngredient;
	}

	public int getDuration() {
		return duration;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public int getRequiredAmount() {
		return fluidIngredient.amount();
	}

	public boolean matchesFluid(FluidStack stack) {
		return fluidIngredient.test(stack);
	}

	@Override
	public boolean matches(RecipeInput inv, Level level) {
		return true;
	}

	@Override
	public ItemStack assemble(RecipeInput inv, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.MIDAS_FLUID_BOOST.getSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.MIDAS_FLUID_BOOST.getType();
	}

	public static class Serializer implements RecipeSerializer<MidasFluidBoostRecipe> {
		public static final MapCodec<MidasFluidBoostRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(MidasFluidBoostRecipe::getFluidIngredient),
				Codec.INT.fieldOf("duration").forGetter(MidasFluidBoostRecipe::getDuration),
				Codec.FLOAT.fieldOf("multiplier").forGetter(MidasFluidBoostRecipe::getMultiplier)
		).apply(inst, MidasFluidBoostRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, MidasFluidBoostRecipe> STREAM_CODEC = StreamCodec.composite(
				SizedFluidIngredient.STREAM_CODEC,
				MidasFluidBoostRecipe::getFluidIngredient,
				ByteBufCodecs.VAR_INT,
				MidasFluidBoostRecipe::getDuration,
				ByteBufCodecs.FLOAT,
				MidasFluidBoostRecipe::getMultiplier,
				MidasFluidBoostRecipe::new
		);

		@Override
		public MapCodec<MidasFluidBoostRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MidasFluidBoostRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
//?} else {
/*import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MidasFluidBoostRecipe implements Recipe<Container> {

	private final ResourceLocation id;
	private final Either<TagKey<Fluid>, Fluid> fluidOrTag;
	private final int amount;
	private final int duration;
	private final float multiplier;

	public MidasFluidBoostRecipe(ResourceLocation id, Either<TagKey<Fluid>, Fluid> fluidOrTag, int amount, int duration, float multiplier) {
		this.id = id;
		this.fluidOrTag = fluidOrTag;
		this.amount = amount;
		this.duration = duration;
		this.multiplier = multiplier;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public Either<TagKey<Fluid>, Fluid> getFluidOrTag() {
		return fluidOrTag;
	}

	public int getAmount() {
		return amount;
	}

	public int getRequiredAmount() {
		return amount;
	}

	public int getDuration() {
		return duration;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public boolean matchesFluid(FluidStack stack) {
		if (stack.getAmount() < amount) return false;
		return fluidOrTag.map(
				tag -> stack.getFluid().is(tag),
				fluid -> stack.getFluid() == fluid
		);
	}

	@Override
	public boolean matches(Container inv, Level level) {
		return true;
	}

	@Override
	public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.MIDAS_FLUID_BOOST.getSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.MIDAS_FLUID_BOOST.getType();
	}

	public static class Serializer implements RecipeSerializer<MidasFluidBoostRecipe> {
		@Override
		public MidasFluidBoostRecipe fromJson(ResourceLocation id, JsonObject json) {
			JsonObject fluidJson = GsonHelper.getAsJsonObject(json, "fluid");
			int amount = GsonHelper.getAsInt(fluidJson, "amount", 1000);
			Either<TagKey<Fluid>, Fluid> fluidOrTag;
			if (fluidJson.has("tag")) {
				ResourceLocation tagLoc = new ResourceLocation(GsonHelper.getAsString(fluidJson, "tag"));
				fluidOrTag = Either.left(TagKey.create(Registries.FLUID, tagLoc));
			} else if (fluidJson.has("fluid")) {
				ResourceLocation fluidLoc = new ResourceLocation(GsonHelper.getAsString(fluidJson, "fluid"));
				Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidLoc);
				if (fluid == null || fluid == Fluids.EMPTY) {
					throw new JsonSyntaxException("Unknown fluid: " + fluidLoc);
				}
				fluidOrTag = Either.right(fluid);
			} else {
				throw new JsonSyntaxException("Midas fluid boost recipe must specify 'tag' or 'fluid' in 'fluid' object");
			}
			int duration = GsonHelper.getAsInt(json, "duration");
			float multiplier = GsonHelper.getAsFloat(json, "multiplier");
			return new MidasFluidBoostRecipe(id, fluidOrTag, amount, duration, multiplier);
		}

		@Override
		public MidasFluidBoostRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			boolean isTag = buf.readBoolean();
			ResourceLocation loc = buf.readResourceLocation();
			Either<TagKey<Fluid>, Fluid> fluidOrTag;
			if (isTag) {
				fluidOrTag = Either.left(TagKey.create(Registries.FLUID, loc));
			} else {
				fluidOrTag = Either.right(ForgeRegistries.FLUIDS.getValue(loc));
			}
			int amount = buf.readVarInt();
			int duration = buf.readVarInt();
			float multiplier = buf.readFloat();
			return new MidasFluidBoostRecipe(id, fluidOrTag, amount, duration, multiplier);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, MidasFluidBoostRecipe recipe) {
			recipe.getFluidOrTag().ifLeft(tag -> {
				buf.writeBoolean(true);
				buf.writeResourceLocation(tag.location());
			}).ifRight(fluid -> {
				buf.writeBoolean(false);
				buf.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(fluid));
			});
			buf.writeVarInt(recipe.getAmount());
			buf.writeVarInt(recipe.getDuration());
			buf.writeFloat(recipe.getMultiplier());
		}
	}
}
*///?}
