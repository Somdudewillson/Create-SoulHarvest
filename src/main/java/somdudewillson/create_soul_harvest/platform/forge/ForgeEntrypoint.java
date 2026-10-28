package somdudewillson.create_soul_harvest.platform.forge;

//? forge {

/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.compat.mekanism.MekanismCompat;
import somdudewillson.create_soul_harvest.config.SoulHarvestConfig;
import somdudewillson.create_soul_harvest.config.graph.ModDependencyGraph;
import somdudewillson.create_soul_harvest.content.event.SoulHarvestGameEvents;
import somdudewillson.create_soul_harvest.content.recipe.mulching.MulchPackFinder;
import somdudewillson.create_soul_harvest.content.recipe.grinding.MobGrindingPackFinder;
import somdudewillson.create_soul_harvest.registry.*;

@Mod(SoulHarvestMod.MOD_ID)
public class ForgeEntrypoint {

	public ForgeEntrypoint() {
		SoulHarvestMod.onInitialize();

		@SuppressWarnings("removal")
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		SoulHarvestConfig.register(ModDependencyGraph.get());
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SoulHarvestConfig.COMMON_SPEC);
		modBus.addListener(SoulHarvestConfig::onLoad);
		modBus.addListener(SoulHarvestConfig::onReload);
		ModBlocks.BLOCKS.register(modBus);
		ModItems.ITEMS.register(modBus);
		ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
		ModMobEffects.MOB_EFFECTS.register(modBus);
		ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);
		ModFluids.FLUID_TYPES.register(modBus);
		ModFluids.FLUIDS.register(modBus);
		ModFanProcessingTypes.FAN_PROCESSING_TYPES.register(modBus);
		ModRecipeTypes.register(modBus);
		ModParticleTypes.PARTICLE_TYPES.register(modBus);
		ModEntityTypes.ENTITY_TYPES.register(modBus);

		MulchPackFinder.register(modBus);
		MobGrindingPackFinder.register(modBus);

		ModCapabilities.register(modBus);
		ModIngredients.register(modBus);
		ModConditions.register(modBus);
		MekanismCompat.register(modBus);

		SoulHarvestGameEvents.register(MinecraftForge.EVENT_BUS);
	}
}
*///?}
