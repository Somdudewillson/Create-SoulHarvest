package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.simibubi.create.content.equipment.armor.BacktankUtil;

public class NecroniumCharmItem extends Item {
	public static final int POISON_COST = 1;
	public static final int WITHER_COST = 2;
	public static final int MAX_DAMAGE = 1 + (WITHER_COST * 30 * 60 * 20);
	public static final int MAX_AIR_USES = WITHER_COST * 20 * 60 * 20;
	
	public static final double DURATION_CURVE_LIMIT = 10 * 20 * 60;
	public static final double SCALER_K = computeScalerK(DURATION_CURVE_LIMIT, 10 * 20);
	
	public static double computeScalerK(double upperBound, double linearCoord) {
        return Math.log((upperBound + linearCoord) / (upperBound - linearCoord)) / linearCoord;
    }
	public static double computeDurationCost(double x) {
        return (2.0 * DURATION_CURVE_LIMIT) / (1.0 + Math.exp(-SCALER_K * x)) - DURATION_CURVE_LIMIT;
    }

	public NecroniumCharmItem(Properties properties) {
		super(properties.durability(MAX_DAMAGE));
	}

	@Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
		if (entity == null || !(entity instanceof LivingEntity livingEntity)) return;
		if ((entity.tickCount & 7) == 0) return;
		
		if (livingEntity.hasEffect(MobEffects.WITHER)) {
			stack = tryCureEffect(livingEntity, level, stack, livingEntity.getEffect(MobEffects.WITHER), WITHER_COST);
		}
		
		if (stack == null || stack.getCount() < 1) return;
		if (livingEntity.hasEffect(MobEffects.POISON)) {
			stack = tryCureEffect(livingEntity, level, stack, livingEntity.getEffect(MobEffects.POISON), POISON_COST);
		}
    }
	
	protected ItemStack tryCureEffect(LivingEntity livingEntity, Level level, ItemStack stack, MobEffectInstance effectInstance, int effCost) {
		int cureCostPerDuration = effCost * (1+effectInstance.getAmplifier());
		int cureCostDuration = (int) Math.round(computeDurationCost(effectInstance.getDuration()));
		cureCostDuration = Math.max(1, cureCostDuration);
		int cureCost = cureCostPerDuration * cureCostDuration;
		
		livingEntity.removeEffect(effectInstance.getEffect());
		return applyDamage(livingEntity, level, stack, cureCost);
	}
	
	protected ItemStack applyDamage(LivingEntity livingEntity, Level level, ItemStack stack, int damage) {
        if (!(level instanceof ServerLevel serverLevel)) return stack;
		if (livingEntity instanceof Player playerEntity && playerEntity.isCreative()) return stack;
		
		int remainingDamage = damage;
		while (remainingDamage>0 && BacktankUtil.canAbsorbDamage(livingEntity, maxUses())) {
			remainingDamage--;
		}
		if (remainingDamage > 0) {
			//? >= 1.21 {
			stack.hurtAndBreak(remainingDamage, serverLevel, livingEntity, c->{
				serverLevel.playSound(null, livingEntity, getBreakingSound(), SoundSource.PLAYERS, 1f, 1f);
			});
			//?} else {
			/*stack.hurtAndBreak(remainingDamage, livingEntity, p -> {
				livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), net.minecraft.sounds.SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1f, 1f);
			});
			*///?}
		}
		
		return stack;
	}

	public static boolean isWitherDamage(DamageSource source) {
		if (source.is(DamageTypes.WITHER)) {
			return true;
		}
		//? neoforge {
		if (source.is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_WITHER) && !source.is(DamageTypes.WITHER_SKULL)) {
			return true;
		}
		//?}
		return false;
	}

	public static boolean isPoisonDamage(LivingEntity entity, DamageSource source) {
		//? neoforge {
		return source.is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_POISON);
		//?} else {
		/*return false;
		*///?}
	}

	public static ItemStack findCharm(LivingEntity entity) {
		Stream<ItemStack> items;
		if (entity instanceof Player player) {
			items = player.getInventory().items.stream();
			items = Stream.concat(items, player.getInventory().armor.stream());
			items = Stream.concat(items, player.getInventory().offhand.stream());
		} else {
			items = StreamSupport.stream(entity.getAllSlots().spliterator(), false);
		}
		return items
				.filter(s -> !s.isEmpty())
				.filter(s -> s.getItem() instanceof NecroniumCharmItem)
				.findFirst()
				.orElse(ItemStack.EMPTY);
	}

	public static boolean tryBlockDamage(LivingEntity entity, DamageSource source, float damage) {
		if (damage <= 0) {
			return false;
		}

		boolean isWither = isWitherDamage(source);
		boolean isPoison = !isWither && isPoisonDamage(entity, source);
		if (!isWither && !isPoison) {
			return false;
		}

		ItemStack charmStack = findCharm(entity);
		if (charmStack.isEmpty()) {
			return false;
		}

		int unitCost = isWither ? WITHER_COST : POISON_COST;
		int totalCost = Math.max(1, Math.round(unitCost * damage));

		if (charmStack.getItem() instanceof NecroniumCharmItem charm) {
			charm.applyDamage(entity, entity.level(), charmStack, totalCost);
		}

		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return BacktankUtil.isBarVisible(stack, maxUses());
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return BacktankUtil.getBarWidth(stack, maxUses());
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BacktankUtil.getBarColor(stack, maxUses());
	}

	private static int maxUses() {
		return MAX_AIR_USES;
	}
}
