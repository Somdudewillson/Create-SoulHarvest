package somdudewillson.create_soul_harvest.content.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;

//? >= 1.21 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public class UniformSequencedAssemblyRecipeSerializer extends SequencedAssemblyRecipeSerializer {
	private MapCodec<SequencedAssemblyRecipe> codec;
	private StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> streamCodec;

	public UniformSequencedAssemblyRecipeSerializer() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public MapCodec<SequencedAssemblyRecipe> codec() {
		if (this.codec == null) {
			MapCodec<SequencedAssemblyRecipe> baseCodec = (MapCodec<SequencedAssemblyRecipe>) (MapCodec<?>) AllRecipeTypes.SEQUENCED_ASSEMBLY.getSerializer().codec();
			MapCodec<UniformSequencedAssemblyRecipe> uniformCodec = RecordCodecBuilder.mapCodec(instance ->
				instance.group(
					baseCodec.forGetter(r -> r),
					Codec.STRING.optionalFieldOf("tooltip").forGetter(r -> Optional.ofNullable(r.getTooltipKey()))
				).apply(instance, (base, tooltipOpt) -> new UniformSequencedAssemblyRecipe(base, tooltipOpt.orElse(null)))
			);
			this.codec = (MapCodec<SequencedAssemblyRecipe>) (MapCodec<?>) uniformCodec;
		}
		return this.codec;
	}

	@Override
	@SuppressWarnings("unchecked")
	public StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> streamCodec() {
		if (this.streamCodec == null) {
			StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> baseStreamCodec = (StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe>) (StreamCodec<?, ?>) AllRecipeTypes.SEQUENCED_ASSEMBLY.getSerializer().streamCodec();
			StreamCodec<RegistryFriendlyByteBuf, Optional<String>> tooltipCodec = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).cast();
			StreamCodec<RegistryFriendlyByteBuf, UniformSequencedAssemblyRecipe> uniformStreamCodec = StreamCodec.composite(
				baseStreamCodec,
				r -> r,
				tooltipCodec,
				r -> Optional.ofNullable(r.getTooltipKey()),
				(base, tooltipOpt) -> new UniformSequencedAssemblyRecipe(base, tooltipOpt.orElse(null))
			);
			this.streamCodec = (StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe>) (StreamCodec<?, ?>) uniformStreamCodec;
		}
		return this.streamCodec;
	}
}
//?} else {

/*import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class UniformSequencedAssemblyRecipeSerializer extends SequencedAssemblyRecipeSerializer {

	@Override
	protected SequencedAssemblyRecipe readFromJson(ResourceLocation id, JsonObject json) {
		SequencedAssemblyRecipeSerializer baseSerializer = AllRecipeTypes.SEQUENCED_ASSEMBLY.getSerializer();
		SequencedAssemblyRecipe base = baseSerializer.fromJson(id, json);
		String tooltip = GsonHelper.getAsString(json, "tooltip", null);
		return new UniformSequencedAssemblyRecipe(base, tooltip);
	}

	@Override
	protected SequencedAssemblyRecipe readFromBuffer(ResourceLocation id, FriendlyByteBuf buffer) {
		SequencedAssemblyRecipeSerializer baseSerializer = AllRecipeTypes.SEQUENCED_ASSEMBLY.getSerializer();
		SequencedAssemblyRecipe base = baseSerializer.fromNetwork(id, buffer);
		String tooltip = buffer.readBoolean() ? buffer.readUtf() : null;
		return new UniformSequencedAssemblyRecipe(base, tooltip);
	}

	@Override
	protected void writeToBuffer(FriendlyByteBuf buffer, SequencedAssemblyRecipe recipe) {
		SequencedAssemblyRecipeSerializer baseSerializer = AllRecipeTypes.SEQUENCED_ASSEMBLY.getSerializer();
		baseSerializer.toNetwork(buffer, recipe);
		if (recipe instanceof UniformSequencedAssemblyRecipe uniformRecipe && uniformRecipe.getTooltipKey() != null) {
			buffer.writeBoolean(true);
			buffer.writeUtf(uniformRecipe.getTooltipKey());
		} else {
			buffer.writeBoolean(false);
		}
	}

	@Override
	protected void writeToJson(JsonObject json, SequencedAssemblyRecipe recipe) {
		super.writeToJson(json, recipe);
		if (recipe instanceof UniformSequencedAssemblyRecipe uniformRecipe && uniformRecipe.getTooltipKey() != null) {
			json.addProperty("tooltip", uniformRecipe.getTooltipKey());
		}
	}
}
*///?}
