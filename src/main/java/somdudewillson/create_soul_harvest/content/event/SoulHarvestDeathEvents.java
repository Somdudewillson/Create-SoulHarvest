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
import somdudewillson.create_soul_harvest.util.AdvancementHelper;

import static somdudewillson.create_soul_harvest.registry.ModTags.EntityTypes.*;

public class SoulHarvestDeathEvents {

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		DamageSourceAttacker:
		{
			Entity attacker = event.getSource().getEntity();
			if (!(attacker instanceof ServerPlayer player)) {
				break DamageSourceAttacker;
			}

			LivingEntity target = event.getEntity();
			ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
			if (!(offhand.getItem() instanceof SoulBookItem soulBook)) {
				break DamageSourceAttacker;
			}

			int bookTier = soulBook.getTier();
			int targetTier = 0;
			int addedPortion = 10;

			if (target.getType().is(T1_MOB)) {
				targetTier = 1;
				addedPortion = 10;
			} else if (target.getType().is(T2_MOB)) {
				targetTier = 2;
				addedPortion = 4;
			} else if (target.getType().is(T3_MOB)) {
				targetTier = 3;
				addedPortion = 100;
			} else {
				break DamageSourceAttacker;
			}

			if (target.getType().is(REQUIRES_MEDIUM)) {
				addedPortion = 10;
			} else if (target.getType().is(REQUIRES_HIGH)) {
				addedPortion = 4;
			} else if (target.getType().is(REQUIRES_LOW)) {
				addedPortion = 100;
			}

			if (targetTier > bookTier) {
				break DamageSourceAttacker;
			}

			String heldSoul = SoulBookItem.getHeldSoul(offhand);
			int soulPortion = SoulBookItem.getSoulPortion(offhand);
			if (soulPortion >= 100) {
				break DamageSourceAttacker;
			}

			ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
			String entityId = entityKey.toString();
			if (!heldSoul.isEmpty() && !heldSoul.equals(entityId)) {
				break DamageSourceAttacker;
			}

			int newPortion = Math.min(100, soulPortion + addedPortion);
			SoulBookItem.setSoulData(offhand, entityId, newPortion);
			player.setItemInHand(InteractionHand.OFF_HAND, offhand);

			if (newPortion >= 100) {
				AdvancementHelper.grantAdvancement(player, "captured_soul");
				if (target.getType().is(T1_MOB)) {
					AdvancementHelper.grantAdvancement(player, "captured_t1_soul");
				} else if (target.getType().is(T2_MOB)) {
					AdvancementHelper.grantAdvancement(player, "captured_t2_soul");
				} else if (target.getType().is(T3_MOB)) {
					AdvancementHelper.grantAdvancement(player, "captured_t3_soul");
				}
				if ("artifacts:mimic".equals(entityId)) {
					AdvancementHelper.grantAdvancement(player, "captured_mimic_soul");
				}
			}
		}
	}
}
