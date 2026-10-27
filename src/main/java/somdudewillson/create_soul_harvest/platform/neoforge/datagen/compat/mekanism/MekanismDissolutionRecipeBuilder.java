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

public class MekanismDissolutionRecipeBuilder extends BaseCompatRecipeBuilder<MekanismDissolutionRecipeBuilder> {
	public static final String MOD_ID = "mekanism";
	public static final String RECIPE_TYPE = "mekanism:dissolution";

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

	public record ChemicalOutputEntry(ResourceLocation chemicalId, int amount) {
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("amount", amount);
			obj.addProperty("id", chemicalId.toString());
			return obj;
		}
	}

	private ItemInputEntry itemInput;
	private ChemicalInputEntry chemicalInput;
	private ChemicalOutputEntry output;
	private boolean perTickUsage = true;
	private String outputNameForId;

	protected MekanismDissolutionRecipeBuilder() {
		super(MOD_ID);
	}

	public static MekanismDissolutionRecipeBuilder dissolution() {
		return new MekanismDissolutionRecipeBuilder();
	}

	public MekanismDissolutionRecipeBuilder itemInput(ItemLike item) {
		return itemInput(item, 1);
	}

	public MekanismDissolutionRecipeBuilder itemInput(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.itemInput = new ItemInputEntry(count, id, null);
		return this;
	}

	public MekanismDissolutionRecipeBuilder itemInput(TagKey<Item> tag, int count) {
		this.itemInput = new ItemInputEntry(count, null, tag.location());
		return this;
	}

	public MekanismDissolutionRecipeBuilder chemicalInput(ResourceLocation chemicalId, int amount) {
		this.chemicalInput = new ChemicalInputEntry(amount, chemicalId, null);
		return this;
	}

	public MekanismDissolutionRecipeBuilder chemicalInput(String chemicalId, int amount) {
		return chemicalInput(ResourceLocation.parse(chemicalId), amount);
	}

	public MekanismDissolutionRecipeBuilder chemicalInputTag(ResourceLocation tagId, int amount) {
		this.chemicalInput = new ChemicalInputEntry(amount, null, tagId);
		return this;
	}

	public MekanismDissolutionRecipeBuilder output(ResourceLocation chemicalId, int amount) {
		this.output = new ChemicalOutputEntry(chemicalId, amount);
		if (this.outputNameForId == null) {
			this.outputNameForId = chemicalId.getPath();
		}
		return this;
	}

	public MekanismDissolutionRecipeBuilder output(String chemicalId, int amount) {
		return output(ResourceLocation.parse(chemicalId), amount);
	}

	public MekanismDissolutionRecipeBuilder perTickUsage(boolean perTickUsage) {
		this.perTickUsage = perTickUsage;
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
		String out = outputNameForId != null ? outputNameForId : "dissolved";
		return SoulHarvestMod.id("compat/mekanism/dissolution/" + out);
	}
}
//?}
