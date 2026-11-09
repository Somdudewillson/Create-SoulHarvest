package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PotentiaTankTest {

	private static final ResourceLocation ZOMBIE = ResourceLocation.fromNamespaceAndPath("minecraft", "zombie");
	private static final ResourceLocation SKELETON = ResourceLocation.fromNamespaceAndPath("minecraft", "skeleton");

	@Test
	public void testTankFillAndDrain() {
		PotentiaTank tank = new PotentiaTank(1000L);
		assertTrue(tank.getPotentia().isEmpty());

		// Fill 400 untyped
		long filled = tank.fill(new SoulPotentiaStack(400L), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(400L, filled);
		assertEquals(400L, tank.getPotentia().getAmount());

		// Drain 150
		SoulPotentiaStack drained = tank.drain(150L, IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(150L, drained.getAmount());
		assertEquals(250L, tank.getPotentia().getAmount());

		// Drain beyond capacity drains remaining
		SoulPotentiaStack drainedAll = tank.drain(500L, IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(250L, drainedAll.getAmount());
		assertTrue(tank.getPotentia().isEmpty());
	}

	@Test
	public void testCollisionRejectionInTank() {
		PotentiaTank tank = new PotentiaTank(1000L);

		// Fill tank with Zombie potentia
		tank.fill(new SoulPotentiaStack(500L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(ZOMBIE, tank.getPotentia().getEntityType());

		// Attempt to fill with Skeleton potentia -> COLLISION! Must be rejected
		assertFalse(tank.isPotentiaValid(0, new SoulPotentiaStack(200L, SKELETON)));
		long rejected = tank.fill(new SoulPotentiaStack(200L, SKELETON), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(0L, rejected);
		assertEquals(500L, tank.getPotentia().getAmount());

		// Attempt to fill with untyped potentia -> COLLISION! Must be rejected
		assertFalse(tank.isPotentiaValid(0, new SoulPotentiaStack(200L)));
		long rejectedUntyped = tank.fill(new SoulPotentiaStack(200L), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(0L, rejectedUntyped);

		// Compatible fill with more Zombie potentia -> accepted
		assertTrue(tank.isPotentiaValid(0, new SoulPotentiaStack(300L, ZOMBIE)));
		long accepted = tank.fill(new SoulPotentiaStack(300L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(300L, accepted);
		assertEquals(800L, tank.getPotentia().getAmount());
	}

	@Test
	public void testSimulationModeDoesNotMutateTank() {
		PotentiaTank tank = new PotentiaTank(1000L);

		long simulatedFill = tank.fill(new SoulPotentiaStack(600L), IPotentiaHandler.PotentiaAction.SIMULATE);
		assertEquals(600L, simulatedFill);
		assertTrue(tank.getPotentia().isEmpty(), "Simulate fill must not mutate tank");

		tank.fill(new SoulPotentiaStack(400L), IPotentiaHandler.PotentiaAction.EXECUTE);
		SoulPotentiaStack simulatedDrain = tank.drain(200L, IPotentiaHandler.PotentiaAction.SIMULATE);
		assertEquals(200L, simulatedDrain.getAmount());
		assertEquals(400L, tank.getPotentia().getAmount(), "Simulate drain must not mutate tank");
	}

	@Test
	public void testTankNbtPersistence() {
		PotentiaTank original = new PotentiaTank(2000L);
		original.fill(new SoulPotentiaStack(1200L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);

		CompoundTag tag = original.writeToNBT(new CompoundTag());
		PotentiaTank restored = new PotentiaTank(1000L);
		restored.readFromNBT(tag);

		assertEquals(2000L, restored.getTankCapacity(0));
		assertEquals(1200L, restored.getPotentia().getAmount());
		assertEquals(ZOMBIE, restored.getPotentia().getEntityType());
	}
}
