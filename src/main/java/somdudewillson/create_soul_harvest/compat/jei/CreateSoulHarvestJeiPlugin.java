package somdudewillson.create_soul_harvest.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.SoulBookItem;
import somdudewillson.create_soul_harvest.registry.ModCreativeTabs;
import somdudewillson.create_soul_harvest.registry.ModItems;

import java.util.List;

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
		IIngredientSubtypeInterpreter<ItemStack> interpreter = (stack, context) -> {
			String heldSoul = SoulBookItem.getHeldSoul(stack);
			int portion = SoulBookItem.getSoulPortion(stack);
			if (heldSoul.isEmpty() && portion <= 0) {
				return IIngredientSubtypeInterpreter.NONE;
			}
			return heldSoul + ":" + portion;
		};

		registration.registerSubtypeInterpreter(ModItems.SOUL_BOOK_SIMPLE.get(), interpreter);
		registration.registerSubtypeInterpreter(ModItems.SOUL_BOOK_STANDARD.get(), interpreter);
		registration.registerSubtypeInterpreter(ModItems.SOUL_BOOK_SUPERIOR.get(), interpreter);
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
			}
		});
	}
}
