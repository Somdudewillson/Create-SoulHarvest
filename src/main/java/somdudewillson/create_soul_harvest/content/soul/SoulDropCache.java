package somdudewillson.create_soul_harvest.content.soul;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SoulDropCache {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String CACHE_FILE_NAME = "soul_harvest_drop_cache.json";

	private final Map<ResourceLocation, List<SoulDropPool>> cache = new ConcurrentHashMap<>();

	private Path getCachePath(MinecraftServer server) {
		if (server == null) return null;
		
		try {
			return server.getWorldPath(LevelResource.ROOT).resolve(CACHE_FILE_NAME);
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to resolve world directory for drop cache: {}", e.getMessage());
			return null;
		}
	}

	public synchronized void load(MinecraftServer server) {
		cache.clear();
		Path path = getCachePath(server);
		if (path == null || !Files.exists(path)) {
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
				ResourceLocation entityId = ResourceLocation.tryParse(entry.getKey());
				if (entityId == null || !entry.getValue().isJsonArray()) continue;

				List<SoulDropPool> pools = new ArrayList<>();
				for (JsonElement poolEl : entry.getValue().getAsJsonArray()) {
					if (!poolEl.isJsonObject()) continue;
					JsonObject poolObj = poolEl.getAsJsonObject();
					String name = poolObj.has("name") ? poolObj.get("name").getAsString() : "main";
					int rolls = poolObj.has("rolls") ? poolObj.get("rolls").getAsInt() : 1;
					List<SoulDropEntry> entries = new ArrayList<>();
					if (poolObj.has("entries") && poolObj.get("entries").isJsonArray()) {
						for (JsonElement entEl : poolObj.get("entries").getAsJsonArray()) {
							if (!entEl.isJsonObject()) continue;
							JsonObject entObj = entEl.getAsJsonObject();
							ResourceLocation itemId = ResourceLocation.tryParse(entObj.get("item").getAsString());
							if (itemId == null) continue;
							int weight = entObj.has("weight") ? entObj.get("weight").getAsInt() : 1;
							int min = entObj.has("min") ? entObj.get("min").getAsInt() : 1;
							int max = entObj.has("max") ? entObj.get("max").getAsInt() : 1;
							entries.add(new SoulDropEntry(itemId, weight, min, max));
						}
					}
					pools.add(new SoulDropPool(name, rolls, entries));
				}
				cache.put(entityId, pools);
			}
			SoulHarvestMod.LOGGER.info("Loaded {} cached entity drop tables from {}", cache.size(), path.getFileName());
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to load soul drop cache: {}", e.getMessage());
		}
	}

	public synchronized void save(MinecraftServer server) {
		Path path = getCachePath(server);
		if (path == null) return;

		try {
			JsonObject root = new JsonObject();
			for (Map.Entry<ResourceLocation, List<SoulDropPool>> entry : cache.entrySet()) {
				JsonArray poolsArray = new JsonArray();
				for (SoulDropPool pool : entry.getValue()) {
					JsonObject poolObj = new JsonObject();
					poolObj.addProperty("name", pool.name());
					poolObj.addProperty("rolls", pool.rolls());
					JsonArray entriesArray = new JsonArray();
					for (SoulDropEntry ent : pool.entries()) {
						JsonObject entObj = new JsonObject();
						entObj.addProperty("item", ent.item().toString());
						entObj.addProperty("weight", ent.weight());
						entObj.addProperty("min", ent.min());
						entObj.addProperty("max", ent.max());
						entriesArray.add(entObj);
					}
					poolObj.add("entries", entriesArray);
					poolsArray.add(poolObj);
				}
				root.add(entry.getKey().toString(), poolsArray);
			}

			Files.createDirectories(path.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				GSON.toJson(root, writer);
			}
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to save soul drop cache: {}", e.getMessage());
		}
	}

	public boolean contains(ResourceLocation entityId) {
		return cache.containsKey(entityId);
	}

	public List<SoulDropPool> get(ResourceLocation entityId) {
		return cache.get(entityId);
	}

	public int size() {
		return cache.size();
	}

	public synchronized void put(ResourceLocation entityId, List<SoulDropPool> pools, MinecraftServer server) {
		cache.put(entityId, pools);
		save(server);
	}

	public synchronized void clear(MinecraftServer server) {
		cache.clear();
		Path path = getCachePath(server);
		if (path != null && Files.exists(path)) {
			try {
				Files.delete(path);
				SoulHarvestMod.LOGGER.info("Deleted drop cache file at {}", path);
			} catch (Exception e) {
				SoulHarvestMod.LOGGER.error("Failed to delete drop cache file: {}", e.getMessage());
			}
		}
	}
}
