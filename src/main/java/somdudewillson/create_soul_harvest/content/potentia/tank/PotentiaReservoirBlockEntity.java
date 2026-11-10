package somdudewillson.create_soul_harvest.content.potentia.tank;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.conduit.network.PotentiaNetwork;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaTooltipHelper;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

import java.util.List;
import java.util.Locale;

public class PotentiaReservoirBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, Clearable {

	public static final long DEFAULT_CAPACITY = 16000L;

	protected FilteringBehaviour filtering;
	@Nullable
	protected ResourceLocation filterEntityType;
	protected ReservoirMode mode = ReservoirMode.CONSUME;

	protected final CreativePotentiaTank creativeTank = new CreativePotentiaTank(
			DEFAULT_CAPACITY,
			this::getMode,
			this::getFilterEntityType
	);

	protected final LerpedFloat crystalLevel = LerpedFloat.linear().startWithValue(0.0f);

	//? if forge {
	/*protected net.minecraftforge.common.util.LazyOptional<IPotentiaHandler> potentiaCapability =
			net.minecraftforge.common.util.LazyOptional.of(() -> creativeTank);
	*///?}

	public PotentiaReservoirBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		if (state.hasProperty(PotentiaReservoirBlock.MODE)) {
			this.mode = state.getValue(PotentiaReservoirBlock.MODE);
			this.crystalLevel.startWithValue(this.mode.isSupply() ? 1.0f : 0.0f);
		}
	}

	public PotentiaReservoirBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.POTENTIA_RESERVOIR_BE.get(), pos, state);
		if (state.hasProperty(PotentiaReservoirBlock.MODE)) {
			this.mode = state.getValue(PotentiaReservoirBlock.MODE);
			this.crystalLevel.startWithValue(this.mode.isSupply() ? 1.0f : 0.0f);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		this.crystalLevel.startWithValue(getMode().isSupply() ? 1.0f : 0.0f);
	}

	public ReservoirMode getMode() {
		BlockState state = getBlockState();
		if (state.hasProperty(PotentiaReservoirBlock.MODE)) {
			return state.getValue(PotentiaReservoirBlock.MODE);
		}
		return mode;
	}

	public void setMode(ReservoirMode mode) {
		this.mode = mode;
		notifyUpdate();
	}

	@Nullable
	public ResourceLocation getFilterEntityType() {
		return filterEntityType;
	}

	public SoulPotentiaStack getPotentia() {
		return creativeTank.getPotentia();
	}

	public LerpedFloat getCrystalLevel() {
		return crystalLevel;
	}

	@Nullable
	public IPotentiaHandler getPotentiaHandler(@Nullable Direction side) {
		return creativeTank;
	}

	//? if forge {
	/*@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
		if (cap == somdudewillson.create_soul_harvest.registry.ModCapabilities.POTENTIA_HANDLER) {
			return potentiaCapability.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		potentiaCapability.invalidate();
	}
	*///?}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		filtering = new FilteringBehaviour(this, new ReservoirValueBoxTransform())
				.withPredicate(stack -> stack.getItem() instanceof SpawnEggItem)
				.withCallback(this::onFilterChanged);
		behaviours.add(filtering);
	}
	
	public void setFilter(ItemStack stack) {
		onFilterChanged(stack);
	}

	protected void onFilterChanged(ItemStack stack) {
		if (stack.getItem() instanceof SpawnEggItem egg) {
			//? >= 1.21 {
			EntityType<?> type = egg.getType(stack);
			//?} else {
			/*EntityType<?> type = egg.getType(stack.getTag());
			*///?}
			this.filterEntityType = BuiltInRegistries.ENTITY_TYPE.getKey(type);
		} else {
			this.filterEntityType = null;
		}
		notifyUpdate();
	}

	@Override
	public void clearContent() {
		if (filtering != null) {
			filtering.setFilter(ItemStack.EMPTY);
		}
		this.filterEntityType = null;
		notifyUpdate();
	}

	@Override
	public void tick() {
		super.tick();

		float target = getMode().isSupply() ? 1.0f : 0.0f;
		crystalLevel.chase(target, 0.2f, LerpedFloat.Chaser.EXP);
		crystalLevel.tickChaser();

		if (level != null && (!level.isClientSide() || isVirtual()) && getMode().isSupply()) {
			for (Direction d : Direction.values()) {
				BlockPos neighborPos = worldPosition.relative(d);
				if (level.getBlockState(neighborPos).getBlock() instanceof IPotentiaTransporter) {
					PotentiaNetwork.distributeFrom(level, worldPosition, d, creativeTank);
				}
			}
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		PotentiaTooltipHelper.langBuilder().translate("gui.goggles.potentia_reservoir")
				.forGoggles(tooltip);

		boolean isSupply = getMode().isSupply();
		PotentiaTooltipHelper.addToGoggles(
				PotentiaTooltipHelper.langBuilder()
						.translate("gui.goggles.potentia_reservoir.mode")
						.style(ChatFormatting.GRAY)
						.space()
						.text(isSupply ? ChatFormatting.GOLD : ChatFormatting.LIGHT_PURPLE,
								Component.translatable("create_soul_harvest.potentia_reservoir.mode." + getMode().getSerializedName()).getString()),
				tooltip, 1
		);

		if (filterEntityType != null) {
			String entityName = BuiltInRegistries.ENTITY_TYPE.getOptional(filterEntityType)
					.map(type -> type.getDescription().getString())
					.orElse(filterEntityType.toString());
			PotentiaTooltipHelper.addToGoggles(
					PotentiaTooltipHelper.langBuilder()
							.translate("gui.goggles.potentia_type_heading")
							.style(ChatFormatting.GRAY)
							.space()
							.text(ChatFormatting.AQUA, entityName),
					tooltip, 1
			);
		} else {
			PotentiaTooltipHelper.addToGoggles(
					PotentiaTooltipHelper.langBuilder()
							.translate(isSupply ? "gui.goggles.potentia_reservoir.untyped_supply" : "gui.goggles.potentia_reservoir.accept_all")
							.style(ChatFormatting.DARK_GRAY),
					tooltip, 1
			);
		}

		return true;
	}

	//? >= 1.21 {
	@Override
	protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		compound.putString("Mode", getMode().getSerializedName());
		if (filterEntityType != null) {
			compound.putString("FilterEntityType", filterEntityType.toString());
		}
	}

	@Override
	protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (compound.contains("Mode")) {
			try {
				this.mode = ReservoirMode.valueOf(compound.getString("Mode").toUpperCase(Locale.ROOT));
				this.crystalLevel.startWithValue(this.mode.isSupply() ? 1.0f : 0.0f);
			} catch (Exception ignored) {
			}
		}
		if (compound.contains("FilterEntityType")) {
			this.filterEntityType = ResourceLocation.tryParse(compound.getString("FilterEntityType"));
		} else {
			this.filterEntityType = null;
		}
	}
	//?} else {
	/*@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("Mode", getMode().getSerializedName());
		if (filterEntityType != null) {
			compound.putString("FilterEntityType", filterEntityType.toString());
		}
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		if (compound.contains("Mode")) {
			try {
				this.mode = ReservoirMode.valueOf(compound.getString("Mode").toUpperCase(Locale.ROOT));
				this.crystalLevel.startWithValue(this.mode.isSupply() ? 1.0f : 0.0f);
			} catch (Exception ignored) {
			}
		}
		if (compound.contains("FilterEntityType")) {
			this.filterEntityType = ResourceLocation.tryParse(compound.getString("FilterEntityType"));
		} else {
			this.filterEntityType = null;
		}
	}
	*///?}

	public static class ReservoirValueBoxTransform extends ValueBoxTransform.Sided {
		@Override
		protected Vec3 getSouthLocation() {
			return new Vec3(8.0 / 16.0, 8.0 / 16.0, 16.05 / 16.0);
		}

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			return direction.getAxis() == Direction.Axis.Y;
		}
	}
}
