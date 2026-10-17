package somdudewillson.create_soul_harvest.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;
import somdudewillson.create_soul_harvest.registry.ModCreativeTabs;
import somdudewillson.create_soul_harvest.registry.ModItems;

import java.util.List;

import lombok.val;

//? >= 1.21 {
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import org.jetbrains.annotations.Nullable;
//?} else {
/*import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
*///?}

@JeiPlugin
public class CreateSoulHarvestJeiPlugin implements IModPlugin {

	private static final ResourceLocation PLUGIN_UID = SoulHarvestMod.id("jei_plugin");
	private static volatile IJeiRuntime jeiRuntime;

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		val interpreter = new SoulBookSubtypeInterpreter(); 
		
		registration.registerSubtypeInterpreter(ModItems.SOUL_BOOK_SIMPLE.get(), interpreter);
		registration.registerSubtypeInterpreter(ModItems.SOUL_BOOK_STANDARD.get(), interpreter);
		registration.registerSubtypeInterpreter(ModItems.SOUL_BOOK_SUPERIOR.get(), interpreter);

		registration.registerSubtypeInterpreter(ModItems.ATTUNED_POLYCLAY.get(), new AttunedPolyclaySubtypeInterpreter());
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime runtime) {
		jeiRuntime = runtime;
	}

	@Override
	public void onRuntimeUnavailable() {
		jeiRuntime = null;
	}

	public static void refreshSoulBooksInJei() {
		if (jeiRuntime == null) {
			return;
		}

		Minecraft.getInstance().execute(() -> {
			if (jeiRuntime != null) {
				IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
				List<ItemStack> soulBooks = ModCreativeTabs.generateAllCompletedSoulBooks();
				if (!soulBooks.isEmpty()) {
					ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, soulBooks);
				}
				List<ItemStack> polyclays = ModCreativeTabs.generateAllAttunedPolyclay();
				if (!polyclays.isEmpty()) {
					ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, polyclays);
				}
			}
		});
	}

	//? >= 1.21 {
	public static class SoulBookSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

		@Override
		public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
			return getSubtypeString(ingredient, context);
		}

		@Override
		public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
			return getSubtypeString(ingredient, context);
		}
	//?} else {
	/*public static class SoulBookSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
		@Override
		public String apply(ItemStack ingredient, UidContext context) {
			return getSubtypeString(ingredient, context);
		}
	*///?}

		protected String getSubtypeString(ItemStack ingredient, UidContext context) {
			ResourceLocation heldSoul = ISoulHolder.getHeldSoul(ingredient);
			int portion = ISoulHolder.getSoulPortion(ingredient);
			if (heldSoul == null && portion <= 0) {
				return "";
			}
			return (heldSoul != null ? heldSoul.toString() : "") + ":" + portion;
		}
	}

	//? >= 1.21 {
	public static class AttunedPolyclaySubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

		@Override
		public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
			return getSubtypeString(ingredient, context);
		}

		@Override
		public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
			return getSubtypeString(ingredient, context);
		}
	//?} else {
	/*public static class AttunedPolyclaySubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
		@Override
		public String apply(ItemStack ingredient, UidContext context) {
			return getSubtypeString(ingredient, context);
		}
	*///?}

		protected String getSubtypeString(ItemStack ingredient, UidContext context) {
			return ISoulHolder.get(ingredient)
					.map(ISoulHolder::getHeldSoul)
					.map(ResourceLocation::toString)
					.orElse("");
		}
	}
}
