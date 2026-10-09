package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
//? neoforge {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} forge {
/*import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?}
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.effect.ContagionMobEffect;

public class ModMobEffects {

	//? neoforge {
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<MobEffect, ContagionMobEffect> CONTAGION = MOB_EFFECTS.register("contagion", ContagionMobEffect::new);
	//?} forge {
	/*public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<ContagionMobEffect> CONTAGION = MOB_EFFECTS.register("contagion", ContagionMobEffect::new);
	*///?}
}
