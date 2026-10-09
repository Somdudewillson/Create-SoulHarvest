package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlock;
import somdudewillson.create_soul_harvest.content.block.AnimaMossBlock;
import somdudewillson.create_soul_harvest.content.block.BiocideGasBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {

	public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, SoulHarvestMod.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		// Carved bone block (orientable model + horizontal facing)
		BlockModelBuilder carvedBoneModel = models().withExistingParent("carved_bone_block", mcLoc("block/orientable"))
				.texture("front", modLoc("block/carved_bone_block"))
				.texture("side", mcLoc("block/bone_block_side"))
				.texture("top", mcLoc("block/bone_block_top"));
		horizontalBlock(ModBlocks.CARVED_BONE_BLOCK.get(), carvedBoneModel);

		// Soul buds and cluster (cross model + directional facing)
		directionalBlock(ModBlocks.SOUL_BUD_SMALL.get(),
				models().cross("soul_bud_small", modLoc("block/soul_bud_small")).renderType("cutout"));
		directionalBlock(ModBlocks.SOUL_BUD_MEDIUM.get(),
				models().cross("soul_bud_medium", modLoc("block/soul_bud_medium")).renderType("cutout"));
		directionalBlock(ModBlocks.SOUL_BUD_LARGE.get(),
				models().cross("soul_bud_large", modLoc("block/soul_bud_large")).renderType("cutout"));
		directionalBlock(ModBlocks.SOUL_CLUSTER.get(),
				models().cross("soul_cluster", modLoc("block/soul_cluster")).renderType("cutout"));

		// Anima moss (cube_all models + age 0..3 with 4 random y-rotations)
		VariantBlockStateBuilder animaMoss = getVariantBuilder(ModBlocks.ANIMA_MOSS.get());
		for (int age = 0; age <= 3; age++) {
			String name = age == 0 ? "anima_moss" : "anima_moss_" + age;
			ModelFile model = models().cubeAll(name, modLoc("block/" + name));
			animaMoss.partialState().with(AnimaMossBlock.AGE, age)
					.modelForState()
					.modelFile(model).nextModel()
					.modelFile(model).rotationY(90).nextModel()
					.modelFile(model).rotationY(180).nextModel()
					.modelFile(model).rotationY(270).addModel();
		}

		// Magic glass (cube_all models + age 0..3)
		VariantBlockStateBuilder magicGlass = getVariantBuilder(ModBlocks.MAGIC_GLASS.get());
		for (int age = 0; age <= 3; age++) {
			String name = age == 0 ? "magic_glass" : "magic_glass_" + age;
			ModelFile model = models().cubeAll(name, modLoc("block/" + name));
			magicGlass.partialState().with(AnimaGlassBlock.AGE, age)
					.modelForState().modelFile(model).addModel();
		}

		// Biocide gas (cube_all models + age 0..15 & triggered true/false)
		ModelFile gas0 = models().cubeAll("biocide_gas_0", modLoc("block/biocide_gas_0"));
		ModelFile gas0Active = models().cubeAll("biocide_gas_0_active", modLoc("block/biocide_gas_0_active"));
		ModelFile gas1 = models().cubeAll("biocide_gas_1", modLoc("block/biocide_gas_1"));
		ModelFile gas1Active = models().cubeAll("biocide_gas_1_active", modLoc("block/biocide_gas_1_active"));
		ModelFile gas2 = models().cubeAll("biocide_gas_2", modLoc("block/biocide_gas"));
		ModelFile gas2Active = models().cubeAll("biocide_gas_2_active", modLoc("block/biocide_gas_active"));

		VariantBlockStateBuilder biocideGas = getVariantBuilder(ModBlocks.BIOCIDE_GAS.get());
		for (int age = 0; age <= 15; age++) {
			ModelFile normal = age <= 2 ? gas0 : (age <= 10 ? gas1 : gas2);
			ModelFile active = age <= 2 ? gas0Active : (age <= 10 ? gas1Active : gas2Active);
			biocideGas.partialState().with(BiocideGasBlock.AGE, age).with(BiocideGasBlock.TRIGGERED, true)
					.modelForState().modelFile(normal).addModel();
			biocideGas.partialState().with(BiocideGasBlock.AGE, age).with(BiocideGasBlock.TRIGGERED, false)
					.modelForState().modelFile(active).addModel();
		}
	}
}
//?}
