package somdudewillson.create_soul_harvest.content.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.AABB;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.spells.WitherToBiocideThinkerSpell;
import somdudewillson.create_soul_harvest.registry.ModDamageSources;

public class WitherSlurryBlock extends LiquidBlock {

	public WitherSlurryBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
		super(fluid, properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);
		
		if ((level.getGameTime() & 15) > 0) return;

		if (level.getBlockState(pos.above()).isAir()) {
			if (random.nextInt(4) == 0) {
				double x = pos.getX() + random.nextDouble();
				double y = pos.getY() + 0.95 + random.nextDouble() * 0.05;
				double z = pos.getZ() + random.nextDouble();
				level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, 0.02, 0.0);
			}

			if (random.nextInt(35) == 0) {
				SoundEvent sound = random.nextBoolean() ? SoundEvents.SOUL_SAND_BREAK : SoundEvents.SOUL_SAND_STEP;
				level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
						sound, SoundSource.BLOCKS, 0.4F + random.nextFloat() * 0.2F, 0.6F + random.nextFloat() * 0.3F, false);
			} else if (random.nextInt(120) == 0) {
				level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
						SoundEvents.WITHER_AMBIENT, SoundSource.BLOCKS, 0.15F, 1.2F + random.nextFloat() * 0.3F, false);
			}
		}
	}

	//? >= 1.21 {
	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
	//?} else {
	/*@SuppressWarnings("deprecation")
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
	*///?}
		super.entityInside(state, level, pos, entity);
		if (level.isClientSide()) return;
		if (!(entity instanceof LivingEntity living)) return;
		if (living.isSpectator()) return;
		if (living instanceof Player player && player.isCreative()) return;
		if (living.getType() == EntityType.WITHER || living.getType() == EntityType.WITHER_SKELETON) return;
		if (living.isInvulnerableTo(ModDamageSources.wither(level))) return;

		living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
		if (entity.tickCount % 20 == 0) {
			living.hurt(ModDamageSources.wither(level), 2.0F);
		}
	}

	public static void triggerBiocideConversion(ServerLevel level, BlockPos pos) {
		AABB checkBounds = new AABB(pos).inflate(10);
		List<SpellThinkerEntity> existing = level.getEntitiesOfClass(
				SpellThinkerEntity.class,
				checkBounds,
				e -> e.getEffect() instanceof WitherToBiocideThinkerSpell
		);
		if (!existing.isEmpty()) {
			return;
		}

		SpellThinkerEntity.spawn(level, pos, new WitherToBiocideThinkerSpell(pos), null);
	}
}
