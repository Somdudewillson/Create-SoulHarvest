package somdudewillson.create_soul_harvest.content.kinetics.extractor;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class SoulExtractorBlock extends DirectionalKineticBlock
		implements SimpleWaterloggedBlock, ICogWheel, IBE<SoulExtractorBlockEntity> {

	public SoulExtractorBlock(Properties properties) {
		super(properties);
		registerDefaultState(super.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction face = context.getClickedFace();
		BlockPos clickedPos = pos.relative(face.getOpposite());
		BlockState clickedState = level.getBlockState(clickedPos);

		Direction facing;
		if (clickedState.is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS)) {
			facing = face.getOpposite();
		} else {
			facing = context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
					? context.getNearestLookingDirection()
					: context.getNearestLookingDirection().getOpposite();
		}

		boolean waterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
		return defaultBlockState()
				.setValue(FACING, facing)
				.setValue(BlockStateProperties.WATERLOGGED, waterlogged);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world,
								  BlockPos pos, BlockPos neighbourPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		if (!world.isClientSide()) {
			withBlockEntityDo(world, pos, SoulExtractorBlockEntity::onNeighborUpdate);
		}
		return state;
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
		super.neighborChanged(state, world, pos, otherBlock, neighborPos, isMoving);
		if (!world.isClientSide()) {
			withBlockEntityDo(world, pos, SoulExtractorBlockEntity::onNeighborUpdate);
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public Class<SoulExtractorBlockEntity> getBlockEntityClass() {
		return SoulExtractorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SoulExtractorBlockEntity> getBlockEntityType() {
		return ModBlockEntities.SOUL_EXTRACTOR_BE.get();
	}
}
