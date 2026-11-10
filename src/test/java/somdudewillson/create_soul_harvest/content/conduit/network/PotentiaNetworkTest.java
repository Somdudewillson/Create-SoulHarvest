package somdudewillson.create_soul_harvest.content.conduit.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaTank;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PotentiaNetworkTest {

	private static final ResourceLocation ZOMBIE = ResourceLocation.fromNamespaceAndPath("minecraft", "zombie");
	private static final ResourceLocation SKELETON = ResourceLocation.fromNamespaceAndPath("minecraft", "skeleton");

	@Test
	public void testVirtualStackDistributionAcrossConsumers() {
		PotentiaTank consumerA = new PotentiaTank(500L);
		PotentiaTank consumerB = new PotentiaTank(500L);
		List<IPotentiaHandler> consumers = List.of(consumerA, consumerB);

		// Distribute 400 mB of untyped potentia across consumers
		SoulPotentiaStack stack = new SoulPotentiaStack(400L);
		long remaining = stack.getAmount();

		for (int i = 0; i < consumers.size(); i++) {
			IPotentiaHandler c = consumers.get(i);
			long share = Math.max(1L, remaining / (consumers.size() - i));
			SoulPotentiaStack toOffer = stack.copy();
			toOffer.setAmount(share);
			long accepted = c.fill(toOffer, IPotentiaHandler.PotentiaAction.EXECUTE);
			remaining -= accepted;
		}

		assertEquals(200L, consumerA.getPotentia().getAmount());
		assertEquals(200L, consumerB.getPotentia().getAmount());
		assertEquals(0L, remaining);
	}

	@Test
	public void testDistributionCollisionAvoidance() {
		PotentiaTank zombieConsumer = new PotentiaTank(500L);
		zombieConsumer.fill(new SoulPotentiaStack(100L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);

		PotentiaTank emptyConsumer = new PotentiaTank(500L);

		// Attempt to distribute Skeleton potentia
		SoulPotentiaStack incoming = new SoulPotentiaStack(300L, SKELETON);
		List<IPotentiaHandler> consumers = List.of(zombieConsumer, emptyConsumer);

		List<IPotentiaHandler> validConsumers = new ArrayList<>();
		for (IPotentiaHandler c : consumers) {
			if (c.isPotentiaValid(0, incoming)) {
				validConsumers.add(c);
			}
		}

		// Only the empty consumer should be valid; zombieConsumer is rejected
		assertEquals(1, validConsumers.size());
		assertSame(emptyConsumer, validConsumers.get(0));

		emptyConsumer.fill(incoming, IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(300L, emptyConsumer.getPotentia().getAmount());
		assertEquals(SKELETON, emptyConsumer.getPotentia().getEntityType());
		// Zombie consumer remains unchanged
		assertEquals(100L, zombieConsumer.getPotentia().getAmount());
		assertEquals(ZOMBIE, zombieConsumer.getPotentia().getEntityType());
	}

	@Test
	public void testFlowDirectionCalculationHorizontalX() {
		PotentiaNetwork network = new PotentiaNetwork();
		BlockPos supplierEast = new BlockPos(2, 0, 0);
		BlockPos supplierWest = new BlockPos(0, 0, 0);
		BlockPos conduit = new BlockPos(1, 0, 0);

		// Single conduit adjacent to supplier
		network.addConnectionForTest(conduit, new BlockPos(1, 0, 1));

		Direction flowFromWest = network.determineFlowDirection(supplierWest, conduit, Axis.X);
		assertEquals(Direction.EAST, flowFromWest);

		Direction flowFromEast = network.determineFlowDirection(supplierEast, conduit, Axis.X);
		assertEquals(Direction.WEST, flowFromEast);
	}

	@Test
	public void testFlowDirectionCalculationHorizontalZ() {
		PotentiaNetwork network = new PotentiaNetwork();
		BlockPos supplierNorth = new BlockPos(0, 0, 0);
		BlockPos supplierSouth = new BlockPos(0, 0, 2);
		BlockPos conduit = new BlockPos(0, 0, 1);

		network.addConnectionForTest(conduit, new BlockPos(1, 0, 1));

		Direction flowFromNorth = network.determineFlowDirection(supplierNorth, conduit, Axis.Z);
		assertEquals(Direction.SOUTH, flowFromNorth);

		Direction flowFromSouth = network.determineFlowDirection(supplierSouth, conduit, Axis.Z);
		assertEquals(Direction.NORTH, flowFromSouth);
	}

	@Test
	public void testFlowDirectionCalculationVerticalY() {
		PotentiaNetwork network = new PotentiaNetwork();
		BlockPos supplierBottom = new BlockPos(0, 0, 0);
		BlockPos supplierTop = new BlockPos(0, 2, 0);
		BlockPos conduit = new BlockPos(0, 1, 0);

		network.addConnectionForTest(conduit, new BlockPos(1, 1, 0));

		Direction flowFromBottom = network.determineFlowDirection(supplierBottom, conduit, Axis.Y);
		assertEquals(Direction.UP, flowFromBottom);

		Direction flowFromTop = network.determineFlowDirection(supplierTop, conduit, Axis.Y);
		assertEquals(Direction.DOWN, flowFromTop);
	}

	@Test
	public void testMultiSegmentFlowDirectionPonderScene() {
		PotentiaNetwork network = new PotentiaNetwork();
		BlockPos supplierA = new BlockPos(0, 1, 0);
		BlockPos supplierB = new BlockPos(4, 1, 6);

		// Build pipe path matching ponder scene
		// (0,1,1) -> (0,1,2) -> (0,1,3) -> (1,1,3) -> (2,1,3) -> (3,1,3) -> (4,1,3) -> (4,1,4) -> (4,1,5)
		network.addConnectionForTest(new BlockPos(0, 1, 1), new BlockPos(0, 1, 2));
		network.addConnectionForTest(new BlockPos(0, 1, 2), new BlockPos(0, 1, 3));
		network.addConnectionForTest(new BlockPos(0, 1, 3), new BlockPos(1, 1, 3));
		network.addConnectionForTest(new BlockPos(1, 1, 3), new BlockPos(2, 1, 3));
		network.addConnectionForTest(new BlockPos(2, 1, 3), new BlockPos(3, 1, 3));
		network.addConnectionForTest(new BlockPos(3, 1, 3), new BlockPos(4, 1, 3));
		network.addConnectionForTest(new BlockPos(4, 1, 3), new BlockPos(4, 1, 4));
		network.addConnectionForTest(new BlockPos(4, 1, 4), new BlockPos(4, 1, 5));

		BlockPos wrenchedConduit = new BlockPos(4, 1, 4);

		// When supplier is A at (0, 1, 0), flow at (4, 1, 4) should be towards +Z (SOUTH)
		Direction flowFromA = network.determineFlowDirection(supplierA, wrenchedConduit, Axis.Z);
		assertEquals(Direction.SOUTH, flowFromA);

		// When supplier is B at (4, 1, 6), flow at (4, 1, 4) should be towards -Z (NORTH)
		Direction flowFromB = network.determineFlowDirection(supplierB, wrenchedConduit, Axis.Z);
		assertEquals(Direction.NORTH, flowFromB);
	}
}
