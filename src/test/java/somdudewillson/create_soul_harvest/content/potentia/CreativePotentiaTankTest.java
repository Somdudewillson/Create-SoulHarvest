package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import somdudewillson.create_soul_harvest.content.potentia.tank.CreativePotentiaTank;
import somdudewillson.create_soul_harvest.content.potentia.tank.ReservoirMode;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class CreativePotentiaTankTest {

	private static final ResourceLocation ZOMBIE = ResourceLocation.fromNamespaceAndPath("minecraft", "zombie");
	private static final ResourceLocation CREEPER = ResourceLocation.fromNamespaceAndPath("minecraft", "creeper");

	@Test
	public void testSupplierModeUntyped() {
		AtomicReference<ReservoirMode> mode = new AtomicReference<>(ReservoirMode.SUPPLY);
		AtomicReference<ResourceLocation> filter = new AtomicReference<>(null);

		CreativePotentiaTank tank = new CreativePotentiaTank(16000L, mode::get, filter::get);

		// In supplier mode with null filter: full untyped potentia stack
		SoulPotentiaStack contents = tank.getPotentia();
		assertFalse(contents.isEmpty());
		assertEquals(16000L, contents.getAmount());
		assertNull(contents.getEntityType());

		// Drain gives requested amount without depleting
		SoulPotentiaStack drained = tank.drain(500L, IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(500L, drained.getAmount());
		assertNull(drained.getEntityType());
		assertEquals(16000L, tank.getPotentia().getAmount());

		// Cannot fill a supplier
		long filled = tank.fill(new SoulPotentiaStack(100L), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(0L, filled);
	}

	@Test
	public void testSupplierModeFiltered() {
		AtomicReference<ReservoirMode> mode = new AtomicReference<>(ReservoirMode.SUPPLY);
		AtomicReference<ResourceLocation> filter = new AtomicReference<>(ZOMBIE);

		CreativePotentiaTank tank = new CreativePotentiaTank(16000L, mode::get, filter::get);

		// Contents are full Zombie potentia
		SoulPotentiaStack contents = tank.getPotentia();
		assertEquals(16000L, contents.getAmount());
		assertEquals(ZOMBIE, contents.getEntityType());

		// Drain specific type matches
		SoulPotentiaStack drainedZombie = tank.drain(new SoulPotentiaStack(300L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(300L, drainedZombie.getAmount());
		assertEquals(ZOMBIE, drainedZombie.getEntityType());

		// Drain wrong type fails
		SoulPotentiaStack drainedCreeper = tank.drain(new SoulPotentiaStack(300L, CREEPER), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertTrue(drainedCreeper.isEmpty());
	}

	@Test
	public void testConsumerModeVoidSink() {
		AtomicReference<ReservoirMode> mode = new AtomicReference<>(ReservoirMode.CONSUME);
		AtomicReference<ResourceLocation> filter = new AtomicReference<>(null);

		CreativePotentiaTank tank = new CreativePotentiaTank(16000L, mode::get, filter::get);

		// Contents are empty
		assertTrue(tank.getPotentia().isEmpty());

		// Accepts any potentia when unfiltered and voids it
		long filledUntyped = tank.fill(new SoulPotentiaStack(1000L), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(1000L, filledUntyped);
		assertTrue(tank.getPotentia().isEmpty());

		long filledZombie = tank.fill(new SoulPotentiaStack(500L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(500L, filledZombie);
		assertTrue(tank.getPotentia().isEmpty());

		// Cannot drain consumer
		SoulPotentiaStack drained = tank.drain(500L, IPotentiaHandler.PotentiaAction.EXECUTE);
		assertTrue(drained.isEmpty());
	}

	@Test
	public void testConsumerModeFiltered() {
		AtomicReference<ReservoirMode> mode = new AtomicReference<>(ReservoirMode.CONSUME);
		AtomicReference<ResourceLocation> filter = new AtomicReference<>(ZOMBIE);

		CreativePotentiaTank tank = new CreativePotentiaTank(16000L, mode::get, filter::get);

		// Accepts Zombie potentia
		long filledZombie = tank.fill(new SoulPotentiaStack(800L, ZOMBIE), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(800L, filledZombie);

		// Rejects Creeper potentia
		long rejectedCreeper = tank.fill(new SoulPotentiaStack(800L, CREEPER), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(0L, rejectedCreeper);

		// Rejects untyped potentia
		long rejectedUntyped = tank.fill(new SoulPotentiaStack(800L), IPotentiaHandler.PotentiaAction.EXECUTE);
		assertEquals(0L, rejectedUntyped);
	}
}
