package somdudewillson.create_soul_harvest.content.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class DenseHypotheticalBlock extends Block {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 5);

	public DenseHypotheticalBlock(Properties properties) {
		super(properties.randomTicks());
		this.registerDefaultState(
				this.stateDefinition.any()
					.setValue(AGE, 0)
					.setValue(VARIANT, 0)
				);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
		builder.add(VARIANT);
	}

	@Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getRandomVariant(context.getLevel().random);
    }

    protected static final BlockPos[] SHIFT_POS = new BlockPos[] {
    		BlockPos.ZERO.north(),
    		BlockPos.ZERO.east(),
    		BlockPos.ZERO.south(),
    		BlockPos.ZERO.west()
    }; 
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    	if (state.getValue(AGE) < BlockStateProperties.MAX_AGE_3) {
    		level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), UPDATE_ALL);
    		return;
    	}
    	
    	if (level.getBlockState(pos.below()).canBeReplaced()) {
    		level.destroyBlock(pos.below(), true);
    		level.setBlock(pos.below(), state, UPDATE_ALL);
    		level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
    		return;
    	}
    	
    	boolean currentlyAdjToDense = isPosAdjToDense(level, pos, null);
    	int offset = random.nextInt(SHIFT_POS.length);
    	for (int i=0;i<SHIFT_POS.length;i++) {
    		BlockPos adjPos = pos.offset(SHIFT_POS[(i+offset) % SHIFT_POS.length]);
    		if (!level.getBlockState(adjPos).canBeReplaced()) continue;
    		BlockState belowState = level.getBlockState(adjPos.below());
    		if (belowState.isAir() && belowState.canBeReplaced()) adjPos = adjPos.below();
    		if (currentlyAdjToDense && !isPosAdjToDense(level, adjPos, pos)) continue;

    		level.setBlock(adjPos, state, UPDATE_ALL);
    		level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
    		return;
    	}
    }
    
    protected static final BlockPos[] ADJ_POS = new BlockPos[] {
    		BlockPos.ZERO.above(),
    		BlockPos.ZERO.below(),
    		
    		BlockPos.ZERO.north(),
    		BlockPos.ZERO.east(),
    		BlockPos.ZERO.south(),
    		BlockPos.ZERO.west(),
    		BlockPos.ZERO.north().east(),
    		BlockPos.ZERO.north().west(),
    		BlockPos.ZERO.south().east(),
    		BlockPos.ZERO.south().west(),
    		
    		BlockPos.ZERO.above().north(),
    		BlockPos.ZERO.above().east(),
    		BlockPos.ZERO.above().south(),
    		BlockPos.ZERO.above().west(),
    		
    		BlockPos.ZERO.below().north(),
    		BlockPos.ZERO.below().east(),
    		BlockPos.ZERO.below().south(),
    		BlockPos.ZERO.below().west()
    }; 
    protected static boolean isPosAdjToDense(ServerLevel level, BlockPos pos, @Nullable BlockPos exclude) {
    	for (BlockPos offsetPos : ADJ_POS) {
    		BlockPos adjPos = pos.offset(offsetPos);
    		if (adjPos.equals(exclude)) continue;
    		
    		if (level.getBlockState(adjPos).is(ModBlocks.DENSE_HYPOTHETICAL)) return true;
    	}
    	return false;
    }
    
    public static BlockState getRandomVariant(RandomSource rand) {
    	return ModBlocks.DENSE_HYPOTHETICAL.get().defaultBlockState().setValue(VARIANT, rand.nextInt(6));
    }
    
    public static void placeRandomVariant(ServerLevel level, BlockPos pos, int updateFlags) {
    	level.setBlock(
    			pos, 
    			getRandomVariant(level.random), 
    			updateFlags);
    }

}
