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
			Map<ResourceLocation, List<PartialSoulData>> partialData
	) {}

	private SoulDataLoader() {}

	@Override
	protected PreparedData prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		Map<ResourceLocation, SoulData> fullData = new HashMap<>();
		Map<ResourceLocation, List<PartialSoulData>> partialData = new HashMap<>();

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
			ResourceLocation entityId = SoulHarvestMod.id(fileLocation.getNamespace(), subPath);

			// Safely skip entities that do not exist in the current game state
			if (!BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
				SoulHarvestMod.LOGGER.debug("Skipping soul data for unknown entity type: {}", entityId);
				continue;
			}

			// Process resources in order (lower priority to higher priority)
			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					JsonElement parsed = JsonParser.parseReader(reader);
					if (!parsed.isJsonObject()) continue;
					JsonObject json = parsed.getAsJsonObject();

					boolean isPartial = false;
					if (json.has("partial") && json.get("partial").getAsBoolean()) {
						isPartial = true;
					} else if (json.has("replace") && !json.get("replace").getAsBoolean()) {
						isPartial = true;
					}

					List<SoulDropPool> pools = parsePools(json);

					if (isPartial) {
						OptionalInt tier = json.has("tier") ? OptionalInt.of(json.get("tier").getAsInt()) : OptionalInt.empty();
						OptionalInt progress = json.has("progress_per_kill") ? OptionalInt.of(json.get("progress_per_kill").getAsInt()) : OptionalInt.empty();
						PartialSoulData partial = new PartialSoulData(tier, progress, pools);
						partialData.computeIfAbsent(entityId, k -> new ArrayList<>()).add(partial);
					} else {
						int tier = json.get("tier").getAsInt();
						int progress = json.get("progress_per_kill").getAsInt();
						SoulData full = new SoulData(tier, progress, pools);
						fullData.put(entityId, full);
					}
				} catch (Exception e) {
					SoulHarvestMod.LOGGER.error("Failed to parse soul data file {} from pack {}: {}",
							fileLocation, resource.sourcePackId(), e.getMessage());
				}
			}
		}

		return new PreparedData(fullData, partialData);
	}

	private List<SoulDropPool> parsePools(JsonObject json) {
		List<SoulDropPool> pools = new ArrayList<>();
		if (json.has("pools") && json.get("pools").isJsonArray()) {
			for (JsonElement poolEl : json.getAsJsonArray("pools")) {
				if (!poolEl.isJsonObject()) continue;
				JsonObject poolObj = poolEl.getAsJsonObject();

				String name = poolObj.has("name") ? poolObj.get("name").getAsString() : "main";
				int rolls = poolObj.has("rolls") ? poolObj.get("rolls").getAsInt() : 1;
				List<SoulDropEntry> entries = new ArrayList<>();

				if (poolObj.has("entries") && poolObj.get("entries").isJsonArray()) {
					for (JsonElement entEl : poolObj.getAsJsonArray("entries")) {
						if (!entEl.isJsonObject()) continue;
						JsonObject entObj = entEl.getAsJsonObject();
						if (!entObj.has("item")) continue;
						ResourceLocation itemLoc = ResourceLocation.tryParse(entObj.get("item").getAsString());
						if (itemLoc == null) continue;

						int weight = entObj.has("weight") ? entObj.get("weight").getAsInt() : 1;
						int min = entObj.has("min") ? entObj.get("min").getAsInt() : 1;
						int max = entObj.has("max") ? entObj.get("max").getAsInt() : 1;
						entries.add(new SoulDropEntry(itemLoc, weight, min, max));
					}
				}
				pools.add(new SoulDropPool(name, rolls, entries));
			}
		}
		return pools;
	}

	@Override
	protected void apply(PreparedData data, ResourceManager resourceManager, ProfilerFiller profiler) {
		SoulDataManager.setRawData(data.fullData(), data.partialData());
		SoulHarvestMod.LOGGER.info("Loaded {} full and {} partial soul data definitions",
				data.fullData().size(), data.partialData().size());
	}
}
