package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.redsparklib.common.data.RedSparkLibSimpleDatagenManager;
import somdudewillson.redsparklib.common.data.task.DatagenTaskBuilder;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SoulHarvestMod.MOD_ID)
public class NeoforgeDataGeneratorHandler {

	static {
		// Queue simple blocks with block items using RedSparkLib
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.BlockDatagenTaskBuilder.unchained(ModBlocks.SOUL_CRYSTAL_BLOCK).blockItem().build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.BlockDatagenTaskBuilder.unchained(ModBlocks.BIOFUEL_BLOCK).blockItem().build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.BlockDatagenTaskBuilder.unchained(ModBlocks.ABSOLUTE_ICE).blockItem().build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.BlockDatagenTaskBuilder.unchained(ModBlocks.GOLD_FOAM).blockItem().build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.BlockDatagenTaskBuilder.unchained(ModBlocks.MIDAS_CORE).blockItem().build());

		// Queue simple items using RedSparkLib
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.ORGANIC_MULCH).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.BIOFUEL_CHUNK).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.INLAID_LAPIS).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.POLISHED_INLAID_LAPIS).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.MOLTEN_LAPIS_CORE).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.MAGIC_GOLD).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.SOUL_DEPOSIT_SEED).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.SOUL_SHARD).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.POLYCLAY).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.LOOT_TOKEN_BLANK).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.LOOT_TOKEN).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.BIOCIDE_GAS_BOTTLE).build());

		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.SOUL_BOOK_SIMPLE).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.SOUL_BOOK_STANDARD).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.SOUL_BOOK_SUPERIOR).build());

		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_1).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_2).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_3).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_4).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_5).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_6).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_7).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_8).build());

		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_1).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_2).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_3).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_4).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_5).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_6).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_7).build());
		RedSparkLibSimpleDatagenManager.queueDatagen(SoulHarvestMod.MOD_ID, DatagenTaskBuilder.ItemDatagenTaskBuilder.unchained(ModItems.GLYPH_SCROLL_8).build());
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		RedSparkLibSimpleDatagenManager.registerDataGenerators(SoulHarvestMod.MOD_ID, event);

		generator.addProvider(event.includeServer(), new NeoforgeRecipeProvider(packOutput, lookupProvider));
		generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, event.getExistingFileHelper()));
	}
}
//?}
