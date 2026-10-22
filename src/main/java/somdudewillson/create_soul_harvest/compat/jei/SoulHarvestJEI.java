package somdudewillson.create_soul_harvest.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.compat.jei.category.FanSoulInfusionCategory;
import somdudewillson.create_soul_harvest.compat.jei.category.LootTokenCategory;
import somdudewillson.create_soul_harvest.compat.jei.recipe.LootTokenJeiRecipe;
import somdudewillson.create_soul_harvest.content.item.LootTokenItem;
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
	//?} else {
	/*public static final mezz.jei.api.recipe.RecipeType<SoulInfusionRecipe> SOUL_INFUSION_TYPE =
			new mezz.jei.api.recipe.RecipeType<>(SoulHarvestMod.id("soul_infusion"), SoulInfusionRecipe.class);
	*///?}

	private FanSoulInfusionCategory category;
	private LootTokenCategory lootTokenCategory;

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
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (category != null) {
			category.registerRecipes(registration);
		}

		List<LootTokenJeiRecipe> tokenRecipes = new ArrayList<>();
		HolderLookup.Provider registries = Minecraft.getInstance().level != null
				? Minecraft.getInstance().level.registryAccess()
				: null;
		Map<ResourceLocation, SoulData> knownSouls = SoulDataManager.getKnownSoulData();

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

			if (!drops.isEmpty()) {
				tokenRecipes.add(new LootTokenJeiRecipe(tokenStack, lootTableId, displayName, drops));
			}
		}

		if (!tokenRecipes.isEmpty()) {
			registration.addRecipes(LootTokenCategory.TYPE, tokenRecipes);
		}
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		if (category != null) {
			category.registerCatalysts(registration);
		}
		registration.addRecipeCatalyst(new ItemStack(ModItems.LOOT_TOKEN.get()), LootTokenCategory.TYPE);
	}
}
