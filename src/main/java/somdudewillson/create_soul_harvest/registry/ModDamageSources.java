package somdudewillson.create_soul_harvest.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

public final class ModDamageSources {

	private ModDamageSources() {}

	public static final ResourceKey<DamageType> CONTAGION = ResourceKey.create(Registries.DAMAGE_TYPE, SoulHarvestMod.id("contagion"));

	public static DamageSource contagion(Level level) {
		return contagion(level, null);
	}

	public static DamageSource contagion(Level level, @Nullable Entity source) {
		return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CONTAGION), source);
	}
}
