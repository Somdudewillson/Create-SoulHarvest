package somdudewillson.create_soul_harvest.client.texture.palette;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.client.texture.dynamic.CompositeItemBakedModel;
import somdudewillson.create_soul_harvest.client.texture.dynamic.SoulBookCompositeHandler;

import static org.junit.jupiter.api.Assertions.*;

public class SoulBookCompositeHandlerTest {

	@Test
	public void testHandlerInitialState() {
		SoulBookCompositeHandler handler = new SoulBookCompositeHandler("soul_book_simple");
		assertEquals("soul_book_simple", handler.getItemPath());
		assertFalse(handler.isLoaded());
		assertFalse(handler.isActive());
		assertFalse(handler.hasDefaultDynamicTexture(), "Soul book should not have a default dynamic texture when un-attuned");
		assertTrue(handler.getQuads().isEmpty());
		assertEquals("create_soul_harvest:textures/item/soul_book_simple.png", handler.getBaseTextureLocation().toString());
		assertEquals(handler.getBaseTextureLocation(), handler.getTextureLocation(null));
	}

	@Test
	public void testHandlerRegistrationAndQuery() {
		assertTrue(MobPatternTextureManager.isRegisteredSoulBook("soul_book_simple"));
		assertTrue(MobPatternTextureManager.isRegisteredSoulBook("soul_book_standard"));
		assertTrue(MobPatternTextureManager.isRegisteredSoulBook("soul_book_superior"));
		assertTrue(MobPatternTextureManager.isRegisteredComposite("soul_book_simple"));

		SoulBookCompositeHandler handler = MobPatternTextureManager.getSoulBookHandler("soul_book_simple");
		assertNotNull(handler);
		assertEquals("soul_book_simple", handler.getItemPath());
	}

	@Test
	public void testTargetItemPathMatchesRegisteredSoulBookEvenWhenInactive() {
		// Verify model wrapper matches soul books
		assertEquals("soul_book_simple", CompositeItemBakedModel.getTargetItemPath(SoulHarvestMod.id("soul_book_simple")));
		assertEquals("soul_book_simple", CompositeItemBakedModel.getTargetItemPath(SoulHarvestMod.id("item/soul_book_simple")));
		assertEquals("soul_book_standard", CompositeItemBakedModel.getTargetItemPath(SoulHarvestMod.id("soul_book_standard")));
		assertEquals("soul_book_superior", CompositeItemBakedModel.getTargetItemPath(SoulHarvestMod.id("soul_book_superior")));

		// Non-registered items must return null
		assertNull(CompositeItemBakedModel.getTargetItemPath(SoulHarvestMod.id("unknown_soul_book")));
	}
}
