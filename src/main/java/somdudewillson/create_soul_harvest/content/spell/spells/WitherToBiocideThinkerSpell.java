package somdudewillson.create_soul_harvest.content.spell.spells;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.BiocideGasBlock;
import somdudewillson.create_soul_harvest.content.entity.SpellThinkerEntity;
import somdudewillson.create_soul_harvest.content.spell.SimplePersistentGlyphSpell;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModFluids;
import somdudewillson.create_soul_harvest.util.AdvancementHelper;

public class WitherToBiocideThinkerSpell extends SimplePersistentGlyphSpell {
	public static final ResourceLocation ID = SoulHarvestMod.id("wither_to_biocide_thinker");
	
	private static final int MAX_BUDGET = 4;

	private final Queue<BlockPos> queue = new ArrayDeque<>();
	private final Set<BlockPos> visited = new HashSet<>();
	private boolean finished = false;

	public WitherToBiocideThinkerSpell() {
		this(ID);
	}

	public WitherToBiocideThinkerSpell(ResourceLocation id) {
		super(id);
	}

	public WitherToBiocideThinkerSpell(BlockPos startPos) {
		this(ID);
		this.queue.add(startPos);
		this.visited.add(startPos);
	}

	@Override
	public boolean cast(Level level, LivingEntity caster) {
		return false;
	}

	public static boolean isWitherSlurry(BlockState state, FluidState fluid) {
		if (state.is(ModBlocks.WITHER_SLURRY.get())) return true;
		return fluid.getType() == ModFluids.WITHER_SLURRY.get() || fluid.getType() == ModFluids.WITHER_SLURRY_FLOWING.get();
	}

	@Override
	public void serverTick(SpellThinkerEntity thinker, ServerLevel level) {
		if (isFinished(thinker)) return;

		int budget = Math.max(1, Math.min(MAX_BUDGET, this.queue.size() / 15 + 1));
		int converted = 0;

		while (!this.queue.isEmpty() && converted < budget) {
			BlockPos current = this.queue.poll();
			if (!level.isLoaded(current)) {
				continue;
			}

			BlockState state = level.getBlockState(current);
			FluidState fluid = level.getFluidState(current);

			if (isWitherSlurry(state, fluid)) {
				BlockState gasState = ModBlocks.BIOCIDE_GAS.get().defaultBlockState()
						.setValue(BiocideGasBlock.DENSITY, fluid.isSource() ? BiocideGasBlock.MAX_DENSITY : 3)
						.setValue(BiocideGasBlock.SETTLED, false);
				level.setBlockAndUpdate(current, gasState);
				level.scheduleTick(current, ModBlocks.BIOCIDE_GAS.get(), 10 + level.random.nextInt(10));

				thinker.setPos(current.getX() + 0.5, current.getY() + 0.5, current.getZ() + 0.5);

				level.playSound(null, current, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.4F + level.random.nextFloat() * 0.4F);
				level.sendParticles(ParticleTypes.LARGE_SMOKE, current.getX() + 0.5, current.getY() + 0.5, current.getZ() + 0.5, 4, 0.2, 0.2, 0.2, 0.02);
				level.sendParticles(ParticleTypes.SOUL, current.getX() + 0.5, current.getY() + 0.5, current.getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.02);

				AABB box = AABB.ofSize(current.getCenter(), 16, 16, 16);
				for (Player player : level.players()) {
					if (box.contains(player.position()) && player instanceof ServerPlayer serverPlayer) {
						AdvancementHelper.grantAdvancement(serverPlayer, "made_biocide_gas");
					}
				}

				converted++;

				for (Direction dir : Direction.values()) {
					BlockPos neighbor = current.relative(dir);
					if (this.visited.add(neighbor) && level.isLoaded(neighbor)) {
						if (isWitherSlurry(level.getBlockState(neighbor), level.getFluidState(neighbor))) {
							this.queue.add(neighbor);
						}
					}
				}
			}
		}

		if (this.queue.isEmpty()) {
			this.finished = true;
		}
	}

	@Override
	public boolean isFinished(SpellThinkerEntity thinker) {
		return this.finished || (this.queue.isEmpty() && !this.visited.isEmpty());
	}

	@Override
	public void writeSaveData(CompoundTag tag) {
		long[] qArray = this.queue.stream().mapToLong(BlockPos::asLong).toArray();
		tag.putLongArray("Queue", qArray);
		long[] vArray = this.visited.stream().mapToLong(BlockPos::asLong).toArray();
		tag.putLongArray("Visited", vArray);
		tag.putBoolean("Finished", this.finished);
	}

	@Override
	public void readSaveData(CompoundTag tag) {
		this.queue.clear();
		for (long l : tag.getLongArray("Queue")) {
			this.queue.add(BlockPos.of(l));
		}
		this.visited.clear();
		for (long l : tag.getLongArray("Visited")) {
			this.visited.add(BlockPos.of(l));
		}
		this.finished = tag.getBoolean("Finished");
	}
}
