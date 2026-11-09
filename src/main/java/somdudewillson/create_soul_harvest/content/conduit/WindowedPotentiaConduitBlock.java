package somdudewillson.create_soul_harvest.content.conduit;

import com.simibubi.create.content.equipment.wrench.IWrenchableWithBracket;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import somdudewillson.create_soul_harvest.content.conduit.network.PotentiaNetwork;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

import java.util.Optional;

public class WindowedPotentiaConduitBlock extends RotatedPillarBlock implements IBE<WindowedPotentiaConduitBlockEntity>, SimpleWaterloggedBlock, IPotentiaTransporter, IWrenchableWithBracket {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty POS_RIM = BooleanProperty.create("pos_rim");
	public static final BooleanProperty NEG_RIM = BooleanProperty.create("neg_rim");

	private static final VoxelShape SHAPE_X = Block.box(0.0, 4.0, 4.0, 16.0, 12.0, 12.0);
	private static final VoxelShape SHAPE_Y = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
	private static final VoxelShape SHAPE_Z = Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 16.0);

	public WindowedPotentiaConduitBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(AXIS, Axis.Y)
				.setValue(WATERLOGGED, false)
				.setValue(POS_RIM, true)
				.setValue(NEG_RIM, true));
	}

	@Override
	public boolean canConnect(BlockState state, Direction side) {
		return side.getAxis() == state.getValue(AXIS);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Axis axis = state.getValue(AXIS);
		return switch (axis) {
			case X -> SHAPE_X;
			case Z -> SHAPE_Z;
			default -> SHAPE_Y;
		};
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		if (tryRemoveBracket(context))
			return InteractionResult.SUCCESS;

		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!world.isClientSide()) {
			Axis axis = state.getValue(AXIS);
			Direction posDir = Direction.get(AxisDirection.POSITIVE, axis);
			Direction negDir = Direction.get(AxisDirection.NEGATIVE, axis);

			BlockState regularState = ModBlocks.POTENTIA_CONDUIT.get().defaultBlockState()
					.setValue(PotentiaConduitBlock.PROPERTY_BY_DIRECTION.get(posDir), PotentiaConduitBlock.ConduitConnectionType.RIM)
					.setValue(PotentiaConduitBlock.PROPERTY_BY_DIRECTION.get(negDir), PotentiaConduitBlock.ConduitConnectionType.RIM)
					.setValue(WATERLOGGED, state.getValue(WATERLOGGED));

			regularState = PotentiaConduitBlock.updateBlockState(regularState, posDir, null, world, pos);
			world.setBlock(pos, regularState, 3);
			PotentiaNetwork.invalidate(pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext) {
		return Optional.empty();
	}

	//? >= 1.21 {
	@Override
	public ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state) {
		return new ItemStack(ModBlocks.POTENTIA_CONDUIT.get());
	}
	//?} else {
	/*@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return new ItemStack(ModBlocks.POTENTIA_CONDUIT.get());
	}
	*///?}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		BlockState state = this.defaultBlockState()
				.setValue(AXIS, context.getClickedFace().getAxis())
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
		return updateBlockState(state, context.getLevel(), context.getClickedPos());
	}

	public static BlockState updateBlockState(BlockState state, LevelAccessor world, BlockPos pos) {
		Axis axis = state.getValue(AXIS);
		Direction posDir = Direction.get(AxisDirection.POSITIVE, axis);
		Direction negDir = Direction.get(AxisDirection.NEGATIVE, axis);

		boolean posRim = shouldHaveRim(world, pos, posDir);
		boolean negRim = shouldHaveRim(world, pos, negDir);

		return state.setValue(POS_RIM, posRim).setValue(NEG_RIM, negRim);
	}

	public static boolean shouldHaveRim(LevelAccessor world, BlockPos pos, Direction dir) {
		BlockPos neighborPos = pos.relative(dir);
		BlockState neighborState = world.getBlockState(neighborPos);

		if (neighborState.getBlock() instanceof WindowedPotentiaConduitBlock) {
			return neighborState.getValue(AXIS) != dir.getAxis();
		}

		if (neighborState.getBlock() instanceof PotentiaConduitBlock) {
			return false;
		}

		return true;
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
		return updateBlockState(state, world, pos);
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
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED, POS_RIM, NEG_RIM);
	}

	@Override
	public Class<WindowedPotentiaConduitBlockEntity> getBlockEntityClass() {
		return WindowedPotentiaConduitBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends WindowedPotentiaConduitBlockEntity> getBlockEntityType() {
		return ModBlockEntities.WINDOWED_POTENTIA_CONDUIT_BE.get();
	}
}
