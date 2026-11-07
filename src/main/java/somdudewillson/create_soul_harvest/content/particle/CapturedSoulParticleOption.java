package somdudewillson.create_soul_harvest.content.particle;

//? neoforge {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

public class CapturedSoulParticleOption implements ParticleOptions {

	public static final MapCodec<CapturedSoulParticleOption> CODEC = RecordCodecBuilder.mapCodec(inst ->
			inst.group(
					Codec.INT.fieldOf("target_id").forGetter(CapturedSoulParticleOption::getTargetEntityId)
			).apply(inst, CapturedSoulParticleOption::new)
	);

	public static final StreamCodec<ByteBuf, CapturedSoulParticleOption> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			CapturedSoulParticleOption::getTargetEntityId,
			CapturedSoulParticleOption::new
	);

	private final int targetEntityId;

	public CapturedSoulParticleOption(int targetEntityId) {
		this.targetEntityId = targetEntityId;
	}

	public CapturedSoulParticleOption() {
		this(-1);
	}

	public int getTargetEntityId() {
		return this.targetEntityId;
	}

	@Override
	public ParticleType<?> getType() {
		return ModParticleTypes.CAPTURED_SOUL.get();
	}
}
//?} forge {
/*import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;

public class CapturedSoulParticleOption implements ParticleOptions {

	public static final Codec<CapturedSoulParticleOption> CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
					Codec.INT.fieldOf("target_id").forGetter(CapturedSoulParticleOption::getTargetEntityId)
			).apply(inst, CapturedSoulParticleOption::new)
	);

	@SuppressWarnings("deprecation")
	public static final Deserializer<CapturedSoulParticleOption> DESERIALIZER = new Deserializer<>() {
		@Override
		public CapturedSoulParticleOption fromCommand(ParticleType<CapturedSoulParticleOption> type, StringReader reader) throws CommandSyntaxException {
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				return new CapturedSoulParticleOption(reader.readInt());
			}
			return new CapturedSoulParticleOption();
		}

		@Override
		public CapturedSoulParticleOption fromNetwork(ParticleType<CapturedSoulParticleOption> type, FriendlyByteBuf buffer) {
			return new CapturedSoulParticleOption(buffer.readVarInt());
		}
	};

	private final int targetEntityId;

	public CapturedSoulParticleOption(int targetEntityId) {
		this.targetEntityId = targetEntityId;
	}

	public CapturedSoulParticleOption() {
		this(-1);
	}

	public int getTargetEntityId() {
		return this.targetEntityId;
	}

	@Override
	public ParticleType<?> getType() {
		return ModParticleTypes.CAPTURED_SOUL.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.targetEntityId);
	}

	@Override
	public String writeToString() {
		return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + this.targetEntityId;
	}
}
*///?}
