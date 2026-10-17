package somdudewillson.create_soul_harvest.content.recipe.ingredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import lombok.Getter;

//? >= 1.21 {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
//?} else {
/*import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
*///?}

import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.item.AttunedPolyclayItem;
import somdudewillson.create_soul_harvest.registry.ModIngredients;
import somdudewillson.create_soul_harvest.registry.ModItems;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Getter
//? >= 1.21 {
public class AttunedPolyclayIngredient implements ICustomIngredient {
//?} else {
/*public class AttunedPolyclayIngredient extends AbstractIngredient {
*///?}

	private final ResourceLocation soul;

	//? >= 1.21 {
	public static final MapCodec<AttunedPolyclayIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.fieldOf("soul").forGetter(AttunedPolyclayIngredient::getSoul)
	).apply(inst, AttunedPolyclayIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttunedPolyclayIngredient> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, AttunedPolyclayIngredient::getSoul,
			AttunedPolyclayIngredient::new
	);
	//?} else {
	/*public static final IIngredientSerializer<AttunedPolyclayIngredient> SERIALIZER = new IIngredientSerializer<>() {
		@Override
		public AttunedPolyclayIngredient parse(FriendlyByteBuf buffer) {
			ResourceLocation soul = buffer.readResourceLocation();
			return new AttunedPolyclayIngredient(soul);
		}

		@Override
		public AttunedPolyclayIngredient parse(JsonObject json) {
			ResourceLocation soul = ResourceLocation.tryParse(json.get("soul").getAsString());
			return new AttunedPolyclayIngredient(soul);
		}

		@Override
		public void write(FriendlyByteBuf buffer, AttunedPolyclayIngredient ingredient) {
			buffer.writeResourceLocation(ingredient.getSoul());
		}
	};
	*///?}

	public AttunedPolyclayIngredient(ResourceLocation soul) {
		//? forge {
		/*super(makeDisplayStacks(soul).stream().map(Ingredient.ItemValue::new));
		*///?}
		this.soul = soul;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		if (!stack.is(ModItems.ATTUNED_POLYCLAY.get())) {
			return false;
		}
		return ISoulHolder.get(stack)
				.map(holder -> java.util.Objects.equals(soul, holder.getHeldSoul()))
				.orElse(false);
	}

	//? >= 1.21 {
	@Override
	public Stream<ItemStack> getItems() {
		return makeDisplayStacks(soul).stream();
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return ModIngredients.ATTUNED_POLYCLAY.get();
	}
	//?} else {
	/*@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public com.google.gson.JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "create_soul_harvest:attuned_polyclay");
		json.addProperty("soul", soul.toString());
		return json;
	}
	*///?}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul) {
		ItemStack stack = AttunedPolyclayItem.createAttunedPolyclay(ModItems.ATTUNED_POLYCLAY.get(), soul);
		return Collections.singletonList(stack);
	}
}
