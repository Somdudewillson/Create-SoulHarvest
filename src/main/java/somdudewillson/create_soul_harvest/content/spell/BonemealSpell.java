package somdudewillson.create_soul_harvest.content.spell;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@RequiredArgsConstructor
public class BonemealSpell implements GlyphSpell {
	protected final int radius;

	@Override
	public void cast(Level level, LivingEntity caster) {
		BlockPos center = caster.blockPosition();
		for (BlockPos pos : BlockPos.withinManhattan(center, radius, 3, radius)) {
			BlockState state = level.getBlockState(pos);
			int manhattanDist = pos.distManhattan(center);
			if (radius - manhattanDist < 3 && level.random.nextFloat() > 0.25f + (0.25f*(radius - manhattanDist))) {
				continue;
			}
			tryBonemealBlock(level, pos, state);
		}
	}
	
	protected void tryBonemealBlock(Level level, BlockPos pos, BlockState state) {
		if (!(state.getBlock() instanceof BonemealableBlock bonemealable)) return;
		
		//? >= 1.21 {
		if (!bonemealable.isValidBonemealTarget(level, pos, state)) return;
		//?} else {
		/*if (!bonemealable.isValidBonemealTarget(level, pos, state, level.isClientSide())) return;
		*///?}
		if (state.is(Blocks.GRASS_BLOCK) && level.random.nextFloat() < 0.25f) return;

		if (level instanceof ServerLevel serverLevel) {
			bonemealable.performBonemeal(serverLevel, level.random, pos, state);
		} else if (level.isClientSide) {
			float particleRoll = level.random.nextFloat();
			Vec3 particlePos = pos.getCenter();

			particlePos.add((pos.getY() & 15) / 30f, (pos.getZ() & 15) / 30f, (pos.getX() & 15) / 30f);
			level.addParticle(ParticleTypes.HAPPY_VILLAGER, 
					particlePos.x(), particlePos.y(), particlePos.z(), 
					0, 0, 0);
			if (particleRoll < 0.75) {
				particlePos.subtract((pos.getZ() & 15) / 30f, (pos.getX() & 15) / 30f, (pos.getY() & 15) / 30f);
				level.addParticle(ParticleTypes.HAPPY_VILLAGER, 
						particlePos.x(), particlePos.y(), particlePos.z(), 
						0, 0, 0);
			}
		}
	}
}
