package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PotentiaTooltipHelperTest {

	private static final ResourceLocation ZOMBIE = ResourceLocation.fromNamespaceAndPath("minecraft", "zombie");

	@Test
	public void testEmptyTankTooltip() {
		PotentiaTank emptyTank = new PotentiaTank(4000L);
		List<Component> tooltip = new ArrayList<>();

		PotentiaTooltipHelper.appendTankTooltip(tooltip, emptyTank);
		assertFalse(tooltip.isEmpty(), "Tooltip should not be empty for an empty tank");
		// Expect heading + empty indicator line
		assertEquals(2, tooltip.size());
	}

	@Test
	public void testFilledUntypedTankTooltip() {
		PotentiaTank untypedTank = new PotentiaTank(4000L);
		untypedTank.fill(new SoulPotentiaStack(1500L), IPotentiaHandler.PotentiaAction.EXECUTE);
		List<Component> tooltip = new ArrayList<>();

		PotentiaTooltipHelper.appendTankTooltip(tooltip, untypedTank);
		// Expect heading + amount line + type line
		assertEquals(3, tooltip.size());

		String typeName = PotentiaTooltipHelper.getPotentiaTypeDisplayName(untypedTank.getPotentia());
		assertNotNull(typeName);
	}

	@Test
	public void testFilledTypedTankTooltip() {
		PotentiaTank typedTank = new PotentiaTank(8000L);
		typedTank.fill(new SoulPotentiaStack(2000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		List<Component> tooltip = new ArrayList<>();

		PotentiaTooltipHelper.appendTankTooltip(tooltip, typedTank);
		assertEquals(3, tooltip.size());

		String typeName = PotentiaTooltipHelper.getPotentiaTypeDisplayName(typedTank.getPotentia());
		assertNotNull(typeName);
		assertFalse(typeName.isEmpty());
	}
}
