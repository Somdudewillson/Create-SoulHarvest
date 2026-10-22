package somdudewillson.create_soul_harvest.mixin;

import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import somdudewillson.create_soul_harvest.content.block.SoulClusterBlock;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

@Mixin(value = BlockHelper.class, remap = false)
public abstract class BlockHelperMixin {
	// I kinda hate this but BlockHelper 'breaks' blocks by setting them to air and doesn't call any method after that so...

	@Unique
	private static final ThreadLocal<Deque<BlockState>> create_soul_harvest$PRE_BREAK_STATES =
			ThreadLocal.withInitial(ArrayDeque::new);

	@Inject(method = "destroyBlockAs", at = @At("HEAD"))
	private static void create_soul_harvest$beforeDestroyBlockAs(
			Level level,
			BlockPos pos,
			Player player,
			ItemStack usedTool,
			float effectChance,
			Consumer<ItemStack> droppedItemCallback,
			CallbackInfo ci
	) {
		if (!level.isClientSide()) {
			create_soul_harvest$PRE_BREAK_STATES.get().push(level.getBlockState(pos));
		}
	}

	@Inject(method = "destroyBlockAs", at = @At("TAIL"))
	private static void create_soul_harvest$afterDestroyBlockAs(
			Level level,
			BlockPos pos,
			Player player,
			ItemStack usedTool,
			float effectChance,
			Consumer<ItemStack> droppedItemCallback,
			CallbackInfo ci
	) {
		if (!level.isClientSide()) {
			Deque<BlockState> stack = create_soul_harvest$PRE_BREAK_STATES.get();
			if (!stack.isEmpty()) {
				BlockState brokenState = stack.pop();
				if (brokenState.getBlock() instanceof SoulClusterBlock && !level.getBlockState(pos).is(brokenState.getBlock())) {
					SoulClusterBlock.replant(level, pos, brokenState, usedTool);
				}
			}
		}
	}
}
