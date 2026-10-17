package somdudewillson.create_soul_harvest.content.recipe.ingredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import lombok.Getter;

//? >= 1.21 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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
import somdudewillson.create_soul_harvest.content.item.SoulBookItem;
import somdudewillson.create_soul_harvest.registry.ModIngredients;
import somdudewillson.create_soul_harvest.registry.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
//? >= 1.21 {
public class SoulBookIngredient implements ICustomIngredient {
//?} else {
/*public class SoulBookIngredient extends AbstractIngredient {
*///?}

	private final ResourceLocation soul;
	private final int minTier;

	//? >= 1.21 {
	public static final MapCodec<SoulBookIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.fieldOf("soul").forGetter(SoulBookIngredient::getSoul),
			Codec.INT.optionalFieldOf("min_tier", 1).forGetter(SoulBookIngredient::getMinTier)
	).apply(inst, SoulBookIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SoulBookIngredient> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, SoulBookIngredient::getSoul,
			ByteBufCodecs.VAR_INT, SoulBookIngredient::getMinTier,
			SoulBookIngredient::new
	);
	//?} else {
	/*public static final IIngredientSerializer<SoulBookIngredient> SERIALIZER = new IIngredientSerializer<>() {
		@Override
		public SoulBookIngredient parse(FriendlyByteBuf buffer) {
			ResourceLocation soul = buffer.readResourceLocation();
			int minTier = buffer.readVarInt();
			return new SoulBookIngredient(soul, minTier);
		}

		@Override
		public SoulBookIngredient parse(JsonObject json) {
			ResourceLocation soul = ResourceLocation.tryParse(json.get("soul").getAsString());
			int minTier = json.has("min_tier") ? json.get("min_tier").getAsInt() : 1;
			return new SoulBookIngredient(soul, minTier);
		}

		@Override
		public void write(FriendlyByteBuf buffer, SoulBookIngredient ingredient) {
			buffer.writeResourceLocation(ingredient.getSoul());
			buffer.writeVarInt(ingredient.getMinTier());
		}
	};
	*///?}

	public SoulBookIngredient(ResourceLocation soul, int minTier) {
		//? forge {
		/*super(makeDisplayStacks(soul, minTier).stream().map(Ingredient.ItemValue::new));
		*///?}
		this.soul = soul;
		this.minTier = Math.max(1, minTier);
	}

	public SoulBookIngredient(ResourceLocation soul) {
		this(soul, 1);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		if (!(stack.getItem() instanceof SoulBookItem book)) {
			return false;
		}
		if (book.getTier() < minTier) {
			return false;
		}
		return ISoulHolder.get(stack)
				.map(holder -> holder.isComplete() && java.util.Objects.equals(soul, holder.getHeldSoul()))
				.orElse(false);
	}

	//? >= 1.21 {
	@Override
	public Stream<ItemStack> getItems() {
		return makeDisplayStacks(soul, minTier).stream();
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return ModIngredients.SOUL_BOOK.get();
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
		json.addProperty("type", "create_soul_harvest:soul_book");
		json.addProperty("soul", soul.toString());
		json.addProperty("min_tier", minTier);
		return json;
	}
	*///?}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul, int minTier) {
		List<ItemStack> stacks = new ArrayList<>();
		int startTier = Math.max(1, minTier);
		for (int tier = startTier; tier <= 3; tier++) {
			SoulBookItem bookItem = ModItems.getSoulBook(tier);
			if (bookItem != null) {
				ItemStack stack = new ItemStack(bookItem);
				ISoulHolder.get(stack).ifPresent(h -> {
					h.setHeldSoul(soul);
					h.setSoulPortion(100);
				});
				stacks.add(stack);
			}
		}
		return stacks;
	}
}
