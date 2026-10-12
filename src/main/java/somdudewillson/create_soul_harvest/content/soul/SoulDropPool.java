package somdudewillson.create_soul_harvest.content.soul;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class SoulDropPool {
	private final String name;
	private final int rolls;
	private final List<SoulDropEntry> entries;

	public SoulDropPool(String name, int rolls, Collection<SoulDropEntry> entries) {
		this.name = name != null ? name : "main";
		this.rolls = Math.max(1, rolls);
		this.entries = new ArrayList<>(entries);
	}

	public SoulDropPool(String name, int rolls) {
		this(name, rolls, Collections.emptyList());
	}

	public List<SoulDropEntry> entries() {
		return Collections.unmodifiableList(entries);
	}

	public void addEntries(Collection<SoulDropEntry> newEntries) {
		this.entries.addAll(newEntries);
	}

	public void roll(RandomSource random, Consumer<ItemStack> output) {
		if (entries.isEmpty()) {
			return;
		}

		int totalWeight = 0;
		for (SoulDropEntry entry : entries) {
			totalWeight += entry.weight();
		}

		if (totalWeight <= 0) {
			return;
		}

		for (int r = 0; r < rolls; r++) {
			int pick = random.nextInt(totalWeight);
			int cumulative = 0;
			for (SoulDropEntry entry : entries) {
				cumulative += entry.weight();
				if (pick < cumulative) {
					ItemStack stack = entry.createStack(random);
					if (!stack.isEmpty()) {
						output.accept(stack);
					}
					break;
				}
			}
		}
	}

	public List<ResourceLocation> getItemIds() {
		List<ResourceLocation> list = new ArrayList<>();
		for (SoulDropEntry entry : entries) {
			if (!list.contains(entry.item())) {
				list.add(entry.item());
			}
		}
		return list;
	}
}
