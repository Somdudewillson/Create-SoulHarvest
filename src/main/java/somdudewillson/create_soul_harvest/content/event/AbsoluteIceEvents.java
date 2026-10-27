package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import somdudewillson.create_soul_harvest.content.chunk.AbsoluteIceChunkHelper;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.ChunkEvent;
*///?}

public class AbsoluteIceEvents {

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (event.getChunk() instanceof LevelChunk chunk && event.getLevel() instanceof Level level) {
			AbsoluteIceChunkHelper.onChunkLoaded(level, chunk);
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (event.getChunk() instanceof LevelChunk chunk && event.getLevel() instanceof Level level) {
			AbsoluteIceChunkHelper.onChunkUnloaded(level, chunk);
		}
	}
}
