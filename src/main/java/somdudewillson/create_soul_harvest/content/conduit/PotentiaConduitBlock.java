package somdudewillson.create_soul_harvest.content.conduit;

import com.simibubi.create.content.equipment.wrench.IWrenchableWithBracket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.conduit.network.PotentiaNetwork;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaHelper;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class PotentiaConduitBlock extends Block implements SimpleWaterloggedBlock, IPotentiaTransporter, IWrenchableWithBracket {

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
		Direction preferred = context.getNearestLookingDirection().getOpposite();
		BlockState state = updateBlockState(this.defaultBlockState(), preferred, null, level, pos);
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
		return updateBlockState(state, null, null, world, pos);
	}

	public static BlockState updateBlockState(BlockState state, @Nullable Direction preferredDirection, @Nullable Direction ignore,
											  LevelAccessor world, BlockPos pos) {
		BlockState prevState = state;
		int prevConnections = 0;
		for (Direction d : Direction.values()) {
			if (prevState.getValue(PROPERTY_BY_DIRECTION.get(d))) {
				prevConnections++;
			}
		}

		// Update sides that are not ignored
		for (Direction d : Direction.values()) {
			if (d != ignore) {
				boolean shouldConnect = canConnectTo(world, pos.relative(d), d.getOpposite());
				state = state.setValue(PROPERTY_BY_DIRECTION.get(d), shouldConnect);
			}
		}

		// Count connections to actual neighbors
		Direction firstConnected = null;
		int connectionCount = 0;
		for (Direction d : Direction.values()) {
			if (state.getValue(PROPERTY_BY_DIRECTION.get(d))) {
				connectionCount++;
				if (firstConnected == null) {
					firstConnected = d;
				}
			}
		}

		// If 2 or more real connections, keep all connected sides
		if (connectionCount >= 2) {
			return state;
		}

		// If exactly 1 connection, extend the opposite direction to make a straight pipe through
		if (connectionCount == 1) {
			return state.setValue(PROPERTY_BY_DIRECTION.get(firstConnected.getOpposite()), true);
		}

		// If 0 connections to neighbors:
		// If it was already a straight 2-ended pipe, preserve that orientation
		if (prevConnections == 2 && getStraightAxis(prevState) != null) {
			return prevState;
		}

		// Otherwise, orient along preferredDirection
		if (preferredDirection != null) {
			return state.setValue(PROPERTY_BY_DIRECTION.get(preferredDirection), true)
					.setValue(PROPERTY_BY_DIRECTION.get(preferredDirection.getOpposite()), true);
		}

		return state.setValue(NORTH, true).setValue(SOUTH, true);
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

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		if (tryRemoveBracket(context))
			return InteractionResult.SUCCESS;

		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction clickedFace = context.getClickedFace();

		Direction.Axis axis = getStraightAxis(state);
		if (axis == null) {
			Vec3 clickLocation = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
			double closest = Double.MAX_VALUE;
			Direction argClosest = Direction.UP;
			for (Direction direction : Direction.values()) {
				if (clickedFace.getAxis() == direction.getAxis())
					continue;
				Vec3 centerOf = Vec3.atCenterOf(direction.getNormal());
				double distance = centerOf.distanceToSqr(clickLocation);
				if (distance < closest) {
					closest = distance;
					argClosest = direction;
				}
			}
			axis = argClosest.getAxis();
		}

		if (clickedFace.getAxis() == axis)
			return InteractionResult.PASS;

		if (!world.isClientSide()) {
			BlockState windowedState = ModBlocks.WINDOWED_POTENTIA_CONDUIT.get().defaultBlockState()
					.setValue(WindowedPotentiaConduitBlock.AXIS, axis)
					.setValue(WindowedPotentiaConduitBlock.WATERLOGGED, state.getValue(WATERLOGGED));
			world.setBlock(pos, windowedState, 3);
			PotentiaNetwork.invalidate(pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	public static Direction.Axis getStraightAxis(BlockState state) {
		boolean x = state.getValue(EAST) && state.getValue(WEST) && !state.getValue(UP) && !state.getValue(DOWN) && !state.getValue(NORTH) && !state.getValue(SOUTH);
		boolean y = state.getValue(UP) && state.getValue(DOWN) && !state.getValue(EAST) && !state.getValue(WEST) && !state.getValue(NORTH) && !state.getValue(SOUTH);
		boolean z = state.getValue(NORTH) && state.getValue(SOUTH) && !state.getValue(EAST) && !state.getValue(WEST) && !state.getValue(UP) && !state.getValue(DOWN);
		if (x) return Direction.Axis.X;
		if (y) return Direction.Axis.Y;
		if (z) return Direction.Axis.Z;
		return null;
	}

	@Override
	public Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext) {
		return Optional.empty();
	}

	public static boolean canConnectTo(LevelAccessor level, BlockPos targetPos, Direction sideOnTarget) {
		BlockState targetState = level.getBlockState(targetPos);
		if (targetState.isAir()) {
			return false;
		}
		if (targetState.getBlock() instanceof PotentiaConduitBlock) {
			return true;
		}
		if (targetState.getBlock() instanceof IPotentiaTransporter transporter) {
			return transporter.canConnect(targetState, sideOnTarget);
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
