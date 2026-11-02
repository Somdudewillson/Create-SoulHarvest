package somdudewillson.create_soul_harvest.content.effect;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? >= 1.21 {
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
//?}
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.block.BiocideGasBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModDamageSources;

public class BiocideMobEffect extends MobEffect {

	public static final int BASE_GAS_RELEASE = 12;
	public static final int GAS_RELEASE_PER_LEVEL = 6;
	public static final float BASE_DAMAGE = 1.5F;
	public static final float DAMAGE_PER_LEVEL = 1.0F;

	public BiocideMobEffect() {
		//? >= 1.21 {
		super(MobEffectCategory.HARMFUL, 0x5DB824, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 93f / 255f, 184f / 255f, 36f / 255f));
		//?} else {
		/*super(MobEffectCategory.HARMFUL, 0x5DB824);
		*///?}
	}

	//? >= 1.21 {
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		tickBiocide(entity, amplifier);
		return true;
	}
	//?} else {
	/*@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		tickBiocide(entity, amplifier);
	}
	*///?}

	private void tickBiocide(LivingEntity entity, int amplifier) {
		if (entity.level().isClientSide()) {
			return;
		}

		if (entity.tickCount % 20 != 0) {
			return;
		}

		// Deal biocide damage
		float damage = BASE_DAMAGE + amplifier * DAMAGE_PER_LEVEL;
		entity.hurt(ModDamageSources.biocide(entity.level()), damage);

		// Release biocide gas using BFS flood fill
		int gasAmount = BASE_GAS_RELEASE + amplifier * GAS_RELEASE_PER_LEVEL;
		releaseBiocideGas(entity.level(), entity.blockPosition(), gasAmount);
	}

	public static void releaseBiocideGas(Level level, BlockPos origin, int amount) {
		if (level.isClientSide() || amount <= 0) {
			return;
		}

		BlockPos startPos = origin;
		if (!isPassableOrGas(level, startPos)) {
			startPos = origin.above();
			if (!isPassableOrGas(level, startPos)) {
				return;
			}
		}

		Queue<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(startPos);
		visited.add(startPos);

		int remaining = amount;
		int maxVisited = 64;

		while (!queue.isEmpty() && remaining > 0 && visited.size() <= maxVisited) {
			BlockPos cur = queue.poll();
			BlockState state = level.getBlockState(cur);

			if (state.is(ModBlocks.BIOCIDE_GAS.get())) {
				int currentDensity = state.getValue(BiocideGasBlock.DENSITY);
				if (currentDensity < BiocideGasBlock.MAX_DENSITY) {
					int toAdd = Math.min(remaining, BiocideGasBlock.MAX_DENSITY - currentDensity);
					level.setBlockAndUpdate(cur, state.setValue(BiocideGasBlock.DENSITY, currentDensity + toAdd).setValue(BiocideGasBlock.SETTLED, false));
					level.scheduleTick(cur, state.getBlock(), 10);
					remaining -= toAdd;
				}
			} else if (state.isAir()) {
				int toAdd = Math.min(remaining, BiocideGasBlock.MAX_DENSITY);
				BlockState newState = ModBlocks.BIOCIDE_GAS.get().defaultBlockState()
						.setValue(BiocideGasBlock.DENSITY, toAdd)
						.setValue(BiocideGasBlock.SETTLED, false);
				level.setBlockAndUpdate(cur, newState);
				level.scheduleTick(cur, newState.getBlock(), 10);
				remaining -= toAdd;
			}

			if (remaining > 0) {
				for (Direction dir : Direction.values()) {
					BlockPos next = cur.relative(dir);
					if (visited.add(next)) {
						if (isPassableOrGas(level, next)) {
							queue.add(next);
						}
					}
				}
			}
		}
	}

	private static boolean isPassableOrGas(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.isAir() || state.is(ModBlocks.BIOCIDE_GAS.get());
	}
}
