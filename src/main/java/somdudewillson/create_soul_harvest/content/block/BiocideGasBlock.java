package somdudewillson.create_soul_harvest.content.block;

import java.util.ArrayList;
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

	public static final int MAX_DENSITY = 15;
	public static final IntegerProperty DENSITY = IntegerProperty.create("density", 0, MAX_DENSITY);
	public static final BooleanProperty SETTLED = BooleanProperty.create("settled");

	public BiocideGasBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(DENSITY, 15)
				.setValue(SETTLED, Boolean.FALSE));
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
			level.scheduleTick(pos, this, 10 + level.random.nextInt(10));
		}
	}

	//? forge {
	/*@SuppressWarnings("deprecation")
	*///?}
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (!level.isClientSide() && state.getValue(SETTLED)) {
			level.setBlock(pos, state.setValue(SETTLED, false), Block.UPDATE_CLIENTS);
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

		Map<BlockPos, BlockState> cachedStates = new HashMap<>(7);
		Map<BlockPos, BlockState> pendingBlockUpdates = new HashMap<>();
		Map<BlockPos, BiocideConversionRecipe> pendingConversions = new HashMap<>();
		Set<BlockPos> newlyCreatedGas = new HashSet<>();
		Set<BlockPos> upgradedGasPositions = new HashSet<>();

		// -- Build blockstate cache
		cachedStates.put(pos, state);
		for (Direction dir : Direction.values()) {
			BlockPos targetPos = pos.relative(dir);
			cachedStates.put(targetPos, level.getBlockState(targetPos));
		}

		List<BiocideConversionRecipe> recipes = getRecipes(level);
		boolean hadActivity = false;

		// -- Handle all conversion recipes, updating the cache with any conversions
		for (Direction dir : Direction.values()) {
			BlockPos targetPos = pos.relative(dir);
			BlockState targetState = cachedStates.get(targetPos);
			if (targetState.is(this)) continue;

			for (BiocideConversionRecipe recipe : recipes) {
				if (recipe.matches(dir, density, targetState)) {
					hadActivity = true;
					int densityChange = recipe.rollDensityChange(random);
					density = Math.min(15, Math.max(0, density + densityChange));
					cachedStates.put(pos, state.setValue(DENSITY, density));

					if (recipe.createsGasBlock()) {
						int baseDensity = recipe.rollBaseGasDensity(random);
						BlockState newGas = this.defaultBlockState()
								.setValue(DENSITY, Math.min(15, Math.max(0, baseDensity)))
								.setValue(SETTLED, Boolean.FALSE);
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
		if (density > 0) {
			int densityBeforeSpread = density;
			density = doSpread(pos, density, cachedStates, pendingBlockUpdates, newlyCreatedGas, upgradedGasPositions, random);
			if (density != densityBeforeSpread) {
				hadActivity = true;
			}
		}
		
		float airExposure = 0.0f;
		for (Direction dir : Direction.values()) {
			BlockPos neighborPos = pos.relative(dir);
			if (cachedStates.get(neighborPos).isAir()) {
				airExposure += (dir == Direction.UP) ? 0.5f : 1.0f;
			}
		}

		// Dissipation: low-density gas blocks adjacent to air slowly 'dissipating', scaled by air exposure
		if (density <= 2 && airExposure > 0.0f) {
			float dissipationChance = Math.min(1.0f, airExposure * 0.34f);
			if (random.nextFloat() < dissipationChance) {
				density--;
				hadActivity = true;
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

		for (BlockPos gasPos : upgradedGasPositions) {
			level.scheduleTick(gasPos, this, 10);
		}

		boolean isSettled = !hadActivity && isEquilibrated(pos, density, cachedStates);

		if (density >= 0) {
			BlockState nextState = this.defaultBlockState()
					.setValue(DENSITY, Math.min(density, 15))
					.setValue(SETTLED, isSettled);
			level.setBlockAndUpdate(pos, nextState);
			int nextTickDelay = isSettled ? 30 : 10;
			level.scheduleTick(pos, this, nextTickDelay);
		} else {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	private boolean isEquilibrated(BlockPos pos, int density, Map<BlockPos, BlockState> cachedStates) {
		if (density <= 0) return true;

		// Down
		BlockPos belowPos = pos.below();
		BlockState belowState = cachedStates.get(belowPos);
		if (belowState != null && (belowState.is(this) || belowState.isAir())) {
			if ((density - getDensity(cachedStates, belowPos) + 2) / 2 > 0) {
				return false;
			}
		}

		// Horizontal
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			BlockPos nPos = pos.relative(dir);
			BlockState nState = cachedStates.get(nPos);
			if (nState != null && (nState.is(this) || nState.isAir())) {
				if (density - getDensity(cachedStates, nPos) >= 2) {
					return false;
				}
			}
		}

		// Up
		BlockPos abovePos = pos.above();
		BlockState aboveState = cachedStates.get(abovePos);
		if (aboveState != null && (aboveState.is(this) || aboveState.isAir())) {
			if ((density - getDensity(cachedStates, abovePos) - 2) / 2 > 0) {
				return false;
			}
		}

		return true;
	}

	private static final int MAX_SPREAD_RATE = 8;
	private int doSpread(BlockPos pos, int density, 
			Map<BlockPos, BlockState> cachedStates,
			Map<BlockPos, BlockState> pendingBlockUpdates, 
			Set<BlockPos> newlyCreatedGas,
			Set<BlockPos> upgradedGasPositions,
			RandomSource random) {
		if (density <= 0) return density;
		int spreadAmount = 0;
		
		// Down
		int belowDensity = getDensity(cachedStates, pos.below());
		int belowSpreadAmount = (density - belowDensity + 2) / 2;
		int amtSpread = trySpreadDensity(cachedStates, pendingBlockUpdates, newlyCreatedGas, upgradedGasPositions, pos, pos.below(), Math.min(MAX_SPREAD_RATE-spreadAmount, belowSpreadAmount), false);
		spreadAmount += amtSpread;
		density -= amtSpread;
		
		if (density <= 0 || spreadAmount >= MAX_SPREAD_RATE) return density;
		
		// Sides
		amtSpread = doSpreadHorizontal(pos, density, MAX_SPREAD_RATE - spreadAmount, cachedStates, pendingBlockUpdates, newlyCreatedGas, upgradedGasPositions, random);
		spreadAmount += amtSpread;
		density -= amtSpread;
		if (density <= 0 || spreadAmount >= MAX_SPREAD_RATE) return density;
		
		// Up
		int aboveDensity = getDensity(cachedStates, pos.above());
		int aboveSpreadAmount = (density - aboveDensity - 2) / 2;
		amtSpread = trySpreadDensity(cachedStates, pendingBlockUpdates, newlyCreatedGas, upgradedGasPositions, pos, pos.above(), Math.min(MAX_SPREAD_RATE-spreadAmount, aboveSpreadAmount), false);
		spreadAmount += amtSpread;
		density -= amtSpread;

		return density;
	}
	
	/**
	 * Returns spread budget used
	 */
	private int doSpreadHorizontal(BlockPos pos, int density, int spreadBudget, 
			Map<BlockPos, BlockState> cachedStates,
			Map<BlockPos, BlockState> pendingBlockUpdates, 
			Set<BlockPos> newlyCreatedGas,
			Set<BlockPos> upgradedGasPositions,
			RandomSource random) {		
		if (spreadBudget <= 0 || density <= 0) return 0;

		List<BlockPos> validNeighbors = new ArrayList<>(4);
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			BlockPos targetPos = pos.relative(dir);
			BlockState targetState = cachedStates.get(targetPos);
			if (targetState != null && (targetState.is(this) || targetState.isAir())) {
				int targetDensity = getDensity(cachedStates, targetPos);
				if (targetDensity < density) {
					validNeighbors.add(targetPos);
				}
			}
		}
		if (validNeighbors.isEmpty()) return 0;

		int numNeighbors = validNeighbors.size();
		int[] currentDensities = new int[numNeighbors];
		int[] transferAmounts = new int[numNeighbors];

		for (int i = 0; i < numNeighbors; i++) {
			currentDensities[i] = getDensity(cachedStates, validNeighbors.get(i));
		}

		int currentCenterDensity = density;
		int remainingBudget = spreadBudget;

		while (remainingBudget > 0) {
			int minDensity = Integer.MAX_VALUE;
			for (int d : currentDensities) {
				if (d < minDensity) {
					minDensity = d;
				}
			}

			// Must be at least 2 higher than the minimum neighbor to transfer without inversion
			if (currentCenterDensity - minDensity < 2) {
				break;
			}

			List<Integer> tiedIndices = new ArrayList<>(numNeighbors);
			for (int i = 0; i < numNeighbors; i++) {
				if (currentDensities[i] == minDensity) {
					tiedIndices.add(i);
				}
			}

			if (tiedIndices.size() > 1 && random != null) {
				for (int i = tiedIndices.size() - 1; i > 0; i--) {
					int j = random.nextInt(i + 1);
					int temp = tiedIndices.get(i);
					tiedIndices.set(i, tiedIndices.get(j));
					tiedIndices.set(j, temp);
				}
			}

			boolean transferredAny = false;
			for (int idx : tiedIndices) {
				if (remainingBudget <= 0 || currentCenterDensity - currentDensities[idx] < 2) {
					break;
				}
				currentDensities[idx]++;
				transferAmounts[idx]++;
				currentCenterDensity--;
				remainingBudget--;
				transferredAny = true;
			}

			if (!transferredAny) {
				break;
			}
		}

		int totalSpread = 0;
		for (int i = 0; i < numNeighbors; i++) {
			int amt = transferAmounts[i];
			if (amt > 0) {
				totalSpread += trySpreadDensity(
						cachedStates, 
						pendingBlockUpdates, 
						newlyCreatedGas, 
						upgradedGasPositions, 
						pos, 
						validNeighbors.get(i), 
						amt, 
						false);
			}
		}

		return totalSpread;
	}
	
	private int getDensity(Map<BlockPos, BlockState> cachedStates, BlockPos pos) {
		BlockState testState = cachedStates.get(pos);
		if (testState != null && testState.is(this)) {
			return testState.getValue(DENSITY);
		}
		return 0;
	}
	
	/**
	 * Returns actual amount moved.
	 */
	private int trySpreadDensity(Map<BlockPos, BlockState> cachedStates, Map<BlockPos, BlockState> pendingBlockUpdates, 
			Set<BlockPos> newlyCreatedGas, Set<BlockPos> upgradedGasPositions, 
			BlockPos sourcePos, BlockPos targetPos, int maxDensity, boolean simulate) {
		if (maxDensity <= 0) return 0;
		
		BlockState sourceState = cachedStates.get(sourcePos);
		BlockState targetState = cachedStates.get(targetPos);
		if (sourceState == null || targetState == null) return 0;

		int sourceDensity = sourceState.getValue(DENSITY);
		maxDensity = Math.min(maxDensity, sourceDensity);
		if (maxDensity <= 0) return 0;
		
		if (targetState.is(this)) {
			int targetStateDensity = targetState.getValue(DENSITY);
			int actualDensityMoved = Math.min(maxDensity, MAX_DENSITY - targetStateDensity);
			if (actualDensityMoved <= 0) return 0;
			
			if (!simulate) {
				BlockState upgradedGas = targetState
						.setValue(DENSITY, targetStateDensity + actualDensityMoved)
						.setValue(SETTLED, Boolean.FALSE);
				cachedStates.put(targetPos, upgradedGas);
				pendingBlockUpdates.put(targetPos, upgradedGas);
				upgradedGasPositions.add(targetPos);
				
				BlockState reducedGas = sourceState
						.setValue(DENSITY, Math.max(0, sourceDensity - actualDensityMoved));
				cachedStates.put(sourcePos, reducedGas);
			}
			
			return actualDensityMoved;
		}
		
		if (targetState.isAir()) {
			int actualDensityMoved = Math.min(maxDensity, MAX_DENSITY);
			if (actualDensityMoved <= 0) return 0;

			if (!simulate) {
				BlockState newGas = this.defaultBlockState()
						.setValue(DENSITY, actualDensityMoved)
						.setValue(SETTLED, Boolean.FALSE);
				cachedStates.put(targetPos, newGas);
				pendingBlockUpdates.put(targetPos, newGas);
				newlyCreatedGas.add(targetPos);
				
				BlockState reducedGas = sourceState
						.setValue(DENSITY, Math.max(0, sourceDensity - actualDensityMoved));
				cachedStates.put(sourcePos, reducedGas);
			}
			
			return actualDensityMoved;
		}
		
		return 0;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DENSITY, SETTLED);
	}
}
