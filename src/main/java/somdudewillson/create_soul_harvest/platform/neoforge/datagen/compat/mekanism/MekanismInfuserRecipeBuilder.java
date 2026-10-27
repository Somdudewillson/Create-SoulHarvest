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

public class MekanismInfuserRecipeBuilder extends BaseCompatRecipeBuilder<MekanismInfuserRecipeBuilder> {
	public static final String MOD_ID = "mekanism";
	public static final String RECIPE_TYPE = "mekanism:metallurgic_infusing";

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

	public record ChemicalInputEntry(int amount, @Nullable ResourceLocation chemical, @Nullable ResourceLocation tag) {
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("amount", amount);
			if (tag != null) {
				obj.addProperty("tag", tag.toString());
			} else if (chemical != null) {
				obj.addProperty("chemical", chemical.toString());
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

	private ItemInputEntry itemInput;
	private ChemicalInputEntry chemicalInput;
	private ItemOutputEntry output;
	private boolean perTickUsage = false;
	private String outputNameForId;

	protected MekanismInfuserRecipeBuilder() {
		super(MOD_ID);
	}

	public static MekanismInfuserRecipeBuilder metallurgicInfusing() {
		return new MekanismInfuserRecipeBuilder();
	}

	public MekanismInfuserRecipeBuilder itemInput(ItemLike item) {
		return itemInput(item, 1);
	}

	public MekanismInfuserRecipeBuilder itemInput(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.itemInput = new ItemInputEntry(count, id, null);
		return this;
	}

	public MekanismInfuserRecipeBuilder itemInput(TagKey<Item> tag, int count) {
		this.itemInput = new ItemInputEntry(count, null, tag.location());
		return this;
	}

	public MekanismInfuserRecipeBuilder chemicalInput(ResourceLocation chemicalId, int amount) {
		this.chemicalInput = new ChemicalInputEntry(amount, chemicalId, null);
		return this;
	}

	public MekanismInfuserRecipeBuilder chemicalInput(String chemicalId, int amount) {
		return chemicalInput(ResourceLocation.parse(chemicalId), amount);
	}

	public MekanismInfuserRecipeBuilder chemicalInputTag(ResourceLocation tagId, int amount) {
		this.chemicalInput = new ChemicalInputEntry(amount, null, tagId);
		return this;
	}

	public MekanismInfuserRecipeBuilder perTickUsage(boolean perTickUsage) {
		this.perTickUsage = perTickUsage;
		return this;
	}

	public MekanismInfuserRecipeBuilder output(ItemLike item) {
		return output(item, 1);
	}

	public MekanismInfuserRecipeBuilder output(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.output = new ItemOutputEntry(id, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = id.getPath();
		}
		return this;
	}

	public MekanismInfuserRecipeBuilder output(ResourceLocation itemId, int count) {
		this.output = new ItemOutputEntry(itemId, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = itemId.getPath();
		}
		return this;
	}

	@Override
	public JsonObject serializeRecipe() {
		Objects.requireNonNull(itemInput, "itemInput cannot be null");
		Objects.requireNonNull(chemicalInput, "chemicalInput cannot be null");
		Objects.requireNonNull(output, "output cannot be null");

		JsonObject json = new JsonObject();
		json.addProperty("type", RECIPE_TYPE);
		json.add("chemical_input", chemicalInput.toJson());
		json.add("item_input", itemInput.toJson());
		json.add("output", output.toJson());
		json.addProperty("per_tick_usage", perTickUsage);
		return json;
	}

	@Override
	public ResourceLocation getDefaultId() {
		String out = outputNameForId != null ? outputNameForId : "infused";
		return SoulHarvestMod.id("compat/mekanism/metallurgic_infusing/" + out);
	}
}
//?}
