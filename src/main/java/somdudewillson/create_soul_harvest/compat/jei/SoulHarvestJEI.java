package somdudewillson.create_soul_harvest.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.compat.jei.category.FanSoulInfusionCategory;
import somdudewillson.create_soul_harvest.compat.jei.category.LootTokenCategory;
import somdudewillson.create_soul_harvest.compat.jei.category.MidasFluidBoostCategory;
import somdudewillson.create_soul_harvest.compat.jei.category.SoulBookCategory;
import somdudewillson.create_soul_harvest.compat.jei.recipe.LootTokenJeiRecipe;
import somdudewillson.create_soul_harvest.compat.jei.recipe.SoulBookJeiRecipe;
import somdudewillson.create_soul_harvest.content.item.LootTokenItem;
import somdudewillson.create_soul_harvest.content.item.SoulBookItem;
import somdudewillson.create_soul_harvest.content.recipe.MidasFluidBoostRecipe;
import somdudewillson.create_soul_harvest.content.recipe.SoulInfusionRecipe;
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;
import somdudewillson.create_soul_harvest.content.soul.SoulDropEntry;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

//? >= 1.21 {
import net.minecraft.world.item.crafting.RecipeHolder;
//?}

@JeiPlugin
@ParametersAreNonnullByDefault
public class SoulHarvestJEI implements IModPlugin {
	public static final ResourceLocation ID = SoulHarvestMod.id("jei_recipes");

	//? >= 1.21 {
	public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<SoulInfusionRecipe>> SOUL_INFUSION_TYPE =
			mezz.jei.api.recipe.RecipeType.createRecipeHolderType(SoulHarvestMod.id("soul_infusion"));
	public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<MidasFluidBoostRecipe>> MIDAS_FLUID_BOOST_TYPE =
			mezz.jei.api.recipe.RecipeType.createRecipeHolderType(SoulHarvestMod.id("midas_fluid_boost"));
	//?} else {
	/*public static final mezz.jei.api.recipe.RecipeType<SoulInfusionRecipe> SOUL_INFUSION_TYPE =
			new mezz.jei.api.recipe.RecipeType<>(SoulHarvestMod.id("soul_infusion"), SoulInfusionRecipe.class);
	public static final mezz.jei.api.recipe.RecipeType<MidasFluidBoostRecipe> MIDAS_FLUID_BOOST_TYPE =
			new mezz.jei.api.recipe.RecipeType<>(SoulHarvestMod.id("midas_fluid_boost"), MidasFluidBoostRecipe.class);
	*///?}

	public static final int LOOT_TOKEN_PAGE_SIZE = 16;

	private FanSoulInfusionCategory category;
	private LootTokenCategory lootTokenCategory;
	private MidasFluidBoostCategory midasFluidBoostCategory;
	private SoulBookCategory soulBookCategory;

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		CreateRecipeCategory.Info<SoulInfusionRecipe> info = new CreateRecipeCategory.Info<>(
				SOUL_INFUSION_TYPE,
				Component.translatable("create_soul_harvest.recipe.soul_infusion"),
				registration.getJeiHelpers().getGuiHelper().createBlankDrawable(178, 72),
				new DoubleItemIcon(() -> new ItemStack(AllItems.PROPELLER.get()), () -> new ItemStack(ModBlocks.SOUL_CLUSTER.get())),
				() -> {
					ClientPacketListener connection = Minecraft.getInstance().getConnection();
					if (connection != null) {
						return connection.getRecipeManager().getAllRecipesFor(ModRecipeTypes.SOUL_INFUSION.getType());
					}
					return Collections.emptyList();
				},
				List.of(
						ProcessingViaFanCategory.getFan("soul_infusion"),
						() -> new ItemStack(ModBlocks.SOUL_CLUSTER.get())
				)
		);
		this.category = new FanSoulInfusionCategory(info);
		registration.addRecipeCategories(category);

		this.lootTokenCategory = new LootTokenCategory();
		registration.addRecipeCategories(lootTokenCategory);

		CreateRecipeCategory.Info<MidasFluidBoostRecipe> boostInfo = new CreateRecipeCategory.Info<>(
				MIDAS_FLUID_BOOST_TYPE,
				Component.translatable("create_soul_harvest.recipe.midas_fluid_boost"),
				registration.getJeiHelpers().getGuiHelper().createBlankDrawable(178, 72),
				new ItemIcon(() -> new ItemStack(ModBlocks.MIDAS_CORE.get())),
				() -> {
					ClientPacketListener connection = Minecraft.getInstance().getConnection();
					if (connection != null) {
						return connection.getRecipeManager().getAllRecipesFor(ModRecipeTypes.MIDAS_FLUID_BOOST.getType());
					}
					return Collections.emptyList();
				},
				List.of(() -> new ItemStack(ModBlocks.MIDAS_CORE.get()))
		);
		this.midasFluidBoostCategory = new MidasFluidBoostCategory(boostInfo);
		registration.addRecipeCategories(midasFluidBoostCategory);

		this.soulBookCategory = new SoulBookCategory();
		registration.addRecipeCategories(soulBookCategory);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (category != null) {
			category.registerRecipes(registration);
		}
		if (midasFluidBoostCategory != null) {
			midasFluidBoostCategory.registerRecipes(registration);
		}

		HolderLookup.Provider registries = Minecraft.getInstance().level != null
				? Minecraft.getInstance().level.registryAccess()
				: null;
		Map<ResourceLocation, SoulData> knownSouls = SoulDataManager.getKnownSoulData();

		List<LootTokenJeiRecipe> tokenRecipes = buildLootTokenRecipes(knownSouls, registries, LOOT_TOKEN_PAGE_SIZE);
		if (!tokenRecipes.isEmpty()) {
			registration.addRecipes(LootTokenCategory.TYPE, tokenRecipes);
		}

		List<SoulBookJeiRecipe> soulBookRecipes = buildSoulBookRecipes(knownSouls, registries);
		if (!soulBookRecipes.isEmpty()) {
			registration.addRecipes(SoulBookCategory.TYPE, soulBookRecipes);
		}
	}

	public static List<LootTokenJeiRecipe> buildLootTokenRecipes(
			Map<ResourceLocation, SoulData> knownSouls,
			@javax.annotation.Nullable HolderLookup.Provider registries,
			int pageSize
	) {
		List<LootTokenJeiRecipe> tokenRecipes = new ArrayList<>();

		for (Map.Entry<ResourceLocation, SoulData> entry : knownSouls.entrySet()) {
			ResourceLocation entityId = entry.getKey();
			SoulData soulData = entry.getValue();
			if (!soulData.hasLootTokenDrops(registries)) {
				continue;
			}
			List<SoulDropEntry> dropEntries = soulData.getLootTokenEntries(registries);
			if (dropEntries.isEmpty()) {
				continue;
			}

			ResourceLocation lootTableId = SoulHarvestMod.id("loot_tokens/" + entityId.getNamespace() + "/" + entityId.getPath());
			String mobLocKey = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId)
					.map(EntityType::getDescriptionId)
					.orElse("entity." + entityId.getNamespace() + "." + entityId.getPath());

			ItemStack tokenStack = LootTokenItem.createWithLootTable(ModItems.LOOT_TOKEN.get(), lootTableId, mobLocKey);
			Component displayName = LootTokenItem.getLootTableDisplayName(tokenStack);

			float totalWeight = (float) dropEntries.stream().mapToInt(SoulDropEntry::weight).sum();
			List<LootTokenJeiRecipe.DropInfo> drops = new ArrayList<>();
			for (SoulDropEntry de : dropEntries) {
				Item item = BuiltInRegistries.ITEM.get(de.item());
				if (item != null && item != Items.AIR) {
					drops.add(new LootTokenJeiRecipe.DropInfo(
							new ItemStack(item, Math.max(1, de.min())),
							de.min(),
							de.max(),
							de.weight(),
							totalWeight
					));
				}
			}

			if (drops.isEmpty()) {
				continue;
			}

			// Sort by weight descending, then by item description id for deterministic order
			drops.sort(Comparator.comparingInt(LootTokenJeiRecipe.DropInfo::weight).reversed()
					.thenComparing(d -> d.stack().getDescriptionId()));

			int totalDrops = drops.size();
			int totalPages = (totalDrops + pageSize - 1) / pageSize;
			for (int page = 0; page < totalPages; page++) {
				int from = page * pageSize;
				int to = Math.min(totalDrops, from + pageSize);
				List<LootTokenJeiRecipe.DropInfo> pageDrops = drops.subList(from, to);
				tokenRecipes.add(new LootTokenJeiRecipe(
						tokenStack,
						lootTableId,
						displayName,
						pageDrops,
						page,
						totalPages,
						totalDrops
				));
			}
		}

		return tokenRecipes;
	}

	public static List<SoulBookJeiRecipe> buildSoulBookRecipes(
			Map<ResourceLocation, SoulData> knownSouls,
			@javax.annotation.Nullable HolderLookup.Provider registries
	) {
		List<SoulBookJeiRecipe> recipes = new ArrayList<>();

		List<Map.Entry<ResourceLocation, SoulData>> sortedEntries = knownSouls.entrySet()
				.stream()
				.filter(e -> e.getValue() != null && e.getValue().tier() > 0 && e.getValue().progressPerKill() > 0)
				.filter(e -> e.getValue().xp() > 0 || e.getValue().hasDrops(registries, true))
				.collect(Collectors.toCollection(ArrayList::new));

		sortedEntries.sort((e1, e2) -> {
			int tier1 = e1.getValue().tier();
			int tier2 = e2.getValue().tier();
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

			int tier = soulData.tier();
			int progress = soulData.progressPerKill();
			int killsNeeded = (100 + progress - 1) / progress;

			Optional<EntityType<?>> entityTypeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
			Component displayName = entityTypeOpt
					.map(EntityType::getDescriptionId)
					.map(Component::translatable)
					.orElse(Component.literal(entityId.toString()));

			ItemStack spawnEgg = ItemStack.EMPTY;
			if (entityTypeOpt.isPresent()) {
				SpawnEggItem egg = SpawnEggItem.byId(entityTypeOpt.get());
				if (egg != null) {
					spawnEgg = new ItemStack(egg);
				}
			}

			List<ItemStack> inputBooks = new ArrayList<>();
			List<ItemStack> outputBooks = new ArrayList<>();

			if (tier <= 1) {
				inputBooks.add(new ItemStack(ModItems.SOUL_BOOK_SIMPLE.get()));
				outputBooks.add(SoulBookItem.createCompletedSoulBook(ModItems.SOUL_BOOK_SIMPLE.get(), entityId));
			}
			if (tier <= 2) {
				inputBooks.add(new ItemStack(ModItems.SOUL_BOOK_STANDARD.get()));
				outputBooks.add(SoulBookItem.createCompletedSoulBook(ModItems.SOUL_BOOK_STANDARD.get(), entityId));
			}
			if (tier <= 3) {
				inputBooks.add(new ItemStack(ModItems.SOUL_BOOK_SUPERIOR.get()));
				outputBooks.add(SoulBookItem.createCompletedSoulBook(ModItems.SOUL_BOOK_SUPERIOR.get(), entityId));
			}

			recipes.add(new SoulBookJeiRecipe(
					entityId,
					displayName,
					tier,
					killsNeeded,
					progress,
					inputBooks,
					spawnEgg,
					outputBooks
			));
		}

		return recipes;
	}

	private static String getEntityDisplayName(ResourceLocation id) {
		return BuiltInRegistries.ENTITY_TYPE.getOptional(id)
				.map(t -> Component.translatable(t.getDescriptionId()).getString())
				.orElse(id.getPath());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		if (category != null) {
			category.registerCatalysts(registration);
		}
		if (midasFluidBoostCategory != null) {
			midasFluidBoostCategory.registerCatalysts(registration);
		}
		registration.addRecipeCatalyst(new ItemStack(ModItems.LOOT_TOKEN.get()), LootTokenCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.SOUL_BOOK_SIMPLE.get()), SoulBookCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.SOUL_BOOK_STANDARD.get()), SoulBookCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.SOUL_BOOK_SUPERIOR.get()), SoulBookCategory.TYPE);
	}
}
