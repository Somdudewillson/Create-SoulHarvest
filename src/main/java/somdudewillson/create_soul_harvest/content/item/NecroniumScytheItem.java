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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
//? >= 1.21 {
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
//?} else {
/*import java.util.UUID;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
*///?}
import somdudewillson.create_soul_harvest.registry.ModDamageSources;
import somdudewillson.create_soul_harvest.registry.ModItemTiers;

public class NecroniumScytheItem extends SwordItem {

	public static final double RANGE_BONUS = 0.5D;

	//? >= 1.21 {
	public NecroniumScytheItem(Item.Properties properties) {
		super(ModItemTiers.NECRONIUM, properties.attributes(createScytheAttributes()));
	}

	public static ItemAttributeModifiers createScytheAttributes() {
		return SwordItem.createAttributes(ModItemTiers.NECRONIUM, 3, -3.2F)
				.withModifierAdded(
						Attributes.ENTITY_INTERACTION_RANGE,
						new AttributeModifier(SoulHarvestMod.id("scythe_range_bonus"), RANGE_BONUS, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
				);
	}
	//?} else {
	/*private static final UUID SCYTHE_RANGE_MODIFIER_UUID = UUID.fromString("938634e2-632b-426b-9528-7fa7621c27cc");
	private final Supplier<Multimap<Attribute, AttributeModifier>> scytheModifiers = Suppliers.memoize(() -> {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)(3.0F + ModItemTiers.NECRONIUM.getAttackDamageBonus()), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.2D, AttributeModifier.Operation.ADDITION));
		builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(SCYTHE_RANGE_MODIFIER_UUID, "Weapon modifier", RANGE_BONUS, AttributeModifier.Operation.ADDITION));
		return builder.build();
	});

	public NecroniumScytheItem(Item.Properties properties) {
		super(ModItemTiers.NECRONIUM, 3, -3.2F, properties);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		if (slot == EquipmentSlot.MAINHAND) {
			return this.scytheModifiers.get();
		}
		return super.getDefaultAttributeModifiers(slot);
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
