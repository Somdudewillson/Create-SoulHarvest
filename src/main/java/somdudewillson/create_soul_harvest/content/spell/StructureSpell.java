package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import somdudewillson.redsparklib.common.utils.OptNbtUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class StructureSpell implements PersistentGlyphSpell {
	
	// This is a record because we might want to put extra data in here in the future
	public static record BlockPlacement(BlockPos pos) {}

	@FunctionalInterface
	public static interface IBlockPlacer {
		void placeBlock(ServerLevel level, BlockPos pos);
	}

	protected Vec3 origin;
	protected int timer;
	protected List<BlockPlacement> remainingPlacements = new ArrayList<>();

	public StructureSpell() {
	}

	protected abstract List<BlockPlacement> supplyBlockPlacements(ServerLevel level, LivingEntity caster, Vec3 origin);
	
	protected abstract void placeBlock(ServerLevel level, BlockPos pos);

	protected abstract Vec3 getOrigin(ServerLevel level, LivingEntity caster);

	protected abstract int getPlaceInterval();

	protected abstract int getBlocksPerInterval();

	protected int getRandomWindowSize() {
		return 8;
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			this.timer = getPlaceInterval();
			this.origin = getOrigin(serverLevel, caster);

			List<BlockPlacement> unordered = supplyBlockPlacements(serverLevel, caster, this.origin);
			if (unordered == null || unordered.isEmpty()) {
				return;
			}

			this.remainingPlacements = new ArrayList<>(unordered);
			this.remainingPlacements.sort(Comparator.comparingDouble(p -> p.pos().distToCenterSqr(this.origin.x, this.origin.y, this.origin.z)));

			SpellThinker.spawn(serverLevel, this.origin, this, caster.getUUID());
		}
	}

	@Override
	public void tick(SpellThinker thinker, ServerLevel level) {
		if (remainingPlacements == null || remainingPlacements.isEmpty()) {
			return;
		}

		timer--;
		if (timer > 0) {
			return;
		}
		timer = getPlaceInterval();

		int blocksToPlace = getBlocksPerInterval();
		if (blocksToPlace <= 0) {
			return;
		}
		int placedCount = 0;

		while (placedCount < blocksToPlace && !remainingPlacements.isEmpty()) {
			int baseOffset = (placedCount * remainingPlacements.size()) / blocksToPlace;
			int available = remainingPlacements.size() - baseOffset;
			int candidateCount = Math.min(getRandomWindowSize(), available);
			int localIndex = selectRandomIndex(level, candidateCount, placedCount);
			int index = baseOffset + localIndex;
			BlockPlacement candidate = remainingPlacements.get(index);

			if (isValidPlacement(level, candidate.pos())) {
				placeBlock(level, candidate.pos());
				placedCount++;
			}
			remainingPlacements.remove(index);
		}
	}

	protected int selectRandomIndex(ServerLevel level, int candidateCount, int step) {
		if (candidateCount <= 1) {
			return 0;
		}
		int windowSize = getRandomWindowSize();
		int mask = windowSize - 1;
		long seed = level.getGameTime() + step;
		int raw = (int) (seed & mask);
		return raw % candidateCount;
	}

	protected boolean isValidPlacement(ServerLevel level, BlockPos pos) {
		if (level.isOutsideBuildHeight(pos)) {
			return false;
		}
		BlockState state = level.getBlockState(pos);
		return state.canBeReplaced();
	}

	@Override
	public boolean isFinished(SpellThinker thinker) {
		return remainingPlacements == null || remainingPlacements.isEmpty();
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		tag.putInt("timer", timer);
		if (remainingPlacements != null && !remainingPlacements.isEmpty()) {
			long[] positions = new long[remainingPlacements.size()];
			for (int i = 0; i < remainingPlacements.size(); i++) {
				positions[i] = remainingPlacements.get(i).pos().asLong();
			}
			tag.putLongArray("remaining_positions", positions);
		}
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		this.timer = OptNbtUtils.getInt(tag, "timer").orElse(getPlaceInterval());
		if (tag.contains("remaining_positions")) {
			long[] positions = tag.getLongArray("remaining_positions");
			this.remainingPlacements = new ArrayList<>(positions.length);
			for (long pos : positions) {
				this.remainingPlacements.add(new BlockPlacement(BlockPos.of(pos)));
			}
		}
	}
}
