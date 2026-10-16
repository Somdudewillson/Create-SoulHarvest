package somdudewillson.create_soul_harvest.content.spell.planetoid;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.content.spell.planetoid.builder.DeepslateLayerBuilder;
import somdudewillson.create_soul_harvest.content.spell.planetoid.builder.StoneLayerBuilder;
import somdudewillson.create_soul_harvest.content.spell.planetoid.builder.SurfaceLayerBuilder;
import somdudewillson.create_soul_harvest.content.spell.planetoid.decorator.CactusDecorator;
import somdudewillson.create_soul_harvest.content.spell.planetoid.decorator.DeadBushDecorator;
import somdudewillson.create_soul_harvest.content.spell.planetoid.decorator.FloraDecorator;
import somdudewillson.create_soul_harvest.content.spell.planetoid.decorator.SnowDecorator;

public class PlanetoidTemplates {

	public static final PlanetoidTemplate VERDANT = new PlanetoidTemplate(
			"verdant",
			8.5,
			6.0,
			20 * 60,
			32,
			1,
			5,
			List.of(
					new PlanetoidLayerDefinition(
							"deepslate",
							new Vec3(0, -3.0, 0),
							4.5,
							new DeepslateLayerBuilder()
					),
					new PlanetoidLayerDefinition(
							"stone",
							new Vec3(0, -1.5, 0),
							6.8,
							new StoneLayerBuilder()
					),
					new PlanetoidLayerDefinition(
							"surface",
							new Vec3(0, 1.8, 0),
							7.5,
							new SurfaceLayerBuilder(
									Blocks.DIRT.defaultBlockState(),
									Blocks.GRASS_BLOCK.defaultBlockState(),
									2.0,
									List.of(new FloraDecorator())
							)
					)
			)
	);
	
	public static final PlanetoidTemplate SNOWY = new PlanetoidTemplate(
			"snowy",
			8.5,
			6.0,
			20 * 60,
			32,
			1,
			5,
			List.of(
					new PlanetoidLayerDefinition(
							"deepslate",
							new Vec3(0, -3.0, 0),
							4.5,
							new DeepslateLayerBuilder()
					),
					new PlanetoidLayerDefinition(
							"stone",
							new Vec3(0, -1.5, 0),
							6.8,
							new StoneLayerBuilder()
					),
					new PlanetoidLayerDefinition(
							"surface",
							new Vec3(0, 1.8, 0),
							7.5,
							new SurfaceLayerBuilder(
									Blocks.DIRT.defaultBlockState(),
									Blocks.GRASS_BLOCK.defaultBlockState(),
									2.0,
									List.of(new SnowDecorator())
							)
					)
			)
	);

	public static final PlanetoidTemplate DESERT = new PlanetoidTemplate(
			"desert",
			8.5,
			6.0,
			20 * 60,
			32,
			1,
			5,
			List.of(
					new PlanetoidLayerDefinition(
							"deepslate",
							new Vec3(0, -3.0, 0),
							4.5,
							new DeepslateLayerBuilder()
					),
					new PlanetoidLayerDefinition(
							"stone",
							new Vec3(0, -1.5, 0),
							6.8,
							new StoneLayerBuilder()
					),
					new PlanetoidLayerDefinition(
							"surface",
							new Vec3(0, 1.8, 0),
							7.5,
							new SurfaceLayerBuilder(
									Blocks.SAND.defaultBlockState(),
									2.0,
									List.of(new CactusDecorator(), new DeadBushDecorator())
							)
					)
			)
	);

	/**
	 * Default biome-based template selector
	 */
	public static final PlanetoidTemplateSelector DEFAULT_SELECTOR = (ServerLevel level, Vec3 origin, RandomSource random) -> {
		BlockPos blockPos = BlockPos.containing(origin.x, origin.y, origin.z);
		Biome biome = level.getBiome(blockPos).value();
		
		if (biome.coldEnoughToSnow(BlockPos.containing(origin))) {
			return SNOWY;
		}
		float temp = biome.getBaseTemperature();
		if (temp >= 1.5f) {
			return DESERT;
		}
		return VERDANT;
	};
}
