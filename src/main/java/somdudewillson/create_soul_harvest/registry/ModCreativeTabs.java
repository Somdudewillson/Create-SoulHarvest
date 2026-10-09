package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
//? neoforge {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} forge {
/*import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
*///?}
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class ModCreativeTabs {

	//? neoforge {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.create_soul_harvest"))
					.icon(() -> new ItemStack(ModItems.SOUL_SHARD.get()))
					.displayItems((parameters, output) -> {
						// Blocks
						output.accept(ModItems.SOUL_BUD_SMALL.get());
						output.accept(ModItems.SOUL_BUD_MEDIUM.get());
						output.accept(ModItems.SOUL_BUD_LARGE.get());
						output.accept(ModItems.SOUL_CLUSTER.get());
						output.accept(ModItems.SOUL_CRYSTAL_BLOCK.get());
						output.accept(ModItems.BIOFUEL_BLOCK.get());
						output.accept(ModItems.CARVED_BONE_BLOCK.get());
						output.accept(ModItems.ABSOLUTE_ICE.get());
						output.accept(ModItems.GOLD_FOAM.get());
						output.accept(ModItems.MIDAS_CORE.get());
						output.accept(ModItems.MAGIC_GLASS.get());

						// Materials & Fuels
						output.accept(ModItems.ORGANIC_MULCH.get());
						output.accept(ModItems.BIOFUEL_CHUNK.get());
						output.accept(ModItems.BLANK_TABLET.get());
						output.accept(ModItems.INLAID_LAPIS.get());
						output.accept(ModItems.POLISHED_INLAID_LAPIS.get());
						output.accept(ModItems.ENCHANTED_INLAID_LAPIS.get());
						output.accept(ModItems.MOLTEN_LAPIS_CORE.get());
						output.accept(ModItems.MAGIC_GOLD.get());
						output.accept(ModItems.SOUL_DEPOSIT_SEED.get());
						output.accept(ModItems.SOUL_SHARD.get());
						output.accept(ModItems.POLYCLAY.get());
						output.accept(ModItems.LOOT_TOKEN_BLANK.get());
						output.accept(ModItems.LOOT_TOKEN.get());
						output.accept(ModItems.BIOCIDE_GAS_BOTTLE.get());

						// Soul Books
						output.accept(ModItems.SOUL_BOOK_SIMPLE.get());
						output.accept(ModItems.SOUL_BOOK_STANDARD.get());
						output.accept(ModItems.SOUL_BOOK_SUPERIOR.get());

						// Glyphs
						output.accept(ModItems.GLYPH_1.get());
						output.accept(ModItems.GLYPH_2.get());
						output.accept(ModItems.GLYPH_3.get());
						output.accept(ModItems.GLYPH_4.get());
						output.accept(ModItems.GLYPH_5.get());
						output.accept(ModItems.GLYPH_6.get());
						output.accept(ModItems.GLYPH_7.get());
						output.accept(ModItems.GLYPH_8.get());

						// Glyph Scrolls
						output.accept(ModItems.GLYPH_SCROLL_1.get());
						output.accept(ModItems.GLYPH_SCROLL_2.get());
						output.accept(ModItems.GLYPH_SCROLL_3.get());
						output.accept(ModItems.GLYPH_SCROLL_4.get());
						output.accept(ModItems.GLYPH_SCROLL_5.get());
						output.accept(ModItems.GLYPH_SCROLL_6.get());
						output.accept(ModItems.GLYPH_SCROLL_7.get());
						output.accept(ModItems.GLYPH_SCROLL_8.get());
					})
					.build());
	//?} forge {
	/*public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.create_soul_harvest"))
					.icon(() -> new ItemStack(ModItems.SOUL_SHARD.get()))
					.displayItems((parameters, output) -> {
						// Blocks
						output.accept(ModItems.SOUL_BUD_SMALL.get());
						output.accept(ModItems.SOUL_BUD_MEDIUM.get());
						output.accept(ModItems.SOUL_BUD_LARGE.get());
						output.accept(ModItems.SOUL_CLUSTER.get());
						output.accept(ModItems.SOUL_CRYSTAL_BLOCK.get());
						output.accept(ModItems.BIOFUEL_BLOCK.get());
						output.accept(ModItems.CHARCOAL_BLOCK.get());
						output.accept(ModItems.CARVED_BONE_BLOCK.get());
						output.accept(ModItems.ANIMA_MOSS.get());
						output.accept(ModItems.ABSOLUTE_ICE.get());
						output.accept(ModItems.GOLD_FOAM.get());
						output.accept(ModItems.MIDAS_CORE.get());
						output.accept(ModItems.MAGIC_GLASS.get());

						// Materials & Fuels
						output.accept(ModItems.FUEL_PELLET.get());
						output.accept(ModItems.ORGANIC_MULCH.get());
						output.accept(ModItems.BIOFUEL_CHUNK.get());
						output.accept(ModItems.BLANK_TABLET.get());
						output.accept(ModItems.INLAID_LAPIS.get());
						output.accept(ModItems.POLISHED_INLAID_LAPIS.get());
						output.accept(ModItems.ENCHANTED_INLAID_LAPIS.get());
						output.accept(ModItems.MOLTEN_LAPIS_CORE.get());
						output.accept(ModItems.MAGIC_GOLD.get());
						output.accept(ModItems.SOUL_DEPOSIT_SEED.get());
						output.accept(ModItems.SOUL_SHARD.get());
						output.accept(ModItems.POLYCLAY.get());
						output.accept(ModItems.LOOT_TOKEN_BLANK.get());
						output.accept(ModItems.LOOT_TOKEN.get());
						output.accept(ModItems.BIOCIDE_GAS_BOTTLE.get());

						// Soul Books
						output.accept(ModItems.SOUL_BOOK_SIMPLE.get());
						output.accept(ModItems.SOUL_BOOK_STANDARD.get());
						output.accept(ModItems.SOUL_BOOK_SUPERIOR.get());

						// Glyphs
						output.accept(ModItems.GLYPH_1.get());
						output.accept(ModItems.GLYPH_2.get());
						output.accept(ModItems.GLYPH_3.get());
						output.accept(ModItems.GLYPH_4.get());
						output.accept(ModItems.GLYPH_5.get());
						output.accept(ModItems.GLYPH_6.get());
						output.accept(ModItems.GLYPH_7.get());
						output.accept(ModItems.GLYPH_8.get());

						// Glyph Scrolls
						output.accept(ModItems.GLYPH_SCROLL_1.get());
						output.accept(ModItems.GLYPH_SCROLL_2.get());
						output.accept(ModItems.GLYPH_SCROLL_3.get());
						output.accept(ModItems.GLYPH_SCROLL_4.get());
						output.accept(ModItems.GLYPH_SCROLL_5.get());
						output.accept(ModItems.GLYPH_SCROLL_6.get());
						output.accept(ModItems.GLYPH_SCROLL_7.get());
						output.accept(ModItems.GLYPH_SCROLL_8.get());
					})
					.build());
	*///?}
}
