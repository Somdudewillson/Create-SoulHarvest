package somdudewillson.create_soul_harvest.content.soul;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class SoulDropPool {
	private final String name;
	private final int rolls;
	private final List<SoulDropEntry> entries;

	public SoulDropPool(String name, int rolls, Collection<SoulDropEntry> entries) {
		this.name = name != null ? name : "main";
		this.rolls = Math.max(1, rolls);
		this.entries = new ArrayList<>(entries);
	}

	public SoulDropPool(String name, int rolls) {
		this(name, rolls, Collections.emptyList());
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", name);
		obj.addProperty("rolls", rolls);
		JsonArray entriesArray = new JsonArray();
		for (SoulDropEntry entry : entries) {
			entriesArray.add(entry.toJson());
		}
		obj.add("entries", entriesArray);
		return obj;
	}

	@Nullable
	public static SoulDropPool fromJson(JsonObject json) {
		if (json == null) return null;
		String name = json.has("name") ? json.get("name").getAsString() : "main";
		int rolls = json.has("rolls") ? json.get("rolls").getAsInt() : 1;
		List<SoulDropEntry> entries = parseEntries(json);
		return new SoulDropPool(name, rolls, entries);
	}

	public static List<SoulDropPool> parsePools(JsonObject json) {
		List<SoulDropPool> pools = new ArrayList<>();
		if (json != null && json.has("pools") && json.get("pools").isJsonArray()) {
			for (JsonElement el : json.getAsJsonArray("pools")) {
				if (el.isJsonObject()) {
					SoulDropPool pool = fromJson(el.getAsJsonObject());
					if (pool != null) {
						pools.add(pool);
					}
				}
			}
		}
		return pools;
	}

	public static List<SoulDropEntry> parseEntries(JsonObject json) {
		List<SoulDropEntry> entries = new ArrayList<>();
		if (json != null && json.has("entries") && json.get("entries").isJsonArray()) {
			for (JsonElement el : json.getAsJsonArray("entries")) {
				if (el.isJsonObject()) {
					SoulDropEntry entry = SoulDropEntry.fromJson(el.getAsJsonObject());
					if (entry != null) {
						entries.add(entry);
					}
				}
			}
		}
		return entries;
	}

	public List<SoulDropEntry> entries() {
		return Collections.unmodifiableList(entries);
	}

	public void addEntries(Collection<SoulDropEntry> newEntries) {
		this.entries.addAll(newEntries);
	}

	public void roll(RandomSource random, Consumer<ItemStack> output) {
		if (entries.isEmpty()) {
			return;
		}

		int totalWeight = 0;
		for (SoulDropEntry entry : entries) {
			totalWeight += entry.weight();
		}

		if (totalWeight <= 0) {
			return;
		}

		for (int r = 0; r < rolls; r++) {
			int pick = random.nextInt(totalWeight);
			int cumulative = 0;
			for (SoulDropEntry entry : entries) {
				cumulative += entry.weight();
				if (pick < cumulative) {
					ItemStack stack = entry.createStack(random);
					if (!stack.isEmpty()) {
						output.accept(stack);
					}
					break;
				}
			}
		}
	}

	public List<ResourceLocation> getItemIds() {
		List<ResourceLocation> list = new ArrayList<>();
		for (SoulDropEntry entry : entries) {
			if (!list.contains(entry.item())) {
				list.add(entry.item());
			}
		}
		return list;
	}
}
