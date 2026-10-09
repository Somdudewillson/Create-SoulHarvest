package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SoulClusterBlock extends SoulBudBlock {

	public SoulClusterBlock(int size, int offset, Properties properties) {
		super(size, offset, null, properties);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return false;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		// Fully grown, no further growth
	}
}
