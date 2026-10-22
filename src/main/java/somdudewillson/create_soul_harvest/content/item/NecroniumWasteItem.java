package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NecroniumWasteItem extends Item {
	public static final float DECAY_PROBABILITY = 0.003f;
	public static final float DECAY_CASCADE_PROBABILITY = 0.025f;
	public static final int ITEM_ENTITY_LIFESPAN = 1200; // 60 seconds (1 minute)

	public NecroniumWasteItem(Properties properties) {
		super(properties);
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, Level level) {
		return ITEM_ENTITY_LIFESPAN;
	}

	@Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean slotSelected) {
		if (level.isClientSide()) return;
		if (entity == null) return;
		if (entity instanceof Player playerEntity && playerEntity.isSpectator()) return;
		
		long tickOffset = entity.tickCount + (slot * 7);
		if ((tickOffset & 31) == 0) {
			return;
		}
		
    	int maxDecayEvents = stack.getCount();
    	int decayEvents = 0;
    	int cascades = 0;
    	for (int i=0; i<maxDecayEvents; i++) {
    		if (level.random.nextFloat() > DECAY_PROBABILITY) continue;
    		decayEvents++;
    		cascades++;
    	}
		// cascade
    	double actualCascadeProbability = 1-Math.pow(1-DECAY_CASCADE_PROBABILITY, maxDecayEvents);
    	while (cascades > 0 && decayEvents < maxDecayEvents) {
    		cascades--;
    		
    		if (level.random.nextFloat() > actualCascadeProbability) continue;
    		decayEvents++;
    		cascades++;
    	}
    	decayEvents = Math.min(maxDecayEvents, decayEvents);
    	if (decayEvents <= 0) return;
    	
    	stack.shrink(decayEvents);
		Vec3 entityPos = entity.position();
    	MobEffectInstance witherEffect = computeMobEffectInstance(decayEvents);
    	if (entity instanceof LivingEntity livingEntity) {
    		MobEffectInstance existingEff = livingEntity.getEffect(witherEffect.getEffect());
    		if (existingEff != null && existingEff.getDuration() > witherEffect.getDuration()) {
    			witherEffect = new MobEffectInstance(
    					witherEffect.getEffect(), 
    					Math.min(existingEff.getDuration()+witherEffect.getDuration(), witherEffect.getDuration()*2), 
    					witherEffect.getAmplifier());
    		}
    		livingEntity.addEffect(witherEffect);
    	} else if (level instanceof ServerLevel serverLevel) {
    		AreaEffectCloud witherCloudEntity = computeAreaEffectCloud(serverLevel, decayEvents, entityPos);
    		serverLevel.addFreshEntity(witherCloudEntity);
    	}
    	
    	if (level instanceof ServerLevel serverLevel) {
    		serverLevel.playLocalSound(
    				entityPos.x(), entityPos.y(), entityPos.z(), 
    				SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 
    				0.8f, 0.4f, 
    				false);
    	}
    }

	public static AreaEffectCloud computeAreaEffectCloud(ServerLevel serverLevel, int decayEvents, Vec3 entityPos) {
		AreaEffectCloud witherCloudEntity = new AreaEffectCloud(serverLevel, entityPos.x(), entityPos.y(), entityPos.z());
		witherCloudEntity.setRadius(3 + (decayEvents * 0.5f));
		witherCloudEntity.addEffect(computeMobEffectInstance(decayEvents));
		return witherCloudEntity;
	}

	public static MobEffectInstance computeMobEffectInstance(int decayEvents) {
		return new MobEffectInstance(MobEffects.WITHER, (20 * 2) + (decayEvents * 20 * 3), (decayEvents / 4));
	}
}
