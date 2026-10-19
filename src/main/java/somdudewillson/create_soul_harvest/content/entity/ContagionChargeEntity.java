package somdudewillson.create_soul_harvest.content.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import somdudewillson.create_soul_harvest.registry.ModEntityTypes;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModMobEffects;

//? >= 1.21 {
import net.minecraft.network.syncher.SynchedEntityData;
//?}

public class ContagionChargeEntity extends AbstractHurtingProjectile implements ItemSupplier {

	public ContagionChargeEntity(EntityType<? extends ContagionChargeEntity> entityType, Level level) {
		super(entityType, level);
	}

	//? >= 1.21 {
	public ContagionChargeEntity(Level level, LivingEntity shooter, Vec3 movement) {
		super(ModEntityTypes.CONTAGION_CHARGE.get(), shooter, movement, level);
	}
	//?} else {
	/*public ContagionChargeEntity(Level level, LivingEntity shooter, Vec3 movement) {
		super(ModEntityTypes.CONTAGION_CHARGE.get(), shooter, movement.x, movement.y, movement.z, level);
	}
	*///?}

	//? >= 1.21 {
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	}
	//?} else {
	/*@Override
	protected void defineSynchedData() {
	}
	*///?}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.CONTAGION_CHARGE.get());
	}

	@Override
	protected boolean shouldBurn() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void tick() {
		Vec3 newMovement = getDeltaMovement().add(0, -0.075, 0);
		double yDrag = 0.5 * Math.pow(newMovement.y() / 2., 2);
		yDrag = Math.min(yDrag, -newMovement.y());
		newMovement.add(0, newMovement.y() < 0 ? yDrag : 0, 0);
		setDeltaMovement(newMovement);

		super.tick();
	}

	@Override
	protected ParticleOptions getTrailParticle() {
		//? >= 1.21 {
		return net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 2f / 255f, 56f / 255f, 14f / 255f);
		//?} else {
		/*return new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(2f / 255f, 56f / 255f, 14f / 255f), 1.0f);
		*///?}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		
		if (!this.level().isClientSide) {
			if (this.level() instanceof ServerLevel serverLevel) {
				double cloudSize = 5.0;
				Vec3 effectCenter = result.getLocation();
				if (result.getType() == HitResult.Type.ENTITY) {
					ClipContext context = new ClipContext(
								effectCenter, 
								effectCenter.add(getDeltaMovement().normalize().scale(1.5)), 
								Block.COLLIDER, Fluid.NONE, CollisionContext.empty()
							);
					BlockHitResult r = level().clip(context);
					if (r.getType() != HitResult.Type.MISS) {
						effectCenter = r.getLocation();
					}
				}
				
				// Particles
				//? >= 1.21 {
				serverLevel.sendParticles(
						net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 2f / 255f, 56f / 255f, 14f / 255f),
						effectCenter.x(), effectCenter.y(), effectCenter.z(), 500, cloudSize / 2, cloudSize / 4, cloudSize / 2, 0.1);
				//?} else {
				/*serverLevel.sendParticles(
						new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(2f / 255f, 56f / 255f, 14f / 255f), 1.0f),
						effectCenter.x(), effectCenter.y(), effectCenter.z(), 500, cloudSize / 2, cloudSize / 4, cloudSize / 2, 0.1);
				*///?}

				AABB aabb = AABB.ofSize(effectCenter, cloudSize * 2 + 1, cloudSize + 1, cloudSize * 2 + 1);
				for (LivingEntity nearby : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
					//? >= 1.21 {
					MobEffectInstance inst = nearby.getEffect(ModMobEffects.CONTAGION);
					if (inst == null || (inst.getAmplifier() <= 0 && inst.getDuration() < 20 * 30)) {
						nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION, 20 * 30, 0), null);
					}
					//?} else {
					/*MobEffectInstance inst = nearby.getEffect(ModMobEffects.CONTAGION.get());
					if (inst == null || (inst.getAmplifier() <= 0 && inst.getDuration() < 20 * 30)) {
						nearby.addEffect(new MobEffectInstance(ModMobEffects.CONTAGION.get(), 20 * 30, 0), null);
					}
					*///?}
				}
			}
			this.discard();
		}
	}
}
