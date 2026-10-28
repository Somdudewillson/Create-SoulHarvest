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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.simibubi.create.AllItems;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.MidasFluidBoostRecipe;
import somdudewillson.create_soul_harvest.content.recipe.condition.FluidExistsCondition;
import somdudewillson.create_soul_harvest.content.recipe.condition.ItemReachableCondition;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.enderio.EnderIOAlloySmeltingRecipeBuilder;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.enderio.EnderIOSagMillingRecipeBuilder;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.enderio.EnderIOSagMillingRecipeBuilder.SagBonusType;
import somdudewillson.create_soul_harvest.platform.neoforge.datagen.compat.mekanism.*;
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
	public static final float XP_BOOST_CATALYST_FAC = 2f;
	public static final int BOOST_CATALYST_MIN_Mb = 20;
	public static final int BOOST_CATALYST_DURATION_PER_XP = 20 * 15;
	public static final int BOOST_CATALYST_MIN_DURATION = 20 * 30;

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
		generateMidasCoreCatalystRecipes(recipeOutput, registries);
		generateEnderIOSagMillingRecipes(recipeOutput, registries);
		generateEnderIOAlloySmeltingRecipes(recipeOutput, registries);
		generateMekanismRecipes(recipeOutput, registries);
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
	
	private static void generateMidasCoreCatalystRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		for (ExperienceUnitRecord xpFluid : XP_FLUID_COMPAT_DATA) {
			int fluidAmt = BOOST_CATALYST_MIN_Mb;
			int duration = (int) (BOOST_CATALYST_DURATION_PER_XP * ((float)fluidAmt/xpFluid.xp()));
			int amtMult = 1;
			while (duration < BOOST_CATALYST_MIN_DURATION) {
				amtMult++;
				fluidAmt = BOOST_CATALYST_MIN_Mb*amtMult;
				duration = (int) (BOOST_CATALYST_DURATION_PER_XP * ((float)fluidAmt/xpFluid.xp()));
			}
			
			recipeOutput
				.withConditions(new ModLoadedCondition(xpFluid.modid()))
				.withConditions(new FluidExistsCondition(xpFluid.fluidId()))
				.accept(
					SoulHarvestMod.id("midas_fluid_boost/"+xpFluid.shortIdString()),
					new MidasFluidBoostRecipe(
							fluidIngredient(xpFluid.fluidId(), fluidAmt),
							duration,
							XP_BOOST_CATALYST_FAC
					),
					null
				);
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
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_INGOT.getId()))
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
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_PLATE.getId()))
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
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_ROD.getId()))
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
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_INGOT.getId()))
				.input(ModTags.Items.C_INGOTS_BRASS, 1)
				.input(ModTags.Items.C_DUSTS_OBSIDIAN, 4)
				.input(Items.WITHER_ROSE, 1)
				.energy(12800)
				.experience(0.3f)
				.output(ModItems.NECRONIUM_INGOT.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/enderio/alloy_smelting/necronium_ingot_raw"));
		
		// Brass Ingot + 8 Necronium Waste + Wither Rose -> Necronium Ingot
		EnderIOAlloySmeltingRecipeBuilder.alloySmelting()
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_INGOT.getId()))
				.input(ModTags.Items.C_INGOTS_BRASS, 1)
				.input(ModItems.NECRONIUM_WASTE.get(), 8)
				.input(Items.WITHER_ROSE, 1)
				.energy(11200)
				.experience(0.3f)
				.output(ModItems.NECRONIUM_INGOT.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/enderio/alloy_smelting/necronium_ingot_recycle"));
	}

	private static void generateMekanismRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
		// --- PRC
		
		// 1 Sand + 1000 mB Bioslurry + 1000 mB Water Vapor -> 2 Bone Meal
		MekanismReactionRecipeBuilder.reaction()
				.itemInput(ItemTags.SMELTS_TO_GLASS, 1)
				.fluidInput(ModFluids.BIOSLURRY.get(), 1000)
				.chemicalInput("mekanism:water_vapor", 1000)
				.duration(100)
				.itemOutput(Items.BONE_MEAL, 16)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/reaction/bone_meal_from_bioslurry_1"));
		
		// 1 Sand + 1000 mB Water + 1000 mB Biogas -> 2 Bone Meal
		MekanismReactionRecipeBuilder.reaction()
				.itemInput(ItemTags.SMELTS_TO_GLASS, 1)
				.fluidInput(Fluids.WATER, 1000)
				.chemicalInput(SoulHarvestMod.MOD_ID+":bioslurry", 1000)
				.duration(100)
				.itemOutput(Items.BONE_MEAL, 16)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/reaction/bone_meal_from_bioslurry_2"));

		// 1 Brass Ingot + 1000 mB Wither Slurry + 80 * 8 mB Refined Obsidian -> 1 Necronium Ingot
		MekanismReactionRecipeBuilder.reaction()
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_INGOT.getId()))
				.itemInput(ModTags.Items.C_INGOTS_BRASS, 1)
				.fluidInput(ModFluids.WITHER_SLURRY.get(), 1000)
				.chemicalInput("mekanism:refined_obsidian", 80 * 8)
				.duration(200)
				.energyRequired(100000)
				.itemOutput(ModItems.NECRONIUM_INGOT.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/reaction/necronium_ingot"));
		
		// --- Rotary

		// Bioslurry Fluid <=> Bioslurry Chemical
		MekanismRotaryRecipeBuilder.rotary()
				.fluid(ModFluids.BIOSLURRY.get())
				.chemical(SoulHarvestMod.id("bioslurry"))
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/rotary/bioslurry"));

		// Liquid Soul Crystal Fluid <=> Liquid Soul Crystal Chemical
		MekanismRotaryRecipeBuilder.rotary()
				.fluid(ModFluids.LIQUID_SOUL_CRYSTAL.get())
				.chemical(SoulHarvestMod.id("liquid_soul_crystal"))
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/rotary/liquid_soul_crystal"));
		
		// --- Enriching

		// 8 Organic Mulch -> 1 Bone Meal
		MekanismEnrichingRecipeBuilder.enriching()
				.input(ModItems.ORGANIC_MULCH.get(), 8)
				.output(Items.BONE_MEAL, 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/enriching/bone_meal_from_organic_mulch"));

		// Inlaid Lapis Lazuli -> Polished Inlaid Lapis Lazuli
		MekanismEnrichingRecipeBuilder.enriching()
				.withCondition(new ItemReachableCondition(ModItems.POLISHED_INLAID_LAPIS.getId()))
				.input(ModItems.INLAID_LAPIS.get(), 1)
				.output(ModItems.POLISHED_INLAID_LAPIS.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/enriching/polished_inlaid_lapis"));
		
		// --- Metallurgic Infusing

		// 1 Lapis Lazuli + 10 mB Gold -> 2 Inlaid Lapis Lazuli
		MekanismInfuserRecipeBuilder.metallurgicInfusing()
				.withCondition(new ItemReachableCondition(ModItems.INLAID_LAPIS.getId()))
				.itemInput(Items.LAPIS_LAZULI, 1)
				.chemicalInput("mekanism:gold", 10)
				.output(ModItems.INLAID_LAPIS.get(), 2)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/metallurgic_infusing/inlaid_lapis"));

		// Polished Inlaid Lapis + 24 mB Soulgas -> Enchanted Lapis Lazuli
		MekanismInfuserRecipeBuilder.metallurgicInfusing()
				.withCondition(new ItemReachableCondition(ModItems.ENCHANTED_INLAID_LAPIS.getId()))
				.itemInput(ModItems.POLISHED_INLAID_LAPIS.get(), 1)
				.chemicalInput(SoulHarvestMod.id("liquid_soul_crystal"), 24)
				.output(ModItems.ENCHANTED_INLAID_LAPIS.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/metallurgic_infusing/enchanted_inlaid_lapis"));

		// 1 Soul Shard + 80 mB Gold -> 1 Anima Gold
		MekanismInfuserRecipeBuilder.metallurgicInfusing()
				.withCondition(new ItemReachableCondition(ModItems.MAGIC_GOLD.getId()))
				.itemInput(ModItems.SOUL_SHARD.get(), 1)
				.chemicalInput("mekanism:gold", 80)
				.output(ModItems.MAGIC_GOLD.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/metallurgic_infusing/magic_gold"));
		
		// --- Combiner

		// 1 Clay Ball + 1 Create Experience Nugget -> 4 Polyclay
		MekanismCombinerRecipeBuilder.combining()
				.mainInput(AllItems.EXP_NUGGET, 1)
				.extraInput(Items.CLAY_BALL, 1)
				.output(ModItems.POLYCLAY.get(), 4)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/combining/polyclay"));

		// 1 Glass Bottle + 8 Necronium Waste -> 1 Necronium Waste Bottle
		MekanismCombinerRecipeBuilder.combining()
				.mainInput(ModItems.NECRONIUM_WASTE.get(), 8)
				.extraInput(Items.GLASS_BOTTLE, 1)
				.output(ModItems.NECRONIUM_WASTE_BOTTLE.get(), 1)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/combining/necronium_waste_bottle"));
		
		// --- Crushing

		MekanismCrushingRecipeBuilder.crushing()
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_INGOT.getId()))
				.input(ModItems.NECRONIUM_INGOT.get(), 1)
				.output(ModItems.NECRONIUM_WASTE.get(), 5)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/crushing/necronium_waste_from_ingot"));

		MekanismCrushingRecipeBuilder.crushing()
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_PLATE.getId()))
				.input(ModItems.NECRONIUM_PLATE.get(), 1)
				.output(ModItems.NECRONIUM_WASTE.get(), 4)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/crushing/necronium_waste_from_plate"));

		MekanismCrushingRecipeBuilder.crushing()
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_ROD.getId()))
				.input(ModItems.NECRONIUM_ROD.get(), 1)
				.output(ModItems.NECRONIUM_WASTE.get(), 4)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/crushing/necronium_waste_from_rod"));

		MekanismCrushingRecipeBuilder.crushing()
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_WASTE_BOTTLE.getId()))
				.input(ModItems.NECRONIUM_WASTE_BOTTLE.get(), 1)
				.output(ModItems.NECRONIUM_WASTE.get(), 8)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/crushing/necronium_waste_from_bottle"));

		// --- Dissolution

		// 1 Necronium Waste + Uranium Hexafluoride -> 1000 mB Nuclear Waste
		MekanismDissolutionRecipeBuilder.dissolution()
				.withCondition(new ItemReachableCondition(ModItems.NECRONIUM_WASTE.getId()))
				.itemInput(ModItems.NECRONIUM_WASTE.get(), 1)
				.chemicalInput("mekanism:uranium_hexafluoride", 4)
				.output("mekanism:nuclear_waste", 200)
				.perTickUsage(true)
				.save(recipeOutput, SoulHarvestMod.id("compat/mekanism/dissolution/nuclear_waste_from_necronium"));
	}
}
//?}
