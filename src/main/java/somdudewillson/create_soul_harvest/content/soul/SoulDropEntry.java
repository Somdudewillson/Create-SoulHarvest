package somdudewillson.create_soul_harvest.content.soul;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public record SoulDropEntry(ResourceLocation item, int weight, int min, int max) {

	public SoulDropEntry {
		if (weight < 1) weight = 1;
		if (min < 1) min = 1;
		if (max < min) max = min;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("item", item.toString());
		obj.addProperty("weight", weight);
		obj.addProperty("min", min);
		obj.addProperty("max", max);
		return obj;
	}

	@Nullable
	public static SoulDropEntry fromJson(JsonObject json) {
		if (json == null || !json.has("item")) return null;
		ResourceLocation itemId = ResourceLocation.tryParse(json.get("item").getAsString());
		if (itemId == null) return null;
		int weight = json.has("weight") ? json.get("weight").getAsInt() : 1;
		int min = json.has("min") ? json.get("min").getAsInt() : 1;
		int max = json.has("max") ? json.get("max").getAsInt() : 1;
		return new SoulDropEntry(itemId, weight, min, max);
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	public ItemStack createStack(RandomSource random) {
		Item registeredItem = BuiltInRegistries.ITEM.get(item);
		if (registeredItem == null || registeredItem == Items.AIR) {
			return ItemStack.EMPTY;
		}
		int count = min == max ? min : random.nextInt(max - min + 1) + min;
		return new ItemStack(registeredItem, count);
	}
}
