package somdudewillson.create_soul_harvest.registry;

import net.minecraft.world.effect.MobEffect;
//? neoforge {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;
//?} forge {
/*import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
*///?}

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.effect.AgileFallingMobEffect;
import somdudewillson.create_soul_harvest.content.effect.ContagionMobEffect;
import somdudewillson.create_soul_harvest.content.effect.StoneskinMobEffect;
import somdudewillson.create_soul_harvest.content.effect.VaccinationMobEffect;

public class ModMobEffects {

	//? neoforge {
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<MobEffect, ContagionMobEffect> CONTAGION = MOB_EFFECTS.register("contagion", ContagionMobEffect::new);
	public static final DeferredHolder<MobEffect, AgileFallingMobEffect> AGILE_FALLING = MOB_EFFECTS.register("agile_falling", AgileFallingMobEffect::new);
	public static final DeferredHolder<MobEffect, StoneskinMobEffect> STONESKIN = MOB_EFFECTS.register("stoneskin", StoneskinMobEffect::new);
	public static final DeferredHolder<MobEffect, VaccinationMobEffect> VACCINATION = MOB_EFFECTS.register("vaccination", VaccinationMobEffect::new);
	//?} forge {
	/*public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<ContagionMobEffect> CONTAGION = MOB_EFFECTS.register("contagion", ContagionMobEffect::new);
	public static final RegistryObject<AgileFallingMobEffect> AGILE_FALLING = MOB_EFFECTS.register("agile_falling", AgileFallingMobEffect::new);
	public static final RegistryObject<StoneskinMobEffect> STONESKIN = MOB_EFFECTS.register("stoneskin", StoneskinMobEffect::new);
	public static final RegistryObject<VaccinationMobEffect> VACCINATION = MOB_EFFECTS.register("vaccination", VaccinationMobEffect::new);
	*///?}
}
