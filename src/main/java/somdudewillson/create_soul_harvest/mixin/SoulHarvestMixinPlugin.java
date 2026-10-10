package somdudewillson.create_soul_harvest.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SoulHarvestMixinPlugin implements IMixinConfigPlugin {
	private static final boolean IS_JEI_LOADED;

	static {
		boolean loaded = false;
		try {
			Class.forName("mezz.jei.api.recipe.category.IRecipeCategory", false, SoulHarvestMixinPlugin.class.getClassLoader());
			loaded = true;
		} catch (Throwable ignored) {
		}
		IS_JEI_LOADED = loaded;
	}

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.contains("SequencedAssemblyCategoryMixin")) {
			return IS_JEI_LOADED;
		}
		return true;
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
