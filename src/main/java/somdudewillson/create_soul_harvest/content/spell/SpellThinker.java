package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.registry.ModEntityTypes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpellThinker extends Entity {

	@FunctionalInterface
	public interface EffectFactory {
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

	static {
		registerEffect(LightningSpell.ID, LightningSpell::new);
		registerEffect(TerrainFlattenSpell.ID, TerrainFlattenSpell::new);
	}

	private PersistentGlyphSpell currentEffect;

	public SpellThinker(EntityType<?> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = true;
		this.setNoGravity(true);
		this.setInvulnerable(true);
	}

	public void setEffect(PersistentGlyphSpell effect) {
		this.currentEffect = effect;
		if (effect != null && this.level() instanceof ServerLevel serverLevel) {
			effect.onStart(this, serverLevel);
		}
	}

	@Nullable
	public PersistentGlyphSpell getEffect() {
		return this.currentEffect;
	}

	public static SpellThinker spawn(ServerLevel level, Vec3 pos, PersistentGlyphSpell effect) {
		SpellThinker thinker = new SpellThinker(ModEntityTypes.SPELL_THINKER.get(), level);
		thinker.setPos(pos.x, pos.y, pos.z);
		thinker.setEffect(effect);
		level.addFreshEntity(thinker);
		return thinker;
	}

	public static SpellThinker spawn(ServerLevel level, BlockPos pos, PersistentGlyphSpell effect) {
		return spawn(level, Vec3.atCenterOf(pos), effect);
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

			this.currentEffect.tick(this, serverLevel);

			if (this.currentEffect.isFinished(this)) {
				this.currentEffect.onEnd(this, serverLevel);
				this.discard();
			}
		}
	}

	//? >= 1.21 {
	@Override
	protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
	}
	//?} else {
	/*@Override
	protected void defineSynchedData() {
	}
	*///?}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
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
	protected void addAdditionalSaveData(CompoundTag compound) {
		if (this.currentEffect != null) {
			compound.putString("EffectId", this.currentEffect.getId().toString());
			CompoundTag effectData = new CompoundTag();
			this.currentEffect.writeSaveData(effectData);
			compound.put("EffectData", effectData);
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
}
