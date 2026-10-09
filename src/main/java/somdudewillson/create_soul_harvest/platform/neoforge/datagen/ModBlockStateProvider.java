package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {

	public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, SoulHarvestMod.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(ModBlocks.SOUL_CRYSTAL_BLOCK.get());
		simpleBlock(ModBlocks.BIOFUEL_BLOCK.get());
		simpleBlock(ModBlocks.CHARCOAL_BLOCK.get());
		simpleBlock(ModBlocks.ABSOLUTE_ICE.get());
		simpleBlock(ModBlocks.GOLD_FOAM.get());
		simpleBlock(ModBlocks.MIDAS_CORE.get());
	}
}
//?}
