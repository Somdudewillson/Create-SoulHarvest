package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.server.MinecraftServer;
import somdudewillson.create_soul_harvest.content.command.SoulHarvestCommands;
import somdudewillson.create_soul_harvest.content.soul.SoulDataLoader;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
*///?}

public class SoulHarvestDataEvents {

	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event) {
		event.addListener(SoulDataLoader.INSTANCE);
	}

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			SoulDataManager.recalculate(server);
		}
	}

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent event) {
		SoulDataManager.recalculate(event.getServer());
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		SoulDataManager.clear();
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		SoulHarvestCommands.register(event.getDispatcher());
	}

	//? neoforge {
	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		SoulDataManager.tick(event.getServer());
	}
	//?} forge {
	/*@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			SoulDataManager.tick(event.getServer());
		}
	}
	*///?}
}
