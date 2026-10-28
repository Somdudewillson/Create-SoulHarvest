package somdudewillson.create_soul_harvest.content.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.block.SoulBudBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModItems;

public class SoulHarvestPonderPlugin implements PonderPlugin {

	@Override
	public String getModId() {
		return SoulHarvestMod.MOD_ID;
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
			.addStoryBoard(SoulHarvestMod.id("soul_cluster/ponder_soul_cluster"), SoulHarvestPonderPlugin::soulClusterGrowthProgram);
		helper.forComponents(
					ModItems.MAGIC_GOLD.getId(),
					ModBlocks.MIDAS_CORE.getId()
				)
			.addStoryBoard(SoulHarvestMod.id("midas_core/ponder_midas_core"), SoulHarvestPonderPlugin::midasCoreConvertProgram)
			.addStoryBoard(SoulHarvestMod.id("midas_core/ponder_midas_core"), SoulHarvestPonderPlugin::midasCoreBoostProgram);
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
}
