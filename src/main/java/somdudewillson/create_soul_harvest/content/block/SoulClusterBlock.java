package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModTags;

//? >= 1.21 {
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.tags.EnchantmentTags;
//?} else {
/*import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
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
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.playerDestroy(level, player, pos, state, blockEntity, tool);

		if (!level.isClientSide() && !player.isCreative()) {
			boolean hasSilkTouch;
			//? >= 1.21 {
			hasSilkTouch = EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING);
			//?} else {
			/*hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
			*///?}

			if (!hasSilkTouch) {
				Direction attachedFace = state.getValue(FACING).getOpposite();
				BlockPos supportingPos = pos.relative(attachedFace);
				BlockState supportingState = level.getBlockState(supportingPos);

				if (supportingState.is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS)) {
					BlockState replantState = ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
							.setValue(FACING, state.getValue(FACING))
							.setValue(WATERLOGGED, state.getValue(WATERLOGGED));
					level.setBlockAndUpdate(pos, replantState);
				}
			}
		}
	}
}
