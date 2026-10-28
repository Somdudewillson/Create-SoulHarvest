package somdudewillson.create_soul_harvest.registry;

//? neoforge {
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
//?} forge {
/*import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
*///?}

import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.condition.FluidExistsCondition;
import somdudewillson.create_soul_harvest.content.recipe.condition.ItemReachableCondition;
import somdudewillson.create_soul_harvest.content.recipe.condition.MinecraftVersionCondition;

public class ModConditions {

	//? neoforge {
	public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
			DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, SoulHarvestMod.MOD_ID);

	public static void register(IEventBus modBus) {
		CONDITION_CODECS.register("fluid_exists", () -> FluidExistsCondition.CODEC);
		CONDITION_CODECS.register("item_reachable", () -> ItemReachableCondition.CODEC);
		CONDITION_CODECS.register("minecraft_version", () -> MinecraftVersionCondition.CODEC);
		CONDITION_CODECS.register(modBus);
	}
	//?} forge {
	/*public static void register(IEventBus modBus) {
		CraftingHelper.register(FluidExistsCondition.Serializer.INSTANCE);
		CraftingHelper.register(ItemReachableCondition.Serializer.INSTANCE);
		CraftingHelper.register(MinecraftVersionCondition.Serializer.INSTANCE);
	}
	*///?}
}
