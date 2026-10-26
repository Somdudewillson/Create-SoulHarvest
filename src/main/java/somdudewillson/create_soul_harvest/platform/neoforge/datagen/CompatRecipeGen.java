package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe.Builder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.simibubi.create.AllItems;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.condition.FluidExistsCondition;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.enderio.EnderIOAlloySmeltingRecipeBuilder;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.enderio.EnderIOSagMillingRecipeBuilder;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.enderio.EnderIOSagMillingRecipeBuilder.SagBonusType;
import somdudewillson.create_soul_harvest.registry.ModFluids;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModTags;

public final class CompatRecipeGen {
	private CompatRecipeGen() {	}

	protected static record ExperienceUnitRecord(
			String modid,
			ResourceLocation fluidId,
			int xp,
			List<ICondition> extraConditions
	) {
		public ExperienceUnitRecord(String modid, ResourceLocation fluidId, int xp) {
			this(modid, fluidId, xp, List.of());
		}
		public ExperienceUnitRecord(String modid, String fluidId, int xp) {
			this(modid, ResourceLocation.fromNamespaceAndPath(modid, fluidId), xp, List.of());
		}
		public ExperienceUnitRecord(String modid, String fluidId, int xp, ICondition... extraConditions) {
			this(modid, ResourceLocation.fromNamespaceAndPath(modid, fluidId), xp, List.of(extraConditions));
		}

		public String shortIdString() {
			return shortIdString(10);
		}
		public String shortIdString(int partLimit) {
			return shortIdString(partLimit, partLimit);
		}
		public String shortIdString(int namespaceLimit, int pathLimit) {
			String namespacePart = fluidId.getNamespace();
			namespacePart = namespacePart.substring(0, Math.min(namespaceLimit, namespacePart.length()));
			String pathPart = fluidId.getPath();
			pathPart = pathPart.substring(0, Math.min(pathLimit, pathPart.length()));
			return namespacePart+"_"+pathPart;
		}
	}

	public static final int XP_PER_POLYCLAY_CRAFT = 3;
	public static final int POLYCLAY_PER_CRAFT = 2;
	public static final int XP_BOOST_LIQUID_SOUL_YIELD = 6;

	public static List<ExperienceUnitRecord> XP_FLUID_COMPAT_DATA = ImmutableList.of(
			new ExperienceUnitRecord("create_enchantment_industry", "experience", 1),
			new ExperienceUnitRecord("cofh_core", "experience", 25),
			new ExperienceUnitRecord("cyclic", "xpjuice", 20),
			new ExperienceUnitRecord("enderio", "xpjuice", 20),
			new ExperienceUnitRecord("enderio", "fluid_xp_juice_still", 20),
			new ExperienceUnitRecord("industrialforegoing", "essence", 20),
			new ExperienceUnitRecord("justdirethings", "xp_fluid_source", 20),
			new ExperienceUnitRecord("mob_grinding_utils", "fluid_xp", 20),
			new ExperienceUnitRecord("pneumaticcraft", "memory_essence", 20),
			new ExperienceUnitRecord("reliquary", "xp_juice_still", 20),
			new ExperienceUnitRecord("sophisticatedcore", "xp_still", 20)
	);

	public static SizedFluidIngredient fluidIngredient(ResourceLocation fluidId, int amount) {
		Holder.Reference<Fluid> holder = Holder.Reference.createStandAlone(
				BuiltInRegistries.FLUID.holderOwner(),
				ResourceKey.create(Registries.FLUID, fluidId)
		);
		return new SizedFluidIngredient(FluidIngredient.single(holder), amount);
	}

	public static void register(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		generatePolyclayCompatRecipes(recipeOutput, registries);
		generateFluidProductionCompatRecipes(recipeOutput, registries);
		generateEnderIOSagMillingRecipes(recipeOutput, registries);
		generateEnderIOAlloySmeltingRecipes(recipeOutput, registries);
	}

	private static void generatePolyclayCompatRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		for (ExperienceUnitRecord xpFluid : XP_FLUID_COMPAT_DATA) {
			int fluidMb = xpFluid.xp() * XP_PER_POLYCLAY_CRAFT;
			int craftBatch = 4;

			Builder<MixingRecipe> recipeBuilder = new StandardProcessingRecipe.Builder<>(
							MixingRecipe::new,
							SoulHarvestMod.id("compat/create_mix_polyclay_" + xpFluid.shortIdString())
					)
					.whenModLoaded(xpFluid.modid())
					.withCondition(new FluidExistsCondition(xpFluid.fluidId()));
			
			for (ICondition extraCondition : xpFluid.extraConditions()) {
				recipeBuilder = recipeBuilder.withCondition(extraCondition);
			}

			for (int i = 0; i < craftBatch; i++) {
				recipeBuilder = recipeBuilder.require(Items.CLAY_BALL);
			}

			recipeBuilder = recipeBuilder.require(fluidIngredient(xpFluid.fluidId(), fluidMb));

			int totalOutput = craftBatch * POLYCLAY_PER_CRAFT;
			while (totalOutput > 0) {
				int count = Math.min(totalOutput, 64);
				recipeBuilder = recipeBuilder.output(ModItems.POLYCLAY.get(), count);
				totalOutput -= count;
			}

			recipeBuilder.build(recipeOutput);
		}
	}
	
	private static void generateFluidProductionCompatRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		for (ExperienceUnitRecord xpFluid : XP_FLUID_COMPAT_DATA) {
			int fluidMb = xpFluid.xp() * XP_BOOST_LIQUID_SOUL_YIELD;

			new StandardProcessingRecipe.Builder<>(
							MixingRecipe::new,
							SoulHarvestMod.id("compat/mix_shards_to_liquid_boosted_" + xpFluid.shortIdString())
					)
					.whenModLoaded(xpFluid.modid())
					.withCondition(new FluidExistsCondition(xpFluid.fluidId()))
					.require(ModItems.SOUL_SHARD.get())
					.require(ModFluids.SILICA_SOLUTION.get(), 24)
					.require(fluidIngredient(xpFluid.fluidId(), fluidMb))
					.requiresHeat(HeatCondition.HEATED)
					.output(ModFluids.LIQUID_SOUL_CRYSTAL.get(), 18)
					.build(recipeOutput);
		}
	}

	private static void generateEnderIOSagMillingRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		// Soul Shard -> Create Experience Nugget
		EnderIOSagMillingRecipeBuilder.sagMilling()
				.input(ModItems.SOUL_SHARD.get())
				.energy(2400)
				.output(ResourceLocation.fromNamespaceAndPath("create", "experience_nugget"), 1, 0.05f)
				.save(recipeOutput);

		// Necronium Ingot -> Necronium Waste
		EnderIOSagMillingRecipeBuilder.sagMilling()
				.input(ModItems.NECRONIUM_INGOT.get())
				.energy(3600)
				.bonus(SagBonusType.CHANCE_ONLY)
				.output(ModItems.NECRONIUM_WASTE.get(), 1)
				.output(ModItems.NECRONIUM_WASTE.get(), 3, 0.438f)
				.output(ModItems.NECRONIUM_WASTE.get(), 1, 0.438f)
				.output(ModItems.NECRONIUM_WASTE.get(), 1, 0.438f)
				.save(recipeOutput);

		// Necronium Plate -> Necronium Waste
		EnderIOSagMillingRecipeBuilder.sagMilling()
				.input(ModItems.NECRONIUM_PLATE.get())
				.energy(3600)
				.bonus(SagBonusType.CHANCE_ONLY)
				.output(ModItems.NECRONIUM_WASTE.get(), 1)
				.output(ModItems.NECRONIUM_WASTE.get(), 2, 0.421f)
				.output(ModItems.NECRONIUM_WASTE.get(), 1, 0.421f)
				.output(ModItems.NECRONIUM_WASTE.get(), 1, 0.421f)
				.save(recipeOutput);

		// Necronium Rod -> Necronium Waste
		EnderIOSagMillingRecipeBuilder.sagMilling()
				.input(ModItems.NECRONIUM_ROD.get())
				.energy(2400)
				.bonus(SagBonusType.CHANCE_ONLY)
				.output(ModItems.NECRONIUM_WASTE.get(), 1)
				.output(ModItems.NECRONIUM_WASTE.get(), 1, 0.4f)
				.output(ModItems.NECRONIUM_WASTE.get(), 1, 0.4f)
				.save(recipeOutput);

		// Necronium Waste Bottle -> Necronium Waste + Glass Bottle
		EnderIOSagMillingRecipeBuilder.sagMilling()
				.input(ModItems.NECRONIUM_WASTE_BOTTLE.get())
				.energy(1200)
				.bonus(SagBonusType.NONE)
				.output(ModItems.NECRONIUM_WASTE.get(), 8)
				.output(Items.SAND, 1, 0.5f)
				.save(recipeOutput);
	}

	private static void generateEnderIOAlloySmeltingRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		// Brass Ingot + 4 Obsidian Dust + Wither Rose -> Necronium Ingot
		EnderIOAlloySmeltingRecipeBuilder.alloySmelting()
				.input(ModTags.Items.C_INGOTS_BRASS, 1)
				.input(ModTags.Items.C_DUSTS_OBSIDIAN, 4)
				.input(Items.WITHER_ROSE, 1)
				.energy(12800)
				.experience(0.3f)
				.output(ModItems.NECRONIUM_INGOT.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/enderio/alloy_smelting/necronium_ingot_raw"));
		
		// Brass Ingot + 8 Necronium Waste + Wither Rose -> Necronium Ingot
		EnderIOAlloySmeltingRecipeBuilder.alloySmelting()
				.input(ModTags.Items.C_INGOTS_BRASS, 1)
				.input(ModItems.NECRONIUM_WASTE.get(), 8)
				.input(Items.WITHER_ROSE, 1)
				.energy(11200)
				.experience(0.3f)
				.output(ModItems.NECRONIUM_INGOT.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/enderio/alloy_smelting/necronium_ingot_recycle"));
	}
}
//?}
