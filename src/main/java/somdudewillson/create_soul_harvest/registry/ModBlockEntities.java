package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.AbsoluteIceBlockEntity;
import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlockEntity;
import somdudewillson.create_soul_harvest.content.block.MidasCoreBlockEntity;

public class ModBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnimaGlassBlockEntity>> MAGIC_GLASS_BE = BLOCK_ENTITY_TYPES.register("magic_glass",
			() -> BlockEntityType.Builder.of(AnimaGlassBlockEntity::new, ModBlocks.MAGIC_GLASS.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MidasCoreBlockEntity>> MIDAS_CORE_BE = BLOCK_ENTITY_TYPES.register("midas_core",
			() -> BlockEntityType.Builder.of(MidasCoreBlockEntity::new, ModBlocks.MIDAS_CORE.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbsoluteIceBlockEntity>> ABSOLUTE_ICE_BE = BLOCK_ENTITY_TYPES.register("absolute_ice",
			() -> BlockEntityType.Builder.of(AbsoluteIceBlockEntity::new, ModBlocks.ABSOLUTE_ICE.get()).build(null));
}
