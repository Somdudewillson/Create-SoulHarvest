package somdudewillson.create_soul_harvest.content.soul;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.event.SoulHarvestDataEvents;
import somdudewillson.create_soul_harvest.content.recipe.grinding.MobGrindingDynamicData;
import somdudewillson.create_soul_harvest.content.soul.sampling.DropSamplingPass;
import somdudewillson.create_soul_harvest.content.soul.sampling.PlayerKillDropSamplingPass;
import somdudewillson.create_soul_harvest.content.soul.sampling.StandardDropSamplingPass;

//? >= 1.21 {
import net.minecraft.resources.ResourceKey;
//?}

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import static somdudewillson.create_soul_harvest.registry.ModTags.EntityTypes.*;

public class SoulDataManager {
	private static final SoulDropCache DROP_CACHE = new SoulDropCache();
	private static final List<DropSamplingPass> SAMPLING_PASSES = new ArrayList<>(List.of(
			new StandardDropSamplingPass(),
			new PlayerKillDropSamplingPass()
	));

	public static void registerSamplingPass(DropSamplingPass pass) {
		SAMPLING_PASSES.add(pass);
	}

	private static Map<ResourceLocation, SoulData> rawFullData = Collections.emptyMap();
	private static Map<ResourceLocation, List<PartialSoulData>> rawPartialData = Collections.emptyMap();
	private static Map<ResourceLocation, SoulDropCache.CachedSoulData> rawFallbackData = Collections.emptyMap();

	private static volatile boolean rawDataLoaded = false;
	private static volatile int dataVersion = 0;
	private static volatile boolean samplingChanged = false;

	private static final Map<ResourceLocation, SoulData> resolvedData = new ConcurrentHashMap<>();
	private static final Queue<ResourceLocation> pendingSamplingQueue = new ConcurrentLinkedQueue<>();
	private static volatile int totalBatchQueued = 0;

	public static boolean isLoaded() {
		return rawDataLoaded;
	}

	public static boolean hasSoulData() {
		return !resolvedData.isEmpty();
	}

	public static int getDataVersion() {
		return dataVersion;
	}

	public static synchronized void setRawData(
			Map<ResourceLocation, SoulData> full,
			Map<ResourceLocation, List<PartialSoulData>> partial,
			Map<ResourceLocation, SoulDropCache.CachedSoulData> fallback
	) {
		rawFullData = full != null ? full : Collections.emptyMap();
		rawPartialData = partial != null ? partial : Collections.emptyMap();
		rawFallbackData = fallback != null ? fallback : Collections.emptyMap();
		rawDataLoaded = true;
	}

	public static synchronized void setRawData(
			Map<ResourceLocation, SoulData> full,
			Map<ResourceLocation, List<PartialSoulData>> partial
	) {
		setRawData(full, partial, Collections.emptyMap());
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	public static synchronized boolean recalculate(@Nullable MinecraftServer server) {
		if (!rawDataLoaded) {
			return false;
		}
		boolean changed = false;

		RegistryAccess nullableRegistryAccess = null;
		if (server != null) {
			DROP_CACHE.load(server);
			nullableRegistryAccess = server.registryAccess();
		}
		pendingSamplingQueue.clear();

		Map<ResourceLocation, SoulData> newResolved = new ConcurrentHashMap<>();

		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
			if (entityId == null) continue;

			SoulData soulData = null;
			boolean wasFullData = false;
			boolean queuedForSampling = false;

			if (rawFullData.containsKey(entityId)) {
				SoulData full = rawFullData.get(entityId);
				soulData = new SoulData(full.tier(), full.progressPerKill(), full.xp(), full.pools().values());
				wasFullData = true;
			} else {
				// Determine tier from complete data, partial data, or tags
				int tier = 0;
				List<PartialSoulData> partials = rawPartialData.get(entityId);
				if (partials != null) {
					for (PartialSoulData p : partials) {
						if (p.tier().isPresent()) {
							tier = p.tier().getAsInt();
							break;
						}
					}
				}

				if (tier <= 0) {
					if (entityType.is(T1_MOB)) {
						tier = 1;
					} else if (entityType.is(T2_MOB)) {
						tier = 2;
					} else if (entityType.is(T3_MOB)) {
						tier = 3;
					}
				}

				// If entity has no tier, it is not capturable (even if fallback data exists)
				if (tier <= 0) {
					continue;
				}

				// Determine progress per kill
				int progress = 0;
				if (partials != null) {
					for (PartialSoulData p : partials) {
						if (p.progressPerKill().isPresent()) {
							progress = p.progressPerKill().getAsInt();
							break;
						}
					}
				}

				if (progress <= 0) {
					if (entityType.is(REQUIRES_LOW)) {
						progress = 100;
					} else if (entityType.is(REQUIRES_MEDIUM)) {
						progress = 10;
					} else if (entityType.is(REQUIRES_HIGH)) {
						progress = 4;
					} else {
						// Defaults based on tier
						progress = switch (tier) {
							case 1 -> 10;
							case 2 -> 4;
							case 3 -> 100;
							default -> 10;
						};
					}
				}

				// Determine xp and drop pools
				List<SoulDropPool> pools;
				int xp = 0;
				if (partials != null) {
					for (PartialSoulData p : partials) {
						if (p.xp().isPresent()) {
							xp = p.xp().getAsInt();
							break;
						}
					}
				}

				if (DROP_CACHE.contains(entityId)) {
					SoulDropCache.CachedSoulData cached = DROP_CACHE.get(entityId);
					pools = cached.pools();
					if (xp <= 0) {
						xp = cached.xp();
					}
				} else if (rawFallbackData.containsKey(entityId)) {
					SoulDropCache.CachedSoulData fallback = rawFallbackData.get(entityId);
					pools = new ArrayList<>(fallback.pools());
					if (xp <= 0) {
						xp = fallback.xp();
					}
					// Still queue for background sampling so world simulation refines the drops
					pendingSamplingQueue.add(entityId);
					queuedForSampling = true;
				} else {
					pools = Collections.emptyList();
					pendingSamplingQueue.add(entityId);
					queuedForSampling = true;
				}

				soulData = new SoulData(tier, progress, xp, pools);
			}

			// Apply partial data additions
			if (rawPartialData.containsKey(entityId)) {
				for (PartialSoulData partial : rawPartialData.get(entityId)) {
					if (!wasFullData) {
						if (partial.tier().isPresent()) {
							soulData.tier(partial.tier().getAsInt());
						}
						if (partial.progressPerKill().isPresent()) {
							soulData.progressPerKill(partial.progressPerKill().getAsInt());
						}
						if (partial.xp().isPresent()) {
							soulData.xp(partial.xp().getAsInt());
						}
					}
					for (SoulDropPool partialPool : partial.pools()) {
						soulData.mergePool(partialPool);
					}
				}
			}

			// Pruning: if not queued for sampling and has neither drops nor xp, omit from final manager entries
			if (!queuedForSampling && soulData.xp() <= 0 && !soulData.hasLootTokenDrops(nullableRegistryAccess) && !soulData.hasDrops(nullableRegistryAccess, true)) {
				continue;
			}
			
			changed |= !soulData.equals(resolvedData.get(entityId));
			newResolved.put(entityId, soulData);
		}
		
		changed |= resolvedData.size() != newResolved.size();
		changed |= resolvedData.size() != Sets.intersection(resolvedData.keySet(), newResolved.keySet()).size();

		if (changed) {
			resolvedData.clear();
			resolvedData.putAll(newResolved);
			dataVersion++;
			MobGrindingDynamicData.invalidateCache();
			totalBatchQueued = pendingSamplingQueue.size();

			SoulHarvestMod.LOGGER.info("Resolved soul data for {} entities ({} queued for background drop computation)",
					resolvedData.size(), pendingSamplingQueue.size());
			return true;
		} else {
			totalBatchQueued = Math.max(totalBatchQueued, pendingSamplingQueue.size());
			return false;
		}
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	public static void tick(MinecraftServer server) {
		if (server == null || pendingSamplingQueue.isEmpty()) {
			return;
		}

		ResourceLocation entityId = pendingSamplingQueue.poll();
		if (entityId == null) {
			return;
		}

		EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityId);
		if (entityType == null) {
			return;
		}

		SoulDropCache.CachedSoulData sampled = sampleEntityData(server, entityType);
		DROP_CACHE.put(entityId, sampled.xp(), sampled.pools(), server);

		SoulData current = resolvedData.get(entityId);
		if (current != null) {
			if (current.xp() <= 0 && sampled.xp() > 0) {
				current.xp(sampled.xp());
				samplingChanged = true;
			}
			for (SoulDropPool pool : sampled.pools()) {
				SoulDropPool existing = current.pools().get(pool.name());
				int prevSize = existing != null ? existing.entries().size() : 0;
				current.mergePool(pool);
				SoulDropPool updated = current.pools().get(pool.name());
				int newSize = updated != null ? updated.entries().size() : 0;
				if (newSize != prevSize) {
					samplingChanged = true;
				}
			}
		}

		if (pendingSamplingQueue.isEmpty()) {
			if (samplingChanged) {
				dataVersion++;
				MobGrindingDynamicData.invalidateCache();
				MobGrindingDynamicData.updateRecipeManager(server);
				MobGrindingDynamicData.updateLootTables(server);
				SoulHarvestDataEvents.onSoulDataUpdated();
				samplingChanged = false;
			}
		}
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	private static SoulDropCache.CachedSoulData sampleEntityData(MinecraftServer server, EntityType<?> entityType) {
		ServerLevel level = server.overworld();
		if (level == null) {
			return new SoulDropCache.CachedSoulData(0, Collections.emptyList());
		}

		try {
			//? >= 1.21 {
			ResourceKey<net.minecraft.world.level.storage.loot.LootTable> lootTableKey = entityType.getDefaultLootTable();
			if (lootTableKey == null) {
				return new SoulDropCache.CachedSoulData(0, Collections.emptyList());
			}
			net.minecraft.world.level.storage.loot.LootTable lootTable = server.reloadableRegistries().getLootTable(lootTableKey);
			//?} else {
			/*ResourceLocation lootTableId = entityType.getDefaultLootTable();
			if (lootTableId == null) {
				return new SoulDropCache.CachedSoulData(0, Collections.emptyList());
			}
			net.minecraft.world.level.storage.loot.LootTable lootTable = server.getLootData().getLootTable(lootTableId);
			*///?}

			Entity entity = entityType.create(level);

			int xp = 0;
			try {
				//? >= 1.21 {
				if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
					xp = living.getExperienceReward(level, null);
				}
				//?} else {
				/*if (entity instanceof net.minecraft.world.entity.Mob mob) {
					xp = mob.getExperienceReward();
				}
				*///?}
			} catch (Exception e) {
				SoulHarvestMod.LOGGER.debug("Failed to calculate experience reward for {}: {}",
						BuiltInRegistries.ENTITY_TYPE.getKey(entityType), e.getMessage());
			}
			xp = Math.max(0, xp);

			if (lootTable == null || lootTable == net.minecraft.world.level.storage.loot.LootTable.EMPTY) {
				if (entity != null) {
					entity.discard();
				}
				return new SoulDropCache.CachedSoulData(xp, Collections.emptyList());
			}

			Map<ResourceLocation, SoulDropEntry> resolvedEntries = new LinkedHashMap<>();

			for (DropSamplingPass pass : SAMPLING_PASSES) {
				try {
					LootParams params = pass.createLootParams(level, entity);
					Map<ResourceLocation, DropStats> passStats = new HashMap<>();
					int totalRolls = pass.rolls();

					for (int i = 0; i < totalRolls; i++) {
						List<ItemStack> drops = lootTable.getRandomItems(params);
						for (ItemStack stack : drops) {
							if (stack.isEmpty()) continue;
							ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
							passStats.computeIfAbsent(itemId, k -> new DropStats()).record(stack.getCount());
						}
					}

					for (Map.Entry<ResourceLocation, DropStats> entry : passStats.entrySet()) {
						ResourceLocation itemId = entry.getKey();
						DropStats stats = entry.getValue();
						if (!resolvedEntries.containsKey(itemId)) {
							resolvedEntries.put(itemId, new SoulDropEntry(
									itemId,
									stats.occurrences,
									stats.minCount,
									stats.maxCount,
									pass.restriction()
							));
						}
					}
				} catch (Exception e) {
					SoulHarvestMod.LOGGER.debug("Failed sampling pass '{}' for {}: {}",
							pass.name(), BuiltInRegistries.ENTITY_TYPE.getKey(entityType), e.getMessage());
				}
			}

			if (entity != null) {
				entity.discard();
			}

			List<SoulDropEntry> entries = new ArrayList<>(resolvedEntries.values());
			List<SoulDropPool> pools = entries.isEmpty()
					? Collections.emptyList()
					: List.of(new SoulDropPool("main", 1, entries));

			return new SoulDropCache.CachedSoulData(xp, pools);
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.debug("Failed to simulate loot table for {}: {}",
					BuiltInRegistries.ENTITY_TYPE.getKey(entityType), e.getMessage());
			return new SoulDropCache.CachedSoulData(0, Collections.emptyList());
		}
	}

	public static synchronized int flushDropCache(MinecraftServer server) {
		DROP_CACHE.clear(server);
		recalculate(server);
		return pendingSamplingQueue.size();
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	public static SoulData get(EntityType<?> type) {
		if (type == null) return null;
		ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
		return get(id);
	}

	public static SoulData get(ResourceLocation id) {
		if (id == null) return null;
		return resolvedData.get(id);
	}

	public static boolean canCapture(EntityType<?> type) {
		return get(type) != null;
	}

	public static Map<ResourceLocation, SoulData> getAllResolvedData() {
		return Collections.unmodifiableMap(resolvedData);
	}

	public static Map<ResourceLocation, SoulData> getKnownSoulData() {
		return Collections.unmodifiableMap(resolvedData);
	}

	public static void clear() {
		resolvedData.clear();
		pendingSamplingQueue.clear();
		totalBatchQueued = 0;
		rawDataLoaded = false;
		samplingChanged = false;
		dataVersion = 0;
	}

	public record ScanProgress(int remaining, int totalBatch, int cachedCount, int resolvedCount) {
		public int completed() {
			return Math.max(0, totalBatch - remaining);
		}

		public boolean isComplete() {
			return remaining == 0;
		}

		public double percentage() {
			if (totalBatch <= 0) return 100.0;
			return (completed() * 100.0) / totalBatch;
		}
	}

	public static ScanProgress getScanProgress() {
		int remaining = pendingSamplingQueue.size();
		int total = Math.max(totalBatchQueued, remaining);
		return new ScanProgress(remaining, total, DROP_CACHE.size(), resolvedData.size());
	}

	private static class DropStats {
		int occurrences = 0;
		int minCount = Integer.MAX_VALUE;
		int maxCount = 0;

		void record(int count) {
			occurrences++;
			if (count < minCount) minCount = count;
			if (count > maxCount) maxCount = count;
		}
	}
}
