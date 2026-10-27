package somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.mekanism;

//? neoforge {

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.BaseCompatRecipeBuilder;

import java.util.Objects;

public class MekanismReactionRecipeBuilder extends BaseCompatRecipeBuilder<MekanismReactionRecipeBuilder> {
	public static final String MOD_ID = "mekanism";
	public static final String RECIPE_TYPE = "mekanism:reaction";

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

	public record FluidInputEntry(int amount, @Nullable ResourceLocation fluid, @Nullable ResourceLocation tag) {
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("amount", amount);
			if (tag != null) {
				obj.addProperty("tag", tag.toString());
			} else if (fluid != null) {
				obj.addProperty("fluid", fluid.toString());
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

	public record ChemicalOutputEntry(ResourceLocation chemicalId, int amount) {
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("amount", amount);
			obj.addProperty("id", chemicalId.toString());
			return obj;
		}
	}

	private ItemInputEntry itemInput;
	private FluidInputEntry fluidInput;
	private ChemicalInputEntry chemicalInput;
	private int duration = 100;
	@Nullable
	private Long energyRequired;
	@Nullable
	private ItemOutputEntry itemOutput;
	@Nullable
	private ChemicalOutputEntry chemicalOutput;

	private String outputNameForId;

	protected MekanismReactionRecipeBuilder() {
		super(MOD_ID);
	}

	public static MekanismReactionRecipeBuilder reaction() {
		return new MekanismReactionRecipeBuilder();
	}

	public MekanismReactionRecipeBuilder itemInput(ItemLike item) {
		return itemInput(item, 1);
	}

	public MekanismReactionRecipeBuilder itemInput(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.itemInput = new ItemInputEntry(count, id, null);
		return this;
	}

	public MekanismReactionRecipeBuilder itemInput(TagKey<Item> tag, int count) {
		this.itemInput = new ItemInputEntry(count, null, tag.location());
		return this;
	}

	public MekanismReactionRecipeBuilder fluidInput(Fluid fluid, int amount) {
		ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
		this.fluidInput = new FluidInputEntry(amount, id, null);
		return this;
	}

	public MekanismReactionRecipeBuilder fluidInput(ResourceLocation fluidId, int amount) {
		this.fluidInput = new FluidInputEntry(amount, fluidId, null);
		return this;
	}

	public MekanismReactionRecipeBuilder fluidInput(TagKey<Fluid> tag, int amount) {
		this.fluidInput = new FluidInputEntry(amount, null, tag.location());
		return this;
	}

	public MekanismReactionRecipeBuilder chemicalInput(ResourceLocation chemicalId, int amount) {
		this.chemicalInput = new ChemicalInputEntry(amount, chemicalId, null);
		return this;
	}

	public MekanismReactionRecipeBuilder chemicalInput(String chemicalId, int amount) {
		return chemicalInput(ResourceLocation.parse(chemicalId), amount);
	}

	public MekanismReactionRecipeBuilder chemicalInputTag(ResourceLocation tagId, int amount) {
		this.chemicalInput = new ChemicalInputEntry(amount, null, tagId);
		return this;
	}

	public MekanismReactionRecipeBuilder duration(int duration) {
		if (duration <= 0) {
			throw new IllegalArgumentException("duration must be positive");
		}
		this.duration = duration;
		return this;
	}

	public MekanismReactionRecipeBuilder energyRequired(long energyRequired) {
		this.energyRequired = energyRequired;
		return this;
	}

	public MekanismReactionRecipeBuilder itemOutput(ItemLike item) {
		return itemOutput(item, 1);
	}

	public MekanismReactionRecipeBuilder itemOutput(ItemLike item, int count) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());
		this.itemOutput = new ItemOutputEntry(id, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = id.getPath();
		}
		return this;
	}

	public MekanismReactionRecipeBuilder itemOutput(ResourceLocation itemId, int count) {
		this.itemOutput = new ItemOutputEntry(itemId, count);
		if (this.outputNameForId == null) {
			this.outputNameForId = itemId.getPath();
		}
		return this;
	}

	public MekanismReactionRecipeBuilder chemicalOutput(ResourceLocation chemicalId, int amount) {
		this.chemicalOutput = new ChemicalOutputEntry(chemicalId, amount);
		if (this.outputNameForId == null) {
			this.outputNameForId = chemicalId.getPath();
		}
		return this;
	}

	public MekanismReactionRecipeBuilder chemicalOutput(String chemicalId, int amount) {
		return chemicalOutput(ResourceLocation.parse(chemicalId), amount);
	}

	@Override
	public JsonObject serializeRecipe() {
		Objects.requireNonNull(itemInput, "itemInput cannot be null");
		Objects.requireNonNull(fluidInput, "fluidInput cannot be null");
		Objects.requireNonNull(chemicalInput, "chemicalInput cannot be null");
		if (itemOutput == null && chemicalOutput == null) {
			throw new IllegalStateException("At least one output (item or chemical) must be specified for PRC recipe");
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", RECIPE_TYPE);
		json.add("item_input", itemInput.toJson());
		json.add("fluid_input", fluidInput.toJson());
		json.add("chemical_input", chemicalInput.toJson());
		json.addProperty("duration", duration);
		if (energyRequired != null && energyRequired > 0) {
			json.addProperty("energy_required", energyRequired);
		}
		if (itemOutput != null) {
			json.add("item_output", itemOutput.toJson());
		}
		if (chemicalOutput != null) {
			json.add("chemical_output", chemicalOutput.toJson());
		}
		return json;
	}

	@Override
	public ResourceLocation getDefaultId() {
		String name = outputNameForId != null ? outputNameForId : "reaction";
		return SoulHarvestMod.id("compat/mekanism/reaction/" + name);
	}
}
//?}
