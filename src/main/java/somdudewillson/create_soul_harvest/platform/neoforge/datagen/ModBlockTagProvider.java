package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import com.simibubi.create.AllTags;
import com.simibubi.create.AllTags.AllBlockTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

	public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SoulHarvestMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		// Soul Bud and growth tags
		tag(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS)
				.add(Blocks.SPAWNER);

		tag(ModTags.Blocks.FAN_PROCESSING_CATALYSTS_SOUL_INFUSION)
				.add(ModBlocks.SOUL_CLUSTER.get());
		
		// Create tags
		tag(AllBlockTags.NON_BREAKABLE.tag).add(
				ModBlocks.SOUL_BUD_SMALL.get(),
				ModBlocks.SOUL_BUD_MEDIUM.get(),
				ModBlocks.SOUL_BUD_LARGE.get()
		);
		
		tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag).add(
				ModBlocks.SOUL_BUD_SMALL.get(),
				ModBlocks.SOUL_BUD_MEDIUM.get(),
				ModBlocks.SOUL_BUD_LARGE.get(),
				ModBlocks.SOUL_CLUSTER.get()
		);

		// Biocide tags
		tag(ModTags.Blocks.BIOCIDE_CONVERT_TO_DIRT).add(
				Blocks.GRASS_BLOCK,
				Blocks.MYCELIUM,
				Blocks.PODZOL,
				Blocks.ROOTED_DIRT
		);

		tag(ModTags.Blocks.BIOCIDE_LOW_CONSUME);

		tag(ModTags.Blocks.BIOCIDE_MEDIUM_CONSUME)
				.addTag(BlockTags.LEAVES)
				.addTag(BlockTags.CROPS);

		tag(ModTags.Blocks.BIOCIDE_HIGH_CONSUME)
				.add(Blocks.TALL_GRASS, Blocks.FERN)
				.addTag(BlockTags.FLOWERS)
				.addOptional(ResourceLocation.withDefaultNamespace("short_grass"))
				.addOptional(ResourceLocation.withDefaultNamespace("grass"));

		// Spell interaction tags
		tag(ModTags.Blocks.CONSIDER_REPLACEABLE)
				.add(Blocks.SWEET_BERRY_BUSH, Blocks.TALL_GRASS, Blocks.FERN, Blocks.SNOW, Blocks.SUGAR_CANE)
				.addTag(BlockTags.PRESSURE_PLATES)
				.addTag(BlockTags.BUTTONS)
				.addTag(BlockTags.FLOWERS)
				.addOptional(ResourceLocation.withDefaultNamespace("short_grass"))
				.addOptional(ResourceLocation.withDefaultNamespace("grass"));

		tag(ModTags.Blocks.FLATTEN_CAN_CRUSH)
				.add(Blocks.GRAVEL, Blocks.STONE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.ICE, Blocks.PACKED_ICE)
				.addTag(BlockTags.DIRT)
				.addTag(BlockTags.SAND)
				.addTag(BlockTags.TERRACOTTA)
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "gravels"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "gravel"));

		tag(ModTags.Blocks.FLATTEN_CAN_GENERATE)
				.add(Blocks.GRAVEL, Blocks.STONE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.SNOW_BLOCK, Blocks.ICE, Blocks.PACKED_ICE)
				.addTag(BlockTags.DIRT)
				.addTag(BlockTags.SAND)
				.addTag(BlockTags.TERRACOTTA)
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "gravels"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "gravel"));

		tag(ModTags.Blocks.FLATTEN_CAN_DISPLACE)
				.add(Blocks.DIRT_PATH, Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.TALL_GRASS, Blocks.FERN, Blocks.SWEET_BERRY_BUSH, Blocks.SUGAR_CANE)
				.addTag(ModTags.Blocks.FLATTEN_CAN_CRUSH)
				.addTag(BlockTags.PRESSURE_PLATES)
				.addTag(BlockTags.FLOWERS)
				.addOptional(ResourceLocation.withDefaultNamespace("short_grass"))
				.addOptional(ResourceLocation.withDefaultNamespace("grass"));

		tag(ModTags.Blocks.MIDAS_CAN_CONVERT)
				.add(Blocks.STONE, Blocks.GRAVEL, Blocks.SANDSTONE, Blocks.RED_SANDSTONE)
				.addTag(BlockTags.DIRT)
				.addTag(BlockTags.SAND)
				.addTag(BlockTags.STONE_BRICKS)
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "stones"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "stone"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "gravels"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "gravel"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "sandstone/blocks"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "sandstone"));
		
		// Absolute Ice tags
		tag(ModTags.Blocks.SMALL_PLANTS_THAT_FREEZE)
				.add(Blocks.BAMBOO)
				.add(Blocks.DEAD_BUSH)
				.add(Blocks.FERN, Blocks.LARGE_FERN)
				.add(Blocks.SHORT_GRASS, Blocks.TALL_GRASS)
				.addTag(BlockTags.FLOWERS);

		// Mineable tags
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
				ModBlocks.SOUL_CRYSTAL_BLOCK.get(),
				ModBlocks.CARVED_BONE_BLOCK.get(),
				ModBlocks.ABSOLUTE_ICE.get(),
				ModBlocks.MIDAS_CORE.get(),
				ModBlocks.GOLD_FOAM.get(),
				ModBlocks.SOUL_BUD_SMALL.get(),
				ModBlocks.SOUL_BUD_MEDIUM.get(),
				ModBlocks.SOUL_BUD_LARGE.get(),
				ModBlocks.SOUL_CLUSTER.get()
		);

		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
				ModBlocks.BIOFUEL_BLOCK.get()
		);

		tag(BlockTags.NEEDS_IRON_TOOL).add(
				ModBlocks.MIDAS_CORE.get()
		);
	}

	@Override
	public @NotNull String getName() {
		return "Create Soul Harvest Block Tags";
	}
}
//?}
