package somdudewillson.create_soul_harvest.content.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import lombok.RequiredArgsConstructor;

//? >= 1.21 {
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
//?}

import java.util.Locale;

@RequiredArgsConstructor
public enum OutputSelectionMode implements StringRepresentable {
	INDEPENDENT("independent"),
	SINGLE("single");

	public static final Codec<OutputSelectionMode> CODEC = StringRepresentable.fromEnum(OutputSelectionMode::values);

	//? >= 1.21 {
	public static final StreamCodec<ByteBuf, OutputSelectionMode> STREAM_CODEC =
			ByteBufCodecs.idMapper(i -> OutputSelectionMode.values()[i], OutputSelectionMode::ordinal);
	//?}

	private final String name;

	@Override
	public String getSerializedName() {
		return name;
	}

	public static OutputSelectionMode fromString(String name) {
		if (name == null) {
			return INDEPENDENT;
		}
		try {
			return OutputSelectionMode.valueOf(name.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			for (OutputSelectionMode mode : values()) {
				if (mode.name.equalsIgnoreCase(name)) {
					return mode;
				}
			}
			return INDEPENDENT;
		}
	}
}
