package somdudewillson.create_soul_harvest.content.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
//? <= 1.20.1 {
/*import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
*///?}
import somdudewillson.create_soul_harvest.registry.ModDamageSources;
import somdudewillson.create_soul_harvest.registry.ModItemTiers;

public class NecroniumScytheItem extends SwordItem {

	//? >= 1.21 {
	public NecroniumScytheItem(Item.Properties properties) {
		super(ModItemTiers.NECRONIUM, properties.attributes(SwordItem.createAttributes(ModItemTiers.NECRONIUM, 3, -3.2F)));
	}
	//?} else {
	/*public NecroniumScytheItem(Item.Properties properties) {
		super(ModItemTiers.NECRONIUM, 3, -3.2F, properties);
	}
	*///?}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		boolean result = super.hurtEnemy(stack, target, attacker);
		if (!target.level().isClientSide() && target.isAlive()) {
			int prevInvul = target.invulnerableTime;
			target.invulnerableTime = 0;
			DamageSource witherSource = ModDamageSources.wither(target.level(), attacker);
			target.hurt(witherSource, 2.0F);
			target.invulnerableTime = Math.max(prevInvul, target.invulnerableTime);
		}
		return result;
	}
	
	@Override
	public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        return target.getBoundingBox().inflate(2.0D, 0.334D, 2.0D);
    }

	//? >= 1.21 {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		tooltipComponents.add(CommonComponents.EMPTY);
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.necronium_scythe.wither_damage").withStyle(ChatFormatting.DARK_PURPLE));
	}
	//?} else {
	/*@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		tooltipComponents.add(CommonComponents.EMPTY);
		tooltipComponents.add(Component.translatable("tooltip.create_soul_harvest.necronium_scythe.wither_damage").withStyle(ChatFormatting.DARK_PURPLE));
	}
	*///?}
}
