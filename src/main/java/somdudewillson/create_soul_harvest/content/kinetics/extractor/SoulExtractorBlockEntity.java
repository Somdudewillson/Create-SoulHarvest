package somdudewillson.create_soul_harvest.content.kinetics.extractor;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.conduit.network.PotentiaNetwork;
import somdudewillson.create_soul_harvest.content.potentia.*;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SoulExtractorBlockEntity extends KineticBlockEntity {

	protected float currentExtractionPoints = 0.0f;
	protected long currentOutputRate = 0L;
	protected boolean isController = false;

	/**
	 * Virtual handler representing the extractor's back-face connection.
	 * Holds no internal storage; pushes potentia directly into connected systems every tick.
	 */
	private final IPotentiaHandler virtualHandler = new IPotentiaHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public SoulPotentiaStack getPotentiaInTank(int tank) {
			return SoulPotentiaStack.EMPTY;
		}

		@Override
		public long getTankCapacity(int tank) {
			return 0L;
		}

		@Override
		public boolean isPotentiaValid(int tank, SoulPotentiaStack stack) {
			return false;
		}

		@Override
		public long fill(SoulPotentiaStack resource, PotentiaAction action) {
			return 0L;
		}

		@Override
		public SoulPotentiaStack drain(SoulPotentiaStack resource, PotentiaAction action) {
			return SoulPotentiaStack.EMPTY;
		}

		@Override
		public SoulPotentiaStack drain(long maxDrain, PotentiaAction action) {
			return SoulPotentiaStack.EMPTY;
		}

		@Override
		public boolean canAccept(@Nullable Direction side) {
			return false;
		}

		@Override
		public boolean canPush(@Nullable Direction side) {
			return true;
		}
	};

	public SoulExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public SoulExtractorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SOUL_EXTRACTOR_BE.get(), pos, state);
	}

	public float getMinSpeed() {
		try {
			BlockState state = getBlockState();
			if (state.getBlock() instanceof IRotate rotate) {
				return rotate.getMinimumRequiredSpeedLevel().getSpeedValue();
			}
		} catch (Throwable ignored) {
		}
		return SoulExtractionMath.DEFAULT_MIN_RPM;
	}

	public float getCurrentExtractionPoints() {
		if (level != null && level.isClientSide()) {
			if (getSpeed() == 0 || isOverStressed() || !isSpeedRequirementFulfilled()) {
				return 0.0f;
			}
			BlockState state = getBlockState();
			if (state.getBlock() instanceof SoulExtractorBlock) {
				Direction facing = state.getValue(SoulExtractorBlock.FACING);
				if (!level.getBlockState(worldPosition.relative(facing)).is(ModTags.Blocks.SOUL_BUD_GROWTH_BLOCKS)) {
					return 0.0f;
				}
			}
			return SoulExtractionMath.calculatePoints(getSpeed(), getMinSpeed());
		}
		return currentExtractionPoints;
	}

	public long getCurrentOutputRate() {
		if (level != null && level.isClientSide()) {
			if (getSpeed() == 0 || isOverStressed() || !isSpeedRequirementFulfilled()) {
				return 0L;
			}
		}
		return currentOutputRate;
	}

	public boolean isController() {
		return isController;
	}

	@Nullable
	public ResourceLocation getProducedPotentiaType() {
		return null; // Produces pure untyped soul potentia
	}

	@Nullable
	public IPotentiaHandler getPotentiaHandler(@Nullable Direction side) {
		if (side == null || side == getBlockState().getValue(SoulExtractorBlock.FACING).getOpposite()) {
			return virtualHandler;
		}
		return null;
	}

	public void onNeighborUpdate() {
		// Target or adjacent network changed
	}

	@Override
	public void onSpeedChanged(float previousSpeed) {
		super.onSpeedChanged(previousSpeed);
		if (level != null && !level.isClientSide()) {
			if (getSpeed() == 0 || !isSpeedRequirementFulfilled()) {
				updateStatus(0.0f, 0L, this.isController);
			}
		}
	}

	public void updateStatus(float points, long outputRate, boolean controller) {
		boolean changed = Math.abs(this.currentExtractionPoints - points) > 0.001f
				|| this.currentOutputRate != outputRate
				|| this.isController != controller;

		this.currentExtractionPoints = points;
		this.currentOutputRate = outputRate;
		this.isController = controller;

		if (changed) {
			notifyUpdate();
		}
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
			updateStatus(0.0f, 0L, false);
		}
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

		boolean amController = lowestPos != null && lowestPos.equals(this.worldPosition);

		if (amController && !group.isEmpty()) {
			float totalPoints = 0.0f;
			List<SoulExtractorBlockEntity> activeExtractors = new ArrayList<>();
			List<Float> activePoints = new ArrayList<>();

			for (SoulExtractorBlockEntity extractor : group) {
				if (extractor.isOverStressed() || !extractor.isSpeedRequirementFulfilled()) {
					continue;
				}
				float pts = SoulExtractionMath.calculatePoints(extractor.getSpeed(), extractor.getMinSpeed());
				if (pts > 0.0f) {
					activeExtractors.add(extractor);
					activePoints.add(pts);
					totalPoints += pts;
				}
			}

			long totalExtracted = SoulExtractionMath.calculateTotalPotentia(totalPoints, SoulExtractionMath.DEFAULT_MAX_POTENTIA_PER_TICK);
			for (SoulExtractorBlockEntity extractor : group) {
				boolean isCtrl = extractor == this;
				float pts = (extractor.isOverStressed() || !extractor.isSpeedRequirementFulfilled())
						? 0.0f
						: SoulExtractionMath.calculatePoints(extractor.getSpeed(), extractor.getMinSpeed());
				long share = 0L;
				if (totalExtracted > 0 && totalPoints > 0.0f && pts > 0.0f) {
					share = SoulExtractionMath.calculateShare(pts, totalPoints, totalExtracted);
				}
				extractor.updateStatus(pts, share, isCtrl);
				if (share > 0) {
					extractor.pushPotentia(share, extractor.getProducedPotentiaType());
				}
			}
		}
	}

	public void pushPotentia(long amount, @Nullable ResourceLocation type) {
		if (amount <= 0 || level == null) {
			return;
		}

		Direction facing = getBlockState().getValue(SoulExtractorBlock.FACING);
		Direction outputSide = facing.getOpposite();
		BlockPos outputPos = worldPosition.relative(outputSide);
		if (!level.isLoaded(outputPos)) {
			return;
		}

		SoulPotentiaStack stack = new SoulPotentiaStack(amount, type);
		BlockState outputState = level.getBlockState(outputPos);
		if (outputState.getBlock() instanceof IPotentiaTransporter) {
			PotentiaNetwork.distributeStack(level, worldPosition, outputSide, stack);
		} else {
			IPotentiaHandler targetHandler = PotentiaHelper.getHandler(level, outputPos, facing);
			if (targetHandler != null && targetHandler.canAccept(facing) && targetHandler.isPotentiaValid(0, stack)) {
				targetHandler.fill(stack, IPotentiaHandler.PotentiaAction.EXECUTE);
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

		if (isOverStressed() || (!isSpeedRequirementFulfilled() && getSpeed() != 0)) {
			return true;
		}

		PotentiaTooltipHelper.langBuilder().translate("gui.goggles.soul_extractor")
				.forGoggles(tooltip);

		float points = getCurrentExtractionPoints();
		long outputRate = getCurrentOutputRate();

		PotentiaTooltipHelper.langBuilder().translate("gui.goggles.extraction_points_heading")
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);
		PotentiaTooltipHelper.langBuilder().text(String.format(Locale.ROOT, "%.1f", points))
				.style(ChatFormatting.WHITE)
				.forGoggles(tooltip, 1);

		PotentiaTooltipHelper.langBuilder().translate("gui.goggles.multiblock_status_heading")
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);
		if (points > 0.0f) {
			if (isController) {
				PotentiaTooltipHelper.langBuilder().translate("gui.goggles.multiblock_controller")
						.style(ChatFormatting.GREEN)
						.forGoggles(tooltip, 1);
			} else {
				PotentiaTooltipHelper.langBuilder().translate("gui.goggles.multiblock_member")
						.style(ChatFormatting.AQUA)
						.forGoggles(tooltip, 1);
			}
		} else {
			PotentiaTooltipHelper.langBuilder().translate("gui.goggles.multiblock_inactive")
					.style(ChatFormatting.DARK_GRAY)
					.forGoggles(tooltip, 1);
		}

		if (outputRate > 0) {
			PotentiaTooltipHelper.langBuilder().translate("gui.goggles.output_rate_heading")
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);
			PotentiaTooltipHelper.langBuilder().translate("gui.goggles.output_rate_value", outputRate)
					.style(ChatFormatting.AQUA)
					.forGoggles(tooltip, 1);
		}

		PotentiaTooltipHelper.langBuilder().translate("gui.goggles.potentia_output_type_heading")
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);
		PotentiaTooltipHelper.langBuilder().text(ChatFormatting.GOLD,
				PotentiaTooltipHelper.getPotentiaTypeDisplayName(new SoulPotentiaStack(1, getProducedPotentiaType())))
				.forGoggles(tooltip, 1);

		return true;
	}

	//? >= 1.21 {
	@Override
	protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		compound.putFloat("ExtractionPoints", currentExtractionPoints);
		compound.putLong("OutputRate", currentOutputRate);
		compound.putBoolean("IsController", isController);
	}

	@Override
	protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		currentExtractionPoints = compound.getFloat("ExtractionPoints");
		currentOutputRate = compound.getLong("OutputRate");
		isController = compound.getBoolean("IsController");
	}
	//?} else {
	/*@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putFloat("ExtractionPoints", currentExtractionPoints);
		compound.putLong("OutputRate", currentOutputRate);
		compound.putBoolean("IsController", isController);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		currentExtractionPoints = compound.getFloat("ExtractionPoints");
		currentOutputRate = compound.getLong("OutputRate");
		isController = compound.getBoolean("IsController");
	}
	*///?}
}
