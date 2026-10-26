package somdudewillson.create_soul_harvest.content.recipe.condition;

import net.minecraft.resources.ResourceLocation;

//? neoforge {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.conditions.ICondition;
//?} forge {
/*import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
*///?}

//? neoforge {
public record FluidExistsCondition(ResourceLocation fluid) implements ICondition {
	public static final MapCodec<FluidExistsCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder.group(
					ResourceLocation.CODEC.fieldOf("fluid").forGetter(FluidExistsCondition::fluid)
			).apply(builder, FluidExistsCondition::new)
	);

	public FluidExistsCondition(String namespace, String path) {
		this(ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	@Override
	public boolean test(IContext context) {
		return BuiltInRegistries.FLUID.containsKey(fluid);
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return "fluid_exists(\"" + fluid + "\")";
	}
}
//?} forge {
/*public class FluidExistsCondition implements ICondition {
	public static final ResourceLocation NAME = SoulHarvestMod.id("fluid_exists");
	private final ResourceLocation fluid;

	public FluidExistsCondition(ResourceLocation fluid) {
		this.fluid = fluid;
	}

	public FluidExistsCondition(String namespace, String path) {
		this(new ResourceLocation(namespace, path));
	}

	public ResourceLocation fluid() {
		return fluid;
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test(IContext context) {
		return ForgeRegistries.FLUIDS.containsKey(fluid);
	}

	@Override
	public String toString() {
		return "fluid_exists(\"" + fluid + "\")";
	}

	public static class Serializer implements IConditionSerializer<FluidExistsCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, FluidExistsCondition value) {
			json.addProperty("fluid", value.fluid.toString());
		}

		@Override
		public FluidExistsCondition read(JsonObject json) {
			return new FluidExistsCondition(new ResourceLocation(GsonHelper.getAsString(json, "fluid")));
		}

		@Override
		public ResourceLocation getID() {
			return NAME;
		}
	}
}
*///?}
