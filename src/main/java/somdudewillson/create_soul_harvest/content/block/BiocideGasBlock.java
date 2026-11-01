package somdudewillson.create_soul_harvest.content.block;

import java.util.List;

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
import somdudewillson.create_soul_harvest.registry.ModItems;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;

public class BiocideGasBlock extends Block {

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

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int density = state.getValue(DENSITY);
		if (random.nextFloat() < 0.3f) {
			density--;
		}

		List<BiocideConversionRecipe> recipes = getRecipes(level);

		// Check non-gas block conversions (e.g., ground to dirt)
		for (Direction dir : Direction.values()) {
			BlockPos targetPos = pos.relative(dir);
			BlockState targetState = level.getBlockState(targetPos);
			if (targetState.is(this) || targetState.isAir()) continue;

			for (BiocideConversionRecipe recipe : recipes) {
				if (!recipe.createsGasBlock() && recipe.matches(dir, density, targetState)) {
					recipe.apply(level, targetPos);
					int densityChange = recipe.rollDensityChange(random);
					density = Math.min(15, Math.max(0, density + densityChange));
					break;
				}
			}
		}

		boolean canSpread = !state.getValue(TRIGGERED);

		// Spread to surrounding positions
		if (canSpread && density > 0) {
			for (Direction dir : Direction.values()) {
				BlockPos spreadPos = pos.relative(dir);
				int spreadLoss = (dir == Direction.UP) ? 3 : (dir == Direction.DOWN) ? 1 : 2;
				int spreadDensity = Math.min(density, 15) - spreadLoss;

				BlockState spreadState = level.getBlockState(spreadPos);
				boolean spreadingToExisting = false;

				if (spreadState.is(this)) {
					spreadingToExisting = true;
					if (spreadState.getValue(DENSITY) >= spreadDensity) {
						continue;
					}
				} else {
					BiocideConversionRecipe matchedRecipe = null;
					for (BiocideConversionRecipe recipe : recipes) {
						if (recipe.createsGasBlock() && recipe.matches(dir, density, spreadState)) {
							matchedRecipe = recipe;
							break;
						}
					}

					if (matchedRecipe != null) {
						int baseDensity = matchedRecipe.rollBaseGasDensity(random);
						spreadDensity = Math.max(spreadDensity, baseDensity);
						int densityChange = matchedRecipe.rollDensityChange(random);
						density = Math.min(15, Math.max(0, density + densityChange));
					} else if (!spreadState.isAir()) {
						continue;
					}
				}

				if (spreadDensity < 0) continue;

				BlockState newGas = this.defaultBlockState().setValue(DENSITY, Math.min(spreadDensity, 15));
				level.setBlockAndUpdate(spreadPos, newGas);

				if (!spreadingToExisting) {
					level.scheduleTick(spreadPos, this, 10 + random.nextInt(10));
				}
			}
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
