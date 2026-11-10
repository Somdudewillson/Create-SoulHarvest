package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import somdudewillson.create_soul_harvest.config.SoulHarvestConfig;
import somdudewillson.create_soul_harvest.content.capability.ISoulBudGrowthHandler;
import somdudewillson.create_soul_harvest.content.potentia.client.PotentiaColorHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PotentiaGrowthCatalystTankTest {

	private static final ResourceLocation ZOMBIE = ResourceLocation.fromNamespaceAndPath("minecraft", "zombie");
	private static final ResourceLocation SKELETON = ResourceLocation.fromNamespaceAndPath("minecraft", "skeleton");

	/**
	 * Standalone handler that mimics the PotentiaGrowthCatalystBlockEntity potentia handler and growth handler
	 * logic to test capacity, type conversion, threshold, and growth consumption in unit test environment.
	 */
	static class TestCatalystHandler implements IPotentiaHandler, ISoulBudGrowthHandler {
		private SoulPotentiaStack buffer = SoulPotentiaStack.EMPTY;
		private Direction facing = Direction.UP;

		public void setFacing(Direction facing) {
			this.facing = facing;
		}

		public SoulPotentiaStack getBuffer() {
			return buffer;
		}

		public boolean hasEnoughPotentia() {
			return buffer.getAmount() >= SoulHarvestConfig.getCatalystThreshold();
		}

		public int getCrystalColor() {
			int primary = PotentiaColorHelper.getColors(buffer).primary();
			if (!hasEnoughPotentia()) {
				int r = (int) (((primary >> 16) & 0xFF) * 0.35f);
				int g = (int) (((primary >> 8) & 0xFF) * 0.35f);
				int b = (int) ((primary & 0xFF) * 0.35f);
				return (r << 16) | (g << 8) | b;
			}
			return primary;
		}

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public SoulPotentiaStack getPotentiaInTank(int tank) {
			return buffer.copy();
		}

		@Override
		public long getTankCapacity(int tank) {
			return SoulHarvestConfig.getCatalystCapacity();
		}

		@Override
		public boolean isPotentiaValid(int tank, SoulPotentiaStack stack) {
			return stack != null && !stack.isEmpty();
		}

		@Override
		public long fill(SoulPotentiaStack resource, PotentiaAction action) {
			if (resource == null || resource.isEmpty()) return 0L;
			long capacity = getTankCapacity(0);
			long currentAmount = buffer.getAmount();
			boolean isMismatch = !buffer.isEmpty() && !SoulPotentiaStack.isSameType(buffer, resource);

			long availableSpace = Math.max(0, capacity - currentAmount);
			long toAccept = Math.min(availableSpace, resource.getAmount());

			if (isMismatch && availableSpace <= 0 && resource.getAmount() > 0) {
				toAccept = 1L;
			}

			if (action.execute()) {
				if (isMismatch) {
					buffer.setEntityType(resource.getEntityType());
					if (toAccept > 0) {
						if (availableSpace <= 0) {
							buffer.setAmount(capacity);
						} else {
							buffer.grow(toAccept);
						}
					}
				} else if (buffer.isEmpty()) {
					buffer = new SoulPotentiaStack(toAccept, resource.getEntityType());
				} else {
					buffer.grow(toAccept);
				}
			}
			return toAccept;
		}

		@Override
		public SoulPotentiaStack drain(SoulPotentiaStack resource, PotentiaAction action) {
			return SoulPotentiaStack.EMPTY;
		}

		@Override
		public SoulPotentiaStack drain(long maxDrain, PotentiaAction action) {
			return SoulPotentiaStack.EMPTY;
		}

		@Override
		public boolean canAccept(Direction side) {
			return side == null || side == facing;
		}

		@Override
		public boolean canPush(Direction side) {
			return false;
		}

		@Override
		public boolean canGrowBuds(Direction side) {
			if (side != null && side == facing) return false;
			return hasEnoughPotentia();
		}

		@Override
		public boolean onBudGrow(Direction side) {
			if (side != null && side == facing) return false;
			if (!hasEnoughPotentia()) return false;
			long cost = SoulHarvestConfig.getCatalystGrowthConsumption();
			if (buffer.getAmount() >= cost) {
				buffer.shrink(cost);
				return true;
			}
			return false;
		}
	}

	@Test
	public void testDefaultCapacitiesAndThresholds() {
		assertEquals(4700L, SoulHarvestConfig.getCatalystGrowthConsumption());
		assertEquals(11750L, SoulHarvestConfig.getCatalystCapacity());
		assertEquals(2350L, SoulHarvestConfig.getCatalystThreshold());
	}

	@Test
	public void testNormalFill() {
		TestCatalystHandler catalyst = new TestCatalystHandler();
		assertTrue(catalyst.getBuffer().isEmpty());

		// Fill 1000 Zombie potentia
		long filled = catalyst.fill(new SoulPotentiaStack(1000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(1000L, filled);
		assertEquals(1000L, catalyst.getBuffer().getAmount());
		assertEquals(ZOMBIE, catalyst.getBuffer().getEntityType());

		// Fill up to capacity
		long filledMore = catalyst.fill(new SoulPotentiaStack(20000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(10750L, filledMore); // 11750 - 1000 = 10750
		assertEquals(11750L, catalyst.getBuffer().getAmount());
	}

	@Test
	public void testTypeConversionOnMismatch() {
		TestCatalystHandler catalyst = new TestCatalystHandler();

		// Fill with 3000 Zombie potentia
		catalyst.fill(new SoulPotentiaStack(3000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(3000L, catalyst.getBuffer().getAmount());
		assertEquals(ZOMBIE, catalyst.getBuffer().getEntityType());

		// Push 500 Skeleton potentia -> converts Zombie to Skeleton and adds 500
		long accepted = catalyst.fill(new SoulPotentiaStack(500L, SKELETON), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(500L, accepted);
		assertEquals(3500L, catalyst.getBuffer().getAmount());
		assertEquals(SKELETON, catalyst.getBuffer().getEntityType());
	}

	@Test
	public void testTypeConversionWhenFull() {
		TestCatalystHandler catalyst = new TestCatalystHandler();

		// Fill completely with Zombie potentia
		catalyst.fill(new SoulPotentiaStack(11750L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(11750L, catalyst.getBuffer().getAmount());
		assertEquals(ZOMBIE, catalyst.getBuffer().getEntityType());

		// Push 100 Skeleton potentia into full tank -> converts buffer type to Skeleton!
		long accepted = catalyst.fill(new SoulPotentiaStack(100L, SKELETON), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(1L, accepted);
		assertEquals(11750L, catalyst.getBuffer().getAmount());
		assertEquals(SKELETON, catalyst.getBuffer().getEntityType());
	}

	@Test
	public void testSimulationDoesNotMutate() {
		TestCatalystHandler catalyst = new TestCatalystHandler();
		catalyst.fill(new SoulPotentiaStack(2000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);

		long sim = catalyst.fill(new SoulPotentiaStack(1000L, SKELETON), IPotentiaHandler.PotentiaAction.SIMULATE);
		assertEquals(1000L, sim);
		// Ensure state untouched
		assertEquals(2000L, catalyst.getBuffer().getAmount());
		assertEquals(ZOMBIE, catalyst.getBuffer().getEntityType());
	}

	@Test
	public void testThresholdAndBudGrowthConsumption() {
		TestCatalystHandler catalyst = new TestCatalystHandler();

		// Buffer at 2000 (< 2350 threshold) -> cannot grow
		catalyst.fill(new SoulPotentiaStack(2000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertFalse(catalyst.hasEnoughPotentia());
		assertFalse(catalyst.canGrowBuds(Direction.NORTH));
		assertFalse(catalyst.onBudGrow(Direction.NORTH));

		// Buffer at 5000 (>= 2350 threshold) -> can grow
		catalyst.fill(new SoulPotentiaStack(3000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(5000L, catalyst.getBuffer().getAmount());
		assertTrue(catalyst.hasEnoughPotentia());
		assertTrue(catalyst.canGrowBuds(Direction.NORTH));

		// Bud on input face cannot grow
		assertFalse(catalyst.canGrowBuds(Direction.UP));
		assertFalse(catalyst.onBudGrow(Direction.UP));

		// Bud on valid side grows -> consumes 4700
		boolean grew = catalyst.onBudGrow(Direction.NORTH);
		assertTrue(grew);
		assertEquals(300L, catalyst.getBuffer().getAmount()); // 5000 - 4700 = 300
		assertFalse(catalyst.hasEnoughPotentia()); // 300 < 2350
	}

	@Test
	public void testColorDimming() {
		TestCatalystHandler catalyst = new TestCatalystHandler();

		// Empty -> dimmed DEFAULT_PRIMARY
		int emptyColor = catalyst.getCrystalColor();
		int defaultPrimary = PotentiaColorHelper.DEFAULT_PRIMARY;
		assertNotEquals(defaultPrimary, emptyColor);

		// Powered with Zombie -> bright color
		catalyst.fill(new SoulPotentiaStack(5000L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		int brightColor = catalyst.getCrystalColor();
		assertEquals(PotentiaColorHelper.getColors(ZOMBIE).primary(), brightColor);
	}

	@Test
	public void testGoggleTooltipDisplay() {
		List<Component> emptyTooltip = new ArrayList<>();
		PotentiaTooltipHelper.addToGoggles(
				PotentiaTooltipHelper.langBuilder().translate("gui.goggles.potentia_growth_catalyst"),
				emptyTooltip, 0
		);
		PotentiaTooltipHelper.appendTankTooltip(emptyTooltip, SoulPotentiaStack.EMPTY, SoulHarvestConfig.getCatalystCapacity());
		// Expect catalyst header + contents heading + empty capacity line
		assertEquals(3, emptyTooltip.size());

		List<Component> filledTooltip = new ArrayList<>();
		PotentiaTooltipHelper.addToGoggles(
				PotentiaTooltipHelper.langBuilder().translate("gui.goggles.potentia_growth_catalyst"),
				filledTooltip, 0
		);
		PotentiaTooltipHelper.appendTankTooltip(filledTooltip, new SoulPotentiaStack(5000L, ZOMBIE), SoulHarvestConfig.getCatalystCapacity());
		// Expect catalyst header + contents heading + amount/capacity line + type line
		assertEquals(4, filledTooltip.size());
	}
}
