package somdudewillson.create_soul_harvest.content.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class ContagionSpreadGoal extends Goal {
	public static final double SEARCH_RANGE = 24.0D;
	public static final double STOP_DISTANCE_SQR = 2.25D; // 1.5 blocks squared (inside 3x3x3 spread box)

	private final PathfinderMob mob;
	private final double speedModifier;
	private final double speedTarget;
	@Nullable
	private LivingEntity target;
	private int timeToRecalcPath;

	public ContagionSpreadGoal(PathfinderMob mob, double speedModifier, double speedTarget) {
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.speedTarget = speedTarget;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		MobEffectInstance contagion = ContagionMobEffect.getContagionEffect(mob);
		if (!ContagionMobEffect.isValidContagion(contagion)) {
			return false;
		}

		LivingEntity bestTarget = findBestTarget(contagion.getAmplifier(), contagion.getDuration(), null);
		if (bestTarget != null) {
			this.target = bestTarget;
			return true;
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		MobEffectInstance contagion = ContagionMobEffect.getContagionEffect(mob);
		if (!ContagionMobEffect.isValidContagion(contagion)) {
			return false;
		}

		if (this.target == null || !this.target.isAlive() || this.target.isRemoved()) {
			return false;
		}

		if (this.target instanceof Player player && (player.isCreative() || player.isSpectator())) {
			return false;
		}

		if (this.mob.distanceToSqr(this.target) > SEARCH_RANGE * SEARCH_RANGE) {
			return false;
		}

		int priority = ContagionMobEffect.getInfectionPriority(this.target, contagion.getAmplifier(), contagion.getDuration());
		return priority > 0;
	}

	public double getEffectiveSpeedModifier() {
		AttributeInstance attr = this.mob.getAttribute(Attributes.MOVEMENT_SPEED);
		double baseSpeed = attr != null ? attr.getBaseValue() : 0.0D;
		if (baseSpeed <= 0.0D || baseSpeed >= this.speedTarget) {
			return this.speedModifier;
		}

		// Proportional boost when base speed is below target speed
		double boostedMultiplier = this.speedModifier * (this.speedTarget / baseSpeed);

		// Must not result in a speed that exceeds target speed * speed multiplier
		double maxMultiplier = (this.speedTarget * this.speedModifier) / baseSpeed;
		return Math.min(boostedMultiplier, maxMultiplier);
	}

	@Override
	public void start() {
		this.timeToRecalcPath = 0;
		if (this.target != null) {
			this.mob.getNavigation().moveTo(this.target, getEffectiveSpeedModifier());
		}
	}

	@Override
	public void stop() {
		this.target = null;
		this.mob.getNavigation().stop();
	}

	@Override
	public void tick() {
		if (this.target == null) {
			return;
		}

		MobEffectInstance contagion = ContagionMobEffect.getContagionEffect(mob);
		if (!ContagionMobEffect.isValidContagion(contagion)) {
			return;
		}

		// Small chance each tick (~3%) to retarget new entity
		if (this.mob.getRandom().nextFloat() < 0.03F) {
			LivingEntity newTarget = findBestTarget(contagion.getAmplifier(), contagion.getDuration(), this.target);
			
			if (newTarget != null) {
				this.target = newTarget;
				this.mob.getNavigation().moveTo(this.target, getEffectiveSpeedModifier());
				this.timeToRecalcPath = 10;
			}
		}

		this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

		double distSqr = this.mob.distanceToSqr(this.target);
		if (distSqr <= STOP_DISTANCE_SQR) {
			this.mob.getNavigation().stop();
		} else {
			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10 + this.mob.getRandom().nextInt(10);
				this.mob.getNavigation().moveTo(this.target, getEffectiveSpeedModifier());
			}
		}
	}

	@Nullable
	private LivingEntity findBestTarget(int currentAmplifier, int currentDuration, @Nullable LivingEntity exclude) {
		AABB searchBox = this.mob.getBoundingBox().inflate(SEARCH_RANGE, 4.0D, SEARCH_RANGE);
		List<LivingEntity> candidates = this.mob.level().getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != this.mob && e.isAlive());

		LivingEntity best = null;
		double bestScore = Double.NEGATIVE_INFINITY;

		for (LivingEntity candidate : candidates) {
			if (candidate == exclude) {
				continue;
			}
			
			if (candidate instanceof Player player && (player.isCreative() || player.isSpectator())) {
				continue;
			}

			double score = calculateTargetScore(candidate, currentAmplifier, currentDuration);
			if (score > bestScore) {
				bestScore = score;
				best = candidate;
			}
		}

		return best;
	}

	private double calculateTargetScore(LivingEntity candidate, int currentAmplifier, int currentDuration) {
		int priority = ContagionMobEffect.getInfectionPriority(candidate, currentAmplifier, currentDuration);
		if (priority <= 0) {
			return Double.NEGATIVE_INFINITY;
		}
		return calculateScoreWithPriority(candidate, priority);
	}

	private double calculateScoreWithPriority(LivingEntity candidate, int priority) {
		long seed = this.mob.getUUID().getLeastSignificantBits() ^ candidate.getUUID().getLeastSignificantBits();
		double noise = ((seed & 0xFFFF) / 65535.0) * 2.0; // [0.0, 2.0]
		double dist = Math.sqrt(this.mob.distanceToSqr(candidate));
		double distRatio = dist / SEARCH_RANGE; // [0.0, 1.0]

		// Priority 3 = ~30 base, Priority 2 = ~20 base, Priority 1 = ~10 base
		return (priority * 10.0D) + noise - distRatio;
	}
}
