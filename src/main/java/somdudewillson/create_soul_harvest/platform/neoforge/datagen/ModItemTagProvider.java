package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import com.simibubi.create.AllTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

	public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, SoulHarvestMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag).add(
				ModItems.POLYCLAY.get(),
				ModItems.POLYCLAY_INCOMPLETE.get(),
				ModItems.POLYCLAY_ATTUNED.get(),
				ModItems.POLYCLAY_ANDESITE.get(),
				ModItems.POLYCLAY_ANDESITE_INCOMPLETE.get(),
				ModItems.POLYCLAY_ANDESITE_ATTUNED.get(),
				ModItems.POLYCLAY_BRASS.get(),
				ModItems.POLYCLAY_BRASS_INCOMPLETE.get(),
				ModItems.POLYCLAY_BRASS_ATTUNED.get()
		);

		tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("create", "blaze_burner_fuel/special")))
				.add(ModItems.MOLTEN_LAPIS_CORE.get());

		// Glyph pages
		tag(ModTags.Items.GLYPH_PAGES).add(
				ModItems.GLYPH_1.get(),
				ModItems.GLYPH_2.get(),
				ModItems.GLYPH_3.get(),
				ModItems.GLYPH_4.get(),
				ModItems.GLYPH_5.get(),
				ModItems.GLYPH_6.get(),
				ModItems.GLYPH_7.get(),
				ModItems.GLYPH_8.get()
		);

		// Glyph scrolls
		tag(ModTags.Items.GLYPH_SCROLLS).add(
				ModItems.GLYPH_SCROLL_1.get(),
				ModItems.GLYPH_SCROLL_2.get(),
				ModItems.GLYPH_SCROLL_3.get(),
				ModItems.GLYPH_SCROLL_4.get(),
				ModItems.GLYPH_SCROLL_5.get(),
				ModItems.GLYPH_SCROLL_6.get(),
				ModItems.GLYPH_SCROLL_7.get(),
				ModItems.GLYPH_SCROLL_8.get()
		);

		// Soul books & covers
		tag(ModTags.Items.SOUL_BOOKS).add(
				ModItems.SOUL_BOOK_SIMPLE.get(),
				ModItems.SOUL_BOOK_STANDARD.get(),
				ModItems.SOUL_BOOK_SUPERIOR.get()
		);

		tag(ModTags.Items.SOUL_BOOK_COVERS).add(
				ModItems.SOUL_BOOK_COVER_SIMPLE.get(),
				ModItems.SOUL_BOOK_COVER_STANDARD.get(),
				ModItems.SOUL_BOOK_COVER_SUPERIOR.get()
		);

		// Book cover ingredients
		tag(ModTags.Items.BOOK_COVER_INGREDIENTS)
				.add(Items.LEATHER)
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "cardboard"));

		// Biocide gas protection
		tag(ModTags.Items.BIOCIDE_GAS_PROTECTION)
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "copper_diving_helmet"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "netherite_diving_helmet"));

		// Convention tags
		tag(Tags.Items.DYES_BLUE)
				.replace(false)
				.add(Items.BLUE_DYE)
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "dyes/blue"));

		tag(Tags.Items.STRINGS)
				.replace(false)
				.add(Items.STRING)
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "strings"));

		tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS)
				.replace(false)
				.add(ModItems.CONTAGION_CHARGE.get());

		tag(ModTags.Items.FORGE_NECRONIUM_INGOTS)
				.replace(false)
				.add(ModItems.NECRONIUM_INGOT.get());
		tag(ModTags.Items.C_NECRONIUM_INGOTS)
				.replace(false)
				.addTag(ModTags.Items.FORGE_NECRONIUM_INGOTS);

		tag(ModTags.Items.FORGE_NECRONIUM_RODS)
				.replace(false)
				.add(ModItems.NECRONIUM_ROD.get());
		tag(ModTags.Items.C_NECRONIUM_RODS)
				.replace(false)
				.addTag(ModTags.Items.FORGE_NECRONIUM_RODS);

		tag(ModTags.Items.FORGE_NECRONIUM_PLATES)
				.replace(false)
				.add(ModItems.NECRONIUM_PLATE.get());
		tag(ModTags.Items.C_NECRONIUM_PLATES)
				.replace(false)
				.addTag(ModTags.Items.FORGE_NECRONIUM_PLATES);
	}

	@Override
	public @NotNull String getName() {
		return "Create Soul Harvest Item Tags";
	}
}
//?}
