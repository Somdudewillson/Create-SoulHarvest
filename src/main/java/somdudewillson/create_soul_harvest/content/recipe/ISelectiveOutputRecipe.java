package somdudewillson.create_soul_harvest.content.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

//? >= 1.21 {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
*///?}

public interface ISelectiveOutputRecipe {

	OutputSelectionMode getOutputSelectionMode();

	void setOutputSelectionMode(OutputSelectionMode mode);

	default boolean isSingleOutput() {
		return getOutputSelectionMode() == OutputSelectionMode.SINGLE;
	}

	default List<ItemStack> rollResults(List<ProcessingOutput> rollableResults, RandomSource randomSource) {
		if (getOutputSelectionMode() == OutputSelectionMode.SINGLE) {
			return rollSingleOutput(rollableResults, randomSource);
		}
		List<ItemStack> results = new ArrayList<>();
		for (ProcessingOutput output : rollableResults) {
			//? >= 1.21 {
			ItemStack stack = output.rollOutput(randomSource);
			//?} else {
			/*ItemStack stack = output.rollOutput();
			*///?}
			if (!stack.isEmpty())
				results.add(stack);
		}
		return results;
	}

	//? < 1.21 {
	/*default List<ItemStack> rollResults(List<ProcessingOutput> rollableResults) {
		return rollResults(rollableResults, RandomSource.create());
	}
	*///?}

	default List<ItemStack> rollSingleOutput(List<ProcessingOutput> rollableResults, RandomSource randomSource) {
		if (rollableResults.isEmpty())
			return Collections.emptyList();

		float totalWeight = 0f;
		for (ProcessingOutput output : rollableResults) {
			totalWeight += output.getChance();
		}
		if (totalWeight <= 0f)
			return Collections.emptyList();

		float range = Math.max(1.0f, totalWeight);
		float roll = randomSource.nextFloat() * range;
		float current = 0f;
		for (ProcessingOutput output : rollableResults) {
			current += output.getChance();
			if (roll < current) {
				ItemStack stack = output.getStack().copy();
				return stack.isEmpty() ? Collections.emptyList() : List.of(stack);
			}
		}
		return Collections.emptyList();
	}

	//? >= 1.21 {
	@FunctionalInterface
	interface Factory<R extends StandardProcessingRecipe<?> & ISelectiveOutputRecipe>
			extends StandardProcessingRecipe.Factory<R> {
		@Override
		R create(ProcessingRecipeParams params);
	}

	class Builder<R extends StandardProcessingRecipe<?> & ISelectiveOutputRecipe>
			extends StandardProcessingRecipe.Builder<R> {
		protected OutputSelectionMode outputSelectionMode = OutputSelectionMode.INDEPENDENT;

		public Builder(Factory<R> factory, ResourceLocation recipeId) {
			super(factory, recipeId);
		}

		public Builder<R> outputSelectionMode(OutputSelectionMode mode) {
			this.outputSelectionMode = mode;
			return this;
		}

		public Builder<R> singleOutput() {
			return outputSelectionMode(OutputSelectionMode.SINGLE);
		}

		public Builder<R> independentOutputs() {
			return outputSelectionMode(OutputSelectionMode.INDEPENDENT);
		}

		@Override
		public R build() {
			R recipe = super.build();
			recipe.setOutputSelectionMode(this.outputSelectionMode);
			return recipe;
		}
	}

	class Serializer<R extends StandardProcessingRecipe<?> & ISelectiveOutputRecipe>
			extends StandardProcessingRecipe.Serializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(Factory<R> factory) {
			super(factory);
			MapCodec<R> baseCodec = ProcessingRecipe.codec(factory, ProcessingRecipeParams.CODEC);
			this.codec = RecordCodecBuilder.mapCodec(instance ->
				instance.group(
					baseCodec.forGetter(r -> r),
					OutputSelectionMode.CODEC.optionalFieldOf("output_mode", OutputSelectionMode.INDEPENDENT)
						.forGetter(ISelectiveOutputRecipe::getOutputSelectionMode)
				).apply(instance, (recipe, mode) -> {
					recipe.setOutputSelectionMode(mode);
					return recipe;
				})
			);
			StreamCodec<RegistryFriendlyByteBuf, R> baseStream = ProcessingRecipe.streamCodec(factory, ProcessingRecipeParams.STREAM_CODEC);
			this.streamCodec = StreamCodec.composite(
				baseStream,
				r -> r,
				OutputSelectionMode.STREAM_CODEC,
				ISelectiveOutputRecipe::getOutputSelectionMode,
				(recipe, mode) -> {
					recipe.setOutputSelectionMode(mode);
					return recipe;
				}
			);
		}

		@Override
		public MapCodec<R> codec() {
			return codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
			return streamCodec;
		}
	}
	//?} else {
	/*@FunctionalInterface
	interface Factory<R extends ProcessingRecipe<?> & ISelectiveOutputRecipe>
			extends ProcessingRecipeFactory<R> {
		@Override
		R create(ProcessingRecipeParams params);
	}

	class Builder<R extends ProcessingRecipe<?> & ISelectiveOutputRecipe>
			extends ProcessingRecipeBuilder<R> {
		protected OutputSelectionMode outputSelectionMode = OutputSelectionMode.INDEPENDENT;

		public Builder(ProcessingRecipeFactory<R> factory, ResourceLocation recipeId) {
			super(factory, recipeId);
		}

		public Builder<R> outputSelectionMode(OutputSelectionMode mode) {
			this.outputSelectionMode = mode;
			return this;
		}

		public Builder<R> singleOutput() {
			return outputSelectionMode(OutputSelectionMode.SINGLE);
		}

		public Builder<R> independentOutputs() {
			return outputSelectionMode(OutputSelectionMode.INDEPENDENT);
		}

		@Override
		public R build() {
			R recipe = super.build();
			recipe.setOutputSelectionMode(this.outputSelectionMode);
			return recipe;
		}
	}

	class Serializer<R extends ProcessingRecipe<?> & ISelectiveOutputRecipe>
			extends ProcessingRecipeSerializer<R> {

		public Serializer(ProcessingRecipeFactory<R> factory) {
			super(factory);
		}

		@Override
		protected R readFromJson(ResourceLocation id, JsonObject json) {
			R recipe = super.readFromJson(id, json);
			if (json.has("output_mode")) {
				recipe.setOutputSelectionMode(OutputSelectionMode.fromString(GsonHelper.getAsString(json, "output_mode")));
			}
			return recipe;
		}

		@Override
		protected R readFromBuffer(ResourceLocation id, FriendlyByteBuf buffer) {
			R recipe = super.readFromBuffer(id, buffer);
			recipe.setOutputSelectionMode(buffer.readEnum(OutputSelectionMode.class));
			return recipe;
		}

		@Override
		protected void writeToBuffer(FriendlyByteBuf buffer, R recipe) {
			super.writeToBuffer(buffer, recipe);
			buffer.writeEnum(recipe.getOutputSelectionMode());
		}

		@Override
		protected void writeToJson(JsonObject json, R recipe) {
			super.writeToJson(json, recipe);
			if (recipe.getOutputSelectionMode() != OutputSelectionMode.INDEPENDENT) {
				json.addProperty("output_mode", recipe.getOutputSelectionMode().getSerializedName());
			}
		}
	}
	*///?}
}
