package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.particle.CapturedSoulParticleOption;

//? neoforge {
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?} forge {
/*import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.mojang.serialization.Codec;
*///?}

public class ModParticleTypes {

	//? neoforge {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
			DeferredRegister.create(Registries.PARTICLE_TYPE, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFUSE_SOUL_FIRE =
			PARTICLE_TYPES.register("infuse_soul_fire", () -> new SimpleParticleType(false));

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STORM_CLOUD =
			PARTICLE_TYPES.register("storm_cloud", () -> new SimpleParticleType(true));

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VERDANT_FLASH =
			PARTICLE_TYPES.register("verdant_flash", () -> new SimpleParticleType(false));

	public static final DeferredHolder<ParticleType<?>, ParticleType<CapturedSoulParticleOption>> CAPTURED_SOUL =
			PARTICLE_TYPES.register("captured_soul", CapturedSoulParticleType::new);
	//?} forge {
	/*public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
			DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<SimpleParticleType> INFUSE_SOUL_FIRE =
			PARTICLE_TYPES.register("infuse_soul_fire", () -> new SimpleParticleType(false));

	public static final RegistryObject<SimpleParticleType> STORM_CLOUD =
			PARTICLE_TYPES.register("storm_cloud", () -> new SimpleParticleType(true));

	public static final RegistryObject<SimpleParticleType> VERDANT_FLASH =
			PARTICLE_TYPES.register("verdant_flash", () -> new SimpleParticleType(false));

	public static final RegistryObject<ParticleType<CapturedSoulParticleOption>> CAPTURED_SOUL =
			PARTICLE_TYPES.register("captured_soul", CapturedSoulParticleType::new);
	*///?}

	public static class CapturedSoulParticleType extends ParticleType<CapturedSoulParticleOption> {
		//? neoforge {
		public CapturedSoulParticleType() {
			super(false);
		}

		@Override
		public MapCodec<CapturedSoulParticleOption> codec() {
			return CapturedSoulParticleOption.CODEC;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, CapturedSoulParticleOption> streamCodec() {
			return CapturedSoulParticleOption.STREAM_CODEC;
		}
		//?} forge {
		/*public CapturedSoulParticleType() {
			super(false, CapturedSoulParticleOption.DESERIALIZER);
		}

		@Override
		public Codec<CapturedSoulParticleOption> codec() {
			return CapturedSoulParticleOption.CODEC;
		}
		*///?}
	}
}
