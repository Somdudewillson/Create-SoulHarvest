package somdudewillson.create_soul_harvest.registry;

import java.util.Optional;
import java.util.function.Supplier;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.SoulInfusionRecipe;

//? >= 1.21 {
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeHolder;
//?} else {
/*import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.minecraft.world.Container;
*///?}

//? neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
//?} forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?}

public enum ModRecipeTypes implements IRecipeTypeInfo {
	SOUL_INFUSION(
		//? >= 1.21 {
		() -> new StandardProcessingRecipe.Serializer<>(SoulInfusionRecipe::new)
		//?} else {
		/*() -> new ProcessingRecipeSerializer<>(SoulInfusionRecipe::new)
		*///?}
	);

	private final ResourceLocation id;
	//? neoforge {
	private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerHolder;
	private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeHolder;
	//?} forge {
	/*private final RegistryObject<RecipeSerializer<?>> serializerHolder;
	private final RegistryObject<RecipeType<?>> typeHolder;
	*///?}

	ModRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
		String name = name().toLowerCase();
		this.id = SoulHarvestMod.id(name);
		this.serializerHolder = Registers.SERIALIZERS.register(name, serializerSupplier);
		this.typeHolder = Registers.TYPES.register(name, () -> RecipeType.simple(id));
	}

	public static void register(IEventBus modEventBus) {
		Registers.SERIALIZERS.register(modEventBus);
		Registers.TYPES.register(modEventBus);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RecipeSerializer<?>> T getSerializer() {
		return (T) serializerHolder.get();
	}

	//? >= 1.21 {
	@SuppressWarnings("unchecked")
	@Override
	public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
		return (RecipeType<R>) typeHolder.get();
	}

	public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I inv, Level level) {
		return level.getRecipeManager().getRecipeFor(getType(), inv, level);
	}
	//?} else {
	/*@SuppressWarnings("unchecked")
	@Override
	public <T extends RecipeType<?>> T getType() {
		return (T) typeHolder.get();
	}

	public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level level) {
		return level.getRecipeManager().getRecipeFor(getType(), inv, level);
	}
	*///?}

	private static class Registers {
		//? neoforge {
		private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
				DeferredRegister.create(Registries.RECIPE_SERIALIZER, SoulHarvestMod.MOD_ID);
		private static final DeferredRegister<RecipeType<?>> TYPES =
				DeferredRegister.create(Registries.RECIPE_TYPE, SoulHarvestMod.MOD_ID);
		//?} forge {
		/*private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
				DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SoulHarvestMod.MOD_ID);
		private static final DeferredRegister<RecipeType<?>> TYPES =
				DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, SoulHarvestMod.MOD_ID);
		*///?}
	}
}
