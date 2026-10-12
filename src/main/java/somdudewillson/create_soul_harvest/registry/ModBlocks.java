package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import somdudewillson.redsparklib.common.data.builder.BlockRegistryBuilder.BlockRegistryBuilderFactory;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.*;

public class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SoulHarvestMod.MOD_ID);
	public static final BlockRegistryBuilderFactory BLOCK_BUILDER_FACTORY = new BlockRegistryBuilderFactory(SoulHarvestMod.MOD_ID, BLOCKS, ModItems.ITEMS);

	public static final DeferredHolder<Block, SoulBudBlock> SOUL_BUD_SMALL = BLOCKS.register("soul_bud_small",
			() -> new SoulBudBlock(3, 4, ModBlocks.SOUL_BUD_MEDIUM::get,
					BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.SMALL_AMETHYST_BUD).strength(1.5f).lightLevel(s -> 1).noOcclusion()));

	public static final DeferredHolder<Block, SoulBudBlock> SOUL_BUD_MEDIUM = BLOCKS.register("soul_bud_medium",
			() -> new SoulBudBlock(4, 3, ModBlocks.SOUL_BUD_LARGE::get,
					BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.MEDIUM_AMETHYST_BUD).strength(1.5f).lightLevel(s -> 2).noOcclusion()));

	public static final DeferredHolder<Block, SoulBudBlock> SOUL_BUD_LARGE = BLOCKS.register("soul_bud_large",
			() -> new SoulBudBlock(5, 3, ModBlocks.SOUL_CLUSTER::get,
					BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.LARGE_AMETHYST_BUD).strength(1.5f).lightLevel(s -> 4).noOcclusion()));

	public static final DeferredHolder<Block, SoulClusterBlock> SOUL_CLUSTER = BLOCKS.register("soul_cluster",
			() -> new SoulClusterBlock(7, 3,
					BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.AMETHYST_CLUSTER).strength(1.5f).lightLevel(s -> 5).noOcclusion()));

	public static final DeferredHolder<Block, Block> SOUL_CRYSTAL_BLOCK = BLOCK_BUILDER_FACTORY.registerBlock("soul_crystal_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.AMETHYST).strength(1.5f).requiresCorrectToolForDrops().lightLevel(s -> 6)))
			.simpleBlockWithBlockItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.properties(new Item.Properties())
			.build();

	public static final DeferredHolder<Block, Block> BIOFUEL_BLOCK = BLOCK_BUILDER_FACTORY.registerBlock("biofuel_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.GRAVEL).strength(4.0f, 5.0f)))
			.simpleBlockNoItem()
			.build();

	public static final DeferredHolder<Block, CarvedBoneBlock> CARVED_BONE_BLOCK = BLOCKS.register("carved_bone_block",
			() -> new CarvedBoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).sound(SoundType.BONE_BLOCK).strength(2.0f).requiresCorrectToolForDrops()));

	@SuppressWarnings("unchecked")
	public static final DeferredHolder<Block, AbsoluteIceBlock> ABSOLUTE_ICE = (DeferredHolder) BLOCK_BUILDER_FACTORY.registerBlock("absolute_ice",
			() -> new AbsoluteIceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).sound(SoundType.GLASS).strength(2.8f, 10.0f).lightLevel(s -> 13).requiresCorrectToolForDrops()))
			.simpleBlockWithBlockItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.properties(new Item.Properties())
			.build();

	public static final DeferredHolder<Block, Block> GOLD_FOAM = BLOCK_BUILDER_FACTORY.registerBlock("gold_foam",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.METAL).strength(0.4f, 0.2f).lightLevel(s -> 2).noOcclusion()))
			.simpleBlockWithBlockItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.properties(new Item.Properties())
			.build();

	@SuppressWarnings("unchecked")
	public static final DeferredHolder<Block, MidasCoreBlock> MIDAS_CORE = (DeferredHolder) BLOCK_BUILDER_FACTORY.registerBlock("midas_core",
			() -> new MidasCoreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.METAL).strength(3.0f, 10.0f).lightLevel(s -> 13).requiresCorrectToolForDrops()))
			.simpleBlockWithBlockItem()
			.inCreativeTab(ModCreativeTabs::addToMainTab)
			.properties(new Item.Properties())
			.build();

	public static final DeferredHolder<Block, AnimaGlassBlock> MAGIC_GLASS = BLOCKS.register("magic_glass",
			() -> new AnimaGlassBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).sound(SoundType.GLASS).strength(1.5f, 100.0f).lightLevel(s -> 2).noOcclusion().noLootTable()));

	public static final DeferredHolder<Block, BiocideGasBlock> BIOCIDE_GAS = BLOCKS.register("biocide_gas",
			() -> new BiocideGasBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.0f).noCollission().noOcclusion().noLootTable()));

}
