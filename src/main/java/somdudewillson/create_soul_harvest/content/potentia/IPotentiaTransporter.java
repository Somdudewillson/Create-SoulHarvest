package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IPotentiaTransporter {

	boolean canConnect(BlockState state, Direction side);

	default boolean hasCollision(SoulPotentiaStack incoming) {
		return false;
	}

	default long getMaxThroughput() {
		return 1000L;
	}
}
