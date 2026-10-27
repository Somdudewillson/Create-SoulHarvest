package somdudewillson.create_soul_harvest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public class AbsoluteIceClientHelper {

	public static void updateBiomeVisuals(BlockPos pos, double radius) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.levelRenderer != null) {
			ClientLevel clientLevel = mc.level;
			clientLevel.clearTintCaches();
			int r = Math.max(1, (int) Math.ceil(radius));
			mc.levelRenderer.setBlocksDirty(
					pos.getX() - r, pos.getY() - 16, pos.getZ() - r,
					pos.getX() + r, pos.getY() + 16, pos.getZ() + r
			);
		}
	}
}
