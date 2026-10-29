package somdudewillson.create_soul_harvest.content.soul;

import com.google.gson.*;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SoulDropCache {
	public static final int CURRENT_VERSION = 2;
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String CACHE_FILE_NAME = "soul_harvest_drop_cache.json";

	public record CachedSoulData(int xp, List<SoulDropPool> pools) {
		public JsonObject toJson() {
			JsonObject obj = new JsonObject();
			obj.addProperty("xp", xp);
			JsonArray poolsArray = new JsonArray();
			for (SoulDropPool pool : pools) {
				poolsArray.add(pool.toJson());
			}
			obj.add("pools", poolsArray);
			return obj;
		}

		@Nullable
		public static CachedSoulData fromJson(JsonObject json) {
			if (json == null) return null;
			int xp = 0;
			if (json.has("xp")) {
				xp = json.get("xp").getAsInt();
			} else if (json.has("experience")) {
				xp = json.get("experience").getAsInt();
			}
			List<SoulDropPool> pools = SoulDropPool.parsePools(json);
			return new CachedSoulData(xp, pools);
		}
	}

	private final Map<ResourceLocation, CachedSoulData> cache = new ConcurrentHashMap<>();
	transient private final Object2BooleanMap<ResourceLocation> entityPresentCache = new Object2BooleanOpenHashMap<>();

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
		entityPresentCache.clear();
		Path path = getCachePath(server);
		if (path == null || !Files.exists(path)) {
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			JsonElement rootEl = JsonParser.parseReader(reader);
			if (!rootEl.isJsonObject()) {
				dropCacheFile(path, "Root is not a JSON object");
				return;
			}

			JsonObject root = rootEl.getAsJsonObject();
			if (!validateCacheRoot(root, path)) {
				return;
			}

			Map<ResourceLocation, CachedSoulData> loaded = deserializeEntities(root);
			cache.putAll(loaded);
			SoulHarvestMod.LOGGER.info("Loaded {} cached entity drop & soul definitions from {}", cache.size(), path.getFileName());
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to load soul drop cache: {}", e.getMessage());
			dropCacheFile(path, "Exception reading cache file: " + e.getMessage());
		}
	}

	private boolean validateCacheRoot(JsonObject root, Path path) {
		if (!root.has("version") || root.get("version").getAsInt() != CURRENT_VERSION) {
			String versionDesc = !root.has("version")
					? "Missing version"
					: "Out-of-date version " + root.get("version").getAsInt() + " (expected " + CURRENT_VERSION + ")";
			dropCacheFile(path, versionDesc);
			return false;
		}

		if (!root.has("entities") && !root.has("data")) {
			dropCacheFile(path, "Missing 'entities' object in cache root");
			return false;
		}
		return true;
	}

	private Map<ResourceLocation, CachedSoulData> deserializeEntities(JsonObject root) {
		Map<ResourceLocation, CachedSoulData> result = new HashMap<>();
		JsonObject entitiesObj = root.has("entities") && root.get("entities").isJsonObject()
				? root.getAsJsonObject("entities")
				: root.getAsJsonObject("data");

		if (entitiesObj == null) {
			return result;
		}

		for (Map.Entry<String, JsonElement> entry : entitiesObj.entrySet()) {
			ResourceLocation entityId = ResourceLocation.tryParse(entry.getKey());
			if (entityId == null || !entry.getValue().isJsonObject()) continue;

			CachedSoulData data = CachedSoulData.fromJson(entry.getValue().getAsJsonObject());
			if (data != null) {
				result.put(entityId, data);
			}
		}
		return result;
	}

	public synchronized void save(MinecraftServer server) {
		Path path = getCachePath(server);
		if (path == null) return;

		try {
			JsonObject root = serializeCache();
			Files.createDirectories(path.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				GSON.toJson(root, writer);
			}
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to save soul drop cache: {}", e.getMessage());
		}
	}

	private JsonObject serializeCache() {
		JsonObject root = new JsonObject();
		root.addProperty("version", CURRENT_VERSION);

		JsonObject entitiesObj = new JsonObject();
		for (Map.Entry<ResourceLocation, CachedSoulData> entry : cache.entrySet()) {
			entitiesObj.add(entry.getKey().toString(), entry.getValue().toJson());
		}
		root.add("entities", entitiesObj);
		return root;
	}
	
	private boolean getEntityPresent(ResourceLocation entityId) {
		return entityPresentCache.computeIfAbsent(entityId, k->{
				boolean inRegistry = BuiltInRegistries.ENTITY_TYPE.containsKey((ResourceLocation) k);
				if (!inRegistry) return false;
				return !BuiltInRegistries.ENTITY_TYPE.get((ResourceLocation) k).is(ModTags.EntityTypes.SOUL_CAPTURE_FORBIDDEN);
			});
	}

	public boolean contains(ResourceLocation entityId) {
		return getEntityPresent(entityId) && cache.containsKey(entityId);
	}

	@Nullable
	public CachedSoulData get(ResourceLocation entityId) {
		return getEntityPresent(entityId) ? cache.get(entityId) : null;
	}

	public List<SoulDropPool> getPools(ResourceLocation entityId) {
		CachedSoulData data = get(entityId);
		return data != null ? data.pools() : Collections.emptyList();
	}

	public int getXp(ResourceLocation entityId) {
		CachedSoulData data = get(entityId);
		return data != null ? data.xp() : 0;
	}

	public int size() {
		return cache.size();
	}

	public synchronized void put(ResourceLocation entityId, int xp, List<SoulDropPool> pools, MinecraftServer server) {
		cache.put(entityId, new CachedSoulData(xp, pools));
		save(server);
	}

	private void dropCacheFile(Path path, String reason) {
		cache.clear();
		entityPresentCache.clear();
		try {
			Files.deleteIfExists(path);
			SoulHarvestMod.LOGGER.info("Dropped out-of-date or invalid soul drop cache file at {} (reason: {}). Will regenerate from scratch.", path.getFileName(), reason);
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to delete out-of-date cache file at {}: {}", path, e.getMessage());
		}
	}

	public synchronized void clear(MinecraftServer server) {
		cache.clear();
		entityPresentCache.clear();
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
