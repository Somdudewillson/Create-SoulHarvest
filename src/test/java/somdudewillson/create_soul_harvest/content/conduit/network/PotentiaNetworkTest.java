package somdudewillson.create_soul_harvest.content.conduit.network;

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
}
