package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

//? >= 1.21 {

//?} else {
/*import net.minecraft.world.level.Level;
*///?}

import java.util.List;

public class NecroniumWasteItem extends Item {
	public static final float DECAY_PROBABILITY = 0.0006f;
	public static final float DECAY_CASCADE_PROBABILITY = 0.015f;
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
		if (entity == null) return;
		if (entity instanceof Player playerEntity && playerEntity.isSpectator()) return;
		
		long tickOffset = entity.tickCount + (slot * 7);
		if ((tickOffset & 16) == 0) {
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

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		addNecroniumWasteTooltip(stack, tooltipComponents);
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		addNecroniumWasteTooltip(stack, tooltipComponents);
	}
	*///?}

	private void addNecroniumWasteTooltip(ItemStack stack, List<Component> tooltipComponents) {
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.necronium_decay_warning").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.necronium_despawn_warning").withStyle(ChatFormatting.YELLOW));
	}
}
