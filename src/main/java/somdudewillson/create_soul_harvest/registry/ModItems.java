package somdudewillson.create_soul_harvest.registry;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import somdudewillson.redsparklib.common.data.builder.ItemRegistryBuilder.ItemRegistryBuilderFactory;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.*;
import somdudewillson.create_soul_harvest.content.spell.RunicSpells;

//? forge {
/*@SuppressWarnings("deprecation")
*///?}
public class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, SoulHarvestMod.MOD_ID);
	public static final ItemRegistryBuilderFactory ITEM_BUILDER_FACTORY = new ItemRegistryBuilderFactory(SoulHarvestMod.MOD_ID, ITEMS);

	private static <T extends Item> DeferredHolder<Item, T> registerWithTab(String name, java.util.function.Supplier<T> supplier) {
		DeferredHolder<Item, T> holder = ITEMS.register(name, supplier);
		ModCreativeTabs.addToMainTab(holder);
		return holder;
	}

	// Block Items (simple blocks with block items are registered via BlockRegistryBuilder in ModBlocks)
	public static final DeferredHolder<Item, BlockItem> SOUL_BUD_SMALL = registerWithTab("soul_bud_small",
			() -> new BlockItem(ModBlocks.SOUL_BUD_SMALL.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> SOUL_BUD_MEDIUM = registerWithTab("soul_bud_medium",
			() -> new BlockItem(ModBlocks.SOUL_BUD_MEDIUM.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> SOUL_BUD_LARGE = registerWithTab("soul_bud_large",
			() -> new BlockItem(ModBlocks.SOUL_BUD_LARGE.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> SOUL_CLUSTER = registerWithTab("soul_cluster",
			() -> new BlockItem(ModBlocks.SOUL_CLUSTER.get(), new Item.Properties()));
	public static final DeferredHolder<Item, FuelBlockItem> BIOFUEL_BLOCK = registerWithTab("biofuel_block",
			() -> new FuelBlockItem(ModBlocks.BIOFUEL_BLOCK.get(), 16000, new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> CARVED_BONE_BLOCK = registerWithTab("carved_bone_block",
			() -> new BlockItem(ModBlocks.CARVED_BONE_BLOCK.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> MAGIC_GLASS = registerWithTab("magic_glass",
			() -> new BlockItem(ModBlocks.MAGIC_GLASS.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> DENSE_HYPOTHETICAL = registerWithTab("dense_hypothetical",
			() -> new BlockItem(ModBlocks.DENSE_HYPOTHETICAL.get(), new Item.Properties()));
	public static final DeferredHolder<Item, BucketItem> LIQUID_SOUL_CRYSTAL_BUCKET = registerWithTab("liquid_soul_crystal_bucket",
			() -> new BucketItem(ModFluids.LIQUID_SOUL_CRYSTAL.get(),
					new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

	// Fuels and organics
	public static final DeferredHolder<Item, Item> ORGANIC_MULCH = ITEM_BUILDER_FACTORY.registerSimpleItem("organic_mulch")
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> BIOFUEL_CHUNK = ITEM_BUILDER_FACTORY.registerItem("biofuel_chunk", props -> new FuelItem(1600, props))
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();

	// Lapis and magic materials
	public static final DeferredHolder<Item, Item> BLANK_TABLET = registerWithTab("blank_tablet",
			() -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> INLAID_LAPIS = ITEM_BUILDER_FACTORY.registerSimpleItem("inlaid_lapis")
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> INLAID_LAPIS_PLATE = ITEM_BUILDER_FACTORY.registerSimpleItem("inlaid_lapis_plate")
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> POLISHED_INLAID_LAPIS = ITEM_BUILDER_FACTORY.registerSimpleItem("polished_inlaid_lapis")
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> ENCHANTED_INLAID_LAPIS = registerWithTab("enchanted_inlaid_lapis",
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			});
	public static final DeferredHolder<Item, Item> MOLTEN_LAPIS_CORE = ITEM_BUILDER_FACTORY.registerItem("molten_lapis_core", props -> new FuelItem(20000, props))
			.properties(new Item.Properties().stacksTo(16))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> MAGIC_GOLD = ITEM_BUILDER_FACTORY.registerItem("magic_gold",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			})
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> MAGIC_GOLD_CHUNK = ITEM_BUILDER_FACTORY.registerItem("magic_gold_chunk",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			})
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> MAGIC_GOLD_STRING = ITEM_BUILDER_FACTORY.registerItem("magic_gold_string",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			})
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();

	// Soul Harvest items
	public static final DeferredHolder<Item, Item> SOUL_DEPOSIT_SEED = ITEM_BUILDER_FACTORY.registerItem("soul_deposit_seed", SoulDepositSeedItem::new)
			.properties(new Item.Properties().stacksTo(16))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_SHARD = ITEM_BUILDER_FACTORY.registerItem("soul_shard",
			() -> new Item(new Item.Properties()) {
				@Override public boolean isFoil(ItemStack stack) { return true; }
			})
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	
	// Polyclays
	public static final DeferredHolder<Item, Item> POLYCLAY = ITEM_BUILDER_FACTORY.registerItem("polyclay",
			props -> new PolyclayItem(0, ModItems.POLYCLAY_INCOMPLETE, ModItems.POLYCLAY_ATTUNED, props))
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> POLYCLAY_INCOMPLETE = ITEM_BUILDER_FACTORY.registerItem("polyclay_attuned_incomplete", SequencedAssemblyItem::new)
			.properties(new Item.Properties())
			.simpleItem()
			.build();
	public static final DeferredHolder<Item, Item> POLYCLAY_ATTUNED = ITEM_BUILDER_FACTORY.registerItem("polyclay_attuned",
			props -> new PolyclayAttunedItem(0, props))
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	
	public static final DeferredHolder<Item, Item> POLYCLAY_ANDESITE = ITEM_BUILDER_FACTORY.registerItem("polyclay_andesite",
			props -> new PolyclayItem(1, ModItems.POLYCLAY_ANDESITE_INCOMPLETE, ModItems.POLYCLAY_ANDESITE_ATTUNED, props))
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> POLYCLAY_ANDESITE_INCOMPLETE = ITEM_BUILDER_FACTORY.registerItem("polyclay_andesite_attuned_incomplete", SequencedAssemblyItem::new)
			.properties(new Item.Properties())
			.simpleItem()
			.build();
	public static final DeferredHolder<Item, Item> POLYCLAY_ANDESITE_ATTUNED = ITEM_BUILDER_FACTORY.registerItem("polyclay_andesite_attuned",
			props -> new PolyclayAttunedItem(1, props))
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	
	public static final DeferredHolder<Item, Item> POLYCLAY_BRASS = ITEM_BUILDER_FACTORY.registerItem("polyclay_brass",
			props -> new PolyclayItem(2, ModItems.POLYCLAY_BRASS_INCOMPLETE, ModItems.POLYCLAY_BRASS_ATTUNED, props))
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> POLYCLAY_BRASS_INCOMPLETE = ITEM_BUILDER_FACTORY.registerItem("polyclay_brass_attuned_incomplete", SequencedAssemblyItem::new)
			.properties(new Item.Properties())
			.simpleItem()
			.build();
	public static final DeferredHolder<Item, Item> POLYCLAY_BRASS_ATTUNED = ITEM_BUILDER_FACTORY.registerItem("polyclay_brass_attuned",
			props -> new PolyclayAttunedItem(2, props))
			.properties(new Item.Properties())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();

	// Tokens and hazardous items
	public static final DeferredHolder<Item, Item> LOOT_TOKEN_BLANK = ITEM_BUILDER_FACTORY.registerSimpleItem("loot_token_blank")
			.properties(new Item.Properties().stacksTo(1).fireResistant())
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> LOOT_TOKEN = ITEM_BUILDER_FACTORY.registerItem("loot_token", LootTokenItem::new)
			.properties(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> BIOCIDE_GAS_BOTTLE = ITEM_BUILDER_FACTORY.registerItem("biocide_gas_bottle", BiocideGasBottleItem::new)
			.properties(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON).craftRemainder(Items.GLASS_BOTTLE))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();

	// Soul Books
	public static final DeferredHolder<Item, Item> SOUL_BOOK_COVER_SIMPLE = ITEM_BUILDER_FACTORY.registerSimpleItem("soul_book_cover_simple")
			.properties(new Item.Properties().stacksTo(16))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_COVER_STANDARD = ITEM_BUILDER_FACTORY.registerSimpleItem("soul_book_cover_standard")
			.properties(new Item.Properties().stacksTo(16))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_COVER_SUPERIOR = ITEM_BUILDER_FACTORY.registerSimpleItem("soul_book_cover_superior")
			.properties(new Item.Properties().stacksTo(16))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_INCOMPLETE_SIMPLE = ITEM_BUILDER_FACTORY.registerItem("soul_book_incomplete_simple", SequencedAssemblyItem::new)
			.properties(new Item.Properties())
			.simpleItem()
//			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_INCOMPLETE_STANDARD = ITEM_BUILDER_FACTORY.registerItem("soul_book_incomplete_standard", SequencedAssemblyItem::new)
			.properties(new Item.Properties().stacksTo(1))
			.simpleItem()
//			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_INCOMPLETE_SUPERIOR = ITEM_BUILDER_FACTORY.registerItem("soul_book_incomplete_superior", SequencedAssemblyItem::new)
			.properties(new Item.Properties().stacksTo(1))
			.simpleItem()
//			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_SIMPLE = ITEM_BUILDER_FACTORY.registerItem("soul_book_simple", props -> new SoulBookItem(1, props))
			.properties(new Item.Properties().stacksTo(1))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_STANDARD = ITEM_BUILDER_FACTORY.registerItem("soul_book_standard", props -> new SoulBookItem(2, props))
			.properties(new Item.Properties().stacksTo(1))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();
	public static final DeferredHolder<Item, Item> SOUL_BOOK_SUPERIOR = ITEM_BUILDER_FACTORY.registerItem("soul_book_superior", props -> new SoulBookItem(3, props))
			.properties(new Item.Properties().stacksTo(1))
			.simpleItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.build();

	public static SoulBookItem getSoulBook(int tier) {
		return switch (tier) {
			case 1 -> (SoulBookItem) SOUL_BOOK_SIMPLE.get();
			case 2 -> (SoulBookItem) SOUL_BOOK_STANDARD.get();
			case 3 -> (SoulBookItem) SOUL_BOOK_SUPERIOR.get();
			default -> null;
		};
	}

	// Glyphs
	public static final DeferredHolder<Item, Item> GLYPH_1 = ITEM_BUILDER_FACTORY.registerItem("glyph_1", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_2 = ITEM_BUILDER_FACTORY.registerItem("glyph_2", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_3 = ITEM_BUILDER_FACTORY.registerItem("glyph_3", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_4 = ITEM_BUILDER_FACTORY.registerItem("glyph_4", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_5 = ITEM_BUILDER_FACTORY.registerItem("glyph_5", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_6 = ITEM_BUILDER_FACTORY.registerItem("glyph_6", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_7 = ITEM_BUILDER_FACTORY.registerItem("glyph_7", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_8 = ITEM_BUILDER_FACTORY.registerItem("glyph_8", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_OVERLOADED = ITEM_BUILDER_FACTORY.registerItem("glyph_overloaded", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	
	public static final DeferredHolder<Item, Item> GLYPH_SHEAF = ITEM_BUILDER_FACTORY.registerItem("glyph_page_sheaf", ModItems::createGlyphPage).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();

	// Glyph Scrolls
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_1 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_1", props -> new GlyphScrollItem(RunicSpells.LIGHTNING, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_2 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_2", props -> new GlyphScrollItem(RunicSpells.FIREBALL, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_3 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_3", props -> new GlyphScrollItem(RunicSpells.BONEMEAL, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_4 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_4", props -> new GlyphScrollItem(RunicSpells.CONTAGION_BLAST, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_5 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_5", props -> new GlyphScrollItem(RunicSpells.FORTITUDE, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_6 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_6", props -> new GlyphScrollItem(RunicSpells.UPDRAFT, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_7 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_7", props -> new GlyphScrollItem(RunicSpells.INSTANT_WALL, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_8 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_8", props -> new GlyphScrollItem(RunicSpells.TERRAIN_FLATTEN_LARGE, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();

	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_1 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_1", props -> new GlyphScrollItem(RunicSpells.LIGHTNING, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_2 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_2", props -> new GlyphScrollItem(RunicSpells.FIREBALL, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_3 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_3", props -> new GlyphScrollItem(RunicSpells.BONEMEAL, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_4 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_4", props -> new GlyphScrollItem(RunicSpells.CONTAGION_BLAST, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_5 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_5", props -> new GlyphScrollItem(RunicSpells.FORTITUDE_ENHANCED, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_6 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_6", props -> new GlyphScrollItem(RunicSpells.UPDRAFT_ENHANCED, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_7 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_7", props -> new GlyphScrollItem(RunicSpells.INSTANT_SHELTER, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();
	public static final DeferredHolder<Item, Item> GLYPH_SCROLL_ENHANCED_8 = ITEM_BUILDER_FACTORY.registerItem("glyph_scroll_enhanced_8", props -> new GlyphScrollItem(RunicSpells.PLANETOID, props)).properties(new Item.Properties().stacksTo(16)).simpleItem().inCreativeTab(ModCreativeTabs::addToMainTab).build();


	private static Item createGlyphPage() {
		return new Item(new Item.Properties()) {
			@Override
			public boolean isFoil(ItemStack stack) {
				return true;
			}
		};
	}
}
