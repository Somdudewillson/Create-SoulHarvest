package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import somdudewillson.create_soul_harvest.content.conduit.PotentiaConduitBlock;
import somdudewillson.create_soul_harvest.content.conduit.PotentiaConduitBlock.ConduitConnectionType;
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

				BlockModelBuilder model = p.models().withExistingParent(modelName, parent);
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

		// Casing for junctions (connections > 2)
		ModelFile casingModel = p.models().getExistingFile(p.modLoc(basePath + "/casing"));
		multipart.part()
				.modelFile(casingModel)
				.addModel()
				.condition(PotentiaConduitBlock.CASING, true)
				.end();

		// 30 core slice models for non-cased runs and elbows
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

		// Directional connection arms and rims
		ModelFile connectionNorth = p.models().getExistingFile(p.modLoc(basePath + "/connection/north"));
		ModelFile rimNorth = p.models().getExistingFile(p.modLoc(basePath + "/rim/north"));
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

			// Connection arm renders for both pipe-to-pipe and machine/rim connections
			multipart.part()
					.modelFile(connectionNorth)
					.rotationX(rotX)
					.rotationY(rotY)
					.addModel()
					.condition(PotentiaConduitBlock.PROPERTY_BY_DIRECTION.get(d),
							ConduitConnectionType.CONDUIT,
							ConduitConnectionType.RIM)
					.end();

			// Rim renders on open ends and machine/handler connections
			multipart.part()
					.modelFile(rimNorth)
					.rotationX(rotX)
					.rotationY(rotY)
					.addModel()
					.condition(PotentiaConduitBlock.PROPERTY_BY_DIRECTION.get(d),
							ConduitConnectionType.RIM)
					.end();
		}

		// Windowed Potentia Conduit blockstate
		ModelFile windowModel = p.models().getExistingFile(p.modLoc(basePath + "/window"));
		MultiPartBlockStateBuilder windowMultipart = p.getMultipartBuilder(ModBlocks.WINDOWED_POTENTIA_CONDUIT.get());

		// Window tube body
		windowMultipart.part()
				.modelFile(windowModel)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.Y)
				.end();
		windowMultipart.part()
				.modelFile(windowModel)
				.rotationX(90)
				.rotationY(90)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.X)
				.end();
		windowMultipart.part()
				.modelFile(windowModel)
				.rotationX(90)
				.rotationY(0)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.Z)
				.end();

		// Rims on X axis (pos: East rotY=90, neg: West rotY=270)
		windowMultipart.part()
				.modelFile(rimNorth)
				.rotationY(90)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.X)
				.condition(WindowedPotentiaConduitBlock.POS_RIM, true)
				.end();
		windowMultipart.part()
				.modelFile(rimNorth)
				.rotationY(270)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.X)
				.condition(WindowedPotentiaConduitBlock.NEG_RIM, true)
				.end();

		// Rims on Y axis (pos: Up rotX=270, neg: Down rotX=90)
		windowMultipart.part()
				.modelFile(rimNorth)
				.rotationX(270)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.Y)
				.condition(WindowedPotentiaConduitBlock.POS_RIM, true)
				.end();
		windowMultipart.part()
				.modelFile(rimNorth)
				.rotationX(90)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.Y)
				.condition(WindowedPotentiaConduitBlock.NEG_RIM, true)
				.end();

		// Rims on Z axis (pos: South rotY=180, neg: North rotX=0, rotY=0)
		windowMultipart.part()
				.modelFile(rimNorth)
				.rotationY(180)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.Z)
				.condition(WindowedPotentiaConduitBlock.POS_RIM, true)
				.end();
		windowMultipart.part()
				.modelFile(rimNorth)
				.addModel()
				.condition(RotatedPillarBlock.AXIS, Axis.Z)
				.condition(WindowedPotentiaConduitBlock.NEG_RIM, true)
				.end();
	}

	private static void putPart(Map<OrientationKey, ModelFile> coreModels, MultiPartBlockStateBuilder builder,
								Axis axis, String s, boolean up, boolean down, boolean left, boolean right) {
		Map<Direction, EnumProperty<ConduitConnectionType>> propertyMap = PotentiaConduitBlock.PROPERTY_BY_DIRECTION;

		Direction upD = getUp(axis);
		Direction downD = getDown(axis);
		Direction leftD = getLeft(axis);
		Direction rightD = getRight(axis);

		var part = builder.part()
				.modelFile(coreModels.get(new OrientationKey(s, axis)))
				.addModel()
				.condition(PotentiaConduitBlock.CASING, false);

		if (up) {
			part.condition(propertyMap.get(upD), ConduitConnectionType.CONDUIT, ConduitConnectionType.RIM);
		} else {
			part.condition(propertyMap.get(upD), ConduitConnectionType.NONE);
		}

		if (down) {
			part.condition(propertyMap.get(downD), ConduitConnectionType.CONDUIT, ConduitConnectionType.RIM);
		} else {
			part.condition(propertyMap.get(downD), ConduitConnectionType.NONE);
		}

		if (left) {
			part.condition(propertyMap.get(leftD), ConduitConnectionType.CONDUIT, ConduitConnectionType.RIM);
		} else {
			part.condition(propertyMap.get(leftD), ConduitConnectionType.NONE);
		}

		if (right) {
			part.condition(propertyMap.get(rightD), ConduitConnectionType.CONDUIT, ConduitConnectionType.RIM);
		} else {
			part.condition(propertyMap.get(rightD), ConduitConnectionType.NONE);
		}

		part.end();
	}

	private static Direction getUp(Axis axis) {
		return axis == Axis.Y ? Direction.SOUTH : Direction.UP;
	}

	private static Direction getDown(Axis axis) {
		return axis == Axis.Y ? Direction.NORTH : Direction.DOWN;
	}

	private static Direction getLeft(Axis axis) {
		return axis == Axis.X ? Direction.SOUTH : Direction.EAST;
	}

	private static Direction getRight(Axis axis) {
		return axis == Axis.X ? Direction.NORTH : Direction.WEST;
	}
}
//?}
