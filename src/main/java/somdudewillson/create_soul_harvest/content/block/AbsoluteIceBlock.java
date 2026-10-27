package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.chunk.AbsoluteIceChunkHelper;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

public class AbsoluteIceBlock extends Block implements EntityBlock {

	public AbsoluteIceBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AbsoluteIceBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide
				? createTickerHelper(blockEntityType, ModBlockEntities.ABSOLUTE_ICE_BE.get(), AbsoluteIceBlockEntity::clientTick)
				: createTickerHelper(blockEntityType, ModBlockEntities.ABSOLUTE_ICE_BE.get(), AbsoluteIceBlockEntity::serverTick);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> actualType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
		return expectedType == actualType ? (BlockEntityTicker<A>) ticker : null;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!level.isClientSide && !state.is(oldState.getBlock())) {
			long gameTime = level.getGameTime();
			AbsoluteIceChunkHelper.registerIce(level, pos, gameTime);
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof AbsoluteIceBlockEntity iceBe) {
				iceBe.setPlacementTime(gameTime);
			}
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (!level.isClientSide) {
				AbsoluteIceChunkHelper.unregisterIce(level, pos);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
}
