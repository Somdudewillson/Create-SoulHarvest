package somdudewillson.create_soul_harvest.content.conduit;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

import java.util.List;

public class WindowedPotentiaConduitBlockEntity extends SmartBlockEntity {

	@Nullable
	private ResourceLocation currentEntityType = null;
	@Nullable
	private Direction currentFlowDirection = null;
	private int activeTicksRemaining = 0;
	private float flowIntensity = 0.0f;

	public WindowedPotentiaConduitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public WindowedPotentiaConduitBlockEntity(BlockPos pos, BlockState state) {
		this(ModBlockEntities.WINDOWED_POTENTIA_CONDUIT_BE.get(), pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public void recordFlow(SoulPotentiaStack stack, Direction direction) {
		if (stack == null || stack.isEmpty()) return;
		this.currentEntityType = stack.getEntityType();
		this.currentFlowDirection = direction;
		this.activeTicksRemaining = 25;
		this.flowIntensity = Math.min(1.0f, Math.max(0.2f, (float) Math.log10(stack.getAmount() + 1) / 3.0f));
		setChanged();
		sendData();
	}

	@Override
	public void tick() {
		super.tick();
		if (activeTicksRemaining > 0) {
			activeTicksRemaining--;
			if (activeTicksRemaining == 0) {
				flowIntensity = 0.0f;
				if (level != null && !level.isClientSide()) {
					sendData();
				}
			}
		}
	}

	@Nullable
	public ResourceLocation getCurrentEntityType() {
		return currentEntityType;
	}

	@Nullable
	public Direction getCurrentFlowDirection() {
		return currentFlowDirection;
	}

	public int getActiveTicksRemaining() {
		return activeTicksRemaining;
	}

	public float getFlowIntensity() {
		return flowIntensity;
	}

	//? >= 1.21 {
	@Override
	protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		if (currentEntityType != null) {
			compound.putString("EntityType", currentEntityType.toString());
		}
		if (currentFlowDirection != null) {
			compound.putInt("FlowDirection", currentFlowDirection.get3DDataValue());
		}
		compound.putInt("ActiveTicks", activeTicksRemaining);
		compound.putFloat("Intensity", flowIntensity);
	}

	@Override
	protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (compound.contains("EntityType")) {
			currentEntityType = ResourceLocation.tryParse(compound.getString("EntityType"));
		} else {
			currentEntityType = null;
		}
		if (compound.contains("FlowDirection")) {
			currentFlowDirection = Direction.from3DDataValue(compound.getInt("FlowDirection"));
		} else {
			currentFlowDirection = null;
		}
		activeTicksRemaining = compound.getInt("ActiveTicks");
		flowIntensity = compound.getFloat("Intensity");
	}
	//?} else {
	/*@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		if (currentEntityType != null) {
			compound.putString("EntityType", currentEntityType.toString());
		}
		if (currentFlowDirection != null) {
			compound.putInt("FlowDirection", currentFlowDirection.get3DDataValue());
		}
		compound.putInt("ActiveTicks", activeTicksRemaining);
		compound.putFloat("Intensity", flowIntensity);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		if (compound.contains("EntityType")) {
			currentEntityType = ResourceLocation.tryParse(compound.getString("EntityType"));
		} else {
			currentEntityType = null;
		}
		if (compound.contains("FlowDirection")) {
			currentFlowDirection = Direction.from3DDataValue(compound.getInt("FlowDirection"));
		} else {
			currentFlowDirection = null;
		}
		activeTicksRemaining = compound.getInt("ActiveTicks");
		flowIntensity = compound.getFloat("Intensity");
	}
	*///?}
}
