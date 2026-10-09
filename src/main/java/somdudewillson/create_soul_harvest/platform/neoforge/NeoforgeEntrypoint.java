package somdudewillson.create_soul_harvest.platform.neoforge;

//? neoforge {

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.event.SoulHarvestGameEvents;
import somdudewillson.create_soul_harvest.registry.*;

@Mod(SoulHarvestMod.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint(IEventBus modBus) {
		SoulHarvestMod.onInitialize();

		ModBlocks.BLOCKS.register(modBus);
		ModItems.ITEMS.register(modBus);
		ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
		ModMobEffects.MOB_EFFECTS.register(modBus);
		ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);
		ModFluids.FLUID_TYPES.register(modBus);
		ModFluids.FLUIDS.register(modBus);

		NeoForge.EVENT_BUS.register(SoulHarvestGameEvents.class);
	}
}
//?}
