package somdudewillson.create_soul_harvest.content.potentia.catalyst;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

public class PotentiaGrowthCatalystBlock extends Block implements IBE<PotentiaGrowthCatalystBlockEntity>, SimpleWaterloggedBlock, IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public PotentiaGrowthCatalystBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.UP)
				.setValue(LIT, false)
				.setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction face = context.getClickedFace();
		Direction facing = face.getOpposite();
		if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
			facing = context.getNearestLookingDirection().getOpposite();
		}
		boolean waterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
		return this.defaultBlockState()
				.setValue(FACING, facing)
				.setValue(LIT, false)
				.setValue(WATERLOGGED, waterlogged);
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!level.isClientSide()) {
			Direction currentFacing = state.getValue(FACING);
			Direction nextFacing = Direction.values()[(currentFacing.ordinal() + 1) % Direction.values().length];
			BlockState newState = state.setValue(FACING, nextFacing);
			level.setBlock(pos, newState, 3);
			IWrenchable.playRotateSound(level, pos);
			withBlockEntityDo(level, pos, PotentiaGrowthCatalystBlockEntity::onFacingChanged);
		}
		return InteractionResult.SUCCESS;
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world,
								  BlockPos pos, BlockPos neighbourPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		if (!world.isClientSide()) {
			withBlockEntityDo(world, pos, PotentiaGrowthCatalystBlockEntity::onNeighborUpdate);
		}
		return super.updateShape(state, direction, neighbourState, world, pos, neighbourPos);
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
		super.neighborChanged(state, world, pos, otherBlock, neighborPos, isMoving);
		if (!world.isClientSide()) {
			withBlockEntityDo(world, pos, PotentiaGrowthCatalystBlockEntity::onNeighborUpdate);
		}
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, WATERLOGGED);
	}

	@Override
	public Class<PotentiaGrowthCatalystBlockEntity> getBlockEntityClass() {
		return PotentiaGrowthCatalystBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends PotentiaGrowthCatalystBlockEntity> getBlockEntityType() {
		return ModBlockEntities.POTENTIA_GROWTH_CATALYST_BE.get();
	}
}
