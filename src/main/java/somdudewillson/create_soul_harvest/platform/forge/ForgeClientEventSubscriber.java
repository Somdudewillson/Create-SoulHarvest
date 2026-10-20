package somdudewillson.create_soul_harvest.platform.forge;

//? forge {

/*import somdudewillson.create_soul_harvest.SoulHarvestMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import somdudewillson.create_soul_harvest.client.particle.InfuseSoulFireParticle;
import somdudewillson.create_soul_harvest.client.particle.StormCloudParticle;
import somdudewillson.create_soul_harvest.client.particle.VerdantFlashParticle;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.INFUSE_SOUL_FIRE.get(), InfuseSoulFireParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.STORM_CLOUD.get(), StormCloudParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.VERDANT_FLASH.get(), VerdantFlashParticle.Provider::new);
	}
}
*///?}
