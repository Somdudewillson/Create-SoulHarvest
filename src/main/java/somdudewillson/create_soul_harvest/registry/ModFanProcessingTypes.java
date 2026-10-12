package somdudewillson.create_soul_harvest.registry;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.kinetics.fan.processing.SoulInfusionType;

//? neoforge {
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
//?} forge {
/*import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
*///?}

public class ModFanProcessingTypes {

	//? neoforge {
	public static final DeferredRegister<FanProcessingType> FAN_PROCESSING_TYPES =
			DeferredRegister.create(CreateRegistries.FAN_PROCESSING_TYPE, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<FanProcessingType, SoulInfusionType> SOUL_INFUSION =
			FAN_PROCESSING_TYPES.register("soul_infusion", SoulInfusionType::new);
	//?} forge {
	/*public static final DeferredRegister<FanProcessingType> FAN_PROCESSING_TYPES =
			DeferredRegister.create(CreateRegistries.FAN_PROCESSING_TYPE, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<SoulInfusionType> SOUL_INFUSION =
			FAN_PROCESSING_TYPES.register("soul_infusion", SoulInfusionType::new);
	*///?}
}
