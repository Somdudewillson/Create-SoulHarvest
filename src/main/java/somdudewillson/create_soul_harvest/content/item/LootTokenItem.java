package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

//? >= 1.21 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

import java.util.List;

public class LootTokenItem extends Item {

	public LootTokenItem(Properties properties) {
		super(properties);
	}

	public static String getLootId(ItemStack stack) {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			return customData.copyTag().getString("soul_harvest:loot_id");
		}
		return "";
		//?} else {
		/*CompoundTag tag = stack.getTag();
		return tag != null ? tag.getString("soul_harvest:loot_id") : "";
		*///?}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		*///?}

		ListTag lootList = tag.getList("soul_harvest:loot_items", Tag.TAG_STRING);
		if (lootList.isEmpty()) {
			return InteractionResultHolder.pass(stack);
		}

		if (!level.isClientSide()) {
			int idx = level.random.nextInt(lootList.size());
			String itemId = lootList.getString(idx);
			Item rewardItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemId));
			if (rewardItem != null) {
				ItemStack rewardStack = new ItemStack(rewardItem);
				if (!player.addItem(rewardStack)) {
					player.drop(rewardStack, false);
				}
			}

			if (player instanceof ServerPlayer serverPlayer) {
				//? >= 1.21 {
				var adv = serverPlayer.server.getAdvancements().get(SoulHarvestMod.id("used_loot_token"));
				if (adv != null) {
					serverPlayer.getAdvancements().award(adv, "use_loot_token");
				}
				//?} else {
				/*var adv = serverPlayer.server.getAdvancements().getAdvancement(SoulHarvestMod.id("used_loot_token"));
				if (adv != null) {
					serverPlayer.getAdvancements().award(adv, "use_loot_token");
				}
				*///?}
			}
		}

		if (!player.isCreative()) {
			stack.shrink(1);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		addTokenTooltip(stack, tooltipComponents);
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		addTokenTooltip(stack, tooltipComponents);
	}
	*///?}

	private void addTokenTooltip(ItemStack stack, List<Component> tooltipComponents) {
		String lootId = getLootId(stack);
		if (!lootId.isEmpty()) {
			String[] words = lootId.split("_");
			StringBuilder sb = new StringBuilder();
			for (String w : words) {
				if (!w.isEmpty()) {
					if (sb.length() > 0) sb.append(" ");
					sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
				}
			}
			tooltipComponents.add(Component.literal(sb.toString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
					.append(Component.literal(" Tuned").withStyle(ChatFormatting.GRAY)));
		}
	}
}
