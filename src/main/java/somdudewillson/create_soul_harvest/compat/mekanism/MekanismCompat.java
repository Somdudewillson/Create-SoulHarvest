package somdudewillson.create_soul_harvest.compat.mekanism;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Supplier;

//? neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.RegisterEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegisterEvent;
*///?}

public class MekanismCompat {
	public static final String MEKANISM_MODID = "mekanism";
	public static final ResourceLocation MEK_CHEMICAL_REGISTRY = ResourceLocation.fromNamespaceAndPath(MEKANISM_MODID, "chemical");
	public static final ResourceLocation MEK_SLURRY_REGISTRY = ResourceLocation.fromNamespaceAndPath(MEKANISM_MODID, "slurry");
	public static final ResourceLocation MEK_GAS_REGISTRY = ResourceLocation.fromNamespaceAndPath(MEKANISM_MODID, "gas");

	public static void register(IEventBus modBus) {
		modBus.addListener(MekanismCompat::onRegister);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void onRegister(RegisterEvent event) {
		if (!ModList.get().isLoaded(MEKANISM_MODID)) {
			return;
		}

		ResourceLocation regLoc = event.getRegistryKey().location();

		// NeoForge 1.21.1: Unified Chemical registry
		if (MEK_CHEMICAL_REGISTRY.equals(regLoc)) {
			registerUnifiedChemical(event, "bioslurry", 0x546924);
			registerUnifiedChemical(event, "liquid_soul_crystal", 0xBD42FF);
			return;
		}

		// Forge 1.20.1: Slurry registry
		if (MEK_SLURRY_REGISTRY.equals(regLoc)) {
			registerLegacySlurry(event, "bioslurry", 0x546924);
			return;
		}

		// Forge 1.20.1: Gas registry
		if (MEK_GAS_REGISTRY.equals(regLoc)) {
			registerLegacyGas(event, "liquid_soul_crystal", 0xBD42FF);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void registerUnifiedChemical(RegisterEvent event, String path, int tint) {
		ResourceLocation id = SoulHarvestMod.id(path);
		event.register((ResourceKey<? extends Registry<Object>>) (ResourceKey) event.getRegistryKey(), id, () -> {
			try {
				Class<?> builderClass = Class.forName("mekanism.api.chemical.ChemicalBuilder");
				Method builderMethod = builderClass.getMethod("builder", ResourceLocation.class);
				Object builder = builderMethod.invoke(null, ResourceLocation.fromNamespaceAndPath(MEKANISM_MODID, "liquid/steam"));
				Method tintMethod = builderClass.getMethod("tint", int.class);
				builder = tintMethod.invoke(builder, tint);
				Class<?> chemicalClass = Class.forName("mekanism.api.chemical.Chemical");
				Constructor<?> ctor = chemicalClass.getConstructor(builderClass);
				Object chemical = ctor.newInstance(builder);
				SoulHarvestMod.LOGGER.info("Registered Mekanism unified chemical: {}", id);
				return chemical;
			} catch (Throwable t) {
				SoulHarvestMod.LOGGER.error("Failed to register Mekanism chemical " + id, t);
				return null;
			}
		});
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void registerLegacySlurry(RegisterEvent event, String path, int tint) {
		ResourceLocation id = SoulHarvestMod.id(path);
		event.register((ResourceKey<? extends Registry<Object>>) (ResourceKey) event.getRegistryKey(), id, () -> {
			try {
				Class<?> builderClass = Class.forName("mekanism.api.chemical.slurry.SlurryBuilder");
				Method cleanMethod = builderClass.getMethod("clean");
				Object builder = cleanMethod.invoke(null);
				try {
					Method colorMethod = builderClass.getMethod("color", int.class);
					builder = colorMethod.invoke(builder, tint);
				} catch (NoSuchMethodException ignored) {
				}
				Class<?> slurryClass = Class.forName("mekanism.api.chemical.slurry.Slurry");
				Constructor<?> ctor = slurryClass.getConstructor(builderClass);
				Object slurry = ctor.newInstance(builder);
				SoulHarvestMod.LOGGER.info("Registered Mekanism legacy slurry: {}", id);
				return slurry;
			} catch (Throwable t) {
				SoulHarvestMod.LOGGER.error("Failed to register Mekanism legacy slurry " + id, t);
				return null;
			}
		});
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void registerLegacyGas(RegisterEvent event, String path, int tint) {
		ResourceLocation id = SoulHarvestMod.id(path);
		event.register((ResourceKey<? extends Registry<Object>>) (ResourceKey) event.getRegistryKey(), id, () -> {
			try {
				Class<?> builderClass = Class.forName("mekanism.api.chemical.gas.GasBuilder");
				Method builderMethod = builderClass.getMethod("builder", ResourceLocation.class);
				Object builder = builderMethod.invoke(null, ResourceLocation.fromNamespaceAndPath(MEKANISM_MODID, "liquid/steam"));
				try {
					Method colorMethod = builderClass.getMethod("color", int.class);
					builder = colorMethod.invoke(builder, tint);
				} catch (NoSuchMethodException ignored) {
				}
				Class<?> gasClass = Class.forName("mekanism.api.chemical.gas.Gas");
				Constructor<?> ctor = gasClass.getConstructor(builderClass);
				Object gas = ctor.newInstance(builder);
				SoulHarvestMod.LOGGER.info("Registered Mekanism legacy gas: {}", id);
				return gas;
			} catch (Throwable t) {
				SoulHarvestMod.LOGGER.error("Failed to register Mekanism legacy gas " + id, t);
				return null;
			}
		});
	}
}
