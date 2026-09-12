package somdudewillson.create_soul_harvest.event;

import net.minecraft.server.level.ServerPlayer;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class ExampleEventHandler {

	public static void onPlayerHurt(ServerPlayer player) {
		SoulHarvestMod.LOGGER.info("{} took damage.", player.getDisplayName());
	}
}
