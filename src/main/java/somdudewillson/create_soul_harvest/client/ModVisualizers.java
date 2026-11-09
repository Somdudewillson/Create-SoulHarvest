package somdudewillson.create_soul_harvest.client;

import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

public class ModVisualizers {

	public static void init() {
		ModPartialModels.init();
		SimpleBlockEntityVisualizer.builder(ModBlockEntities.POTENTIA_EXTRACTOR_BE.get())
				.factory(OrientedRotatingVisual.of(ModPartialModels.POTENTIA_EXTRACTOR_HEAD))
				.skipVanillaRender(be -> true)
				.apply();
	}
}
