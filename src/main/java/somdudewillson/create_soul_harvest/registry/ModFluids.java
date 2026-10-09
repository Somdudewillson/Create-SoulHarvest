package somdudewillson.create_soul_harvest.registry;

import com.simibubi.create.content.fluids.VirtualFluid;
import net.minecraft.world.level.material.Fluid;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

//? neoforge {
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.core.registries.Registries;
//?} forge {
/*import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?}

import java.util.function.Consumer;

public class ModFluids {

	public static class TintedFluidType extends FluidType {
		private final int tint;

		public TintedFluidType(Properties properties, int tint) {
			super(properties);
			this.tint = tint;
		}

		//? neoforge {
		@Override
		public void initializeClient(Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
			consumer.accept(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
				@Override
				public int getTintColor() {
					return tint;
				}
			});
		}
		//?} forge {
		/*@Override
		public void initializeClient(Consumer<net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
			consumer.accept(new net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions() {
				@Override
				public int getTintColor() {
					return tint;
				}
			});
		}
		*///?}
	}

	//? neoforge {
	public static final DeferredRegister<FluidType> FLUID_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, SoulHarvestMod.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS =
			DeferredRegister.create(Registries.FLUID, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<FluidType, TintedFluidType> BIOSLURRY_TYPE =
			FLUID_TYPES.register("bioslurry", () -> new TintedFluidType(FluidType.Properties.create(), 0xFF546924));
	public static final DeferredHolder<Fluid, VirtualFluid> BIOSLURRY =
			FLUIDS.register("bioslurry", () -> VirtualFluid.createSource(bioslurryProps()));
	public static final DeferredHolder<Fluid, VirtualFluid> BIOSLURRY_FLOWING =
			FLUIDS.register("flowing_bioslurry", () -> VirtualFluid.createFlowing(bioslurryProps()));

	public static final DeferredHolder<FluidType, TintedFluidType> WITHER_SLURRY_TYPE =
			FLUID_TYPES.register("wither_slurry", () -> new TintedFluidType(FluidType.Properties.create(), 0xFF37251B));
	public static final DeferredHolder<Fluid, VirtualFluid> WITHER_SLURRY =
			FLUIDS.register("wither_slurry", () -> VirtualFluid.createSource(witherSlurryProps()));
	public static final DeferredHolder<Fluid, VirtualFluid> WITHER_SLURRY_FLOWING =
			FLUIDS.register("flowing_wither_slurry", () -> VirtualFluid.createFlowing(witherSlurryProps()));

	public static final DeferredHolder<FluidType, TintedFluidType> SILICA_SOLUTION_TYPE =
			FLUID_TYPES.register("silica_solution", () -> new TintedFluidType(FluidType.Properties.create(), 0xFFDBD7CA));
	public static final DeferredHolder<Fluid, VirtualFluid> SILICA_SOLUTION =
			FLUIDS.register("silica_solution", () -> VirtualFluid.createSource(silicaProps()));
	public static final DeferredHolder<Fluid, VirtualFluid> SILICA_SOLUTION_FLOWING =
			FLUIDS.register("flowing_silica_solution", () -> VirtualFluid.createFlowing(silicaProps()));

	private static BaseFlowingFluid.Properties bioslurryProps() {
		return new BaseFlowingFluid.Properties(BIOSLURRY_TYPE, BIOSLURRY, BIOSLURRY_FLOWING);
	}

	private static BaseFlowingFluid.Properties witherSlurryProps() {
		return new BaseFlowingFluid.Properties(WITHER_SLURRY_TYPE, WITHER_SLURRY, WITHER_SLURRY_FLOWING);
	}

	private static BaseFlowingFluid.Properties silicaProps() {
		return new BaseFlowingFluid.Properties(SILICA_SOLUTION_TYPE, SILICA_SOLUTION, SILICA_SOLUTION_FLOWING);
	}
	//?} forge {
	/*public static final DeferredRegister<FluidType> FLUID_TYPES =
			DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, SoulHarvestMod.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS =
			DeferredRegister.create(ForgeRegistries.FLUIDS, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<TintedFluidType> BIOSLURRY_TYPE =
			FLUID_TYPES.register("bioslurry", () -> new TintedFluidType(FluidType.Properties.create(), 0xFF546924));
	public static final RegistryObject<VirtualFluid> BIOSLURRY =
			FLUIDS.register("bioslurry", () -> VirtualFluid.createSource(bioslurryProps()));
	public static final RegistryObject<VirtualFluid> BIOSLURRY_FLOWING =
			FLUIDS.register("flowing_bioslurry", () -> VirtualFluid.createFlowing(bioslurryProps()));

	public static final RegistryObject<TintedFluidType> WITHER_SLURRY_TYPE =
			FLUID_TYPES.register("wither_slurry", () -> new TintedFluidType(FluidType.Properties.create(), 0xFF37251B));
	public static final RegistryObject<VirtualFluid> WITHER_SLURRY =
			FLUIDS.register("wither_slurry", () -> VirtualFluid.createSource(witherSlurryProps()));
	public static final RegistryObject<VirtualFluid> WITHER_SLURRY_FLOWING =
			FLUIDS.register("flowing_wither_slurry", () -> VirtualFluid.createFlowing(witherSlurryProps()));

	public static final RegistryObject<TintedFluidType> SILICA_SOLUTION_TYPE =
			FLUID_TYPES.register("silica_solution", () -> new TintedFluidType(FluidType.Properties.create(), 0xFFDBD7CA));
	public static final RegistryObject<VirtualFluid> SILICA_SOLUTION =
			FLUIDS.register("silica_solution", () -> VirtualFluid.createSource(silicaProps()));
	public static final RegistryObject<VirtualFluid> SILICA_SOLUTION_FLOWING =
			FLUIDS.register("flowing_silica_solution", () -> VirtualFluid.createFlowing(silicaProps()));

	private static ForgeFlowingFluid.Properties bioslurryProps() {
		return new ForgeFlowingFluid.Properties(BIOSLURRY_TYPE, BIOSLURRY, BIOSLURRY_FLOWING);
	}

	private static ForgeFlowingFluid.Properties witherSlurryProps() {
		return new ForgeFlowingFluid.Properties(WITHER_SLURRY_TYPE, WITHER_SLURRY, WITHER_SLURRY_FLOWING);
	}

	private static ForgeFlowingFluid.Properties silicaProps() {
		return new ForgeFlowingFluid.Properties(SILICA_SOLUTION_TYPE, SILICA_SOLUTION, SILICA_SOLUTION_FLOWING);
	}
	*///?}
}
