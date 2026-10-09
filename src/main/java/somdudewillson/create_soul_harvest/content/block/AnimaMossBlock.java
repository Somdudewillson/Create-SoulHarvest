package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;

public class AnimaMossBlock extends Block {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

	public AnimaMossBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(AGE, 0)
				.setValue(TRIGGERED, Boolean.FALSE));
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int maxIters = 3;
		int maxGrowth = 2;
		if (state.getValue(TRIGGERED)) {
			maxIters += 4;
			maxGrowth += 3;
		}

		// Reset triggered
		level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, Boolean.FALSE));

		Deque<BlockPos> validPositions = new ArrayDeque<>();
		validPositions.push(pos);
		Set<Long> visitedPositions = new HashSet<>();
		int growths = 0;

		List<Direction> shuffledDirections = new ArrayList<>(Arrays.asList(Direction.values()));

		for (int i = 0; i < maxIters && growths < maxGrowth && !validPositions.isEmpty(); i++) {
			BlockPos currentCenter = validPositions.pop();
			visitedPositions.add(currentCenter.asLong());

			BlockPos bestPos = null;
			Collections.shuffle(shuffledDirections);

			for (Direction originDir : shuffledDirections) {
				BlockPos testPos = currentCenter.relative(originDir);
				BlockState testBlock = level.getBlockState(testPos);
				if (testBlock.is(this) && !visitedPositions.contains(testPos.asLong())) {
					validPositions.push(testPos);
				}
				if (!testBlock.canBeReplaced()) {
					continue;
				}

				int validAdjacentsCount = 0;
				for (Direction testDir : Direction.values()) {
					BlockPos adjTestPos = testPos.relative(testDir);
					BlockState adjTestBlock = level.getBlockState(adjTestPos);
					if (adjTestBlock.is(Blocks.GLASS)) continue;
					if (!adjTestBlock.canBeReplaced() && adjTestBlock.getFluidState().isEmpty()) {
						validAdjacentsCount++;
					}
				}
				if (validAdjacentsCount >= 3) {
					bestPos = testPos;
					break;
				}
			}

			if (bestPos != null) {
				BlockState newBlockState = this.defaultBlockState().setValue(AGE, 0);
				if (random.nextFloat() < 0.1f) {
					newBlockState = newBlockState.setValue(AGE, random.nextInt(3) + 1);
				}
				level.setBlockAndUpdate(bestPos, newBlockState);
				growths++;

				if (!visitedPositions.contains(bestPos.asLong())) {
					validPositions.push(bestPos);
				}
				validPositions.push(currentCenter);
			}
		}
	}

	@Override
	public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		triggerSurrounding(level, pos);
		super.wasExploded(level, pos, explosion);
	}

	private void triggerSurrounding(Level level, BlockPos pos) {
		for (Direction dir : Direction.values()) {
			BlockPos testPos = pos.relative(dir);
			BlockState testBlock = level.getBlockState(testPos);
			if (testBlock.is(this)) {
				level.setBlockAndUpdate(testPos, testBlock.setValue(TRIGGERED, Boolean.TRUE));
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE, TRIGGERED);
	}
}
