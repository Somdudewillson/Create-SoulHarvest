package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

//? neoforge {
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
//?} forge {
/*import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
*///?}

public class ModParticleTypes {

	//? neoforge {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
			DeferredRegister.create(Registries.PARTICLE_TYPE, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFUSE_SOUL_FIRE =
			PARTICLE_TYPES.register("infuse_soul_fire", () -> new SimpleParticleType(false));

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STORM_CLOUD =
			PARTICLE_TYPES.register("storm_cloud", () -> new SimpleParticleType(false));
	//?} forge {
	/*public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
			DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<SimpleParticleType> INFUSE_SOUL_FIRE =
			PARTICLE_TYPES.register("infuse_soul_fire", () -> new SimpleParticleType(false));

	public static final RegistryObject<SimpleParticleType> STORM_CLOUD =
			PARTICLE_TYPES.register("storm_cloud", () -> new SimpleParticleType(false));
	*///?}
}
