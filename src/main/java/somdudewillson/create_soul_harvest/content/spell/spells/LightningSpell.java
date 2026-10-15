package somdudewillson.create_soul_harvest.content.spell.spells;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import somdudewillson.create_soul_harvest.content.spell.SimplePersistentGlyphSpell;
import somdudewillson.create_soul_harvest.content.spell.SpellThinker;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

public class LightningSpell extends SimplePersistentGlyphSpell {
	protected int remainingTicks;

	public LightningSpell(ResourceLocation id, int durationTicks) {
		super(id);
		
		this.remainingTicks = durationTicks;
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, caster.blockPosition());
			SpellThinker.spawn(serverLevel, surfacePos, this, caster.getUUID());
		}
	}

	@Override
	public void serverTick(SpellThinker thinker, ServerLevel level) {
		
		// Random strikes
		if (level.random.nextFloat() < 0.15f) {
			BlockPos center = thinker.blockPosition();
			Vec3 ownerPos = Optional.ofNullable(thinker.getOwner())
					.map(o->o.position())
					.orElse(null);
			int samples = 0;
			BlockPos strikePos;
			do {
				strikePos = center.offset(
							level.random.nextInt(31) - 15,
							0,
							level.random.nextInt(31) - 15
						);
				strikePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, strikePos);
			} while (samples < 5 && (ownerPos!=null && strikePos.distToCenterSqr(ownerPos) < 2*2));
			
			EntityType.LIGHTNING_BOLT.spawn(level, strikePos, MobSpawnType.TRIGGERED);
		}
		
		// Targeted strikes
		if (level.random.nextFloat() < 0.1f) {
			List<Entity> potentialTargets = level.getEntities(
					thinker, 
					AABB.ofSize(
							thinker.position(), 
							15*2, 35*2, 15*2
							), 
					getIsValidStrikeTargetPredicate(thinker)
				);
			
			for (Entity potentialTarget : getRandomSample(level.random, potentialTargets, 8)) {
				BlockPos potentialTargetPos = potentialTarget.blockPosition();
				BlockPos heightmapPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, potentialTargetPos);
				if (heightmapPos.getY() > potentialTargetPos.getY()) { continue; }
				
				EntityType.LIGHTNING_BOLT.spawn(level, potentialTargetPos, MobSpawnType.TRIGGERED);
				break;
			}
		}

		remainingTicks--;
	}

	@Override
	public void clientTick(SpellThinker thinker, Level level) {
		Vec3 thinkerPos = thinker.position();
		double centerX = thinkerPos.x() + 0.5;
		double centerY = thinkerPos.y() + 35;
		double centerZ = thinkerPos.z() + 0.5;

		for (int i = 0; i < 5; i++) {
			double px = centerX + level.random.nextGaussian() * 15;
			double py = centerY + level.random.nextGaussian() * 1.5;
			double pz = centerZ + level.random.nextGaussian() * 15;
			level.addAlwaysVisibleParticle(ModParticleTypes.STORM_CLOUD.get(), true, px, py, pz, 0, 0, 0);
		}
	}
	
	public Predicate<Entity> getIsValidStrikeTargetPredicate(SpellThinker thinker) {
		return e -> isValidStrikeTarget(thinker.getOwnerUUID(), thinker.getOwner().getTeam(), e);
	}
	
	public static boolean isValidStrikeTarget(UUID owner, Team ownerTeam, Entity e) {
		if (ownerTeam != null && ownerTeam.isAlliedTo(e.getTeam())) { return false; }
		if (e instanceof OwnableEntity oE && oE.getOwnerUUID().equals(owner)) { return false; }
		if (e.getUUID().equals(owner)) { return false; }
		
		if (!(e instanceof LivingEntity)) { return false; }
		LivingEntity lE = (LivingEntity) e;
		
		if (lE.isDeadOrDying()) { return false; }
		if (!lE.attackable()) { return false; }
		if (lE.isInvulnerable()) { return false; }
		
		if (lE.getType().getCategory() == MobCategory.MONSTER) {
			return true;
		}
		if (lE instanceof Monster) {
			return true;
		}
		return false;
	}
	
    public static <T> List<T> getRandomSample(RandomSource rand, List<T> list, int n) {
    	int listSize = list.size();
    	
        return IntStream.generate(()->rand.nextInt(listSize))
                .distinct()
                .limit(Math.min(n, listSize))
                .mapToObj(list::get)
                .collect(Collectors.toList());
    }

	@Override
	public boolean isFinished(SpellThinker thinker) {
		return remainingTicks <= 0;
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		tag.putInt("remaining_ticks", remainingTicks);
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		if (tag.contains("remaining_ticks")) {
			this.remainingTicks = tag.getInt("remaining_ticks");
		}
	}
}
