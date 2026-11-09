package somdudewillson.create_soul_harvest.content.kinetics.extractor;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.client.ModPartialModels;

public class PotentiaExtractorRenderer extends KineticBlockEntityRenderer<PotentiaExtractorBlockEntity> {

	public PotentiaExtractorRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(PotentiaExtractorBlockEntity be, BlockState state) {
		return CachedBuffers.partialFacing(ModPartialModels.POTENTIA_EXTRACTOR_HEAD, state);
	}
}
