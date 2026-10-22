package somdudewillson.create_soul_harvest.content.item;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.grinding.MobGrindingDynamicData;
import somdudewillson.create_soul_harvest.util.AdvancementHelper;
import somdudewillson.create_soul_harvest.util.SimplePalettizedTooltipModifier;

//? neoforge {
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
//?} forge {
/*import net.minecraftforge.event.entity.player.ItemTooltipEvent;
*///?}

//? >= 1.21 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

import java.util.ArrayList;
import java.util.List;

public class LootTokenItem extends Item {
	protected static final Logger LOGGER = LogUtils.getLogger();

	public static final String TAG_LOOT_TABLE = SoulHarvestMod.id("loot_table").toString();
	public static final String TAG_LOCALIZATION_KEY = SoulHarvestMod.id("localization_key").toString();

	public LootTokenItem(Properties properties) {
		super(properties);
	}

	@Nullable
	public static ResourceLocation getLootTable(ItemStack stack) {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			CompoundTag tag = customData.copyTag();
			String str = tag.getString(TAG_LOOT_TABLE);
			if (!str.isEmpty()) {
				return ResourceLocation.tryParse(str);
			}
		}
		return null;
		//?} else {
		/*CompoundTag tag = stack.getTag();
		if (tag != null) {
			String str = tag.getString(TAG_LOOT_TABLE);
			if (!str.isEmpty()) {
				return ResourceLocation.tryParse(str);
			}
		}
		return null;
		*///?}
	}

	public static void setLootTable(ItemStack stack, @Nullable ResourceLocation lootTable) {
		//? >= 1.21 {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (lootTable == null) {
			tag.remove(TAG_LOOT_TABLE);
		} else {
			tag.putString(TAG_LOOT_TABLE, lootTable.toString());
		}
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		if (lootTable == null) {
			tag.remove(TAG_LOOT_TABLE);
		} else {
			tag.putString(TAG_LOOT_TABLE, lootTable.toString());
		}
		*///?}
	}

	@Nullable
	public static String getCustomLocalizationKey(ItemStack stack) {
		//? >= 1.21 {
		CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			String key = customData.copyTag().getString(TAG_LOCALIZATION_KEY);
			return key.isEmpty() ? null : key;
		}
		return null;
		//?} else {
		/*CompoundTag tag = stack.getTag();
		if (tag != null) {
			String key = tag.getString(TAG_LOCALIZATION_KEY);
			return key.isEmpty() ? null : key;
		}
		return null;
		*///?}
	}

	public static void setCustomLocalizationKey(ItemStack stack, @Nullable String key) {
		//? >= 1.21 {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (key == null || key.isEmpty()) {
			tag.remove(TAG_LOCALIZATION_KEY);
		} else {
			tag.putString(TAG_LOCALIZATION_KEY, key);
		}
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		//?} else {
		/*CompoundTag tag = stack.getOrCreateTag();
		if (key == null || key.isEmpty()) {
			tag.remove(TAG_LOCALIZATION_KEY);
		} else {
			tag.putString(TAG_LOCALIZATION_KEY, key);
		}
		*///?}
	}

	public static ItemStack createWithLootTable(Item item, ResourceLocation lootTable, @Nullable String customKey) {
		ItemStack stack = new ItemStack(item);
		setLootTable(stack, lootTable);
		if (customKey != null) {
			setCustomLocalizationKey(stack, customKey);
		}
		return stack;
	}

	public static Component getLootTableDisplayName(ItemStack stack) {
		String customKey = getCustomLocalizationKey(stack);
		if (customKey != null && !customKey.isEmpty()) {
			return Component.translatable(customKey);
		}
		ResourceLocation lootTableId = getLootTable(stack);
		if (lootTableId != null) {
			return Component.translatable("loot_table." + lootTableId.getNamespace() + "." + lootTableId.getPath().replace('/', '.'));
		}
		return Component.translatable("tooltip.create_soul_harvest.loot_token_empty");
	}

	public static LootTable getLootTableFromRegistry(MinecraftServer server, ResourceLocation lootTableId) {
		//? >= 1.21 {
		return server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableId));
		//?} else {
		/*return server.getLootData().getLootTable(lootTableId);
		*///?}
	}

	public static LootTable getLootTable(MinecraftServer server, ResourceLocation lootTableId) {
		LootTable lootTable = getLootTableFromRegistry(server, lootTableId);
		if (lootTable != null && lootTable != LootTable.EMPTY) {
			return lootTable;
		}
		return MobGrindingDynamicData.getDynamicLootTable(lootTableId, server.registryAccess());
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		ResourceLocation lootTableId = getLootTable(stack);
		if (lootTableId == null) {
			return InteractionResultHolder.pass(stack);
		}

		if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
			MinecraftServer server = serverLevel.getServer();
			LootTable lootTable = getLootTable(server, lootTableId);
			List<ItemStack> drops = new ArrayList<>();
			if (lootTable != null && lootTable != LootTable.EMPTY) {
				LootParams params = new LootParams.Builder(serverLevel)
						.withParameter(LootContextParams.THIS_ENTITY, player)
						.withParameter(LootContextParams.ENCHANTMENT_LEVEL, 0)
						.withParameter(LootContextParams.ORIGIN, player.position())
						.withLuck(player.getLuck())
						.create(LootContextParamSets.ENCHANTED_ENTITY);
				drops.addAll(lootTable.getRandomItems(params));
			} else {
				LOGGER.warn("Failed to load loot table "+lootTableId);
				return InteractionResultHolder.fail(stack);
			}

			for (ItemStack rewardStack : drops) {
				if (!player.addItem(rewardStack)) {
					player.drop(rewardStack, false);
				}
			}

			if (player instanceof ServerPlayer serverPlayer) {
				AdvancementHelper.grantAdvancement(serverPlayer, "used_loot_token", "use_loot_token");
			}

			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.2f);
		}

		if (!player.isCreative()) {
			stack.shrink(1);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	public static class LootTokenTooltipModifier extends SimplePalettizedTooltipModifier {
		protected final Item item;

		public LootTokenTooltipModifier(FontHelper.Palette palette, Item item) {
			super(palette);
			this.item = item;
		}

		@Override
		public void modify(ItemTooltipEvent context) {
			List<Component> tooltip = context.getToolTip();
			tooltip.addAll(getLootTokenInfo(context.getItemStack()));
		}

		public List<Component> getLootTokenInfo(ItemStack stack) {
			List<Component> tooltipComponents = new ArrayList<>();
			ResourceLocation lootTableId = getLootTable(stack);
			if (lootTableId != null) {
				Component displayName = getLootTableDisplayName(stack);
				Component tunedComponent = Component.translatable("tooltip.create_soul_harvest.loot_token_tuned", displayName);
				tooltipComponents.addAll(TooltipHelper.cutTextComponent(tunedComponent, palette));
			}
			return tooltipComponents;
		}
	}
}
