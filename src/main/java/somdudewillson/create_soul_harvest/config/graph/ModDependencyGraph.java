package somdudewillson.create_soul_harvest.config.graph;

import net.minecraft.resources.ResourceLocation;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;

public class ModDependencyGraph {
	private static final ItemDependencyGraph GRAPH = new ItemDependencyGraph();
	
	public static final ResourceLocation SOUL_INFUSION_NODE = SoulHarvestMod.id("soul_infusion");
	public static final ResourceLocation SPELLS_T1_SHARED_NODE = SoulHarvestMod.id("spells_t1_ingredients");
	public static final ResourceLocation SPELLS_T2_SHARED_NODE = SoulHarvestMod.id("spells_t2_ingredients");
	
	public static final ResourceLocation MEKANISM_PRESENT_NODE = SoulHarvestMod.id("mekanism_present");

	public static ItemDependencyGraph get() {
		return GRAPH;
	}

	public static void init() {
		GRAPH.node(MEKANISM_PRESENT_NODE)
				.requiresMod("mekanism")
				.groups("compat", "compat_mekanism");
		
		// Biofuel group
		GRAPH.node(ModItems.ORGANIC_MULCH.getId())
				.groups("biofuel");

		GRAPH.node(ModItems.BIOFUEL_BLOCK.getId())
				.groups("biofuel")
				.requires(ModItems.ORGANIC_MULCH.getId());

		// Soul crystals group
		GRAPH.node(ModItems.SOUL_DEPOSIT_SEED.getId())
				.groups("soul_crystals");
		
		GRAPH.node(ModBlocks.SOUL_BUD_SMALL.getId())
				.groups("soul_crystals")
				.requires(ModItems.SOUL_DEPOSIT_SEED.getId());

		GRAPH.node(ModBlocks.SOUL_BUD_MEDIUM.getId())
				.groups("soul_crystals")
				.requires(ModBlocks.SOUL_BUD_SMALL.getId());

		GRAPH.node(ModBlocks.SOUL_BUD_LARGE.getId())
				.groups("soul_crystals")
				.requires(ModBlocks.SOUL_BUD_MEDIUM.getId());

		GRAPH.node(ModBlocks.SOUL_CLUSTER.getId())
				.groups("soul_crystals")
				.requires(ModBlocks.SOUL_BUD_LARGE.getId());

		GRAPH.node(ModItems.SOUL_SHARD.getId())
				.groups("soul_crystals")
				.requires(ModBlocks.SOUL_CLUSTER.getId());

		GRAPH.node(ModBlocks.SOUL_CRYSTAL_BLOCK.getId())
				.groups("soul_crystals")
				.requires(ModItems.SOUL_SHARD.getId());

		GRAPH.node(ModItems.LIQUID_SOUL_CRYSTAL_BUCKET.getId())
				.groups("soul_crystals")
				.requires(ModItems.SOUL_SHARD.getId());

		GRAPH.node(ModItems.LIQUID_SOUL_CRYSTAL_BUCKET.getId())
				.groups("soul_crystals")
				.requires(ModItems.SOUL_SHARD.getId());

		ResourceLocation soulGasNode = SoulHarvestMod.id("soul_gas_infusion_type");
		GRAPH.node(soulGasNode)
				.groups("soul_crystals")
				.requiresAll(ModItems.LIQUID_SOUL_CRYSTAL_BUCKET.getId(), MEKANISM_PRESENT_NODE);

		GRAPH.node(SOUL_INFUSION_NODE)
				.groups("soul_crystals")
				.requires(ModBlocks.SOUL_CLUSTER.getId());
		
		// Inlaid Lapis group
		GRAPH.node(ModItems.INLAID_LAPIS.getId())
				.groups("inlaid_lapis");
		
		GRAPH.node(ModItems.INLAID_LAPIS_PLATE.getId())
				.groups("inlaid_lapis")
				.requires(ModItems.INLAID_LAPIS.getId());
		
		GRAPH.node(ModItems.POLISHED_INLAID_LAPIS.getId())
				.groups("inlaid_lapis")
				.requiresAny(ModItems.INLAID_LAPIS.getId());
		
		ResourceLocation baseEnchantedLapisRecipeNode = SoulHarvestMod.id("inlaid_lapis_enchant_base");
		GRAPH.node(baseEnchantedLapisRecipeNode)
				.groups("inlaid_lapis")
				.requiresAll(ModItems.POLISHED_INLAID_LAPIS.getId(), SOUL_INFUSION_NODE);
		ResourceLocation mekEnchantedLapisRecipeNode = SoulHarvestMod.id("inlaid_lapis_enchant_mekanism");
		GRAPH.node(mekEnchantedLapisRecipeNode)
				.groups("inlaid_lapis")
				.requiresAll(ModItems.POLISHED_INLAID_LAPIS.getId(), soulGasNode, MEKANISM_PRESENT_NODE);
		GRAPH.node(ModItems.ENCHANTED_INLAID_LAPIS.getId())
				.groups("inlaid_lapis")
				.requiresAny(baseEnchantedLapisRecipeNode, mekEnchantedLapisRecipeNode);
		
		GRAPH.node(ModItems.MOLTEN_LAPIS_CORE.getId())
				.groups("inlaid_lapis")
				.requires(ModItems.POLISHED_INLAID_LAPIS.getId());

		// Midas group
		GRAPH.node(ModBlocks.MIDAS_CORE.getId())
				.groups("midas")
				.requires(ModItems.ENCHANTED_INLAID_LAPIS.getId());
		
		GRAPH.node(ModBlocks.GOLD_FOAM.getId())
				.groups("midas")
				.requires(ModBlocks.MIDAS_CORE.getId());

		ResourceLocation baseMagicGoldNode = SoulHarvestMod.id("magic_gold_base");
		GRAPH.node(baseMagicGoldNode)
				.groups("midas")
				.requires(ModBlocks.GOLD_FOAM.getId());
		ResourceLocation mekMagicGoldNode = SoulHarvestMod.id("magic_gold_mekanism");
		GRAPH.node(mekMagicGoldNode)
				.groups("midas")
				.requiresAll(ModItems.SOUL_SHARD.getId(), MEKANISM_PRESENT_NODE);
		GRAPH.node(ModItems.MAGIC_GOLD.getId())
				.groups("midas")
				.requiresAny(baseMagicGoldNode, mekMagicGoldNode);
		
		GRAPH.node(ModItems.MAGIC_GOLD_STRING.getId())
				.groups("midas")
				.requires(ModItems.MAGIC_GOLD.getId());
		
		GRAPH.node(ModItems.MAGIC_GOLD_CHUNK.getId())
				.groups("midas")
				.requires(ModItems.MAGIC_GOLD.getId());
		
		// Absolute Ice
		GRAPH.node(ModBlocks.ABSOLUTE_ICE.getId())
				.groups("absolute_ice")
				.requires(ModItems.ENCHANTED_INLAID_LAPIS.getId());

		// Biocide group
		GRAPH.node(ModItems.BIOCIDE_GAS_BOTTLE.getId())
				.groups("biocide");
		
		// Necronium group
		GRAPH.node(ModItems.NECRONIUM_INGOT.getId())
				.groups("necronium");
		
		GRAPH.node(ModItems.NECRONIUM_PLATE.getId())
				.requires(ModItems.NECRONIUM_INGOT.getId())
				.groups("necronium");
		
		GRAPH.node(ModItems.NECRONIUM_ROD.getId())
				.requires(ModItems.NECRONIUM_INGOT.getId())
				.groups("necronium");
		
		GRAPH.node(ModItems.NECRONIUM_WASTE.getId())
				.groups("necronium");

		// Spells group
		GRAPH.node(ModItems.SPELL_ROD_WOOD.getId())
				.requires(ModItems.MAGIC_GOLD_STRING.getId())
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.SPELL_ROD_NECRONIUM.getId())
				.requiresAll(ModItems.MAGIC_GOLD_STRING.getId(), ModItems.NECRONIUM_ROD.getId())
				.groups("spells", "spells_t2");
		
		GRAPH.node(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1")
				.requiresAll(ModItems.SPELL_ROD_WOOD.getId(), ModItems.MAGIC_GOLD.getId());
		GRAPH.node(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2")
				.requiresAll(
						ModItems.SPELL_ROD_NECRONIUM.getId(), 
						ModItems.MAGIC_GOLD_CHUNK.getId(), 
						ModItems.INLAID_LAPIS_PLATE.getId(), 
						ModItems.SOUL_SHARD.getId(), 
						ModItems.NECRONIUM_PLATE.getId()
						);
		
		GRAPH.node(ModItems.GLYPH_SCROLL_1.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.GLYPH_SCROLL_2.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.GLYPH_SCROLL_3.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.GLYPH_SCROLL_4.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.GLYPH_SCROLL_5.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.GLYPH_SCROLL_6.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.GLYPH_SCROLL_7.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		GRAPH.node(ModItems.GLYPH_SCROLL_8.getId())
				.requires(SPELLS_T1_SHARED_NODE)
				.groups("spells", "spells_t1");
		
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_1.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_2.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_3.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_4.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_5.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_6.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_7.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		GRAPH.node(ModItems.GLYPH_SCROLL_ENHANCED_8.getId())
				.requires(SPELLS_T2_SHARED_NODE)
				.groups("spells", "spells_t2");
		
		GRAPH.node(ModItems.MAGIC_GLASS.getId())
				.requiresAny(ModItems.GLYPH_SCROLL_7.getId(), ModItems.GLYPH_SCROLL_ENHANCED_7.getId())
				.groups("spells");
		GRAPH.node(ModBlocks.DENSE_HYPOTHETICAL.getId())
				.requires(ModItems.GLYPH_SCROLL_ENHANCED_8.getId())
				.groups("spells");

		GRAPH.invalidateCache();
	}
}
