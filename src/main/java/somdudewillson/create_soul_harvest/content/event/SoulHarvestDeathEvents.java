package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
*///?}

import somdudewillson.create_soul_harvest.content.item.SoulBookItem;
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;
import somdudewillson.create_soul_harvest.util.AdvancementHelper;

public class SoulHarvestDeathEvents {

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Entity attacker = event.getSource().getEntity();
		if (!(attacker instanceof ServerPlayer player)) {
			return;
		}

		LivingEntity target = event.getEntity();
		ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
		if (!(offhand.getItem() instanceof SoulBookItem soulBook)) {
			return;
		}

		SoulData soulData = SoulDataManager.get(target.getType());
		if (soulData == null || soulData.tier() > soulBook.getTier()) {
			return;
		}

		String heldSoul = SoulBookItem.getHeldSoul(offhand);
		int soulPortion = SoulBookItem.getSoulPortion(offhand);
		if (soulPortion >= 100) {
			return;
		}

		ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
		String entityId = entityKey.toString();
		if (!heldSoul.isEmpty() && !heldSoul.equals(entityId)) {
			return;
		}

		int newPortion = Math.min(100, soulPortion + soulData.progressPerKill());
		SoulBookItem.setSoulData(offhand, entityId, newPortion);
		player.setItemInHand(InteractionHand.OFF_HAND, offhand);

		if (newPortion >= 100) {
			AdvancementHelper.grantAdvancement(player, "captured_soul");
			if (soulData.tier() == 1) {
				AdvancementHelper.grantAdvancement(player, "captured_t1_soul");
			} else if (soulData.tier() == 2) {
				AdvancementHelper.grantAdvancement(player, "captured_t2_soul");
			} else if (soulData.tier() == 3) {
				AdvancementHelper.grantAdvancement(player, "captured_t3_soul");
			}
			if ("artifacts:mimic".equals(entityId)) {
				AdvancementHelper.grantAdvancement(player, "captured_mimic_soul");
			}
		}
	}
}
