package somdudewillson.create_soul_harvest.platform.forge;

//? forge {

/*import somdudewillson.create_soul_harvest.SoulHarvestMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import somdudewillson.create_soul_harvest.client.overlay.BiocideGasOverlay;
import somdudewillson.create_soul_harvest.client.particle.CapturedSoulParticle;
import somdudewillson.create_soul_harvest.client.particle.InfuseSoulFireParticle;
import somdudewillson.create_soul_harvest.client.particle.StormCloudParticle;
import somdudewillson.create_soul_harvest.client.particle.VerdantFlashParticle;
import somdudewillson.create_soul_harvest.client.texture.dynamic.CompositeItemBakedModel;
import somdudewillson.create_soul_harvest.client.texture.palette.ClientPatternDumpCommand;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureManager;
import somdudewillson.create_soul_harvest.compat.jei.SoulHarvestJeiHelper;
import somdudewillson.create_soul_harvest.content.recipe.RecipeSoulTracker;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import somdudewillson.create_soul_harvest.registry.ModCreativeTabs;
import somdudewillson.create_soul_harvest.registry.ModEntityTypes;

@Mod.EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeClientEventSubscriber {

	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		SoulHarvestMod.onInitializeClient();
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntityTypes.SPELL_THINKER.get(), NoopRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.CONTAGION_CHARGE.get(), p -> new ThrownItemRenderer<>(p, 0.75F, true));
		event.registerEntityRenderer(ModEntityTypes.FIREBOLT.get(), NoopRenderer::new);
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.INFUSE_SOUL_FIRE.get(), InfuseSoulFireParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.STORM_CLOUD.get(), StormCloudParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.VERDANT_FLASH.get(), VerdantFlashParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.CAPTURED_SOUL.get(), CapturedSoulParticle.Provider::new);
	}

	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(
				VanillaGuiOverlay.VIGNETTE.id(),
				"biocide_gas_overlay",
				(gui, guiGraphics, partialTick, width, height) -> BiocideGasOverlay.render(guiGraphics));
	}

	@SubscribeEvent
	public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(MobPatternTextureManager.INSTANCE);
	}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		CompositeItemBakedModel.wrapTargetModels(event.getModels());
	}

	@Mod.EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
*///?}
