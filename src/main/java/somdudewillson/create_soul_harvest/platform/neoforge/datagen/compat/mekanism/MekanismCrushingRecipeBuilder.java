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

public class MekanismCrushingRecipeBuilder extends BaseCompatRecipeBuilder<MekanismCrushingRecipeBuilder> {
	public static final String MOD_ID = "mekanism";
	public static final String RECIPE_TYPE = "mekanism:crushing";

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

	private ItemInputEntry input;
	private ItemOutputEntry output;
	private String inputNameForId;
	private String outputNameForId;

	protected MekanismCrushingRecipeBuilder() {
		super(MOD_ID);
	}

	public static MekanismCrushingRecipeBuilder crushing() {
		return new MekanismCrushingRecipeBuilder();
	}

	public MekanismCrushingRecipeBuilder input(ItemLike item) {
		return input(item, 1);
	}

	public MekanismCrushingRecipeBuilder input(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.input = new ItemInputEntry(count, id, null);
		if (this.inputNameForId == null) {
			this.inputNameForId = id.getPath();
		}
		return this;
	}

	public MekanismCrushingRecipeBuilder input(TagKey<Item> tag, int count) {
		this.input = new ItemInputEntry(count, null, tag.location());
		if (this.inputNameForId == null) {
			this.inputNameForId = tag.location().getPath().replace('/', '_');
		}
		return this;
	}

	public MekanismCrushingRecipeBuilder output(ItemLike item) {
		return output(item, 1);
	}

	public MekanismCrushingRecipeBuilder output(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.output = new ItemOutputEntry(id, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = id.getPath();
		}
		return this;
	}

	public MekanismCrushingRecipeBuilder output(ResourceLocation itemId, int count) {
		this.output = new ItemOutputEntry(itemId, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = itemId.getPath();
		}
		return this;
	}

	@Override
	public JsonObject serializeRecipe() {
		Objects.requireNonNull(input, "input cannot be null");
		Objects.requireNonNull(output, "output cannot be null");

		JsonObject json = new JsonObject();
		json.addProperty("type", RECIPE_TYPE);
		json.add("input", input.toJson());
		json.add("output", output.toJson());
		return json;
	}

	@Override
	public ResourceLocation getDefaultId() {
		String in = inputNameForId != null ? inputNameForId : "input";
		String out = outputNameForId != null ? outputNameForId : "output";
		return SoulHarvestMod.id("compat/mekanism/crushing/" + out + "_from_" + in);
	}
}
//?}
