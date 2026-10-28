package somdudewillson.create_soul_harvest.compat.mekanism;

import somdudewillson.create_soul_harvest.SoulHarvestMod;

//? neoforge {
import net.neoforged.bus.api.IEventBus;
//?} forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
*///?}

public class MekanismCompat {
	public static final String MEKANISM_MODID = "mekanism";

	public static void register(IEventBus modBus) {
		if (!SoulHarvestMod.xplat().isModLoaded(MEKANISM_MODID)) {
			return;
		}
		MekanismRegistration.register(modBus);
	}
}
