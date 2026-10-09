package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModItems;

public class ModItemModelProvider extends ItemModelProvider {

	public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, SoulHarvestMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// Simple items
		basicItem(ModItems.FUEL_PELLET.get());
		basicItem(ModItems.ORGANIC_MULCH.get());
		basicItem(ModItems.BIOFUEL_CHUNK.get());
		basicItem(ModItems.INLAID_LAPIS.get());
		basicItem(ModItems.POLISHED_INLAID_LAPIS.get());
		withExistingParent("enchanted_inlaid_lapis", "item/generated")
				.texture("layer0", modLoc("item/polished_inlaid_lapis"));
		basicItem(ModItems.MOLTEN_LAPIS_CORE.get());
		basicItem(ModItems.MAGIC_GOLD.get());
		basicItem(ModItems.SOUL_DEPOSIT_SEED.get());
		basicItem(ModItems.SOUL_SHARD.get());
		basicItem(ModItems.POLYCLAY.get());
		basicItem(ModItems.LOOT_TOKEN_BLANK.get());
		basicItem(ModItems.LOOT_TOKEN.get());
		basicItem(ModItems.BIOCIDE_GAS_BOTTLE.get());

		// Blank tablet
		withExistingParent("blank_tablet", "item/generated")
				.texture("layer0", modLoc("item/runic_tablet_empty"));

		// Soul Books
		basicItem(ModItems.SOUL_BOOK_SIMPLE.get());
		basicItem(ModItems.SOUL_BOOK_STANDARD.get());
		basicItem(ModItems.SOUL_BOOK_SUPERIOR.get());

		// Glyphs and Scrolls 1..8
		for (int i = 1; i <= 8; i++) {
			withExistingParent("glyph_" + i, "item/generated")
					.texture("layer0", modLoc("item/glyph_" + i));
			withExistingParent("glyph_scroll_" + i, "item/generated")
					.texture("layer0", modLoc("item/glyph_scroll_" + i));
		}

		// Buds and cluster item models
		withExistingParent("soul_bud_small", "item/generated").texture("layer0", modLoc("block/soul_bud_small"));
		withExistingParent("soul_bud_medium", "item/generated").texture("layer0", modLoc("block/soul_bud_medium"));
		withExistingParent("soul_bud_large", "item/generated").texture("layer0", modLoc("block/soul_bud_large"));
		withExistingParent("soul_cluster", "item/generated").texture("layer0", modLoc("block/soul_cluster"));

		// Cube block items
		withExistingParent("soul_crystal_block", modLoc("block/soul_crystal_block"));
		withExistingParent("biofuel_block", modLoc("block/biofuel_block"));
		withExistingParent("charcoal_block", modLoc("block/charcoal_block"));
		withExistingParent("absolute_ice", modLoc("block/absolute_ice"));
		withExistingParent("gold_foam", modLoc("block/gold_foam"));
		withExistingParent("midas_core", modLoc("block/midas_core"));

		// Other block items
		withExistingParent("carved_bone_block", modLoc("block/carved_bone_block"));
		withExistingParent("anima_moss", modLoc("block/anima_moss"));
		withExistingParent("magic_glass", modLoc("block/magic_glass"));
	}
}
//?}
