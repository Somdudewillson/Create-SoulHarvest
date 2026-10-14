package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class BonemealSpell {

	private BonemealSpell() {}

	public static void cast(Level level, LivingEntity caster) {
		BlockPos center = caster.blockPosition();
		for (BlockPos pos : BlockPos.withinManhattan(center, 15, 3, 15)) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof BonemealableBlock bonemealable) {
				//? >= 1.21 {
				if (!bonemealable.isValidBonemealTarget(level, pos, state)) continue;
				//?} else {
				/*if (!bonemealable.isValidBonemealTarget(level, pos, state, level.isClientSide())) continue;
				*///?}
				if (state.is(Blocks.GRASS_BLOCK) && level.random.nextFloat() < 0.25f) continue;

				if (level instanceof ServerLevel serverLevel) {
					bonemealable.performBonemeal(serverLevel, level.random, pos, state);
					if (level.random.nextFloat() < 0.5f) {
						serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2, 0.5, 0.5, 0.5, 0);
					}
				}
			}
		}
	}
}
