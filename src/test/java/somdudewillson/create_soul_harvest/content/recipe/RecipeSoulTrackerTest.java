package somdudewillson.create_soul_harvest.content.recipe;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeSoulTrackerTest {

	private final ResourceLocation frogSoul = ResourceLocation.fromNamespaceAndPath("minecraft", "frog");
	private final ResourceLocation blazeSoul = ResourceLocation.fromNamespaceAndPath("minecraft", "blaze");
	private final ResourceLocation wardenSoul = ResourceLocation.fromNamespaceAndPath("minecraft", "warden");

	@BeforeEach
	@AfterEach
	public void setUp() {
		RecipeSoulTracker.clear();
	}

	@Test
	public void testInitialState() {
		assertFalse(RecipeSoulTracker.isSoulUsedInRecipes(frogSoul));
		assertFalse(RecipeSoulTracker.requiresEnhanced(frogSoul));
		assertTrue(RecipeSoulTracker.getUsedSouls().isEmpty());
	}

	@Test
	public void testRegisterNormalSoul() {
		RecipeSoulTracker.registerUsedSoul(frogSoul, false);

		assertTrue(RecipeSoulTracker.isSoulUsedInRecipes(frogSoul));
		assertFalse(RecipeSoulTracker.requiresEnhanced(frogSoul));
		assertFalse(RecipeSoulTracker.isSoulUsedInRecipes(blazeSoul));
		assertEquals(1, RecipeSoulTracker.getUsedSouls().size());
		assertTrue(RecipeSoulTracker.getUsedSouls().contains(frogSoul));
	}

	@Test
	public void testRegisterEnhancedSoul() {
		RecipeSoulTracker.registerUsedSoul(wardenSoul, true);

		assertTrue(RecipeSoulTracker.isSoulUsedInRecipes(wardenSoul));
		assertTrue(RecipeSoulTracker.requiresEnhanced(wardenSoul));
	}

	@Test
	public void testClear() {
		RecipeSoulTracker.registerUsedSoul(frogSoul, false);
		RecipeSoulTracker.registerUsedSoul(wardenSoul, true);
		assertEquals(2, RecipeSoulTracker.getUsedSouls().size());

		RecipeSoulTracker.clear();
		assertFalse(RecipeSoulTracker.isSoulUsedInRecipes(frogSoul));
		assertFalse(RecipeSoulTracker.isSoulUsedInRecipes(wardenSoul));
		assertTrue(RecipeSoulTracker.getUsedSouls().isEmpty());
	}

	@Test
	public void testGeneratedProcessingRecipesAreIgnored() {
		// Generated mob grinding and attuning recipes must be ignored (marked with _dyn_)
		assertTrue(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "splashing/wash_" + RecipeSoulTracker.GENERATED_RECIPE_MARKER + "polyclay_attuned_minecraft_zombie")));
		assertTrue(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "splashing/wash_enhanced_" + RecipeSoulTracker.GENERATED_RECIPE_MARKER + "polyclay_attuned_minecraft_zombie")));
		assertTrue(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "deploying/enhance_" + RecipeSoulTracker.GENERATED_RECIPE_MARKER + "polyclay_attuned_minecraft_zombie")));
		assertTrue(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "splashing/grind_" + RecipeSoulTracker.GENERATED_RECIPE_MARKER + "polyclay_attuned_minecraft_zombie")));
		assertTrue(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "sequenced_assembly/attune_" + RecipeSoulTracker.GENERATED_RECIPE_MARKER + "polyclay_minecraft_zombie")));
		assertTrue(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "haunting/haunt_" + RecipeSoulTracker.GENERATED_RECIPE_MARKER + "exp_drop_minecraft_zombie")));
		assertTrue(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "sequenced_assembly/loot_token_" + RecipeSoulTracker.GENERATED_RECIPE_MARKER + "minecraft_zombie")));

		// Custom crafted recipes using polyclay should not be ignored even if from create_soul_harvest
		assertFalse(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "create_mix_polyclay_ochre_froglight")));
		assertFalse(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "create_sequenced_blaze_burner")));
		assertFalse(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("create_soul_harvest", "splashing/wash_polyclay_attuned_minecraft_zombie")));
		assertFalse(RecipeSoulTracker.isIgnoredRecipe(ResourceLocation.fromNamespaceAndPath("minecraft", "bread")));
		assertFalse(RecipeSoulTracker.isIgnoredRecipe(null));
	}
}
