package somdudewillson.create_soul_harvest.client.texture.palette;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Client-side dev command to dump generated mob pattern textures and an HTML visual catalog.
 */
public class ClientPatternDumpCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("csh_client")
						.then(Commands.literal("dump_patterns")
								.then(Commands.argument("entity", ResourceLocationArgument.id())
										.executes(context -> {
											ResourceLocation entityId = ResourceLocationArgument.getId(context, "entity");
											executeDump(context.getSource(), entityId);
											return 1;
										})
								)
								.executes(context -> {
									executeDump(context.getSource(), null);
									return 1;
								})
						)
		);
	}

	private static void executeDump(CommandSourceStack source, ResourceLocation targetEntityId) {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			source.sendFailure(Component.literal("Minecraft client instance is unavailable."));
			return;
		}

		if (mc.level == null) {
			source.sendFailure(Component.literal("Cannot dump mob patterns: client world is not loaded. Please join a world first."));
			return;
		}

		source.sendSuccess(
				() -> Component.literal("Generating and dumping mob pattern textures..."),
				false
		);

		Path outputDir = mc.gameDirectory.toPath().resolve("test_samples").resolve("mob_patterns");

		MobPatternTextureManager.INSTANCE.generateTexturesAsync(targetEntityId, () -> {
			CompletableFuture.runAsync(() -> {
				try {
					int exported = MobPatternTextureManager.dumpTexturesToDisk(outputDir, targetEntityId);
					if (exported == 0) {
						source.sendFailure(Component.literal("No mob patterns were generated or exported."));
						return;
					}

					Component link = Component.literal("[Open Folder]")
							.withStyle(style -> style
									.withColor(ChatFormatting.GREEN)
									.withUnderlined(true)
									.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, outputDir.toAbsolutePath().toString()))
									.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open output folder")))
							);

					source.sendSuccess(
							() -> Component.literal(String.format("Exported %d pattern textures to %s ", exported, outputDir.getFileName()))
									.append(link),
							false
					);
				} catch (Throwable t) {
					SoulHarvestMod.LOGGER.error("Failed to dump mob pattern textures", t);
					source.sendFailure(Component.literal("Error dumping pattern textures: " + t.getMessage()));
				}
			});
		});
	}
}
