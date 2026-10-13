package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.UUID;

public class AnimaGlassBlock extends Block implements EntityBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

	public AnimaGlassBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AnimaGlassBlockEntity(pos, state);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int age = state.getValue(AGE);
		if (age < 3) {
			UUID oldUuid = null;
			BlockEntity oldBe = level.getBlockEntity(pos);
			if (oldBe instanceof AnimaGlassBlockEntity animaBe) {
				oldUuid = animaBe.getOwnerId();
			}

			level.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));

			if (oldUuid != null) {
				BlockEntity newBe = level.getBlockEntity(pos);
				if (newBe instanceof AnimaGlassBlockEntity animaBe) {
					animaBe.setOwnerId(oldUuid);
				}
			}
		} else {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
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
