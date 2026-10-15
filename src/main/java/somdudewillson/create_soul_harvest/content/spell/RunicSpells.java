package somdudewillson.create_soul_harvest.content.spell;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class RunicSpells {
	public static final Supplier<GlyphSpell> LIGHTNING = register("lightning", (id)->new LightningSpell(id, 20 * 15)); // 15 seconds
	public static final Supplier<GlyphSpell> FIREBALL = FireballSpell::new;
	public static final Supplier<GlyphSpell> BONEMEAL = BonemealSpell::new;
	public static final Supplier<GlyphSpell> CONTAGION_BLAST = ContagionBlastSpell::new;
	public static final Supplier<GlyphSpell> FORTITUDE = FortitudeSpell::new;
	public static final Supplier<GlyphSpell> UPDRAFT = UpdraftSpell::new;
	public static final Supplier<GlyphSpell> INSTANT_WALL = register("instant_wall", InstantWallSpell::new);
	public static final Supplier<GlyphSpell> INSTANT_SHELTER = register("instant_shelter", InstantShelterSpell::new);
	public static final Supplier<GlyphSpell> TERRAIN_FLATTEN_SMALL = register("terrain_flatten_small", (id)->new TerrainFlattenSpell(id, 8));
	public static final Supplier<GlyphSpell> TERRAIN_FLATTEN_LARGE = register("terrain_flatten_large", (id)->new TerrainFlattenSpell(id, 16));
	
	private static <T extends PersistentGlyphSpell> Supplier<GlyphSpell> register(String registryName, Function<ResourceLocation, T> factory) {
		return register(SoulHarvestMod.id(registryName), factory);
	}
	
	private static <T extends PersistentGlyphSpell> Supplier<GlyphSpell> register(ResourceLocation registryName, Function<ResourceLocation, T> factory) {
		SpellThinker.registerEffect(registryName, () -> factory.apply(registryName));
		return () -> factory.apply(registryName);
	}
}
