package somdudewillson.create_soul_harvest.client.texture.palette;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MobPatternRenderTypes extends RenderType {

	private record TypeKey(ResourceLocation texture, int quantizedIntensity) {}
	private static final Map<TypeKey, RenderType> CACHE = new ConcurrentHashMap<>();

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

	public static RenderType getPatternRenderType(ResourceLocation textureLoc, float intensity) {
		int quantized = Math.round(intensity * 100f);
		return CACHE.computeIfAbsent(new TypeKey(textureLoc, quantized), key -> createPatternType(key.texture(), key.quantizedIntensity() / 100.0f));
	}

	public static RenderType getPatternRenderType(ResourceLocation textureLoc, boolean halfOpacity) {
		return getPatternRenderType(textureLoc, halfOpacity ? 0.5f : 1.0f);
	}

	private static RenderType createPatternType(ResourceLocation texture, float intensity) {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setWriteMaskState(COLOR_WRITE)
				.setDepthTestState(EQUAL_DEPTH_TEST)
				.setCullState(NO_CULL)
				.setTexturingState(new RenderStateShard.TexturingStateShard("mob_pattern_pulse", () -> {
					long millis = Util.getMillis();
					double cycle = (millis % 2500L) / 2500.0 * (Math.PI * 2.0);
					float pulse = (float) (0.50 + 0.10 * Math.sin(cycle));
					float alpha = pulse * intensity;
					RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
				}, () -> {
					RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				}))
				.createCompositeState(false);

		return create("mob_pattern_overlay_" + texture.getPath() + "_" + Math.round(intensity * 100),
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				false,
				false,
				state);
	}
}
