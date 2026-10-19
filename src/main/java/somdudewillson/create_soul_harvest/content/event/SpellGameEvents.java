package somdudewillson.create_soul_harvest.content.event;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
*///?}

import somdudewillson.create_soul_harvest.content.effect.StoneskinMobEffect;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;

public class SpellGameEvents {


	@SubscribeEvent
	public static void onLivingFall(LivingFallEvent event) {
		//? >= 1.21 {
		if (event.getEntity().hasEffect(ModMobEffects.AGILE_FALLING)) {
			event.setCanceled(true);
			event.setDistance(0);
		}
		//?} else {
		/*if (event.getEntity().hasEffect(ModMobEffects.AGILE_FALLING.get())) {
			event.setCanceled(true);
			event.setDistance(0);
		}
		*///?}
	}

	//? neoforge {
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent.Pre event) {
		float currentDamage = event.getNewDamage();
		float newDamage = StoneskinMobEffect.modifyDamage(event.getEntity(), event.getSource(), currentDamage);
		if (newDamage != currentDamage) {
			event.setNewDamage(newDamage);
		}
	}
	//?} forge {
	/*@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		float currentDamage = event.getAmount();
		float newDamage = StoneskinMobEffect.modifyDamage(event.getEntity(), event.getSource(), currentDamage);
		if (newDamage != currentDamage) {
			event.setAmount(newDamage);
		}
	}
	*///?}
}
