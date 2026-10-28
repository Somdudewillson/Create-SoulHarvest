package somdudewillson.create_soul_harvest.config.graph;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

@FunctionalInterface
public interface ItemDependency {
	boolean isSatisfied(ItemDependencyGraph graph, Set<ResourceLocation> visited);

	static ItemDependency item(ResourceLocation id) {
		return (graph, visited) -> !visited.contains(id) && graph.isReachable(id, visited);
	}

	static ItemDependency all(ResourceLocation... ids) {
		return (graph, visited) -> {
			for (ResourceLocation id : ids) {
				if (visited.contains(id) || !graph.isReachable(id, visited)) {
					return false;
				}
			}
			return true;
		};
	}

	static ItemDependency any(ResourceLocation... ids) {
		return (graph, visited) -> {
			if (ids.length == 0) {
				return true;
			}
			for (ResourceLocation id : ids) {
				if (!visited.contains(id) && graph.isReachable(id, visited)) {
					return true;
				}
			}
			return false;
		};
	}

	static ItemDependency mod(String modId) {
		return (graph, visited) -> graph.isModLoaded(modId);
	}

	static ItemDependency and(ItemDependency... dependencies) {
		return (graph, visited) -> {
			for (ItemDependency dep : dependencies) {
				if (!dep.isSatisfied(graph, visited)) {
					return false;
				}
			}
			return true;
		};
	}

	static ItemDependency or(ItemDependency... dependencies) {
		return (graph, visited) -> {
			if (dependencies.length == 0) {
				return true;
			}
			for (ItemDependency dep : dependencies) {
				if (dep.isSatisfied(graph, visited)) {
					return true;
				}
			}
			return false;
		};
	}
}
