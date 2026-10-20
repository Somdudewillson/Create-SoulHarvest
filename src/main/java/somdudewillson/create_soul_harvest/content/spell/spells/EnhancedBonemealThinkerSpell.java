package somdudewillson.create_soul_harvest.content.spell.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.SimplePersistentGlyphSpell;
import somdudewillson.create_soul_harvest.content.spell.plants.SurfacePlantRegistry;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class EnhancedBonemealThinkerSpell extends SimplePersistentGlyphSpell {

	public static final ResourceLocation ID = SoulHarvestMod.id("enhanced_bonemeal_thinker");

	public record MovementStep(double x, double y, double z, double groundY) {}

	protected BlockPos center = BlockPos.ZERO;
	protected int armIndex = 0;
	protected int totalArms = 4;
	protected double currentRadius = 0.5;
	protected double currentTheta = 0.0;
	protected double currentY = 64.0;
	protected double maxRadius = 24.0;
	protected double stepSize = 0.5;
	protected int bonemealRadius = 1;
	protected float plantChance = 0.25F;
	protected double hoverOffset = 2.5;
	protected boolean initialized = false;

	public EnhancedBonemealThinkerSpell() {
		this(ID);
	}

	public EnhancedBonemealThinkerSpell(ResourceLocation id) {
		super(id);
	}

	public EnhancedBonemealThinkerSpell(ResourceLocation id, BlockPos center, int armIndex, int totalArms,
			double maxRadius, double stepSize, int bonemealRadius, float plantChance) {
		super(id);
		this.center = center;
		this.armIndex = armIndex;
		this.totalArms = Math.max(1, totalArms);
		this.maxRadius = maxRadius;
		this.stepSize = stepSize;
		this.bonemealRadius = bonemealRadius;
		this.plantChance = plantChance;
		this.currentTheta = (armIndex * 2.0 * Math.PI) / this.totalArms;
		this.currentRadius = 0.5;
		this.currentY = center.getY() + hoverOffset;
		this.initialized = true;
	}

	@Override
	public void onStart(SpellThinkerEntity thinker, ServerLevel level) {
		super.onStart(thinker, level);
		// Sync spiral parameters once upon creation via entity metadata
		syncFlags(thinker);
	}

	@Override
	public void cast(Level level, LivingEntity caster) {
		// This thinker is not cast directly
	}

	@Override
	public void serverTick(SpellThinkerEntity thinker, ServerLevel level) {
		if (isFinished(thinker)) {
			return;
		}

		ensureInitialized(thinker);
		MovementStep step = advanceMovement(level, thinker);
		applyBonemealAndPlants(level, step.x(), step.z(), step.groundY());
	}

	@Override
	public void clientTick(SpellThinkerEntity thinker, Level level) {
		if (isFinished(thinker)) {
			return;
		}

		ensureInitialized(thinker);
		double prevX = thinker.getX();
		double prevY = thinker.getY();
		double prevZ = thinker.getZ();

		MovementStep step = advanceMovement(level, thinker);
		spawnMovementParticles(level, prevX, prevY, prevZ, step.x(), step.y(), step.z());
	}

	/**
	 * Advances the deterministic Archimedean spiral trajectory and hover height.
	 * Shared identically across serverTick (world interactions) and clientTick (visual particles).
	 */
	protected MovementStep advanceMovement(Level level, SpellThinkerEntity thinker) {
		double deltaTheta = stepSize / Math.max(1.0, currentRadius);
		double loopSpacing = 4.0; // Radial spacing per revolution
		double deltaR = ((loopSpacing * totalArms) / (2.0 * Math.PI)) * deltaTheta;

		currentTheta += deltaTheta;
		currentRadius += deltaR;

		double x = center.getX() + 0.5 + currentRadius * Math.cos(currentTheta);
		double z = center.getZ() + 0.5 + currentRadius * Math.sin(currentTheta);

		// Sample ground height from multiple points in the vicinity (filtering out trees/foliage)
		double vicinityGroundY = sampleVicinityGroundY(level, x, z, currentY - hoverOffset);

		// Sample ground height ahead along the spiral tangent to smoothly anticipate elevation changes
		double tangentX = -Math.sin(currentTheta);
		double tangentZ = Math.cos(currentTheta);
		double aheadX = x + 1.5 * tangentX;
		double aheadZ = z + 1.5 * tangentZ;
		double aheadGroundY = sampleVicinityGroundY(level, aheadX, aheadZ, vicinityGroundY);

		// Target hover height blends vicinity ground and look-ahead ground
		double blendedGroundY = 0.6 * vicinityGroundY + 0.4 * aheadGroundY;
		double targetHoverY = blendedGroundY + hoverOffset;
		currentY = Mth.lerp(0.25, currentY, targetHoverY);

		thinker.setPos(x, currentY, z);
		return new MovementStep(x, currentY, z, vicinityGroundY);
	}

	protected void ensureInitialized(SpellThinkerEntity thinker) {
		if (!initialized) {
			long flags = thinker.getEffectFlags();
			this.armIndex = (int) (flags & 0xFL);
			this.totalArms = Math.max(1, (int) ((flags >> 4) & 0xFL));
			int flagRadius = (int) ((flags >> 8) & 0xFFL);
			if (flagRadius > 0) {
				this.maxRadius = flagRadius;
			}
			this.center = thinker.blockPosition();
			this.currentTheta = (armIndex * 2.0 * Math.PI) / totalArms;
			this.currentRadius = 0.5;
			this.currentY = center.getY() + hoverOffset;
			this.initialized = true;

			if (!thinker.level().isClientSide()) {
				syncFlags(thinker);
			}
		}
	}

	protected void syncFlags(SpellThinkerEntity thinker) {
		long flags = (armIndex & 0xFL) | ((totalArms & 0xFL) << 4) | (((long) Math.min(255, (int) maxRadius)) << 8);
		thinker.setEffectFlags(flags);
	}

	protected void applyBonemealAndPlants(ServerLevel level, double x, double z, double groundY) {
		BlockPos groundPos = BlockPos.containing(x, groundY - 0.5, z);
		for (BlockPos p : BlockPos.betweenClosed(
				groundPos.offset(-bonemealRadius, -1, -bonemealRadius),
				groundPos.offset(bonemealRadius, 2, bonemealRadius))) {
			BlockState state = level.getBlockState(p);
			BonemealSpell.tryBonemealBlock(level, p, state, 0);
		}

		BlockState groundState = level.getBlockState(groundPos);
		if (level.random.nextFloat() < plantChance) {
			SurfacePlantRegistry.getDefault()
					.selectPlant(level, groundPos, groundState, level.random)
					.ifPresent(plant -> plant.plant(level, groundPos, groundState, level.random));
		}
	}

	protected void spawnMovementParticles(Level level, double prevX, double prevY, double prevZ, double x, double y, double z) {
		double dx = x - prevX;
		double dy = y - prevY;
		double dz = z - prevZ;
		double distSqr = dx * dx + dy * dy + dz * dz;

		if (distSqr > 0.0001 && distSqr < 100.0) {
			double dist = Math.sqrt(distSqr);
			int particles = Math.max(3, (int) Math.ceil(dist / 0.12));
			for (int i = 0; i < particles; i++) {
				double f = (i + level.random.nextDouble() / particles) / (double) particles;
				double px = prevX + dx * f + (level.random.nextDouble() - 0.5) * 0.08;
				double py = prevY + dy * f + (level.random.nextDouble() - 0.5) * 0.06;
				double pz = prevZ + dz * f + (level.random.nextDouble() - 0.5) * 0.08;

				level.addAlwaysVisibleParticle(ModParticleTypes.VERDANT_FLASH.get(),
						px, py, pz,
						(level.random.nextDouble() - 0.5) * 0.005,
						0.025 + level.random.nextDouble() * 0.015,
						(level.random.nextDouble() - 0.5) * 0.005);
			}
		} else {
			level.addAlwaysVisibleParticle(ModParticleTypes.VERDANT_FLASH.get(),
					x + (level.random.nextDouble() - 0.5) * 0.1,
					y + (level.random.nextDouble() - 0.5) * 0.08,
					z + (level.random.nextDouble() - 0.5) * 0.1,
					(level.random.nextDouble() - 0.5) * 0.005,
					0.025 + level.random.nextDouble() * 0.015,
					(level.random.nextDouble() - 0.5) * 0.005);
		}
	}

	@Override
	public boolean isFinished(SpellThinkerEntity thinker) {
		return currentRadius >= maxRadius;
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		super.writeSaveData(tag);
		tag.putInt("center_x", center.getX());
		tag.putInt("center_y", center.getY());
		tag.putInt("center_z", center.getZ());
		tag.putInt("arm_index", armIndex);
		tag.putInt("total_arms", totalArms);
		tag.putDouble("radius", currentRadius);
		tag.putDouble("theta", currentTheta);
		tag.putDouble("current_y", currentY);
		tag.putDouble("max_radius", maxRadius);
		tag.putDouble("step_size", stepSize);
		tag.putInt("bonemeal_radius", bonemealRadius);
		tag.putFloat("plant_chance", plantChance);
		tag.putDouble("hover_offset", hoverOffset);
		tag.putBoolean("initialized", initialized);
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		super.readSaveData(tag);

		center = new BlockPos(tag.getInt("center_x"), tag.getInt("center_y"), tag.getInt("center_z"));
		armIndex = tag.getInt("arm_index");
		totalArms = Math.max(1, tag.getInt("total_arms"));
		currentRadius = tag.getDouble("radius");
		currentTheta = tag.getDouble("theta");
		currentY = tag.getDouble("current_y");
		maxRadius = tag.getDouble("max_radius");
		stepSize = tag.getDouble("step_size");
		bonemealRadius = tag.getInt("bonemeal_radius");
		plantChance = tag.getFloat("plant_chance");
		hoverOffset = tag.getDouble("hover_offset");
		initialized = tag.getBoolean("initialized");
	}

	protected double sampleVicinityGroundY(Level level, double x, double z, double refY) {
		double yCenter = sampleTerrainGroundY(level, x, z, refY);
		double yEast = sampleTerrainGroundY(level, x + 1.0, z, refY);
		double yWest = sampleTerrainGroundY(level, x - 1.0, z, refY);
		double ySouth = sampleTerrainGroundY(level, x, z + 1.0, refY);
		double yNorth = sampleTerrainGroundY(level, x, z - 1.0, refY);

		return (yCenter + yEast + yWest + ySouth + yNorth) / 5.0;
	}

	@SuppressWarnings("deprecation")
	protected double sampleTerrainGroundY(Level level, double x, double z, double refY) {
		int bx = Mth.floor(x);
		int bz = Mth.floor(z);
		int startY = Math.min(level.getMaxBuildHeight() - 1, Mth.floor(refY) + 6);
		int minY = Math.max(level.getMinBuildHeight(), Mth.floor(refY) - 12);

		for (int y = startY; y >= minY; y--) {
			BlockPos checkPos = new BlockPos(bx, y, bz);
			BlockState state = level.getBlockState(checkPos);

			// Skip air, leaves, logs, crops, flowers, and replaceable foliage so thinkers don't climb trees
			if (state.isAir() 
					|| state.is(BlockTags.LEAVES) 
					|| state.is(BlockTags.LOGS)
					|| state.getBlock() instanceof BushBlock 
					|| state.is(BlockTags.FLOWERS)
					|| state.is(BlockTags.CROPS) 
					|| state.canBeReplaced()
					|| state.is(ModTags.Blocks.CONSIDER_REPLACEABLE)) {
				continue;
			}

			// First solid terrain surface or fluid surface
			if (state.blocksMotion() || !state.getFluidState().isEmpty() || state.isSolid()) {
				return y + 1.0;
			}
		}

		return refY;
	}
}
