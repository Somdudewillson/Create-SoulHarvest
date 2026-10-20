package somdudewillson.create_soul_harvest.content.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import somdudewillson.create_soul_harvest.registry.ModEntityTypes;

//? >= 1.21 {
import net.minecraft.network.syncher.SynchedEntityData;
//?}

public class FireboltEntity extends AbstractHurtingProjectile {
	protected static final double DAMAGE_RADIUS = 7.0;
	protected static final int IGNITE_RADIUS = 6;
	protected static final int IGNITE_MAX = 32;

	public FireboltEntity(EntityType<? extends FireboltEntity> entityType, Level level) {
		super(entityType, level);
	}

	//? >= 1.21 {
	public FireboltEntity(Level level, LivingEntity shooter, Vec3 movement) {
		super(ModEntityTypes.FIREBOLT.get(), shooter, movement, level);
	}
	//?} else {
	/*public FireboltEntity(Level level, LivingEntity shooter, Vec3 movement) {
		super(ModEntityTypes.FIREBOLT.get(), shooter, movement.x, movement.y, movement.z, level);
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
	protected ParticleOptions getTrailParticle() {
		return null;
	}

	@Override
	public void tick() {
		if (this.level().isClientSide()) {
			Vec3 currentPos = this.position();
			Vec3 oldPos = new Vec3(this.xo, this.yo, this.zo);
			Vec3 diff = currentPos.subtract(oldPos);
			int steps = 8;
			for (int i = 0; i < steps; i++) {
				double fraction = (double) i / steps;
				double px = oldPos.x + diff.x * fraction + (this.random.nextDouble() - 0.5) * 0.15;
				double py = oldPos.y + diff.y * fraction + (this.random.nextDouble() - 0.5) * 0.15 + (this.getBbHeight() * 0.5);
				double pz = oldPos.z + diff.z * fraction + (this.random.nextDouble() - 0.5) * 0.15;
				this.level().addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);
				if (this.random.nextFloat() < 0.25F) {
					this.level().addParticle(ParticleTypes.SMOKE, px, py, pz, 0.0, 0.0, 0.0);
				}
			}
		} else {
			checkFlammableBlocksAlongPath();
		}

		super.tick();
	}

	@SuppressWarnings("deprecation")
	private void checkFlammableBlocksAlongPath() {
		AABB box = this.getBoundingBox().inflate(0.3);
		int minX = Mth.floor(box.minX);
		int minY = Mth.floor(box.minY);
		int minZ = Mth.floor(box.minZ);
		int maxX = Mth.floor(box.maxX);
		int maxY = Mth.floor(box.maxY);
		int maxZ = Mth.floor(box.maxZ);

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = this.level().getBlockState(pos);
					if (state.isAir() || state.getBlock() instanceof BaseFireBlock) {
						continue;
					}

					if (state.hasProperty(BlockStateProperties.LIT) && !state.getValue(BlockStateProperties.LIT)) {
						if (CampfireBlock.canLight(state) || CandleBlock.canLight(state) || CandleCakeBlock.canLight(state)) {
							this.level().setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
							continue;
						}
					}

					if (isFlammableBlock(state, pos)) {
						if (state.canBeReplaced() || !state.blocksMotion()) {
							this.level().setBlockAndUpdate(pos, BaseFireBlock.getState(this.level(), pos));
						} else {
							for (Direction dir : Direction.values()) {
								BlockPos adjacent = pos.relative(dir);
								if (this.level().isEmptyBlock(adjacent) && BaseFireBlock.canBePlacedAt(this.level(), adjacent, dir.getOpposite())) {
									this.level().setBlockAndUpdate(adjacent, BaseFireBlock.getState(this.level(), adjacent));
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isFlammableBlock(BlockState state, BlockPos pos) {
		for (Direction dir : Direction.values()) {
			if (state.isFlammable(this.level(), pos, dir)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level().isClientSide()) {
			if (this.level() instanceof ServerLevel serverLevel) {
				Vec3 impactPos = result.getLocation();
				explodeImpact(serverLevel, impactPos);
			}
			this.discard();
		}
	}

	private void explodeImpact(ServerLevel serverLevel, Vec3 impactPos) {
		serverLevel.playSound(null, impactPos.x, impactPos.y, impactPos.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 2.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
		serverLevel.playSound(null, impactPos.x, impactPos.y, impactPos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8F, 1.2F);
		serverLevel.sendParticles(ParticleTypes.EXPLOSION, impactPos.x, impactPos.y, impactPos.z, 4, 0.5, 0.5, 0.5, 0.1);
		serverLevel.sendParticles(ParticleTypes.FLAME, impactPos.x, impactPos.y, impactPos.z, 60, 1.8, 1.8, 1.8, 0.2);
		serverLevel.sendParticles(ParticleTypes.LAVA, impactPos.x, impactPos.y, impactPos.z, 20, 1.2, 1.2, 1.2, 0.0);

		Entity owner = this.getOwner();

		// Fire Propagation
		//? >= 1.21 {
		boolean canGrief = !(owner instanceof Mob) || net.neoforged.neoforge.event.EventHooks.canEntityGrief(serverLevel, owner);
		//?} else {
		/*boolean canGrief = !(owner instanceof Mob) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(serverLevel, owner);
		*///?}

		if (canGrief) {
			BlockPos centerPos = BlockPos.containing(impactPos.x, impactPos.y, impactPos.z);
			int r = IGNITE_RADIUS;
			int firesPlaced = 0;
			int maxFires = IGNITE_MAX;

			List<BlockPos> candidatePositions = new ArrayList<>();
			for (int dx = -r; dx <= r; dx++) {
				for (int dy = -3; dy <= 3; dy++) {
					for (int dz = -r; dz <= r; dz++) {
						if (dx * dx + dz * dz <= r * r) {
							candidatePositions.add(centerPos.offset(dx, dy, dz));
						}
					}
				}
			}
			Collections.shuffle(candidatePositions);

			for (BlockPos p : candidatePositions) {
				if (firesPlaced >= maxFires) {
					break;
				}
				if (serverLevel.isEmptyBlock(p) && BaseFireBlock.canBePlacedAt(serverLevel, p, Direction.UP)) {
					serverLevel.setBlockAndUpdate(p, BaseFireBlock.getState(serverLevel, p));
					firesPlaced++;
				}
			}
		}

		// AoE Fire Damage with Line of Sight (LOS) check
		AABB aoeBox = new AABB(
			impactPos.x - DAMAGE_RADIUS, impactPos.y - DAMAGE_RADIUS, impactPos.z - DAMAGE_RADIUS,
			impactPos.x + DAMAGE_RADIUS, impactPos.y + DAMAGE_RADIUS, impactPos.z + DAMAGE_RADIUS
		);
		List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, aoeBox);
		ResourceKey<DamageType> damageTypeKey = owner == null ? DamageTypes.UNATTRIBUTED_FIREBALL : DamageTypes.FIREBALL;
		DamageSource damageSource = new DamageSource(
			serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageTypeKey),
			this,
			owner
		);

		for (LivingEntity target : targets) {
			if (target == owner) {
				continue;
			}
			PlayerTeam ownerTeam = owner.getTeam();
			if (ownerTeam != null && ownerTeam.isAlliedTo(target.getTeam())) continue;
			if (target instanceof OwnableEntity oE && oE.getOwnerUUID().equals(owner.getUUID())) continue;
			
			double distSqr = target.distanceToSqr(impactPos);
			if (distSqr > DAMAGE_RADIUS * DAMAGE_RADIUS) {
				continue;
			}
			double dist = Math.sqrt(distSqr);

			Vec3 targetCenter = target.getBoundingBox().getCenter();
			Vec3 targetEye = target.getEyePosition();
			ClipContext contextCenter = new ClipContext(impactPos, targetCenter, Block.COLLIDER, Fluid.NONE, this);
			ClipContext contextEye = new ClipContext(impactPos, targetEye, Block.COLLIDER, Fluid.NONE, this);

			BlockHitResult hitCenter = serverLevel.clip(contextCenter);
			BlockHitResult hitEye = serverLevel.clip(contextEye);

			boolean hasLOS = (hitCenter.getType() == HitResult.Type.MISS) || (hitEye.getType() == HitResult.Type.MISS);
			if (!hasLOS) {
				continue;
			}

			float damage = (float) Math.max(3.0, 10.0 - (dist / DAMAGE_RADIUS) * 22.0);
			int fireSeconds = (int) Math.max(3, 8 - (dist / DAMAGE_RADIUS) * 15);

			//? >= 1.21 {
			target.igniteForSeconds(fireSeconds);
			//?} else {
			/*target.setSecondsOnFire(fireSeconds);
			*///?}

			target.hurt(damageSource, damage);
		}
	}
}
