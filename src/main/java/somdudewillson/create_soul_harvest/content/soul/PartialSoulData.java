package somdudewillson.create_soul_harvest.content.soul;

import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PartialSoulData {
	private final OptionalInt tier;
	private final OptionalInt progressPerKill;
	private final List<SoulDropPool> pools;

	public PartialSoulData(OptionalInt tier, OptionalInt progressPerKill, List<SoulDropPool> pools) {
		this.tier = tier;
		this.progressPerKill = progressPerKill;
		this.pools = pools != null ? pools : Collections.emptyList();
	}

	public List<SoulDropPool> pools() {
		return Collections.unmodifiableList(pools);
	}
}
