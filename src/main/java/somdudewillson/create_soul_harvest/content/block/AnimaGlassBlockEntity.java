package somdudewillson.create_soul_harvest.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.redsparklib.common.utils.OptNbtUtils;

import java.util.UUID;

import lombok.Getter;
//? >= 1.21 {
import net.minecraft.core.HolderLookup;
//?}

@Getter
public class AnimaGlassBlockEntity extends BlockEntity {

	private UUID ownerId = null;
	private int maxLifespan = 20 * 16;
	private int currentLifespan = 0;

	public AnimaGlassBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MAGIC_GLASS_BE.get(), pos, blockState);
	}

	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
		setChanged();
	}

	public void setMaxLifespan(int maxLifespan) {
		this.maxLifespan = maxLifespan;
		setChanged();
	}

	public void setCurrentLifespan(int currentLifespan) {
		this.currentLifespan = currentLifespan;
		setChanged();
	}

	//? >= 1.21 {
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		
		OptNbtUtils.putUUID(tag, "ownerId", ownerId);
		tag.putInt("maxLifespan", maxLifespan);
		tag.putInt("currentLifespan", currentLifespan);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		
		ownerId = OptNbtUtils.getUUID(tag, "ownerId").orElse(null);
		maxLifespan = OptNbtUtils.getInt(tag, "maxLifespan").orElse(0);
		currentLifespan = OptNbtUtils.getInt(tag, "currentLifespan").orElse(0);
	}
	//?} else {
	/*@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		
		OptNbtUtils.putUUID(tag, "ownerId", ownerId);
		tag.putInt("maxLifespan", maxLifespan);
		tag.putInt("currentLifespan", currentLifespan);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		
		ownerId = OptNbtUtils.getUUID(tag, "ownerId").orElse(null);
		maxLifespan = OptNbtUtils.getInt(tag, "maxLifespan").orElse(0);
		currentLifespan = OptNbtUtils.getInt(tag, "currentLifespan").orElse(0);
	}
	*///?}
}
