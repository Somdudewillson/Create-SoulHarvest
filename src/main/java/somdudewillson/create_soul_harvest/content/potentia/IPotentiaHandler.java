package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface IPotentiaHandler {

	enum PotentiaAction {
		EXECUTE,
		SIMULATE;

		public boolean execute() {
			return this == EXECUTE;
		}

		public boolean simulate() {
			return this == SIMULATE;
		}
	}

	int getTanks();

	SoulPotentiaStack getPotentiaInTank(int tank);

	long getTankCapacity(int tank);

	boolean isPotentiaValid(int tank, SoulPotentiaStack stack);

	long fill(SoulPotentiaStack resource, PotentiaAction action);

	SoulPotentiaStack drain(SoulPotentiaStack resource, PotentiaAction action);

	SoulPotentiaStack drain(long maxDrain, PotentiaAction action);

	default boolean canPush(@Nullable Direction side) {
		return true;
	}

	default boolean canAccept(@Nullable Direction side) {
		return true;
	}
}
