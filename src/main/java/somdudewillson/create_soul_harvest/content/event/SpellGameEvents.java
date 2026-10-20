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
import somdudewillson.create_soul_harvest.content.effect.VaccinationMobEffect;
import somdudewillson.create_soul_harvest.content.item.NecroniumCharmItem;
import somdudewillson.create_soul_harvest.registry.ModDamageSources;
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
		if (event.getSource().is(ModDamageSources.CONTAGION) && VaccinationMobEffect.hasVaccination(event.getEntity())) {
			event.setNewDamage(0);
			return;
		}
		if (NecroniumCharmItem.tryBlockDamage(event.getEntity(), event.getSource(), event.getNewDamage())) {
			event.setNewDamage(0);
			return;
		}
		float currentDamage = event.getNewDamage();
		float newDamage = StoneskinMobEffect.modifyDamage(event.getEntity(), event.getSource(), currentDamage);
		if (newDamage != currentDamage) {
			event.setNewDamage(newDamage);
		}
	}
	//?} forge {
	/*@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		if (event.getSource().is(ModDamageSources.CONTAGION) && VaccinationMobEffect.hasVaccination(event.getEntity())) {
			event.setAmount(0);
			event.setCanceled(true);
			return;
		}
		if (NecroniumCharmItem.tryBlockDamage(event.getEntity(), event.getSource(), event.getAmount())) {
			event.setAmount(0);
			event.setCanceled(true);
			return;
		}
		float currentDamage = event.getAmount();
		float newDamage = StoneskinMobEffect.modifyDamage(event.getEntity(), event.getSource(), currentDamage);
		if (newDamage != currentDamage) {
			event.setAmount(newDamage);
		}
	}
	*///?}
}
