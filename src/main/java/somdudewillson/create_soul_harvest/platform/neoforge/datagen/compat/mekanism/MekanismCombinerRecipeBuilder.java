package somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.mekanism;

//? neoforge {

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.BaseCompatRecipeBuilder;

import java.util.Objects;

public class MekanismCombinerRecipeBuilder extends BaseCompatRecipeBuilder<MekanismCombinerRecipeBuilder> {
	public static final String MOD_ID = "mekanism";
	public static final String RECIPE_TYPE = "mekanism:combining";

	public record ItemInputEntry(int count, @Nullable ResourceLocation item, @Nullable ResourceLocation tag) {
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			if (count > 1) {
				obj.addProperty("count", count);
			} else {
				obj.addProperty("count", 1);
			}
			if (tag != null) {
				obj.addProperty("tag", tag.toString());
			} else if (item != null) {
				obj.addProperty("item", item.toString());
			}
			return obj;
		}
	}

	public record ItemOutputEntry(ResourceLocation itemId, int count) {
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("count", count);
			obj.addProperty("id", itemId.toString());
			return obj;
		}
	}

	private ItemInputEntry mainInput;
	private ItemInputEntry extraInput;
	private ItemOutputEntry output;
	private String outputNameForId;

	protected MekanismCombinerRecipeBuilder() {
		super(MOD_ID);
	}

	public static MekanismCombinerRecipeBuilder combining() {
		return new MekanismCombinerRecipeBuilder();
	}

	public MekanismCombinerRecipeBuilder mainInput(ItemLike item) {
		return mainInput(item, 1);
	}

	public MekanismCombinerRecipeBuilder mainInput(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.mainInput = new ItemInputEntry(count, id, null);
		return this;
	}

	public MekanismCombinerRecipeBuilder mainInput(TagKey<Item> tag, int count) {
		this.mainInput = new ItemInputEntry(count, null, tag.location());
		return this;
	}

	public MekanismCombinerRecipeBuilder extraInput(ItemLike item) {
		return extraInput(item, 1);
	}

	public MekanismCombinerRecipeBuilder extraInput(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.extraInput = new ItemInputEntry(count, id, null);
		return this;
	}

	public MekanismCombinerRecipeBuilder extraInput(TagKey<Item> tag, int count) {
		this.extraInput = new ItemInputEntry(count, null, tag.location());
		return this;
	}

	public MekanismCombinerRecipeBuilder extraInput(ResourceLocation itemId, int count) {
		this.extraInput = new ItemInputEntry(count, itemId, null);
		return this;
	}

	public MekanismCombinerRecipeBuilder output(ItemLike item) {
		return output(item, 1);
	}

	public MekanismCombinerRecipeBuilder output(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.output = new ItemOutputEntry(id, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = id.getPath();
		}
		return this;
	}

	public MekanismCombinerRecipeBuilder output(ResourceLocation itemId, int count) {
		this.output = new ItemOutputEntry(itemId, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = itemId.getPath();
		}
		return this;
	}

	@Override
	public JsonObject serializeRecipe() {
		Objects.requireNonNull(mainInput, "mainInput cannot be null");
		Objects.requireNonNull(extraInput, "extraInput cannot be null");
		Objects.requireNonNull(output, "output cannot be null");

		JsonObject json = new JsonObject();
		json.addProperty("type", RECIPE_TYPE);
		json.add("main_input", mainInput.toJson());
		json.add("extra_input", extraInput.toJson());
		json.add("output", output.toJson());
		return json;
	}

	@Override
	public ResourceLocation getDefaultId() {
		String out = outputNameForId != null ? outputNameForId : "combined";
		return SoulHarvestMod.id("compat/mekanism/combining/" + out);
	}
}
//?}
