package somdudewillson.create_soul_harvest.registry;

import java.util.function.Supplier;

//? >= 1.21 {
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
//?}
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum ModItemTiers implements Tier {
	NECRONIUM(
		//? >= 1.21 {
		BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
		//?} else {
		/*4,
		*///?}
		1800,
		8.0F,
		3.0F,
		15,
		() -> Ingredient.of(ModItems.NECRONIUM_INGOT.get())
	);

	//? >= 1.21 {
	private final TagKey<Block> incorrectBlocksForDrops;
	//?} else {
	/*private final int level;
	*///?}
	private final int uses;
	private final float speed;
	private final float attackDamageBonus;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;

	//? >= 1.21 {
	ModItemTiers(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
		this.incorrectBlocksForDrops = incorrectBlocksForDrops;
		this.uses = uses;
		this.speed = speed;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairIngredient;
	}
	//?} else {
	/*ModItemTiers(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
		this.level = level;
		this.uses = uses;
		this.speed = speed;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairIngredient;
	}
	*///?}

	@Override
	public int getUses() {
		return this.uses;
	}

	@Override
	public float getSpeed() {
		return this.speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.attackDamageBonus;
	}

	//? >= 1.21 {
	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return this.incorrectBlocksForDrops;
	}
	//?} else {
	/*@Override
	public int getLevel() {
		return this.level;
	}
	*///?}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
}
