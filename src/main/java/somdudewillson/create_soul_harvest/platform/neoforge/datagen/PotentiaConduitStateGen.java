package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import somdudewillson.create_soul_harvest.content.conduit.PotentiaConduitBlock;
import somdudewillson.create_soul_harvest.content.conduit.WindowedPotentiaConduitBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotentiaConduitStateGen {

	private record OrientationKey(String orientation, Axis axis) {}
	private record UVCoord(int u, int v) {}

	public static void generate(BlockStateProvider p) {
		String basePath = "block/potentia_conduit";

		String LU = "lu";
		String RU = "ru";
		String LD = "ld";
		String RD = "rd";
		String LR = "lr";
		String UD = "ud";
		String U = "u";
		String D = "d";
		String L = "l";
		String R = "r";

		List<String> orientations = List.of(LU, RU, LD, RD, LR, UD, U, D, L, R);
		Map<String, UVCoord> uvs = Map.of(
				LU, new UVCoord(12, 4),
				RU, new UVCoord(8, 4),
				LD, new UVCoord(12, 0),
				RD, new UVCoord(8, 0),
				LR, new UVCoord(4, 8),
				UD, new UVCoord(0, 8),
				U, new UVCoord(4, 4),
				D, new UVCoord(0, 0),
				L, new UVCoord(4, 0),
				R, new UVCoord(0, 4)
		);

		Map<OrientationKey, ModelFile> coreModels = new HashMap<>();

		for (Axis axis : Axis.values()) {
			ResourceLocation parent = p.modLoc(basePath + "/core_" + axis.getSerializedName());
			for (String s : orientations) {
				String modelName = basePath + "/" + s + "_" + axis.getSerializedName();
				UVCoord uv = uvs.get(s);
				int u = uv.u();
				int v = uv.v();

				BlockModelBuilder model = p.models().withExistingParent(modelName, parent).renderType("cutout_mipped");
				var elem = model.element().from(4, 4, 4).to(12, 12, 12);

				if (axis == Axis.X) {
					elem.face(Direction.EAST).end();
					elem.face(Direction.WEST).end();
				} else if (axis == Axis.Y) {
					elem.face(Direction.UP).end();
					elem.face(Direction.DOWN).end();
				} else {
					elem.face(Direction.NORTH).end();
					elem.face(Direction.SOUTH).end();
				}

				elem.faces((d, builder) -> {
					if (d == Direction.UP)
						builder.uvs(u + 4, v + 4, u, v);
					else if (d == Direction.DOWN)
						builder.uvs(u + 4, v, u, v + 4);
					else if (d == Direction.NORTH)
						builder.uvs(u, v, u + 4, v + 4);
					else if (d == Direction.SOUTH)
						builder.uvs(u + 4, v, u, v + 4);
					else if (d == Direction.EAST)
						builder.uvs(u, v, u + 4, v + 4);
					else if (d == Direction.WEST)
						builder.uvs(u + 4, v, u, v + 4);
					builder.texture("#0");
				});
				elem.end();

				coreModels.put(new OrientationKey(s, axis), model);
			}
		}

		MultiPartBlockStateBuilder multipart = p.getMultipartBuilder(ModBlocks.POTENTIA_CONDUIT.get());

		for (Axis axis : Axis.values()) {
			putPart(coreModels, multipart, axis, LU, true, false, true, false);
			putPart(coreModels, multipart, axis, RU, true, false, false, true);
			putPart(coreModels, multipart, axis, LD, false, true, true, false);
			putPart(coreModels, multipart, axis, RD, false, true, false, true);
			putPart(coreModels, multipart, axis, UD, true, true, false, false);
			putPart(coreModels, multipart, axis, U, true, false, false, false);
			putPart(coreModels, multipart, axis, D, false, true, false, false);
			putPart(coreModels, multipart, axis, LR, false, false, true, true);
			putPart(coreModels, multipart, axis, L, false, false, true, false);
			putPart(coreModels, multipart, axis, R, false, false, false, true);
		}

		ModelFile connectionNorth = p.models().getExistingFile(p.modLoc(basePath + "/connection/north"));
		for (Direction d : Direction.values()) {
			int rotX = 0;
			int rotY = 0;
			switch (d) {
				case SOUTH -> rotY = 180;
				case EAST -> rotY = 90;
				case WEST -> rotY = 270;
				case DOWN -> rotX = 90;
				case UP -> rotX = 270;
				default -> {} // NORTH is rotX = 0, rotY = 0
			}

			multipart.part()
					.modelFile(connectionNorth)
					.rotationX(rotX)
					.rotationY(rotY)
					.addModel()
					.condition(PotentiaConduitBlock.PROPERTY_BY_DIRECTION.get(d), true)
					.end();
		}

		// Windowed Potentia Conduit blockstate
		ModelFile windowModel = p.models().getExistingFile(p.modLoc(basePath + "/window"));
		p.getVariantBuilder(ModBlocks.WINDOWED_POTENTIA_CONDUIT.get())
				.forAllStatesExcept(state -> {
					Axis axis = state.getValue(RotatedPillarBlock.AXIS);
					return ConfiguredModel.builder()
							.modelFile(windowModel)
							.uvLock(false)
							.rotationX(axis == Axis.Y ? 0 : 90)
							.rotationY(axis == Axis.X ? 90 : 0)
							.build();
				}, WindowedPotentiaConduitBlock.WATERLOGGED);
	}

	private static void putPart(Map<OrientationKey, ModelFile> coreModels, MultiPartBlockStateBuilder builder,
								Axis axis, String s, boolean up, boolean down, boolean left, boolean right) {
		Map<Direction, BooleanProperty> propertyMap = PotentiaConduitBlock.PROPERTY_BY_DIRECTION;

		Direction upD = getUp(axis);
		Direction downD = getDown(axis);
		Direction leftD = getLeft(axis);
		Direction rightD = getRight(axis);

		builder.part()
				.modelFile(coreModels.get(new OrientationKey(s, axis)))
				.addModel()
				.condition(propertyMap.get(upD), up)
				.condition(propertyMap.get(leftD), left)
				.condition(propertyMap.get(rightD), right)
				.condition(propertyMap.get(downD), down)
				.end();
	}

	private static Direction getUp(Axis axis) {
		return axis == Axis.Y ? Direction.NORTH : Direction.UP;
	}

	private static Direction getDown(Axis axis) {
		return axis == Axis.Y ? Direction.SOUTH : Direction.DOWN;
	}

	private static Direction getLeft(Axis axis) {
		return axis == Axis.Z ? Direction.EAST : (axis == Axis.X ? Direction.SOUTH : Direction.EAST);
	}

	private static Direction getRight(Axis axis) {
		return axis == Axis.Z ? Direction.WEST : (axis == Axis.X ? Direction.NORTH : Direction.WEST);
	}
}
//?}
