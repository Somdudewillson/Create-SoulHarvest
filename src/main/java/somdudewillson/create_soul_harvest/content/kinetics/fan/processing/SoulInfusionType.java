package somdudewillson.create_soul_harvest.content.kinetics.fan.processing;

import java.util.List;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import somdudewillson.create_soul_harvest.registry.ModParticleTypes;
import somdudewillson.create_soul_harvest.registry.ModRecipeTypes;
import somdudewillson.create_soul_harvest.registry.ModTags;

//? >= 1.21 {
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
//?} else {
/*import somdudewillson.create_soul_harvest.content.recipe.SoulInfusionRecipe.SoulInfusionWrapper;
*///?}

public class SoulInfusionType implements FanProcessingType {

	//? < 1.21 {
	/*private static final SoulInfusionWrapper RECIPE_WRAPPER = new SoulInfusionWrapper();
	*///?}

	@Override
	public boolean isValidAt(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(ModTags.Blocks.FAN_PROCESSING_CATALYSTS_SOUL_INFUSION);
	}

	@Override
	public int getPriority() {
		return 500;
	}

	@Override
	public boolean canProcess(ItemStack stack, Level level) {
		//? >= 1.21 {
		return ModRecipeTypes.SOUL_INFUSION.find(new SingleRecipeInput(stack), level).isPresent();
		//?} else {
		/*RECIPE_WRAPPER.setItem(0, stack);
		return ModRecipeTypes.SOUL_INFUSION.find(RECIPE_WRAPPER, level).isPresent();
		*///?}
	}

	@Override
	@Nullable
	public List<ItemStack> process(ItemStack stack, Level level) {
		//? >= 1.21 {
		return ModRecipeTypes.SOUL_INFUSION.find(new SingleRecipeInput(stack), level)
				.map(RecipeHolder::value)
				.map(r -> RecipeApplier.applyRecipeOn(level, stack, r, true))
				.orElse(null);
		//?} else {
		/*RECIPE_WRAPPER.setItem(0, stack);
		return ModRecipeTypes.SOUL_INFUSION.find(RECIPE_WRAPPER, level)
				.map(r -> RecipeApplier.applyRecipeOn(level, stack, r, true))
				.orElse(null);
		*///?}
	}

	@Override
	public void spawnProcessingParticles(Level level, Vec3 pos) {
		if (level.random.nextInt(8) != 0)
			return;

		level.addParticle(ModParticleTypes.INFUSE_SOUL_FIRE.get(),
				pos.x + (level.random.nextFloat() - 0.5f) * 0.4f,
				pos.y + 0.35f,
				pos.z + (level.random.nextFloat() - 0.5f) * 0.4f,
				0, 0.05f, 0);

		if (level.random.nextInt(2) == 0) {
			Vector3f magentaDust = new Color(0xBF55EC).asVectorF();
			level.addParticle(new DustParticleOptions(magentaDust, 1.0f),
					pos.x + (level.random.nextFloat() - 0.5f) * 0.5f,
					pos.y + 0.5f,
					pos.z + (level.random.nextFloat() - 0.5f) * 0.5f,
					0, 1.0f / 16.0f, 0);
		}
	}

	@Override
	public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
		// Soul-amethyst gradient: between deep purple (0x6A1B9A) and glowing magenta/violet (0xBF55EC)
		particleAccess.setColor(Color.mixColors(0x6A1B9A, 0xBF55EC, random.nextFloat()));
		particleAccess.setAlpha(0.85f);

		if (random.nextFloat() < 1.0f / 24.0f) {
			particleAccess.spawnExtraParticle(ModParticleTypes.INFUSE_SOUL_FIRE.get(), 0.125f);
		}
		if (random.nextFloat() < 1.0f / 48.0f) {
			particleAccess.spawnExtraParticle(ParticleTypes.PORTAL, 0.15f);
		}
	}

	@Override
	public void affectEntity(Entity entity, Level level) {
		if (level.isClientSide())
			return;

		if (entity instanceof LivingEntity livingEntity) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0, false, false));
			livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
		}
	}
}
