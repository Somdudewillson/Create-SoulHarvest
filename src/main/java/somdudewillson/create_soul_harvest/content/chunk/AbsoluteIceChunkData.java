package somdudewillson.create_soul_harvest.content.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//? >= 1.21 {
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.util.INBTSerializable;
//?} else {
/*import net.minecraftforge.common.util.INBTSerializable;
*///?}

public class AbsoluteIceChunkData implements INBTSerializable<CompoundTag> {

	private final Map<BlockPos, Long> iceBlocks = new ConcurrentHashMap<>();

	public void add(BlockPos pos, long placementTime) {
		iceBlocks.put(pos.immutable(), placementTime);
	}

	public void remove(BlockPos pos) {
		iceBlocks.remove(pos);
	}

	public Long getPlacementTime(BlockPos pos) {
		return iceBlocks.get(pos);
	}

	public Map<BlockPos, Long> getAll() {
		return Collections.unmodifiableMap(iceBlocks);
	}

	public boolean isEmpty() {
		return iceBlocks.isEmpty();
	}

	//? >= 1.21 {
	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (Map.Entry<BlockPos, Long> entry : iceBlocks.entrySet()) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.putLong("Pos", entry.getKey().asLong());
			entryTag.putLong("PlacementTime", entry.getValue());
			list.add(entryTag);
		}
		tag.put("IceBlocks", list);
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		iceBlocks.clear();
		ListTag list = tag.getList("IceBlocks", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entryTag = list.getCompound(i);
			iceBlocks.put(BlockPos.of(entryTag.getLong("Pos")), entryTag.getLong("PlacementTime"));
		}
	}
	//?} else {
	/*@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (Map.Entry<BlockPos, Long> entry : iceBlocks.entrySet()) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.putLong("Pos", entry.getKey().asLong());
			entryTag.putLong("PlacementTime", entry.getValue());
			list.add(entryTag);
		}
		tag.put("IceBlocks", list);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		iceBlocks.clear();
		ListTag list = tag.getList("IceBlocks", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entryTag = list.getCompound(i);
			iceBlocks.put(BlockPos.of(entryTag.getLong("Pos")), entryTag.getLong("PlacementTime"));
		}
	}
	*///?}
}
