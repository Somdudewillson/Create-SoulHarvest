package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.content.spell.RunicSpells;

public class GlyphScrollItem extends Item {

	private final int spellId;

	public GlyphScrollItem(int spellId, Properties properties) {
		super(properties);
		this.spellId = spellId;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	//? >= 1.21 {
	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 1;
	}
	//?} else {
	/*@Override
	public int getUseDuration(ItemStack stack) {
		return 1;
	}
	*///?}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		switch (spellId) {
			case 1 -> RunicSpells.castSpell1_Lightning(level, entity);
			case 2 -> RunicSpells.castSpell2_Fireball(level, entity);
			case 3 -> RunicSpells.castSpell3_Bonemeal(level, entity);
			case 4 -> RunicSpells.castSpell4_ContagionBlast(level, entity);
			case 5 -> RunicSpells.castSpell5_Fortitude(level, entity);
			case 6 -> RunicSpells.castSpell6_Updraft(level, entity);
			case 7 -> RunicSpells.castSpell7_InstantShelter(level, entity);
			case 8 -> RunicSpells.castSpell8_TerrainFlatten(level, entity);
		}

		if (entity instanceof Player player && !player.isCreative()) {
			stack.shrink(1);
		}

		return stack;
	}
}
