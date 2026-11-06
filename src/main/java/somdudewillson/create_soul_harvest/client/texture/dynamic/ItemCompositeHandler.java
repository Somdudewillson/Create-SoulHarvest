package somdudewillson.create_soul_harvest.client.texture.dynamic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureGenerator;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureGenerator.GenerationResult;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Common contract for items with procedurally composited dynamic textures.
 */
public interface ItemCompositeHandler {

	String getItemPath();

	ResourceLocation getTextureLocation(@Nullable ResourceLocation entityId);

	List<BakedQuad> getQuads();

	boolean isActive();

	boolean isLoaded();

	/**
	 * Returns true if this handler provides a default dynamic composite texture even when un-attuned.
	 * Returns false if un-attuned items should use vanilla baked models and textures.
	 */
	boolean hasDefaultDynamicTexture();

	void ensureLoaded(ResourceManager resourceManager, Minecraft mc);

	void reload(ResourceManager resourceManager, Minecraft mc);

	void uploadCompositeForEntity(ResourceLocation entityId, BufferedImage mobPatternImage, Minecraft mc);

	void dumpToDisk(Path outputDir, Map<ResourceLocation, MobPatternTextureGenerator.GenerationResult> results) throws Exception;

	void clear();
}
