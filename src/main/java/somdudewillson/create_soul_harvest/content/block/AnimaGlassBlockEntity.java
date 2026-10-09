package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

import java.util.UUID;

public class AnimaGlassBlockEntity extends BlockEntity {

	private UUID ownerId = null;

	public AnimaGlassBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MAGIC_GLASS_BE.get(), pos, blockState);
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
		setChanged();
	}

	//? >= 1.21 {
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (ownerId != null) {
			tag.putUUID("ownerId", ownerId);
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.hasUUID("ownerId")) {
			ownerId = tag.getUUID("ownerId");
		}
	}
	//?} else {
	/*@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (ownerId != null) {
			tag.putUUID("ownerId", ownerId);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.hasUUID("ownerId")) {
			ownerId = tag.getUUID("ownerId");
		}
	}
	*///?}
}
