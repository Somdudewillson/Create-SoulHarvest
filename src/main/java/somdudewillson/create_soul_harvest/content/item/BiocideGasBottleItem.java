package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.block.BiocideGasBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class BiocideGasBottleItem extends Item {

	public BiocideGasBottleItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos targetPos = context.getClickedPos().relative(context.getClickedFace());

		if (level.getBlockState(targetPos).isAir()) {
			if (!level.isClientSide()) {
				BlockState gasState = ModBlocks.BIOCIDE_GAS.get().defaultBlockState()
						.setValue(BiocideGasBlock.AGE, 15);
				level.setBlockAndUpdate(targetPos, gasState);
			}

			Player player = context.getPlayer();
			if (player != null && !player.isCreative()) {
				context.getItemInHand().shrink(1);
				ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
				if (!player.addItem(emptyBottle)) {
					player.drop(emptyBottle, false);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return InteractionResult.PASS;
	}
}
