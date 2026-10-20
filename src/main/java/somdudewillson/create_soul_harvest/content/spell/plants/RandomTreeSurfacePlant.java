package somdudewillson.create_soul_harvest.content.spell.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RandomTreeSurfacePlant extends AbstractTreeSurfacePlant {

	@Override
	protected BlockState pickSapling(ServerLevel level, BlockPos groundPos, RandomSource random) {
		Block[] saplings = level.registryAccess()
				.lookupOrThrow(BuiltInRegistries.BLOCK.key())
				.getOrThrow(BlockTags.SAPLINGS)
				.stream()
				.filter(Holder::isBound)
				.map(Holder::value)
				.filter(v->v!=null)
				.filter(b -> b.defaultBlockState().canSurvive(level, groundPos.above()))
				.toArray(Block[]::new);
		Block sapling = saplings[random.nextInt(saplings.length)];
		return sapling.defaultBlockState();
	}
}
