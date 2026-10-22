package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModTags;

//? >= 1.21 {
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.tags.EnchantmentTags;
//?} else {
/*import net.minecraft.world.item.enchantment.Enchantments;
*///?}

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

	@Override
	public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropXP) {
		super.spawnAfterBreak(state, level, pos, tool, dropXP);
		
		if (!level.isClientSide()) {
			replant(level, pos, state, tool);
		}
	}
	
	public static void replant(LevelAccessor level, BlockPos pos, BlockState state, ItemStack tool) {
		tool = tool == null ? ItemStack.EMPTY : tool;
		
		Direction attachedFace = state.getValue(FACING).getOpposite();
		BlockPos supportingPos = pos.relative(attachedFace);
		BlockState supportingState = level.getBlockState(supportingPos);

		boolean hasSilkTouch;
		//? >= 1.21 {
		hasSilkTouch = EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING);
		//?} else {
		/*hasSilkTouch = tool != null && tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0;
		*///?}
		if (hasSilkTouch) return;

		if (supportingState.is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS)) {
			BlockState replantState = ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
					.setValue(FACING, state.getValue(FACING))
					.setValue(WATERLOGGED, state.getValue(WATERLOGGED))
					.setValue(ACTIVE, true);
			level.setBlock(pos, replantState, UPDATE_ALL);
		}
	}
}
