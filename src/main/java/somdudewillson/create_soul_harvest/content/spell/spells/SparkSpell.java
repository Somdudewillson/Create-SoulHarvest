package somdudewillson.create_soul_harvest.content.spell.spells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.SimplePersistentGlyphSpell;

public class SparkSpell extends SimplePersistentGlyphSpell {
	public static final ResourceLocation ID = SoulHarvestMod.id("spark");

	// Configurable Constants
	public static final int DEFAULT_STRIKE_COUNT = 7;
	public static final int MIN_STRIKE_INTERVAL = 5; // Ticks between any two strikes
	public static final int SAME_TARGET_COOLDOWN_TICKS = 25; // Invulnerability (20) + 5 ticks
	public static final double DEFAULT_MAX_RANGE = 64.0;
	public static final double DEFAULT_CONE_ANGLE_DEGREES = 25.0;
	public static final double DEFAULT_STEP_SIZE = 3.0;
	public static final double DEFAULT_ANGULAR_FACTOR = 1.0;
	public static final double DEFAULT_MONSTER_FACTOR = 1.0;
	public static final double DEFAULT_PLAYER_FACTOR = 1.5;
	public static final double DEFAULT_NON_MONSTER_NON_PLAYER_FACTOR = 2.0;
	public static final double DEFAULT_BLOCK_DISTANCE_FACTOR = 6.0;
	public static final double DEFAULT_HEALTH_PENALTY_BASE = 15.0;
	public static final double DEFAULT_BLOCK_REPEAT_PENALTY = 60.0;
	public static final double DEFAULT_DEVIATION_RADIUS = 1.0;

	// Configuration fields
	protected int strikeCount = DEFAULT_STRIKE_COUNT;
	protected double maxRange = DEFAULT_MAX_RANGE;
	protected double coneAngleDegrees = DEFAULT_CONE_ANGLE_DEGREES;
	protected double stepSize = DEFAULT_STEP_SIZE;
	protected double angularFactor = DEFAULT_ANGULAR_FACTOR;
	protected double monsterFactor = DEFAULT_MONSTER_FACTOR;
	protected double playerFactor = DEFAULT_PLAYER_FACTOR;
	protected double nonMonsterNonPlayerFactor = DEFAULT_NON_MONSTER_NON_PLAYER_FACTOR;
	protected double blockDistanceFactor = DEFAULT_BLOCK_DISTANCE_FACTOR;
	protected double healthPenaltyBase = DEFAULT_HEALTH_PENALTY_BASE;
	protected double blockRepeatPenalty = DEFAULT_BLOCK_REPEAT_PENALTY;
	protected double deviationRadius = DEFAULT_DEVIATION_RADIUS;

	// Thinker runtime state
	protected List<TargetState> targets = new ArrayList<>();
	protected int ticksSinceLastStrike = MIN_STRIKE_INTERVAL;
	protected long currentTick = 0L;
	@Nullable
	protected UUID ownerUUID;

	public SparkSpell() {
		this(ID);
	}

	public SparkSpell(ResourceLocation id) {
		super(id);
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (!(level instanceof ServerLevel serverLevel)) return;

		Vec3 eyePos = caster.getEyePosition();
		Vec3 lookVec = caster.getLookAngle().normalize();
		Vec3 maxReachPos = eyePos.add(lookVec.scale(maxRange));

		// Raycast for blocks
		ClipContext clipCtx = new ClipContext(eyePos, maxReachPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster);
		BlockHitResult hitResult = level.clip(clipCtx);

		boolean hitBlock = hitResult.getType() == HitResult.Type.BLOCK;
		double coneReach = hitBlock ? eyePos.distanceTo(hitResult.getLocation()) : maxRange;

		// Collect entities within cone up to the hit point
		Set<LivingEntity> candidateEntities = findCandidates(serverLevel, caster, eyePos, lookVec, coneReach);

		// Build scored candidate targets
		List<TargetCandidate> scoredTargets = new ArrayList<>();

		for (LivingEntity entity : candidateEntities) {
			Vec3 entityCenter = entity.getBoundingBox().getCenter();
			Vec3 toEntity = entityCenter.subtract(eyePos);
			double dist = toEntity.length();
			if (dist > coneReach + 1.0 || dist < 1e-4) continue;

			double dot = toEntity.normalize().dot(lookVec);
			double angleRad = Math.acos(Mth.clamp(dot, -1.0, 1.0));
			double angleDeg = Math.toDegrees(angleRad);

			if (angleDeg > coneAngleDegrees) continue;

			double baseScore = dist + (angularFactor * angleDeg);
			double mult;
			if (entity.getType().getCategory() == MobCategory.MONSTER || entity instanceof Monster) {
				mult = monsterFactor;
			} else if (entity instanceof Player) {
				mult = playerFactor;
			} else {
				mult = nonMonsterNonPlayerFactor;
			}

			scoredTargets.add(new TargetCandidate(entity, null, entityCenter, baseScore * mult, entity.getMaxHealth()));
		}

		// Add block hit location if present
		if (hitBlock) {
			Vec3 blockHitPos = hitResult.getLocation();
			double dist = eyePos.distanceTo(blockHitPos);
			double score = dist * blockDistanceFactor;
			scoredTargets.add(new TargetCandidate(null, hitResult.getBlockPos(), blockHitPos, score, -1.0f));
		}

		if (scoredTargets.isEmpty()) {
			return;
		}

		// Allocate strikes among targets using priority scores
		Map<TargetCandidate, Integer> strikeAllocations = new LinkedHashMap<>();
		for (int i = 0; i < strikeCount; i++) {
			TargetCandidate best = Collections.min(scoredTargets, Comparator.comparingDouble(TargetCandidate::getScore));
			strikeAllocations.put(best, strikeAllocations.getOrDefault(best, 0) + 1);

			// Apply health-scaled penalty for entities, elevated penalty for blocks
			if (best.isEntity()) {
				double penalty = healthPenaltyBase * (20.0 / Math.max(1.0, best.getMaxHealth()));
				best.addScore(penalty);
			} else {
				best.addScore(blockRepeatPenalty);
			}
		}

		// Prepare active TargetState list for the thinker
		List<TargetState> targetStates = new ArrayList<>();
		for (Map.Entry<TargetCandidate, Integer> entry : strikeAllocations.entrySet()) {
			TargetCandidate cand = entry.getKey();
			int count = entry.getValue();
			if (count > 0) {
				targetStates.add(new TargetState(
					cand.getEntity() != null ? cand.getEntity().getUUID() : null,
					cand.getBlockPos(),
					cand.getPosition(),
					count,
					0L,
					cand.getScore()
				));
			}
		}

		// Spawn SpellThinkerEntity
		SparkSpell thinkerSpell = new SparkSpell(this.id);
		thinkerSpell.targets = targetStates;
		thinkerSpell.ownerUUID = caster.getUUID();
		thinkerSpell.strikeCount = this.strikeCount;
		thinkerSpell.deviationRadius = this.deviationRadius;

		Vec3 spawnPos = hitBlock ? hitResult.getLocation() : eyePos.add(lookVec.scale(Math.min(coneReach, 5.0)));
		SpellThinkerEntity.spawn(serverLevel, spawnPos, thinkerSpell, caster.getUUID());
	}

	@Override
	public void serverTick(SpellThinkerEntity thinker, ServerLevel level) {
		currentTick++;
		ticksSinceLastStrike++;

		// Visual particle indicator around remaining active targets
		for (TargetState state : targets) {
			if (state.getRemainingStrikes() <= 0) continue;

			Vec3 particlePos = null;
			if (state.getEntityUUID() != null) {
				Entity entity = level.getEntity(state.getEntityUUID());
				if (entity instanceof LivingEntity living && living.isAlive()) {
					particlePos = living.getBoundingBox().getCenter();
					state.setLastKnownPos(particlePos);
				} else {
					particlePos = state.getLastKnownPos();
				}
			} else if (state.getBlockPos() != null) {
				particlePos = Vec3.atCenterOf(state.getBlockPos());
			}

			if (particlePos != null) {
				level.sendParticles(
					ParticleTypes.ELECTRIC_SPARK,
					particlePos.x, particlePos.y+2, particlePos.z,
					3,
					1.3, 2.4, 1.3,
					0.02
				);
			}
		}

		// Strike pacing check (minimum delay between any strikes)
		if (ticksSinceLastStrike < MIN_STRIKE_INTERVAL) {
			return;
		}

		// Find eligible target whose invulnerability cooldown has passed
		TargetState readyTarget = null;
		double bestScore = Double.MAX_VALUE;

		for (TargetState state : targets) {
			if (state.getRemainingStrikes() <= 0) continue;
			if (currentTick < state.getNextEligibleTick()) continue;

			if (state.getPriorityScore() < bestScore) {
				bestScore = state.getPriorityScore();
				readyTarget = state;
			}
		}

		if (readyTarget == null) {
			return; // Waiting for cooldown
		}

		// Strike the ready target
		Vec3 strikeBasePos = readyTarget.getLastKnownPos();
		LivingEntity struckLiving = null;

		if (readyTarget.getEntityUUID() != null) {
			Entity entity = level.getEntity(readyTarget.getEntityUUID());
			if (entity instanceof LivingEntity living && living.isAlive()) {
				strikeBasePos = living.position();
				readyTarget.setLastKnownPos(strikeBasePos);
				struckLiving = living;
			}
		} else if (readyTarget.getBlockPos() != null) {
			strikeBasePos = Vec3.atBottomCenterOf(readyTarget.getBlockPos());
		}

		// Add random horizontal deviation
		double devX = (level.random.nextDouble() - 0.5) * 2.0 * deviationRadius;
		double devZ = (level.random.nextDouble() - 0.5) * 2.0 * deviationRadius;
		Vec3 finalStrikePos = strikeBasePos.add(devX, 0, devZ);

		// Reset target invulnerability time so this strike deals full damage
		if (struckLiving != null) {
			struckLiving.invulnerableTime = 0;
		}

		// Spawn lightning bolt
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
		if (bolt != null) {
			bolt.moveTo(finalStrikePos);
			Entity owner = thinker.getOwner();
			if (owner instanceof ServerPlayer sp) {
				bolt.setCause(sp);
			}
			level.addFreshEntity(bolt);
		}

		// pdate state
		readyTarget.decrementStrikes();
		readyTarget.setNextEligibleTick(currentTick + SAME_TARGET_COOLDOWN_TICKS);
		ticksSinceLastStrike = 0;
	}

	@Override
	public boolean isFinished(SpellThinkerEntity thinker) {
		for (TargetState state : targets) {
			if (state.getRemainingStrikes() > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		tag.putLong("current_tick", currentTick);
		tag.putInt("ticks_since_last_strike", ticksSinceLastStrike);
		if (ownerUUID != null) {
			tag.putUUID("owner_uuid", ownerUUID);
		}
		ListTag targetList = new ListTag();
		for (TargetState state : targets) {
			CompoundTag targetTag = new CompoundTag();
			state.writeToNbt(targetTag);
			targetList.add(targetTag);
		}
		tag.put("targets", targetList);
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		if (tag.contains("current_tick")) {
			currentTick = tag.getLong("current_tick");
		}
		if (tag.contains("ticks_since_last_strike")) {
			ticksSinceLastStrike = tag.getInt("ticks_since_last_strike");
		}
		if (tag.hasUUID("owner_uuid")) {
			ownerUUID = tag.getUUID("owner_uuid");
		}
		if (tag.contains("targets")) {
			ListTag targetList = tag.getList("targets", Tag.TAG_COMPOUND);
			targets.clear();
			for (int i = 0; i < targetList.size(); i++) {
				targets.add(TargetState.readFromNbt(targetList.getCompound(i)));
			}
		}
	}

	protected Set<LivingEntity> findCandidates(ServerLevel level, LivingEntity caster, Vec3 eyePos, Vec3 lookVec, double coneReach) {
		Set<LivingEntity> candidates = new HashSet<>();
		double tanAngle = Math.tan(Math.toRadians(coneAngleDegrees));

		for (double d = 0; d <= coneReach; d += stepSize) {
			Vec3 stepCenter = eyePos.add(lookVec.scale(d));
			double radius = Math.max(1.5, d * tanAngle + (stepSize * 0.5));
			AABB box = new AABB(
				stepCenter.x - radius, stepCenter.y - radius, stepCenter.z - radius,
				stepCenter.x + radius, stepCenter.y + radius, stepCenter.z + radius
			);
			List<LivingEntity> found = level.getEntitiesOfClass(LivingEntity.class, box, e -> isValidTarget(caster, e));
			candidates.addAll(found);
		}

		// Also check at exact reach boundary if not covered
		if (coneReach > 0) {
			Vec3 endCenter = eyePos.add(lookVec.scale(coneReach));
			double radius = Math.max(1.5, coneReach * tanAngle + (stepSize * 0.5));
			AABB box = new AABB(
				endCenter.x - radius, endCenter.y - radius, endCenter.z - radius,
				endCenter.x + radius, endCenter.y + radius, endCenter.z + radius
			);
			List<LivingEntity> found = level.getEntitiesOfClass(LivingEntity.class, box, e -> isValidTarget(caster, e));
			candidates.addAll(found);
		}

		return candidates;
	}

	public static boolean isValidTarget(LivingEntity caster, Entity e) {
		if (e == caster || e.getUUID().equals(caster.getUUID())) return false;
		if (caster.getTeam() != null && caster.getTeam().isAlliedTo(e.getTeam())) return false;
		if (e instanceof OwnableEntity ownable && Objects.equals(ownable.getOwnerUUID(), caster.getUUID())) return false;

		if (!(e instanceof LivingEntity living)) return false;
		if (living.isDeadOrDying()) return false;
		if (!living.attackable()) return false;
		if (living.isInvulnerable()) return false;
		if (e instanceof Player player && (player.isCreative() || player.isSpectator())) return false;

		return true;
	}

	// Internal helper class for initial target scoring and allocation
	@Getter
	protected static class TargetCandidate {
		@Nullable
		private final LivingEntity entity;
		@Nullable
		private final BlockPos blockPos;
		private final Vec3 position;
		@Setter
		private double score;
		private final float maxHealth;

		public TargetCandidate(@Nullable LivingEntity entity, @Nullable BlockPos blockPos, Vec3 position, double score, float maxHealth) {
			this.entity = entity;
			this.blockPos = blockPos;
			this.position = position;
			this.score = score;
			this.maxHealth = maxHealth;
		}

		public boolean isEntity() {
			return entity != null;
		}

		public void addScore(double penalty) {
			this.score += penalty;
		}
	}

	// Runtime target state preserved across thinker ticks and NBT serialization
	@Getter
	@Setter
	public static class TargetState {
		@Nullable
		private UUID entityUUID;
		@Nullable
		private BlockPos blockPos;
		private Vec3 lastKnownPos;
		private int remainingStrikes;
		private long nextEligibleTick;
		private double priorityScore;

		public TargetState(@Nullable UUID entityUUID, @Nullable BlockPos blockPos, Vec3 lastKnownPos,
				int remainingStrikes, long nextEligibleTick, double priorityScore) {
			this.entityUUID = entityUUID;
			this.blockPos = blockPos;
			this.lastKnownPos = lastKnownPos;
			this.remainingStrikes = remainingStrikes;
			this.nextEligibleTick = nextEligibleTick;
			this.priorityScore = priorityScore;
		}

		public void decrementStrikes() {
			this.remainingStrikes--;
		}

		public void writeToNbt(CompoundTag tag) {
			if (entityUUID != null) {
				tag.putUUID("entity_uuid", entityUUID);
			}
			if (blockPos != null) {
				tag.put("block_pos", NbtUtils.writeBlockPos(blockPos));
			}
			tag.putDouble("pos_x", lastKnownPos.x);
			tag.putDouble("pos_y", lastKnownPos.y);
			tag.putDouble("pos_z", lastKnownPos.z);
			tag.putInt("remaining_strikes", remainingStrikes);
			tag.putLong("next_eligible_tick", nextEligibleTick);
			tag.putDouble("priority_score", priorityScore);
		}

		public static TargetState readFromNbt(CompoundTag tag) {
			UUID uuid = tag.hasUUID("entity_uuid") ? tag.getUUID("entity_uuid") : null;
			BlockPos pos = null;
			if (tag.contains("block_pos")) {
				//? >= 1.21 {
				pos = NbtUtils.readBlockPos(tag, "block_pos").orElse(null);
				//?} else {
				/*pos = NbtUtils.readBlockPos(tag.getCompound("block_pos"));
				*///?}
			}
			Vec3 lastPos = new Vec3(tag.getDouble("pos_x"), tag.getDouble("pos_y"), tag.getDouble("pos_z"));
			int remaining = tag.getInt("remaining_strikes");
			long nextTick = tag.getLong("next_eligible_tick");
			double score = tag.getDouble("priority_score");
			return new TargetState(uuid, pos, lastPos, remaining, nextTick, score);
		}
	}
}
