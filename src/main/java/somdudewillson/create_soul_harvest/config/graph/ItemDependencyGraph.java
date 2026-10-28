package somdudewillson.create_soul_harvest.config.graph;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class ItemDependencyGraph {
	private final Map<ResourceLocation, ItemDependencyNode> nodes = new LinkedHashMap<>();
	private final Map<String, Set<ResourceLocation>> groupToItems = new LinkedHashMap<>();

	private final Map<ResourceLocation, List<BooleanSupplier>> itemConfigBindings = new LinkedHashMap<>();
	private final Map<String, List<BooleanSupplier>> groupConfigBindings = new LinkedHashMap<>();

	private final Map<ResourceLocation, Boolean> reachabilityCache = new ConcurrentHashMap<>();
	private Predicate<String> modLoadedChecker;

	public ItemDependencyGraph() {
		this(modId -> {
			try {
				return SoulHarvestMod.xplat().isModLoaded(modId);
			} catch (Throwable t) {
				return false;
			}
		});
	}

	public ItemDependencyGraph(Predicate<String> modLoadedChecker) {
		this.modLoadedChecker = Objects.requireNonNull(modLoadedChecker, "modLoadedChecker cannot be null");
	}

	public void setModLoadedChecker(Predicate<String> checker) {
		this.modLoadedChecker = Objects.requireNonNull(checker, "modLoadedChecker cannot be null");
		invalidateCache();
	}

	public boolean isModLoaded(String modId) {
		return modLoadedChecker.test(modId);
	}

	public synchronized ItemDependencyNode node(ResourceLocation id) {
		ItemDependencyNode node = nodes.computeIfAbsent(id, k -> new ItemDependencyNode(k, this));
		invalidateCache();
		return node;
	}

	public ItemDependencyNode node(ItemLike item) {
		//? >= 1.21 {
		return node(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()));
		//?} else {
		/*return node(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item.asItem()));
		*///?}
	}

	public synchronized void registerGroupMembership(ResourceLocation id, String group) {
		if (group != null && !group.isBlank()) {
			groupToItems.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(id);
			reachabilityCache.clear();
		}
	}

	public synchronized void bindConfig(BooleanSupplier option, ResourceLocation... items) {
		Objects.requireNonNull(option, "Config option cannot be null");
		for (ResourceLocation item : items) {
			itemConfigBindings.computeIfAbsent(item, k -> new ArrayList<>()).add(option);
		}
		invalidateCache();
	}

	public void bindConfig(BooleanSupplier option, ItemLike... items) {
		ResourceLocation[] ids = new ResourceLocation[items.length];
		for (int i = 0; i < items.length; i++) {
			//? >= 1.21 {
			ids[i] = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(items[i].asItem());
			//?} else {
			/*ids[i] = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(items[i].asItem());
			*///?}
		}
		bindConfig(option, ids);
	}

	public synchronized void bindConfigToGroup(BooleanSupplier option, String... groups) {
		Objects.requireNonNull(option, "Config option cannot be null");
		for (String group : groups) {
			if (group != null && !group.isBlank()) {
				groupConfigBindings.computeIfAbsent(group, k -> new ArrayList<>()).add(option);
			}
		}
		invalidateCache();
	}

	public synchronized void invalidateCache() {
		// Refresh groupToItems index from registered nodes
		groupToItems.clear();
		for (ItemDependencyNode node : nodes.values()) {
			for (String group : node.getGroups()) {
				groupToItems.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(node.getId());
			}
		}
		reachabilityCache.clear();
	}

	public boolean isReachable(ResourceLocation id) {
		Boolean cached = reachabilityCache.get(id);
		if (cached != null) {
			return cached;
		}

		boolean result = isReachable(id, new HashSet<>());
		reachabilityCache.put(id, result);
		return result;
	}

	public boolean isReachable(ResourceLocation id, Set<ResourceLocation> visited) {
		ItemDependencyNode node = nodes.get(id);
		// Items not in the graph default to being available
		if (node == null) {
			return true;
		}

		if (visited.contains(id)) {
			// Cycle detected
			return false;
		}

		// 1. Direct item config bindings: ALL must be true
		List<BooleanSupplier> itemBindings = itemConfigBindings.get(id);
		if (itemBindings != null) {
			for (BooleanSupplier supplier : itemBindings) {
				if (!supplier.getAsBoolean()) {
					return false;
				}
			}
		}

		// 2. Group config bindings: ALL options targeting ANY of this item's groups must be true
		for (String group : node.getGroups()) {
			List<BooleanSupplier> groupBindings = groupConfigBindings.get(group);
			if (groupBindings != null) {
				for (BooleanSupplier supplier : groupBindings) {
					if (!supplier.getAsBoolean()) {
						return false;
					}
				}
			}
		}

		// 3. Prerequisite item & mod dependencies
		visited.add(id);
		try {
			for (ItemDependency dependency : node.getDependencies()) {
				if (!dependency.isSatisfied(this, visited)) {
					return false;
				}
			}
		} finally {
			visited.remove(id);
		}

		return true;
	}

	public Set<ResourceLocation> getItemsInGroup(String group) {
		if (groupToItems.isEmpty() && !nodes.isEmpty()) {
			invalidateCache();
		}
		Set<ResourceLocation> items = groupToItems.get(group);
		if (items == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(items);
	}

	public Collection<ItemDependencyNode> getAllNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}

	public Set<String> getAllGroups() {
		if (groupToItems.isEmpty() && !nodes.isEmpty()) {
			invalidateCache();
		}
		return Collections.unmodifiableSet(groupToItems.keySet());
	}
}
