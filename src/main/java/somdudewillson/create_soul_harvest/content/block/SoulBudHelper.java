package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.capability.ISoulBudGrowthHandler;
import somdudewillson.create_soul_harvest.registry.ModCapabilities;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class SoulBudHelper {

	@Nullable
	public static ISoulBudGrowthHandler getGrowthHandler(BlockGetter level, BlockPos pos, @Nullable Direction side) {
		if (level == null || pos == null) return null;
		//? neoforge {
		if (level instanceof Level l) {
			return l.getCapability(ModCapabilities.SOUL_BUD_GROWTH_HANDLER, pos, side);
		}
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof somdudewillson.create_soul_harvest.content.potentia.catalyst.PotentiaGrowthCatalystBlockEntity catalyst) {
			return catalyst.getGrowthHandler(side);
		}
		return null;
		//?} forge {
		/*BlockEntity be = level.getBlockEntity(pos);
		if (be != null) {
			return be.getCapability(ModCapabilities.SOUL_BUD_GROWTH_HANDLER, side).resolve().orElse(null);
		}
		return null;
		*///?}
	}

	public static boolean isValidGrowthBlock(BlockGetter level, BlockPos pos, @Nullable Direction side) {
		if (level == null || pos == null) return false;
		ISoulBudGrowthHandler handler = getGrowthHandler(level, pos, side);
		if (handler != null) {
			return handler.canGrowBuds(side);
		}
		return level.getBlockState(pos).is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS);
	}

	public static boolean isGrowthCapabilityPresent(BlockGetter level, BlockPos pos, @Nullable Direction side) {
		return getGrowthHandler(level, pos, side) != null;
	}

	public static boolean onBudGrow(BlockGetter level, BlockPos pos, @Nullable Direction side) {
		if (level == null || pos == null) return false;
		ISoulBudGrowthHandler handler = getGrowthHandler(level, pos, side);
		if (handler != null) {
			return handler.onBudGrow(side);
		}
		return level.getBlockState(pos).is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS);
	}
}
