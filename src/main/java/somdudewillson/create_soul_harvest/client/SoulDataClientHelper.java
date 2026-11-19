package somdudewillson.create_soul_harvest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.compat.jei.SoulHarvestJeiHelper;
import somdudewillson.create_soul_harvest.registry.ModCreativeTabs;

public class SoulDataClientHelper {

	public static void onSoulDataUpdated() {
		Minecraft mc = Minecraft.getInstance();
		if (mc != null) {
			mc.execute(() -> {
				if (mc.getConnection() != null && mc.level != null) {
					CreativeModeTabs.tryRebuildTabContents(
							mc.getConnection().enabledFeatures(),
							mc.options.operatorItemsTab().get(),
							mc.level.registryAccess()
					);
				}
				if (SoulHarvestMod.xplat().isModLoaded("jei")) {
					SoulHarvestJeiHelper.refreshSoulBooks();
				}
			});
		}
	}

	public static void refreshCreativeTabs() {
		try {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level != null && mc.getConnection() != null) {
				mc.execute(() -> {
					try {
						CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(
								mc.getConnection().enabledFeatures(),
								mc.options.operatorItemsTab().get(),
								mc.level.registryAccess()
						);
						if (ModCreativeTabs.MAIN_TAB != null && ModCreativeTabs.MAIN_TAB.get() != null) {
							ModCreativeTabs.MAIN_TAB.get().buildContents(params);
						}
						if (ModCreativeTabs.SOUL_ITEMS_TAB != null && ModCreativeTabs.SOUL_ITEMS_TAB.get() != null) {
							ModCreativeTabs.SOUL_ITEMS_TAB.get().buildContents(params);
						}
					} catch (Throwable ignored) {
					}
				});
			}
		} catch (Throwable ignored) {
		}
	}
}
