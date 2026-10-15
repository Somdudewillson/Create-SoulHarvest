package somdudewillson.create_soul_harvest.content.spell;

import java.util.Objects;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlockEntity;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.redsparklib.common.utils.OptNbtUtils;

public abstract class AnimaGlassStructureSpell extends StructureSpell {

	protected UUID ownerUUID;

	protected abstract int getLifespan();
	
	public AnimaGlassStructureSpell(ResourceLocation id) {
		super(id);
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			this.ownerUUID = caster.getUUID();
			super.cast(serverLevel, caster);
		}
	}

	@Override
	protected boolean isValidPlacement(ServerLevel level, BlockPos pos) {
		if (super.isValidPlacement(level, pos)) {
			return true;
		}

		if (level.isOutsideBuildHeight(pos)) {
			return false;
		}

		BlockState state = level.getBlockState(pos);
		if (state.is(ModBlocks.MAGIC_GLASS.get())) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof AnimaGlassBlockEntity animaBe) {
				return Objects.equals(this.ownerUUID, animaBe.getOwnerId());
			}
		}

		return false;
	}

	@Override
	protected void placeBlock(ServerLevel level, BlockPos pos) {
		level.setBlockAndUpdate(pos, ModBlocks.MAGIC_GLASS.get().defaultBlockState());
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof AnimaGlassBlockEntity animaBe) {
			if (this.ownerUUID != null) {
				animaBe.setOwnerId(this.ownerUUID);
			}
			animaBe.setMaxLifespan(getLifespan());
			animaBe.setCurrentLifespan(0);
		}
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		super.writeSaveData(tag);
		
		OptNbtUtils.putUUID(tag, "owner_uuid", ownerUUID);
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		super.readSaveData(tag);
		
		this.ownerUUID = OptNbtUtils.getUUID(tag, "owner_uuid").orElse(null);
	}
}
