package somdudewillson.create_soul_harvest.content.soul;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

//? >= 1.21 {
import net.minecraft.resources.ResourceKey;
//?}

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static somdudewillson.create_soul_harvest.registry.ModTags.EntityTypes.*;

public class SoulDataManager {
	private static final SoulDropCache DROP_CACHE = new SoulDropCache();

	private static Map<ResourceLocation, SoulData> rawFullData = Collections.emptyMap();
	private static Map<ResourceLocation, List<PartialSoulData>> rawPartialData = Collections.emptyMap();

	private static final Map<ResourceLocation, SoulData> resolvedData = new ConcurrentHashMap<>();
	private static final Queue<ResourceLocation> pendingSamplingQueue = new ConcurrentLinkedQueue<>();
	private static volatile int totalBatchQueued = 0;

	public static synchronized void setRawData(
			Map<ResourceLocation, SoulData> full,
			Map<ResourceLocation, List<PartialSoulData>> partial
	) {
		rawFullData = full != null ? full : Collections.emptyMap();
		rawPartialData = partial != null ? partial : Collections.emptyMap();
	}

	public static synchronized void recalculate(MinecraftServer server) {
		if (server == null) {
			return;
		}

		DROP_CACHE.load(server);
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
				soulData = new SoulData(full.tier(), full.progressPerKill(), full.pools().values());
				wasFullData = true;
			} else {
				// Determine tier from partial data or tags
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

				// If entity has no tier, it is not capturable
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

				// Resolve drop pools
				List<SoulDropPool> pools;
				if (DROP_CACHE.contains(entityId)) {
					pools = DROP_CACHE.get(entityId);
				} else {
					pools = Collections.emptyList();
					pendingSamplingQueue.add(entityId);
					queuedForSampling = true;
				}

				soulData = new SoulData(tier, progress, pools);
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
					}
					for (SoulDropPool partialPool : partial.pools()) {
						soulData.mergePool(partialPool);
					}
				}
			}

			// Pruning: if not queued for sampling and has empty drops, omit from final manager entries
			if (!queuedForSampling && !soulData.hasDrops()) {
				continue;
			}

			newResolved.put(entityId, soulData);
		}

		resolvedData.clear();
		resolvedData.putAll(newResolved);
		totalBatchQueued = pendingSamplingQueue.size();

		SoulHarvestMod.LOGGER.info("Resolved soul data for {} entities ({} queued for background drop computation)",
				resolvedData.size(), pendingSamplingQueue.size());
	}

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

		List<SoulDropPool> sampledPools = sampleNonPlayerDrops(server, entityType);
		DROP_CACHE.put(entityId, sampledPools, server);

		SoulData current = resolvedData.get(entityId);
		if (current != null) {
			for (SoulDropPool pool : sampledPools) {
				current.mergePool(pool);
			}
		}
	}

	private static List<SoulDropPool> sampleNonPlayerDrops(MinecraftServer server, EntityType<?> entityType) {
		ServerLevel level = server.overworld();
		if (level == null) {
			return Collections.emptyList();
		}

		try {
			//? >= 1.21 {
			ResourceKey<net.minecraft.world.level.storage.loot.LootTable> lootTableKey = entityType.getDefaultLootTable();
			if (lootTableKey == null) {
				return Collections.emptyList();
			}
			net.minecraft.world.level.storage.loot.LootTable lootTable = server.reloadableRegistries().getLootTable(lootTableKey);
			//?} else {
			/*ResourceLocation lootTableId = entityType.getDefaultLootTable();
			if (lootTableId == null) {
				return Collections.emptyList();
			}
			net.minecraft.world.level.storage.loot.LootTable lootTable = server.getLootData().getLootTable(lootTableId);
			*///?}

			if (lootTable == null || lootTable == net.minecraft.world.level.storage.loot.LootTable.EMPTY) {
				return Collections.emptyList();
			}

			Entity entity = entityType.create(level);
			DamageSource damageSource = level.damageSources().genericKill();
			Vec3 pos = entity != null ? entity.position() : Vec3.atCenterOf(level.getSharedSpawnPos());

			LootParams.Builder paramsBuilder = new LootParams.Builder(level)
					.withParameter(LootContextParams.ORIGIN, pos)
					.withParameter(LootContextParams.DAMAGE_SOURCE, damageSource);

			if (entity != null) {
				paramsBuilder.withParameter(LootContextParams.THIS_ENTITY, entity);
			}

			LootParams params = paramsBuilder.create(LootContextParamSets.ENTITY);

			Map<ResourceLocation, DropStats> statsMap = new HashMap<>();
			int totalRolls = 100;

			for (int i = 0; i < totalRolls; i++) {
				List<ItemStack> drops = lootTable.getRandomItems(params);
				for (ItemStack stack : drops) {
					if (stack.isEmpty()) continue;
					ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
					statsMap.computeIfAbsent(itemId, k -> new DropStats()).record(stack.getCount());
				}
			}

			if (entity != null) {
				entity.discard();
			}

			List<SoulDropEntry> entries = new ArrayList<>();
			for (Map.Entry<ResourceLocation, DropStats> entry : statsMap.entrySet()) {
				DropStats stats = entry.getValue();
				entries.add(new SoulDropEntry(entry.getKey(), stats.occurrences, stats.minCount, stats.maxCount));
			}

			if (entries.isEmpty()) {
				return Collections.emptyList();
			}

			return List.of(new SoulDropPool("main", 1, entries));
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.debug("Failed to simulate loot table for {}: {}",
					BuiltInRegistries.ENTITY_TYPE.getKey(entityType), e.getMessage());
			return Collections.emptyList();
		}
	}

	public static synchronized int flushDropCache(MinecraftServer server) {
		DROP_CACHE.clear(server);
		recalculate(server);
		return pendingSamplingQueue.size();
	}

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

	public static void clear() {
		resolvedData.clear();
		pendingSamplingQueue.clear();
		totalBatchQueued = 0;
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
