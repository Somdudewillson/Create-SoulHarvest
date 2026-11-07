package somdudewillson.create_soul_harvest.client.texture.dynamic;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of item composite handlers for custom
 * spritesheet slicing, 3D quad generation, and dynamic compositing.
 */
public class ItemCompositeRegistry {

	private static final Map<String, ItemCompositeHandler> COMPOSITE_HANDLERS = new ConcurrentHashMap<>();

	static {
		// Register known polyclay items
		COMPOSITE_HANDLERS.put("polyclay_attuned", new PolyclayCompositeHandler("polyclay_attuned"));
		COMPOSITE_HANDLERS.put("polyclay_andesite_attuned", new PolyclayCompositeHandler("polyclay_andesite_attuned"));
		COMPOSITE_HANDLERS.put("polyclay_brass_attuned", new PolyclayCompositeHandler("polyclay_brass_attuned"));

		// Register known soul books
		COMPOSITE_HANDLERS.put("soul_book_simple", new SoulBookCompositeHandler("soul_book_simple"));
		COMPOSITE_HANDLERS.put("soul_book_standard", new SoulBookCompositeHandler("soul_book_standard"));
		COMPOSITE_HANDLERS.put("soul_book_superior", new SoulBookCompositeHandler("soul_book_superior"));
	}

	public static void registerPolyclay(String itemPath) {
		COMPOSITE_HANDLERS.putIfAbsent(itemPath, new PolyclayCompositeHandler(itemPath));
	}

	public static void registerSoulBook(String itemPath) {
		COMPOSITE_HANDLERS.putIfAbsent(itemPath, new SoulBookCompositeHandler(itemPath));
	}

	public static boolean isRegisteredComposite(String itemPath) {
		return COMPOSITE_HANDLERS.containsKey(itemPath);
	}

	public static boolean isRegisteredPolyclay(String itemPath) {
		ItemCompositeHandler handler = COMPOSITE_HANDLERS.get(itemPath);
		return handler instanceof PolyclayCompositeHandler;
	}

	public static boolean isRegisteredSoulBook(String itemPath) {
		ItemCompositeHandler handler = COMPOSITE_HANDLERS.get(itemPath);
		return handler instanceof SoulBookCompositeHandler;
	}

	public static boolean hasActiveCompositeHandler(String itemPath) {
		ItemCompositeHandler handler = COMPOSITE_HANDLERS.get(itemPath);
		return handler != null && handler.isActive();
	}

	@Nullable
	public static ItemCompositeHandler getCompositeHandler(String itemPath) {
		return COMPOSITE_HANDLERS.get(itemPath);
	}

	@Nullable
	public static PolyclayCompositeHandler getPolyclayHandler(String itemPath) {
		ItemCompositeHandler handler = COMPOSITE_HANDLERS.get(itemPath);
		return handler instanceof PolyclayCompositeHandler polyclay ? polyclay : null;
	}

	@Nullable
	public static SoulBookCompositeHandler getSoulBookHandler(String itemPath) {
		ItemCompositeHandler handler = COMPOSITE_HANDLERS.get(itemPath);
		return handler instanceof SoulBookCompositeHandler book ? book : null;
	}

	public static Collection<ItemCompositeHandler> getHandlers() {
		return COMPOSITE_HANDLERS.values();
	}
}
