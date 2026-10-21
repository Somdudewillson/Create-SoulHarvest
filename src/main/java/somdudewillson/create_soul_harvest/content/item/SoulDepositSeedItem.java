package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.block.SoulBudBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class SoulDepositSeedItem extends Item {

	public SoulDepositSeedItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos spawnerPos = context.getClickedPos();
		BlockState clickedState = level.getBlockState(spawnerPos);

		if (clickedState.is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS)) {
			Direction clickedFace = context.getClickedFace();
			BlockPos budPos = spawnerPos.relative(clickedFace);

			if (level.getBlockState(budPos).canBeReplaced()) {
				if (!level.isClientSide()) {
					BlockState newBud = ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
							.setValue(SoulBudBlock.FACING, clickedFace)
							.setValue(SoulBudBlock.ACTIVE, true);
					level.setBlockAndUpdate(budPos, newBud);
				}

				Player player = context.getPlayer();
				if (player != null && !player.isCreative()) {
					context.getItemInHand().shrink(1);
				}
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return InteractionResult.PASS;
	}
}
