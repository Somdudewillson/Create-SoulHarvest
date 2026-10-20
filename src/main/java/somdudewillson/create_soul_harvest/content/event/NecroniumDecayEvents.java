package somdudewillson.create_soul_harvest.content.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

//? neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
*///?}

import somdudewillson.create_soul_harvest.content.item.NecroniumWasteItem;
import somdudewillson.create_soul_harvest.registry.ModItems;

import java.util.List;

public class NecroniumDecayEvents {

	public static final float WITHER_SKELETON_SPAWN_CHANCE = 0.125f; // 1 in 8 per waste unit
	public static final int MAX_WITHER_SKELETONS = 8;
	public static final double NEARBY_SEARCH_RADIUS = 8.0;
	public static final int BASE_REDUCTION = 20 * 10;
	public static final int PER_WASTE_REDUCTION = 25; // 1.25 seconds per effective waste unit

	@SubscribeEvent
	public static void onItemExpire(ItemExpireEvent event) {
		ItemEntity itemEntity = event.getEntity();
		if (itemEntity == null || itemEntity.level().isClientSide()) {
			return;
		}

		ItemStack stack = itemEntity.getItem();
		if (stack.isEmpty()) {
			return;
		}

		boolean isWaste = stack.is(ModItems.NECRONIUM_WASTE.get());
		boolean isBottle = stack.is(ModItems.NECRONIUM_WASTE_BOTTLE.get());
		if (!isWaste && !isBottle) {
			return;
		}

		if (!(itemEntity.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		int count = stack.getCount();
		int effectiveWaste = count * (isBottle ? 8 : 1);
		Vec3 pos = itemEntity.position();

		// Spawn Area of Effect Cloud (Wither Effect)
		AreaEffectCloud witherCloudEntity = NecroniumWasteItem.computeAreaEffectCloud(serverLevel, effectiveWaste, pos);
		serverLevel.addFreshEntity(witherCloudEntity);

		// FX
		if (isBottle) {
			serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 0.9f);
		}
		serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_SHOOT, SoundSource.BLOCKS, 0.8f, 0.75f);
		serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.2, pos.z, 20 + effectiveWaste, 0.4, 0.3, 0.4, 0.05);
		serverLevel.sendParticles(ParticleTypes.SOUL, pos.x, pos.y + 0.2, pos.z, 10 + effectiveWaste, 0.3, 0.3, 0.3, 0.02);

		// Wither Skeleton Spawning
		int skeletonSpawns = 0;
		for (int i = 0; i < effectiveWaste && skeletonSpawns < MAX_WITHER_SKELETONS; i++) {
			if (serverLevel.random.nextFloat() < WITHER_SKELETON_SPAWN_CHANCE) {
				skeletonSpawns++;
			}
		}

		for (int i = 0; i < skeletonSpawns; i++) {
			double offsetX = (serverLevel.random.nextDouble() - 0.5) * 3.0;
			double offsetZ = (serverLevel.random.nextDouble() - 0.5) * 3.0;
			BlockPos spawnPos = BlockPos.containing(pos.x + offsetX, pos.y, pos.z + offsetZ);
			EntityType.WITHER_SKELETON.spawn(serverLevel, spawnPos, MobSpawnType.EVENT);
		}

		// Lifespan Reduction
		AABB aabb = new AABB(
				pos.x - NEARBY_SEARCH_RADIUS, pos.y - NEARBY_SEARCH_RADIUS, pos.z - NEARBY_SEARCH_RADIUS,
				pos.x + NEARBY_SEARCH_RADIUS, pos.y + NEARBY_SEARCH_RADIUS, pos.z + NEARBY_SEARCH_RADIUS
		);

		List<ItemEntity> nearby = serverLevel.getEntitiesOfClass(
				ItemEntity.class,
				aabb,
				e -> !e.isRemoved() && e != itemEntity && isWasteItem(e.getItem())
		);

		int reduction = BASE_REDUCTION + (effectiveWaste * PER_WASTE_REDUCTION);
		for (ItemEntity nearbyItem : nearby) {
			nearbyItem.lifespan = Math.max(0, nearbyItem.lifespan - reduction);
			serverLevel.sendParticles(ParticleTypes.SMOKE, nearbyItem.getX(), nearbyItem.getY() + 0.2, nearbyItem.getZ(), 5, 0.1, 0.1, 0.1, 0.02);
		}
	}

	private static boolean isWasteItem(ItemStack stack) {
		return stack.is(ModItems.NECRONIUM_WASTE.get()) || stack.is(ModItems.NECRONIUM_WASTE_BOTTLE.get());
	}
}
