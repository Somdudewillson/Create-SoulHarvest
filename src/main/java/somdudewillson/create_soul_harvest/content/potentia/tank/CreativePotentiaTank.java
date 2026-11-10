package somdudewillson.create_soul_harvest.content.potentia.tank;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;

import java.util.Objects;
import java.util.function.Supplier;

public class CreativePotentiaTank implements IPotentiaHandler {

	private final Supplier<ReservoirMode> modeSupplier;
	private final Supplier<ResourceLocation> filterEntityTypeSupplier;
	private final long capacity;

	public CreativePotentiaTank(long capacity, Supplier<ReservoirMode> modeSupplier, Supplier<ResourceLocation> filterEntityTypeSupplier) {
		this.capacity = capacity;
		this.modeSupplier = modeSupplier;
		this.filterEntityTypeSupplier = filterEntityTypeSupplier;
	}

	public ReservoirMode getMode() {
		return modeSupplier.get();
	}

	@Nullable
	public ResourceLocation getFilterEntityType() {
		return filterEntityTypeSupplier.get();
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public SoulPotentiaStack getPotentiaInTank(int tank) {
		if (tank != 0) return SoulPotentiaStack.EMPTY;
		if (getMode().isSupply()) {
			return new SoulPotentiaStack(capacity, getFilterEntityType());
		}
		return SoulPotentiaStack.EMPTY;
	}

	public SoulPotentiaStack getPotentia() {
		return getPotentiaInTank(0);
	}

	@Override
	public long getTankCapacity(int tank) {
		return capacity;
	}

	@Override
	public boolean isPotentiaValid(int tank, SoulPotentiaStack stack) {
		if (stack == null || stack.isEmpty()) return false;
		if (getMode().isSupply()) {
			ResourceLocation filter = getFilterEntityType();
			return Objects.equals(filter, stack.getEntityType());
		} else {
			ResourceLocation filter = getFilterEntityType();
			// If filter is empty, consumer accepts all potentia types; otherwise must match filter
			return filter == null || Objects.equals(filter, stack.getEntityType());
		}
	}

	@Override
	public long fill(SoulPotentiaStack resource, PotentiaAction action) {
		if (resource == null || resource.isEmpty()) return 0;
		if (!getMode().isConsume()) {
			return 0; // Suppliers cannot be filled
		}
		if (!isPotentiaValid(0, resource)) {
			return 0;
		}
		// Void sink accepts the full amount
		return resource.getAmount();
	}

	@Override
	public SoulPotentiaStack drain(SoulPotentiaStack resource, PotentiaAction action) {
		if (resource == null || resource.isEmpty() || !getMode().isSupply()) {
			return SoulPotentiaStack.EMPTY;
		}
		ResourceLocation filter = getFilterEntityType();
		if (!Objects.equals(filter, resource.getEntityType())) {
			return SoulPotentiaStack.EMPTY;
		}
		return drain(resource.getAmount(), action);
	}

	@Override
	public SoulPotentiaStack drain(long maxDrain, PotentiaAction action) {
		if (maxDrain <= 0 || !getMode().isSupply()) {
			return SoulPotentiaStack.EMPTY;
		}
		long drained = Math.min(capacity, maxDrain);
		return new SoulPotentiaStack(drained, getFilterEntityType());
	}
}
