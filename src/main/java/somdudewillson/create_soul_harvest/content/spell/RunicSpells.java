package somdudewillson.create_soul_harvest.content.spell;

import java.util.function.Supplier;

public class RunicSpells {
	public static final Supplier<GlyphSpell> LIGHTNING = LightningSpell::new;
	public static final Supplier<GlyphSpell> FIREBALL = FireballSpell::new;
	public static final Supplier<GlyphSpell> BONEMEAL = BonemealSpell::new;
	public static final Supplier<GlyphSpell> CONTAGION_BLAST = ContagionBlastSpell::new;
	public static final Supplier<GlyphSpell> FORTITUDE = FortitudeSpell::new;
	public static final Supplier<GlyphSpell> UPDRAFT = UpdraftSpell::new;
	public static final Supplier<GlyphSpell> INSTANT_SHELTER = InstantShelterSpell::new;
	public static final Supplier<GlyphSpell> TERRAIN_FLATTEN = TerrainFlattenSpell::new;
}
