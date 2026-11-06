package somdudewillson.create_soul_harvest.client.texture.palette;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MobPatternRenderTypes extends RenderType {

	private static final Map<ResourceLocation, RenderType> COMPOSITE_CACHE = new ConcurrentHashMap<>();

	private static final RenderType BASE_ITEM_CULL = createBaseItemType(true);
	private static final RenderType BASE_ITEM_NO_CULL = createBaseItemType(false);

	private MobPatternRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static RenderType getBaseItemRenderType(boolean cull) {
		return cull ? BASE_ITEM_CULL : BASE_ITEM_NO_CULL;
	}

	private static RenderType createBaseItemType(boolean cull) {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.setCullState(cull ? CULL : NO_CULL)
				.createCompositeState(true);

		return create(cull ? "create_soul_harvest_base_item_cull" : "create_soul_harvest_base_item_no_cull",
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				true,
				true,
				state);
	}

	/**
	 * Returns a RenderType that binds a standalone dynamic composite texture
	 * in a single pass with proper depth testing and writes.
	 */
	public static RenderType getCompositeItemRenderType(ResourceLocation texture) {
		return COMPOSITE_CACHE.computeIfAbsent(texture, MobPatternRenderTypes::createCompositeItemType);
	}

	private static RenderType createCompositeItemType(ResourceLocation texture) {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.setCullState(NO_CULL)
				.createCompositeState(true);

		return create("mob_pattern_composite_" + texture.getNamespace() + "_" + texture.getPath(),
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				true,
				true,
				state);
	}
}
