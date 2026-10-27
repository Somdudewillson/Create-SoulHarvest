package somdudewillson.create_soul_harvest.content.block;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;

import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.crafting.RecipeType;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.recipe.MidasFluidBoostRecipe;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.*;

//? >= 1.21 {
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
//?} else {
/*import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
*///?}

public class MidasCoreBlockEntity extends BlockEntity implements IHaveGoggleInformation {

	public static final int DEFAULT_CONVERSION_INTERVAL = 20 * 80; // 80 seconds
	public static final int WEAR_OFF_THRESHOLD = 20*2; // 2 seconds
	public static final int PERIODIC_SYNC_INTERVAL = 20*5; // 5 seconds

	private float conversionTimer = DEFAULT_CONVERSION_INTERVAL;
	private float displayedConversionTimer = DEFAULT_CONVERSION_INTERVAL;
	private int boostTimeRemaining = 0;
	private float currentBoostRate = 1.0f;
	private int totalBoostDuration = 0;
	private int syncCooldown = 0;

	private final Map<BlockPos, Integer> recentlyConverted = new HashMap<>();
	private final MidasFluidHandler fluidHandler = new MidasFluidHandler();

	//? < 1.21 {
	/*private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> fluidHandler);
	*///?}

	public MidasCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MIDAS_CORE_BE.get(), pos, blockState);
	}

	public IFluidHandler getFluidHandler() {
		return fluidHandler;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, MidasCoreBlockEntity be) {
		if (be.boostTimeRemaining > 0) {
			be.boostTimeRemaining--;
			if (be.boostTimeRemaining <= 0) {
				be.currentBoostRate = 1.0f;
				be.totalBoostDuration = 0;
			}

			if (level.random.nextFloat() < 0.12f) {
				double px = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 1.1;
				double py = pos.getY() + 0.5 + (level.random.nextDouble() - 0.5) * 1.1;
				double pz = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 1.1;
				level.addParticle(ParticleTypes.WAX_ON, px, py, pz, 0, 0.02, 0);
			}
		}

		be.conversionTimer -= be.currentBoostRate;
		be.displayedConversionTimer -= be.currentBoostRate;

		if (be.conversionTimer <= 0) {
			be.conversionTimer += DEFAULT_CONVERSION_INTERVAL;
			if (be.conversionTimer <= 0) {
				be.conversionTimer = DEFAULT_CONVERSION_INTERVAL;
			}
			be.displayedConversionTimer = be.conversionTimer;
		}

		float diff = be.conversionTimer - be.displayedConversionTimer;
		if (Math.abs(diff) > 40.0f) {
			be.displayedConversionTimer = be.conversionTimer;
		} else {
			be.displayedConversionTimer += diff * (1f/PERIODIC_SYNC_INTERVAL);
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MidasCoreBlockEntity be) {
		if (be.boostTimeRemaining > 0) {
			be.boostTimeRemaining--;
			if (be.boostTimeRemaining <= 0) {
				be.currentBoostRate = 1.0f;
				be.totalBoostDuration = 0;
				be.setChanged();
				be.sync();
			}
		}

		be.conversionTimer -= be.currentBoostRate;
		if (be.conversionTimer <= 0) {
			be.conversionTimer += DEFAULT_CONVERSION_INTERVAL;
			if (be.conversionTimer <= 0) {
				be.conversionTimer = DEFAULT_CONVERSION_INTERVAL;
			}
			if (level instanceof ServerLevel serverLevel) {
				be.performConversion(serverLevel, pos);
			}
			be.setChanged();
			be.sync();
		}

		be.syncCooldown++;
		if (be.syncCooldown >= PERIODIC_SYNC_INTERVAL) {
			be.syncCooldown = 0;
			be.sync();
		}

		if (!be.recentlyConverted.isEmpty() && level instanceof ServerLevel serverLevel) {
			be.tickRecentlyConverted(serverLevel);
		}
	}

	public void performConversion(ServerLevel level, BlockPos pos) {
		final int brancherCount = 1;
		final int brancherMaxNewBlocks = 10;
		final int brancherMaxDistance = 16;
		final int brancherMaxSteps = brancherMaxNewBlocks*2;

		Block goldFoam = ModBlocks.GOLD_FOAM.get();
		RandomSource random = level.getRandom();

		for (int b = 0; b < brancherCount; b++) {
			BlockPos brancherPos = pos;
			Direction brancherDir = null;
			int brancherNewBlocks = 0;

			for (int step = 0; step < brancherMaxSteps; step++) {
				List<Direction> turnOptions = new ArrayList<>();
				for (Direction possibleDir : Direction.values()) {
					BlockPos possibleNextPos = brancherPos.relative(possibleDir);
					if (possibleNextPos.distManhattan(pos) > brancherMaxDistance) continue;
					if (brancherDir != null && possibleDir == brancherDir.getOpposite()) continue;
					if (!level.isLoaded(possibleNextPos)) continue;

					BlockState targetState = level.getBlockState(possibleNextPos);
					if (!targetState.is(goldFoam) && !targetState.is(ModTags.Blocks.MIDAS_CAN_CONVERT)) {
						continue;
					}
					BlockState aboveState = level.getBlockState(possibleNextPos.above());

					int weight = 2;
					if (step > 0 && !targetState.is(goldFoam)) {
						weight += 6;
					}
					if (aboveState.isAir()) {
						weight += 2;
					}
					if (possibleDir == brancherDir) {
						weight *= 4;
					}

					for (int w = 0; w < weight; w++) {
						turnOptions.add(possibleDir);
					}
				}

				if (turnOptions.isEmpty()) break;

				brancherDir = turnOptions.get(random.nextInt(turnOptions.size()));
				brancherPos = brancherPos.relative(brancherDir);

				BlockState blockAtNewPos = level.getBlockState(brancherPos);
				if (!blockAtNewPos.is(goldFoam)) {
					level.setBlockAndUpdate(brancherPos, goldFoam.defaultBlockState());
					brancherNewBlocks++;

					level.sendParticles(ParticleTypes.WAX_ON,
							brancherPos.getX() + 0.5,
							brancherPos.getY() + 0.5,
							brancherPos.getZ() + 0.5,
							10, 0.35, 0.35, 0.35, 0.05);

					if (recentlyConverted.size() < 64) {
						recentlyConverted.put(brancherPos, 60);
					}

					if (brancherNewBlocks >= brancherMaxNewBlocks) break;
				}
			}
		}
	}

	private void tickRecentlyConverted(ServerLevel level) {
		Block goldFoam = ModBlocks.GOLD_FOAM.get();
		Iterator<Map.Entry<BlockPos, Integer>> iterator = recentlyConverted.entrySet().iterator();
		RandomSource random = level.getRandom();
		while (iterator.hasNext()) {
			Map.Entry<BlockPos, Integer> entry = iterator.next();
			BlockPos targetPos = entry.getKey();
			int remaining = entry.getValue() - 1;
			if (remaining <= 0 || !level.isLoaded(targetPos) || !level.getBlockState(targetPos).is(goldFoam)) {
				iterator.remove();
				continue;
			}
			entry.setValue(remaining);
			if (random.nextFloat() < 0.25f) {
				double px = targetPos.getX() + random.nextDouble();
				double py = targetPos.getY() + random.nextDouble();
				double pz = targetPos.getZ() + random.nextDouble();
				level.sendParticles(ParticleTypes.WAX_ON, px, py, pz, 1, 0, 0, 0, 0.02);
			}
		}
	}

	private MidasFluidBoostRecipe findMatchingRecipe(FluidStack stack) {
		if (level == null || stack.isEmpty()) return null;
		//? >= 1.21 {
		RecipeType<MidasFluidBoostRecipe> type = ModRecipeTypes.MIDAS_FLUID_BOOST.getType();
		for (RecipeHolder<MidasFluidBoostRecipe> holder : level.getRecipeManager().getAllRecipesFor(type)) {
			if (holder.value().matchesFluid(stack)) {
				return holder.value();
			}
		}
		//?} else {
		/*RecipeType<MidasFluidBoostRecipe> type = ModRecipeTypes.MIDAS_FLUID_BOOST.getType();
		for (MidasFluidBoostRecipe recipe : level.getRecipeManager().getAllRecipesFor(type)) {
			if (recipe.matchesFluid(stack)) {
				return recipe;
			}
		}
		*///?}
		return null;
	}

	public void sync() {
		if (level != null && !level.isClientSide()) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(worldPosition, state, state, 2);
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		langBuilder().translate("gui.goggles.midas_core")
				.forGoggles(tooltip);

		float secondsLeft = Math.max(0.0f, displayedConversionTimer / currentBoostRate / 20.0f);
		langBuilder().translate("gui.goggles.conversion_countdown_heading")
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);
		langBuilder().text(String.format(Locale.ROOT, "%.1fs", secondsLeft))
				.style(ChatFormatting.AQUA)
				.forGoggles(tooltip, 1);

		langBuilder().translate("gui.goggles.boost_heading")
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);
		if (boostTimeRemaining > 0) {
			int totalSeconds = boostTimeRemaining / 20;
			int minutes = totalSeconds / 60;
			int seconds = totalSeconds % 60;
			String timeStr = minutes > 0 ? String.format(Locale.ROOT, "%dm %ds", minutes, seconds) : String.format(Locale.ROOT, "%ds", seconds);
			langBuilder().text("")
					.text(ChatFormatting.AQUA, String.format(Locale.ROOT, "%.1fx", currentBoostRate))
					.space()
					.translate("gui.goggles.boost_active", timeStr)
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip, 1);
		} else {
			langBuilder().translate("gui.goggles.boost_inactive")
					.style(ChatFormatting.DARK_GRAY)
					.forGoggles(tooltip, 1);
		}

		return true;
	}
	
	protected static LangBuilder langBuilder() {
		return new LangBuilder(SoulHarvestMod.MOD_ID);
	}

	//? >= 1.21 {
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putFloat("ConversionTimer", conversionTimer);
		tag.putInt("BoostTimeRemaining", boostTimeRemaining);
		tag.putFloat("CurrentBoostRate", currentBoostRate);
		tag.putInt("TotalBoostDuration", totalBoostDuration);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		conversionTimer = tag.contains("ConversionTimer") ? tag.getFloat("ConversionTimer") : DEFAULT_CONVERSION_INTERVAL;
		displayedConversionTimer = conversionTimer;
		boostTimeRemaining = tag.getInt("BoostTimeRemaining");
		currentBoostRate = tag.contains("CurrentBoostRate") ? tag.getFloat("CurrentBoostRate") : 1.0f;
		totalBoostDuration = tag.getInt("TotalBoostDuration");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putFloat("ConversionTimer", conversionTimer);
		tag.putInt("BoostTimeRemaining", boostTimeRemaining);
		tag.putFloat("CurrentBoostRate", currentBoostRate);
		return tag;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
		super.onDataPacket(net, pkt, lookupProvider);
		CompoundTag tag = pkt.getTag();
		if (tag != null) {
			conversionTimer = tag.getFloat("ConversionTimer");
			boostTimeRemaining = tag.getInt("BoostTimeRemaining");
			currentBoostRate = tag.getFloat("CurrentBoostRate");
			if (Math.abs(displayedConversionTimer - conversionTimer) > 40.0f) {
				displayedConversionTimer = conversionTimer;
			}
		}
	}
	//?} else {
	/*@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putFloat("ConversionTimer", conversionTimer);
		tag.putInt("BoostTimeRemaining", boostTimeRemaining);
		tag.putFloat("CurrentBoostRate", currentBoostRate);
		tag.putInt("TotalBoostDuration", totalBoostDuration);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		conversionTimer = tag.contains("ConversionTimer") ? tag.getFloat("ConversionTimer") : DEFAULT_CONVERSION_INTERVAL;
		displayedConversionTimer = conversionTimer;
		boostTimeRemaining = tag.getInt("BoostTimeRemaining");
		currentBoostRate = tag.contains("CurrentBoostRate") ? tag.getFloat("CurrentBoostRate") : 1.0f;
		totalBoostDuration = tag.getInt("TotalBoostDuration");
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putFloat("ConversionTimer", conversionTimer);
		tag.putInt("BoostTimeRemaining", boostTimeRemaining);
		tag.putFloat("CurrentBoostRate", currentBoostRate);
		return tag;
	}

	@Override
	public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag = pkt.getTag();
		if (tag != null) {
			conversionTimer = tag.getFloat("ConversionTimer");
			boostTimeRemaining = tag.getInt("BoostTimeRemaining");
			currentBoostRate = tag.getFloat("CurrentBoostRate");
			if (Math.abs(displayedConversionTimer - conversionTimer) > 40.0f) {
				displayedConversionTimer = conversionTimer;
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return fluidOptional.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidOptional.invalidate();
	}
	*///?}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public float getConversionTimer() {
		return conversionTimer;
	}

	public float getDisplayedConversionTimer() {
		return displayedConversionTimer;
	}

	public int getBoostTimeRemaining() {
		return boostTimeRemaining;
	}

	public float getCurrentBoostRate() {
		return currentBoostRate;
	}

	private class MidasFluidHandler implements IFluidHandler {
		@Override
		public int getTanks() {
			return 1;
		}

		@NotNull
		@Override
		public FluidStack getFluidInTank(int tank) {
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return 1000;
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			return findMatchingRecipe(stack) != null;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (resource.isEmpty()) return 0;
			MidasFluidBoostRecipe recipe = findMatchingRecipe(resource);
			if (recipe == null) return 0;
			int requiredAmount = recipe.getRequiredAmount();
			if (resource.getAmount() < requiredAmount) return 0;

			float incomingRate = recipe.getMultiplier();
			boolean canAccept = boostTimeRemaining <= 0
					|| (boostTimeRemaining <= WEAR_OFF_THRESHOLD && incomingRate >= currentBoostRate);
			if (!canAccept) return 0;

			if (action.execute()) {
				boostTimeRemaining = recipe.getDuration();
				totalBoostDuration = recipe.getDuration();
				currentBoostRate = incomingRate;
				setChanged();
				sync();
			}
			return requiredAmount;
		}

		@NotNull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			return FluidStack.EMPTY;
		}

		@NotNull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			return FluidStack.EMPTY;
		}
	}
}
