package somdudewillson.create_soul_harvest.content.soul;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record SoulDropEntry(ResourceLocation item, int weight, int min, int max) {

	public SoulDropEntry {
		if (weight < 1) weight = 1;
		if (min < 1) min = 1;
		if (max < min) max = min;
	}

	public ItemStack createStack(RandomSource random) {
		Item registeredItem = BuiltInRegistries.ITEM.get(item);
		if (registeredItem == null || registeredItem == Items.AIR) {
			return ItemStack.EMPTY;
		}
		int count = min == max ? min : random.nextInt(max - min + 1) + min;
		return new ItemStack(registeredItem, count);
	}
}
