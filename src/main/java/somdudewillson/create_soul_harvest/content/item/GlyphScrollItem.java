package somdudewillson.create_soul_harvest.content.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import somdudewillson.create_soul_harvest.content.spell.GlyphSpell;

import java.util.function.Supplier;

public class GlyphScrollItem extends Item {

	private final Supplier<? extends GlyphSpell> spellSupplier;

	public GlyphScrollItem(Supplier<? extends GlyphSpell> spellSupplier, Properties properties) {
		super(properties);
		this.spellSupplier = spellSupplier;
	}

	public GlyphSpell getSpell() {
		return this.spellSupplier.get();
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
		GlyphSpell spell = spellSupplier.get();
		if (spell != null) {
			spell.cast(level, entity);
		}

		if (entity instanceof Player player && !player.isCreative()) {
			stack.shrink(1);
		}

		return stack;
	}
}
