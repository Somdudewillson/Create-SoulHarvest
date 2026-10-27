package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModFluids;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FluidTagsProvider {

	public ModFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SoulHarvestMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ModTags.Fluids.BIOSLURRY_LIKE)
				.add(ModFluids.BIOSLURRY.get())
				.addOptional(ResourceLocation.fromNamespaceAndPath("mekanism", "nutritional_paste"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("enderio", "fluid_nutrient_distillation_still"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("biofactory", "nutrients_fluid"));
	}

	@Override
	public @NotNull String getName() {
		return "Create Soul Harvest Fluid Tags";
	}
}
//?}
