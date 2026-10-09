package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
*///?}

import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlockEntity;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class AnimaGlassEvents {

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		if (player.isCreative()) return;

		if (event.getState().is(ModBlocks.MAGIC_GLASS.get())) {
			if (event.getPosition().isPresent()) {
				BlockEntity be = player.level().getBlockEntity(event.getPosition().get());
				if (be instanceof AnimaGlassBlockEntity animaBe) {
					if (animaBe.getOwnerId() != null && !player.getUUID().equals(animaBe.getOwnerId())) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
