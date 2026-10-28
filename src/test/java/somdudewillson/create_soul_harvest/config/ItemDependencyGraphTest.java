package somdudewillson.create_soul_harvest.config;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.config.graph.ItemDependency;
import somdudewillson.create_soul_harvest.config.graph.ItemDependencyGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ItemDependencyGraphTest {

	private ItemDependencyGraph graph;
	private Map<String, Boolean> loadedMods;

	private final ResourceLocation itemA = SoulHarvestMod.id("test", "item_a");
	private final ResourceLocation itemB = SoulHarvestMod.id("test", "item_b");
	private final ResourceLocation itemC = SoulHarvestMod.id("test", "item_c");
	private final ResourceLocation itemUnregistered = SoulHarvestMod.id("test", "unregistered");

	@BeforeEach
	public void setUp() {
		loadedMods = new HashMap<>();
		graph = new ItemDependencyGraph(modId -> loadedMods.getOrDefault(modId, false));
	}

	@Test
	public void testDefaultAvailability() {
		// Items not in the graph should default to being available
		assertTrue(graph.isReachable(itemUnregistered));
	}

	@Test
	public void testManualItemConfigBinding() {
		AtomicBoolean toggle = new AtomicBoolean(true);
		graph.node(itemA);
		graph.bindConfig(toggle::get, itemA);

		assertTrue(graph.isReachable(itemA));

		toggle.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemA));

		toggle.set(true);
		graph.invalidateCache();
		assertTrue(graph.isReachable(itemA));
	}

	@Test
	public void testGroupConfigBinding() {
		AtomicBoolean groupToggle = new AtomicBoolean(true);
		graph.node(itemA).group("biofuel");
		graph.node(itemB).group("biofuel");
		graph.bindConfigToGroup(groupToggle::get, "biofuel");

		assertTrue(graph.isReachable(itemA));
		assertTrue(graph.isReachable(itemB));

		groupToggle.set(false);
		graph.invalidateCache();

		assertFalse(graph.isReachable(itemA));
		assertFalse(graph.isReachable(itemB));
	}

	@Test
	public void testOverlappingGroupBindingsAllMustBeTrue() {
		AtomicBoolean option1 = new AtomicBoolean(true);
		AtomicBoolean option2 = new AtomicBoolean(true);

		graph.node(itemA).group("biofuel");
		graph.bindConfigToGroup(option1::get, "biofuel");
		graph.bindConfigToGroup(option2::get, "biofuel");

		// Both true -> reachable
		assertTrue(graph.isReachable(itemA));

		// Option 1 false, Option 2 true -> unreachable
		option1.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemA));

		// Option 1 true, Option 2 false -> unreachable
		option1.set(true);
		option2.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemA));

		// Both true -> reachable
		option2.set(true);
		graph.invalidateCache();
		assertTrue(graph.isReachable(itemA));
	}

	@Test
	public void testOverlappingItemAndGroupBindings() {
		AtomicBoolean groupOption = new AtomicBoolean(true);
		AtomicBoolean itemOption = new AtomicBoolean(true);

		graph.node(itemA).group("biofuel");
		graph.bindConfigToGroup(groupOption::get, "biofuel");
		graph.bindConfig(itemOption::get, itemA);

		assertTrue(graph.isReachable(itemA));

		// Group disabled -> item is unreachable
		groupOption.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemA));

		// Group enabled, item option disabled -> unreachable
		groupOption.set(true);
		itemOption.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemA));

		// Both enabled -> reachable
		itemOption.set(true);
		graph.invalidateCache();
		assertTrue(graph.isReachable(itemA));
	}

	@Test
	public void testPrerequisiteDependencies() {
		AtomicBoolean mulchToggle = new AtomicBoolean(true);
		graph.node(itemA); // Mulch
		graph.node(itemB).requires(itemA); // Biofuel block requires mulch
		graph.bindConfig(mulchToggle::get, itemA);

		assertTrue(graph.isReachable(itemB));

		// If mulch is disabled, biofuel block becomes unreachable
		mulchToggle.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemB));
	}

	@Test
	public void testRequiresAll() {
		AtomicBoolean toggleA = new AtomicBoolean(true);
		AtomicBoolean toggleB = new AtomicBoolean(true);

		graph.node(itemA);
		graph.node(itemB);
		graph.node(itemC).requiresAll(itemA, itemB);

		graph.bindConfig(toggleA::get, itemA);
		graph.bindConfig(toggleB::get, itemB);

		assertTrue(graph.isReachable(itemC));

		toggleA.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemC));

		toggleA.set(true);
		toggleB.set(false);
		graph.invalidateCache();
		assertFalse(graph.isReachable(itemC));

		toggleB.set(true);
		graph.invalidateCache();
		assertTrue(graph.isReachable(itemC));
	}

	@Test
	public void testRequiresAny() {
		AtomicBoolean toggleA = new AtomicBoolean(false);
		AtomicBoolean toggleB = new AtomicBoolean(false);

		graph.node(itemA);
		graph.node(itemB);
		graph.node(itemC).requiresAny(itemA, itemB);

		graph.bindConfig(toggleA::get, itemA);
		graph.bindConfig(toggleB::get, itemB);

		// Both false -> unreachable
		assertFalse(graph.isReachable(itemC));

		// A true -> reachable
		toggleA.set(true);
		graph.invalidateCache();
		assertTrue(graph.isReachable(itemC));

		// B true, A false -> reachable
		toggleA.set(false);
		toggleB.set(true);
		graph.invalidateCache();
		assertTrue(graph.isReachable(itemC));
	}

	@Test
	public void testRequiresMod() {
		graph.node(itemA).requiresMod("mekanism");

		// Mod not loaded
		assertFalse(graph.isReachable(itemA));

		// Mod loaded
		loadedMods.put("mekanism", true);
		graph.invalidateCache();
		assertTrue(graph.isReachable(itemA));
	}

	@Test
	public void testCycleProtection() {
		// Circular dependency A -> B -> A
		graph.node(itemA).requires(itemB);
		graph.node(itemB).requires(itemA);

		// Must not throw StackOverflowError and must resolve safely
		assertFalse(graph.isReachable(itemA));
		assertFalse(graph.isReachable(itemB));
	}

	@Test
	public void testItemsInGroup() {
		graph.node(itemA).group("test_group");
		graph.node(itemB).group("test_group");

		Set<ResourceLocation> groupItems = graph.getItemsInGroup("test_group");
		assertEquals(2, groupItems.size());
		assertTrue(groupItems.contains(itemA));
		assertTrue(groupItems.contains(itemB));

		Set<ResourceLocation> emptyGroup = graph.getItemsInGroup("non_existent");
		assertTrue(emptyGroup.isEmpty());
	}
}
