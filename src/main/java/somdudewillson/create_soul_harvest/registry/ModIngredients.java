package somdudewillson.create_soul_harvest.registry;

//? neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
//?} forge {
/*import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
*///?}

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.ingredient.AttunedPolyclayIngredient;
import somdudewillson.create_soul_harvest.content.recipe.ingredient.SoulBookIngredient;

public class ModIngredients {

	//? neoforge {
	public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<IngredientType<?>, IngredientType<SoulBookIngredient>> SOUL_BOOK =
			INGREDIENT_TYPES.register("soul_book", () -> new IngredientType<>(SoulBookIngredient.CODEC, SoulBookIngredient.STREAM_CODEC));

	public static final DeferredHolder<IngredientType<?>, IngredientType<AttunedPolyclayIngredient>> ATTUNED_POLYCLAY =
			INGREDIENT_TYPES.register("attuned_polyclay", () -> new IngredientType<>(AttunedPolyclayIngredient.CODEC, AttunedPolyclayIngredient.STREAM_CODEC));

	public static void register(IEventBus modBus) {
		INGREDIENT_TYPES.register(modBus);
	}
	//?} forge {
	/*public static void register(IEventBus modBus) {
		CraftingHelper.register(SoulHarvestMod.id("soul_book"), SoulBookIngredient.SERIALIZER);
		CraftingHelper.register(SoulHarvestMod.id("attuned_polyclay"), AttunedPolyclayIngredient.SERIALIZER);
	}
	*///?}
}
