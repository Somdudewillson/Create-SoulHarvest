package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public interface PersistentGlyphSpell extends GlyphSpell {

	/**
	 * Unique identifier for this spell effect, used for serialization and registry lookup.
	 */
	ResourceLocation getId();

	/**
	 * Executed on each server tick of the owning {@link SpellThinker}.
	 */
	void tick(SpellThinker thinker, ServerLevel level);

	/**
	 * Returns true if this spell effect has finished executing and the owning thinker should be discarded.
	 */
	boolean isFinished(SpellThinker thinker);

	/**
	 * Serializes effect-specific state to NBT.
	 */
	default void writeSaveData(CompoundTag tag) {}

	/**
	 * Restores effect-specific state from NBT.
	 */
	default void readSaveData(CompoundTag tag) {}

	/**
	 * Called once when the effect is first assigned to a thinker.
	 */
	default void onStart(SpellThinker thinker, ServerLevel level) {}

	/**
	 * Called when the effect is completed or discarded.
	 */
	default void onEnd(SpellThinker thinker, ServerLevel level) {}
}
