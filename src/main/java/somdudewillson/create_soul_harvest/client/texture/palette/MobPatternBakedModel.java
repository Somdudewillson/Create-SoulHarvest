package somdudewillson.create_soul_harvest.client.texture.palette;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.content.capability.ISoulGlintHolder;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.util.*;

//? neoforge {
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
//?} forge {
/*import net.minecraftforge.client.model.BakedModelWrapper;
*///?}

public class MobPatternBakedModel extends BakedModelWrapper<BakedModel> {

	private static final Set<String> TARGET_ITEMS = Set.of(
			"soul_book_simple",
			"soul_book_standard",
			"soul_book_superior",
			"polyclay_attuned",
			"polyclay_andesite_attuned",
			"polyclay_brass_attuned",
			"experience_effigy_attuned",
			"loot_token"
	);

	public MobPatternBakedModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
		if (ISoulGlintHolder.hasValidSoulGlint(itemStack)) {
			ResourceLocation entityId = ISoulGlintHolder.getEntityId(itemStack);
			if (entityId != null) {
				ResourceLocation textureLoc = MobPatternTextureManager.getTextureLocation(entityId);
				if (textureLoc != null) {
					float intensity = ISoulGlintHolder.getIntensity(itemStack);
					return List.of(this, new PatternOverlayModel(originalModel, textureLoc, intensity));
				}
			}
		}
		return List.of(this);
	}

	@Override
	public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
		if (ISoulGlintHolder.hasValidSoulGlint(itemStack)) {
			return List.of(MobPatternRenderTypes.getBaseItemRenderType(fabulous));
		}
		return originalModel.getRenderTypes(itemStack, fabulous);
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		BakedModel transformed = originalModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
		if (transformed == originalModel) {
			return this;
		}
		return new MobPatternBakedModel(transformed);
	}

	@Override
	public ItemOverrides getOverrides() {
		ItemOverrides originalOverrides = originalModel.getOverrides();
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
				BakedModel resolved = originalOverrides.resolve(originalModel, stack, level, entity, seed);
				if (resolved == null || resolved == originalModel) {
					return MobPatternBakedModel.this;
				}
				return new MobPatternBakedModel(resolved);
			}
		};
	}

	public static boolean isTargetItem(ResourceLocation loc) {
		if (loc == null || !loc.getNamespace().equals(SoulHarvestMod.MOD_ID)) {
			return false;
		}
		String path = loc.getPath();
		if (path.startsWith("item/")) {
			path = path.substring(5);
		}
		return TARGET_ITEMS.contains(path);
	}

	//? >= 1.21 {
	public static void wrapTargetModels(Map<ModelResourceLocation, BakedModel> models) {
		for (Map.Entry<ModelResourceLocation, BakedModel> entry : models.entrySet()) {
			ResourceLocation loc = entry.getKey().id();
			if (isTargetItem(loc)) {
				entry.setValue(new MobPatternBakedModel(entry.getValue()));
			}
		}
	}
	//?} else {
	/*public static void wrapTargetModels(Map<ResourceLocation, BakedModel> models) {
		for (Map.Entry<ResourceLocation, BakedModel> entry : models.entrySet()) {
			ResourceLocation loc = entry.getKey();
			if (isTargetItem(loc)) {
				entry.setValue(new MobPatternBakedModel(entry.getValue()));
			}
		}
	}
	*///?}

	public static class PatternOverlayModel extends BakedModelWrapper<BakedModel> {
		private final ResourceLocation textureLoc;
		private final float intensity;
		private final List<BakedQuad> unculled;
		private final Map<Direction, List<BakedQuad>> culled;

		public PatternOverlayModel(BakedModel originalModel, ResourceLocation textureLoc, float intensity) {
			super(originalModel);
			this.textureLoc = textureLoc;
			this.intensity = intensity;

			RandomSource rand = RandomSource.create(42L);
			this.unculled = remapQuads(originalModel.getQuads(null, null, rand));
			Map<Direction, List<BakedQuad>> map = new EnumMap<>(Direction.class);
			for (Direction dir : Direction.values()) {
				map.put(dir, remapQuads(originalModel.getQuads(null, dir, rand)));
			}
			this.culled = map;
		}

		@Override
		public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
			return List.of(this);
		}

		@Override
		public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
			return List.of(MobPatternRenderTypes.getPatternRenderType(textureLoc, intensity));
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
			if (side == null) {
				return unculled;
			}
			return culled.getOrDefault(side, List.of());
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
			return new PatternOverlayModel(transformed, textureLoc, intensity);
		}

		private static List<BakedQuad> remapQuads(List<BakedQuad> quads) {
			if (quads == null || quads.isEmpty()) {
				return List.of();
			}
			List<BakedQuad> remapped = new ArrayList<>(quads.size());
			for (BakedQuad quad : quads) {
				int[] vertices = quad.getVertices();
				if (vertices == null || vertices.length < 32) {
					remapped.add(quad);
					continue;
				}
				TextureAtlasSprite sprite = quad.getSprite();
				if (sprite != null) {
					float u0 = sprite.getU0();
					float u1 = sprite.getU1();
					float v0 = sprite.getV0();
					float v1 = sprite.getV1();
					float du = u1 - u0;
					float dv = v1 - v0;
					if (Math.abs(du) > 1e-6f && Math.abs(dv) > 1e-6f) {
						int[] newVertices = Arrays.copyOf(vertices, vertices.length);
						for (int i = 0; i < 4; i++) {
							int offset = i * 8;
							float u = Float.intBitsToFloat(vertices[offset + 4]);
							float v = Float.intBitsToFloat(vertices[offset + 5]);
							float normU = (u - u0) / du;
							float normV = (v - v0) / dv;
							newVertices[offset + 4] = Float.floatToRawIntBits(normU);
							newVertices[offset + 5] = Float.floatToRawIntBits(normV);
						}
						remapped.add(new BakedQuad(newVertices, -1, quad.getDirection(), sprite, quad.isShade()));
						continue;
					}
				}
				remapped.add(quad);
			}
			return remapped;
		}
	}
}
