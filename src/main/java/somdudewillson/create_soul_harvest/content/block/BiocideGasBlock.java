package somdudewillson.create_soul_harvest.content.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.crafting.RecipeType;
//? >= 1.21 {
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.crafting.RecipeHolder;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import somdudewillson.create_soul_harvest.content.recipe.BiocideConversionRecipe;
import somdudewillson.create_soul_harvest.content.recipe.ConversionResult;
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

public class BiocideGasBlock extends TransparentBlock {

	public static final IntegerProperty DENSITY = IntegerProperty.create("density", 0, 15);
	public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

	public BiocideGasBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(DENSITY, 15)
				.setValue(TRIGGERED, Boolean.FALSE));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!level.isClientSide()) {
			level.scheduleTick(pos, this, 10);
		}
	}

	//? >= 1.21 {
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.is(Items.GLASS_BOTTLE)) {
			if (!player.isCreative()) {
				stack.shrink(1);
			}
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			ItemStack bottle = new ItemStack(ModItems.BIOCIDE_GAS_BOTTLE.get());
			if (!player.addItem(bottle)) {
				player.drop(bottle, false);
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	//?} else {
	/*@Override
	public net.minecraft.world.InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.is(Items.GLASS_BOTTLE)) {
			if (!player.isCreative()) {
				stack.shrink(1);
			}
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			ItemStack bottle = new ItemStack(ModItems.BIOCIDE_GAS_BOTTLE.get());
			if (!player.addItem(bottle)) {
				player.drop(bottle, false);
			}
			return net.minecraft.world.InteractionResult.SUCCESS;
		}
		return net.minecraft.world.InteractionResult.PASS;
	}
	*///?}

	private List<BiocideConversionRecipe> getRecipes(Level level) {
		//? >= 1.21 {
		RecipeType<BiocideConversionRecipe> type = ModRecipeTypes.BIOCIDE_CONVERSION.getType();
		return level.getRecipeManager().getAllRecipesFor(type)
				.stream().map(RecipeHolder::value).toList();
		//?} else {
		/*RecipeType<BiocideConversionRecipe> type = ModRecipeTypes.BIOCIDE_CONVERSION.getType();
		return level.getRecipeManager().getAllRecipesFor(type);
		*///?}
	}

	private BlockState getConvertedState(BiocideConversionRecipe recipe, BlockState fallback) {
		return recipe.getResult()
				.map(ConversionResult::getResultingState)
				.orElse(fallback);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int density = state.getValue(DENSITY);

		Map<BlockPos, BlockState> cachedStates = new HashMap<>(6);
		Map<BlockPos, BlockState> pendingBlockUpdates = new HashMap<>();
		Map<BlockPos, BiocideConversionRecipe> pendingConversions = new HashMap<>();
		Set<BlockPos> newlyCreatedGas = new HashSet<>();

		// -- Build blockstate cache
		for (Direction dir : Direction.values()) {
			BlockPos targetPos = pos.relative(dir);
			cachedStates.put(targetPos, level.getBlockState(targetPos));
		}

		List<BiocideConversionRecipe> recipes = getRecipes(level);

		// -- Handle all conversion recipes, updating the cache with any conversions
		for (Direction dir : Direction.values()) {
			BlockPos targetPos = pos.relative(dir);
			BlockState targetState = cachedStates.get(targetPos);
			if (targetState.is(this)) continue;

			for (BiocideConversionRecipe recipe : recipes) {
				if (recipe.matches(dir, density, targetState)) {
					int densityChange = recipe.rollDensityChange(random);
					density = Math.min(15, Math.max(0, density + densityChange));

					if (recipe.createsGasBlock()) {
						int baseDensity = recipe.rollBaseGasDensity(random);
						BlockState newGas = this.defaultBlockState()
								.setValue(DENSITY, Math.min(15, Math.max(0, baseDensity)));
						cachedStates.put(targetPos, newGas);
						pendingBlockUpdates.put(targetPos, newGas);
						newlyCreatedGas.add(targetPos);
					} else {
						BlockState convertedState = getConvertedState(recipe, targetState);
						cachedStates.put(targetPos, convertedState);
						pendingConversions.put(targetPos, recipe);
					}
					break;
				}
			}
		}

		// -- Spread logic operating on the cache
		float airExposure = 0.0f;
		for (Direction dir : Direction.values()) {
			BlockPos neighborPos = pos.relative(dir);
			if (cachedStates.get(neighborPos).isAir()) {
				airExposure += (dir == Direction.UP) ? 0.5f : 1.0f;
			}
		}

		boolean canSpread = !state.getValue(TRIGGERED);

		if (canSpread && density > 0) {
			for (Direction dir : Direction.values()) {
				BlockPos spreadPos = pos.relative(dir);
				int spreadLoss = (dir == Direction.UP) ? 3 : (dir == Direction.DOWN) ? 1 : 2;
				int spreadDensity = Math.min(density, 15) - spreadLoss;

				BlockState spreadState = cachedStates.get(spreadPos);

				if (spreadState.is(this)) {
					if (spreadDensity > spreadState.getValue(DENSITY)) {
						BlockState upgradedGas = this.defaultBlockState()
								.setValue(DENSITY, Math.min(15, spreadDensity));
						cachedStates.put(spreadPos, upgradedGas);
						pendingBlockUpdates.put(spreadPos, upgradedGas);
					}
				} else if (spreadState.isAir()) {
					if (spreadDensity > 0) {
						BlockState newGas = this.defaultBlockState()
								.setValue(DENSITY, Math.min(15, spreadDensity));
						cachedStates.put(spreadPos, newGas);
						pendingBlockUpdates.put(spreadPos, newGas);
						newlyCreatedGas.add(spreadPos);
					}
				}
			}
		}

		// Dissipation: low-density gas blocks adjacent to air slowly 'dissipating', scaled by air exposure
		if (density <= 2 && airExposure > 0.0f) {
			float dissipationChance = Math.min(1.0f, airExposure * 0.16f);
			if (random.nextFloat() < dissipationChance) {
				density--;
			}
		}

		// -- Send any blockstate changes
		for (Map.Entry<BlockPos, BiocideConversionRecipe> entry : pendingConversions.entrySet()) {
			BlockPos targetPos = entry.getKey();
			if (!pendingBlockUpdates.containsKey(targetPos)) {
				entry.getValue().apply(level, targetPos);
			}
		}

		for (Map.Entry<BlockPos, BlockState> entry : pendingBlockUpdates.entrySet()) {
			level.setBlockAndUpdate(entry.getKey(), entry.getValue());
		}

		for (BlockPos gasPos : newlyCreatedGas) {
			level.scheduleTick(gasPos, this, 10 + random.nextInt(10));
		}

		if (density >= 0) {
			BlockState nextState = this.defaultBlockState()
					.setValue(DENSITY, Math.min(density, 15))
					.setValue(TRIGGERED, Boolean.TRUE);
			level.setBlockAndUpdate(pos, nextState);
			level.scheduleTick(pos, this, 10);
		} else {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DENSITY, TRIGGERED);
	}
}
