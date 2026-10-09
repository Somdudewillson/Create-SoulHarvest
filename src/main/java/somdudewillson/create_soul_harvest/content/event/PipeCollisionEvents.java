package somdudewillson.create_soul_harvest.content.event;

import com.simibubi.create.api.event.PipeCollisionEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
*///?}

import somdudewillson.create_soul_harvest.content.block.BiocideGasBlock;
import somdudewillson.create_soul_harvest.registry.ModBlocks;
import somdudewillson.create_soul_harvest.registry.ModFluids;
import somdudewillson.create_soul_harvest.util.AdvancementHelper;

public class PipeCollisionEvents {

	@SubscribeEvent
	public static void onPipeCollision(PipeCollisionEvent.Flow event) {
		boolean isLavaFirst = event.getFirstFluid().isSame(Fluids.LAVA) && event.getSecondFluid().isSame(ModFluids.WITHER_SLURRY.get());
		boolean isSlurryFirst = event.getFirstFluid().isSame(ModFluids.WITHER_SLURRY.get()) && event.getSecondFluid().isSame(Fluids.LAVA);

		if (isLavaFirst || isSlurryFirst) {
			BlockState gasState = ModBlocks.BIOCIDE_GAS.get().defaultBlockState().setValue(BiocideGasBlock.AGE, 15);
			event.setState(gasState);

			AABB box = AABB.ofSize(event.getPos().getCenter(), 16, 16, 16);
			for (Player player : event.getLevel().players()) {
				if (box.contains(player.position()) && player instanceof ServerPlayer serverPlayer) {
					AdvancementHelper.grantAdvancement(serverPlayer, "made_biocide_gas");
				}
			}
		}
	}
}
