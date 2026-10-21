package somdudewillson.create_soul_harvest.content.soul.sampling;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.soul.DropRestriction;

public class StandardDropSamplingPass implements DropSamplingPass {

	@Override
	public String name() {
		return "standard";
	}

	@Override
	public DropRestriction restriction() {
		return DropRestriction.STANDARD;
	}

	@Override
	public LootParams createLootParams(ServerLevel level, @Nullable Entity entity) {
		DamageSource damageSource = level.damageSources().genericKill();
		Vec3 pos = entity != null ? entity.position() : Vec3.atCenterOf(level.getSharedSpawnPos());

		LootParams.Builder paramsBuilder = new LootParams.Builder(level)
				.withParameter(LootContextParams.ORIGIN, pos)
				.withParameter(LootContextParams.DAMAGE_SOURCE, damageSource);

		if (entity != null) {
			paramsBuilder.withParameter(LootContextParams.THIS_ENTITY, entity);
		}

		return paramsBuilder.create(LootContextParamSets.ENTITY);
	}
}
