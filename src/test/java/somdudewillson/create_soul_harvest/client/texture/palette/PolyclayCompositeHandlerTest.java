package somdudewillson.create_soul_harvest.client.texture.palette;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import static org.junit.jupiter.api.Assertions.*;

public class PolyclayCompositeHandlerTest {

	@Test
	public void testHandlerInitialState() {
		PolyclayCompositeHandler handler = new PolyclayCompositeHandler("polyclay_attuned");
		assertEquals("polyclay_attuned", handler.getItemPath());
		assertFalse(handler.isLoaded());
		assertFalse(handler.isActive());
		assertTrue(handler.getQuads().isEmpty());
		assertEquals("create_soul_harvest:textures/item/polyclay_attuned.png", handler.getSpritesheetLocation().toString());
	}

	@Test
	public void testHandlerRegistrationAndQuery() {
		MobPatternTextureManager.registerPolyclay("polyclay_custom");
		assertTrue(MobPatternTextureManager.isRegisteredPolyclay("polyclay_custom"));
		PolyclayCompositeHandler handler = MobPatternTextureManager.getPolyclayHandler("polyclay_custom");
		assertNotNull(handler);
		assertEquals("polyclay_custom", handler.getItemPath());
	}

	@Test
	public void testTargetItemPathMatchesRegisteredPolyclayEvenWhenInactive() {
		// Verify known polyclays are registered
		assertTrue(MobPatternTextureManager.isRegisteredPolyclay("polyclay_attuned"));
		assertTrue(MobPatternTextureManager.isRegisteredPolyclay("polyclay_andesite_attuned"));
		assertTrue(MobPatternTextureManager.isRegisteredPolyclay("polyclay_brass_attuned"));

		// Prior to resource reload, handler is inactive, but getTargetItemPath MUST still return path for model wrapping
		assertEquals("polyclay_attuned", MobPatternBakedModel.getTargetItemPath(SoulHarvestMod.id("polyclay_attuned")));
		assertEquals("polyclay_attuned", MobPatternBakedModel.getTargetItemPath(SoulHarvestMod.id("item/polyclay_attuned")));
		assertEquals("polyclay_brass_attuned", MobPatternBakedModel.getTargetItemPath(SoulHarvestMod.id("polyclay_brass_attuned")));

		// Non-registered or other namespace items must return null
		assertNull(MobPatternBakedModel.getTargetItemPath(ResourceLocation.tryParse("minecraft:diamond")));
		assertNull(MobPatternBakedModel.getTargetItemPath(SoulHarvestMod.id("unknown_item")));
	}
}
