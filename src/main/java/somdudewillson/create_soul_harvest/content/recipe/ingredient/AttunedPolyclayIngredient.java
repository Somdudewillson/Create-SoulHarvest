package somdudewillson.create_soul_harvest.content.recipe.ingredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import lombok.Getter;

//? >= 1.21 {
import com.mojang.serialization.Codec;
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
	@Nullable
	private final Boolean enhanced;

	//? >= 1.21 {
	public static final MapCodec<AttunedPolyclayIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.fieldOf("soul").forGetter(AttunedPolyclayIngredient::getSoul),
			BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(i -> Optional.ofNullable(i.getItem())),
			Codec.BOOL.optionalFieldOf("enhanced").forGetter(i -> Optional.ofNullable(i.getEnhanced()))
	).apply(inst, (soul, itemOpt, enhOpt) -> new AttunedPolyclayIngredient(soul, itemOpt.orElse(null), enhOpt.orElse(null))));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttunedPolyclayIngredient> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, AttunedPolyclayIngredient::getSoul,
			ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ITEM)), i -> Optional.ofNullable(i.getItem()),
			ByteBufCodecs.optional(ByteBufCodecs.BOOL), i -> Optional.ofNullable(i.getEnhanced()),
			(soul, itemOpt, enhOpt) -> new AttunedPolyclayIngredient(soul, itemOpt.orElse(null), enhOpt.orElse(null))
	);
	//?} else {
	/*public static final IIngredientSerializer<AttunedPolyclayIngredient> SERIALIZER = new IIngredientSerializer<>() {
		@Override
		public AttunedPolyclayIngredient parse(FriendlyByteBuf buffer) {
			ResourceLocation soul = buffer.readResourceLocation();
			Item item = buffer.readBoolean() ? ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation()) : null;
			Boolean enhanced = buffer.readBoolean() ? buffer.readBoolean() : null;
			return new AttunedPolyclayIngredient(soul, item, enhanced);
		}

		@Override
		public AttunedPolyclayIngredient parse(JsonObject json) {
			ResourceLocation soul = ResourceLocation.tryParse(json.get("soul").getAsString());
			Item item = json.has("item") ? ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("item").getAsString())) : null;
			Boolean enhanced = json.has("enhanced") ? json.get("enhanced").getAsBoolean() : null;
			return new AttunedPolyclayIngredient(soul, item, enhanced);
		}

		@Override
		public void write(FriendlyByteBuf buffer, AttunedPolyclayIngredient ingredient) {
			buffer.writeResourceLocation(ingredient.getSoul());
			buffer.writeBoolean(ingredient.getItem() != null);
			if (ingredient.getItem() != null) {
				buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(ingredient.getItem()));
			}
			buffer.writeBoolean(ingredient.getEnhanced() != null);
			if (ingredient.getEnhanced() != null) {
				buffer.writeBoolean(ingredient.getEnhanced());
			}
		}
	};
	*///?}

	public AttunedPolyclayIngredient(ResourceLocation soul, @Nullable Item item, @Nullable Boolean enhanced) {
		//? forge {
		/*super(makeDisplayStacks(soul, item, enhanced).stream().map(Ingredient.ItemValue::new));
		*///?}
		this.soul = soul;
		this.item = item;
		this.enhanced = enhanced;
	}

	public AttunedPolyclayIngredient(ResourceLocation soul, @Nullable Item item) {
		this(soul, item, null);
	}

	public AttunedPolyclayIngredient(ResourceLocation soul) {
		this(soul, null, null);
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
		if (enhanced != null) {
			if (ISoulHolder.isEnhanced(stack) != enhanced) {
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
		return makeDisplayStacks(soul, item, enhanced).stream();
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
		if (enhanced != null) {
			json.addProperty("enhanced", enhanced);
		}
		return json;
	}
	*///?}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul, @Nullable Item item, @Nullable Boolean enhanced) {
		boolean isEnh = Boolean.TRUE.equals(enhanced);
		if (item != null) {
			return Collections.singletonList(PolyclayAttunedItem.createAttunedPolyclay(item, soul, isEnh));
		}
		List<ItemStack> list = new ArrayList<>();
		for (Item registered : BuiltInRegistries.ITEM) {
			if (registered instanceof PolyclayAttunedItem) {
				list.add(PolyclayAttunedItem.createAttunedPolyclay(registered, soul, isEnh));
			}
		}
		if (list.isEmpty()) {
			list.add(PolyclayAttunedItem.createAttunedPolyclay(ModItems.POLYCLAY_ATTUNED.get(), soul, isEnh));
		}
		return list;
	}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul, @Nullable Item item) {
		return makeDisplayStacks(soul, item, null);
	}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul) {
		return makeDisplayStacks(soul, null, null);
	}
}
