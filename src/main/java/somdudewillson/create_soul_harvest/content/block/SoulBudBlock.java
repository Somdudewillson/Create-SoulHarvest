package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class SoulBudBlock extends Block implements SimpleWaterloggedBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected final VoxelShape shapeUp;
	protected final VoxelShape shapeDown;
	protected final VoxelShape shapeNorth;
	protected final VoxelShape shapeSouth;
	protected final VoxelShape shapeEast;
	protected final VoxelShape shapeWest;

	private final Supplier<Block> nextStageBlock;

	public SoulBudBlock(int size, int offset, Supplier<Block> nextStageBlock, Properties properties) {
		super(properties);
		this.nextStageBlock = nextStageBlock;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.UP)
				.setValue(WATERLOGGED, Boolean.FALSE));

		this.shapeUp = Block.box(offset, 0.0, offset, 16 - offset, size, 16 - offset);
		this.shapeDown = Block.box(offset, 16 - size, offset, 16 - offset, 16.0, 16 - offset);
		this.shapeNorth = Block.box(offset, offset, 16 - size, 16 - offset, 16 - offset, 16.0);
		this.shapeSouth = Block.box(offset, offset, 0.0, 16 - offset, 16 - offset, size);
		this.shapeEast = Block.box(0.0, offset, offset, size, 16 - offset, 16 - offset);
		this.shapeWest = Block.box(16 - size, offset, offset, 16.0, 16 - offset, 16 - offset);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);
		return switch (direction) {
			case NORTH -> this.shapeNorth;
			case SOUTH -> this.shapeSouth;
			case EAST -> this.shapeEast;
			case WEST -> this.shapeWest;
			case DOWN -> this.shapeDown;
			default -> this.shapeUp;
		};
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos supportingPos = pos.relative(direction.getOpposite());
		return level.getBlockState(supportingPos).isFaceSturdy(level, supportingPos, direction);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return facing == state.getValue(FACING).getOpposite() && !state.canSurvive(level, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		LevelAccessor level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		return this.defaultBlockState()
				.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER)
				.setValue(FACING, context.getClickedFace());
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (nextStageBlock == null) return;
		Direction attachedFace = state.getValue(FACING).getOpposite();
		BlockPos attachedPos = pos.relative(attachedFace);
		BlockState attachedState = level.getBlockState(attachedPos);

		if (attachedState.is(Blocks.SPAWNER)) {
			Block nextBlock = nextStageBlock.get();
			if (nextBlock != null) {
				BlockState nextState = nextBlock.defaultBlockState()
						.setValue(FACING, state.getValue(FACING))
						.setValue(WATERLOGGED, state.getValue(WATERLOGGED));
				level.setBlockAndUpdate(pos, nextState);
			}
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}
}
