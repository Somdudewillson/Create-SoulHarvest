package somdudewillson.create_soul_harvest.content.kinetics.extractor;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.conduit.network.PotentiaNetwork;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaHelper;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaTank;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.ArrayList;
import java.util.List;

public class SoulExtractorBlockEntity extends KineticBlockEntity {

	public static final long TANK_CAPACITY = 2000L;

	protected final PotentiaTank tank = new PotentiaTank(TANK_CAPACITY, t -> notifyUpdate());
	protected float currentExtractionPoints = 0.0f;
	protected boolean isController = false;

	public SoulExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public SoulExtractorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SOUL_EXTRACTOR_BE.get(), pos, state);
	}

	public PotentiaTank getTank() {
		return tank;
	}

	public float getCurrentExtractionPoints() {
		return currentExtractionPoints;
	}

	@Nullable
	public IPotentiaHandler getPotentiaHandler(@Nullable Direction side) {
		if (side == null || side == getBlockState().getValue(SoulExtractorBlock.FACING).getOpposite()) {
			return tank;
		}
		return null;
	}

	public void onNeighborUpdate() {
		// Target or adjacent network changed
	}

	@Override
	public void tick() {
		super.tick();

		if (level == null || level.isClientSide()) {
			return;
		}

		BlockState state = getBlockState();
		if (!(state.getBlock() instanceof SoulExtractorBlock)) {
			return;
		}

		Direction facing = state.getValue(SoulExtractorBlock.FACING);
		BlockPos targetPos = worldPosition.relative(facing);

		if (level.getBlockState(targetPos).is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS)) {
			tickMultiblock(targetPos);
		} else {
			this.currentExtractionPoints = 0.0f;
			this.isController = false;
		}

		pushPotentiaOutBack(facing);
	}

	private void tickMultiblock(BlockPos targetPos) {
		List<SoulExtractorBlockEntity> group = new ArrayList<>();
		BlockPos lowestPos = null;

		for (Direction d : Direction.values()) {
			BlockPos adjPos = targetPos.relative(d);
			if (!level.isLoaded(adjPos)) continue;

			BlockState adjState = level.getBlockState(adjPos);
			if (adjState.getBlock() instanceof SoulExtractorBlock) {
				Direction adjFacing = adjState.getValue(SoulExtractorBlock.FACING);
				if (adjFacing == d.getOpposite()) {
					BlockEntity be = level.getBlockEntity(adjPos);
					if (be instanceof SoulExtractorBlockEntity extractor) {
						group.add(extractor);
						if (lowestPos == null || comparePos(adjPos, lowestPos) < 0) {
							lowestPos = adjPos;
						}
					}
				}
			}
		}

		this.isController = lowestPos != null && lowestPos.equals(this.worldPosition);

		if (this.isController && !group.isEmpty()) {
			float totalPoints = 0.0f;
			List<SoulExtractorBlockEntity> activeExtractors = new ArrayList<>();
			List<Float> activePoints = new ArrayList<>();

			for (SoulExtractorBlockEntity extractor : group) {
				if (extractor.isOverStressed()) {
					extractor.currentExtractionPoints = 0.0f;
					continue;
				}
				float pts = SoulExtractionMath.calculatePoints(extractor.getSpeed());
				extractor.currentExtractionPoints = pts;
				if (pts > 0.0f) {
					activeExtractors.add(extractor);
					activePoints.add(pts);
					totalPoints += pts;
				}
			}

			long totalExtracted = SoulExtractionMath.calculateTotalPotentia(totalPoints, SoulExtractionMath.DEFAULT_MAX_POTENTIA_PER_TICK);
			if (totalExtracted > 0 && totalPoints > 0.0f) {
				for (int i = 0; i < activeExtractors.size(); i++) {
					SoulExtractorBlockEntity extractor = activeExtractors.get(i);
					float pts = activePoints.get(i);
					long share = SoulExtractionMath.calculateShare(pts, totalPoints, totalExtracted);
					if (share > 0) {
						extractor.tank.fill(new SoulPotentiaStack(share, null), IPotentiaHandler.PotentiaAction.EXECUTE);
					}
				}
			}
		}
	}

	private void pushPotentiaOutBack(Direction facing) {
		if (tank.getPotentia().isEmpty()) {
			return;
		}

		Direction outputSide = facing.getOpposite();
		BlockPos outputPos = worldPosition.relative(outputSide);
		if (!level.isLoaded(outputPos)) {
			return;
		}

		BlockState outputState = level.getBlockState(outputPos);
		if (outputState.getBlock() instanceof IPotentiaTransporter) {
			PotentiaNetwork.distributeFrom(level, worldPosition, outputSide, tank);
		} else {
			IPotentiaHandler targetHandler = PotentiaHelper.getHandler(level, outputPos, facing);
			if (targetHandler != null && targetHandler.canAccept(facing)) {
				SoulPotentiaStack available = tank.getPotentia();
				long filled = targetHandler.fill(available, IPotentiaHandler.PotentiaAction.EXECUTE);
				if (filled > 0) {
					tank.drain(filled, IPotentiaHandler.PotentiaAction.EXECUTE);
				}
			}
		}
	}

	private static int comparePos(BlockPos a, BlockPos b) {
		int diffY = Integer.compare(a.getY(), b.getY());
		if (diffY != 0) return diffY;
		int diffX = Integer.compare(a.getX(), b.getX());
		if (diffX != 0) return diffX;
		return Integer.compare(a.getZ(), b.getZ());
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		tooltip.add(Component.literal("§dSoul Extraction:"));
		tooltip.add(Component.literal(" §7Extraction Points: §f" + String.format("%.1f", currentExtractionPoints)));
		if (isController) {
			tooltip.add(Component.literal(" §7Multiblock Status: §aController"));
		}
		tooltip.add(Component.literal(" §7Potentia Stored: §b" + tank.getPotentia().getAmount() + " / " + tank.getTankCapacity(0) + " mB"));
		return true;
	}

	//? >= 1.21 {
	@Override
	protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		tank.writeToNBT(compound);
		compound.putFloat("ExtractionPoints", currentExtractionPoints);
		compound.putBoolean("IsController", isController);
	}

	@Override
	protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		tank.readFromNBT(compound);
		currentExtractionPoints = compound.getFloat("ExtractionPoints");
		isController = compound.getBoolean("IsController");
	}
	//?} else {
	/*@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		tank.writeToNBT(compound);
		compound.putFloat("ExtractionPoints", currentExtractionPoints);
		compound.putBoolean("IsController", isController);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		tank.readFromNBT(compound);
		currentExtractionPoints = compound.getFloat("ExtractionPoints");
		isController = compound.getBoolean("IsController");
	}
	*///?}
}
