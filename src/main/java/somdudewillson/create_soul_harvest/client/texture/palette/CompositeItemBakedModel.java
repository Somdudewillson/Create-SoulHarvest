package somdudewillson.create_soul_harvest.client.texture.palette;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.capability.ISoulHolder;

import java.util.*;

//? neoforge {
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
//?} forge {
/*import net.minecraftforge.client.model.BakedModelWrapper;
*///?}

/**
 * Custom baked model for soul items with custom spritesheet compositing.
 */
public class CompositeItemBakedModel extends BakedModelWrapper<BakedModel> {

	private final String itemPath;

	public CompositeItemBakedModel(BakedModel originalModel, String itemPath) {
		super(originalModel);
		this.itemPath = itemPath;
	}

	public String getItemPath() {
		return itemPath;
	}

	@Override
	public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
		return List.of(this);
	}

	@Override
	public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
		ItemCompositeHandler handler = MobPatternTextureManager.getCompositeHandler(itemPath);
		if (handler != null && handler.isActive()) {
			ResourceLocation heldSoul = ISoulHolder.getHeldSoul(itemStack);
			if (heldSoul != null || handler.hasDefaultDynamicTexture()) {
				ResourceLocation textureLoc = handler.getTextureLocation(heldSoul);
				return List.of(MobPatternRenderTypes.getCompositeItemRenderType(textureLoc));
			}
		}
		return originalModel.getRenderTypes(itemStack, fabulous);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		if (side != null) {
			return List.of();
		}
		ItemCompositeHandler handler = MobPatternTextureManager.getCompositeHandler(itemPath);
		if (handler != null && handler.isActive() && handler.hasDefaultDynamicTexture()) {
			List<BakedQuad> quads = handler.getQuads();
			if (!quads.isEmpty()) {
				return quads;
			}
		}
		return originalModel.getQuads(state, side, rand);
	}

	//? >= 1.21 {
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, net.neoforged.neoforge.client.model.data.ModelData extraData, @Nullable RenderType renderType) {
		return getQuads(state, side, rand);
	}
	//?} else {
	/*@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, net.minecraftforge.client.model.data.ModelData extraData, @Nullable RenderType renderType) {
		return getQuads(state, side, rand);
	}
	*///?}

	@Override
	public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		BakedModel transformed = originalModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
		if (transformed == originalModel) {
			return this;
		}
		return new CompositeItemBakedModel(transformed, itemPath);
	}

	@Override
	public ItemOverrides getOverrides() {
		ItemOverrides originalOverrides = originalModel.getOverrides();
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
				BakedModel baseResolved = originalOverrides.resolve(originalModel, stack, level, entity, seed);
				BakedModel targetBase = (baseResolved != null) ? baseResolved : originalModel;

				ItemCompositeHandler handler = MobPatternTextureManager.getCompositeHandler(itemPath);
				if (handler != null) {
					if (!handler.isLoaded()) {
						Minecraft mc = Minecraft.getInstance();
						if (mc != null && mc.getResourceManager() != null) {
							handler.ensureLoaded(mc.getResourceManager(), mc);
						}
					}
					if (handler.isActive()) {
						ResourceLocation heldSoul = ISoulHolder.getHeldSoul(stack);
						if (heldSoul != null || handler.hasDefaultDynamicTexture()) {
							ResourceLocation textureLoc = handler.getTextureLocation(heldSoul);
							List<BakedQuad> quads = handler.getQuads();
							return new CompositeItemResolvedModel(targetBase, textureLoc, quads);
						}
					}
				}
				return targetBase;
			}
		};
	}

	@Nullable
	public static String getTargetItemPath(ResourceLocation loc) {
		if (loc == null || !loc.getNamespace().equals(SoulHarvestMod.MOD_ID)) {
			return null;
		}
		String path = loc.getPath();
		if (path.startsWith("item/")) {
			path = path.substring(5);
		}
		return MobPatternTextureManager.isRegisteredComposite(path) ? path : null;
	}

	public static boolean isTargetItem(ResourceLocation loc) {
		return getTargetItemPath(loc) != null;
	}

	//? >= 1.21 {
	public static void wrapTargetModels(Map<ModelResourceLocation, BakedModel> models) {
		for (Map.Entry<ModelResourceLocation, BakedModel> entry : models.entrySet()) {
			ResourceLocation loc = entry.getKey().id();
			String path = getTargetItemPath(loc);
			if (path != null) {
				entry.setValue(new CompositeItemBakedModel(entry.getValue(), path));
			}
		}
	}
	//?} else {
	/*public static void wrapTargetModels(Map<ResourceLocation, BakedModel> models) {
		for (Map.Entry<ResourceLocation, BakedModel> entry : models.entrySet()) {
			ResourceLocation loc = entry.getKey();
			String path = getTargetItemPath(loc);
			if (path != null) {
				entry.setValue(new CompositeItemBakedModel(entry.getValue(), path));
			}
		}
	}
	*///?}

	public static class CompositeItemResolvedModel extends BakedModelWrapper<BakedModel> {
		private final ResourceLocation textureLoc;
		private final List<BakedQuad> quads;

		public CompositeItemResolvedModel(BakedModel originalModel, ResourceLocation textureLoc, List<BakedQuad> quads) {
			super(originalModel);
			this.textureLoc = textureLoc;
			this.quads = quads;
		}

		public CompositeItemResolvedModel(BakedModel originalModel, ResourceLocation textureLoc) {
			this(originalModel, textureLoc, Collections.emptyList());
		}

		@Override
		public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
			return List.of(this);
		}

		@Override
		public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
			return List.of(MobPatternRenderTypes.getCompositeItemRenderType(textureLoc));
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
			if (side != null) {
				return List.of();
			}
			return (quads != null && !quads.isEmpty()) ? quads : originalModel.getQuads(state, side, rand);
		}

		//? >= 1.21 {
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, net.neoforged.neoforge.client.model.data.ModelData extraData, @Nullable RenderType renderType) {
			return getQuads(state, side, rand);
		}
		//?} else {
		/*@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, net.minecraftforge.client.model.data.ModelData extraData, @Nullable RenderType renderType) {
			return getQuads(state, side, rand);
		}
		*///?}

		@Override
		public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
			BakedModel transformed = originalModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
			if (transformed == originalModel) {
				return this;
			}
			return new CompositeItemResolvedModel(transformed, textureLoc, quads);
		}
	}
}
