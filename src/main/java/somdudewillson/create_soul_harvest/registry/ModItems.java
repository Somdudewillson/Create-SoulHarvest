package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
//? neoforge {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} forge {
/*import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?}
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.*;

public class ModItems {

	//? neoforge {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, SoulHarvestMod.MOD_ID);

	// Block Items
	public static final DeferredHolder<Item, BlockItem> SOUL_BUD_SMALL = ITEMS.register("soul_bud_small",
			() -> new BlockItem(ModBlocks.SOUL_BUD_SMALL.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> SOUL_BUD_MEDIUM = ITEMS.register("soul_bud_medium",
			() -> new BlockItem(ModBlocks.SOUL_BUD_MEDIUM.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> SOUL_BUD_LARGE = ITEMS.register("soul_bud_large",
			() -> new BlockItem(ModBlocks.SOUL_BUD_LARGE.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> SOUL_CLUSTER = ITEMS.register("soul_cluster",
			() -> new BlockItem(ModBlocks.SOUL_CLUSTER.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> SOUL_CRYSTAL_BLOCK = ITEMS.register("soul_crystal_block",
			() -> new BlockItem(ModBlocks.SOUL_CRYSTAL_BLOCK.get(), new Item.Properties()));
	public static final DeferredHolder<Item, FuelBlockItem> BIOFUEL_BLOCK = ITEMS.register("biofuel_block",
			() -> new FuelBlockItem(ModBlocks.BIOFUEL_BLOCK.get(), 16000, new Item.Properties()));
	public static final DeferredHolder<Item, FuelBlockItem> CHARCOAL_BLOCK = ITEMS.register("charcoal_block",
			() -> new FuelBlockItem(ModBlocks.CHARCOAL_BLOCK.get(), 16000, new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> CARVED_BONE_BLOCK = ITEMS.register("carved_bone_block",
			() -> new BlockItem(ModBlocks.CARVED_BONE_BLOCK.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> ANIMA_MOSS = ITEMS.register("anima_moss",
			() -> new BlockItem(ModBlocks.ANIMA_MOSS.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> ABSOLUTE_ICE = ITEMS.register("absolute_ice",
			() -> new BlockItem(ModBlocks.ABSOLUTE_ICE.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> GOLD_FOAM = ITEMS.register("gold_foam",
			() -> new BlockItem(ModBlocks.GOLD_FOAM.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> MIDAS_CORE = ITEMS.register("midas_core",
			() -> new BlockItem(ModBlocks.MIDAS_CORE.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> MAGIC_GLASS = ITEMS.register("magic_glass",
			() -> new BlockItem(ModBlocks.MAGIC_GLASS.get(), new Item.Properties()));

	// Fuels and organics
	public static final DeferredHolder<Item, FuelItem> FUEL_PELLET = ITEMS.register("fuel_pellet",
			() -> new FuelItem(200, new Item.Properties()));
	public static final DeferredHolder<Item, Item> ORGANIC_MULCH = ITEMS.register("organic_mulch",
			() -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, FuelItem> BIOFUEL_CHUNK = ITEMS.register("biofuel_chunk",
			() -> new FuelItem(1600, new Item.Properties()));

	// Lapis and magic materials
	public static final DeferredHolder<Item, Item> BLANK_TABLET = ITEMS.register("blank_tablet",
			() -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> INLAID_LAPIS = ITEMS.register("inlaid_lapis",
			() -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> POLISHED_INLAID_LAPIS = ITEMS.register("polished_inlaid_lapis",
			() -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ENCHANTED_INLAID_LAPIS = ITEMS.register("enchanted_inlaid_lapis",
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			});
	public static final DeferredHolder<Item, FuelItem> MOLTEN_LAPIS_CORE = ITEMS.register("molten_lapis_core",
			() -> new FuelItem(20000, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, Item> MAGIC_GOLD = ITEMS.register("magic_gold",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			});

	// Soul Harvest items
	public static final DeferredHolder<Item, SoulDepositSeedItem> SOUL_DEPOSIT_SEED = ITEMS.register("soul_deposit_seed",
			() -> new SoulDepositSeedItem(new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, Item> SOUL_SHARD = ITEMS.register("soul_shard",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			});
	public static final DeferredHolder<Item, PolyclayItem> POLYCLAY = ITEMS.register("polyclay",
			() -> new PolyclayItem(new Item.Properties()));

	// Tokens and hazardous items
	public static final DeferredHolder<Item, Item> LOOT_TOKEN_BLANK = ITEMS.register("loot_token_blank",
			() -> new Item(new Item.Properties().stacksTo(1).fireResistant()));
	public static final DeferredHolder<Item, LootTokenItem> LOOT_TOKEN = ITEMS.register("loot_token",
			() -> new LootTokenItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, BiocideGasBottleItem> BIOCIDE_GAS_BOTTLE = ITEMS.register("biocide_gas_bottle",
			() -> new BiocideGasBottleItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON).craftRemainder(Items.GLASS_BOTTLE)));

	// Soul Books
	public static final DeferredHolder<Item, SoulBookItem> SOUL_BOOK_SIMPLE = ITEMS.register("soul_book_simple",
			() -> new SoulBookItem(1, new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, SoulBookItem> SOUL_BOOK_STANDARD = ITEMS.register("soul_book_standard",
			() -> new SoulBookItem(2, new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, SoulBookItem> SOUL_BOOK_SUPERIOR = ITEMS.register("soul_book_superior",
			() -> new SoulBookItem(3, new Item.Properties().stacksTo(1)));

	// Glyphs
	public static final DeferredHolder<Item, Item> GLYPH_1 = ITEMS.register("glyph_1", () -> createGlyphPage());
	public static final DeferredHolder<Item, Item> GLYPH_2 = ITEMS.register("glyph_2", () -> createGlyphPage());
	public static final DeferredHolder<Item, Item> GLYPH_3 = ITEMS.register("glyph_3", () -> createGlyphPage());
	public static final DeferredHolder<Item, Item> GLYPH_4 = ITEMS.register("glyph_4", () -> createGlyphPage());
	public static final DeferredHolder<Item, Item> GLYPH_5 = ITEMS.register("glyph_5", () -> createGlyphPage());
	public static final DeferredHolder<Item, Item> GLYPH_6 = ITEMS.register("glyph_6", () -> createGlyphPage());
	public static final DeferredHolder<Item, Item> GLYPH_7 = ITEMS.register("glyph_7", () -> createGlyphPage());
	public static final DeferredHolder<Item, Item> GLYPH_8 = ITEMS.register("glyph_8", () -> createGlyphPage());

	// Glyph Scrolls
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_1 = ITEMS.register("glyph_scroll_1", () -> new GlyphScrollItem(1, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_2 = ITEMS.register("glyph_scroll_2", () -> new GlyphScrollItem(2, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_3 = ITEMS.register("glyph_scroll_3", () -> new GlyphScrollItem(3, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_4 = ITEMS.register("glyph_scroll_4", () -> new GlyphScrollItem(4, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_5 = ITEMS.register("glyph_scroll_5", () -> new GlyphScrollItem(5, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_6 = ITEMS.register("glyph_scroll_6", () -> new GlyphScrollItem(6, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_7 = ITEMS.register("glyph_scroll_7", () -> new GlyphScrollItem(7, new Item.Properties().stacksTo(16)));
	public static final DeferredHolder<Item, GlyphScrollItem> GLYPH_SCROLL_8 = ITEMS.register("glyph_scroll_8", () -> new GlyphScrollItem(8, new Item.Properties().stacksTo(16)));
	//?} forge {
	/*public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SoulHarvestMod.MOD_ID);

	// Block Items
	public static final RegistryObject<BlockItem> SOUL_BUD_SMALL = ITEMS.register("soul_bud_small",
			() -> new BlockItem(ModBlocks.SOUL_BUD_SMALL.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> SOUL_BUD_MEDIUM = ITEMS.register("soul_bud_medium",
			() -> new BlockItem(ModBlocks.SOUL_BUD_MEDIUM.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> SOUL_BUD_LARGE = ITEMS.register("soul_bud_large",
			() -> new BlockItem(ModBlocks.SOUL_BUD_LARGE.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> SOUL_CLUSTER = ITEMS.register("soul_cluster",
			() -> new BlockItem(ModBlocks.SOUL_CLUSTER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> SOUL_CRYSTAL_BLOCK = ITEMS.register("soul_crystal_block",
			() -> new BlockItem(ModBlocks.SOUL_CRYSTAL_BLOCK.get(), new Item.Properties()));
	public static final RegistryObject<FuelBlockItem> BIOFUEL_BLOCK = ITEMS.register("biofuel_block",
			() -> new FuelBlockItem(ModBlocks.BIOFUEL_BLOCK.get(), 16000, new Item.Properties()));
	public static final RegistryObject<FuelBlockItem> CHARCOAL_BLOCK = ITEMS.register("charcoal_block",
			() -> new FuelBlockItem(ModBlocks.CHARCOAL_BLOCK.get(), 16000, new Item.Properties()));
	public static final RegistryObject<BlockItem> CARVED_BONE_BLOCK = ITEMS.register("carved_bone_block",
			() -> new BlockItem(ModBlocks.CARVED_BONE_BLOCK.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> ANIMA_MOSS = ITEMS.register("anima_moss",
			() -> new BlockItem(ModBlocks.ANIMA_MOSS.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> ABSOLUTE_ICE = ITEMS.register("absolute_ice",
			() -> new BlockItem(ModBlocks.ABSOLUTE_ICE.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> GOLD_FOAM = ITEMS.register("gold_foam",
			() -> new BlockItem(ModBlocks.GOLD_FOAM.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> MIDAS_CORE = ITEMS.register("midas_core",
			() -> new BlockItem(ModBlocks.MIDAS_CORE.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> MAGIC_GLASS = ITEMS.register("magic_glass",
			() -> new BlockItem(ModBlocks.MAGIC_GLASS.get(), new Item.Properties()));

	// Fuels and organics
	public static final RegistryObject<FuelItem> FUEL_PELLET = ITEMS.register("fuel_pellet",
			() -> new FuelItem(200, new Item.Properties()));
	public static final RegistryObject<Item> ORGANIC_MULCH = ITEMS.register("organic_mulch",
			() -> new Item(new Item.Properties()));
	public static final RegistryObject<FuelItem> BIOFUEL_CHUNK = ITEMS.register("biofuel_chunk",
			() -> new FuelItem(1600, new Item.Properties()));

	// Lapis and magic materials
	public static final RegistryObject<Item> BLANK_TABLET = ITEMS.register("blank_tablet",
			() -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> INLAID_LAPIS = ITEMS.register("inlaid_lapis",
			() -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> POLISHED_INLAID_LAPIS = ITEMS.register("polished_inlaid_lapis",
			() -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> ENCHANTED_INLAID_LAPIS = ITEMS.register("enchanted_inlaid_lapis",
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			});
	public static final RegistryObject<FuelItem> MOLTEN_LAPIS_CORE = ITEMS.register("molten_lapis_core",
			() -> new FuelItem(20000, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<Item> MAGIC_GOLD = ITEMS.register("magic_gold",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			});

	// Soul Harvest items
	public static final RegistryObject<SoulDepositSeedItem> SOUL_DEPOSIT_SEED = ITEMS.register("soul_deposit_seed",
			() -> new SoulDepositSeedItem(new Item.Properties().stacksTo(16)));
	public static final RegistryObject<Item> SOUL_SHARD = ITEMS.register("soul_shard",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			});
	public static final RegistryObject<PolyclayItem> POLYCLAY = ITEMS.register("polyclay",
			() -> new PolyclayItem(new Item.Properties()));

	// Tokens and hazardous items
	public static final RegistryObject<Item> LOOT_TOKEN_BLANK = ITEMS.register("loot_token_blank",
			() -> new Item(new Item.Properties().stacksTo(1).fireResistant()));
	public static final RegistryObject<LootTokenItem> LOOT_TOKEN = ITEMS.register("loot_token",
			() -> new LootTokenItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<BiocideGasBottleItem> BIOCIDE_GAS_BOTTLE = ITEMS.register("biocide_gas_bottle",
			() -> new BiocideGasBottleItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON).craftRemainder(Items.GLASS_BOTTLE)));

	// Soul Books
	public static final RegistryObject<SoulBookItem> SOUL_BOOK_SIMPLE = ITEMS.register("soul_book_simple",
			() -> new SoulBookItem(1, new Item.Properties().stacksTo(1)));
	public static final RegistryObject<SoulBookItem> SOUL_BOOK_STANDARD = ITEMS.register("soul_book_standard",
			() -> new SoulBookItem(2, new Item.Properties().stacksTo(1)));
	public static final RegistryObject<SoulBookItem> SOUL_BOOK_SUPERIOR = ITEMS.register("soul_book_superior",
			() -> new SoulBookItem(3, new Item.Properties().stacksTo(1)));

	// Glyphs
	public static final RegistryObject<Item> GLYPH_1 = ITEMS.register("glyph_1", () -> createGlyphPage());
	public static final RegistryObject<Item> GLYPH_2 = ITEMS.register("glyph_2", () -> createGlyphPage());
	public static final RegistryObject<Item> GLYPH_3 = ITEMS.register("glyph_3", () -> createGlyphPage());
	public static final RegistryObject<Item> GLYPH_4 = ITEMS.register("glyph_4", () -> createGlyphPage());
	public static final RegistryObject<Item> GLYPH_5 = ITEMS.register("glyph_5", () -> createGlyphPage());
	public static final RegistryObject<Item> GLYPH_6 = ITEMS.register("glyph_6", () -> createGlyphPage());
	public static final RegistryObject<Item> GLYPH_7 = ITEMS.register("glyph_7", () -> createGlyphPage());
	public static final RegistryObject<Item> GLYPH_8 = ITEMS.register("glyph_8", () -> createGlyphPage());

	// Glyph Scrolls
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_1 = ITEMS.register("glyph_scroll_1", () -> new GlyphScrollItem(1, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_2 = ITEMS.register("glyph_scroll_2", () -> new GlyphScrollItem(2, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_3 = ITEMS.register("glyph_scroll_3", () -> new GlyphScrollItem(3, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_4 = ITEMS.register("glyph_scroll_4", () -> new GlyphScrollItem(4, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_5 = ITEMS.register("glyph_scroll_5", () -> new GlyphScrollItem(5, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_6 = ITEMS.register("glyph_scroll_6", () -> new GlyphScrollItem(6, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_7 = ITEMS.register("glyph_scroll_7", () -> new GlyphScrollItem(7, new Item.Properties().stacksTo(16)));
	public static final RegistryObject<GlyphScrollItem> GLYPH_SCROLL_8 = ITEMS.register("glyph_scroll_8", () -> new GlyphScrollItem(8, new Item.Properties().stacksTo(16)));
	*///?}

	private static Item createGlyphPage() {
		return new Item(new Item.Properties()) {
			@Override
			public boolean isFoil(ItemStack stack) {
				return true;
			}
		};
	}
}
