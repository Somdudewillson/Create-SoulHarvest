package somdudewillson.create_soul_harvest.platform.neoforge;

//? neoforge {

import java.util.function.Supplier;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import somdudewillson.create_soul_harvest.client.particle.InfuseSoulFireParticle;
import somdudewillson.create_soul_harvest.client.particle.StormCloudParticle;
import somdudewillson.create_soul_harvest.client.particle.VerdantFlashParticle;
import somdudewillson.create_soul_harvest.compat.jei.SoulHarvestJeiHelper;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
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
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.INFUSE_SOUL_FIRE.get(), InfuseSoulFireParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.STORM_CLOUD.get(), StormCloudParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.VERDANT_FLASH.get(), VerdantFlashParticle.Provider::new);
	}

	@EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, value = Dist.CLIENT)
	public static class ClientGameEvents {
		@SubscribeEvent
		public static void onRecipesUpdated(RecipesUpdatedEvent event) {
			SoulHarvestJeiHelper.updateHiddenIngredients();
			ModCreativeTabs.refreshCreativeTabs();
		}
	}
}
//?}
