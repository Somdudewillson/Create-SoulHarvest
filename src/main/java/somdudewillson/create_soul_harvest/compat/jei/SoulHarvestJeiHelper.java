package somdudewillson.create_soul_harvest.compat.jei;

import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class SoulHarvestJeiHelper {

	public static void refreshSoulBooks() {
		if (SoulHarvestMod.xplat().isModLoaded("jei")) {
			JeiBridge.refresh();
		}
	}

	private static class JeiBridge {
		private static void refresh() {
			try {
				CreateSoulHarvestJeiPlugin.refreshSoulBooksInJei();
			} catch (Throwable ignored) {
			}
		}
	}
}
