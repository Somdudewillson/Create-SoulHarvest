package somdudewillson.create_soul_harvest.content.chunk;

import com.google.common.collect.MapMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.registry.ModBiomes;

import java.util.Map;

public class AbsoluteIceBiomeHelper {

	private static final Map<Level, Holder<Biome>> BIOME_CACHE = new MapMaker().weakKeys().makeMap();

	@Nullable
	public static Holder<Biome> getOverriddenBiome(Level level, BlockPos pos) {
		if (!AbsoluteIceChunkHelper.isInfluencedByAbsoluteIce(level, pos)) {
			return null;
		}

		return BIOME_CACHE.computeIfAbsent(level, l ->
				l.registryAccess().registry(Registries.BIOME)
						.flatMap(reg -> reg.getHolder(ModBiomes.ABSOLUTE_ICE))
						.orElse(null)
		);
	}
}
