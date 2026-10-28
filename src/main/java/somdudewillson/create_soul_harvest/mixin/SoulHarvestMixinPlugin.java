package somdudewillson.create_soul_harvest.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.util.List;
import java.util.Set;

public class SoulHarvestMixinPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (targetClassName.contains("compat.jei")
				|| mixinClassName.contains("SequencedAssemblyCategoryMixin")
				|| mixinClassName.contains("ProcessingViaFanCategoryMixin")) {
			return isJeiLoaded();
		}
		return true;
	}

	private boolean isJeiLoaded() {
		return SoulHarvestMod.xplat().isModPresent("jei");
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
