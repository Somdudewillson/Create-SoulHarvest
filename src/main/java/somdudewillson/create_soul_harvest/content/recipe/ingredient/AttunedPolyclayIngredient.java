package somdudewillson.create_soul_harvest.content.recipe.ingredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import lombok.Getter;

//? >= 1.21 {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import java.util.Optional;
//?} else {
/*import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
*///?}

import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.item.PolyclayAttunedItem;
import somdudewillson.create_soul_harvest.registry.ModIngredients;
import somdudewillson.create_soul_harvest.registry.ModItems;

import java.util.ArrayList;
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
	@Nullable
	private final Item item;

	//? >= 1.21 {
	public static final MapCodec<AttunedPolyclayIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.fieldOf("soul").forGetter(AttunedPolyclayIngredient::getSoul),
			BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(i -> Optional.ofNullable(i.getItem()))
	).apply(inst, (soul, itemOpt) -> new AttunedPolyclayIngredient(soul, itemOpt.orElse(null))));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttunedPolyclayIngredient> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, AttunedPolyclayIngredient::getSoul,
			ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ITEM)), i -> Optional.ofNullable(i.getItem()),
			(soul, itemOpt) -> new AttunedPolyclayIngredient(soul, itemOpt.orElse(null))
	);
	//?} else {
	/*public static final IIngredientSerializer<AttunedPolyclayIngredient> SERIALIZER = new IIngredientSerializer<>() {
		@Override
		public AttunedPolyclayIngredient parse(FriendlyByteBuf buffer) {
			ResourceLocation soul = buffer.readResourceLocation();
			Item item = buffer.readBoolean() ? ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation()) : null;
			return new AttunedPolyclayIngredient(soul, item);
		}

		@Override
		public AttunedPolyclayIngredient parse(JsonObject json) {
			ResourceLocation soul = ResourceLocation.tryParse(json.get("soul").getAsString());
			Item item = json.has("item") ? ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("item").getAsString())) : null;
			return new AttunedPolyclayIngredient(soul, item);
		}

		@Override
		public void write(FriendlyByteBuf buffer, AttunedPolyclayIngredient ingredient) {
			buffer.writeResourceLocation(ingredient.getSoul());
			buffer.writeBoolean(ingredient.getItem() != null);
			if (ingredient.getItem() != null) {
				buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(ingredient.getItem()));
			}
		}
	};
	*///?}

	public AttunedPolyclayIngredient(ResourceLocation soul, @Nullable Item item) {
		//? forge {
		/*super(makeDisplayStacks(soul, item).stream().map(Ingredient.ItemValue::new));
		*///?}
		this.soul = soul;
		this.item = item;
	}

	public AttunedPolyclayIngredient(ResourceLocation soul) {
		this(soul, null);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		if (item != null) {
			if (!stack.is(item)) {
				return false;
			}
		} else {
			if (!(stack.getItem() instanceof PolyclayAttunedItem)) {
				return false;
			}
		}
		return ISoulHolder.get(stack)
				.map(holder -> java.util.Objects.equals(soul, holder.getHeldSoul()))
				.orElse(false);
	}

	//? >= 1.21 {
	@Override
	public Stream<ItemStack> getItems() {
		return makeDisplayStacks(soul, item).stream();
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
		if (item != null) {
			json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
		}
		return json;
	}
	*///?}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul, @Nullable Item item) {
		if (item != null) {
			return Collections.singletonList(PolyclayAttunedItem.createAttunedPolyclay(item, soul));
		}
		List<ItemStack> list = new ArrayList<>();
		for (Item registered : BuiltInRegistries.ITEM) {
			if (registered instanceof PolyclayAttunedItem) {
				list.add(PolyclayAttunedItem.createAttunedPolyclay(registered, soul));
			}
		}
		if (list.isEmpty()) {
			list.add(PolyclayAttunedItem.createAttunedPolyclay(ModItems.POLYCLAY_ATTUNED.get(), soul));
		}
		return list;
	}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul) {
		return makeDisplayStacks(soul, null);
	}
}
