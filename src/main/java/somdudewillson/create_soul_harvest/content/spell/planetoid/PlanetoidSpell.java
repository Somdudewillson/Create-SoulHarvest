package somdudewillson.create_soul_harvest.content.spell.planetoid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.DenseHypotheticalBlock;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.SimplePersistentGlyphSpell;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;
import somdudewillson.create_soul_harvest.registry.ModTags;
import somdudewillson.redsparklib.common.utils.OptNbtUtils;

public class PlanetoidSpell extends SimplePersistentGlyphSpell {
	public static final ResourceLocation ID = SoulHarvestMod.id("planetoid");

	private static final Map<ResourceKey<Level>, Map<UUID, AABB>> ACTIVE_ZONES = new ConcurrentHashMap<>();
	
	private PlanetoidTemplate template;
	private PlanetoidTemplateSelector templateSelector;
	private PlanetoidPhase phase = PlanetoidPhase.CHARGE;
	private Vec3 origin;
	private long seed;
	private int phaseTimer;
	private int soundTimer;
	private int placementIndex;
	private boolean fizzled = false;

	private List<BlockPos> shellPositions = new ArrayList<>();
	private List<BlockPos> sortedGenerationPositions = new ArrayList<>();
	private Map<BlockPos, PlannedPlanetoidBlockPlacement> plannedPlanetoid = new HashMap<>();

	public PlanetoidSpell() {
		this(ID, PlanetoidTemplates.VERDANT, PlanetoidTemplates.DEFAULT_SELECTOR);
	}

	public PlanetoidSpell(ResourceLocation id) {
		this(id, PlanetoidTemplates.VERDANT, PlanetoidTemplates.DEFAULT_SELECTOR);
	}

	public PlanetoidSpell(ResourceLocation id, PlanetoidTemplate template) {
		this(id, template, (lvl, orig, rnd) -> template);
	}

	public PlanetoidSpell(ResourceLocation id, PlanetoidTemplate template, PlanetoidTemplateSelector templateSelector) {
		super(id);
		this.template = template;
		this.templateSelector = templateSelector;
	}

	/**
	 * Fast check to prevent DenseHypotheticalBlock random-tick destabilization
	 * while an active Planetoid spell is working in the area.
	 */
	public static boolean isProtected(Level level, BlockPos pos) {
		Map<UUID, AABB> zones = ACTIVE_ZONES.get(level.dimension());
		if (zones == null || zones.isEmpty()) {
			return false;
		}
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		for (AABB box : zones.values()) {
			if (box.contains(x, y, z)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			if (this.templateSelector != null) {
				this.template = this.templateSelector.select(serverLevel, caster.position(), serverLevel.random);
			}
			if (this.template == null) {
				return false;
			}

			// Precondition
			BlockPos basePos = caster.blockPosition();
			for (int dy = 2; dy <= 7; dy++) {
				BlockPos checkPos = basePos.above(dy);
				if (!serverLevel.getBlockState(checkPos).canBeReplaced()) {
					return false;
				}
			}

			double elevation = template.getSpawnElevation();
			Vec3 castPos = caster.position().add(0, elevation, 0);

			double maxY = serverLevel.getMaxBuildHeight() - template.maxRadius() - 2.0;
			if (castPos.y > maxY) {
				castPos = new Vec3(castPos.x, maxY, castPos.z);
			}

			if (castPos.y - template.maxRadius() < serverLevel.getMinBuildHeight()) {
				return false;
			}

			this.origin = castPos;
			this.seed = serverLevel.random.nextLong();
			this.phase = PlanetoidPhase.CHARGE;
			this.phaseTimer = template.chargeTicks();
			this.soundTimer = 2;
			this.placementIndex = 0;
			this.fizzled = false;

			SpellThinkerEntity.spawn(serverLevel, this.origin, this, caster.getUUID());
			return true;
		}
		return true;
	}

	public static long packEffectFlags(PlanetoidPhase phase, int radius) {
		return ((long) (radius & 0xFFFF) << 8) | ((long) (phase.ordinal() & 0xFF));
	}

	public static PlanetoidPhase unpackPhase(long flags) {
		int ordinal = (int) (flags & 0xFF);
		return PlanetoidPhase.values()[Mth.clamp(ordinal, 0, PlanetoidPhase.values().length - 1)];
	}

	public static int unpackRadius(long flags) {
		int r = (int) ((flags >> 8) & 0xFFFF);
		return r > 0 ? r : 6;
	}

	private void updateEffectFlags(SpellThinkerEntity thinker) {
		int radius = template != null ? (int) Math.ceil(template.maxRadius()) : 6;
		thinker.setEffectFlags(packEffectFlags(this.phase, radius));
	}

	@Override
	public void onStart(SpellThinkerEntity thinker, ServerLevel level) {
		if (this.origin == null) {
			this.origin = thinker.position();
		}
		if (this.templateSelector != null && this.template == null) {
			this.template = this.templateSelector.select(level, this.origin, level.random);
		}
		updateEffectFlags(thinker);
		registerActiveZone(thinker, level);
	}

	@Override
	public void onEnd(SpellThinkerEntity thinker, ServerLevel level) {
		unregisterActiveZone(thinker, level);
	}

	private void registerActiveZone(SpellThinkerEntity thinker, Level level) {
		if (this.origin != null && this.template != null) {
			double r = this.template.maxRadius() + 4.0;
			AABB box = new AABB(
					origin.x - r, origin.y - r, origin.z - r,
					origin.x + r, origin.y + r, origin.z + r
			);
			ACTIVE_ZONES.computeIfAbsent(level.dimension(), k -> new ConcurrentHashMap<>()).put(thinker.getUUID(), box);
		}
	}

	private void unregisterActiveZone(SpellThinkerEntity thinker, Level level) {
		Map<UUID, AABB> zones = ACTIVE_ZONES.get(level.dimension());
		if (zones != null) {
			zones.remove(thinker.getUUID());
		}
	}

	public boolean isFizzled() {
		return fizzled;
	}

	public void fizzle(SpellThinkerEntity thinker, ServerLevel level) {
		if (this.fizzled) return;
		this.fizzled = true;

		if (this.phase == PlanetoidPhase.CHARGE) {
			// Scatter random dense hypothetical blocks
			RandomSource random = level.random;
			BlockPos center = BlockPos.containing(origin.x, origin.y, origin.z);
			for (int i = 0; i < 50; i++) {
				int dx = random.nextInt(11) - 5;
				int dy = random.nextInt(11) - 5;
				int dz = random.nextInt(11) - 5;
				if (dx * dx + dy * dy + dz * dz <= 5*5) {
					BlockPos target = center.offset(dx, dy, dz);
					BlockState current = level.getBlockState(target);
					if (isReplaceable(level, target, current)) {
						DenseHypotheticalBlock.placeRandomVariant(level, target, 3);
					}
				}
			}
			level.playSound(null, center, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.2F, 0.8F);
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, origin.x, origin.y, origin.z, 30, 1.5, 1.5, 1.5, 0.05);
		} else {
			level.playSound(null, BlockPos.containing(origin.x, origin.y, origin.z), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 0.6F);
		}

		this.phase = PlanetoidPhase.FIZZLED;
		updateEffectFlags(thinker);
		unregisterActiveZone(thinker, level);
		thinker.discard();
	}

	private boolean checkConflicts(SpellThinkerEntity thinker, ServerLevel level) {
		double conflictDist = template.getConflictRadius();
		AABB searchBox = thinker.getBoundingBox().inflate(conflictDist);
		List<SpellThinkerEntity> others = level.getEntitiesOfClass(
				SpellThinkerEntity.class,
				searchBox,
				e -> e != thinker && e.isAlive() && e.getEffect() instanceof PlanetoidSpell otherSpell && !otherSpell.isFizzled()
		);

		if (!others.isEmpty()) {
			for (SpellThinkerEntity other : others) {
				if (other.getEffect() instanceof PlanetoidSpell otherSpell) {
					otherSpell.fizzle(other, level);
				}
			}
			this.fizzle(thinker, level);
			return true;
		}
		return false;
	}

	@Override
	public void serverTick(SpellThinkerEntity thinker, ServerLevel level) {
		if (this.fizzled) {
			return;
		}

		if (checkConflicts(thinker, level)) {
			return;
		}
		
		considerPlayingSoundLoop(thinker, level);

		switch (this.phase) {
			case CHARGE -> {				
				this.phaseTimer--;
				
				if (this.phaseTimer <= 0) {
					if (this.templateSelector != null) {
						this.template = this.templateSelector.select(level, this.origin, level.random);
					}
					precomputePlanetoid(level);
					this.phase = PlanetoidPhase.SHELL;
					this.placementIndex = 0;
					updateEffectFlags(thinker);
					registerActiveZone(thinker, level);
				}
			}
			case SHELL -> {				
				int blocksToPlace = template.shellBlocksPerTick();
				int placed = 0;
				while (placed < blocksToPlace && placementIndex < shellPositions.size()) {
					BlockPos target = shellPositions.get(placementIndex);
					BlockState current = level.getBlockState(target);

					if (!isReplaceable(level, target, current)) {
						// Failed to place block -> fizzle
						fizzle(thinker, level);
						return;
					}

					DenseHypotheticalBlock.placeRandomVariant(level, target, 3);
					
					SoundType soundType = Blocks.OBSIDIAN.getSoundType(Blocks.OBSIDIAN.defaultBlockState(), level, target, thinker);
					level.playSound(null, target, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 0.5F) / 3.5F, soundType.getPitch() * 0.7F);
					
					placementIndex++;
					placed++;
				}

				if (placementIndex >= shellPositions.size()) {
					this.phase = PlanetoidPhase.GENERATION;
					this.placementIndex = 0;
					this.phaseTimer = 0;
					updateEffectFlags(thinker);
				}
			}
			case GENERATION -> {
				this.phaseTimer++;
				if (this.phaseTimer % template.generationInterval() != 0) {
					return;
				}

				int blocksToPlace = template.generationBlocksPerInterval();
				int placed = 0;
				while (placed < blocksToPlace && placementIndex < sortedGenerationPositions.size()) {
					BlockPos target = sortedGenerationPositions.get(placementIndex);
					PlannedPlanetoidBlockPlacement targetPlacement = plannedPlanetoid.get(target);
					BlockState targetState = targetPlacement != null ? targetPlacement.blockState() : null;
//					PlanetoidLayerDefinition layer = targetPlacement != null ? targetPlacement.placingLayer() : null;

					if (!level.getBlockState(target).is(ModBlocks.DENSE_HYPOTHETICAL.get())) {
						// Non-hypothetical block -> 5% chance to fizzle
						if (level.random.nextFloat() < 0.05f) {
							fizzle(thinker, level);
							return;
						}
					} else {
						SoundType soundType = Blocks.OBSIDIAN.getSoundType(Blocks.OBSIDIAN.defaultBlockState(), level, target, thinker);
						level.playSound(null, target, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 0.5F) / 3.5F, soundType.getPitch() * 0.7F);
						
						if (targetState == null || targetState.isAir()) {
							level.setBlock(target, Blocks.AIR.defaultBlockState(), 2);
						} else {
							level.setBlock(target, targetState, 2);
						}
					}

					placementIndex++;
					placed++;
				}

				if (placementIndex >= sortedGenerationPositions.size()) {
					unregisterActiveZone(thinker, level);
					thinker.discard();
				}
			}
			case FIZZLED -> {
				unregisterActiveZone(thinker, level);
				thinker.discard();
			}
		}
	}

	@Override
	public void clientTick(SpellThinkerEntity thinker, Level level) {
		long flags = thinker.getEffectFlags();
		PlanetoidPhase clientPhase = unpackPhase(flags);
		int radius = unpackRadius(flags);

		Vec3 center = thinker.position();

		if (clientPhase == PlanetoidPhase.CHARGE) {
			double time = level.getGameTime() + (double) thinker.tickCount;

			// Double rotating orbital particle rings
			int ringPoints = Math.max(6, (int) Math.round(radius * 1.2));
			double ringRadius = radius * 0.9;
			double wobble = Math.max(1.0, radius * 0.25);
			for (int i = 0; i < ringPoints; i++) {
				double angle1 = (time * 0.08) + (i * (2 * Math.PI / ringPoints));
				double x1 = center.x + Math.cos(angle1) * ringRadius;
				double z1 = center.z + Math.sin(angle1) * ringRadius;
				double y1 = center.y + Math.sin(time * 0.05 + i) * wobble;
				level.addParticle(ModParticleTypes.INFUSE_SOUL_FIRE.get(), x1, y1, z1, 0, 0, 0);

				double angle2 = (-time * 0.08) + (i * (2 * Math.PI / ringPoints));
				double x2 = center.x + Math.sin(angle2) * (ringRadius * 0.8);
				double y2 = center.y + Math.cos(angle2) * (ringRadius * 0.8);
				double z2 = center.z + Math.sin(time * 0.05) * (wobble * 0.8);
				level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x2, y2, z2, 0, 0, 0);
			}

			// Inward spiraling portal convergence
			int portalCount = Math.max(3, (int) Math.round(radius * 0.5));
			for (int i = 0; i < portalCount; i++) {
				double theta = level.random.nextDouble() * 2 * Math.PI;
				double phi = level.random.nextDouble() * Math.PI;
				double dist = (radius * 0.3) + level.random.nextDouble() * (radius * 0.8);
				double px = center.x + dist * Math.sin(phi) * Math.cos(theta);
				double py = center.y + dist * Math.cos(phi);
				double pz = center.z + dist * Math.sin(phi) * Math.sin(theta);
				level.addParticle(ParticleTypes.PORTAL, px, py, pz, (center.x - px) * 0.2, (center.y - py) * 0.2, (center.z - pz) * 0.2);
			}

			// Core flash sparkles
			if (level.random.nextFloat() < 0.25f) {
				level.addParticle(ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z,
						(level.random.nextDouble() - 0.5) * 0.2,
						(level.random.nextDouble() - 0.5) * 0.2,
						(level.random.nextDouble() - 0.5) * 0.2);
			}
		} else if (clientPhase == PlanetoidPhase.GENERATION) {
			double sphereRadius = radius + 0.3;
			double period = Math.max(40.0, radius * 8.0);
			int numRings = 2;
			double time = level.getGameTime() + (double) thinker.getId();

			for (int ringIdx = 0; ringIdx < numRings; ringIdx++) {
				double progress = ((time / period) + ((double) ringIdx / numRings)) % 1.0;
				double theta = progress * Math.PI; // 0 at top pole to PI at bottom pole

				double ringY = center.y + sphereRadius * Math.cos(theta);
				double rLat = sphereRadius * Math.sin(theta);

				if (rLat < 0.2) {
					continue;
				}

				int ringPoints = Math.max(4, (int) Math.round(rLat * 5.0));
				double spin = (time * 0.03) * (ringIdx % 2 == 0 ? 1.0 : -1.0);

				for (int i = 0; i < ringPoints; i++) {
					double phi = spin + (i * (2.0 * Math.PI / ringPoints));
					double px = center.x + rLat * Math.cos(phi);
					double pz = center.z + rLat * Math.sin(phi);

					level.addParticle(ModParticleTypes.INFUSE_SOUL_FIRE.get(), px, ringY, pz, 0, 0, 0);
					if (i % 2 == 0) {
						level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, ringY, pz, 0, 0, 0);
					}
					if (level.random.nextFloat() < 0.1f) {
						level.addParticle(ParticleTypes.ENCHANT, px, ringY, pz, 0, 0.05, 0);
					}
				}
			}
		}
	}

	private void precomputePlanetoid(ServerLevel level) {
		shellPositions.clear();
		plannedPlanetoid.clear();
		sortedGenerationPositions.clear();

		RandomSource rand = RandomSource.create(this.seed);

		PartialPlanetoidGeneration generation = new PartialPlanetoidGeneration(origin, template.maxRadius(), plannedPlanetoid);

		// Populate each layer into the partial generation object
		for (PlanetoidLayerDefinition layer : template.layers()) {
			generation.setCurrentLayer(layer);
			layer.builder().populate(level, layer, origin, generation, rand);
		}

		// Shell positions are all positions within the bounding volume, sorted from the center outward
		shellPositions.addAll(plannedPlanetoid.keySet());
		shellPositions.sort(Comparator.comparingDouble(p -> p.distToCenterSqr(origin.x, origin.y, origin.z)));
		
		sortedGenerationPositions.addAll(shellPositions);
	}
	
	protected void considerPlayingSoundLoop(SpellThinkerEntity thinker, ServerLevel level) {
		soundTimer--;
		if (soundTimer <= 0) {
			level.playSound(null, thinker.blockPosition(), SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 2.5f, 0.6f);
			soundTimer = level.random.nextIntBetweenInclusive(30, 55);
		}
	}

	public static boolean isReplaceable(ServerLevel level, BlockPos pos, BlockState state) {
		if (level.isOutsideBuildHeight(pos)) {
			return false;
		}
		return state.canBeReplaced() || state.is(ModTags.Blocks.CONSIDER_REPLACEABLE);
	}

	@Override
	public boolean isFinished(SpellThinkerEntity thinker) {
		return fizzled || (phase == PlanetoidPhase.GENERATION && placementIndex >= sortedGenerationPositions.size());
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		tag.putString("phase", phase.name());
		tag.putInt("phase_timer", phaseTimer);
		tag.putInt("placement_index", placementIndex);
		tag.putLong("seed", seed);
		tag.putBoolean("fizzled", fizzled);
		if (origin != null) {
			tag.putDouble("origin_x", origin.x);
			tag.putDouble("origin_y", origin.y);
			tag.putDouble("origin_z", origin.z);
		}
		if (template != null) {
			tag.putString("template_id", template.id());
		}
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		if (tag.contains("phase")) {
			try {
				this.phase = PlanetoidPhase.valueOf(tag.getString("phase"));
			} catch (IllegalArgumentException ignored) {}
		}
		this.phaseTimer = OptNbtUtils.getInt(tag, "phase_timer").orElse(0);
		this.placementIndex = OptNbtUtils.getInt(tag, "placement_index").orElse(0);
		this.seed = OptNbtUtils.getLong(tag, "seed").orElse(0L);
		this.fizzled = OptNbtUtils.getBoolean(tag, "fizzled").orElse(false);

		if (tag.contains("origin_x")) {
			this.origin = new Vec3(tag.getDouble("origin_x"), tag.getDouble("origin_y"), tag.getDouble("origin_z"));
		}

		String templateId = OptNbtUtils.getString(tag, "template_id").orElse("verdant");
		if ("desert".equalsIgnoreCase(templateId)) {
			this.template = PlanetoidTemplates.DESERT;
		} else {
			this.template = PlanetoidTemplates.VERDANT;
		}
	}
}
