package somdudewillson.create_soul_harvest.content.potentia.tank;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import somdudewillson.create_soul_harvest.registry.ModBlockEntities;

public class PotentiaReservoirBlock extends Block implements IBE<PotentiaReservoirBlockEntity>, IWrenchable {

	public static final EnumProperty<ReservoirMode> MODE = EnumProperty.create("mode", ReservoirMode.class);

	public PotentiaReservoirBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(MODE, ReservoirMode.CONSUME));
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!level.isClientSide()) {
			BlockState newState = state.cycle(MODE);
			level.setBlock(pos, newState, 3);
			IWrenchable.playRotateSound(level, pos);
			if (level.getBlockEntity(pos) instanceof PotentiaReservoirBlockEntity be) {
				be.setMode(newState.getValue(MODE));
			}
			Player player = context.getPlayer();
			if (player != null) {
				ReservoirMode mode = newState.getValue(MODE);
				player.displayClientMessage(
						Component.translatable("create_soul_harvest.potentia_reservoir.mode." + mode.getSerializedName()),
						true
				);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(MODE);
	}

	@Override
	public Class<PotentiaReservoirBlockEntity> getBlockEntityClass() {
		return PotentiaReservoirBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends PotentiaReservoirBlockEntity> getBlockEntityType() {
		return ModBlockEntities.POTENTIA_RESERVOIR_BE.get();
	}
}
