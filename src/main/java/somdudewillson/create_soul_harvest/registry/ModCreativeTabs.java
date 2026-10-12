package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class ModCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SoulHarvestMod.MOD_ID);

	private static final java.util.List<DeferredHolder<Item, ? extends Item>> MAIN_TAB_ITEMS = new java.util.ArrayList<>();

	public static void addToMainTab(DeferredHolder<Item, ? extends Item> item) {
		MAIN_TAB_ITEMS.add(item);
	}

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.create_soul_harvest"))
					.icon(() -> new ItemStack(ModItems.SOUL_SHARD.get()))
					.displayItems((parameters, output) -> {
						MAIN_TAB_ITEMS.forEach(holder -> output.accept(holder.get()));
					})
					.build());
}
