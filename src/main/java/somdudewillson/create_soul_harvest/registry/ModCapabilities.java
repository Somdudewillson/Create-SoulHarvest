package somdudewillson.create_soul_harvest.registry;

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.content.capability.ItemStackSoulHolder;

//? neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//?} forge {
/*import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
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
				ModItems.ATTUNED_POLYCLAY.get(),
				ModItems.SOUL_BOOK_SIMPLE.get(),
				ModItems.SOUL_BOOK_STANDARD.get(),
				ModItems.SOUL_BOOK_SUPERIOR.get()
		);
	}
	//?} forge {
	/*public static final Capability<ISoulHolder> SOUL_HOLDER =
			CapabilityManager.get(new CapabilityToken<>() {});

	public static void register(IEventBus modBus) {
		MinecraftForge.EVENT_BUS.register(ForgeCapabilityHandler.class);
	}

	public static class ForgeCapabilityHandler {
		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
			ItemStack stack = event.getObject();
			if (stack.getItem() == ModItems.ATTUNED_POLYCLAY.get()
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
