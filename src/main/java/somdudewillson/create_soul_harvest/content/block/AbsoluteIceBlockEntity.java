package somdudewillson.create_soul_harvest.content.block;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2i;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.math.IntMath;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.chunk.AbsoluteIceChunkData;
import somdudewillson.create_soul_harvest.content.chunk.AbsoluteIceChunkHelper;
import somdudewillson.create_soul_harvest.content.recipe.AbsoluteIceRecipe;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;
//? >= 1.21 {
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.world.item.crafting.RecipeHolder;
//?}

public class AbsoluteIceBlockEntity extends BlockEntity {

	public static final int DEFAULT_INTERVAL = 20 * 1;

	public static final long GROWTH_DURATION_TICKS = 20 * 300L;
	public static final long GROWTH_DELAY_TICKS = 20 * 60L;
	public static final double MAX_RADIUS = 50.0;
	
	private static LoadingCache<CylinderProps, BlockPos[]> VOLUME_CACHE = CacheBuilder.newBuilder()
			.maximumSize(10)
			.build(CacheLoader.from(AbsoluteIceBlockEntity::computeVolume));

	private int timer = DEFAULT_INTERVAL;
	private int interval = DEFAULT_INTERVAL;
	private long placementTime = 0;
	private long freezeStep = 0;
	private double lastRenderedRadius = -1;

	public AbsoluteIceBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ABSOLUTE_ICE_BE.get(), pos, state);
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getPlacementTime() {
		return placementTime;
	}

	public void setPlacementTime(long placementTime) {
		this.placementTime = placementTime;
		setChanged();
	}
	
	public long getAge(Level level) {
		return getAge(level.getGameTime());
	}
	
	public long getAge(long currentTime) {
		return currentTime - getPlacementTime();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (level != null) {
			if (placementTime == 0 && !level.isClientSide) {
				// Initialize placement time if not already stored
				AbsoluteIceChunkData data = AbsoluteIceChunkHelper.getChunkData(level.getChunk(worldPosition));
				if (data != null && data.getPlacementTime(worldPosition) != null) {
					placementTime = data.getPlacementTime(worldPosition);
				} else {
					placementTime = level.getGameTime();
				}
			}
			AbsoluteIceChunkHelper.registerIce(level, worldPosition, placementTime);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (level != null) {
			AbsoluteIceChunkHelper.unregisterIce(level, worldPosition);
			if (level.isClientSide) {
				notifyClientVisuals(MAX_RADIUS);
			}
		}
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, AbsoluteIceBlockEntity be) {
		if (be.placementTime <= 0) return;

		if (level.getGameTime() % 20 == 0) {
			double currentRadius = AbsoluteIceChunkHelper.getEffectiveRadius(level.getGameTime(), be.placementTime);
			if (be.lastRenderedRadius < 0 || Math.abs(currentRadius - be.lastRenderedRadius) >= 2.0
					|| (currentRadius >= MAX_RADIUS && be.lastRenderedRadius < MAX_RADIUS)) {
				be.lastRenderedRadius = currentRadius;
				be.notifyClientVisuals(currentRadius);
			}
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AbsoluteIceBlockEntity be) {
		be.timer--;
		if (be.timer <= 0) {
			be.timer = be.interval;

			if (be.placementTime == 0) {
				be.placementTime = level.getGameTime();
				be.setChanged();
				level.sendBlockUpdated(pos, state, state, net.minecraft.world.level.block.Block.UPDATE_ALL);
			}

			// Ensure chunk data is in sync
			AbsoluteIceChunkHelper.registerIce(level, pos, be.placementTime);

			// Execute environmental freeze & spire events
			be.performFreezeEvents(level, pos, state);
		}
	}

	private void notifyClientVisuals(double radius) {
		if (level != null && level.isClientSide) {
			ClientHandler.updateVisuals(worldPosition, radius);
		}
	}

	private static class ClientHandler {
		private static void updateVisuals(BlockPos pos, double radius) {
			somdudewillson.create_soul_harvest.client.AbsoluteIceClientHelper.updateBiomeVisuals(pos, radius);
		}
	}
	
	protected static final LoadingCache<Integer, Integer> COPRIME_CACHE = CacheBuilder
			.newBuilder()
			.maximumSize(256)
			.build(CacheLoader.from(AbsoluteIceBlockEntity::computeCoprimeMultiplier));
	private static int remapIndex(int arraySize, int index) {
		int mult = COPRIME_CACHE.getUnchecked(arraySize);
		long result = ((long)index * mult + 251) % arraySize;
		return (int) result;
	}
	private static int computeCoprimeMultiplier(int n) {
	    int candidate = (int)(n/1.618);
	    while (IntMath.gcd(candidate, n) != 1) {
	        candidate += 1;
	    }
	    return candidate;
	}

	private void performFreezeEvents(Level level, BlockPos pos, BlockState state) {
		freezeStep++;
		
		// Snow & freezing environmental aura
		final double maxDist = MAX_RADIUS;
		final int maxDistY = 15;
		final int maxPerTick = 64;
		BlockPos[] volumePositions = VOLUME_CACHE.getUnchecked(new CylinderProps((int)Math.floor(maxDist), maxDistY));
		freezeEnvironment(level, pos, state, maxDist, volumePositions, maxPerTick);

		if ((freezeStep & 63) == 0) return;
		// Blue ice spire generation around the block
		final int spirePerTick = 1;
		final int spireHeight = maxDistY;
		buildSpire(level, pos, spirePerTick, spireHeight);
	}

	private List<AbsoluteIceRecipe> getConversionRecipes(Level level) {
		//? >= 1.21 {
		net.minecraft.world.item.crafting.RecipeType<AbsoluteIceRecipe> type = ModRecipeTypes.ABSOLUTE_ICE_CONVERSION.getType();
		return level.getRecipeManager().getAllRecipesFor(type)
				.stream().map(RecipeHolder::value).toList();
		//?} else {
		/*net.minecraft.world.item.crafting.RecipeType<AbsoluteIceRecipe> type = ModRecipeTypes.ABSOLUTE_ICE_CONVERSION.getType();
		return level.getRecipeManager().getAllRecipesFor(type);
		*///?}
	}

	protected void freezeEnvironment(Level level, BlockPos pos, BlockState state, 
			double maxDist, BlockPos[] volumePositions, int maxPerTick) {
		int step = volumePositions.length/maxPerTick;
		int offset = (int) ((freezeStep) % maxPerTick);
		List<AbsoluteIceRecipe> recipes = getConversionRecipes(level);
		
		for (int i = 0; i < maxPerTick && (i * step + offset) < volumePositions.length; i++) {
			int idx = i * step + offset;
			idx = remapIndex(volumePositions.length, idx);
			BlockPos testPos = pos.offset(volumePositions[idx]);
			if (!level.isLoaded(testPos)) continue;

			double distSqr = pos.distSqr(testPos);
			double effectStrength = 1.0 - (Math.sqrt(distSqr) / maxDist);

			BlockState testState = level.getBlockState(testPos);
			BlockState aboveState = level.getBlockState(testPos.above());
			boolean seesSky = level.canSeeSky(pos.above());
			int blockLight = level.getBrightness(LightLayer.BLOCK, pos.above());

			boolean converted = false;
			for (AbsoluteIceRecipe recipe : recipes) {
				if (recipe.matches(testPos, testState, aboveState, seesSky, blockLight, effectStrength)) {
					recipe.apply(level, testPos);
					converted = true;
					break;
				}
			}
			if (converted) continue;

			if (!testState.is(BlockTags.ICE) && !testState.is(BlockTags.SNOW) && !testState.is(state.getBlock())
					&& !aboveState.is(Blocks.SNOW) && aboveState.getFluidState().isEmpty()
					&& aboveState.canBeReplaced() && testState.isFaceSturdy(level, testPos, Direction.UP, SupportType.FULL)) {
				level.setBlockAndUpdate(testPos.above(), Blocks.SNOW.defaultBlockState());
			}
		}
	}

	protected void buildSpire(Level level, BlockPos pos, 
			int spirePerTick, int spireHeight) {
		int spireCount = 0;
		
		for (BlockPos spirePos : BlockPos.withinManhattan(pos, 2, spireHeight, 2)) {
			if (spirePos.equals(pos)) continue;
			int horizontalDist = Math.abs(spirePos.getX() - pos.getX()) + Math.abs(spirePos.getZ() - pos.getZ());
			int maxYDist = spireHeight - (int) Math.round(horizontalDist * (spireHeight / 3.2));
			if (Math.abs(spirePos.getY() - pos.getY()) > maxYDist) continue;

			BlockState spireState = level.getBlockState(spirePos);
			if (spireState.is(Blocks.BLUE_ICE)) continue;
			if (!spireState.canBeReplaced()) continue;

			level.setBlockAndUpdate(spirePos, Blocks.BLUE_ICE.defaultBlockState());
			spireCount++;
			if (spireCount >= spirePerTick) break;
		}
		
	}
	
	protected static record CylinderProps(int radius, int halfHeight) {}
	
	protected static BlockPos[] computeVolume(CylinderProps cylinderProps) {
		final int radius = cylinderProps.radius();
		final int halfHeight = cylinderProps.halfHeight();
		final int radiusSq = radius*radius;
		
		List<BlockPos> slicePositions = new ArrayList<>(radiusSq);
		for (int x=-radius; x<=radius; x++) {
			for (int z=-radius; z<=radius; z++) {
				if (Vector2i.distanceSquared(x, z, 0, 0) > radiusSq) continue;
				slicePositions.add(new BlockPos(x, 0, z));
			}
		}
		
		BlockPos[] volume = new BlockPos[slicePositions.size() * (1 + halfHeight + halfHeight)];
		int idx = 0;
		for (int y=-halfHeight; y<=halfHeight; y++) {
			for (BlockPos slicePos : slicePositions) {
				volume[idx++] = slicePos.atY(y);
			}
		}
		return volume;
	}

	//? >= 1.21 {
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("Timer", timer);
		tag.putInt("Interval", interval);
		tag.putLong("PlacementTime", placementTime);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		timer = tag.contains("Timer") ? tag.getInt("Timer") : DEFAULT_INTERVAL;
		interval = tag.contains("Interval") ? tag.getInt("Interval") : DEFAULT_INTERVAL;
		placementTime = tag.getLong("PlacementTime");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putInt("Timer", timer);
		tag.putInt("Interval", interval);
		tag.putLong("PlacementTime", placementTime);
		return tag;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
		super.onDataPacket(net, pkt, lookupProvider);
		CompoundTag tag = pkt.getTag();
		if (tag != null) {
			timer = tag.contains("Timer") ? tag.getInt("Timer") : DEFAULT_INTERVAL;
			interval = tag.contains("Interval") ? tag.getInt("Interval") : DEFAULT_INTERVAL;
			placementTime = tag.getLong("PlacementTime");
			if (level != null) {
				AbsoluteIceChunkHelper.registerIce(level, worldPosition, placementTime);
				double currentRadius = AbsoluteIceChunkHelper.getEffectiveRadius(level.getGameTime(), placementTime);
				lastRenderedRadius = currentRadius;
				notifyClientVisuals(Math.max(currentRadius, 1.0));
			}
		}
	}
	//?} else {
	/*@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("Timer", timer);
		tag.putInt("Interval", interval);
		tag.putLong("PlacementTime", placementTime);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		timer = tag.contains("Timer") ? tag.getInt("Timer") : DEFAULT_INTERVAL;
		interval = tag.contains("Interval") ? tag.getInt("Interval") : DEFAULT_INTERVAL;
		placementTime = tag.getLong("PlacementTime");
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putInt("Timer", timer);
		tag.putInt("Interval", interval);
		tag.putLong("PlacementTime", placementTime);
		return tag;
	}

	@Override
	public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag = pkt.getTag();
		if (tag != null) {
			timer = tag.contains("Timer") ? tag.getInt("Timer") : DEFAULT_INTERVAL;
			interval = tag.contains("Interval") ? tag.getInt("Interval") : DEFAULT_INTERVAL;
			placementTime = tag.getLong("PlacementTime");
			if (level != null) {
				AbsoluteIceChunkHelper.registerIce(level, worldPosition, placementTime);
				double currentRadius = AbsoluteIceChunkHelper.getEffectiveRadius(level.getGameTime(), placementTime);
				lastRenderedRadius = currentRadius;
				notifyClientVisuals(Math.max(currentRadius, 1.0));
			}
		}
	}
	*///?}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
