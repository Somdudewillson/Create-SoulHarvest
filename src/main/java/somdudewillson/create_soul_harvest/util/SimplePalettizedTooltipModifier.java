package somdudewillson.create_soul_harvest.util;

import static net.minecraft.ChatFormatting.*;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class SimplePalettizedTooltipModifier implements TooltipModifier {
	protected final Palette palette;
	
	protected SimplePalettizedTooltipModifier(Palette palette) {
		this.palette = palette;
	}
	
	protected static List<Component> wrapShiftConditionComponents(List<Component> innerComponents) {
		List<Component> list = new ArrayList<>();
		String[] holdDesc = CreateLang.translateDirect("tooltip.holdForDescription", "$")
				.getString()
				.split("\\$");
		MutableComponent keyShift = CreateLang.translateDirect("tooltip.keyShift");

		boolean shift = Screen.hasShiftDown();

		MutableComponent tabBuilder = Component.empty();
		tabBuilder.append(Component.literal(holdDesc[0]).withStyle(DARK_GRAY));
		tabBuilder.append(keyShift.plainCopy()
			.withStyle(shift ? WHITE : GRAY));
		tabBuilder.append(Component.literal(holdDesc[1]).withStyle(DARK_GRAY));
		list.add(0, tabBuilder);
		
		if (shift) {
			list.add(CommonComponents.EMPTY);
			
			list.addAll(innerComponents);
		}
		
		return list;
	}
	
	protected static List<Component> wrapCtrlConditionComponents(List<Component> innerComponents) {
		List<Component> list = new ArrayList<>();
		String[] holdCtrl = CreateLang.translateDirect("tooltip.holdForControls", "$")
				.getString()
				.split("\\$");
		MutableComponent keyCtrl = CreateLang.translateDirect("tooltip.keyCtrl");

		boolean ctrl = Screen.hasControlDown();

		MutableComponent tabBuilder = Component.empty();
		tabBuilder.append(Component.literal(holdCtrl[0]).withStyle(DARK_GRAY));
		tabBuilder.append(keyCtrl.plainCopy()
			.withStyle(ctrl ? WHITE : GRAY));
		tabBuilder.append(Component.literal(holdCtrl[1]).withStyle(DARK_GRAY));
		list.add(0, tabBuilder);
		
		if (ctrl) {
			list.add(CommonComponents.EMPTY);
			
			list.addAll(innerComponents);
		}
		
		return list;
	}
}
