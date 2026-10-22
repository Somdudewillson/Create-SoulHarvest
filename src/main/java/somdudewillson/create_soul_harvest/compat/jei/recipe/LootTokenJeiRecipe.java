package somdudewillson.create_soul_harvest.compat.jei.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record LootTokenJeiRecipe(
		ItemStack tokenStack,
		ResourceLocation lootTableId,
		Component displayName,
		List<DropInfo> drops
) {
	public record DropInfo(
			ItemStack stack,
			int min,
			int max,
			int weight,
			float totalWeight
	) {
		public float getChance() {
			return totalWeight > 0 ? (weight / totalWeight) : 1.0f;
		}
	}
}
