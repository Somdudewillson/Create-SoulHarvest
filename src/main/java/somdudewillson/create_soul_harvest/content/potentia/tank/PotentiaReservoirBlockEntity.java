package somdudewillson.create_soul_harvest.content.potentia.tank;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaTank;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaTooltipHelper;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

import java.util.List;

public class PotentiaReservoirBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

	public static final long DEFAULT_CAPACITY = 16000L;

	protected final PotentiaTank tank = new PotentiaTank(DEFAULT_CAPACITY, t -> notifyUpdate());

	public PotentiaReservoirBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public PotentiaReservoirBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.POTENTIA_RESERVOIR_BE.get(), pos, state);
	}

	public PotentiaTank getTank() {
		return tank;
	}

	@Nullable
	public IPotentiaHandler getPotentiaHandler(@Nullable Direction side) {
		return tank;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		PotentiaTooltipHelper.langBuilder().translate("gui.goggles.potentia_reservoir")
				.forGoggles(tooltip);

		PotentiaTooltipHelper.appendTankTooltip(tooltip, tank);
		return true;
	}

	//? >= 1.21 {
	@Override
	protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		tank.writeToNBT(compound);
	}

	@Override
	protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		tank.readFromNBT(compound);
	}
	//?} else {
	/*@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		tank.writeToNBT(compound);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		tank.readFromNBT(compound);
	}
	*///?}
}
