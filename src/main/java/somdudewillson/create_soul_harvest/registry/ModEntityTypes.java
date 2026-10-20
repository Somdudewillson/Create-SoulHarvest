package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.entity.ContagionChargeEntity;
import somdudewillson.create_soul_harvest.content.entity.FireboltEntity;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;

public class ModEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SoulHarvestMod.MOD_ID);

	public static final DeferredHolder<EntityType<?>, EntityType<SpellThinkerEntity>> SPELL_THINKER = ENTITY_TYPES.register("spell_thinker",
			() -> EntityType.Builder.<SpellThinkerEntity>of(SpellThinkerEntity::new, MobCategory.MISC)
					.sized(0.0F, 0.0F)
					.clientTrackingRange(16)
					.updateInterval(20)
					.build("spell_thinker"));

	public static final DeferredHolder<EntityType<?>, EntityType<ContagionChargeEntity>> CONTAGION_CHARGE = ENTITY_TYPES.register("contagion_charge",
			() -> EntityType.Builder.<ContagionChargeEntity>of(ContagionChargeEntity::new, MobCategory.MISC)
					.sized(0.3125F, 0.3125F)
					.clientTrackingRange(4)
					.updateInterval(10)
					.build("contagion_charge"));

	public static final DeferredHolder<EntityType<?>, EntityType<FireboltEntity>> FIREBOLT = ENTITY_TYPES.register("firebolt",
			() -> EntityType.Builder.<FireboltEntity>of(FireboltEntity::new, MobCategory.MISC)
					.sized(0.3125F, 0.3125F)
					.clientTrackingRange(4)
					.updateInterval(10)
					.build("firebolt"));
}
