package somdudewillson.create_soul_harvest.content.capability;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface ISoulBudGrowthHandler {

	/**
	 * Checks whether this block currently supports soul bud growth on the specified side.
	 *
	 * @param side The face of this block touching the soul bud.
	 * @return true if soul buds can attach and grow on this face.
	 */
	boolean canGrowBuds(@Nullable Direction side);

	/**
	 * Called when an attached soul bud attempts to advance a growth stage.
	 *
	 * @param side The face of this block touching the growing soul bud.
	 * @return true if the growth advancement is permitted and any required resources were consumed.
	 */
	boolean onBudGrow(@Nullable Direction side);
}
