package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public class ModBiomes {
	public static final ResourceKey<Biome> ABSOLUTE_ICE =
			ResourceKey.create(Registries.BIOME, SoulHarvestMod.id("absolute_ice"));
}
