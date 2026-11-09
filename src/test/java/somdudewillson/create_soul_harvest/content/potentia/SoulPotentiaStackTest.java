package somdudewillson.create_soul_harvest.content.potentia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SoulPotentiaStackTest {

	private static final ResourceLocation ZOMBIE = ResourceLocation.fromNamespaceAndPath("minecraft", "zombie");
	private static final ResourceLocation SKELETON = ResourceLocation.fromNamespaceAndPath("minecraft", "skeleton");

	@Test
	public void testEmptyStack() {
		assertTrue(SoulPotentiaStack.EMPTY.isEmpty());
		assertEquals(0, SoulPotentiaStack.EMPTY.getAmount());
		assertNull(SoulPotentiaStack.EMPTY.getEntityType());

		SoulPotentiaStack zeroStack = new SoulPotentiaStack(0);
		assertTrue(zeroStack.isEmpty());
	}

	@Test
	public void testUntypedStack() {
		SoulPotentiaStack untyped = new SoulPotentiaStack(500);
		assertFalse(untyped.isEmpty());
		assertEquals(500, untyped.getAmount());
		assertFalse(untyped.isTyped());
		assertNull(untyped.getEntityType());
	}

	@Test
	public void testTypedStack() {
		SoulPotentiaStack typed = new SoulPotentiaStack(250, ZOMBIE);
		assertTrue(typed.isTyped());
		assertEquals(ZOMBIE, typed.getEntityType());
		assertEquals(250, typed.getAmount());
	}

	@Test
	public void testCollisionRejectionBetweenDistinctTypes() {
		SoulPotentiaStack zombieStack = new SoulPotentiaStack(100, ZOMBIE);
		SoulPotentiaStack skeletonStack = new SoulPotentiaStack(100, SKELETON);
		SoulPotentiaStack untypedStack = new SoulPotentiaStack(100);

		// Different entity types collide and cannot combine
		assertFalse(zombieStack.canCombine(skeletonStack));
		assertFalse(skeletonStack.canCombine(zombieStack));
		assertFalse(SoulPotentiaStack.isSameType(zombieStack, skeletonStack));

		// Untyped vs typed collide and cannot combine
		assertFalse(zombieStack.canCombine(untypedStack));
		assertFalse(untypedStack.canCombine(zombieStack));
		assertFalse(SoulPotentiaStack.isSameType(zombieStack, untypedStack));

		// Identical typed stacks can combine
		SoulPotentiaStack zombieStack2 = new SoulPotentiaStack(50, ZOMBIE);
		assertTrue(zombieStack.canCombine(zombieStack2));
		assertTrue(SoulPotentiaStack.isSameType(zombieStack, zombieStack2));

		// Identical untyped stacks can combine
		SoulPotentiaStack untypedStack2 = new SoulPotentiaStack(50);
		assertTrue(untypedStack.canCombine(untypedStack2));
		assertTrue(SoulPotentiaStack.isSameType(untypedStack, untypedStack2));
	}

	@Test
	public void testGrowShrinkAndSplit() {
		SoulPotentiaStack stack = new SoulPotentiaStack(200, ZOMBIE);
		stack.grow(50);
		assertEquals(250, stack.getAmount());

		SoulPotentiaStack splitStack = stack.split(100);
		assertEquals(100, splitStack.getAmount());
		assertEquals(ZOMBIE, splitStack.getEntityType());
		assertEquals(150, stack.getAmount());

		stack.shrink(150);
		assertTrue(stack.isEmpty());
		assertNull(stack.getEntityType());
	}

	@Test
	public void testNbtSerialization() {
		SoulPotentiaStack originalTyped = new SoulPotentiaStack(750, ZOMBIE);
		CompoundTag tag = originalTyped.writeToNBT(new CompoundTag());
		SoulPotentiaStack deserializedTyped = SoulPotentiaStack.readFromNBT(tag);

		assertEquals(originalTyped, deserializedTyped);
		assertEquals(ZOMBIE, deserializedTyped.getEntityType());
		assertEquals(750, deserializedTyped.getAmount());

		SoulPotentiaStack originalUntyped = new SoulPotentiaStack(300);
		CompoundTag tagUntyped = originalUntyped.writeToNBT(new CompoundTag());
		SoulPotentiaStack deserializedUntyped = SoulPotentiaStack.readFromNBT(tagUntyped);

		assertEquals(originalUntyped, deserializedUntyped);
		assertNull(deserializedUntyped.getEntityType());
		assertEquals(300, deserializedUntyped.getAmount());
	}
}
