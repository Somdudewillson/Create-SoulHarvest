package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
//? neoforge {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
//?} forge {
/*import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?}
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlockEntity;

public class ModBlockEntities {

	//? neoforge {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnimaGlassBlockEntity>> MAGIC_GLASS_BE = BLOCK_ENTITY_TYPES.register("magic_glass",
			() -> BlockEntityType.Builder.of(AnimaGlassBlockEntity::new, ModBlocks.MAGIC_GLASS.get()).build(null));
	//?} forge {
	/*public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SoulHarvestMod.MOD_ID);

	public static final RegistryObject<BlockEntityType<AnimaGlassBlockEntity>> MAGIC_GLASS_BE = BLOCK_ENTITY_TYPES.register("magic_glass",
			() -> BlockEntityType.Builder.of(AnimaGlassBlockEntity::new, ModBlocks.MAGIC_GLASS.get()).build(null));
	*///?}
}
