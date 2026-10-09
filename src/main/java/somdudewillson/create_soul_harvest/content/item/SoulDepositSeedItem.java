package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.block.SoulBudBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

import java.util.List;

public class SoulDepositSeedItem extends Item {

	public SoulDepositSeedItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos spawnerPos = context.getClickedPos();
		BlockState clickedState = level.getBlockState(spawnerPos);

		if (clickedState.is(Blocks.SPAWNER)) {
			Direction clickedFace = context.getClickedFace();
			BlockPos budPos = spawnerPos.relative(clickedFace);

			if (level.getBlockState(budPos).canBeReplaced()) {
				if (!level.isClientSide()) {
					BlockState newBud = ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
							.setValue(SoulBudBlock.FACING, clickedFace);
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

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		tooltipComponents.add(Component.literal("Plant on a spawner").withStyle(ChatFormatting.GRAY));
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		tooltipComponents.add(Component.literal("Plant on a spawner").withStyle(ChatFormatting.GRAY));
	}
	*///?}
}
