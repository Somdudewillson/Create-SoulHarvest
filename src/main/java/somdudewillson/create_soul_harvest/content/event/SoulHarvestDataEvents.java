package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.server.MinecraftServer;
import somdudewillson.create_soul_harvest.compat.jei.CreateSoulHarvestJeiPlugin;
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
			onSoulDataUpdated();
		}
	}

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent event) {
		SoulDataManager.recalculate(event.getServer());
		onSoulDataUpdated();
	}

	private static void onSoulDataUpdated() {
		// If running client-side (integrated server), refresh creative tab contents and JEI
		try {
			boolean isClient = false;
			//? neoforge {
			isClient = net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT;
			//?} forge {
			/*isClient = net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT;
			*///?}
			if (isClient) {
				net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
				if (mc != null) {
					mc.execute(() -> {
						if (mc.getConnection() != null && mc.level != null) {
							net.minecraft.world.item.CreativeModeTabs.tryRebuildTabContents(
									mc.getConnection().enabledFeatures(),
									mc.options.operatorItemsTab().get(),
									mc.level.registryAccess()
							);
						}
						CreateSoulHarvestJeiPlugin.refreshSoulBooksInJei();
					});
				}
			}
		} catch (Throwable ignored) {
			// Environment without client classes or early initialization
		}
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
