package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class ModItemModelProvider extends ItemModelProvider {

	public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, SoulHarvestMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// Items with custom texture mappings
		withExistingParent("blank_tablet", "item/generated")
				.texture("layer0", modLoc("item/runic_tablet_empty"));
		withExistingParent("enchanted_inlaid_lapis", "item/generated")
				.texture("layer0", modLoc("item/polished_inlaid_lapis"));
		withExistingParent("liquid_soul_crystal_bucket", "item/generated")
				.texture("layer0", modLoc("item/liquid_soul_crystal_bucket"));

		// Flat block items using block/ textures
		withExistingParent("soul_bud_small", "item/generated").texture("layer0", modLoc("block/soul_bud_small"));
		withExistingParent("soul_bud_medium", "item/generated").texture("layer0", modLoc("block/soul_bud_medium"));
		withExistingParent("soul_bud_large", "item/generated").texture("layer0", modLoc("block/soul_bud_large"));
		withExistingParent("soul_cluster", "item/generated").texture("layer0", modLoc("block/soul_cluster"));

		// Block items whose block models are custom-generated in ModBlockStateProvider
		withExistingParent("carved_bone_block", modLoc("block/carved_bone_block"));
		withExistingParent("magic_glass", modLoc("block/magic_glass"));
		withExistingParent("dense_hypothetical", modLoc("block/dense_hypothetical"));
	}
}
//?}
