package somdudewillson.create_soul_harvest.content.spell;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;

@RequiredArgsConstructor
@Getter
public abstract class SimplePersistentGlyphSpell implements PersistentGlyphSpell {
	protected final ResourceLocation id;
}
