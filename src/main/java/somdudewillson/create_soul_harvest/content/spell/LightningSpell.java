package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class LightningSpell implements PersistentGlyphSpell {

	public static final ResourceLocation ID = SoulHarvestMod.id("lightning");
	public static final int DEFAULT_DURATION = 20 * 15; // 15 seconds

	private int remainingTicks;

	public LightningSpell() {
		this(DEFAULT_DURATION);
	}

	public LightningSpell(int durationTicks) {
		this.remainingTicks = durationTicks;
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			SpellThinker.spawn(serverLevel, caster.blockPosition(), this);
		}
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void tick(SpellThinker thinker, ServerLevel level) {
		if (level.random.nextFloat() < 0.2f) {
			BlockPos center = thinker.blockPosition();
			BlockPos strikePos = center.offset(
					level.random.nextInt(31) - 15,
					0,
					level.random.nextInt(31) - 15
			);
			strikePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, strikePos);
			EntityType.LIGHTNING_BOLT.spawn(level, strikePos, MobSpawnType.TRIGGERED);
		}

		BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, thinker.blockPosition());
		level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, surfacePos.getX(), surfacePos.getY() + 35, surfacePos.getZ(), 50, 15, 1.5, 15, 0);

		remainingTicks--;
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
