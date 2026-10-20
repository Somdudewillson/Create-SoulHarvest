package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface GlyphSpell {

	/**
	 * Casts this spell using the given level and caster.
	 *
	 * @param level the level where the spell is cast
	 * @param caster the entity casting the spell
	 * @return true if the spell successfully took effect, false if it fizzled (failed to do anything)
	 */
	boolean cast(Level level, LivingEntity caster);
}
