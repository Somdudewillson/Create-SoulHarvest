package somdudewillson.create_soul_harvest.content.soul.sampling;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

import somdudewillson.create_soul_harvest.config.SoulHarvestConfig;
import somdudewillson.create_soul_harvest.content.soul.DropRestriction;

public interface DropSamplingPass {
	String name();

	DropRestriction restriction();

	LootParams createLootParams(ServerLevel level, @Nullable Entity entity);

	default int rolls() {
		return getBaseRollsScaled(1);
	}

	static int getBaseRollsScaled(double rollMult) {
		return applyRollConfig(SoulHarvestConfig.SCANNING_DEFAULT_ROLLS.get() * rollMult);
	}
	static int applyRollConfig(double baseRolls) {
		int rolls = (int) (baseRolls * SoulHarvestConfig.SCANNING_ROLLS_MULT.get());
		return Math.min(Math.max(1, rolls), 10000);
	}
}
