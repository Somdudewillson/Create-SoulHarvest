package somdudewillson.create_soul_harvest.content.event;

import java.util.function.BiFunction;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.PolyclayAttunedItem;
import somdudewillson.create_soul_harvest.content.item.PolyclayItem;
import somdudewillson.create_soul_harvest.content.item.SoulBookItem;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;

@EventBusSubscriber(modid = SoulHarvestMod.MOD_ID, value = Dist.CLIENT)
public class TooltipProvisionEvents {
	@SubscribeEvent
	public static void provisionTooltips(final FMLClientSetupEvent event) {
		registerStandardTooltip(ModItems.NECRONIUM_CHARM);
		registerStandardTooltip(ModItems.NECRONIUM_WASTE);
		registerStandardTooltip(ModItems.NECRONIUM_WASTE_BOTTLE);
		registerStandardTooltip(ModItems.SOUL_DEPOSIT_SEED);
		registerStandardTooltipBlock(ModBlocks.MIDAS_CORE);
		registerStandardTooltipBlock(ModBlocks.ABSOLUTE_ICE);
		
		registerTooltip(ModItems.SOUL_BOOK_SIMPLE, FontHelper.Palette.STANDARD_CREATE, SoulBookItem.SoulBookTooltipModifier::new);
		registerTooltip(ModItems.SOUL_BOOK_STANDARD, FontHelper.Palette.STANDARD_CREATE, SoulBookItem.SoulBookTooltipModifier::new);
		registerTooltip(ModItems.SOUL_BOOK_SUPERIOR, FontHelper.Palette.STANDARD_CREATE, SoulBookItem.SoulBookTooltipModifier::new);
		
		registerTooltip(ModItems.POLYCLAY, FontHelper.Palette.STANDARD_CREATE, PolyclayItem.PolyclayTooltipModifier::new);
		registerTooltip(ModItems.POLYCLAY_ANDESITE, FontHelper.Palette.STANDARD_CREATE, PolyclayItem.PolyclayTooltipModifier::new);
		registerTooltip(ModItems.POLYCLAY_BRASS, FontHelper.Palette.STANDARD_CREATE, PolyclayItem.PolyclayTooltipModifier::new);
		
		registerTooltip(ModItems.POLYCLAY_ATTUNED, FontHelper.Palette.STANDARD_CREATE, PolyclayAttunedItem.PolyclayAttunedTooltipModifier::new);
		registerTooltip(ModItems.POLYCLAY_ANDESITE_ATTUNED, FontHelper.Palette.STANDARD_CREATE, PolyclayAttunedItem.PolyclayAttunedTooltipModifier::new);
		registerTooltip(ModItems.POLYCLAY_BRASS_ATTUNED, FontHelper.Palette.STANDARD_CREATE, PolyclayAttunedItem.PolyclayAttunedTooltipModifier::new);
	}
	
	protected static void registerStandardTooltipBlock(DeferredHolder<Block, ? extends Block> block) {
		registerStandardTooltipBlock(block.get());
	}

	protected static void registerStandardTooltipBlock(Block block) {
		registerStandardTooltip(block.asItem());
	}
	
	protected static void registerStandardTooltip(DeferredHolder<Item, ? extends Item> item) {
		registerStandardTooltip(item.get());
	}

	protected static void registerStandardTooltip(Item item) {
		TooltipModifier.REGISTRY.register(item, makeStandardTooltipModifier(item));
	}
	
	protected static void registerTooltip(
			DeferredHolder<Item, ? extends Item> item, 
			FontHelper.Palette palette, 
			BiFunction<FontHelper.Palette, Item, TooltipModifier> tooltipModifierFactory) {
		registerTooltip(item.get(), palette, tooltipModifierFactory);
	}

	protected static void registerTooltip(Item item, FontHelper.Palette palette, BiFunction<FontHelper.Palette, Item, TooltipModifier> tooltipModifierFactory) {
		TooltipModifier.REGISTRY.register(item, tooltipModifierFactory.apply(palette, item));
	}
	
	protected static TooltipModifier makeStandardTooltipModifier(Item item) {
		return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
	}
}
