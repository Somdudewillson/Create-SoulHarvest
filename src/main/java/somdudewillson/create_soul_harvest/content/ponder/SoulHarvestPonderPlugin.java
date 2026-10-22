package somdudewillson.create_soul_harvest.content.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
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
		ItemStack seed = new ItemStack(ModItems.SOUL_DEPOSIT_SEED);
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
		scene.overlay().showText(20*3)
			.pointAt(util.vector().blockSurface(spawner, Direction.NORTH))
			.text("The soul bud will grow over time");
		scene.idle(15);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_BUD_MEDIUM.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.idle(15);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_BUD_LARGE.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.idle(15);
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
		ItemStack shard = new ItemStack(ModItems.SOUL_SHARD, 2);
		scene.world().destroyBlock(cluster);
		scene.world().setBlock(
				cluster, 
				ModBlocks.SOUL_BUD_SMALL.get().defaultBlockState()
					.setValue(SoulBudBlock.FACING, Direction.NORTH)
					.setValue(SoulBudBlock.ACTIVE, true), 
				false);
		scene.world().createItemEntity(util.vector().centerOf(cluster), Vec3.ZERO, shard);
		scene.overlay().showText(20*3)
			.pointAt(util.vector().blockSurface(spawner, Direction.NORTH))
			.text("it will drop soul shards and reset to its initial growth phase");
		scene.idle(20*3);
	}
}
