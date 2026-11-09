package somdudewillson.create_soul_harvest.client;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class ModPartialModels {

	public static final PartialModel POTENTIA_EXTRACTOR_HEAD = block("potentia_extractor/head");

	private static PartialModel block(String path) {
		return PartialModel.of(SoulHarvestMod.id("block/" + path));
	}

	public static void init() {
		// Initialize static fields
	}
}
