package somdudewillson.create_soul_harvest.platform.neoforge;

//? neoforge {

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
