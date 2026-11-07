package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.item.SoulBookItem;
import somdudewillson.create_soul_harvest.content.particle.CapturedSoulParticleOption;
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;
import somdudewillson.create_soul_harvest.registry.ModTags;
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
		if (!(player.level() instanceof ServerLevel serverLevel)) {
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

		ResourceLocation heldSoul = ISoulHolder.getHeldSoul(offhand);
		int soulPortion = ISoulHolder.getSoulPortion(offhand);
		if (soulPortion >= 100) {
			return;
		}

		ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
		if (heldSoul != null && !heldSoul.equals(entityKey)) {
			return;
		}
		
		int soulYield = soulData.progressPerKill();
		if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.Items.ENHANCES_SOUL_YIELD)) {
			soulYield = Math.round(soulYield * 1.5f);
		}

		int newPortion = Math.min(100, soulPortion + soulYield);
		SoulBookItem.setSoulData(offhand, entityKey, newPortion);
		player.setItemInHand(InteractionHand.OFF_HAND, offhand);

		int particleCount = Math.max(1, soulYield * 2);
		double mobX = target.getX();
		double mobY = target.getY() + target.getBbHeight() * 0.5;
		double mobZ = target.getZ();
		double xSpread = Math.max(0.2, target.getBbWidth() * 0.35);
		double ySpread = Math.max(0.2, target.getBbHeight() * 0.25);
		double zSpread = Math.max(0.2, target.getBbWidth() * 0.35);

		serverLevel.sendParticles(
				new CapturedSoulParticleOption(player.getId()),
				mobX, mobY, mobZ,
				particleCount,
				xSpread, ySpread, zSpread,
				0.2
		);
		//? neoforge {
		serverLevel.playSound(null, mobX, mobY, mobZ, SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 60F, 1.4F);
		//?} forge {
		/*serverLevel.playSound(null, mobX, mobY, mobZ, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 60F, 1.4F);
		*///?}

		if (newPortion >= 100) {
			serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.5F, 1.0F);
			
			AdvancementHelper.grantAdvancement(player, "captured_soul");
			if (soulData.tier() == 1) {
				AdvancementHelper.grantAdvancement(player, "captured_t1_soul");
			} else if (soulData.tier() == 2) {
				AdvancementHelper.grantAdvancement(player, "captured_t2_soul");
			} else if (soulData.tier() == 3) {
				AdvancementHelper.grantAdvancement(player, "captured_t3_soul");
			}
			if ("artifacts:mimic".equals(entityKey.toString())) {
				AdvancementHelper.grantAdvancement(player, "captured_mimic_soul");
			}
		}
	}
}
