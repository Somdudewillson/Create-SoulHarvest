package somdudewillson.create_soul_harvest.content.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.item.LootTokenItem;

public class ItemStackLootTokenGlintHolder implements ISoulGlintHolder {

	private final ItemStack stack;

	public ItemStackLootTokenGlintHolder(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public boolean hasSoulGlint() {
		return getSoulGlintEntityId() != null;
	}

	@Override
	@Nullable
	public ResourceLocation getSoulGlintEntityId() {
		return LootTokenItem.getSoulGlintEntityId(stack);
	}

	@Override
	public float getSoulGlintIntensity() {
		return 1.0f;
	}
}
