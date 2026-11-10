package somdudewillson.create_soul_harvest.content.ponder;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.SoulBudBlock;
import somdudewillson.create_soul_harvest.content.conduit.PotentiaConduitBlock;
import somdudewillson.create_soul_harvest.content.conduit.PotentiaConduitBlock.ConduitConnectionType;
import somdudewillson.create_soul_harvest.content.conduit.WindowedPotentiaConduitBlock;
import somdudewillson.create_soul_harvest.content.conduit.network.PotentiaNetwork;
import somdudewillson.create_soul_harvest.content.kinetics.extractor.PotentiaExtractionMath;
import somdudewillson.create_soul_harvest.content.kinetics.extractor.PotentiaExtractorBlockEntity;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;
import somdudewillson.create_soul_harvest.content.potentia.catalyst.PotentiaGrowthCatalystBlock;
import somdudewillson.create_soul_harvest.content.potentia.tank.PotentiaReservoirBlock;
import somdudewillson.create_soul_harvest.content.potentia.tank.PotentiaReservoirBlockEntity;
import somdudewillson.create_soul_harvest.content.potentia.tank.ReservoirMode;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;

public class SoulHarvestPonderPlugin implements PonderPlugin {
	
	public static ResourceLocation SOUL_CRYSTAL = tag("soul_crystal");
	public static ResourceLocation MAGIC_GOLD = tag("magic_gold");
	public static ResourceLocation POTENTIA = tag("potentia");

	@Override
	public String getModId() {
		return SoulHarvestMod.MOD_ID;
	}
	
	protected static ResourceLocation tag(String path) {
		return ResourceLocation.fromNamespaceAndPath(SoulHarvestMod.MOD_ID, path);
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		helper.forComponents(
					ModItems.SOUL_DEPOSIT_SEED.getId(),
					ModItems.SOUL_SHARD.getId(),
					ModBlocks.SOUL_BUD_SMALL.getId(),
					ModBlocks.SOUL_BUD_MEDIUM.getId(),
					ModBlocks.SOUL_BUD_LARGE.getId(),
					ModBlocks.SOUL_CLUSTER.getId()
				)
			.addStoryBoard(SoulHarvestMod.id("soul_cluster/ponder_soul_cluster"), SoulHarvestPonderPlugin::soulClusterGrowthProgram)
			.addStoryBoard(SoulHarvestMod.id("potentia/ponder_potentia_growth"), SoulHarvestPonderPlugin::potentiaGrowthProgram);
		
		helper.forComponents(
					ModItems.MAGIC_GOLD.getId(),
					ModBlocks.MIDAS_CORE.getId()
				)
			.addStoryBoard(SoulHarvestMod.id("midas_core/ponder_midas_core"), SoulHarvestPonderPlugin::midasCoreConvertProgram)
			.addStoryBoard(SoulHarvestMod.id("midas_core/ponder_midas_core"), SoulHarvestPonderPlugin::midasCoreBoostProgram);
		
		helper.forComponents(
					ModBlocks.POTENTIA_RESERVOIR.getId()
				)
			.addStoryBoard(SoulHarvestMod.id("potentia/ponder_potentia_creative_reservoir"), SoulHarvestPonderPlugin::creativeReservoirProgram);
		helper.forComponents(
					ModBlocks.POTENTIA_CONDUIT.getId()
				)
			.addStoryBoard(SoulHarvestMod.id("potentia/ponder_potentia_conduit"), SoulHarvestPonderPlugin::potentiaConduitProgram);
		helper.forComponents(
					ModBlocks.POTENTIA_EXTRACTOR.getId()
				)
			.addStoryBoard(SoulHarvestMod.id("potentia/ponder_potentia_extractor"), SoulHarvestPonderPlugin::potentiaExtractorProgram);
		helper.forComponents(
					ModBlocks.POTENTIA_GROWTH_CATALYST.getId()
				)
			.addStoryBoard(SoulHarvestMod.id("potentia/ponder_potentia_growth"), SoulHarvestPonderPlugin::potentiaGrowthProgram);
	}

	@Override
	public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
		helper.registerTag(SOUL_CRYSTAL)
				.addToIndex()
				.item(ModBlocks.SOUL_CLUSTER.get(), true, false)
				.title("Soul Crystals")
				.description("Components which help produce Soul Shards")
				.item(ModItems.SOUL_DEPOSIT_SEED.get())
				.item(ModItems.SOUL_SHARD.get())
				.item(ModBlocks.SOUL_BUD_SMALL.get())
				.item(ModBlocks.SOUL_BUD_MEDIUM.get())
				.item(ModBlocks.SOUL_BUD_LARGE.get())
				.register();
		
		helper.registerTag(MAGIC_GOLD)
				.addToIndex()
				.item(ModBlocks.MIDAS_CORE.get(), true, false)
				.title("Anima Gold")
				.description("Components which help produce Anima Gold")
				.item(ModItems.MAGIC_GOLD.get())
				.item(ModBlocks.GOLD_FOAM.get())
				.register();
		
		helper.registerTag(POTENTIA)
				.addToIndex()
				.item(ModBlocks.POTENTIA_EXTRACTOR.get(), true, false)
				.title("Potentia Manipulators")
				.description("Components which help relaying and making use of Potentia")
				.register();
		
		
		helper.addToTag(SOUL_CRYSTAL)
				.add(ModBlocks.SOUL_CLUSTER.getId())
				.add(ModBlocks.SOUL_BUD_LARGE.getId())
				.add(ModBlocks.SOUL_BUD_MEDIUM.getId())
				.add(ModBlocks.SOUL_BUD_SMALL.getId())
				.add(ModItems.SOUL_DEPOSIT_SEED.getId())
				.add(ModItems.SOUL_SHARD.getId())
				.add(ModItems.POTENTIA_GROWTH_CATALYST.getId());
		
		helper.addToTag(MAGIC_GOLD)
				.add(ModBlocks.MIDAS_CORE.getId())
				.add(ModBlocks.GOLD_FOAM.getId())
				.add(ModItems.MAGIC_GOLD.getId());
		
		helper.addToTag(POTENTIA)
				.add(ModBlocks.POTENTIA_EXTRACTOR.getId())
				.add(ModBlocks.POTENTIA_CONDUIT.getId())
				.add(ModBlocks.POTENTIA_GROWTH_CATALYST.getId())
				.add(ModBlocks.POTENTIA_RESERVOIR.getId());
		
		helper.addToTag(AllCreatePonderTags.CREATIVE)
				.add(ModBlocks.POTENTIA_RESERVOIR.getId());
	}
	
	protected static void soulClusterGrowthProgram(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		BlockPos spawner = util.grid().at(2, 2, 2);
		BlockPos cluster = spawner.north();
		
		scene.title("soul_cluster_growth", "Growing Soul Crystals");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0)
				.add(util.select().layer(1)), Direction.UP);
		scene.idle(5);
		scene.world().setBlock(cluster, Blocks.AIR.defaultBlockState(), false);
		scene.world().showSection(util.select().position(spawner)
				.add(util.select().position(cluster)), Direction.DOWN);
		scene.rotateCameraY(-42f);
		scene.idle(25);

		scene.idle(5);
		scene.overlay().showOutlineWithText(util.select().position(cluster), 20*4)
			.colored(PonderPalette.WHITE)
			.text("Use a seed on any face of a spawner to plant a soul bud");
		scene.idle(20*2);
		ItemStack seed = new ItemStack(ModItems.SOUL_DEPOSIT_SEED.get());
		scene.overlay().showControls(util.vector().blockSurface(spawner, Direction.NORTH), Pointing.RIGHT, 20).rightClick()
			.withItem(seed);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.idle(20*2);

		scene.addKeyframe();

		scene.idle(20*2);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().blockSurface(spawner, Direction.NORTH))
			.text("The soul bud will grow over time");
		scene.idle(20);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_BUD_MEDIUM.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.idle(20);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_BUD_LARGE.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.idle(20);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_CLUSTER.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.idle(40);

		scene.addKeyframe();

		scene.overlay().showText(20*3)
			.pointAt(util.vector().blockSurface(spawner, Direction.NORTH))
			.text("When broken...");
		scene.idle(20*3);
		ItemStack shard = new ItemStack(ModItems.SOUL_SHARD.get(), 2);
		scene.world().destroyBlock(cluster);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.world().createItemEntity(util.vector().centerOf(cluster), Vec3.ZERO, shard);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().blockSurface(spawner, Direction.NORTH))
			.text("it will drop soul shards and reset to its initial growth phase");
		scene.idle(20*4);
	}
	
	protected static void midasCoreConvertProgram(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		
		BlockPos core = util.grid().at(3, 1, 3);
		BlockPos cog = util.grid().at(2, 1, 7);
		Selection convertedRegion = util.select().fromTo(6,1,2, 6,1,6)
			.add(util.select().fromTo(6,1,6, 4,1,6))
			.add(util.select().fromTo(4,1,6, 4,1,4))
			.add(util.select().position(3,1,4));
		
		scene.title("midas_core_convert", "Growing Anima Gold");
		scene.configureBasePlate(0, 0, 7);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(15);
		scene.world().showSection(util.select().layer(1)
				.substract(util.select().position(core))
				.substract(util.select().position(cog)), Direction.DOWN);
		scene.idle(15);
		scene.world().showSection(util.select().position(core), Direction.DOWN);
		scene.idle(25);
		
		scene.overlay().showText(20*4)
			.pointAt(util.vector().blockSurface(core, Direction.UP))
			.text("The Midas Core will periodically convert compatible blocks to Gold Foam");
		scene.idle(20 * 4);
		scene.idle(25);
		scene.world().setBlocks(convertedRegion, ModBlocks.GOLD_FOAM.get().defaultBlockState(), false);
		for (BlockPos subPos : convertedRegion) {
			scene.effects().createRedstoneParticles(subPos, 0xFFCF3D, 20);
		}
		scene.idle(25);
		scene.overlay().showOutlineWithText(convertedRegion, 20 * 4)
			.colored(PonderPalette.WHITE)
			.text("The Gold Foam can then be mined for Anima Gold");
		scene.idle(20 * 4);
	}
	
	protected static void midasCoreBoostProgram(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		
		BlockPos core = util.grid().at(3, 1, 3);
		BlockPos cog = util.grid().at(2, 1, 7);
		BlockPos tank = util.grid().at(3, 2, 8);
		BlockPos pump = util.grid().at(3, 2, 7);
		Selection pipe = util.select().fromTo(3,2,6, 3,2,3);
		Selection convertedRegion1 = util.select().fromTo(6,1,2, 6,1,6)
				.add(util.select().fromTo(6,1,6, 4,1,6))
				.add(util.select().fromTo(4,1,6, 4,1,4))
				.add(util.select().position(3,1,4));
		Selection convertedRegion2 = util.select().fromTo(4,1,0, 0,1,0)
				.add(util.select().fromTo(0,1,0, 0,1,3))
				.add(util.select().fromTo(0,1,3, 2,1,3));
		
		scene.title("midas_core_boost", "Boosting the Midas Core");
		scene.configureBasePlate(0, 0, 7);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(15);
		scene.world().showSection(util.select().layer(1)
				.substract(util.select().position(core))
				.substract(util.select().position(cog)), Direction.DOWN);
		scene.idle(15);
		scene.world().showSection(util.select().position(core), Direction.DOWN);
		scene.idle(25);
		
		scene.overlay().showText(20*4)
			.pointAt(util.vector().blockSurface(core, Direction.UP))
			.text("The Midas Core can be accelerated by supplying a liquid catalyst");
		scene.idle(85);
		scene.world().showSection(util.select().layer(2)
				.add(util.select().position(cog))
				.substract(util.select().position(tank)), Direction.NORTH);
		scene.idle(45);
		scene.world().setKineticSpeed(util.select().position(cog), 16);
		scene.world().setKineticSpeed(util.select().position(pump), -32);
		scene.world().propagatePipeChange(pump);
		scene.idle(25);
		
		scene.overlay().showOutlineWithText(pipe, 20*4 + 5)
			.colored(PonderPalette.INPUT)
			.placeNearTarget()
			.text("Liquid Soul Crystal");
		scene.idle(10);
		scene.overlay().showOutlineWithText(util.select().position(core), 20*4)
			.colored(PonderPalette.OUTPUT)
			.placeNearTarget()
			.text("8x Boost");
		scene.idle(20 * 4);
		scene.idle(25);

		scene.world().setBlocks(convertedRegion1, ModBlocks.GOLD_FOAM.get().defaultBlockState(), false);
		for (BlockPos subPos : convertedRegion1) {
			scene.effects().createRedstoneParticles(subPos, 0xFFCF3D, 20);
		}
		scene.idle(25);
		scene.world().setBlocks(convertedRegion2, ModBlocks.GOLD_FOAM.get().defaultBlockState(), false);
		for (BlockPos subPos : convertedRegion2) {
			scene.effects().createRedstoneParticles(subPos, 0xFFCF3D, 20);
		}
		scene.idle(25);
	}
	
	protected static void creativeReservoirProgram(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		
		BlockPos reservoirA = util.grid().at(2, 1, 0);
		BlockPos reservoirB = util.grid().at(2, 1, 4);
		
		scene.title("creative_reservoir", "Creative Potentia Reservoirs");
		scene.configureBasePlate(0, 0, 5);
		scene.setSceneOffsetY(1);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(15);
		scene.world().showSection(util.select().layer(1), Direction.DOWN);
		scene.rotateCameraY(-20f);
		scene.idle(15);
		
		scene.overlay().showOutlineWithText(util.select().position(reservoirB), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Creative Potentia Reservoirs can be used to void incoming potentia...");
		scene.idle(20 * 4);
		
		ItemStack wrench = new ItemStack(AllItems.WRENCH.get());
		scene.overlay().showControls(util.vector().blockSurface(reservoirA, Direction.UP), Pointing.RIGHT, 20).rightClick()
			.withItem(wrench);
		scene.idle(15);
		scene.world()
			.modifyBlock(reservoirA, s->s.setValue(PotentiaReservoirBlock.MODE, ReservoirMode.SUPPLY), false);
		scene.world()
			.modifyBlockEntity(reservoirA, PotentiaReservoirBlockEntity.class, e->e.setMode(ReservoirMode.SUPPLY));
		propagateConduitUpdates(scene, reservoirA);
		scene.idle(15);
		scene.overlay().showOutlineWithText(util.select().position(reservoirA), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("or produce a bottomless supply.");
		scene.idle(20 * 4);
		scene.idle(25);

		scene.overlay().showOutlineWithText(util.select().position(reservoirA), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("The reservoir can be filtered with an appropriate item.");
		scene.idle(20 * 4);
		ItemStack egg = new ItemStack(Items.GLOW_SQUID_SPAWN_EGG);
		scene.overlay().showControls(util.vector().blockSurface(reservoirA, Direction.UP), Pointing.RIGHT, 20).rightClick()
			.withItem(egg);
		scene.world()
			.modifyBlockEntity(reservoirA, PotentiaReservoirBlockEntity.class, e->e.setFilter(egg));
		propagateConduitUpdates(scene, reservoirA);
		scene.idle(25);
	}
	
	protected static void potentiaConduitProgram(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		
		BlockPos reservoirA = util.grid().at(0, 1, 0);
		BlockPos reservoirB = util.grid().at(4, 1, 6);
		BlockPos wrenchedConduit = util.grid().at(4, 1, 4);
		Selection horizontalPipe = util.select().fromTo(0,1,1, 0,1,3)
				.add(util.select().fromTo(0,1,3, 4,1,3))
				.add(util.select().fromTo(4,1,3, 4,1,5));
		Selection verticalPipe = util.select().fromTo(1,1,2, 1,3,2)
				.add(util.select().fromTo(1,3,2, 1,3,3));
		
		scene.title("potentia_conduit", "Moving Potentia with Conduits");
		scene.configureBasePlate(0, 1, 5);
		scene.setSceneOffsetY(1);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(15);
		scene.world().showSection(util.select().position(reservoirA), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(reservoirB), Direction.DOWN);
		scene.rotateCameraY(-20f);
		scene.idle(10);
		scene.world()
			.modifyBlock(reservoirA, s->s.setValue(PotentiaReservoirBlock.MODE, ReservoirMode.SUPPLY), false);
		scene.world()
			.modifyBlockEntity(reservoirA, PotentiaReservoirBlockEntity.class, e->e.setMode(ReservoirMode.SUPPLY));
		scene.idle(10);

		for (int i = 1; i <= 3; i++) {
			scene.world().showSection(util.select().position(0, 1, i),  Direction.EAST);
			scene.idle(3);
		}
		for (int i = 1; i <= 4; i++) {
			scene.world().showSection(util.select().position(i, 1, 3),  Direction.WEST);
			scene.idle(3);
		}
		for (int i = 4; i <= 5; i++) {
			scene.world().showSection(util.select().position(4, 1, i),  Direction.WEST);
			scene.idle(3);
		}
		propagateConduitUpdates(scene, reservoirA);
		scene.idle(15);
		
		scene.overlay().showText(20*4)
			.pointAt(horizontalPipe.getCenter())
			.placeNearTarget()
			.attachKeyFrame()
			.text("Potentia Conduits can connect two or more potentia sources and targets");
		scene.idle(20*4);
		scene.idle(15);
		
		scene.addKeyframe();
		ItemStack wrench = new ItemStack(AllItems.WRENCH.get());
		scene.overlay().showControls(util.vector().blockSurface(wrenchedConduit, Direction.UP), Pointing.RIGHT, 20).rightClick()
			.withItem(wrench);
		scene.idle(5);
		scene.world()
			.modifyBlock(wrenchedConduit, s->ModBlocks.WINDOWED_POTENTIA_CONDUIT.get().defaultBlockState()
					.setValue(WindowedPotentiaConduitBlock.AXIS, PotentiaConduitBlock.getStraightAxis(s)), true);
		propagateConduitUpdates(scene, reservoirA);
		scene.overlay().showOutlineWithText(util.select().position(wrenchedConduit), 20*4)
				.placeNearTarget()
				.text("Using a wrench, a straight conduit segment can be given a window");
		scene.idle(20*4);
		scene.idle(15);
		

		Vec3 center = util.vector().centerOf(1, 1, 2);
		AABB bb = new AABB(center, center).inflate(1 / 6f);
		AABB bb1 = bb.move(-0.5, 0, 0);
		AABB bb2 = bb.move(0, 0, 0.5);

		scene.addKeyframe();
		scene.world().showSection(verticalPipe, Direction.DOWN);
		scene.idle(10);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, bb1, bb, 1);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, bb2, bb, 1);
		scene.idle(1);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, bb1, bb1, 50);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, bb2, bb2, 50);
		scene.idle(10);
		scene.overlay().showText(20*4)
			.attachKeyFrame()
			.pointAt(util.vector().centerOf(1, 1, 2))
			.placeNearTarget()
			.colored(PonderPalette.RED)
			.text("Windowed conduits will not connect to any other adjacent conduit segments");
		scene.idle(20*4);
		scene.idle(15);
		scene.world().hideSection(verticalPipe, Direction.UP);
		scene.idle(20);

		scene.overlay().showOutline(PonderPalette.GREEN, new Object(), util.select().position(reservoirA), 100);
		scene.idle(5);
		scene.overlay().showOutline(PonderPalette.GREEN, new Object(), util.select().position(reservoirB), 100);
		scene.idle(5);
		scene.overlay().showText(20*4)
			.attachKeyFrame()
			.independent()
			.text("Once source and target are connected by conduits, the endpoints gradually transfer their contents");
		scene.overlay().showLine(PonderPalette.GREEN, util.vector().blockSurface(reservoirA, Direction.WEST),
								 util.vector().blockSurface(reservoirB, Direction.EAST), 80);
		scene.idle(20*4);
		scene.idle(15);

		scene.overlay().showText(20*5)
			.text("Thus, the Conduit blocks themselves never 'physically' contain any potentia")
			.placeNearTarget()
			.pointAt(util.vector().topOf(2, 1, 3));
		scene.idle(20*4);
	}
	
	protected static void potentiaExtractorProgram(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		
		BlockPos reservoir = util.grid().at(3, 4, 0);
		BlockPos spawner = util.grid().at(3, 1, 4);
		BlockPos upperExtractor = util.grid().at(3, 2, 4);
		BlockPos lowerExtractor = util.grid().at(3, 1, 3);
		
		scene.title("potentia_extractor", "Extracting Potentia");
		scene.configureBasePlate(0, 1, 7);
		scene.setSceneOffsetY(1);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(15);
		scene.world().showSection(util.select().position(spawner), Direction.DOWN);
		scene.idle(5);
		scene.rotateCameraY(-20f);
		scene.idle(10);
		
		
		scene.world().setKineticSpeed(util.select().position(upperExtractor), 0);
		scene.world().setKineticSpeed(util.select().position(lowerExtractor), 0);
		scene.world().showSection(util.select().position(upperExtractor), Direction.DOWN);
		scene.idle(5);
		scene.overlay().showOutlineWithText(util.select().position(upperExtractor), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("When facing a spawner and powered, an Extractor will extract potentia");
		scene.idle(5);
		scene.world().setKineticSpeed(util.select().position(upperExtractor), 32);
		scene.idle(5);
		scene.effects().rotationSpeedIndicator(upperExtractor);
		scene.idle(20*4);

		scene.world()
			.modifyBlock(util.grid().at(3, 4, 2), s->s.setValue(PotentiaConduitBlock.DOWN, ConduitConnectionType.NONE).setValue(PotentiaConduitBlock.CASING, false), false);
		scene.world().showSection(util.select().position(3, 3, 4), Direction.DOWN);
		scene.idle(3);
		for (int i = 4; i >= 1; i--) {
			scene.world().showSection(util.select().position(3, 4, i),  Direction.DOWN);
			scene.idle(3);
		}
		scene.idle(5);
		scene.world().showSection(util.select().position(reservoir), Direction.DOWN);
		scene.idle(10);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().topOf(3, 4, 4))
			.placeNearTarget()
			.attachKeyFrame()
			.text("The Extractor will automatically push potentia into connected Potentia Conduits");
		scene.idle(20*4);
		scene.idle(5);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().blockSurface(upperExtractor, Direction.WEST))
			.colored(PonderPalette.RED)
			.placeNearTarget()
			.text("The Extractor has no internal buffer - any excess potentia extracted will be lost");
		scene.idle(20*4);
		scene.idle(15);
		
		
		scene.world().showSection(util.select().position(lowerExtractor), Direction.SOUTH);
		scene.world().setKineticSpeed(util.select().position(lowerExtractor), 32);
		scene.idle(5);
		for (int i = 1; i <= 3; i++) {
			scene.world().showSection(util.select().position(3, i, 2),  Direction.SOUTH);
			scene.idle(3);
		}
		scene.world().restoreBlocks(util.select().position(3, 4, 2));
		scene.idle(10);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().blockSurface(lowerExtractor, Direction.WEST))
			.placeNearTarget()
			.attachKeyFrame()
			.text("Additional Extractors may be placed to increase yield");
		scene.idle(20*4);
		scene.idle(10);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().blockSurface(lowerExtractor, Direction.WEST))
			.colored(PonderPalette.RED)
			.placeNearTarget()
			.text("However, this incurs diminishing returns");
		scene.idle(20*4);
	}
	
	protected static void potentiaGrowthProgram(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		
		BlockPos reservoir = util.grid().at(5, 2, 2);
		BlockPos catalyst = util.grid().at(2, 2, 2);
		Selection crystals = util.select().position(2,3,2)
				.add(util.select().position(2,2,3))
				.add(util.select().position(2,2,1))
				.add(util.select().position(1,2,2))
				.add(util.select().position(2,1,2));
		
		scene.title("potentia_growth", "Growing Soul Crystals with Potentia");
		scene.configureBasePlate(0, 0, 5);
		scene.setSceneOffsetY(1);
		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(15);
		scene.world().showSection(util.select().position(catalyst), Direction.EAST);
		scene.world().modifyBlock(catalyst, s->s.setValue(PotentiaGrowthCatalystBlock.LIT, false), false);
		scene.idle(15);

		scene.overlay().showOutlineWithText(util.select().position(catalyst), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("A Potentia Growth Catalyst can be used to grow Soul Crystals without a spawner");
		scene.idle(20*4);
		scene.idle(15);
		
		scene.rotateCameraY(25f);
		scene.idle(3);
		for (int i = 3; i <= 4; i++) {
			scene.world().showSection(util.select().position(i, 2, 2),  Direction.WEST);
			scene.idle(3);
		}
		scene.idle(5);
		scene.world().showSection(util.select().position(reservoir), Direction.DOWN);
		scene.idle(3);
		propagateConduitUpdates(scene, reservoir);
		scene.world().modifyBlock(catalyst, s -> s.setValue(PotentiaGrowthCatalystBlock.LIT, true), false);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().topOf(3, 2, 2))
			.placeNearTarget()
			.attachKeyFrame()
			.text("It must be supplied with Potentia to operate");
		scene.idle(20*4);
		scene.idle(15);
		scene.rotateCameraY(-25f);
		scene.overlay().showText(20*4)
			.pointAt(util.vector().topOf(catalyst))
			.placeNearTarget()
			.text("An active Growth Catalyst emits light");
		scene.idle(20*4);
		scene.idle(15);
		
		ItemStack seed = new ItemStack(ModItems.SOUL_DEPOSIT_SEED.get());
		scene.overlay().showControls(util.vector().blockSurface(catalyst, Direction.WEST), Pointing.LEFT, 20).rightClick()
			.withItem(seed);
		scene.idle(5);
		scene.world().setBlock(
				catalyst.west(), 
				ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.WEST)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.world().showSection(util.select().position(catalyst.west()), null);
		scene.idle(35);
		scene.overlay().showOutlineWithText(util.select().position(catalyst.west()), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Soul Crystals may be planted on an active Growth Catalyst");
		scene.idle(20*4);
		scene.idle(25);
		scene.effects().indicateSuccess(catalyst);
		scene.world().setBlock(
				catalyst.west(), 
				ModBlocks.SOUL_BUD_MEDIUM.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.WEST)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.overlay().showOutlineWithText(util.select().position(catalyst), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Each Soul Crystal growth tick will consume Potentia from the Catalyst's internal buffer");
		scene.idle(20*1);
		scene.effects().indicateSuccess(catalyst);
		scene.world().setBlock(
				catalyst.west(), 
				ModBlocks.SOUL_BUD_LARGE.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.WEST)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.idle(20*1);
		scene.effects().indicateSuccess(catalyst);
		scene.world().restoreBlocks(util.select().position(catalyst.west()));
		scene.idle(20*2);
		scene.idle(25);
		scene.overlay().showOutlineWithText(util.select().position(catalyst.west()), 20*4)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Additionally, each Soul Crystal attached to the Catalyst will drain a small amount of Potentia continously");
		scene.idle(20*4);
		scene.idle(15);

		scene.overlay().showControls(util.vector().blockSurface(catalyst, Direction.NORTH), Pointing.RIGHT, 20).rightClick()
			.withItem(seed);
		scene.idle(5);
		scene.world().showSection(util.select().position(catalyst.north()), null);
		scene.idle(30);
		scene.rotateCameraY(-45f);
		scene.idle(5);
		scene.overlay().showControls(util.vector().blockSurface(catalyst, Direction.SOUTH), Pointing.LEFT, 20).rightClick()
			.withItem(seed);
		scene.idle(5);
		scene.world().showSection(util.select().position(catalyst.south()), null);
		scene.idle(30);
		scene.rotateCameraY(45f);
		scene.idle(5);
		scene.overlay().showControls(util.vector().blockSurface(catalyst, Direction.UP), Pointing.RIGHT, 20).rightClick()
			.withItem(seed);
		scene.idle(5);
		scene.world().showSection(util.select().position(catalyst.above()), null);
		scene.idle(30);
		scene.overlay().showControls(util.vector().blockSurface(catalyst, Direction.DOWN), Pointing.LEFT, 20).rightClick()
			.withItem(seed);
		scene.idle(5);
		scene.world().showSection(util.select().position(catalyst.below()), null);
		scene.idle(30);
		scene.overlay().showOutlineWithText(crystals, 20*4)
			.colored(PonderPalette.GREEN)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Up to five Soul Crystals may be planted on a single Catalyst");
		scene.idle(20*4);
	}
	
	// --- Helpers

	public static void propagateConduitUpdates(SceneBuilder scene, BlockPos startPos) {
		scene.addInstruction(s -> {
			PonderLevel level = s.getWorld();
			Set<BlockPos> toUpdate = collectConnectedConduits(level, startPos);
			updateConduits(level, toUpdate);
			s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
			PotentiaNetwork.invalidate(startPos);
			triggerSupplierDistribution(level, startPos, toUpdate);
		});
	}

	public static void propagateConduitUpdates(SceneBuilder scene, Selection selection) {
		scene.addInstruction(s -> {
			PonderLevel level = s.getWorld();
			Set<BlockPos> toUpdate = new LinkedHashSet<>();
			selection.forEach(pos -> {
				if (level.getBlockState(pos).getBlock() instanceof IPotentiaTransporter) {
					toUpdate.add(pos.immutable());
				}
			});
			updateConduits(level, toUpdate);
			s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
			for (BlockPos pos : toUpdate) {
				PotentiaNetwork.invalidate(pos);
			}
			triggerSupplierDistribution(level, null, toUpdate);
		});
	}

	private static void triggerSupplierDistribution(PonderLevel level, BlockPos startPos, Set<BlockPos> conduits) {
		if (startPos != null) {
			distributeIfSupplier(level, startPos);
		}
		for (BlockPos conduitPos : conduits) {
			for (Direction d : Direction.values()) {
				distributeIfSupplier(level, conduitPos.relative(d));
			}
		}
	}

	private static void distributeIfSupplier(PonderLevel level, BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof PotentiaReservoirBlockEntity reservoir && reservoir.getMode().isSupply()) {
			for (Direction d : Direction.values()) {
				BlockPos neighborPos = pos.relative(d);
				if (level.getBlockState(neighborPos).getBlock() instanceof IPotentiaTransporter) {
					PotentiaNetwork.distributeFrom(level, pos, d, reservoir.getPotentiaHandler(d));
				}
			}
		} else if (be instanceof PotentiaExtractorBlockEntity extractor) {
			if (extractor.getSpeed() != 0 && !extractor.isOverStressed() && extractor.isSpeedRequirementFulfilled()) {
				float pts = PotentiaExtractionMath.calculatePoints(extractor.getSpeed(), extractor.getMinSpeed());
				long rate = PotentiaExtractionMath.calculateTotalPotentia(pts, PotentiaExtractionMath.DEFAULT_MAX_POTENTIA_PER_TICK);
				if (rate > 0) {
					extractor.pushPotentia(rate, extractor.getProducedPotentiaType());
				}
			}
		}
	}

	private static Set<BlockPos> collectConnectedConduits(PonderLevel level, BlockPos startPos) {
		Set<BlockPos> toUpdate = new LinkedHashSet<>();
		Queue<BlockPos> queue = new ArrayDeque<>();

		if (level.getBlockState(startPos).getBlock() instanceof IPotentiaTransporter) {
			toUpdate.add(startPos.immutable());
			queue.add(startPos.immutable());
		}

		for (Direction dir : Direction.values()) {
			BlockPos neighbor = startPos.relative(dir);
			if (level.getBlockState(neighbor).getBlock() instanceof IPotentiaTransporter) {
				if (toUpdate.add(neighbor.immutable())) {
					queue.add(neighbor.immutable());
				}
			}
		}

		while (!queue.isEmpty() && toUpdate.size() < PotentiaNetwork.MAX_NETWORK_SIZE) {
			BlockPos current = queue.poll();
			for (Direction dir : Direction.values()) {
				BlockPos neighbor = current.relative(dir);
				if (!toUpdate.contains(neighbor) && level.getBlockState(neighbor).getBlock() instanceof IPotentiaTransporter) {
					toUpdate.add(neighbor.immutable());
					queue.add(neighbor.immutable());
				}
			}
		}

		return toUpdate;
	}

	private static boolean updateConduits(PonderLevel level, Set<BlockPos> positions) {
		boolean anyChanged = false;
		for (BlockPos pos : positions) {
			BlockState current = level.getBlockState(pos);
			BlockState updated = current;
			if (current.getBlock() instanceof PotentiaConduitBlock) {
				updated = PotentiaConduitBlock.updateBlockState(current, null, null, level, pos);
			} else if (current.getBlock() instanceof WindowedPotentiaConduitBlock) {
				updated = WindowedPotentiaConduitBlock.updateBlockState(current, level, pos);
			}
			if (updated != current) {
				level.setBlockAndUpdate(pos, updated);
				anyChanged = true;
			}
		}
		return anyChanged;
	}
}
