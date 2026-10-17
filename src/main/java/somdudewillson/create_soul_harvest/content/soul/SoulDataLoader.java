package somdudewillson.create_soul_harvest.content.soul;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.io.Reader;
import java.util.*;

public class SoulDataLoader extends SimplePreparableReloadListener<SoulDataLoader.PreparedData> {
	public static final SoulDataLoader INSTANCE = new SoulDataLoader();
	private static final String DIRECTORY = "soul_harvest/souls";

	public record PreparedData(
			Map<ResourceLocation, SoulData> fullData,
			Map<ResourceLocation, List<PartialSoulData>> partialData,
			Map<ResourceLocation, SoulDropCache.CachedSoulData> fallbackData
	) {}

	private SoulDataLoader() {}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	protected PreparedData prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		Map<ResourceLocation, SoulData> fullData = new HashMap<>();
		Map<ResourceLocation, List<PartialSoulData>> partialData = new HashMap<>();
		Map<ResourceLocation, SoulDropCache.CachedSoulData> fallbackData = new HashMap<>();

		Map<ResourceLocation, List<Resource>> resourceStacks = resourceManager.listResourceStacks(
				DIRECTORY,
				loc -> loc.getPath().endsWith(".json")
		);

		for (Map.Entry<ResourceLocation, List<Resource>> entry : resourceStacks.entrySet()) {
			ResourceLocation fileLocation = entry.getKey();
			String path = fileLocation.getPath();
			if (!path.startsWith(DIRECTORY + "/") || !path.endsWith(".json")) {
				continue;
			}

			String subPath = path.substring((DIRECTORY + "/").length(), path.length() - ".json".length());
			ResourceLocation entityId;
			if (subPath.contains("/")) {
				int slash = subPath.indexOf('/');
				String ns = subPath.substring(0, slash);
				String p = subPath.substring(slash + 1);
				entityId = ResourceLocation.fromNamespaceAndPath(ns, p);
			} else {
				entityId = SoulHarvestMod.id(fileLocation.getNamespace(), subPath);
			}

			// Safely skip entities that do not exist in the current game state
			if (!BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
				SoulHarvestMod.LOGGER.debug("Skipping soul data for unknown entity type: {}", entityId);
				continue;
			}

			// Process resources in order (lower priority to higher priority)
			for (Resource resource : entry.getValue()) {
				parseResource(fileLocation, resource, entityId, fullData, partialData, fallbackData);
			}
		}

		return new PreparedData(fullData, partialData, fallbackData);
	}

	private void parseResource(
			ResourceLocation fileLocation,
			Resource resource,
			ResourceLocation entityId,
			Map<ResourceLocation, SoulData> fullData,
			Map<ResourceLocation, List<PartialSoulData>> partialData,
			Map<ResourceLocation, SoulDropCache.CachedSoulData> fallbackData
	) {
		try (Reader reader = resource.openAsReader()) {
			JsonElement parsed = JsonParser.parseReader(reader);
			if (!parsed.isJsonObject()) return;
			JsonObject json = parsed.getAsJsonObject();

			boolean isFallback = json.has("fallback") && json.get("fallback").getAsBoolean();
			boolean isPartial = isPartialDefinition(json);
			List<SoulDropPool> pools = SoulDropPool.parsePools(json);
			OptionalInt xp = parseXp(json);

			if (isFallback) {
				int xpAmount = xp.orElse(0);
				SoulDropCache.CachedSoulData fallback = new SoulDropCache.CachedSoulData(xpAmount, pools);
				fallbackData.put(entityId, fallback);
			} else if (isPartial) {
				OptionalInt tier = json.has("tier") ? OptionalInt.of(json.get("tier").getAsInt()) : OptionalInt.empty();
				OptionalInt progress = json.has("progress_per_kill") ? OptionalInt.of(json.get("progress_per_kill").getAsInt()) : OptionalInt.empty();
				PartialSoulData partial = new PartialSoulData(tier, progress, xp, pools);
				partialData.computeIfAbsent(entityId, k -> new ArrayList<>()).add(partial);
			} else {
				int tier = json.has("tier") ? json.get("tier").getAsInt() : 1;
				int progress = json.has("progress_per_kill") ? json.get("progress_per_kill").getAsInt() : 10;
				int xpAmount = xp.orElse(0);
				SoulData full = new SoulData(tier, progress, xpAmount, pools);
				fullData.put(entityId, full);
			}
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to parse soul data file {} from pack {}: {}",
					fileLocation, resource.sourcePackId(), e.getMessage());
		}
	}

	private boolean isPartialDefinition(JsonObject json) {
		if (json.has("partial") && json.get("partial").getAsBoolean()) {
			return true;
		}
		return json.has("replace") && !json.get("replace").getAsBoolean();
	}

	private OptionalInt parseXp(JsonObject json) {
		if (json.has("xp")) {
			return OptionalInt.of(json.get("xp").getAsInt());
		} else if (json.has("experience")) {
			return OptionalInt.of(json.get("experience").getAsInt());
		}
		return OptionalInt.empty();
	}

	@Override
	protected void apply(PreparedData data, ResourceManager resourceManager, ProfilerFiller profiler) {
		SoulDataManager.setRawData(data.fullData(), data.partialData(), data.fallbackData());
		SoulHarvestMod.LOGGER.info("Loaded {} full, {} partial, and {} fallback soul data definitions",
				data.fullData().size(), data.partialData().size(), data.fallbackData().size());
	}
}
