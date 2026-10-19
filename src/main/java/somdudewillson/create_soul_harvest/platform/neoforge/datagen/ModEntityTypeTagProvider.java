package somdudewillson.create_soul_harvest.platform.neoforge.datagen;

//? neoforge {

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.registry.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagProvider extends EntityTypeTagsProvider {

	public ModEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SoulHarvestMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		// T1 mobs
		tag(ModTags.EntityTypes.T1_MOB).add(
				EntityType.COW,
				EntityType.VILLAGER,
				EntityType.GLOW_SQUID,
				EntityType.SNOW_GOLEM,
				EntityType.CAT,
				EntityType.PANDA,
				EntityType.SHEEP,
				EntityType.ALLAY,
				EntityType.GOAT,
				EntityType.AXOLOTL,
				EntityType.FOX,
				EntityType.TADPOLE,
				EntityType.HORSE,
				EntityType.TROPICAL_FISH,
				EntityType.SALMON,
				EntityType.PUFFERFISH,
				EntityType.PIG,
				EntityType.MOOSHROOM,
				EntityType.CAMEL,
				EntityType.RABBIT,
				EntityType.FROG,
				EntityType.STRIDER,
				EntityType.DOLPHIN,
				EntityType.TRADER_LLAMA,
				EntityType.WOLF,
				EntityType.CHICKEN,
				EntityType.COD,
				EntityType.BAT,
				EntityType.SQUID,
				EntityType.MULE,
				EntityType.TURTLE,
				EntityType.LLAMA,
				EntityType.PARROT,
				EntityType.BEE,
				EntityType.OCELOT,
				EntityType.SNIFFER,
				EntityType.WANDERING_TRADER,
				EntityType.DONKEY,
				EntityType.ZOMBIE_HORSE,
				EntityType.SKELETON_HORSE
		)
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "moobloom"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "glare"))
		.addOptional(ResourceLocation.withDefaultNamespace("armadillo"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "copper_golem"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "rascal"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "crab"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("supplementaries", "red_merchant"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "tuff_golem"));

		// T2 mobs
		tag(ModTags.EntityTypes.T2_MOB).add(
				EntityType.GUARDIAN,
				EntityType.SKELETON,
				EntityType.CAVE_SPIDER,
				EntityType.SLIME,
				EntityType.SHULKER,
				EntityType.HOGLIN,
				EntityType.POLAR_BEAR,
				EntityType.MAGMA_CUBE,
				EntityType.SPIDER,
				EntityType.ZOMBIE_VILLAGER,
				EntityType.ZOGLIN,
				EntityType.PILLAGER,
				EntityType.ENDERMITE,
				EntityType.SILVERFISH,
				EntityType.ENDERMAN,
				EntityType.BLAZE,
				EntityType.CREEPER,
				EntityType.STRAY,
				EntityType.VEX,
				EntityType.WITHER_SKELETON,
				EntityType.WITCH,
				EntityType.PHANTOM,
				EntityType.VINDICATOR,
				EntityType.GHAST,
				EntityType.IRON_GOLEM,
				EntityType.ZOMBIE,
				EntityType.ZOMBIFIED_PIGLIN,
				EntityType.DROWNED,
				EntityType.PIGLIN_BRUTE,
				EntityType.RAVAGER,
				EntityType.HUSK,
				EntityType.PIGLIN
		)
		.addOptional(ResourceLocation.withDefaultNamespace("bogged"))
		.addOptional(ResourceLocation.withDefaultNamespace("breeze"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "mauler"));

		// T3 mobs
		tag(ModTags.EntityTypes.T3_MOB).add(
				EntityType.EVOKER,
				EntityType.ELDER_GUARDIAN,
				EntityType.WITHER,
				EntityType.WARDEN,
				EntityType.ENDER_DRAGON,
				EntityType.GIANT
		)
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "illusioner"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "iceologer"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("artifacts", "mimic"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "wildfire"));

		// Soul can be captured
		tag(ModTags.EntityTypes.SOUL_CAN_BE_CAPTURED)
				.addTag(ModTags.EntityTypes.T1_MOB)
				.addTag(ModTags.EntityTypes.T2_MOB)
				.addTag(ModTags.EntityTypes.T3_MOB);

		// Requires low kills
		tag(ModTags.EntityTypes.REQUIRES_LOW).add(
				EntityType.ELDER_GUARDIAN,
				EntityType.WITHER,
				EntityType.WARDEN,
				EntityType.ENDER_DRAGON,
				EntityType.GIANT
		)
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "wildfire"));

		// Requires high kills
		tag(ModTags.EntityTypes.REQUIRES_HIGH).add(
				EntityType.GUARDIAN,
				EntityType.SKELETON,
				EntityType.CAVE_SPIDER,
				EntityType.SLIME,
				EntityType.SHULKER,
				EntityType.HOGLIN,
				EntityType.POLAR_BEAR,
				EntityType.MAGMA_CUBE,
				EntityType.SPIDER,
				EntityType.ZOMBIE_VILLAGER,
				EntityType.ZOGLIN,
				EntityType.PILLAGER,
				EntityType.ENDERMITE,
				EntityType.SILVERFISH,
				EntityType.ENDERMAN,
				EntityType.BLAZE,
				EntityType.CREEPER,
				EntityType.STRAY,
				EntityType.VEX,
				EntityType.WITHER_SKELETON,
				EntityType.WITCH,
				EntityType.PHANTOM,
				EntityType.VINDICATOR,
				EntityType.GHAST,
				EntityType.IRON_GOLEM,
				EntityType.ZOMBIE,
				EntityType.ZOMBIFIED_PIGLIN,
				EntityType.DROWNED,
				EntityType.PIGLIN_BRUTE,
				EntityType.RAVAGER,
				EntityType.HUSK,
				EntityType.PIGLIN
		)
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "illusioner"))
		.addOptional(ResourceLocation.withDefaultNamespace("bogged"))
		.addOptional(ResourceLocation.withDefaultNamespace("breeze"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "mauler"));

		// Requires medium kills
		tag(ModTags.EntityTypes.REQUIRES_MEDIUM).add(
				EntityType.COW,
				EntityType.VILLAGER,
				EntityType.GLOW_SQUID,
				EntityType.SNOW_GOLEM,
				EntityType.CAT,
				EntityType.PANDA,
				EntityType.SHEEP,
				EntityType.ALLAY,
				EntityType.GOAT,
				EntityType.AXOLOTL,
				EntityType.FOX,
				EntityType.TADPOLE,
				EntityType.HORSE,
				EntityType.TROPICAL_FISH,
				EntityType.SALMON,
				EntityType.PUFFERFISH,
				EntityType.PIG,
				EntityType.MOOSHROOM,
				EntityType.CAMEL,
				EntityType.RABBIT,
				EntityType.FROG,
				EntityType.STRIDER,
				EntityType.DOLPHIN,
				EntityType.TRADER_LLAMA,
				EntityType.WOLF,
				EntityType.CHICKEN,
				EntityType.COD,
				EntityType.BAT,
				EntityType.SQUID,
				EntityType.MULE,
				EntityType.TURTLE,
				EntityType.LLAMA,
				EntityType.PARROT,
				EntityType.BEE,
				EntityType.OCELOT,
				EntityType.SNIFFER,
				EntityType.WANDERING_TRADER,
				EntityType.DONKEY,
				EntityType.ZOMBIE_HORSE,
				EntityType.SKELETON_HORSE,
				EntityType.EVOKER
		)
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "moobloom"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "glare"))
		.addOptional(ResourceLocation.withDefaultNamespace("armadillo"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "copper_golem"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "rascal"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "crab"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("supplementaries", "red_merchant"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "tuff_golem"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "illusioner"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "iceologer"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("artifacts", "mimic"));

		// Contagion immune entities
		tag(ModTags.EntityTypes.CONTAGION_IMMUNE).add(
				EntityType.IRON_GOLEM,
				EntityType.SNOW_GOLEM,
				EntityType.BLAZE,
				EntityType.GUARDIAN,
				EntityType.ELDER_GUARDIAN,
				EntityType.WITHER,
				EntityType.ENDER_DRAGON
		)
		.addTag(EntityTypeTags.SKELETONS)
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "wildfire"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "copper_golem"))
		.addOptional(ResourceLocation.fromNamespaceAndPath("friendsandfoes", "tuff_golem"));
	}

	@Override
	public @NotNull String getName() {
		return "Create Soul Harvest Entity Type Tags";
	}
}
//?}
