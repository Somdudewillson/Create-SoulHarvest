package somdudewillson.create_soul_harvest.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.config.graph.ModDependencyGraph;
import somdudewillson.create_soul_harvest.content.item.ExperienceDropAttunedItem;
import somdudewillson.create_soul_harvest.content.item.LootTokenItem;
import somdudewillson.create_soul_harvest.content.item.PolyclayAttunedItem;
import somdudewillson.create_soul_harvest.content.item.PolyclayItem;
import somdudewillson.create_soul_harvest.content.item.SoulBookItem;
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;

public class ModCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SoulHarvestMod.MOD_ID);

	private static final List<DeferredHolder<Item, ? extends Item>> MAIN_TAB_ITEMS = new ArrayList<>();

	public static void addToMainTab(DeferredHolder<Item, ? extends Item> item) {
		MAIN_TAB_ITEMS.add(item);
	}

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("creative_tab_main",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.create_soul_harvest"))
					.icon(() -> new ItemStack(ModItems.SOUL_SHARD.get()))
					.displayItems((parameters, output) -> {
						MAIN_TAB_ITEMS.forEach(holder -> {
							if (isItemReachable(holder.getId())) {
								output.accept(holder.get());
							}
						});
					})
					.build());

	public static List<ItemStack> generateAllCompletedSoulBooks(@Nullable HolderLookup.Provider registries) {
		List<ItemStack> list = new ArrayList<>();
		Map<ResourceLocation, SoulData> dataMap = SoulDataManager.getAllResolvedData();

		List<Map.Entry<ResourceLocation, SoulData>> sortedEntries = dataMap.entrySet()
				.stream()
				.filter(e -> e.getValue() != null)
				.filter(e -> e.getValue().xp() > 0 || e.getValue().hasDrops(registries, true))
				.collect(Collectors.toCollection(ArrayList::new));

		sortedEntries.sort((e1, e2) -> {
			int tier1 = e1.getValue() != null ? e1.getValue().tier() : 0;
			int tier2 = e2.getValue() != null ? e2.getValue().tier() : 0;
			if (tier1 != tier2) {
				return Integer.compare(tier1, tier2);
			}

			String name1 = getEntityDisplayName(e1.getKey());
			String name2 = getEntityDisplayName(e2.getKey());
			return name1.compareToIgnoreCase(name2);
		});

		for (Map.Entry<ResourceLocation, SoulData> entry : sortedEntries) {
			ResourceLocation entityId = entry.getKey();
			SoulData soulData = entry.getValue();
			if (soulData == null) continue;

			Item bookItem = switch (soulData.tier()) {
				case 1 -> ModItems.SOUL_BOOK_SIMPLE.get();
				case 2 -> ModItems.SOUL_BOOK_STANDARD.get();
				default -> ModItems.SOUL_BOOK_SUPERIOR.get();
			};

			list.add(SoulBookItem.createCompletedSoulBook(bookItem, entityId));
		}
		return list;
	}

	public static List<ItemStack> generateAllAttunedPolyclay(@Nullable HolderLookup.Provider registries) {
		List<ItemStack> list = new ArrayList<>();
		Map<ResourceLocation, SoulData> dataMap = SoulDataManager.getKnownSoulData();

		List<Map.Entry<ResourceLocation, SoulData>> sortedEntries = dataMap.entrySet()
				.stream()
				.filter(e -> e.getValue() != null && e.getValue().hasDrops(registries, true))
				.collect(Collectors.toCollection(ArrayList::new));

		sortedEntries.sort((e1, e2) -> {
			int tier1 = e1.getValue() != null ? e1.getValue().tier() : 0;
			int tier2 = e2.getValue() != null ? e2.getValue().tier() : 0;
			if (tier1 != tier2) {
				return Integer.compare(tier1, tier2);
			}

			String name1 = getEntityDisplayName(e1.getKey());
			String name2 = getEntityDisplayName(e2.getKey());
			return name1.compareToIgnoreCase(name2);
		});

		Map<Integer, PolyclayItem> polyclays = new HashMap<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (!(item instanceof PolyclayItem polyclay)) continue;
			if (polyclay.getAttunedItem() == null) continue;
			
			for (int t=polyclay.getTier(); t <= polyclay.getTier()+1; t++) {
				PolyclayItem existing = polyclays.get(t);
				int tierDiff = Math.abs(t-polyclay.getTier());
				int existingTierDiff = existing!=null ? Math.abs(t-existing.getTier()) : Integer.MAX_VALUE;
				
				if (existing == null
						|| (tierDiff < existingTierDiff)
						|| (existingTierDiff == tierDiff && item.getDescriptionId().compareTo(existing.getDescriptionId())<0)) {
					polyclays.put(t, polyclay);
				}
			}
		}

		for (Map.Entry<ResourceLocation, SoulData> entry : sortedEntries) {
			ResourceLocation entityId = entry.getKey();
			SoulData soulData = entry.getValue();
			int mobTier = soulData != null ? soulData.tier() : 0;
			
			if (!polyclays.containsKey(mobTier)) continue;
			Item attunedItem = polyclays.get(mobTier).getAttunedItem();
			if (soulData != null && soulData.hasDrops(registries, false)) {
				list.add(PolyclayAttunedItem.createAttunedPolyclay(attunedItem, entityId, false));
			}
			if (soulData != null && soulData.hasEnhancedSpecificDrops(registries)) {
				list.add(PolyclayAttunedItem.createAttunedPolyclay(attunedItem, entityId, true));
			}
		}
		return list;
	}

	public static List<ItemStack> generateAllCompletedLootTokens(@Nullable HolderLookup.Provider registries) {
		List<ItemStack> list = new ArrayList<>();
		Map<ResourceLocation, SoulData> dataMap = SoulDataManager.getKnownSoulData();

		List<Map.Entry<ResourceLocation, SoulData>> sortedEntries = dataMap.entrySet()
				.stream()
				.filter(e -> e.getValue() != null && e.getValue().hasLootTokenDrops(registries))
				.collect(Collectors.toCollection(ArrayList::new));

		sortedEntries.sort((e1, e2) -> {
			int tier1 = e1.getValue() != null ? e1.getValue().tier() : 0;
			int tier2 = e2.getValue() != null ? e2.getValue().tier() : 0;
			if (tier1 != tier2) {
				return Integer.compare(tier1, tier2);
			}

			String name1 = getEntityDisplayName(e1.getKey());
			String name2 = getEntityDisplayName(e2.getKey());
			return name1.compareToIgnoreCase(name2);
		});

		for (Map.Entry<ResourceLocation, SoulData> entry : sortedEntries) {
			ResourceLocation entityId = entry.getKey();
			ResourceLocation lootTableId = SoulHarvestMod.id("loot_tokens/" + entityId.getNamespace() + "/" + entityId.getPath());
			String mobLocKey = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId)
					.map(EntityType::getDescriptionId)
					.orElse("entity." + entityId.getNamespace() + "." + entityId.getPath());

			list.add(LootTokenItem.createWithLootTable(ModItems.LOOT_TOKEN.get(), lootTableId, mobLocKey));
		}
		return list;
	}

	public static List<ItemStack> generateAllAttunedExperienceDrops(@Nullable HolderLookup.Provider registries) {
		List<ItemStack> list = new ArrayList<>();
		Map<ResourceLocation, SoulData> dataMap = SoulDataManager.getKnownSoulData();

		List<Map.Entry<ResourceLocation, SoulData>> sortedEntries = dataMap.entrySet()
				.stream()
				.filter(e -> e.getValue() != null && e.getValue().xp() > 0)
				.collect(Collectors.toCollection(ArrayList::new));

		sortedEntries.sort((e1, e2) -> {
			int tier1 = e1.getValue() != null ? e1.getValue().tier() : 0;
			int tier2 = e2.getValue() != null ? e2.getValue().tier() : 0;
			if (tier1 != tier2) {
				return Integer.compare(tier1, tier2);
			}

			String name1 = getEntityDisplayName(e1.getKey());
			String name2 = getEntityDisplayName(e2.getKey());
			return name1.compareToIgnoreCase(name2);
		});

		List<Item> expDropItems = new ArrayList<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof ExperienceDropAttunedItem) {
				expDropItems.add(item);
			}
		}
		if (expDropItems.isEmpty() && ModItems.EXPERIENCE_EFFIGY_ATTUNED != null && ModItems.EXPERIENCE_EFFIGY_ATTUNED.get() != null) {
			expDropItems.add(ModItems.EXPERIENCE_EFFIGY_ATTUNED.get());
		}

		for (Map.Entry<ResourceLocation, SoulData> entry : sortedEntries) {
			ResourceLocation entityId = entry.getKey();
			for (Item attunedItem : expDropItems) {
				list.add(ExperienceDropAttunedItem.createAttunedExperienceDrop(attunedItem, entityId));
			}
		}
		return list;
	}

	private static String getEntityDisplayName(ResourceLocation id) {
		Optional<EntityType<?>> entityType = EntityType.byString(id.toString());
		if (entityType.isPresent()) {
			return Component.translatable(entityType.get().getDescriptionId()).getString();
		}
		return id.getPath();
	}

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SOUL_ITEMS_TAB = CREATIVE_MODE_TABS.register(
				"creative_tab_soul_items",
				() -> CreativeModeTab.builder()
						.title(Component.translatable("itemGroup.create_soul_harvest_soul_items"))
						.icon(() -> new ItemStack(ModItems.SOUL_BOOK_SUPERIOR.get()))
						.displayItems((parameters, output) -> {
							for (ItemStack bookStack : generateAllCompletedSoulBooks(parameters.holders())) {
								output.accept(bookStack);
							}
							for (ItemStack polyclayStack : generateAllAttunedPolyclay(parameters.holders())) {
								output.accept(polyclayStack);
							}
							for (ItemStack expDropStack : generateAllAttunedExperienceDrops(parameters.holders())) {
								output.accept(expDropStack);
							}
							for (ItemStack tokenStack : generateAllCompletedLootTokens(parameters.holders())) {
								output.accept(tokenStack);
							}
						})
						.build()
			);

	public static boolean isItemReachable(ResourceLocation id) {
		return ModDependencyGraph.get().isReachable(id);
	}

	public static boolean isStackReachable(ItemStack stack) {
		//? >= 1.21 {
		ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
		//?} else {
		/*ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
		*///?}
		return isItemReachable(key);
	}

	public static void refreshCreativeTabs() {
		try {
			var mc = Minecraft.getInstance();
			if (mc.level != null && mc.getConnection() != null) {
				mc.execute(() -> {
					try {
						CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(
								mc.getConnection().enabledFeatures(),
								mc.options.operatorItemsTab().get(),
								mc.level.registryAccess()
						);
						if (MAIN_TAB != null && MAIN_TAB.get() != null) {
							MAIN_TAB.get().buildContents(params);
						}
						if (SOUL_ITEMS_TAB != null && SOUL_ITEMS_TAB.get() != null) {
							SOUL_ITEMS_TAB.get().buildContents(params);
						}
					} catch (Throwable ignored) {
					}
				});
			}
		} catch (Throwable ignored) {
		}
	}
}
