package somdudewillson.create_soul_harvest.content.spell.planetoid.builder;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PartialPlanetoidGeneration;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PlanetoidLayerBuilder;
import somdudewillson.create_soul_harvest.content.spell.planetoid.PlanetoidLayerDefinition;
import somdudewillson.create_soul_harvest.registry.ModTags;

public class DeepslateLayerBuilder implements PlanetoidLayerBuilder {

	private final float commonOreWeight;
	private final float uncommonOreWeight;
	private final float rareOreWeight;

	public DeepslateLayerBuilder() {
		this(0.06f, 0.03f, 0.01f);
	}

	public DeepslateLayerBuilder(float commonOreWeight, float uncommonOreWeight, float rareOreWeight) {
		this.commonOreWeight = commonOreWeight;
		this.uncommonOreWeight = uncommonOreWeight;
		this.rareOreWeight = rareOreWeight;
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
			return getRandomBlockFromTag(ModTags.Blocks.PLANETOID_DEEPSLATE_ORES_RARE, random, Blocks.DEEPSLATE.defaultBlockState());
		}
		roll -= rareOreWeight;
		if (roll < uncommonOreWeight) {
			return getRandomBlockFromTag(ModTags.Blocks.PLANETOID_DEEPSLATE_ORES_UNCOMMON, random, Blocks.DEEPSLATE.defaultBlockState());
		}
		roll -= uncommonOreWeight;
		if (roll < commonOreWeight) {
			return getRandomBlockFromTag(ModTags.Blocks.PLANETOID_DEEPSLATE_ORES_COMMON, random, Blocks.DEEPSLATE.defaultBlockState());
		}
		return Blocks.DEEPSLATE.defaultBlockState();
	}

	public static BlockState getRandomBlockFromTag(TagKey<Block> tag, RandomSource random, BlockState fallback) {
		Optional<HolderSet.Named<Block>> tagHolders = BuiltInRegistries.BLOCK.getTag(tag);
		if (tagHolders.isPresent() && tagHolders.get().size() > 0) {
			Optional<Holder<Block>> holder = tagHolders.get().getRandomElement(random);
			if (holder.isPresent()) {
				return holder.get().value().defaultBlockState();
			}
		}
		return fallback;
	}
}
