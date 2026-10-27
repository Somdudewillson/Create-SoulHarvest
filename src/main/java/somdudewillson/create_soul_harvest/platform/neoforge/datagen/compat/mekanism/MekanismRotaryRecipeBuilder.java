package somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.mekanism;

//? neoforge {

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.BaseCompatRecipeBuilder;

import java.util.Objects;

public class MekanismRotaryRecipeBuilder extends BaseCompatRecipeBuilder<MekanismRotaryRecipeBuilder> {
	public static final String MOD_ID = "mekanism";
	public static final String RECIPE_TYPE = "mekanism:rotary";

	private ResourceLocation fluidId;
	@Nullable
	private ResourceLocation fluidTag;
	private ResourceLocation chemicalId;
	private int fluidAmount = 1;
	private int chemicalAmount = 1;

	protected MekanismRotaryRecipeBuilder() {
		super(MOD_ID);
	}

	public static MekanismRotaryRecipeBuilder rotary() {
		return new MekanismRotaryRecipeBuilder();
	}

	public MekanismRotaryRecipeBuilder fluid(Fluid fluid) {
		this.fluidId = BuiltInRegistries.FLUID.getKey(fluid);
		return this;
	}

	public MekanismRotaryRecipeBuilder fluid(ResourceLocation fluidId) {
		this.fluidId = fluidId;
		return this;
	}

	public MekanismRotaryRecipeBuilder fluidTag(TagKey<Fluid> tag) {
		this.fluidTag = tag.location();
		return this;
	}

	public MekanismRotaryRecipeBuilder chemical(ResourceLocation chemicalId) {
		this.chemicalId = chemicalId;
		return this;
	}

	public MekanismRotaryRecipeBuilder amounts(int fluidAmount, int chemicalAmount) {
		this.fluidAmount = fluidAmount;
		this.chemicalAmount = chemicalAmount;
		return this;
	}

	@Override
	public JsonObject serializeRecipe() {
		Objects.requireNonNull(chemicalId, "chemicalId cannot be null");
		if (fluidId == null && fluidTag == null) {
			throw new IllegalStateException("Either fluidId or fluidTag must be set");
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", RECIPE_TYPE);

		// Fluid -> Chemical (Condensentrating)
		JsonObject fluidIn = new JsonObject();
		fluidIn.addProperty("amount", fluidAmount);
		if (fluidTag != null) {
			fluidIn.addProperty("tag", fluidTag.toString());
		} else {
			fluidIn.addProperty("fluid", fluidId.toString());
		}
		json.add("fluid_input", fluidIn);

		JsonObject chemOut = new JsonObject();
		chemOut.addProperty("amount", chemicalAmount);
		chemOut.addProperty("id", chemicalId.toString());
		json.add("chemical_output", chemOut);

		// Chemical -> Fluid (Decondensentrating)
		JsonObject chemIn = new JsonObject();
		chemIn.addProperty("amount", chemicalAmount);
		chemIn.addProperty("chemical", chemicalId.toString());
		json.add("chemical_input", chemIn);

		JsonObject fluidOut = new JsonObject();
		fluidOut.addProperty("amount", fluidAmount);
		fluidOut.addProperty("id", (fluidId != null ? fluidId : fluidTag).toString());
		json.add("fluid_output", fluidOut);

		return json;
	}

	@Override
	public ResourceLocation getDefaultId() {
		String name = chemicalId != null ? chemicalId.getPath() : "rotary";
		return SoulHarvestMod.id("compat/mekanism/rotary/" + name);
	}
}
//?}
