package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static somdudewillson.create_soul_harvest.registry.ModTags.Blocks.CONSIDER_REPLACEABLE;

public class RunicSpells {

	public static void castSpell1_Lightning(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			SpellThinker.spawn(serverLevel, caster.blockPosition(), new LightningSpellEffect());
		}
	}

	public static void castSpell2_Fireball(Level level, LivingEntity caster) {
		FireballSpell.cast(level, caster);
	}

	public static void castSpell3_Bonemeal(Level level, LivingEntity caster) {
		BonemealSpell.cast(level, caster);
	}

	public static void castSpell4_ContagionBlast(Level level, LivingEntity caster) {
		ContagionBlastSpell.cast(level, caster);
	}

	public static void castSpell5_Fortitude(Level level, LivingEntity caster) {
		FortitudeSpell.cast(level, caster);
	}

	public static void castSpell6_Updraft(Level level, LivingEntity caster) {
		UpdraftSpell.cast(level, caster);
	}

	public static void castSpell7_InstantShelter(Level level, LivingEntity caster) {
		InstantShelterSpell.cast(level, caster);
	}

	public static void castSpell8_TerrainFlatten(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			BlockPos spawnPos = caster.blockPosition();
			while (serverLevel.getBlockState(spawnPos.below()).canBeReplaced() || serverLevel.getBlockState(spawnPos.below()).is(CONSIDER_REPLACEABLE)) {
				spawnPos = spawnPos.below();
				if (serverLevel.isOutsideBuildHeight(spawnPos) || serverLevel.isOutsideBuildHeight(spawnPos.above())) {
					return;
				}
			}

			SpellThinker.spawn(serverLevel, spawnPos, new TerrainFlattenSpellEffect());
		}
	}
}
