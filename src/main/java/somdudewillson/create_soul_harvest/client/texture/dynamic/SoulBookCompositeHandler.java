package somdudewillson.create_soul_harvest.client.texture.dynamic;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureGenerator;
import somdudewillson.create_soul_harvest.client.texture.palette.MobPatternTextureGenerator.GenerationResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Encapsulates dynamic texture compositing and 3D quads for an attuned soul book tier.
 */
public class SoulBookCompositeHandler implements ItemCompositeHandler {

	public record PreparedAssets(
			BufferedImage baseImage,
			List<BakedQuad> quads
	) {}

	private final String itemPath;
	private final ResourceLocation baseTextureLocation;

	private volatile boolean loaded = false;
	private volatile PreparedAssets assets;
	private final Map<ResourceLocation, ResourceLocation> entityTextureLocations = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, DynamicTexture> entityDynamicTextures = new ConcurrentHashMap<>();

	public SoulBookCompositeHandler(String itemPath) {
		this.itemPath = itemPath;
		this.baseTextureLocation = SoulHarvestMod.id("textures/item/" + itemPath + ".png");
	}

	@Override
	public String getItemPath() {
		return itemPath;
	}

	public ResourceLocation getBaseTextureLocation() {
		return baseTextureLocation;
	}

	@Override
	public boolean isActive() {
		return assets != null;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public boolean hasDefaultDynamicTexture() {
		return false;
	}

	@Override
	public void ensureLoaded(ResourceManager resourceManager, Minecraft mc) {
		if (!this.loaded) {
			synchronized (this) {
				if (!this.loaded) {
					reload(resourceManager, mc);
				}
			}
		}
	}

	@Override
	public List<BakedQuad> getQuads() {
		PreparedAssets current = assets;
		return current != null ? current.quads() : Collections.emptyList();
	}

	@Override
	public ResourceLocation getTextureLocation(@Nullable ResourceLocation entityId) {
		if (entityId != null) {
			ResourceLocation loc = entityTextureLocations.get(entityId);
			if (loc != null) {
				return loc;
			}
		}
		return baseTextureLocation;
	}

	@Override
	public void reload(ResourceManager resourceManager, Minecraft mc) {
		this.loaded = true;
		Optional<Resource> res = resourceManager.getResource(baseTextureLocation);
		if (res.isEmpty()) {
			clearAssets();
			return;
		}

		BufferedImage base;
		try (InputStream is = res.get().open()) {
			base = ImageIO.read(is);
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to read base texture for {}: {}", itemPath, baseTextureLocation, e);
			clearAssets();
			return;
		}

		if (base == null) {
			clearAssets();
			return;
		}

		try {
			List<BakedQuad> quads = DynamicItemQuadBuilder.buildQuads(base, null);
			this.assets = new PreparedAssets(base, quads);
			SoulHarvestMod.LOGGER.info("Initialized composite soul book handler for '{}'.", itemPath);
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to prepare composite assets for soul book {}", itemPath, e);
			clearAssets();
		}
	}

	@Override
	public void uploadCompositeForEntity(ResourceLocation entityId, BufferedImage mobPatternImage, Minecraft mc) {
		PreparedAssets current = assets;
		if (current == null) {
			return;
		}

		try {
			BufferedImage composite = AttunedSoulBookCompositor.compose(current.baseImage(), mobPatternImage);
			DynamicTexture dynamicTexture = createDynamicTexture(composite);

			ResourceLocation regId = SoulHarvestMod.id(itemPath + "_" + entityId.getNamespace() + "_" + entityId.getPath());
			if (mc != null && mc.getTextureManager() != null) {
				mc.getTextureManager().register(regId, dynamicTexture);
			}

			DynamicTexture old = entityDynamicTextures.put(entityId, dynamicTexture);
			if (old != null) {
				old.close();
			}
			entityTextureLocations.put(entityId, regId);
		} catch (Throwable t) {
			SoulHarvestMod.LOGGER.error("Failed uploading composite texture for soul book {} ({})", itemPath, entityId, t);
		}
	}

	@Override
	public void dumpToDisk(Path outputDir, Map<ResourceLocation, MobPatternTextureGenerator.GenerationResult> results) throws Exception {
		PreparedAssets current = assets;
		if (current == null) {
			return;
		}

		Path itemDir = outputDir.resolve(itemPath);
		Files.createDirectories(itemDir);

		ImageIO.write(current.baseImage(), "PNG", itemDir.resolve("_default.png").toFile());

		for (Map.Entry<ResourceLocation, MobPatternTextureGenerator.GenerationResult> entry : results.entrySet()) {
			ResourceLocation entityId = entry.getKey();
			BufferedImage patternImg = entry.getValue().image();

			BufferedImage comp = AttunedSoulBookCompositor.compose(current.baseImage(), patternImg);
			String fileName = entityId.getNamespace() + "_" + entityId.getPath().replace('/', '_') + ".png";
			ImageIO.write(comp, "PNG", itemDir.resolve(fileName).toFile());
		}
	}

	@Override
	public void clear() {
		this.loaded = false;
		clearAssets();
	}

	private void clearAssets() {
		assets = null;
		for (DynamicTexture tex : entityDynamicTextures.values()) {
			try {
				tex.close();
			} catch (Throwable ignored) {}
		}
		entityDynamicTextures.clear();
		entityTextureLocations.clear();
	}

	private static DynamicTexture createDynamicTexture(BufferedImage img) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "PNG", baos);
		NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(baos.toByteArray()));
		return new DynamicTexture(nativeImage);
	}
}
