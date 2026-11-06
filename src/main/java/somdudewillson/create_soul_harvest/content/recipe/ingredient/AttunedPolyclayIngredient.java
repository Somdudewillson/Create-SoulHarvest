package somdudewillson.create_soul_harvest.content.recipe.ingredient;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import lombok.Getter;

import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.item.PolyclayAttunedItem;
import somdudewillson.create_soul_harvest.registry.ModIngredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
import net.neoforged.neoforge.registries.NeoForgeRegistries;
*///?}

@Getter
//? >= 1.21 {
public class AttunedPolyclayIngredient implements ICustomIngredient {
//?} else {
/*public class AttunedPolyclayIngredient extends AbstractIngredient {
*///?}

	@Nullable
	private final ResourceLocation soul;
	@Nullable
	private final TagKey<EntityType<?>> soulTag;
	private final int minTier;
	@Nullable
	private final Item item;
	@Nullable
	private final Boolean enhanced;

	//? >= 1.21 {
	public static final MapCodec<AttunedPolyclayIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.optionalFieldOf("soul").forGetter(i -> Optional.ofNullable(i.getSoul())),
			TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("soul_tag").forGetter(i -> Optional.ofNullable(i.getSoulTag())),
			Codec.INT.optionalFieldOf("min_tier", 0).forGetter(AttunedPolyclayIngredient::getMinTier),
			BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(i -> Optional.ofNullable(i.getItem())),
			Codec.BOOL.optionalFieldOf("enhanced").forGetter(i -> Optional.ofNullable(i.getEnhanced()))
	).apply(inst, (soulOpt, soulTagOpt, minTier, itemOpt, enhOpt) -> new AttunedPolyclayIngredient(
			soulOpt.orElse(null),
			soulTagOpt.orElse(null),
			minTier,
			itemOpt.orElse(null),
			enhOpt.orElse(null)
	)));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttunedPolyclayIngredient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), i -> Optional.ofNullable(i.getSoul()),
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC.map(loc -> TagKey.create(Registries.ENTITY_TYPE, loc), TagKey::location)), i -> Optional.ofNullable(i.getSoulTag()),
			ByteBufCodecs.VAR_INT, AttunedPolyclayIngredient::getMinTier,
			ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ITEM)), i -> Optional.ofNullable(i.getItem()),
			ByteBufCodecs.optional(ByteBufCodecs.BOOL), i -> Optional.ofNullable(i.getEnhanced()),
			(soulOpt, soulTagOpt, minTier, itemOpt, enhOpt) -> new AttunedPolyclayIngredient(
					soulOpt.orElse(null),
					soulTagOpt.orElse(null),
					minTier,
					itemOpt.orElse(null),
					enhOpt.orElse(null)
			)
	);
	//?} else {
	/*public static final IIngredientSerializer<AttunedPolyclayIngredient> SERIALIZER = new IIngredientSerializer<>() {
		@Override
		public AttunedPolyclayIngredient parse(FriendlyByteBuf buffer) {
			ResourceLocation soul = buffer.readBoolean() ? buffer.readResourceLocation() : null;
			TagKey<EntityType<?>> soulTag = buffer.readBoolean() ? TagKey.create(Registries.ENTITY_TYPE, buffer.readResourceLocation()) : null;
			int minTier = buffer.readVarInt();
			Item item = buffer.readBoolean() ? ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation()) : null;
			Boolean enhanced = buffer.readBoolean() ? buffer.readBoolean() : null;
			return new AttunedPolyclayIngredient(soul, soulTag, minTier, item, enhanced);
		}

		@Override
		public AttunedPolyclayIngredient parse(JsonObject json) {
			ResourceLocation soul = json.has("soul") ? ResourceLocation.tryParse(json.get("soul").getAsString()) : null;
			TagKey<EntityType<?>> soulTag = json.has("soul_tag") ? TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.tryParse(json.get("soul_tag").getAsString())) : null;
			int minTier = json.has("min_tier") ? json.get("min_tier").getAsInt() : 0;
			Item item = json.has("item") ? ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("item").getAsString())) : null;
			Boolean enhanced = json.has("enhanced") ? json.get("enhanced").getAsBoolean() : null;
			return new AttunedPolyclayIngredient(soul, soulTag, minTier, item, enhanced);
		}

		@Override
		public void write(FriendlyByteBuf buffer, AttunedPolyclayIngredient ingredient) {
			buffer.writeBoolean(ingredient.getSoul() != null);
			if (ingredient.getSoul() != null) {
				buffer.writeResourceLocation(ingredient.getSoul());
			}
			buffer.writeBoolean(ingredient.getSoulTag() != null);
			if (ingredient.getSoulTag() != null) {
				buffer.writeResourceLocation(ingredient.getSoulTag().location());
			}
			buffer.writeVarInt(ingredient.getMinTier());
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

	public AttunedPolyclayIngredient(
			@Nullable ResourceLocation soul,
			@Nullable TagKey<EntityType<?>> soulTag,
			int minTier,
			@Nullable Item item,
			@Nullable Boolean enhanced
	) {
		//? forge {
		/*super(makeDisplayStacks(soul, soulTag, minTier, item, enhanced).stream().map(Ingredient.ItemValue::new));
		*///?}
		this.soul = soul;
		this.soulTag = soulTag;
		this.minTier = minTier;
		this.item = item;
		this.enhanced = enhanced;
	}

	public AttunedPolyclayIngredient(ResourceLocation soul, @Nullable Item item, @Nullable Boolean enhanced) {
		this(soul, null, 0, item, enhanced);
	}

	public AttunedPolyclayIngredient(ResourceLocation soul, @Nullable Item item) {
		this(soul, item, null);
	}

	public AttunedPolyclayIngredient(ResourceLocation soul) {
		this(soul, null, null);
	}

	public static AttunedPolyclayIngredient of(EntityType<? extends LivingEntity> entityType) {
		return new AttunedPolyclayIngredient(EntityType.getKey(entityType), null, 0, null, null);
	}

	public static AttunedPolyclayIngredient of(TagKey<EntityType<?>> soulTag, int minTier) {
		return new AttunedPolyclayIngredient(null, soulTag, minTier, null, null);
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
		if (stack.getItem() instanceof PolyclayAttunedItem polyclayItem) {
			if (polyclayItem.getTier() < minTier) {
				return false;
			}
		} else {
			return false;
		}
		if (enhanced != null) {
			if (ISoulHolder.isEnhanced(stack) != enhanced) {
				return false;
			}
		}
		return ISoulHolder.get(stack)
				.map(holder -> {
					ResourceLocation heldSoul = holder.getHeldSoul();
					if (heldSoul == null) {
						return false;
					}
					if (soul != null && !soul.equals(heldSoul)) {
						return false;
					}
					if (soulTag != null) {
						Optional<EntityType<?>> entityType = holder.tryGetHeldSoulEntityType();
						if (entityType.isEmpty() || !entityType.get().is(soulTag)) {
							return false;
						}
					}
					return true;
				})
				.orElse(false);
	}

	//? >= 1.21 {
	@Override
	public Stream<ItemStack> getItems() {
		return makeDisplayStacks(soul, soulTag, minTier, item, enhanced).stream();
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
		if (soul != null) {
			json.addProperty("soul", soul.toString());
		}
		if (soulTag != null) {
			json.addProperty("soul_tag", soulTag.location().toString());
		}
		if (minTier > 0) {
			json.addProperty("min_tier", minTier);
		}
		if (item != null) {
			json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
		}
		if (enhanced != null) {
			json.addProperty("enhanced", enhanced);
		}
		return json;
	}
	*///?}

	public static List<ItemStack> makeDisplayStacks(
			@Nullable ResourceLocation soul,
			@Nullable TagKey<EntityType<?>> soulTag,
			int minTier,
			@Nullable Item item,
			@Nullable Boolean enhanced
	) {
		boolean isEnh = Boolean.TRUE.equals(enhanced);
		List<ResourceLocation> soulIds = new ArrayList<>();
		if (soul != null) {
			soulIds.add(soul);
		} else if (soulTag != null) {
			Optional<HolderSet.Named<EntityType<?>>> tag = BuiltInRegistries.ENTITY_TYPE.getTag(soulTag);
			if (tag.isPresent()) {
				for (Holder<EntityType<?>> holder : tag.get()) {
					holder.unwrapKey().ifPresent(key -> soulIds.add(key.location()));
				}
			}
		}

		if (soulIds.isEmpty()) {
			return Collections.emptyList();
		}

		if (item != null) {
			if (item instanceof PolyclayAttunedItem polyclay && polyclay.getTier() < minTier) {
				return Collections.emptyList();
			}
			List<ItemStack> list = new ArrayList<>();
			for (ResourceLocation soulId : soulIds) {
				list.add(PolyclayAttunedItem.createAttunedPolyclay(item, soulId, isEnh));
			}
			return list;
		}

		List<ItemStack> list = new ArrayList<>();
		for (Item registered : BuiltInRegistries.ITEM) {
			if (registered instanceof PolyclayAttunedItem polyclay && polyclay.getTier() >= minTier) {
				for (ResourceLocation soulId : soulIds) {
					list.add(PolyclayAttunedItem.createAttunedPolyclay(registered, soulId, isEnh));
				}
			}
		}
		return list;
	}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul, @Nullable Item item, @Nullable Boolean enhanced) {
		return makeDisplayStacks(soul, null, 0, item, enhanced);
	}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul, @Nullable Item item) {
		return makeDisplayStacks(soul, item, null);
	}

	public static List<ItemStack> makeDisplayStacks(ResourceLocation soul) {
		return makeDisplayStacks(soul, null, null);
	}
}
