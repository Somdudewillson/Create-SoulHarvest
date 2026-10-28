//? neoforge {
package somdudewillson.create_soul_harvest.compat.mekanism;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class MekanismRegistration {

	public static void register(IEventBus modBus) {
		modBus.addListener(MekanismRegistration::onRegister);
	}

	public static void onRegister(RegisterEvent event) {
		if (event.getRegistryKey().equals(MekanismAPI.CHEMICAL_REGISTRY_NAME)) {
			registerChemical(event, "bioslurry", 0x546924);
			registerChemical(event, "liquid_soul_crystal", 0xBD42FF);
		}
	}

	private static void registerChemical(RegisterEvent event, String path, int tint) {
		ResourceLocation id = SoulHarvestMod.id(path);
		event.register(MekanismAPI.CHEMICAL_REGISTRY_NAME, id, () -> {
			Chemical chemical = new Chemical(
					ChemicalBuilder.builder(ResourceLocation.fromNamespaceAndPath(MekanismCompat.MEKANISM_MODID, "liquid/steam"))
							.tint(tint)
			);
			SoulHarvestMod.LOGGER.info("Registered Mekanism unified chemical: {}", id);
			return chemical;
		});
	}
}
//?} forge {
/*package somdudewillson.create_soul_harvest.compat.mekanism;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasBuilder;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfuseTypeBuilder;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class MekanismRegistration {

	public static void register(IEventBus modBus) {
		modBus.addListener(MekanismRegistration::onRegister);
	}

	public static void onRegister(RegisterEvent event) {
		if (event.getRegistryKey().equals(MekanismAPI.GAS_REGISTRY_NAME)) {
			registerGas(event, "bioslurry", 0x546924);
			registerGas(event, "liquid_soul_crystal", 0xBD42FF);
		} else if (event.getRegistryKey().equals(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME)) {
			registerInfuseType(event, "liquid_soul_crystal", 0xBD42FF);
		} else if (event.getRegistryKey().equals(MekanismAPI.SLURRY_REGISTRY_NAME)) {
			registerSlurry(event, "bioslurry", 0x546924);
		}
	}

	private static void registerSlurry(RegisterEvent event, String path, int tint) {
		ResourceLocation id = SoulHarvestMod.id(path);
		event.register(MekanismAPI.SLURRY_REGISTRY_NAME, id, () -> {
			Slurry slurry = new Slurry(SlurryBuilder.clean().tint(tint));
			SoulHarvestMod.LOGGER.info("Registered Mekanism legacy slurry: {}", id);
			return slurry;
		});
	}

	private static void registerGas(RegisterEvent event, String path, int tint) {
		ResourceLocation id = SoulHarvestMod.id(path);
		event.register(MekanismAPI.GAS_REGISTRY_NAME, id, () -> {
			Gas gas = new Gas(
					GasBuilder.builder(new ResourceLocation(MekanismCompat.MEKANISM_MODID, "liquid/steam"))
							.tint(tint)
			);
			SoulHarvestMod.LOGGER.info("Registered Mekanism legacy gas: {}", id);
			return gas;
		});
	}

	private static void registerInfuseType(RegisterEvent event, String path, int tint) {
		ResourceLocation id = SoulHarvestMod.id(path);
		event.register(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME, id, () -> {
			InfuseType infuseType = new InfuseType(
					InfuseTypeBuilder.builder(new ResourceLocation(MekanismCompat.MEKANISM_MODID, "infuse_type/bio"))
							.tint(tint)
			);
			SoulHarvestMod.LOGGER.info("Registered Mekanism legacy infuse type: {}", id);
			return infuseType;
		});
	}
}
*///?}
