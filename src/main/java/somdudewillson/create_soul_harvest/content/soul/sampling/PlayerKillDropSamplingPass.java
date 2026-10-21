package somdudewillson.create_soul_harvest.content.soul.sampling;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.soul.DropRestriction;

//? >= 1.21 {
import net.neoforged.neoforge.common.util.FakePlayerFactory;
//?} else {
/*import net.minecraftforge.common.util.FakePlayerFactory;
*///?}

public class PlayerKillDropSamplingPass implements DropSamplingPass {

	@Override
	public String name() {
		return "player_kill";
	}

	@Override
	public DropRestriction restriction() {
		return DropRestriction.ENHANCED;
	}

	@Override
	public LootParams createLootParams(ServerLevel level, @Nullable Entity entity) {
		Player player = FakePlayerFactory.getMinecraft(level);
		Vec3 pos = entity != null ? entity.position() : Vec3.atCenterOf(level.getSharedSpawnPos());
		if (player != null) {
			player.setPos(pos.x, pos.y, pos.z);
		}

		DamageSource damageSource = level.damageSources().playerAttack(player);
		LootParams.Builder paramsBuilder = new LootParams.Builder(level)
				.withParameter(LootContextParams.ORIGIN, pos)
				.withParameter(LootContextParams.DAMAGE_SOURCE, damageSource);

		if (entity != null) {
			paramsBuilder.withParameter(LootContextParams.THIS_ENTITY, entity);
		}
		if (player != null) {
			//? >= 1.21 {
			paramsBuilder.withParameter(LootContextParams.ATTACKING_ENTITY, player);
			//?} else {
			/*paramsBuilder.withParameter(LootContextParams.KILLER_ENTITY, player);
			*///?}
			paramsBuilder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
		}

		return paramsBuilder.create(LootContextParamSets.ENTITY);
	}
}
