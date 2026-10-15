package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModTags;

import static somdudewillson.create_soul_harvest.registry.ModTags.Blocks.CONSIDER_REPLACEABLE;

import java.util.ArrayList;
import java.util.List;

public class TerrainFlattenSpell implements PersistentGlyphSpell {

	public static final ResourceLocation ID = SoulHarvestMod.id("terrain_flatten");
	public static final int EFFECT_SIZE = 15;
	public static final int MAX_VERTICAL_SEARCH = 15;
	public static final int DOWNSHIFT_MAX_SIZE = 10;

	private int flattenStep = 0;
	private List<BlockPos> flattenPositions;

	public TerrainFlattenSpell() {
		this(0);
	}

	public TerrainFlattenSpell(int initialStep) {
		this.flattenStep = initialStep;
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			BlockPos spawnPos = caster.blockPosition();
			while (serverLevel.getBlockState(spawnPos.below()).canBeReplaced() || serverLevel.getBlockState(spawnPos.below()).is(CONSIDER_REPLACEABLE)) {
				spawnPos = spawnPos.below();
				if (serverLevel.isOutsideBuildHeight(spawnPos) || serverLevel.isOutsideBuildHeight(spawnPos.above())) {
					return;
				}
			}

			SpellThinker.spawn(serverLevel, spawnPos, this, caster.getUUID());
		}
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	private void ensurePositions(BlockPos center) {
		if (flattenPositions == null) {
			flattenPositions = generateSpiral(center.below(), EFFECT_SIZE);
		}
	}

	@Override
	public void tick(SpellThinker thinker, ServerLevel level) {
		BlockPos entityPos = thinker.blockPosition();
		ensurePositions(entityPos);

		level.sendParticles(ParticleTypes.WITCH, entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5, 20, EFFECT_SIZE / 2.0, 1, EFFECT_SIZE / 2.0, 0);

		if (flattenStep >= flattenPositions.size()) {
			return;
		}

		BlockPos currentPos = flattenPositions.get(flattenStep);
		BlockState currentState = level.getBlockState(currentPos);
		level.sendParticles(ParticleTypes.POOF, currentPos.getX() + 0.5, currentPos.getY() + 1.5, currentPos.getZ() + 0.5, 1, 0.5, 1, 0.5, 0);

		if (isFlattenReplaceable(currentState)) {
			// Raising height
			boolean findingGround = true;
			BlockPos raisePos = currentPos;
			BlockState raiseState = null;
			BlockPos searchPos = raisePos;
			for (int i = 0; i < MAX_VERTICAL_SEARCH; i++) {
				searchPos = searchPos.below();
				BlockState searchState = level.getBlockState(searchPos);

				if (findingGround && !isFlattenReplaceable(searchState)) {
					findingGround = false;
					raisePos = searchPos.above();
				}
				if (!isFlattenReplaceable(searchState) && searchState.is(ModTags.Blocks.FLATTEN_CAN_GENERATE)) {
					raiseState = searchState;
				}
				if (!findingGround && raiseState != null) break;
			}

			BlockState raiseReplacedState = level.getBlockState(raisePos);
			if (!findingGround && raiseState != null && (raiseReplacedState.is(ModTags.Blocks.FLATTEN_CAN_DISPLACE) || isFlattenReplaceable(raiseReplacedState))) {
				level.setBlockAndUpdate(raisePos, raiseState);
			} else {
				flattenStep++;
			}
		} else {
			// Reducing height
			BlockPos abovePos = currentPos.above();
			BlockState aboveState = level.getBlockState(abovePos);
			if (isFlattenReplaceable(aboveState)) {
				flattenStep++;
			} else {
				BlockPos pullPos = abovePos;
				BlockState pullState = aboveState;
				boolean foundTop = false;

				for (int i = 0; i < MAX_VERTICAL_SEARCH; i++) {
					BlockPos nextAbove = pullPos.above();
					BlockState nextAboveState = level.getBlockState(nextAbove);
					if (isFlattenReplaceable(nextAboveState)) {
						foundTop = true;
						break;
					} else {
						pullPos = nextAbove;
						pullState = nextAboveState;
					}
				}

				if (!foundTop || !pullState.is(ModTags.Blocks.FLATTEN_CAN_DISPLACE)) {
					flattenStep++;
				} else {
					List<BlockState> pullStates = new ArrayList<>();
					pullStates.add(pullState);
					BlockPos crushedPos = pullPos.below();
					for (int i = 0; i < DOWNSHIFT_MAX_SIZE; i++) {
						BlockState crushedState = level.getBlockState(crushedPos);
						if (isFlattenReplaceable(crushedState) || crushedState.is(ModTags.Blocks.FLATTEN_CAN_CRUSH)) {
							for (int s = pullStates.size() - 1; s >= 0; s--) {
								level.setBlockAndUpdate(crushedPos, pullStates.get(s));
								crushedPos = crushedPos.above();
							}
							level.setBlockAndUpdate(pullPos, Blocks.AIR.defaultBlockState());
							break;
						} else {
							crushedPos = crushedPos.below();
							pullStates.add(crushedState);
						}

						if (i >= DOWNSHIFT_MAX_SIZE - 1) {
							if (isFlattenReplaceable(pullState) || pullState.is(ModTags.Blocks.FLATTEN_CAN_CRUSH)) {
								level.setBlockAndUpdate(pullPos, Blocks.AIR.defaultBlockState());
							} else {
								flattenStep++;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isFinished(SpellThinker thinker) {
		ensurePositions(thinker.blockPosition());
		return flattenStep >= flattenPositions.size();
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		tag.putInt("flatten_step", flattenStep);
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		if (tag.contains("flatten_step")) {
			this.flattenStep = tag.getInt("flatten_step");
		}
	}

	private static boolean isFlattenReplaceable(BlockState state) {
		return state.canBeReplaced() || state.is(ModTags.Blocks.CONSIDER_REPLACEABLE);
	}

	private static List<BlockPos> generateSpiral(BlockPos center, int radius) {
		List<BlockPos> positions = new ArrayList<>();
		int x = 0, z = 0;
		int dx = 0, dz = -1;
		int max = (2 * radius + 1) * (2 * radius + 1);
		for (int i = 0; i < max; i++) {
			if ((-radius <= x) && (x <= radius) && (-radius <= z) && (z <= radius)) {
				positions.add(center.offset(x, 0, z));
			}
			if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
				int t = dx;
				dx = -dz;
				dz = t;
			}
			x += dx;
			z += dz;
		}
		return positions;
	}
}
