package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NeoforgeDataGeneratorHandler {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new NeoforgeRecipeProvider(packOutput, lookupProvider));
		generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, event.getExistingFileHelper()));
	}
}
//?}
