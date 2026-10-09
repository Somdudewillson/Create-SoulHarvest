package somdudewillson.create_soul_harvest.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public final class AdvancementHelper {

	private AdvancementHelper() {}

	public static void grantAdvancement(ServerPlayer player, String name) {
		grantAdvancement(player, name, name);
	}

	public static void grantAdvancement(ServerPlayer player, String advancementName, String criterionName) {
		ResourceLocation id = SoulHarvestMod.id(advancementName);
		//? >= 1.21 {
		var adv = player.server.getAdvancements().get(id);
		if (adv != null) {
			player.getAdvancements().award(adv, criterionName);
		}
		//?} else {
		/*var adv = player.server.getAdvancements().getAdvancement(id);
		if (adv != null) {
			player.getAdvancements().award(adv, criterionName);
		}
		*///?}
	}
}
