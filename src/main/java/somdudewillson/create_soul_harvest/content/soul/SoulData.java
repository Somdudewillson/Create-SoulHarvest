package somdudewillson.create_soul_harvest.content.soul;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class SoulData {
	private int tier;
	private int progressPerKill;
	private int xp;
	private final Map<String, SoulDropPool> pools;

	public SoulData(int tier, int progressPerKill, int xp, Collection<SoulDropPool> poolList) {
		this.tier = Math.max(1, tier);
		this.progressPerKill = Math.max(1, progressPerKill);
		this.xp = Math.max(0, xp);
		this.pools = new LinkedHashMap<>();
		if (poolList != null) {
			for (SoulDropPool pool : poolList) {
				this.pools.put(pool.name(), pool);
			}
		}
	}

	public SoulData(int tier, int progressPerKill, int xp) {
		this(tier, progressPerKill, xp, Collections.emptyList());
	}

	public SoulData(int tier, int progressPerKill, Collection<SoulDropPool> poolList) {
		this(tier, progressPerKill, 0, poolList);
	}

	public SoulData(int tier, int progressPerKill) {
		this(tier, progressPerKill, 0, Collections.emptyList());
	}

	public void tier(int tier) {
		this.tier = Math.max(1, tier);
	}

	public void progressPerKill(int progressPerKill) {
		this.progressPerKill = Math.max(1, progressPerKill);
	}

	public void xp(int xp) {
		this.xp = Math.max(0, xp);
	}

	public Map<String, SoulDropPool> pools() {
		return Collections.unmodifiableMap(pools);
	}

	public boolean hasDrops() {
		for (SoulDropPool pool : pools.values()) {
			if (!pool.entries().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void mergePool(SoulDropPool partialPool) {
		if (partialPool == null) return;
		SoulDropPool existing = pools.get(partialPool.name());
		if (existing != null) {
			// Preserve existing rolls, add entries
			existing.addEntries(partialPool.entries());
		} else {
			// Newly created pool uses partial pool's rolls
			pools.put(partialPool.name(), new SoulDropPool(partialPool.name(), partialPool.rolls(), partialPool.entries()));
		}
	}

	public List<ItemStack> rollDrops(RandomSource random) {
		List<ItemStack> drops = new ArrayList<>();
		for (SoulDropPool pool : pools.values()) {
			pool.roll(random, drops::add);
		}
		return drops;
	}

	public List<ResourceLocation> getAllPossibleItemIds() {
		List<ResourceLocation> list = new ArrayList<>();
		for (SoulDropPool pool : pools.values()) {
			for (ResourceLocation id : pool.getItemIds()) {
				if (!list.contains(id)) {
					list.add(id);
				}
			}
		}
		return list;
	}
}
