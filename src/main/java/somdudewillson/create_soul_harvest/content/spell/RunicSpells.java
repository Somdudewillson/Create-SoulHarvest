package somdudewillson.create_soul_harvest.content.spell;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PlanetoidSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.BonemealSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.EnhancedBonemealSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.EnhancedBonemealThinkerSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.ContagionBlastSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.ContagionCarrierSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.FireballSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.FireboltSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.FortitudeSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.InstantShelterSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.InstantWallSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.LightningSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.SparkSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.TerrainFlattenSpell;
import somdudewillson.create_soul_harvest.content.spell.spells.UpdraftSpell;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;

public class RunicSpells {
	public static final Supplier<GlyphSpell> SPARK = register("spark", SparkSpell::new);
	public static final Supplier<GlyphSpell> LIGHTNING = register("lightning", (id)->new LightningSpell(id, 20 * 15)); // 15 seconds
	public static final Supplier<GlyphSpell> FIREBOLT = FireboltSpell::new;
	public static final Supplier<GlyphSpell> FIREBALL = FireballSpell::new;
	public static final Supplier<GlyphSpell> BONEMEAL = ()->new BonemealSpell(8);
	public static final Supplier<GlyphSpell> BONEMEAL_ENHANCED = register(EnhancedBonemealThinkerSpell.ID, (id) -> new EnhancedBonemealThinkerSpell(id), EnhancedBonemealSpell::new);
	public static final Supplier<GlyphSpell> CONTAGION_BLAST = ContagionBlastSpell::new;
	public static final Supplier<GlyphSpell> CONTAGION_ENHANCED = ContagionCarrierSpell::new;
	public static final Supplier<GlyphSpell> FORTITUDE = ()->new FortitudeSpell(false);
	public static final Supplier<GlyphSpell> FORTITUDE_ENHANCED = ()->new FortitudeSpell(true);
	//? >= 1.21 {
	public static final Supplier<GlyphSpell> UPDRAFT = () -> new UpdraftSpell(7.0, ()->new MobEffectInstance(MobEffects.SLOW_FALLING, 20*25, 0));
	public static final Supplier<GlyphSpell> UPDRAFT_ENHANCED = () -> new UpdraftSpell(7.0, ()->new MobEffectInstance(ModMobEffects.AGILE_FALLING, 20*35, 0));
	//?} else {
	/*public static final Supplier<GlyphSpell> UPDRAFT = () -> new UpdraftSpell(7.0, ()->new MobEffectInstance(MobEffects.SLOW_FALLING, 20*25, 0));
	public static final Supplier<GlyphSpell> UPDRAFT_ENHANCED = () -> new UpdraftSpell(7.0, ()->new MobEffectInstance(ModMobEffects.AGILE_FALLING.get(), 20*35, 0));
	*///?}
	public static final Supplier<GlyphSpell> INSTANT_WALL = register("instant_wall", InstantWallSpell::new);
	public static final Supplier<GlyphSpell> INSTANT_SHELTER = register("instant_shelter", InstantShelterSpell::new);
	public static final Supplier<GlyphSpell> TERRAIN_FLATTEN_SMALL = register("terrain_flatten_small", (id)->new TerrainFlattenSpell(id, 8));
	public static final Supplier<GlyphSpell> TERRAIN_FLATTEN_LARGE = register("terrain_flatten_large", (id)->new TerrainFlattenSpell(id, 16));
	public static final Supplier<GlyphSpell> PLANETOID = register("planetoid", PlanetoidSpell::new);
	
	private static <T extends PersistentGlyphSpell> Supplier<GlyphSpell> register(ResourceLocation registryName, Function<ResourceLocation, T> thinkerFactory, Supplier<GlyphSpell> castableSpellSupplier) {
		SpellThinkerEntity.registerEffect(registryName, () -> thinkerFactory.apply(registryName));
		return castableSpellSupplier;
	}

	private static <T extends PersistentGlyphSpell> Supplier<GlyphSpell> register(String registryName, Function<ResourceLocation, T> factory) {
		return register(SoulHarvestMod.id(registryName), factory);
	}
	
	private static <T extends PersistentGlyphSpell> Supplier<GlyphSpell> register(ResourceLocation registryName, Function<ResourceLocation, T> factory) {
		SpellThinkerEntity.registerEffect(registryName, () -> factory.apply(registryName));
		return () -> factory.apply(registryName);
	}
}
