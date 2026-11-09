package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SoulPotentiaStack {

	public static final SoulPotentiaStack EMPTY = new SoulPotentiaStack(0, null);

	private long amount;
	@Nullable
	private ResourceLocation entityType;

	public SoulPotentiaStack(long amount) {
		this(amount, null);
	}

	public SoulPotentiaStack(long amount, @Nullable ResourceLocation entityType) {
		this.amount = Math.max(0, amount);
		this.entityType = this.amount > 0 ? entityType : null;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = Math.max(0, amount);
		if (this.amount == 0) {
			this.entityType = null;
		}
	}

	public void grow(long count) {
		setAmount(this.amount + count);
	}

	public void shrink(long count) {
		setAmount(this.amount - count);
	}

	@Nullable
	public ResourceLocation getEntityType() {
		return entityType;
	}

	public void setEntityType(@Nullable ResourceLocation entityType) {
		this.entityType = entityType;
	}

	public boolean isEmpty() {
		return amount <= 0;
	}

	public boolean isTyped() {
		return entityType != null;
	}

	/**
	 * Checks if two stacks have the exact same potentia type.
	 */
	public static boolean isSameType(SoulPotentiaStack a, SoulPotentiaStack b) {
		if (a == null || b == null) return false;
		if (a.isEmpty() && b.isEmpty()) return true;
		if (a.isEmpty() || b.isEmpty()) return false;
		return Objects.equals(a.getEntityType(), b.getEntityType());
	}

	/**
	 * Returns true if this stack can be merged into or combined with other.
	 * Rejects combination if either stack has distinct types (collision).
	 */
	public boolean canCombine(SoulPotentiaStack other) {
		if (other == null) return false;
		if (this.isEmpty() || other.isEmpty()) return true;
		return isSameType(this, other);
	}

	public SoulPotentiaStack copy() {
		return new SoulPotentiaStack(this.amount, this.entityType);
	}

	public SoulPotentiaStack split(long maxDrain) {
		long toDrain = Math.min(this.amount, Math.max(0, maxDrain));
		if (toDrain <= 0) {
			return EMPTY;
		}
		SoulPotentiaStack result = new SoulPotentiaStack(toDrain, this.entityType);
		this.shrink(toDrain);
		return result;
	}

	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putLong("Amount", this.amount);
		if (this.entityType != null) {
			tag.putString("EntityType", this.entityType.toString());
		}
		return tag;
	}

	public static SoulPotentiaStack readFromNBT(CompoundTag tag) {
		if (tag == null || !tag.contains("Amount")) {
			return EMPTY;
		}
		long amount = tag.getLong("Amount");
		ResourceLocation entityType = null;
		if (tag.contains("EntityType")) {
			entityType = ResourceLocation.tryParse(tag.getString("EntityType"));
		}
		return new SoulPotentiaStack(amount, entityType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SoulPotentiaStack that = (SoulPotentiaStack) o;
		if (this.isEmpty() && that.isEmpty()) return true;
		return amount == that.amount && Objects.equals(entityType, that.entityType);
	}

	@Override
	public int hashCode() {
		if (isEmpty()) return 0;
		return Objects.hash(amount, entityType);
	}

	@Override
	public String toString() {
		if (isEmpty()) return "SoulPotentiaStack[EMPTY]";
		return "SoulPotentiaStack[" + amount + " mB" + (entityType != null ? " (" + entityType + ")" : " (untyped)") + "]";
	}
}
