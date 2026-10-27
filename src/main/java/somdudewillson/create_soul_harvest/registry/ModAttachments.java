package somdudewillson.create_soul_harvest.registry;

//? neoforge {
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.chunk.AbsoluteIceChunkData;

public class ModAttachments {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SoulHarvestMod.MOD_ID);

	public static final Supplier<AttachmentType<AbsoluteIceChunkData>> ABSOLUTE_ICE_CHUNK_DATA =
			ATTACHMENT_TYPES.register("absolute_ice_chunk_data", () -> AttachmentType.serializable(
					(IAttachmentHolder holder) -> new AbsoluteIceChunkData()
			).build());

	public static void register(IEventBus modBus) {
		ATTACHMENT_TYPES.register(modBus);
	}
}
//?}
