package somdudewillson.create_soul_harvest.content.recipe.condition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ItemLike;
import somdudewillson.create_soul_harvest.config.graph.ItemDependencyGraph;
import somdudewillson.create_soul_harvest.config.graph.ModDependencyGraph;

import java.util.*;

//? neoforge {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;
//?} forge {
/*import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
*///?}

public class ItemReachableCondition implements ICondition {

	public enum RequireMode implements StringRepresentable {
		ALL("all"),
		ANY("any");

		//? neoforge {
		public static final Codec<RequireMode> CODEC = StringRepresentable.fromEnum(RequireMode::values);
		//?}

		private final String name;

		RequireMode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}

		public static RequireMode fromString(String name) {
			if ("any".equalsIgnoreCase(name)) {
				return ANY;
			}
			return ALL;
		}
	}

	//? neoforge {
	public static final MapCodec<ItemReachableCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder.group(
					ResourceLocation.CODEC.optionalFieldOf("item").forGetter(ItemReachableCondition::item),
					ResourceLocation.CODEC.listOf().optionalFieldOf("items").forGetter(ItemReachableCondition::items),
					Codec.STRING.optionalFieldOf("group").forGetter(ItemReachableCondition::group),
					RequireMode.CODEC.optionalFieldOf("mode", RequireMode.ALL).forGetter(ItemReachableCondition::mode)
			).apply(builder, ItemReachableCondition::new)
	);
	//?} forge {
	/*public static final ResourceLocation NAME = SoulHarvestMod.id("item_reachable");
	*///?}

	private final Optional<ResourceLocation> item;
	private final Optional<List<ResourceLocation>> items;
	private final Optional<String> group;
	private final RequireMode mode;

	public ItemReachableCondition(Optional<ResourceLocation> item, Optional<List<ResourceLocation>> items, Optional<String> group, RequireMode mode) {
		this.item = item;
		this.items = items;
		this.group = group;
		this.mode = mode != null ? mode : RequireMode.ALL;
	}

	public ItemReachableCondition(ResourceLocation item) {
		this(Optional.of(item), Optional.empty(), Optional.empty(), RequireMode.ALL);
	}

	public ItemReachableCondition(String group, RequireMode mode) {
		this(Optional.empty(), Optional.empty(), Optional.of(group), mode);
	}

	public ItemReachableCondition(List<ResourceLocation> items, RequireMode mode) {
		this(Optional.empty(), Optional.of(items), Optional.empty(), mode);
	}

	public Optional<ResourceLocation> item() {
		return item;
	}

	public Optional<List<ResourceLocation>> items() {
		return items;
	}

	public Optional<String> group() {
		return group;
	}

	public RequireMode mode() {
		return mode;
	}

	// Static factory helpers
	public static ItemReachableCondition item(ResourceLocation id) {
		return new ItemReachableCondition(id);
	}

	public static ItemReachableCondition item(ItemLike itemLike) {
		//? >= 1.21 {
		return item(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(itemLike.asItem()));
		//?} else {
		/*return item(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(itemLike.asItem()));
		*///?}
	}

	public static ItemReachableCondition all(ResourceLocation... ids) {
		return new ItemReachableCondition(List.of(ids), RequireMode.ALL);
	}

	public static ItemReachableCondition all(ItemLike... itemLikes) {
		List<ResourceLocation> ids = new ArrayList<>();
		for (ItemLike item : itemLikes) {
			//? >= 1.21 {
			ids.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()));
			//?} else {
			/*ids.add(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item.asItem()));
			*///?}
		}
		return new ItemReachableCondition(ids, RequireMode.ALL);
	}

	public static ItemReachableCondition any(ResourceLocation... ids) {
		return new ItemReachableCondition(List.of(ids), RequireMode.ANY);
	}

	public static ItemReachableCondition any(ItemLike... itemLikes) {
		List<ResourceLocation> ids = new ArrayList<>();
		for (ItemLike item : itemLikes) {
			//? >= 1.21 {
			ids.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()));
			//?} else {
			/*ids.add(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item.asItem()));
			*///?}
		}
		return new ItemReachableCondition(ids, RequireMode.ANY);
	}

	public static ItemReachableCondition groupAll(String group) {
		return new ItemReachableCondition(group, RequireMode.ALL);
	}

	public static ItemReachableCondition groupAny(String group) {
		return new ItemReachableCondition(group, RequireMode.ANY);
	}

	@Override
	public boolean test(IContext context) {
		ItemDependencyGraph graph = ModDependencyGraph.get();

		if (item.isPresent()) {
			return graph.isReachable(item.get());
		}

		if (group.isPresent()) {
			Set<ResourceLocation> groupItems = graph.getItemsInGroup(group.get());
			// Items not in the graph default to being available
			if (groupItems.isEmpty()) {
				return true;
			}
			if (mode == RequireMode.ANY) {
				for (ResourceLocation id : groupItems) {
					if (graph.isReachable(id)) {
						return true;
					}
				}
				return false;
			} else {
				for (ResourceLocation id : groupItems) {
					if (!graph.isReachable(id)) {
						return false;
					}
				}
				return true;
			}
		}

		if (items.isPresent()) {
			List<ResourceLocation> list = items.get();
			if (list.isEmpty()) {
				return true;
			}
			if (mode == RequireMode.ANY) {
				for (ResourceLocation id : list) {
					if (graph.isReachable(id)) {
						return true;
					}
				}
				return false;
			} else {
				for (ResourceLocation id : list) {
					if (!graph.isReachable(id)) {
						return false;
					}
				}
				return true;
			}
		}

		return true;
	}

	//? neoforge {
	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}
	//?} forge {
	/*@Override
	public ResourceLocation getID() {
		return NAME;
	}

	public static class Serializer implements IConditionSerializer<ItemReachableCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ItemReachableCondition value) {
			value.item().ifPresent(id -> json.addProperty("item", id.toString()));
			value.group().ifPresent(g -> json.addProperty("group", g));
			value.items().ifPresent(list -> {
				JsonArray array = new JsonArray();
				for (ResourceLocation id : list) {
					array.add(id.toString());
				}
				json.add("items", array);
			});
			if (value.mode() != RequireMode.ALL) {
				json.addProperty("mode", value.mode().getSerializedName());
			}
		}

		@Override
		public ItemReachableCondition read(JsonObject json) {
			Optional<ResourceLocation> item = json.has("item")
					? Optional.of(ResourceLocation.parse(GsonHelper.getAsString(json, "item")))
					: Optional.empty();
			Optional<String> group = json.has("group")
					? Optional.of(GsonHelper.getAsString(json, "group"))
					: Optional.empty();
			Optional<List<ResourceLocation>> items = Optional.empty();
			if (json.has("items")) {
				JsonArray array = GsonHelper.getAsJsonArray(json, "items");
				List<ResourceLocation> list = new ArrayList<>();
				for (JsonElement el : array) {
					list.add(ResourceLocation.parse(el.getAsString()));
				}
				items = Optional.of(list);
			}
			RequireMode mode = json.has("mode")
					? RequireMode.fromString(GsonHelper.getAsString(json, "mode"))
					: RequireMode.ALL;
			return new ItemReachableCondition(item, items, group, mode);
		}

		@Override
		public ResourceLocation getID() {
			return NAME;
		}
	}
	*///?}

	@Override
	public String toString() {
		if (item.isPresent()) {
			return "item_reachable(\"" + item.get() + "\")";
		}
		if (group.isPresent()) {
			return "item_reachable(group=\"" + group.get() + "\", mode=" + mode + ")";
		}
		if (items.isPresent()) {
			return "item_reachable(items=" + items.get() + ", mode=" + mode + ")";
		}
		return "item_reachable(always_true)";
	}
}
