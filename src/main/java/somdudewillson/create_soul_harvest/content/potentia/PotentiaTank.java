package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class PotentiaTank implements IPotentiaHandler {

	protected long capacity;
	protected SoulPotentiaStack potentia = SoulPotentiaStack.EMPTY;
	protected Consumer<PotentiaTank> onContentsChanged;

	public PotentiaTank(long capacity) {
		this(capacity, null);
	}

	public PotentiaTank(long capacity, Consumer<PotentiaTank> onContentsChanged) {
		this.capacity = capacity;
		this.onContentsChanged = onContentsChanged;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
		if (this.potentia.getAmount() > capacity) {
			this.potentia.setAmount(capacity);
			onContentsChanged();
		}
	}

	public SoulPotentiaStack getPotentia() {
		return potentia;
	}

	public void setPotentia(SoulPotentiaStack potentia) {
		this.potentia = potentia == null ? SoulPotentiaStack.EMPTY : potentia;
		onContentsChanged();
	}

	protected void onContentsChanged() {
		if (onContentsChanged != null) {
			onContentsChanged.accept(this);
		}
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public SoulPotentiaStack getPotentiaInTank(int tank) {
		return potentia;
	}

	@Override
	public long getTankCapacity(int tank) {
		return capacity;
	}

	@Override
	public boolean isPotentiaValid(int tank, SoulPotentiaStack stack) {
		if (stack == null || stack.isEmpty()) return false;
		if (potentia.isEmpty()) return true;
		return potentia.canCombine(stack);
	}

	@Override
	public long fill(SoulPotentiaStack resource, PotentiaAction action) {
		if (resource == null || resource.isEmpty()) return 0;
		if (!isPotentiaValid(0, resource)) return 0;

		long space = capacity - potentia.getAmount();
		if (space <= 0) return 0;

		long filled = Math.min(space, resource.getAmount());
		if (action.execute() && filled > 0) {
			if (potentia.isEmpty()) {
				potentia = new SoulPotentiaStack(filled, resource.getEntityType());
			} else {
				potentia.grow(filled);
			}
			onContentsChanged();
		}
		return filled;
	}

	@Override
	public SoulPotentiaStack drain(SoulPotentiaStack resource, PotentiaAction action) {
		if (resource == null || resource.isEmpty() || potentia.isEmpty()) return SoulPotentiaStack.EMPTY;
		if (!SoulPotentiaStack.isSameType(potentia, resource)) return SoulPotentiaStack.EMPTY;
		return drain(resource.getAmount(), action);
	}

	@Override
	public SoulPotentiaStack drain(long maxDrain, PotentiaAction action) {
		if (maxDrain <= 0 || potentia.isEmpty()) return SoulPotentiaStack.EMPTY;

		long drained = Math.min(potentia.getAmount(), maxDrain);
		SoulPotentiaStack result = new SoulPotentiaStack(drained, potentia.getEntityType());
		if (action.execute() && drained > 0) {
			potentia.shrink(drained);
			if (potentia.isEmpty()) {
				potentia = SoulPotentiaStack.EMPTY;
			}
			onContentsChanged();
		}
		return result;
	}

	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putLong("Capacity", capacity);
		if (!potentia.isEmpty()) {
			CompoundTag potentiaTag = new CompoundTag();
			potentia.writeToNBT(potentiaTag);
			tag.put("Potentia", potentiaTag);
		}
		return tag;
	}

	public void readFromNBT(CompoundTag tag) {
		if (tag.contains("Capacity")) {
			this.capacity = tag.getLong("Capacity");
		}
		if (tag.contains("Potentia")) {
			this.potentia = SoulPotentiaStack.readFromNBT(tag.getCompound("Potentia"));
		} else {
			this.potentia = SoulPotentiaStack.EMPTY;
		}
	}
}
