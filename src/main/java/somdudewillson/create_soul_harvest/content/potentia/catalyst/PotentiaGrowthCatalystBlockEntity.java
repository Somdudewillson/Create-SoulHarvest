package somdudewillson.create_soul_harvest.content.potentia.catalyst;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.config.SoulHarvestConfig;
import somdudewillson.create_soul_harvest.content.block.SoulBudBlock;
import somdudewillson.create_soul_harvest.content.capability.ISoulBudGrowthHandler;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaTooltipHelper;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;
import somdudewillson.create_soul_harvest.content.potentia.client.PotentiaColorHelper;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

import java.util.List;
import java.util.Objects;

public class PotentiaGrowthCatalystBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, Clearable {

	protected SoulPotentiaStack buffer = SoulPotentiaStack.EMPTY;
	private int clientCachedColor = -1;

	private final IPotentiaHandler potentiaHandler = new IPotentiaHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public SoulPotentiaStack getPotentiaInTank(int tank) {
			return buffer.copy();
		}

		@Override
		public long getTankCapacity(int tank) {
			return SoulHarvestConfig.getCatalystCapacity();
		}

		@Override
		public boolean isPotentiaValid(int tank, SoulPotentiaStack stack) {
			return stack != null && !stack.isEmpty();
		}

		@Override
		public long fill(SoulPotentiaStack resource, PotentiaAction action) {
			if (resource == null || resource.isEmpty()) return 0L;
			if (isVirtual()) {
				if (action.execute() && resource.getEntityType() != null && !Objects.equals(buffer.getEntityType(), resource.getEntityType())) {
					buffer = new SoulPotentiaStack(resource.getAmount(), resource.getEntityType());
					notifyUpdate();
				}
				return resource.getAmount();
			}
			long capacity = getTankCapacity(0);
			long currentAmount = buffer.getAmount();
			boolean isMismatch = !buffer.isEmpty() && !SoulPotentiaStack.isSameType(buffer, resource);

			long availableSpace = Math.max(0, capacity - currentAmount);
			long toAccept = Math.min(availableSpace, resource.getAmount());

			if (isMismatch && availableSpace <= 0 && resource.getAmount() > 0) {
				toAccept = 1L;
			}

			if (action.execute()) {
				if (isMismatch) {
					buffer.setEntityType(resource.getEntityType());
					if (toAccept > 0) {
						if (availableSpace <= 0) {
							buffer.setAmount(capacity);
						} else {
							buffer.grow(toAccept);
						}
					}
				} else if (buffer.isEmpty()) {
					buffer = new SoulPotentiaStack(toAccept, resource.getEntityType());
				} else {
					buffer.grow(toAccept);
				}
				notifyUpdate();
			}
			return toAccept;
		}

		@Override
		public SoulPotentiaStack drain(SoulPotentiaStack resource, PotentiaAction action) {
			return SoulPotentiaStack.EMPTY;
		}

		@Override
		public SoulPotentiaStack drain(long maxDrain, PotentiaAction action) {
			return SoulPotentiaStack.EMPTY;
		}

		@Override
		public boolean canAccept(@Nullable Direction side) {
			return side == null || isVirtual() || side == getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING);
		}

		@Override
		public boolean canPush(@Nullable Direction side) {
			return false;
		}
	};

	private final ISoulBudGrowthHandler growthHandler = new ISoulBudGrowthHandler() {
		@Override
		public boolean canGrowBuds(@Nullable Direction side) {
			if (side != null && side == getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING)) {
				return false;
			}
			return hasEnoughPotentia();
		}

		@Override
		public boolean onBudGrow(@Nullable Direction side) {
			if (side != null && side == getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING)) {
				return false;
			}
			if (!hasEnoughPotentia()) {
				return false;
			}
			long cost = SoulHarvestConfig.getCatalystGrowthConsumption();
			if (buffer.getAmount() >= cost) {
				buffer.shrink(cost);
				notifyUpdate();
				
				if (level instanceof ServerLevel serverLevel) {
					Vec3 centerPos = getBlockPos().getCenter();
					serverLevel.sendParticles(
							ParticleTypes.HAPPY_VILLAGER, 
							centerPos.x, centerPos.y, centerPos.z, 
							25, 
							0.4, 0.4, 0.4, 
							0.05);
				}
				return true;
			}
			return false;
		}
	};

	//? if forge {
	/*protected net.minecraftforge.common.util.LazyOptional<IPotentiaHandler> potentiaCapability =
			net.minecraftforge.common.util.LazyOptional.of(() -> potentiaHandler);
	protected net.minecraftforge.common.util.LazyOptional<ISoulBudGrowthHandler> growthCapability =
			net.minecraftforge.common.util.LazyOptional.of(() -> growthHandler);
	*///?}

	public PotentiaGrowthCatalystBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public PotentiaGrowthCatalystBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.POTENTIA_GROWTH_CATALYST_BE.get(), pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public SoulPotentiaStack getPotentia() {
		return buffer;
	}

	public boolean hasEnoughPotentia() {
		return buffer.getAmount() >= SoulHarvestConfig.getCatalystThreshold();
	}

	public int getCrystalColor() {
		return getCrystalColor(null);
	}

	public int getCrystalColor(@Nullable BlockState state) {
		if (state == null) {
			state = getBlockState();
			if (state == null && level != null) {
				state = level.getBlockState(worldPosition);
			}
		}
		int primary = PotentiaColorHelper.getColors(buffer).primary();
		boolean lit;
		if (isVirtual() || (state != null && state.hasProperty(PotentiaGrowthCatalystBlock.LIT))) {
			lit = state != null && state.getValue(PotentiaGrowthCatalystBlock.LIT);
		} else {
			lit = hasEnoughPotentia();
		}
		if (!lit) {
			return dimColor(primary, 0.35f);
		}
		return primary;
	}

	public static int dimColor(int rgb, float factor) {
		int r = (int) (((rgb >> 16) & 0xFF) * factor);
		int g = (int) (((rgb >> 8) & 0xFF) * factor);
		int b = (int) ((rgb & 0xFF) * factor);
		return (r << 16) | (g << 8) | b;
	}

	@Nullable
	public IPotentiaHandler getPotentiaHandler(@Nullable Direction side) {
		if (side == null || isVirtual() || side == getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING)) {
			return potentiaHandler;
		}
		return null;
	}

	@Nullable
	public ISoulBudGrowthHandler getGrowthHandler(@Nullable Direction side) {
		if (side == null || side != getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING)) {
			return growthHandler;
		}
		return null;
	}

	//? if forge {
	/*@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
		if (cap == somdudewillson.create_soul_harvest.registry.ModCapabilities.POTENTIA_HANDLER) {
			if (side == null || isVirtual() || side == getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING)) {
				return potentiaCapability.cast();
			}
			return net.minecraftforge.common.util.LazyOptional.empty();
		}
		if (cap == somdudewillson.create_soul_harvest.registry.ModCapabilities.SOUL_BUD_GROWTH_HANDLER) {
			if (side == null || side != getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING)) {
				return growthCapability.cast();
			}
			return net.minecraftforge.common.util.LazyOptional.empty();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		potentiaCapability.invalidate();
		growthCapability.invalidate();
	}
	*///?}

	public void onFacingChanged() {
		notifyUpdate();
	}

	public void onNeighborUpdate() {
		// Called on neighbor block change
	}

	@Override
	public void tick() {
		super.tick();

		if (level != null && !level.isClientSide()) {
			if (level.getGameTime() % 20 == 0) {
				Direction inputSide = getBlockState().getValue(PotentiaGrowthCatalystBlock.FACING);
				int attachedBuds = 0;
				for (Direction d : Direction.values()) {
					if (d == inputSide) continue;
					BlockPos neighborPos = worldPosition.relative(d);
					BlockState neighborState = level.getBlockState(neighborPos);
					if (neighborState.getBlock() instanceof SoulBudBlock) {
						if (neighborState.getValue(SoulBudBlock.FACING) == d) {
							attachedBuds++;
						}
					}
				}

				if (attachedBuds > 0 && buffer.getAmount() > 0) {
					long passiveDrain = attachedBuds * SoulHarvestConfig.getCatalystPassiveDepletionPerTick() * 20L;
					buffer.shrink(Math.min(buffer.getAmount(), passiveDrain));
					notifyUpdate();
				}
			}

			boolean hasEnough = hasEnoughPotentia();
			BlockState state = getBlockState();
			if (state.hasProperty(PotentiaGrowthCatalystBlock.LIT) && state.getValue(PotentiaGrowthCatalystBlock.LIT) != hasEnough) {
				level.setBlock(worldPosition, state.setValue(PotentiaGrowthCatalystBlock.LIT, hasEnough), 3);
				level.updateNeighborsAt(worldPosition, state.getBlock());
			}
		} else if (level != null && level.isClientSide()) {
			int currentColor = getCrystalColor();
			if (clientCachedColor != currentColor) {
				clientCachedColor = currentColor;
				requestModelDataUpdate();
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 8);
			}
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		PotentiaTooltipHelper.addToGoggles(
				PotentiaTooltipHelper.langBuilder().translate("gui.goggles.potentia_growth_catalyst"),
				tooltip, 0
		);

		PotentiaTooltipHelper.appendTankTooltip(tooltip, buffer, SoulHarvestConfig.getCatalystCapacity());
		return true;
	}

	@Override
	public void clearContent() {
		this.buffer = SoulPotentiaStack.EMPTY;
		notifyUpdate();
	}

	//? >= 1.21 {
	@Override
	protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		compound.put("Buffer", buffer.writeToNBT(new CompoundTag()));
	}

	@Override
	protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (compound.contains("Buffer")) {
			this.buffer = SoulPotentiaStack.readFromNBT(compound.getCompound("Buffer"));
		} else {
			this.buffer = SoulPotentiaStack.EMPTY;
		}
	}
	//?} else {
	/*@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.put("Buffer", buffer.writeToNBT(new CompoundTag()));
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		if (compound.contains("Buffer")) {
			this.buffer = SoulPotentiaStack.readFromNBT(compound.getCompound("Buffer"));
		} else {
			this.buffer = SoulPotentiaStack.EMPTY;
		}
	}
	*///?}
}
