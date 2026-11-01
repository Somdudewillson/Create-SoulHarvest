package somdudewillson.create_soul_harvest.content.item;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.block.BiocideGasBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;

//? >= 1.21 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

public class BiocideGasBottleItem extends Item {

	public static final int MAX_BIOCIDE = BiocideGasBlock.MAX_DENSITY;
	public static final String TAG_BIOCIDE = "biocide";

	public BiocideGasBottleItem(Properties properties) {
		super(properties);
	}

	public static int getBiocideAmount(ItemStack stack) {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null && customData.copyTag().contains(TAG_BIOCIDE)) {
			return customData.copyTag().getInt(TAG_BIOCIDE);
		}
		return MAX_BIOCIDE;
		//?} else {
		/*CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains(TAG_BIOCIDE)) {
			return tag.getInt(TAG_BIOCIDE);
		}
		return MAX_BIOCIDE;
		*///?}
	}

	public static void setBiocideAmount(ItemStack stack, int amount) {
		int clamped = Math.max(0, Math.min(amount, MAX_BIOCIDE));
		//? >= 1.21 {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.putInt(TAG_BIOCIDE, clamped);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		tag.putInt(TAG_BIOCIDE, clamped);
		*///?}
	}

	public static boolean isFull(ItemStack stack) {
		return getBiocideAmount(stack) >= MAX_BIOCIDE;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		int amount = getBiocideAmount(stack);
		return amount > 0 && amount < MAX_BIOCIDE;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0F * getBiocideAmount(stack) / MAX_BIOCIDE);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0x55FF55;
	}

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		int amount = getBiocideAmount(stack);
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.biocide_bottle_amount", amount, MAX_BIOCIDE).withStyle(net.minecraft.ChatFormatting.GRAY));
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		int amount = getBiocideAmount(stack);
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.biocide_bottle_amount", amount, MAX_BIOCIDE).withStyle(net.minecraft.ChatFormatting.GRAY));
	}
	*///?}

	/**
	 * Called when a player uses an empty bottle or biocide gas bottle on a BiocideGasBlock.
	 * Siphons biocide gas breadth-first up to MAX_BIOCIDE capacity.
	 */
	public static boolean onInteractWithGasBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
		boolean isGlassBottle = stack.is(Items.GLASS_BOTTLE);
		boolean isBiocideBottle = stack.is(ModItems.BIOCIDE_GAS_BOTTLE.get());
		if (!isGlassBottle && !isBiocideBottle) {
			return false;
		}

		int currentGas = isBiocideBottle ? getBiocideAmount(stack) : 0;
		int needed = MAX_BIOCIDE - currentGas;
		if (needed <= 0) {
			return false;
		}

		if (level.isClientSide()) {
			return true;
		}

		Queue<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(pos);
		visited.add(pos);

		int collected = 0;
		while (!queue.isEmpty() && collected < needed) {
			BlockPos curPos = queue.poll();
			BlockState curState = level.getBlockState(curPos);

			if (curState.is(ModBlocks.BIOCIDE_GAS.get())) {
				int blockDensity = curState.getValue(BiocideGasBlock.DENSITY);
				int toTake = Math.min(blockDensity, needed - collected);
				collected += toTake;
				int newDensity = blockDensity - toTake;

				if (newDensity < BiocideGasBlock.MIN_DENSITY) {
					level.setBlockAndUpdate(curPos, Blocks.AIR.defaultBlockState());
				} else {
					level.setBlockAndUpdate(curPos, curState.setValue(BiocideGasBlock.DENSITY, newDensity).setValue(BiocideGasBlock.SETTLED, false));
					level.scheduleTick(curPos, curState.getBlock(), 10);
				}
			}

			if (collected < needed) {
				for (Direction dir : Direction.values()) {
					BlockPos nextPos = curPos.relative(dir);
					if (visited.add(nextPos)) {
						if (level.getBlockState(nextPos).is(ModBlocks.BIOCIDE_GAS.get())) {
							queue.add(nextPos);
						}
					}
				}
			}
		}

		if (collected > 0) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);

			int totalAmount = currentGas + collected;
			if (player.isCreative() && isBiocideBottle && stack.getCount() == 1) {
				setBiocideAmount(stack, totalAmount);
			} else {
				ItemStack filledBottle = new ItemStack(ModItems.BIOCIDE_GAS_BOTTLE.get());
				setBiocideAmount(filledBottle, totalAmount);
				ItemStack result = ItemUtils.createFilledResult(stack, player, filledBottle);
				player.setItemInHand(hand, result);
			}
			return true;
		}

		return false;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		BlockState clickedState = level.getBlockState(clickedPos);
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();

		if (clickedState.is(ModBlocks.BIOCIDE_GAS.get())) {
			if (player != null && onInteractWithGasBlock(stack, clickedState, level, clickedPos, player, context.getHand())) {
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
			return InteractionResult.PASS;
		}

		BlockPos targetPos = clickedPos.relative(context.getClickedFace());
		if (level.getBlockState(targetPos).isAir()) {
			int amount = getBiocideAmount(stack);
			if (amount >= BiocideGasBlock.MIN_DENSITY) {
				int toPlace = Math.min(amount, BiocideGasBlock.MAX_DENSITY);
				if (!level.isClientSide()) {
					BlockState gasState = ModBlocks.BIOCIDE_GAS.get().defaultBlockState()
							.setValue(BiocideGasBlock.DENSITY, toPlace);
					level.setBlockAndUpdate(targetPos, gasState);
				}

				if (player != null && !player.isCreative()) {
					int remaining = amount - toPlace;
					if (remaining <= 0) {
						ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
						ItemStack result = ItemUtils.createFilledResult(stack, player, emptyBottle);
						player.setItemInHand(context.getHand(), result);
					} else {
						if (stack.getCount() == 1) {
							setBiocideAmount(stack, remaining);
						} else {
							stack.shrink(1);
							ItemStack partialBottle = new ItemStack(ModItems.BIOCIDE_GAS_BOTTLE.get());
							setBiocideAmount(partialBottle, remaining);
							if (!player.addItem(partialBottle)) {
								player.drop(partialBottle, false);
							}
						}
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return InteractionResult.PASS;
	}
}
