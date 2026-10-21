package somdudewillson.create_soul_harvest.content.soul.sampling;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.soul.DropRestriction;

public interface DropSamplingPass {
	String name();

	DropRestriction restriction();

	LootParams createLootParams(ServerLevel level, @Nullable Entity entity);

	default int rolls() {
		return 100;
	}
}
