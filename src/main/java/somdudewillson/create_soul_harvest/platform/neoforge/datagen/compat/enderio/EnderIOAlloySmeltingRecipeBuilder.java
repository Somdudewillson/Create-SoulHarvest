package somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.enderio;

//? neoforge {

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.BaseCompatRecipeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnderIOAlloySmeltingRecipeBuilder extends BaseCompatRecipeBuilder<EnderIOAlloySmeltingRecipeBuilder> {
	public static final String MOD_ID = "enderio";
	public static final String RECIPE_TYPE = "enderio:alloy_smelting";
	public static final int DEFAULT_ENERGY = 4800;

	public record SizedInput(int count, @Nullable ResourceLocation item, @Nullable ResourceLocation tag, @Nullable JsonObject rawIngredient) {
		public SizedInput {
			if (count <= 0) {
				throw new IllegalArgumentException("count must be positive");
			}
		}

		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("count", count);
			if (tag != null) {
				obj.addProperty("tag", tag.toString());
			} else if (item != null) {
				obj.addProperty("item", item.toString());
			} else if (rawIngredient != null) {
				rawIngredient.entrySet().forEach(entry -> obj.add(entry.getKey(), entry.getValue()));
			}
			return obj;
		}
	}

	public record OutputEntry(ResourceLocation itemId, int count) {
		public OutputEntry {
			Objects.requireNonNull(itemId, "itemId cannot be null");
			if (count <= 0) {
				throw new IllegalArgumentException("count must be positive");
			}
		}

		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("count", count);
			obj.addProperty("id", itemId.toString());
			return obj;
		}
	}

	private int energy = DEFAULT_ENERGY;
	@Nullable
	private Float experience = null;
	private final List<SizedInput> inputs = new ArrayList<>();
	private OutputEntry output;
	private String outputNameForId;

	protected EnderIOAlloySmeltingRecipeBuilder() {
		super(MOD_ID);
	}

	public static EnderIOAlloySmeltingRecipeBuilder alloySmelting() {
		return new EnderIOAlloySmeltingRecipeBuilder();
	}

	public EnderIOAlloySmeltingRecipeBuilder energy(int energy) {
		if (energy <= 0) {
			throw new IllegalArgumentException("energy must be positive");
		}
		this.energy = energy;
		return this;
	}

	public EnderIOAlloySmeltingRecipeBuilder experience(float experience) {
		if (experience < 0.0f) {
			throw new IllegalArgumentException("experience cannot be negative");
		}
		this.experience = experience;
		return this;
	}

	public EnderIOAlloySmeltingRecipeBuilder input(ItemLike item) {
		return input(item, 1);
	}

	public EnderIOAlloySmeltingRecipeBuilder input(ItemLike item, int count) {
		return input(BuiltInRegistries.ITEM.getKey(item.asItem()), count);
	}

	public EnderIOAlloySmeltingRecipeBuilder input(ResourceLocation itemId) {
		return input(itemId, 1);
	}

	public EnderIOAlloySmeltingRecipeBuilder input(ResourceLocation itemId, int count) {
		validateInputCount();
		this.inputs.add(new SizedInput(count, itemId, null, null));
		return this;
	}

	public EnderIOAlloySmeltingRecipeBuilder input(TagKey<Item> tag) {
		return input(tag, 1);
	}

	public EnderIOAlloySmeltingRecipeBuilder input(TagKey<Item> tag, int count) {
		validateInputCount();
		this.inputs.add(new SizedInput(count, null, tag.location(), null));
		return this;
	}

	public EnderIOAlloySmeltingRecipeBuilder input(Ingredient ingredient, int count) {
		validateInputCount();
		JsonElement elem = Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow();
		if (!elem.isJsonObject()) {
			throw new IllegalArgumentException("Ingredient did not encode to a JsonObject");
		}
		this.inputs.add(new SizedInput(count, null, null, elem.getAsJsonObject()));
		return this;
	}

	public EnderIOAlloySmeltingRecipeBuilder input(JsonObject inputJson) {
		validateInputCount();
		int count = inputJson.has("count") ? inputJson.get("count").getAsInt() : 1;
		ResourceLocation tag = inputJson.has("tag") ? ResourceLocation.parse(inputJson.get("tag").getAsString()) : null;
		ResourceLocation item = inputJson.has("item") ? ResourceLocation.parse(inputJson.get("item").getAsString()) : null;
		this.inputs.add(new SizedInput(count, item, tag, null));
		return this;
	}

	private void validateInputCount() {
		if (this.inputs.size() >= 3) {
			throw new IllegalStateException("Ender IO alloy smelting recipes support a maximum of 3 inputs");
		}
	}

	public EnderIOAlloySmeltingRecipeBuilder output(ItemLike item) {
		return output(item, 1);
	}

	public EnderIOAlloySmeltingRecipeBuilder output(ItemLike item, int count) {
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item.asItem());
		return output(itemId, count);
	}

	public EnderIOAlloySmeltingRecipeBuilder output(ResourceLocation itemId) {
		return output(itemId, 1);
	}

	public EnderIOAlloySmeltingRecipeBuilder output(ResourceLocation itemId, int count) {
		this.output = new OutputEntry(itemId, count);
		this.outputNameForId = itemId.getPath();
		return this;
	}

	@Override
	public JsonObject serializeRecipe() {
		if (this.inputs.isEmpty()) {
			throw new IllegalStateException("Ender IO alloy smelting recipe must define at least one input");
		}
		if (this.output == null) {
			throw new IllegalStateException("Ender IO alloy smelting recipe must define an output");
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", RECIPE_TYPE);
		json.addProperty("energy", this.energy);
		if (this.experience != null) {
			json.addProperty("experience", this.experience);
		}

		JsonArray inputArray = new JsonArray();
		for (SizedInput in : this.inputs) {
			inputArray.add(in.toJson());
		}
		json.add("inputs", inputArray);
		json.add("output", this.output.toJson());

		return json;
	}

	@Override
	public ResourceLocation getDefaultId() {
		String suffix = this.outputNameForId != null ? this.outputNameForId : "alloy_smelting";
		return SoulHarvestMod.id("compat/enderio/alloy_smelting/" + suffix);
	}
}
//?}
