package somdudewillson.create_soul_harvest.content.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.block.AnimaGlassBlockEntity;
import somdudewillson.create_soul_harvest.registry.ModBlocks;

public class InstantShelterSpell implements GlyphSpell {

	public InstantShelterSpell() {}

	@Override
	public void cast(Level level, LivingEntity caster) {
		if (level instanceof ServerLevel serverLevel) {
			final double bubbleRadius = 6.0;
			final double bubbleThickness = 0.9;
			BlockPos center = caster.blockPosition();
			int extent = (int) Math.ceil(bubbleRadius + bubbleThickness);

			for (BlockPos testPos : BlockPos.betweenClosed(center.offset(-extent, -extent, -extent), center.offset(extent, extent, extent))) {
				BlockState cur = serverLevel.getBlockState(testPos);
				if (!cur.canBeReplaced()) continue;

				double distFromCenter = Math.sqrt(testPos.distToCenterSqr(center.getCenter()));
				double distFromEdge = Math.abs(distFromCenter - bubbleRadius);
				if (distFromEdge <= bubbleThickness) {
					serverLevel.setBlockAndUpdate(testPos, ModBlocks.MAGIC_GLASS.get().defaultBlockState());
					BlockEntity be = serverLevel.getBlockEntity(testPos);
					if (be instanceof AnimaGlassBlockEntity animaBe) {
						animaBe.setOwnerId(caster.getUUID());
					}
				}
			}
		}
	}
}
