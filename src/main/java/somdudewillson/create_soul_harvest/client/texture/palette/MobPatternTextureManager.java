package somdudewillson.create_soul_harvest.client.texture.palette;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.client.texture.dynamic.ItemCompositeHandler;
import somdudewillson.create_soul_harvest.client.texture.dynamic.PolyclayCompositeHandler;
import somdudewillson.create_soul_harvest.client.texture.dynamic.SoulBookCompositeHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages procedural representative pattern textures for mobs and custom composited
 * soul items.
 */
public class MobPatternTextureManager implements ResourceManagerReloadListener {

	public static final MobPatternTextureManager INSTANCE = new MobPatternTextureManager();

	public record EntityTextureTarget(
			ResourceLocation entityId,
			ResourceLocation mainTexture,
			List<ResourceLocation> extraTextures
	) {}

	public record ExportItem(
			ResourceLocation entityId,
			String fileName,
			MobPalette palette,
			List<PaletteColor> arrangedCommon,
			List<PaletteColor> arrangedRare,
			MobPatternTextureGenerator.AppliedPattern appliedPattern
	) {}

	private final Map<ResourceLocation, BufferedImage> patternMasks = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, MobPalette> mobPalettes = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, BufferedImage> generatedImages = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, MobPatternTextureGenerator.GenerationResult> generationResults = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, ResourceLocation> dynamicTextureLocations = new ConcurrentHashMap<>();
	private final Map<ResourceLocation, DynamicTexture> dynamicTextures = new ConcurrentHashMap<>();

	// Registry of item composite handlers
	private final Map<String, ItemCompositeHandler> compositeHandlers = new ConcurrentHashMap<>();

	private volatile boolean needsGeneration = false;

	private MobPatternTextureManager() {
		// Register known polyclay items
		this.compositeHandlers.put("polyclay_attuned", new PolyclayCompositeHandler("polyclay_attuned"));
		this.compositeHandlers.put("polyclay_andesite_attuned", new PolyclayCompositeHandler("polyclay_andesite_attuned"));
		this.compositeHandlers.put("polyclay_brass_attuned", new PolyclayCompositeHandler("polyclay_brass_attuned"));

		// Register known soul books
		this.compositeHandlers.put("soul_book_simple", new SoulBookCompositeHandler("soul_book_simple"));
		this.compositeHandlers.put("soul_book_standard", new SoulBookCompositeHandler("soul_book_standard"));
		this.compositeHandlers.put("soul_book_superior", new SoulBookCompositeHandler("soul_book_superior"));
	}

	public static void registerPolyclay(String itemPath) {
		INSTANCE.compositeHandlers.putIfAbsent(itemPath, new PolyclayCompositeHandler(itemPath));
	}

	public static void registerSoulBook(String itemPath) {
		INSTANCE.compositeHandlers.putIfAbsent(itemPath, new SoulBookCompositeHandler(itemPath));
	}

	public static boolean isRegisteredComposite(String itemPath) {
		return INSTANCE.compositeHandlers.containsKey(itemPath);
	}

	public static boolean isRegisteredPolyclay(String itemPath) {
		ItemCompositeHandler handler = INSTANCE.compositeHandlers.get(itemPath);
		return handler instanceof PolyclayCompositeHandler;
	}

	public static boolean isRegisteredSoulBook(String itemPath) {
		ItemCompositeHandler handler = INSTANCE.compositeHandlers.get(itemPath);
		return handler instanceof SoulBookCompositeHandler;
	}

	public static boolean hasActiveCompositeHandler(String itemPath) {
		ItemCompositeHandler handler = INSTANCE.compositeHandlers.get(itemPath);
		return handler != null && handler.isActive();
	}

	@Nullable
	public static ItemCompositeHandler getCompositeHandler(String itemPath) {
		return INSTANCE.compositeHandlers.get(itemPath);
	}

	@Nullable
	public static PolyclayCompositeHandler getPolyclayHandler(String itemPath) {
		ItemCompositeHandler handler = INSTANCE.compositeHandlers.get(itemPath);
		return handler instanceof PolyclayCompositeHandler polyclay ? polyclay : null;
	}

	@Nullable
	public static SoulBookCompositeHandler getSoulBookHandler(String itemPath) {
		ItemCompositeHandler handler = INSTANCE.compositeHandlers.get(itemPath);
		return handler instanceof SoulBookCompositeHandler book ? book : null;
	}

	public static ResourceLocation getCompositeTextureLocation(String itemPath, @Nullable ResourceLocation entityId) {
		ItemCompositeHandler handler = INSTANCE.compositeHandlers.get(itemPath);
		if (handler != null) {
			return handler.getTextureLocation(entityId);
		}
		return SoulHarvestMod.id("textures/item/" + itemPath + ".png");
	}

	public static List<BakedQuad> getCompositeQuads(String itemPath) {
		ItemCompositeHandler handler = INSTANCE.compositeHandlers.get(itemPath);
		return handler != null ? handler.getQuads() : Collections.emptyList();
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		patternMasks.clear();
		Map<ResourceLocation, Resource> maskResources = resourceManager.listResources(
				"textures/mob_tex_patterns",
				loc -> loc.getPath().endsWith(".png")
		);

		for (Map.Entry<ResourceLocation, Resource> entry : maskResources.entrySet()) {
			ResourceLocation loc = entry.getKey();
			try (InputStream is = entry.getValue().open()) {
				BufferedImage img = ImageIO.read(is);
				if (img != null) {
					patternMasks.put(loc, img);
				}
			} catch (Exception e) {
				SoulHarvestMod.LOGGER.error("Failed to load mob pattern mask: {}", loc, e);
			}
		}

		SoulHarvestMod.LOGGER.info("Loaded {} mob pattern masks from resource packs.", patternMasks.size());

		// Reload all registered composite handlers
		Minecraft mc = Minecraft.getInstance();
		for (ItemCompositeHandler handler : compositeHandlers.values()) {
			handler.reload(resourceManager, mc);
		}

		if (mc != null && mc.level != null) {
			generateTexturesAsync(null, null);
		} else {
			needsGeneration = true;
		}
	}

	public static boolean needsGeneration() {
		return INSTANCE.needsGeneration;
	}

	public static void checkOnWorldJoin() {
		if (INSTANCE.needsGeneration) {
			INSTANCE.generateTexturesAsync(null, null);
		}
	}

	public static List<MobPatternTextureGenerator.NamedMask> getPatternMaskList() {
		List<MobPatternTextureGenerator.NamedMask> list = new ArrayList<>(INSTANCE.patternMasks.size());
		for (Map.Entry<ResourceLocation, BufferedImage> entry : INSTANCE.patternMasks.entrySet()) {
			String name = entry.getKey().getPath();
			int lastSlash = name.lastIndexOf('/');
			if (lastSlash >= 0) {
				name = name.substring(lastSlash + 1);
			}
			list.add(new MobPatternTextureGenerator.NamedMask(name, entry.getValue()));
		}
		// Sort by name for deterministic order across platforms
		list.sort(Comparator.comparing(MobPatternTextureGenerator.NamedMask::name));
		return Collections.unmodifiableList(list);
	}

	public static ResourceLocation getTextureLocation(ResourceLocation entityId) {
		return INSTANCE.dynamicTextureLocations.get(entityId);
	}

	public static BufferedImage getPatternImage(ResourceLocation entityId) {
		return INSTANCE.generatedImages.get(entityId);
	}

	public static MobPalette getPalette(ResourceLocation entityId) {
		return INSTANCE.mobPalettes.get(entityId);
	}

	/**
	 * Asynchronously extracts palettes and generates textures for target entities.
	 *
	 * @param targetEntityId If non-null, only generates for this entity; otherwise generates for all living entities.
	 * @param onComplete     Optional callback invoked when generation and registration complete.
	 */
	public CompletableFuture<Void> generateTexturesAsync(ResourceLocation targetEntityId, Runnable onComplete) {
		needsGeneration = false;
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			return CompletableFuture.completedFuture(null);
		}

		CompletableFuture<Void> future = new CompletableFuture<>();

		mc.execute(() -> {
			if (mc.level == null) {
				needsGeneration = true;
				future.complete(null);
				return;
			}

			List<EntityTextureTarget> targets = discoverTargets(mc, targetEntityId);
			if (targets.isEmpty()) {
				future.complete(null);
				if (onComplete != null) onComplete.run();
				return;
			}

			List<MobPatternTextureGenerator.NamedMask> masks = getPatternMaskList();

			CompletableFuture.supplyAsync(() -> processTargets(targets, mc, masks))
					.thenAcceptAsync(results -> {
						uploadDynamicTextures(results, mc);
						future.complete(null);
						if (onComplete != null) onComplete.run();
					}, mc);
		});

		return future;
	}

	public static List<EntityTextureTarget> discoverTargets(Minecraft mc, ResourceLocation targetEntityId) {
		List<EntityTextureTarget> targets = new ArrayList<>();
		if (mc.level == null) return targets;

		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
			if (entityId == null) continue;

			if (targetEntityId != null && !targetEntityId.equals(entityId)) {
				continue;
			}

			boolean isLiving = false;
			try {
				@SuppressWarnings("unchecked")
				EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) entityType;
				isLiving = DefaultAttributes.hasSupplier(livingType);
			} catch (Throwable ignored) {}

			if (!isLiving && entityType.getCategory() != MobCategory.MISC) {
				isLiving = true;
			}
			if (!isLiving && targetEntityId != null) {
				isLiving = true;
			}

			if (!isLiving) continue;

			try {
				Entity entity = entityType.create(mc.level);
				if (entity == null) continue;

				if (!(entity instanceof LivingEntity) && targetEntityId == null) {
					continue;
				}

				EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(entity);
				if (renderer == null) continue;

				@SuppressWarnings("unchecked")
				EntityRenderer<Entity> typedRenderer = (EntityRenderer<Entity>) renderer;
				ResourceLocation textureLocation = typedRenderer.getTextureLocation(entity);
				if (textureLocation == null) continue;

				List<ResourceLocation> extraTextures = new ArrayList<>();
				if (entityId.getPath().contains("enderman")) {
					extraTextures.add(SoulHarvestMod.id("minecraft", "textures/entity/enderman/enderman_eyes.png"));
				} else if (entityId.getPath().contains("spider")) {
					extraTextures.add(SoulHarvestMod.id("minecraft", "textures/entity/spider/spider_eyes.png"));
				}

				targets.add(new EntityTextureTarget(entityId, textureLocation, extraTextures));
			} catch (Throwable t) {
				SoulHarvestMod.LOGGER.debug("Could not resolve texture for entity type {}", entityId, t);
			}
		}

		return targets;
	}

	private static Map<ResourceLocation, MobPatternTextureGenerator.GenerationResult> processTargets(
			List<EntityTextureTarget> targets,
			Minecraft mc,
			List<MobPatternTextureGenerator.NamedMask> masks
	) {
		MobPaletteExtractor extractor = new MobPaletteExtractor();
		MobPatternTextureGenerator generator = new MobPatternTextureGenerator();
		Map<ResourceLocation, MobPatternTextureGenerator.GenerationResult> results = new HashMap<>();

		for (EntityTextureTarget target : targets) {
			try {
				List<BufferedImage> images = new ArrayList<>();
				Optional<Resource> mainRes = mc.getResourceManager().getResource(target.mainTexture());
				if (mainRes.isPresent()) {
					try (InputStream is = mainRes.get().open()) {
						BufferedImage img = ImageIO.read(is);
						if (img != null) images.add(img);
					}
				}

				for (ResourceLocation extraLoc : target.extraTextures()) {
					Optional<Resource> extraRes = mc.getResourceManager().getResource(extraLoc);
					if (extraRes.isPresent()) {
						try (InputStream is = extraRes.get().open()) {
							BufferedImage img = ImageIO.read(is);
							if (img != null) images.add(img);
						}
					}
				}

				if (images.isEmpty()) continue;

				Optional<MobPalette> paletteOpt = extractor.tryExtractPalette(images);
				if (paletteOpt.isEmpty()) continue;

				MobPalette palette = paletteOpt.get();
				INSTANCE.mobPalettes.put(target.entityId(), palette);

				long seed = target.entityId().toString().hashCode();
				MobPatternTextureGenerator.GenerationResult genResult = generator.generateTextureWithDetails(palette, seed, masks);

				results.put(target.entityId(), genResult);
				INSTANCE.generatedImages.put(target.entityId(), genResult.image());
				INSTANCE.generationResults.put(target.entityId(), genResult);

			} catch (Throwable t) {
				SoulHarvestMod.LOGGER.debug("Failed processing pattern texture for entity {}", target.entityId(), t);
			}
		}

		return results;
	}

	private static void uploadDynamicTextures(
			Map<ResourceLocation, MobPatternTextureGenerator.GenerationResult> results,
			Minecraft mc
	) {
		for (Map.Entry<ResourceLocation, MobPatternTextureGenerator.GenerationResult> entry : results.entrySet()) {
			ResourceLocation entityId = entry.getKey();
			BufferedImage img = entry.getValue().image();

			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img, "PNG", baos);
				NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(baos.toByteArray()));
				DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);

				ResourceLocation regId = SoulHarvestMod.id("mob_pattern_" + entityId.getNamespace() + "_" + entityId.getPath());
				ResourceLocation registeredLocation = mc.getTextureManager().register(regId.getPath(), dynamicTexture);

				DynamicTexture old = INSTANCE.dynamicTextures.put(entityId, dynamicTexture);
				if (old != null) {
					old.close();
				}
				INSTANCE.dynamicTextureLocations.put(entityId, registeredLocation != null ? registeredLocation : regId);

				// Delegate composite uploading to all active composite handlers
				for (ItemCompositeHandler handler : INSTANCE.compositeHandlers.values()) {
					if (handler.isActive()) {
						handler.uploadCompositeForEntity(entityId, img, mc);
					}
				}
			} catch (Throwable t) {
				SoulHarvestMod.LOGGER.error("Failed uploading dynamic texture for {}", entityId, t);
			}
		}
		SoulHarvestMod.LOGGER.info("Registered {} mob pattern dynamic textures and updated dynamic composites.", results.size());
	}

	/**
	 * Dumps pattern textures and composite items to the specified output directory.
	 */
	public static int dumpTexturesToDisk(Path outputDir, ResourceLocation targetEntityId) throws Exception {
		Files.createDirectories(outputDir);
		List<ExportItem> items = new ArrayList<>();

		// Dump each active composite item to its own subfolder
		for (ItemCompositeHandler handler : INSTANCE.compositeHandlers.values()) {
			if (handler.isActive()) {
				handler.dumpToDisk(outputDir, INSTANCE.generationResults);
			}
		}

		for (Map.Entry<ResourceLocation, MobPatternTextureGenerator.GenerationResult> entry : INSTANCE.generationResults.entrySet()) {
			ResourceLocation entityId = entry.getKey();
			if (targetEntityId != null && !targetEntityId.equals(entityId)) {
				continue;
			}

			MobPatternTextureGenerator.GenerationResult genResult = entry.getValue();
			MobPalette palette = INSTANCE.mobPalettes.get(entityId);
			if (palette == null) continue;

			String fileName = entityId.getNamespace() + "_" + entityId.getPath().replace('/', '_') + ".png";
			Path filePath = outputDir.resolve(fileName);
			ImageIO.write(genResult.image(), "PNG", filePath.toFile());

			items.add(new ExportItem(
					entityId,
					fileName,
					palette,
					genResult.arrangedCommon(),
					genResult.arrangedRare(),
					genResult.appliedPattern()
			));
		}

		items.sort(Comparator.comparing(i -> i.entityId().toString()));
		generateHtmlCatalog(outputDir, items);
		return items.size();
	}

	private static void generateHtmlCatalog(Path outputDir, List<ExportItem> items) {
		Path htmlPath = outputDir.resolve("index.html");
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(htmlPath))) {
			writer.println("<!DOCTYPE html>");
			writer.println("<html lang=\"en\">");
			writer.println("<head>");
			writer.println("<meta charset=\"UTF-8\">");
			writer.println("<title>Mob Representative Textures Catalog</title>");
			writer.println("<style>");
			writer.println("  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: #18191c; color: #dcddde; margin: 0; padding: 24px; }");
			writer.println("  h1 { color: #5865f2; margin-bottom: 4px; }");
			writer.println("  p.sub { color: #8e9297; margin-top: 0; margin-bottom: 24px; }");
			writer.println("  .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }");
			writer.println("  .card { background: #2f3136; border-radius: 8px; padding: 16px; display: flex; flex-direction: column; align-items: center; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }");
			writer.println("  .card-title { font-weight: bold; margin-bottom: 12px; color: #ffffff; text-align: center; word-break: break-all; }");
			writer.println("  .img-wrap { background: #202225; padding: 12px; border-radius: 6px; margin-bottom: 14px; display: flex; justify-content: center; align-items: center; }");
			writer.println("  .texture-img { width: 128px; height: 128px; image-rendering: pixelated; image-rendering: -moz-crisp-edges; border: 1px solid #40444b; }");
			writer.println("  .section-label { font-size: 11px; text-transform: uppercase; letter-spacing: 0.5px; color: #b9bbbe; width: 100%; margin-top: 8px; margin-bottom: 4px; }");
			writer.println("  .swatches { display: flex; flex-wrap: wrap; gap: 6px; width: 100%; margin-bottom: 6px; }");
			writer.println("  .swatch { width: 24px; height: 24px; border-radius: 4px; border: 1px solid rgba(255,255,255,0.2); position: relative; }");
			writer.println("  .badge { font-size: 11px; padding: 2px 6px; border-radius: 4px; background: #40444b; color: #ffffff; margin-right: 4px; margin-bottom: 4px; display: inline-block; }");
			writer.println("</style>");
			writer.println("</head>");
			writer.println("<body>");
			writer.println("  <h1>Mob Representative Textures</h1>");
			writer.println("  <p class=\"sub\">Procedurally generated palette, dithered gradient, and pattern overlays for " + items.size() + " mobs.</p>");
			writer.println("  <div class=\"grid\">");

			for (ExportItem item : items) {
				writer.println("    <div class=\"card\">");
				writer.println("      <div class=\"card-title\">" + item.entityId() + "</div>");
				writer.println("      <div class=\"img-wrap\"><img class=\"texture-img\" src=\"" + item.fileName() + "\" alt=\"" + item.entityId() + "\"></div>");

				// Common colors
				writer.println("      <div class=\"section-label\">Common Colors (Gradient: Top &rarr; Bottom)</div>");
				writer.println("      <div class=\"swatches\">");
				for (int i = 0; i < item.arrangedCommon().size(); i++) {
					PaletteColor c = item.arrangedCommon().get(i);
					double pct = ((double) c.getCount() / item.palette().totalPixels()) * 100.0;
					String label = (i == 0) ? "Top" : ((i == item.arrangedCommon().size() - 1) ? "Bottom" : "Mid");
					writer.printf("        <div class=\"swatch\" style=\"background: %s;\" title=\"%s: %s (%.1f%%, mergedFrom=%d)\"></div>%n",
							c.toHex(), label, c.toHex(), pct, c.getMergedFrom());
				}
				writer.println("      </div>");

				// Rare colors
				writer.println("      <div class=\"section-label\">Rare Colors & Pattern</div>");
				if (item.arrangedRare().isEmpty()) {
					writer.println("      <span style=\"color: #72767d; font-size: 12px; width: 100%;\">None (all colors common)</span>");
				} else {
					writer.println("      <div style=\"width: 100%;\">");
					if (item.appliedPattern() != null) {
						MobPatternTextureGenerator.AppliedPattern ap = item.appliedPattern();
						String flipInfo = (ap.flipX() && ap.flipY()) ? " (flip XY)" : (ap.flipX() ? " (flip X)" : (ap.flipY() ? " (flip Y)" : ""));
						writer.printf("        <div style=\"margin-bottom: 6px;\"><span class=\"badge\" style=\"background: #5865f2;\">Pattern: %s%s</span></div>%n",
								ap.maskName(), flipInfo);
					}
					writer.println("        <div class=\"swatches\">");
					for (int i = 0; i < item.arrangedRare().size(); i++) {
						PaletteColor c = item.arrangedRare().get(i);
						double pct = ((double) c.getCount() / item.palette().totalPixels()) * 100.0;
						String label = (i == 0) ? "Top" : ((i == item.arrangedRare().size() - 1) ? "Bottom" : "Mid");
						writer.printf("          <div class=\"swatch\" style=\"background: %s;\" title=\"%s: %s (%.1f%%, mergedFrom=%d)\"></div>%n",
								c.toHex(), label, c.toHex(), pct, c.getMergedFrom());
					}
					writer.println("        </div>");
					writer.println("      </div>");
				}

				writer.println("    </div>");
			}

			writer.println("  </div>");
			writer.println("</body>");
			writer.println("</html>");
		} catch (Throwable t) {
			SoulHarvestMod.LOGGER.error("Failed to write catalog HTML", t);
		}
	}
}
