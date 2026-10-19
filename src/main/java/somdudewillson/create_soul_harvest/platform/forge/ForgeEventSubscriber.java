package somdudewillson.create_soul_harvest.platform.forge;

//? forge {

/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;
import somdudewillson.create_soul_harvest.registry.ModTags;

@Mod.EventBusSubscriber
public class ForgeEventSubscriber {

	@SubscribeEvent
	public static void onEffectApplicable(MobEffectEvent.Applicable event) {
		if (event.getEffectInstance() != null
				&& event.getEffectInstance().getEffect() == ModMobEffects.CONTAGION.get()
				&& event.getEntity().getType().is(ModTags.EntityTypes.CONTAGION_IMMUNE)) {
			event.setResult(Event.Result.DENY);
		}
	}
}
*///?}
