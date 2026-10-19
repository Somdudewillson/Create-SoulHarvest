package somdudewillson.create_soul_harvest.content.entity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.PersistentGlyphSpell;
import somdudewillson.create_soul_harvest.registry.ModEntityTypes;
import somdudewillson.redsparklib.common.utils.OptNbtUtils;

public class SpellThinkerEntity extends Entity implements OwnableEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(
    		SpellThinkerEntity.class, EntityDataSerializers.OPTIONAL_UUID
        );
    protected static final EntityDataAccessor<String> DATA_EFFECT_ID = SynchedEntityData.defineId(
    		SpellThinkerEntity.class, EntityDataSerializers.STRING
        );
    protected static final EntityDataAccessor<Long> DATA_EFFECT_FLAGS_ID = SynchedEntityData.defineId(
    		SpellThinkerEntity.class, EntityDataSerializers.LONG
        );

	@FunctionalInterface
	public static interface EffectFactory {
		PersistentGlyphSpell create();
	}

	private static final Map<ResourceLocation, EffectFactory> REGISTRY = new ConcurrentHashMap<>();

	public static void registerEffect(ResourceLocation id, EffectFactory factory) {
		REGISTRY.put(id, factory);
	}

	@Nullable
	public static PersistentGlyphSpell createEffect(ResourceLocation id) {
		EffectFactory factory = REGISTRY.get(id);
		return factory != null ? factory.create() : null;
	}

	private PersistentGlyphSpell currentEffect;

	public SpellThinkerEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = true;
		this.setNoGravity(true);
		this.setInvulnerable(true);
	}

	public void setEffect(PersistentGlyphSpell effect) {
		this.currentEffect = effect;
		if (effect != null) {
			this.entityData.set(DATA_EFFECT_ID, effect.getId().toString());
			if (this.level() instanceof ServerLevel serverLevel) {
				effect.onStart(this, serverLevel);
			}
		} else {
			this.entityData.set(DATA_EFFECT_ID, "");
		}
	}

	@Nullable
	public PersistentGlyphSpell getEffect() {
		return this.currentEffect;
	}
	
	public long getEffectFlags() {
		return this.entityData.get(DATA_EFFECT_FLAGS_ID);
	}
	
	public void setEffectFlags(long flags) {
		this.entityData.set(DATA_EFFECT_FLAGS_ID, flags);
	}

	public static SpellThinkerEntity spawn(ServerLevel level, Vec3 pos, PersistentGlyphSpell effect, @Nullable UUID owner) {
		SpellThinkerEntity thinker = new SpellThinkerEntity(ModEntityTypes.SPELL_THINKER.get(), level);
		thinker.setPos(pos.x, pos.y, pos.z);
		thinker.setEffect(effect);
		thinker.setOwnerUUID(owner);
		level.addFreshEntity(thinker);
		return thinker;
	}

	public static SpellThinkerEntity spawn(ServerLevel level, BlockPos pos, PersistentGlyphSpell effect, @Nullable UUID owner) {
		return spawn(level, Vec3.atCenterOf(pos), effect, owner);
	}

	@Override
	public void tick() {
		super.tick();
		
		if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
			if (this.currentEffect == null || this.currentEffect.isFinished(this)) {
				if (this.currentEffect != null) {
					this.currentEffect.onEnd(this, serverLevel);
				}
				this.discard();
				return;
			}

			this.currentEffect.serverTick(this, serverLevel);

			if (this.currentEffect.isFinished(this)) {
				this.currentEffect.onEnd(this, serverLevel);
				this.discard();
			}
		} else if (this.level().isClientSide()) {
			if (this.currentEffect == null) {
				updateClientEffect();
			}
			if (this.currentEffect != null) {
				this.currentEffect.clientTick(this, this.level());
			}
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (this.level().isClientSide() && DATA_EFFECT_ID.equals(key)) {
			updateClientEffect();
		}
	}

	private void updateClientEffect() {
		String idStr = this.entityData.get(DATA_EFFECT_ID);
		if (idStr != null && !idStr.isEmpty()) {
			ResourceLocation effectId = ResourceLocation.tryParse(idStr);
			if (effectId != null && (this.currentEffect == null || !this.currentEffect.getId().equals(effectId))) {
				this.currentEffect = createEffect(effectId);
			}
		} else {
			this.currentEffect = null;
		}
	}

	//? >= 1.21 {
	@Override
	protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
		builder.define(DATA_OWNERUUID_ID, Optional.empty());
		builder.define(DATA_EFFECT_ID, "");
		builder.define(DATA_EFFECT_FLAGS_ID, 0l);
	}
	//?} else {
	/*@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
		this.entityData.define(DATA_EFFECT_ID, "");
		this.entityData.define(DATA_EFFECT_FLAGS_ID, 0l);
	}
	*///?}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		OptNbtUtils.putUUID(compound, "Owner", getOwnerUUID());
		compound.putLong("EffectFlags", getEffectFlags());
		
		if (this.currentEffect != null) {
			compound.putString("EffectId", this.currentEffect.getId().toString());
			CompoundTag effectData = new CompoundTag();
			this.currentEffect.writeSaveData(effectData);
			compound.put("EffectData", effectData);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		setOwnerUUID(OptNbtUtils.getUUID(compound, "Owner").orElse(null));
		setEffectFlags(OptNbtUtils.getLong(compound, "EffectFlags").orElse(0l));
		
		if (compound.contains("EffectId")) {
			ResourceLocation effectId = ResourceLocation.tryParse(compound.getString("EffectId"));
			if (effectId != null) {
				PersistentGlyphSpell effect = createEffect(effectId);
				if (effect != null) {
					if (compound.contains("EffectData")) {
						effect.readSaveData(compound.getCompound("EffectData"));
					}
					this.currentEffect = effect;
				}
			}
		}
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return false;
	}

	@Override
	public boolean displayFireAnimation() {
		return false;
	}

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID p_21817_) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(p_21817_));
    }
}
