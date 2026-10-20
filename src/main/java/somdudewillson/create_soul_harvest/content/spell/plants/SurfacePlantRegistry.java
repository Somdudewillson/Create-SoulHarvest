package somdudewillson.create_soul_harvest.content.spell.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SurfacePlantRegistry {

	public record WeightedPlant(SurfacePlant plant, int weight) {}

	private final List<WeightedPlant> entries = new ArrayList<>();
	private static SurfacePlantRegistry DEFAULT_INSTANCE;

	public static SurfacePlantRegistry getDefault() {
		if (DEFAULT_INSTANCE == null) {
			DEFAULT_INSTANCE = new SurfacePlantRegistry();
			DEFAULT_INSTANCE.register(new SugarcaneSurfacePlant(), 100);
			DEFAULT_INSTANCE.register(new CactusSurfacePlant(), 75);
			DEFAULT_INSTANCE.register(new BiomeTreeSurfacePlant(), 80);
			DEFAULT_INSTANCE.register(new RandomTreeSurfacePlant(), 15);
		}
		return DEFAULT_INSTANCE;
	}

	public void register(SurfacePlant plant, int weight) {
		if (weight > 0) {
			entries.add(new WeightedPlant(plant, weight));
		}
	}

	public Optional<SurfacePlant> selectPlant(ServerLevel level, BlockPos groundPos, BlockState groundState, RandomSource random) {
		List<WeightedPlant> applicable = new ArrayList<>();
		int totalWeight = 0;

		for (WeightedPlant entry : entries) {
			if (entry.plant().isApplicable(level, groundPos, groundState)) {
				applicable.add(entry);
				totalWeight += entry.weight();
			}
		}

		if (applicable.isEmpty() || totalWeight <= 0) {
			return Optional.empty();
		}

		int roll = random.nextInt(totalWeight);
		int running = 0;
		for (WeightedPlant entry : applicable) {
			running += entry.weight();
			if (roll < running) {
				return Optional.of(entry.plant());
			}
		}

		return Optional.of(applicable.get(applicable.size() - 1).plant());
	}

	public List<WeightedPlant> getEntries() {
		return entries;
	}
}
