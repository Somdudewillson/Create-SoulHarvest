package somdudewillson.create_soul_harvest.content.conduit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import somdudewillson.create_soul_harvest.content.conduit.network.PotentiaNetwork;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaHelper;

import java.util.EnumMap;
import java.util.Map;

public class PotentiaConduitBlock extends Block implements SimpleWaterloggedBlock, IPotentiaTransporter {

	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = new EnumMap<>(Direction.class);

	private static final VoxelShape CORE_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);
	private static final Map<Direction, VoxelShape> ARM_SHAPES = new EnumMap<>(Direction.class);

	static {
		PROPERTY_BY_DIRECTION.put(Direction.NORTH, NORTH);
		PROPERTY_BY_DIRECTION.put(Direction.SOUTH, SOUTH);
		PROPERTY_BY_DIRECTION.put(Direction.EAST, EAST);
		PROPERTY_BY_DIRECTION.put(Direction.WEST, WEST);
		PROPERTY_BY_DIRECTION.put(Direction.UP, UP);
		PROPERTY_BY_DIRECTION.put(Direction.DOWN, DOWN);

		ARM_SHAPES.put(Direction.NORTH, Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 4.0));
		ARM_SHAPES.put(Direction.SOUTH, Block.box(4.0, 4.0, 12.0, 12.0, 12.0, 16.0));
		ARM_SHAPES.put(Direction.EAST, Block.box(12.0, 4.0, 4.0, 16.0, 12.0, 12.0));
		ARM_SHAPES.put(Direction.WEST, Block.box(0.0, 4.0, 4.0, 4.0, 12.0, 12.0));
		ARM_SHAPES.put(Direction.UP, Block.box(4.0, 12.0, 4.0, 12.0, 16.0, 12.0));
		ARM_SHAPES.put(Direction.DOWN, Block.box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0));
	}

	public PotentiaConduitBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(SOUTH, false)
				.setValue(EAST, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(WATERLOGGED, false));
	}

	@Override
	public boolean canConnect(BlockState state, Direction side) {
		return state.getValue(PROPERTY_BY_DIRECTION.get(side));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = CORE_SHAPE;
		for (Direction d : Direction.values()) {
			if (state.getValue(PROPERTY_BY_DIRECTION.get(d))) {
				shape = Shapes.or(shape, ARM_SHAPES.get(d));
			}
		}
		return shape;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = this.defaultBlockState();

		for (Direction d : Direction.values()) {
			state = state.setValue(PROPERTY_BY_DIRECTION.get(d), canConnectTo(level, pos.relative(d), d.getOpposite()));
		}

		boolean waterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
		return state.setValue(WATERLOGGED, waterlogged);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world,
								  BlockPos pos, BlockPos neighbourPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		if (!world.isClientSide()) {
			PotentiaNetwork.invalidate(pos);
		}
		return state.setValue(PROPERTY_BY_DIRECTION.get(direction), canConnectTo(world, neighbourPos, direction.getOpposite()));
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!level.isClientSide()) {
			PotentiaNetwork.invalidate(pos);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, level, pos, newState, isMoving);
		if (!level.isClientSide()) {
			PotentiaNetwork.invalidate(pos);
		}
	}

	public static boolean canConnectTo(LevelAccessor level, BlockPos targetPos, Direction sideOnTarget) {
		BlockState targetState = level.getBlockState(targetPos);
		if (targetState.getBlock() instanceof PotentiaConduitBlock) {
			return true;
		}
		if (level instanceof Level l) {
			IPotentiaHandler handler = PotentiaHelper.getHandler(l, targetPos, sideOnTarget);
			if (handler != null) {
				return handler.canAccept(sideOnTarget) || handler.canPush(sideOnTarget);
			}
		}
		return false;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
	}
}
