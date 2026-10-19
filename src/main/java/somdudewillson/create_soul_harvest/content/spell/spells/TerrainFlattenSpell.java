package somdudewillson.create_soul_harvest.content.spell.spells;

import static somdudewillson.create_soul_harvest.registry.ModTags.Blocks.CONSIDER_REPLACEABLE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.SimplePersistentGlyphSpell;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class TerrainFlattenSpell extends SimplePersistentGlyphSpell {	
	public static final int MAX_VERTICAL_SEARCH = 15;
	public static final int DOWNSHIFT_MAX_SIZE = 10;

	private final int effectSize;
	private int flattenStep = 0;
	private List<BlockPos> flattenPositions;

	public TerrainFlattenSpell(ResourceLocation id, int effectSize) {
		this(id, 0, effectSize);
	}

	public TerrainFlattenSpell(ResourceLocation id, int initialStep, int effectSize) {
		super(id);
		
		this.flattenStep = initialStep;
		this.effectSize = effectSize;
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

			SpellThinkerEntity.spawn(serverLevel, spawnPos, this, caster.getUUID());
		}
	}

	private void ensurePositions(BlockPos center) {
		if (flattenPositions == null) {
			flattenPositions = generateSpiral(center.below(), effectSize);
		}
	}

	@Override
	public void serverTick(SpellThinkerEntity thinker, ServerLevel level) {
		BlockPos entityPos = thinker.blockPosition();
		ensurePositions(entityPos);

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
				createBlock(level, raisePos, raiseState);
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
							removeBlock(level, pullPos);
							break;
						} else {
							crushedPos = crushedPos.below();
							pullStates.add(crushedState);
						}

						if (i >= DOWNSHIFT_MAX_SIZE - 1) {
							if (isFlattenReplaceable(pullState) || pullState.is(ModTags.Blocks.FLATTEN_CAN_CRUSH)) {
								removeBlock(level, pullPos);
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
	public void clientTick(SpellThinkerEntity thinker, Level level) {
		BlockPos entityPos = thinker.blockPosition();
		double cx = entityPos.getX() + 0.5;
		double cy = entityPos.getY() + 0.5;
		double cz = entityPos.getZ() + 0.5;

		for (int i = 0; i < 20; i++) {
			double px = cx + level.random.nextGaussian() * (effectSize / 2.0);
			double py = cy + level.random.nextGaussian() * 1.0;
			double pz = cz + level.random.nextGaussian() * (effectSize / 2.0);
			level.addParticle(ParticleTypes.WITCH, px, py, pz, 0, 0, 0);
		}
	}

	@Override
	public boolean isFinished(SpellThinkerEntity thinker) {
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

	protected void createBlock(ServerLevel level, BlockPos pos, BlockState state) {
		level.setBlockAndUpdate(pos, state);
		if (state != null && !state.isAir()) {
			SoundType soundType = state.getSoundType(level, pos, null);
			level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 0.5F) / 3.0F, soundType.getPitch() * 0.8F);
		}
	}

	protected void placeBlock(ServerLevel level, BlockPos pos, BlockState state) {
		createBlock(level, pos, state);
	}

	protected void removeBlock(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		removeBlock(level, pos, state);
	}

	protected void removeBlock(ServerLevel level, BlockPos pos, BlockState state) {
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		if (state != null && !state.isAir()) {
			SoundType soundType = state.getSoundType(level, pos, null);
			level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 0.5F) / 3.0F, soundType.getPitch() * 0.8F);
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
