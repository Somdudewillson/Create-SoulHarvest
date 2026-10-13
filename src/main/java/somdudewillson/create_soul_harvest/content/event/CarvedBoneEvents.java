package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.entity.LivingEntity;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
*///?}

import somdudewillson.create_soul_harvest.content.block.CarvedBoneBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class CarvedBoneEvents {

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		ItemStack item = event.getItemStack();

		if (state.is(Blocks.BONE_BLOCK) && item.is(ItemTags.AXES)) {
			Player player = event.getEntity();
			Direction facing = player.getDirection().getOpposite();
			BlockState carved = ModBlocks.CARVED_BONE_BLOCK.get().defaultBlockState().setValue(CarvedBoneBlock.FACING, facing);
			level.setBlockAndUpdate(pos, carved);

			if (!player.isCreative()) {
				//? >= 1.21 {
				item.hurtAndBreak(1, player, LivingEntity.getSlotForHand(event.getHand()));
				//?} else {
				/*item.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
				*///?}
			}

			event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide()));
			event.setCanceled(true);
		}
	}
}
