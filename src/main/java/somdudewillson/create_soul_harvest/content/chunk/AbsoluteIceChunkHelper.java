package somdudewillson.create_soul_harvest.content.chunk;

import com.google.common.collect.MapMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import somdudewillson.create_soul_harvest.content.block.AbsoluteIceBlockEntity;
//? >= 1.21 {
import somdudewillson.create_soul_harvest.registry.ModAttachments;
//?} else {
/*import somdudewillson.create_soul_harvest.registry.ModCapabilities;
*///?}

public class AbsoluteIceChunkHelper {

	private static final Map<Level, Map<BlockPos, Long>> ACTIVE_ICE_BLOCKS = new MapMaker().weakKeys().makeMap();

	@Nullable
	public static AbsoluteIceChunkData getChunkData(ChunkAccess chunk) {
		if (!(chunk instanceof LevelChunk levelChunk)) {
			return null;
		}
		//? >= 1.21 {
		return levelChunk.getData(ModAttachments.ABSOLUTE_ICE_CHUNK_DATA.get());
		//?} else {
		/*return levelChunk.getCapability(ModCapabilities.ABSOLUTE_ICE_CHUNK_DATA).orElse(null);
		*///?}
	}

	public static void registerIce(Level level, BlockPos pos, long placementTime) {
		ACTIVE_ICE_BLOCKS.computeIfAbsent(level, k -> new ConcurrentHashMap<>()).put(pos.immutable(), placementTime);

		ChunkAccess chunk = level.getChunkSource().getChunk(pos.getX() >> 4, pos.getZ() >> 4, false);
		if (chunk != null) {
			AbsoluteIceChunkData data = getChunkData(chunk);
			if (data != null) {
				data.add(pos, placementTime);
				if (!level.isClientSide) {
					chunk.setUnsaved(true);
				}
			}
		}
	}

	public static void unregisterIce(Level level, BlockPos pos) {
		Map<BlockPos, Long> map = ACTIVE_ICE_BLOCKS.get(level);
		if (map != null) {
			map.remove(pos);
		}

		ChunkAccess chunk = level.getChunkSource().getChunk(pos.getX() >> 4, pos.getZ() >> 4, false);
		if (chunk != null) {
			AbsoluteIceChunkData data = getChunkData(chunk);
			if (data != null) {
				data.remove(pos);
				if (!level.isClientSide) {
					chunk.setUnsaved(true);
				}
			}
		}
	}

	public static void onChunkLoaded(Level level, LevelChunk chunk) {
		AbsoluteIceChunkData data = getChunkData(chunk);
		if (data != null && !data.isEmpty()) {
			Map<BlockPos, Long> map = ACTIVE_ICE_BLOCKS.computeIfAbsent(level, k -> new ConcurrentHashMap<>());
			map.putAll(data.getAll());
		}
	}

	public static void onChunkUnloaded(Level level, LevelChunk chunk) {
		AbsoluteIceChunkData data = getChunkData(chunk);
		if (data != null && !data.isEmpty()) {
			Map<BlockPos, Long> map = ACTIVE_ICE_BLOCKS.get(level);
			if (map != null) {
				for (BlockPos pos : data.getAll().keySet()) {
					map.remove(pos);
				}
			}
		}
	}

	public static double getEffectiveRadius(long gameTime, long placementTime) {
		long age = Math.max(0, gameTime - placementTime);
		if (age < AbsoluteIceBlockEntity.GROWTH_DELAY_TICKS) {
			return 0.0;
		}
		if (age >= AbsoluteIceBlockEntity.GROWTH_DELAY_TICKS + AbsoluteIceBlockEntity.GROWTH_DURATION_TICKS) {
			return AbsoluteIceBlockEntity.MAX_RADIUS;
		}
		double progress = (double) (age - AbsoluteIceBlockEntity.GROWTH_DELAY_TICKS) / (double) AbsoluteIceBlockEntity.GROWTH_DURATION_TICKS;
		return AbsoluteIceBlockEntity.MAX_RADIUS * progress;
	}

	public static boolean isInfluencedByAbsoluteIce(Level level, BlockPos pos) {
		Map<BlockPos, Long> activeIce = ACTIVE_ICE_BLOCKS.get(level);
		if (activeIce == null || activeIce.isEmpty()) {
			return false;
		}

		long gameTime = level.getGameTime();
		double maxRadius = AbsoluteIceBlockEntity.MAX_RADIUS;

		for (Map.Entry<BlockPos, Long> entry : activeIce.entrySet()) {
			BlockPos icePos = entry.getKey();
			if (Math.abs(icePos.getX() - pos.getX()) > maxRadius || Math.abs(icePos.getZ() - pos.getZ()) > maxRadius) {
				continue;
			}

			double effectiveRadius = getEffectiveRadius(gameTime, entry.getValue());
			if (effectiveRadius <= 0.0) continue;

			if (icePos.distSqr(pos) <= effectiveRadius * effectiveRadius) {
				return true;
			}
		}
		return false;
	}
}
