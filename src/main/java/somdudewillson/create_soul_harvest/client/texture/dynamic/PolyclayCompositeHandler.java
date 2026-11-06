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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Encapsulates spritesheet slicing, 3D quad geometry, and dynamic composite textures
 * for a specific polyclay item type.
 */
public class PolyclayCompositeHandler implements ItemCompositeHandler {

	public record PreparedAssets(
			BufferedImage sprite0,
			BufferedImage sprite1,
			BufferedImage sprite2,
			List<BakedQuad> quads,
			ResourceLocation defaultTextureLocation
	) {}

	private final String itemPath;
	private final ResourceLocation spritesheetLocation;

	private volatile boolean loaded = false;
	private volatile PreparedAssets assets;
	private final Map<ResourceLocation, ResourceLocation> entityTextureLocations = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, DynamicTexture> entityDynamicTextures = new ConcurrentHashMap<>();
	private volatile DynamicTexture defaultDynamicTexture;

	public PolyclayCompositeHandler(String itemPath) {
		this.itemPath = itemPath;
		this.spritesheetLocation = SoulHarvestMod.id("textures/item/" + itemPath + ".png");
	}

	public String getItemPath() {
		return itemPath;
	}

	public ResourceLocation getSpritesheetLocation() {
		return spritesheetLocation;
	}

	public boolean isActive() {
		return assets != null;
	}

	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public boolean hasDefaultDynamicTexture() {
		return true;
	}

	public void ensureLoaded(ResourceManager resourceManager, Minecraft mc) {
		if (!this.loaded) {
			synchronized (this) {
				if (!this.loaded) {
					reload(resourceManager, mc);
				}
			}
		}
	}

	public List<BakedQuad> getQuads() {
		PreparedAssets current = assets;
		return current != null ? current.quads() : Collections.emptyList();
	}

	public ResourceLocation getTextureLocation(@Nullable ResourceLocation entityId) {
		if (entityId != null) {
			ResourceLocation loc = entityTextureLocations.get(entityId);
			if (loc != null) {
				return loc;
			}
		}
		PreparedAssets current = assets;
		if (current != null && current.defaultTextureLocation() != null) {
			return current.defaultTextureLocation();
		}
		return spritesheetLocation;
	}

	/**
	 * Reloads the spritesheet from the resource manager. If the texture is a 3-part horizontal spritesheet
	 * (width >= 48, height >= 16), slices the sprites, precomputes 3D quads from Sprite 0,
	 * and registers the default composite texture.
	 */
	public void reload(ResourceManager resourceManager, Minecraft mc) {
		this.loaded = true;
		Optional<Resource> res = resourceManager.getResource(spritesheetLocation);
		if (res.isEmpty()) {
			clearAssets();
			return;
		}

		BufferedImage sheet;
		try (InputStream is = res.get().open()) {
			sheet = ImageIO.read(is);
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to read spritesheet for {}: {}", itemPath, spritesheetLocation, e);
			clearAssets();
			return;
		}

		if (sheet == null || sheet.getWidth() < AttunedPolyclayCompositor.SHEET_WIDTH || sheet.getHeight() < AttunedPolyclayCompositor.SHEET_HEIGHT) {
			// Not a 3-part spritesheet (e.g. still a standard 16x16 texture). Remain inactive.
			clearAssets();
			return;
		}

		try {
			AttunedPolyclayCompositor.Slices slices = AttunedPolyclayCompositor.sliceSpritesheet(sheet);
			List<BakedQuad> quads = DynamicItemQuadBuilder.buildQuads(slices.sprite0(), null);

			BufferedImage defaultComp = AttunedPolyclayCompositor.compose(
					slices.sprite0(),
					null,
					slices.sprite1(),
					slices.sprite2()
			);

			ResourceLocation defaultLoc = uploadDefaultTexture(defaultComp, mc);
			this.assets = new PreparedAssets(slices.sprite0(), slices.sprite1(), slices.sprite2(), quads, defaultLoc);
			SoulHarvestMod.LOGGER.info("Initialized composite polyclay handler for '{}' with 3-part spritesheet.", itemPath);
		} catch (Exception e) {
			SoulHarvestMod.LOGGER.error("Failed to prepare composite assets for {}", itemPath, e);
			clearAssets();
		}
	}

	private ResourceLocation uploadDefaultTexture(BufferedImage img, Minecraft mc) {
		ResourceLocation regId = SoulHarvestMod.id(itemPath + "_default");
		Runnable task = () -> {
			try {
				DynamicTexture dynamicTexture = createDynamicTexture(img);
				if (mc != null && mc.getTextureManager() != null) {
					mc.getTextureManager().register(regId, dynamicTexture);
				}
				if (defaultDynamicTexture != null) {
					defaultDynamicTexture.close();
				}
				defaultDynamicTexture = dynamicTexture;
			} catch (Throwable t) {
				SoulHarvestMod.LOGGER.error("Failed uploading default dynamic texture for {}", itemPath, t);
			}
		};

		if (mc != null) {
			if (mc.isSameThread()) {
				task.run();
			} else {
				mc.execute(task);
			}
		}
		return regId;
	}

	/**
	 * Composites the item's spritesheet with an entity's mob pattern texture and registers the DynamicTexture.
	 */
	public void uploadCompositeForEntity(ResourceLocation entityId, BufferedImage mobPatternImage, Minecraft mc) {
		PreparedAssets current = assets;
		if (current == null) {
			return;
		}

		try {
			BufferedImage composite = AttunedPolyclayCompositor.compose(
					current.sprite0(),
					mobPatternImage,
					current.sprite1(),
					current.sprite2()
			);

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
			SoulHarvestMod.LOGGER.error("Failed uploading composite texture for {} ({})", itemPath, entityId, t);
		}
	}

	/**
	 * Exports the default composite and all generated mob composites for this polyclay to disk.
	 */
	public void dumpToDisk(Path outputDir, Map<ResourceLocation, MobPatternTextureGenerator.GenerationResult> results) throws Exception {
		PreparedAssets current = assets;
		if (current == null) {
			return;
		}

		Path itemDir = outputDir.resolve(itemPath);
		Files.createDirectories(itemDir);

		BufferedImage defaultComp = AttunedPolyclayCompositor.compose(
				current.sprite0(),
				null,
				current.sprite1(),
				current.sprite2()
		);
		ImageIO.write(defaultComp, "PNG", itemDir.resolve("_default.png").toFile());

		for (Map.Entry<ResourceLocation, MobPatternTextureGenerator.GenerationResult> entry : results.entrySet()) {
			ResourceLocation entityId = entry.getKey();
			BufferedImage patternImg = entry.getValue().image();

			BufferedImage comp = AttunedPolyclayCompositor.compose(
					current.sprite0(),
					patternImg,
					current.sprite1(),
					current.sprite2()
			);
			String fileName = entityId.getNamespace() + "_" + entityId.getPath().replace('/', '_') + ".png";
			ImageIO.write(comp, "PNG", itemDir.resolve(fileName).toFile());
		}
	}

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
		if (defaultDynamicTexture != null) {
			try {
				defaultDynamicTexture.close();
			} catch (Throwable ignored) {}
			defaultDynamicTexture = null;
		}
	}

	private static DynamicTexture createDynamicTexture(BufferedImage img) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "PNG", baos);
		NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(baos.toByteArray()));
		return new DynamicTexture(nativeImage);
	}
}
