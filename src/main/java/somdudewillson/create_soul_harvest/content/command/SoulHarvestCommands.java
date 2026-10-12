package somdudewillson.create_soul_harvest.content.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;

public class SoulHarvestCommands {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("create_soul_harvest")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("drop_cache_flush")
								.executes(context -> {
									MinecraftServer server = context.getSource().getServer();
									int queued = SoulDataManager.flushDropCache(server);
									context.getSource().sendSuccess(
											() -> Component.literal("Flushed Soul Harvest computed drop cache (" + queued + " entities queued for computation)."),
											true
									);
									return 1;
								})
						)
						.then(Commands.literal("drop_cache_progress")
								.executes(context -> {
									SoulDataManager.ScanProgress progress = SoulDataManager.getScanProgress();
									if (progress.isComplete()) {
										context.getSource().sendSuccess(
												() -> Component.literal(String.format(
														"Drop Scan Status: Complete (0 remaining). %d cached tables, %d active capturable entities.",
														progress.cachedCount(), progress.resolvedCount()
												)),
												false
										);
									} else {
										context.getSource().sendSuccess(
												() -> Component.literal(String.format(
														"Drop Scan Status: In Progress — %d/%d completed (%.1f%%, %d remaining). %d cached tables, %d active capturable entities.",
														progress.completed(), progress.totalBatch(), progress.percentage(), progress.remaining(),
														progress.cachedCount(), progress.resolvedCount()
												)),
												false
										);
									}
									return 1;
								})
						)
		);
	}
}
