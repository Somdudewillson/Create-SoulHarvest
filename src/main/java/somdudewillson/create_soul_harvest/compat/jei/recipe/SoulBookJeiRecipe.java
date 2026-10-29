package somdudewillson.create_soul_harvest.compat.jei.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SoulBookJeiRecipe(
		ResourceLocation entityId,
		Component displayName,
		int tier,
		int killsNeeded,
		int progressPerKill,
		List<ItemStack> inputBooks,
		ItemStack spawnEgg,
		List<ItemStack> outputBooks
) {}
