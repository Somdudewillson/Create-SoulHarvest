package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.UUID;

public class AnimaGlassBlock extends TransparentBlock implements EntityBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	public static final int TICK_RATE = 20;
	public static final int DEFAULT_LIFESPAN = 20 * 16;

	public AnimaGlassBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AnimaGlassBlockEntity(pos, state);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!level.isClientSide() && !state.is(oldState.getBlock())) {
			int tickOffset = pos.getX() * 7;
			tickOffset += pos.getY() * 17;
			tickOffset += pos.getZ() * 31;
			tickOffset += level.getGameTime() * 67;
			tickOffset &= 63;
			level.scheduleTick(pos, this, TICK_RATE + tickOffset);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof AnimaGlassBlockEntity animaBe)) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}

		int currentLifespan = animaBe.getCurrentLifespan() + TICK_RATE;
		animaBe.setCurrentLifespan(currentLifespan);

		int maxLifespan = animaBe.getMaxLifespan();
		if (maxLifespan <= 0) {
			maxLifespan = DEFAULT_LIFESPAN;
		}

		if (currentLifespan >= maxLifespan) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}

		int targetAge = Math.min(3, Math.max(0, (currentLifespan * 4) / maxLifespan));
		if (state.getValue(AGE) != targetAge) {
			UUID owner = animaBe.getOwnerId();
			level.setBlock(pos, state.setValue(AGE, targetAge), 3);
			BlockEntity newBe = level.getBlockEntity(pos);
			if (newBe instanceof AnimaGlassBlockEntity newAnimaBe) {
				newAnimaBe.setOwnerId(owner);
				newAnimaBe.setMaxLifespan(maxLifespan);
				newAnimaBe.setCurrentLifespan(currentLifespan);
			}
		}

		level.scheduleTick(pos, this, TICK_RATE);
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
		return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		if (player.isCreative()) return super.getDestroyProgress(state, player, level, pos);
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof AnimaGlassBlockEntity animaBe) {
			UUID owner = animaBe.getOwnerId();
			if (owner != null && !player.getUUID().equals(owner)) {
				return 0.0f;
			}
		}
		return super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
