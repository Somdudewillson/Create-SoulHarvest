package somdudewillson.create_soul_harvest.content.soul;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum DropRestriction {
	STANDARD,
	ENHANCED,
	LOOT_TOKEN;

	public static DropRestriction fromString(@Nullable String name) {
		if (name == null || name.isEmpty()) {
			return STANDARD;
		}
		try {
			return valueOf(name.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			return STANDARD;
		}
	}
}
