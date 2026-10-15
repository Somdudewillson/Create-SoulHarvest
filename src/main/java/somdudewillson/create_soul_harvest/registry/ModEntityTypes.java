package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.spell.SpellThinker;

public class ModEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<EntityType<?>, EntityType<SpellThinker>> SPELL_THINKER = ENTITY_TYPES.register("spell_thinker",
			() -> EntityType.Builder.<SpellThinker>of(SpellThinker::new, MobCategory.MISC)
					.sized(0.0F, 0.0F)
					.clientTrackingRange(16)
					.updateInterval(20)
					.build("spell_thinker"));
}
