package somdudewillson.create_soul_harvest.platform.neoforge;

//? neoforge {

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import net.neoforged.fml.common.Mod;

@Mod(SoulHarvestMod.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint() {
		SoulHarvestMod.onInitialize();
	}
}
//?}
