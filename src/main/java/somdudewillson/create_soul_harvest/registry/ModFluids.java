package somdudewillson.create_soul_harvest.registry;

import com.simibubi.create.content.fluids.VirtualFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.util.function.Consumer;

public class ModFluids {

	public static class SimpleTexturedFluidType extends FluidType {
		public static final int NO_TINT = 0xffffffff;

		private static final ResourceLocation WATER_STILL = SoulHarvestMod.id("minecraft", "block/water_still");
		private static final ResourceLocation WATER_FLOWING = SoulHarvestMod.id("minecraft", "block/water_flow");

		private final ResourceLocation stillTexture;
		private final ResourceLocation flowingTexture;
		private final int tint;

		public SimpleTexturedFluidType(Properties properties, int tint) {
			this(properties, WATER_STILL, WATER_FLOWING, tint);
		}

		public SimpleTexturedFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
			this(properties, stillTexture, flowingTexture, NO_TINT);
		}

		public SimpleTexturedFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int tint) {
			super(properties);
			this.stillTexture = stillTexture;
			this.flowingTexture = flowingTexture;
			this.tint = tint;
		}

		@Override
		public void initializeClient(Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
			consumer.accept(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
				@Override
				public ResourceLocation getStillTexture() {
					return stillTexture;
				}

				@Override
				public ResourceLocation getFlowingTexture() {
					return flowingTexture;
				}

				@Override
				public int getTintColor() {
					return tint;
				}
			});
		}
	}

	public static final DeferredRegister<FluidType> FLUID_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, SoulHarvestMod.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS =
			DeferredRegister.create(Registries.FLUID, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<FluidType, SimpleTexturedFluidType> BIOSLURRY_TYPE =
			FLUID_TYPES.register("bioslurry", () -> new SimpleTexturedFluidType(FluidType.Properties.create(), 0xFF546924));
	public static final DeferredHolder<Fluid, VirtualFluid> BIOSLURRY =
			FLUIDS.register("bioslurry", () -> VirtualFluid.createSource(bioslurryProps()));
	public static final DeferredHolder<Fluid, VirtualFluid> BIOSLURRY_FLOWING =
			FLUIDS.register("flowing_bioslurry", () -> VirtualFluid.createFlowing(bioslurryProps()));

	public static final DeferredHolder<FluidType, SimpleTexturedFluidType> WITHER_SLURRY_TYPE =
			FLUID_TYPES.register("wither_slurry", () -> new SimpleTexturedFluidType(FluidType.Properties.create(), 0xFF37251B));
	public static final DeferredHolder<Fluid, VirtualFluid> WITHER_SLURRY =
			FLUIDS.register("wither_slurry", () -> VirtualFluid.createSource(witherSlurryProps()));
	public static final DeferredHolder<Fluid, VirtualFluid> WITHER_SLURRY_FLOWING =
			FLUIDS.register("flowing_wither_slurry", () -> VirtualFluid.createFlowing(witherSlurryProps()));

	public static final DeferredHolder<FluidType, SimpleTexturedFluidType> SILICA_SOLUTION_TYPE =
			FLUID_TYPES.register("silica_solution", () -> new SimpleTexturedFluidType(FluidType.Properties.create(), 0xFFDBD7CA));
	public static final DeferredHolder<Fluid, VirtualFluid> SILICA_SOLUTION =
			FLUIDS.register("silica_solution", () -> VirtualFluid.createSource(silicaProps()));
	public static final DeferredHolder<Fluid, VirtualFluid> SILICA_SOLUTION_FLOWING =
			FLUIDS.register("flowing_silica_solution", () -> VirtualFluid.createFlowing(silicaProps()));

	public static final DeferredHolder<FluidType, SimpleTexturedFluidType> LIQUID_SOUL_CRYSTAL_TYPE =
			FLUID_TYPES.register("liquid_soul_crystal", () -> new SimpleTexturedFluidType(FluidType.Properties.create()
					.descriptionId("fluid_type.create_soul_harvest.liquid_soul_crystal")
					.lightLevel(10)
					.density(3000)
					.viscosity(3000)
					.temperature(600),
					SoulHarvestMod.id("block/liquid_soul_crystal_still"),
					SoulHarvestMod.id("block/liquid_soul_crystal_flow")));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_SOUL_CRYSTAL =
			FLUIDS.register("liquid_soul_crystal", () -> new BaseFlowingFluid.Source(liquidSoulCrystalProps()));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> LIQUID_SOUL_CRYSTAL_FLOWING =
			FLUIDS.register("flowing_liquid_soul_crystal", () -> new BaseFlowingFluid.Flowing(liquidSoulCrystalProps()));

	private static BaseFlowingFluid.Properties bioslurryProps() {
		return new BaseFlowingFluid.Properties(BIOSLURRY_TYPE, BIOSLURRY, BIOSLURRY_FLOWING);
	}

	private static BaseFlowingFluid.Properties witherSlurryProps() {
		return new BaseFlowingFluid.Properties(WITHER_SLURRY_TYPE, WITHER_SLURRY, WITHER_SLURRY_FLOWING);
	}

	private static BaseFlowingFluid.Properties silicaProps() {
		return new BaseFlowingFluid.Properties(SILICA_SOLUTION_TYPE, SILICA_SOLUTION, SILICA_SOLUTION_FLOWING);
	}

	private static BaseFlowingFluid.Properties liquidSoulCrystalProps() {
		return new BaseFlowingFluid.Properties(LIQUID_SOUL_CRYSTAL_TYPE, LIQUID_SOUL_CRYSTAL, LIQUID_SOUL_CRYSTAL_FLOWING)
				.slopeFindDistance(2)
				.levelDecreasePerBlock(2)
				.tickRate(30)
				.explosionResistance(100f)
				.block(ModBlocks.LIQUID_SOUL_CRYSTAL)
				.bucket(ModItems.LIQUID_SOUL_CRYSTAL_BUCKET);
	}
}
