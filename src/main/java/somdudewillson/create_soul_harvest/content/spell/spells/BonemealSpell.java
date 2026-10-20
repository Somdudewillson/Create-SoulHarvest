package somdudewillson.create_soul_harvest.content.spell.spells;

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
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;

@RequiredArgsConstructor
public class BonemealSpell implements GlyphSpell {
	protected final int radius;

	@Override
	public boolean cast(Level level, LivingEntity caster) {
		BlockPos center = caster.blockPosition();
		boolean success = false;
		for (BlockPos pos : BlockPos.withinManhattan(center, radius, 3, radius)) {
			BlockState state = level.getBlockState(pos);
			int manhattanDist = pos.distManhattan(center);
			if (radius - manhattanDist < 3 && level.random.nextFloat() > 0.25f + (0.25f*(radius - manhattanDist))) {
				continue;
			}
			if (tryBonemealBlock(level, pos, state, 0.25)) {
				success = true;
			}
		}
		return success;
	}
	
	public static boolean tryBonemealBlock(Level level, BlockPos pos, BlockState state, double grassChance) {
		if (!(state.getBlock() instanceof BonemealableBlock bonemealable)) return false;
		
		//? >= 1.21 {
		if (!bonemealable.isValidBonemealTarget(level, pos, state)) return false;
		//?} else {
		/*if (!bonemealable.isValidBonemealTarget(level, pos, state, level.isClientSide())) return false;
		*///?}
		if (state.is(Blocks.GRASS_BLOCK) && level.random.nextFloat() > grassChance) return false;

		if (level instanceof ServerLevel serverLevel) {
			bonemealable.performBonemeal(serverLevel, level.random, pos, state);
			return true;
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
			return true;
		}
		return false;
	}
}
