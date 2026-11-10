package somdudewillson.create_soul_harvest.content.potentia.tank;

import net.minecraft.util.StringRepresentable;

public enum ReservoirMode implements StringRepresentable {
	SUPPLY("supply"),
	CONSUME("consume");

	private final String name;

	ReservoirMode(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public ReservoirMode cycle() {
		return this == SUPPLY ? CONSUME : SUPPLY;
	}

	public boolean isSupply() {
		return this == SUPPLY;
	}

	public boolean isConsume() {
		return this == CONSUME;
	}
}
