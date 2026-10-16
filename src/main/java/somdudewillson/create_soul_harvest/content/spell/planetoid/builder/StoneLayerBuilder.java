package somdudewillson.create_soul_harvest.content.spell.planetoid.builder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PlanetoidLayerBuilder;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PlanetoidLayerDefinition;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class StoneLayerBuilder implements PlanetoidLayerBuilder {

	private final float commonOreWeight;
	private final float uncommonOreWeight;
	private final float rareOreWeight;
	private final float stoneVariantWeight;

	public StoneLayerBuilder() {
		this(0.04f, 0.02f, 0.0067f, 0.15f);
	}

	public StoneLayerBuilder(float commonOreWeight, float uncommonOreWeight, float rareOreWeight, float stoneVariantWeight) {
		this.commonOreWeight = commonOreWeight;
		this.uncommonOreWeight = uncommonOreWeight;
		this.rareOreWeight = rareOreWeight;
		this.stoneVariantWeight = stoneVariantWeight;
	}

	@Override
	public void populate(ServerLevel level, PlanetoidLayerDefinition layerDef, Vec3 origin, PartialPlanetoidGeneration generation, RandomSource random) {
		Vec3 center = layerDef.getCenter(origin);
		BlockPos centerPos = BlockPos.containing(center.x, center.y, center.z);
		int r = (int) Math.ceil(layerDef.radius());
		double rSqr = layerDef.getRadiusSqr();

		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				for (int z = -r; z <= r; z++) {
					if (x * x + y * y + z * z <= rSqr) {
						BlockPos p = centerPos.offset(x, y, z);
						BlockState state = selectBlockState(random);
						generation.setBlockStateIfAir(p, state);
					}
				}
			}
		}
	}

	protected BlockState selectBlockState(RandomSource random) {
		float roll = random.nextFloat();
		if (roll < rareOreWeight) {
			return DeepslateLayerBuilder.getRandomBlockFromTag(ModTags.Blocks.PLANETOID_STONE_ORES_RARE, random, Blocks.STONE.defaultBlockState());
		}
		roll -= rareOreWeight;
		if (roll < uncommonOreWeight) {
			return DeepslateLayerBuilder.getRandomBlockFromTag(ModTags.Blocks.PLANETOID_STONE_ORES_UNCOMMON, random, Blocks.STONE.defaultBlockState());
		}
		roll -= uncommonOreWeight;
		if (roll < commonOreWeight) {
			return DeepslateLayerBuilder.getRandomBlockFromTag(ModTags.Blocks.PLANETOID_STONE_ORES_COMMON, random, Blocks.STONE.defaultBlockState());
		}
		roll -= commonOreWeight;
		if (roll < stoneVariantWeight) {
			return DeepslateLayerBuilder.getRandomBlockFromTag(ModTags.Blocks.PLANETOID_STONE_VARIANTS, random, Blocks.STONE.defaultBlockState());
		}
		return Blocks.STONE.defaultBlockState();
	}
}
