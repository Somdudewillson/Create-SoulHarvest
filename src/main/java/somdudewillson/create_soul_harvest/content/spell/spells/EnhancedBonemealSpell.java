package somdudewillson.create_soul_harvest.content.spell.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

public class EnhancedBonemealSpell implements GlyphSpell {

	private final int thinkerCount;
	private final double maxRadius;
	private final double stepSize;
	private final int bonemealRadius;
	private final float plantChance;

	public EnhancedBonemealSpell() {
		this(8, 48.0, 0.5, 1, 0.25F);
	}

	public EnhancedBonemealSpell(int thinkerCount) {
		this(thinkerCount, 48.0, 0.5, 1, 0.25F);
	}

	public EnhancedBonemealSpell(int thinkerCount, double maxRadius, double stepSize, int bonemealRadius, float plantChance) {
		this.thinkerCount = Math.max(1, thinkerCount);
		this.maxRadius = maxRadius;
		this.stepSize = stepSize;
		this.bonemealRadius = bonemealRadius;
		this.plantChance = plantChance;
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			BlockPos center = caster.blockPosition();

			for (int i = 0; i < thinkerCount; i++) {
				EnhancedBonemealThinkerSpell thinkerSpell = new EnhancedBonemealThinkerSpell(
						EnhancedBonemealThinkerSpell.ID,
						center,
						i,
						thinkerCount,
						maxRadius,
						stepSize,
						bonemealRadius,
						plantChance
				);
				SpellThinkerEntity thinker = SpellThinkerEntity.spawn(serverLevel, center, thinkerSpell, caster.getUUID());
				long flags = (i & 0xFL) | ((thinkerCount & 0xFL) << 4) | (((long) Math.min(255, (int) maxRadius)) << 8);
				thinker.setEffectFlags(flags);
			}

			serverLevel.playSound(null, center, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
			serverLevel.sendParticles(ModParticleTypes.VERDANT_FLASH.get(),
					center.getX() + 0.5, center.getY() + 1.0, center.getZ() + 0.5,
					20, 0.5, 0.5, 0.5, 0.05);
		}
	}

	public int getThinkerCount() {
		return thinkerCount;
	}

	public double getMaxRadius() {
		return maxRadius;
	}

	public double getStepSize() {
		return stepSize;
	}

	public int getBonemealRadius() {
		return bonemealRadius;
	}

	public float getPlantChance() {
		return plantChance;
	}
}
