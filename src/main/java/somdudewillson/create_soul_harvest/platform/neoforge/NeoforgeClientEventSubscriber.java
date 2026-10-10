package somdudewillson.create_soul_harvest.platform.neoforge;

//? neoforge {

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import somdudewillson.create_soul_harvest.client.particle.InfuseSoulFireParticle;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

@EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		SoulHarvestMod.onInitializeClient();
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.INFUSE_SOUL_FIRE.get(), InfuseSoulFireParticle.Provider::new);
	}
}
//?}
