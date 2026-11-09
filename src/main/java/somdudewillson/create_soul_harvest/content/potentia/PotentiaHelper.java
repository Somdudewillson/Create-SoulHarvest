package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.registry.ModCapabilities;

public class PotentiaHelper {

	@Nullable
	public static IPotentiaHandler getHandler(Level level, BlockPos pos, @Nullable Direction side) {
		if (level == null || pos == null) return null;
		//? neoforge {
		return level.getCapability(ModCapabilities.POTENTIA_HANDLER, pos, side);
		//?} forge {
		/*BlockEntity be = level.getBlockEntity(pos);
		if (be != null) {
			return be.getCapability(ModCapabilities.POTENTIA_HANDLER, side).resolve().orElse(null);
		}
		return null;
		*///?}
	}

	@Nullable
	public static IPotentiaTransporter getTransporter(Level level, BlockPos pos, BlockState state, @Nullable Direction side) {
		if (level == null || pos == null || state == null) return null;
		if (state.getBlock() instanceof IPotentiaTransporter transporter) {
			return transporter;
		}
		//? neoforge {
		return level.getCapability(ModCapabilities.POTENTIA_TRANSPORTER, pos, state, null, side);
		//?} forge {
		/*BlockEntity be = level.getBlockEntity(pos);
		if (be != null) {
			return be.getCapability(ModCapabilities.POTENTIA_TRANSPORTER, side).resolve().orElse(null);
		}
		return null;
		*///?}
	}
}
