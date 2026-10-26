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

public class EnderIOSagMillingRecipeBuilder extends BaseCompatRecipeBuilder<EnderIOSagMillingRecipeBuilder> {
	public static final String MOD_ID = "enderio";
	public static final String RECIPE_TYPE = "enderio:sag_milling";
	public static final int DEFAULT_ENERGY = 2400;

	public enum SagBonusType {
		NONE("none"),
		CHANCE_ONLY("chance_only"),
		MULTIPLY_OUTPUT("multiply_output");

		private final String serializedName;

		SagBonusType(String serializedName) {
			this.serializedName = serializedName;
		}

		public String serializedName() {
			return serializedName;
		}
	}

	public record SagOutput(ResourceLocation itemId, int count, @Nullable Float chance) {
		public SagOutput {
			Objects.requireNonNull(itemId, "itemId cannot be null");
			if (count <= 0) {
				throw new IllegalArgumentException("count must be positive");
			}
			if (chance != null && (chance <= 0.0f || chance > 1.0f)) {
				throw new IllegalArgumentException("chance must be between 0.0 (exclusive) and 1.0 (inclusive)");
			}
		}

		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			if (chance != null && chance < 1.0f) {
				obj.addProperty("chance", chance);
			}
			JsonObject itemObj = new JsonObject();
			itemObj.addProperty("count", count);
			itemObj.addProperty("id", itemId.toString());
			obj.add("item", itemObj);
			return obj;
		}
	}

	private JsonObject inputJson;
	private String inputNameForId;
	private int energy = DEFAULT_ENERGY;
	@Nullable
	private String bonus;
	private final List<SagOutput> outputs = new ArrayList<>();

	protected EnderIOSagMillingRecipeBuilder() {
		super(MOD_ID);
	}

	public static EnderIOSagMillingRecipeBuilder sagMilling() {
		return new EnderIOSagMillingRecipeBuilder();
	}

	public EnderIOSagMillingRecipeBuilder energy(int energy) {
		if (energy <= 0) {
			throw new IllegalArgumentException("energy must be positive");
		}
		this.energy = energy;
		return this;
	}

	public EnderIOSagMillingRecipeBuilder bonus(SagBonusType bonusType) {
		this.bonus = bonusType != null ? bonusType.serializedName() : null;
		return this;
	}

	public EnderIOSagMillingRecipeBuilder bonus(String bonus) {
		this.bonus = bonus;
		return this;
	}

	public EnderIOSagMillingRecipeBuilder input(ItemLike item) {
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item.asItem());
		return input(itemId);
	}

	public EnderIOSagMillingRecipeBuilder input(ResourceLocation itemId) {
		JsonObject obj = new JsonObject();
		obj.addProperty("item", itemId.toString());
		this.inputJson = obj;
		this.inputNameForId = itemId.getPath();
		return this;
	}

	public EnderIOSagMillingRecipeBuilder input(TagKey<Item> tag) {
		JsonObject obj = new JsonObject();
		obj.addProperty("tag", tag.location().toString());
		this.inputJson = obj;
		this.inputNameForId = tag.location().getPath().replace('/', '_');
		return this;
	}

	public EnderIOSagMillingRecipeBuilder input(Ingredient ingredient) {
		JsonElement elem = Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow();
		if (!elem.isJsonObject()) {
			throw new IllegalArgumentException("Ingredient did not encode to a JsonObject");
		}
		this.inputJson = elem.getAsJsonObject();
		return this;
	}

	public EnderIOSagMillingRecipeBuilder input(JsonObject inputJson) {
		this.inputJson = Objects.requireNonNull(inputJson, "inputJson cannot be null");
		return this;
	}

	public EnderIOSagMillingRecipeBuilder output(ItemLike item) {
		return output(item, 1);
	}

	public EnderIOSagMillingRecipeBuilder output(ItemLike item, int count) {
		return output(BuiltInRegistries.ITEM.getKey(item.asItem()), count, null);
	}

	public EnderIOSagMillingRecipeBuilder output(ItemLike item, int count, float chance) {
		return output(BuiltInRegistries.ITEM.getKey(item.asItem()), count, chance);
	}

	public EnderIOSagMillingRecipeBuilder output(ResourceLocation itemId) {
		return output(itemId, 1);
	}

	public EnderIOSagMillingRecipeBuilder output(ResourceLocation itemId, int count) {
		return output(itemId, count, null);
	}

	public EnderIOSagMillingRecipeBuilder output(ResourceLocation itemId, int count, @Nullable Float chance) {
		if (this.outputs.size() >= 4) {
			throw new IllegalStateException("Ender IO SAG mill recipes support a maximum of 4 outputs");
		}
		this.outputs.add(new SagOutput(itemId, count, chance));
		return this;
	}

	@Override
	public JsonObject serializeRecipe() {
		if (this.inputJson == null) {
			throw new IllegalStateException("Ender IO SAG mill recipe must define an input");
		}
		if (this.outputs.isEmpty()) {
			throw new IllegalStateException("Ender IO SAG mill recipe must define at least one output");
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", RECIPE_TYPE);
		if (this.bonus != null && !this.bonus.isBlank()) {
			json.addProperty("bonus", this.bonus);
		}
		json.addProperty("energy", this.energy);
		json.add("input", this.inputJson);

		JsonArray outputArray = new JsonArray();
		for (SagOutput out : this.outputs) {
			outputArray.add(out.toJson());
		}
		json.add("outputs", outputArray);

		return json;
	}

	@Override
	public ResourceLocation getDefaultId() {
		String suffix = this.inputNameForId != null ? this.inputNameForId :
				(this.outputs.isEmpty() ? "unknown" : this.outputs.get(0).itemId().getPath());
		return SoulHarvestMod.id("compat/enderio/sag_milling/" + suffix);
	}
}
//?}
