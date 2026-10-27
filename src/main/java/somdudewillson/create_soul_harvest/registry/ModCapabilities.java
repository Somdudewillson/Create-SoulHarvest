package somdudewillson.create_soul_harvest.registry;

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.capability.ItemStackSoulHolder;

//? neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//?} forge {
/*import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.nbt.CompoundTag;
import somdudewillson.create_soul_harvest.content.chunk.AbsoluteIceChunkData;
*///?}

public class ModCapabilities {

	//? neoforge {
	public static final ItemCapability<ISoulHolder, Void> SOUL_HOLDER =
			ItemCapability.createVoid(SoulHarvestMod.id("soul_holder"), ISoulHolder.class);

	public static void register(IEventBus modBus) {
		modBus.addListener(ModCapabilities::onRegisterCapabilities);
	}

	private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(SOUL_HOLDER, (stack, ctx) -> new ItemStackSoulHolder(stack),
				ModItems.POLYCLAY_ATTUNED.get(),
				ModItems.POLYCLAY_ANDESITE_ATTUNED.get(),
				ModItems.POLYCLAY_BRASS_ATTUNED.get(),
				ModItems.EXPERIENCE_EFFIGY_ATTUNED.get(),
				ModItems.SOUL_BOOK_SIMPLE.get(),
				ModItems.SOUL_BOOK_STANDARD.get(),
				ModItems.SOUL_BOOK_SUPERIOR.get()
		);

		event.registerBlockEntity(
				Capabilities.FluidHandler.BLOCK,
				ModBlockEntities.MIDAS_CORE_BE.get(),
				(be, side) -> be.getFluidHandler()
		);
	}
	//?} forge {
	/*public static final Capability<ISoulHolder> SOUL_HOLDER =
			CapabilityManager.get(new CapabilityToken<>() {});

	public static final Capability<AbsoluteIceChunkData> ABSOLUTE_ICE_CHUNK_DATA =
			CapabilityManager.get(new CapabilityToken<>() {});

	public static void register(IEventBus modBus) {
		MinecraftForge.EVENT_BUS.register(ForgeCapabilityHandler.class);
	}

	public static class ForgeCapabilityHandler {
		@SubscribeEvent
		public static void onAttachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
			AbsoluteIceChunkData data = new AbsoluteIceChunkData();
			LazyOptional<AbsoluteIceChunkData> opt = LazyOptional.of(() -> data);
			event.addCapability(SoulHarvestMod.id("absolute_ice_chunk_data"), new ICapabilitySerializable<CompoundTag>() {
				@Override
				public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
					return cap == ABSOLUTE_ICE_CHUNK_DATA ? opt.cast() : LazyOptional.empty();
				}

				@Override
				public CompoundTag serializeNBT() {
					return data.serializeNBT();
				}

				@Override
				public void deserializeNBT(CompoundTag nbt) {
					data.deserializeNBT(nbt);
				}
			});
		}

		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
			ItemStack stack = event.getObject();
			if (stack.getItem() == ModItems.POLYCLAY_ATTUNED.get()
					|| stack.getItem() == ModItems.POLYCLAY_ANDESITE_ATTUNED.get()
					|| stack.getItem() == ModItems.POLYCLAY_BRASS_ATTUNED.get()
					|| stack.getItem() == ModItems.EXPERIENCE_EFFIGY_ATTUNED.get()
					|| stack.getItem() == ModItems.SOUL_BOOK_SIMPLE.get()
					|| stack.getItem() == ModItems.SOUL_BOOK_STANDARD.get()
					|| stack.getItem() == ModItems.SOUL_BOOK_SUPERIOR.get()) {
				ISoulHolder holder = new ItemStackSoulHolder(stack);
				LazyOptional<ISoulHolder> opt = LazyOptional.of(() -> holder);
				event.addCapability(SoulHarvestMod.id("soul_holder"), new ICapabilityProvider() {
					@Override
					public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
						return cap == SOUL_HOLDER ? opt.cast() : LazyOptional.empty();
					}
				});
			}
		}
	}
	*///?}
}
