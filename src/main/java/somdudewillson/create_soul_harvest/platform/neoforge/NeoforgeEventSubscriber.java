package somdudewillson.create_soul_harvest.platform.neoforge;

//? neoforge {

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;
import somdudewillson.create_soul_harvest.registry.ModTags;

@EventBusSubscriber
public class NeoforgeEventSubscriber {

	@SubscribeEvent
	public static void onEffectApplicable(MobEffectEvent.Applicable event) {
		if (event.getEffectInstance() != null
				&& event.getEffectInstance().getEffect().value() == ModMobEffects.CONTAGION.value()
				&& event.getEntity().getType().is(ModTags.EntityTypes.CONTAGION_IMMUNE)) {
			event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
		}
	}
}
//?}
