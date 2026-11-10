package somdudewillson.create_soul_harvest.platform.neoforge;

//? neoforge {

import java.util.function.Supplier;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import somdudewillson.create_soul_harvest.client.overlay.BiocideGasOverlay;
import somdudewillson.create_soul_harvest.client.particle.CapturedSoulParticle;
import somdudewillson.create_soul_harvest.client.particle.InfuseSoulFireParticle;
import somdudewillson.create_soul_harvest.client.particle.StormCloudParticle;
import somdudewillson.create_soul_harvest.client.particle.VerdantFlashParticle;
import somdudewillson.create_soul_harvest.client.texture.dynamic.CompositeItemBakedModel;
import somdudewillson.create_soul_harvest.client.texture.palette.ClientPatternDumpCommand;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureManager;
import somdudewillson.create_soul_harvest.compat.jei.SoulHarvestJeiHelper;
import somdudewillson.create_soul_harvest.content.conduit.client.WindowedPotentiaConduitRenderer;
import somdudewillson.create_soul_harvest.content.kinetics.extractor.PotentiaExtractorRenderer;
import somdudewillson.create_soul_harvest.content.potentia.catalyst.PotentiaGrowthCatalystBlock;
import somdudewillson.create_soul_harvest.content.potentia.catalyst.PotentiaGrowthCatalystBlockEntity;
import somdudewillson.create_soul_harvest.content.potentia.client.PotentiaColorHelper;
import somdudewillson.create_soul_harvest.content.potentia.tank.PotentiaReservoirRenderer;
import somdudewillson.create_soul_harvest.content.recipe.RecipeSoulTracker;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.create_soul_harvest.registry.ModCreativeTabs;
import somdudewillson.create_soul_harvest.registry.ModEntityTypes;

@EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		SoulHarvestMod.onInitializeClient();
	}

	@SubscribeEvent
	public static void onLoadComplete(final FMLLoadCompleteEvent event) {
		ModContainer modContainer = ModList.get()
			.getModContainerById(SoulHarvestMod.MOD_ID)
			.orElseThrow(() -> new IllegalStateException("Soul Harvest mod container missing on LoadComplete"));
		Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, SoulHarvestMod.MOD_ID);
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntityTypes.SPELL_THINKER.get(), NoopRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.CONTAGION_CHARGE.get(), p -> new ThrownItemRenderer<>(p, 0.75F, true));
		event.registerEntityRenderer(ModEntityTypes.FIREBOLT.get(), NoopRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.POTENTIA_EXTRACTOR_BE.get(), PotentiaExtractorRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.WINDOWED_POTENTIA_CONDUIT_BE.get(), WindowedPotentiaConduitRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.POTENTIA_RESERVOIR_BE.get(), PotentiaReservoirRenderer::new);
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.INFUSE_SOUL_FIRE.get(), InfuseSoulFireParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.STORM_CLOUD.get(), StormCloudParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.VERDANT_FLASH.get(), VerdantFlashParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.CAPTURED_SOUL.get(), CapturedSoulParticle.Provider::new);
	}

	@SubscribeEvent
	public static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAbove(
				VanillaGuiLayers.CAMERA_OVERLAYS,
				BiocideGasOverlay.OVERLAY_TEXTURE,
				(guiGraphics, deltaTracker) -> BiocideGasOverlay.render(guiGraphics));
	}

	@SubscribeEvent
	public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(MobPatternTextureManager.INSTANCE);
	}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		CompositeItemBakedModel.wrapTargetModels(event.getModels());
	}

	@SubscribeEvent
	public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		event.register(
				(state, level, pos, tintIndex) -> {
					if (tintIndex == 0) {
						if (level != null && pos != null && level.getBlockEntity(pos) instanceof PotentiaGrowthCatalystBlockEntity be) {
							return be.getCrystalColor(state);
						}
						if (state != null && state.hasProperty(PotentiaGrowthCatalystBlock.LIT)
								&& !state.getValue(PotentiaGrowthCatalystBlock.LIT)) {
							return PotentiaGrowthCatalystBlockEntity.dimColor(PotentiaColorHelper.DEFAULT_PRIMARY, 0.35f);
						}
					}
					return PotentiaColorHelper.DEFAULT_PRIMARY;
				},
				ModBlocks.POTENTIA_GROWTH_CATALYST.get()
		);
	}

	@SubscribeEvent
	public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
		event.register(
				(stack, tintIndex) -> tintIndex == 0 ? PotentiaColorHelper.DEFAULT_PRIMARY : 0xFFFFFF,
				ModItems.POTENTIA_GROWTH_CATALYST.get()
		);
	}

	@EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, value = Dist.CLIENT)
	public static class ClientGameEvents {
		@SubscribeEvent
		public static void onRecipesUpdated(RecipesUpdatedEvent event) {
			RecipeSoulTracker.scan(event.getRecipeManager());
			SoulHarvestJeiHelper.updateHiddenIngredients();
			ModCreativeTabs.refreshCreativeTabs();
		}

		@SubscribeEvent
		public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
			if (SoulHarvestMod.xplat().isDevelopmentEnvironment()) {
				ClientPatternDumpCommand.register(event.getDispatcher());
			}
		}

		@SubscribeEvent
		public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
			MobPatternTextureManager.checkOnWorldJoin();
		}
	}
}
//?}
