package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.server.MinecraftServer;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.client.SoulDataClientHelper;
import somdudewillson.create_soul_harvest.content.command.SoulHarvestCommands;
import somdudewillson.create_soul_harvest.content.recipe.RecipeSoulTracker;
import somdudewillson.create_soul_harvest.content.recipe.grinding.MobGrindingDynamicData;
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
			RecipeSoulTracker.scan(server.getRecipeManager());
			boolean changed = SoulDataManager.recalculate(server);
			if (changed) {
				MobGrindingDynamicData.updateRecipeManager(server);
				MobGrindingDynamicData.updateLootTables(server);
				onSoulDataUpdated();
			}
		}
	}

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent event) {
		RecipeSoulTracker.scan(event.getServer().getRecipeManager());
		boolean changed = SoulDataManager.recalculate(event.getServer());
		if (changed) {
			MobGrindingDynamicData.updateRecipeManager(event.getServer());
			MobGrindingDynamicData.updateLootTables(event.getServer());
			onSoulDataUpdated();
		}
	}

	public static void onSoulDataUpdated() {
		// If running client-side (integrated server), refresh creative tab contents and JEI
		try {
			if (SoulHarvestMod.xplat().isClient()) {
				ClientHandler.onSoulDataUpdated();
			}
		} catch (Throwable ignored) {
			// Environment without client classes or early initialization
		}
	}

	private static class ClientHandler {
		private static void onSoulDataUpdated() {
			SoulDataClientHelper.onSoulDataUpdated();
		}
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		SoulDataManager.clear();
		MobGrindingDynamicData.clear();
		RecipeSoulTracker.clear();
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
