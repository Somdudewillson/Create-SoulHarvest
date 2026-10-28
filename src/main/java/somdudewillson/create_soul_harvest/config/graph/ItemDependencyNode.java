package somdudewillson.create_soul_harvest.config.graph;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.*;

public class ItemDependencyNode {
	private final ResourceLocation id;
	private final ItemDependencyGraph graph;
	private final Set<String> groups = new LinkedHashSet<>();
	private final List<ItemDependency> dependencies = new ArrayList<>();

	public ItemDependencyNode(ResourceLocation id, ItemDependencyGraph graph) {
		this.id = Objects.requireNonNull(id, "Node id cannot be null");
		this.graph = graph;
	}

	public ItemDependencyNode(ResourceLocation id) {
		this(id, null);
	}

	public ResourceLocation getId() {
		return id;
	}

	public Set<String> getGroups() {
		return Collections.unmodifiableSet(groups);
	}

	public List<ItemDependency> getDependencies() {
		return Collections.unmodifiableList(dependencies);
	}

	public ItemDependencyNode group(String group) {
		if (group != null && !group.isBlank()) {
			this.groups.add(group);
			if (graph != null) {
				graph.registerGroupMembership(this.id, group);
			}
		}
		return this;
	}

	public ItemDependencyNode groups(String... groups) {
		for (String g : groups) {
			group(g);
		}
		return this;
	}

	public ItemDependencyNode requires(ItemDependency dependency) {
		if (dependency != null) {
			this.dependencies.add(dependency);
		}
		return this;
	}

	public ItemDependencyNode requires(ResourceLocation dependencyId) {
		return requires(ItemDependency.item(dependencyId));
	}

	public ItemDependencyNode requires(ItemLike item) {
		//? >= 1.21 {
		return requires(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()));
		//?} else {
		/*return requires(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item.asItem()));
		*///?}
	}

	public ItemDependencyNode requiresAll(ResourceLocation... dependencyIds) {
		return requires(ItemDependency.all(dependencyIds));
	}

	public ItemDependencyNode requiresAny(ResourceLocation... dependencyIds) {
		return requires(ItemDependency.any(dependencyIds));
	}

	public ItemDependencyNode requiresMod(String modId) {
		return requires(ItemDependency.mod(modId));
	}
}
