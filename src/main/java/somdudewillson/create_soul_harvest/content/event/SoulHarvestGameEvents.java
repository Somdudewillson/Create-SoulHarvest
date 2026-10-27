package somdudewillson.create_soul_harvest.content.event;

//? neoforge {
import net.neoforged.bus.api.IEventBus;
//?} forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
*///?}

public class SoulHarvestGameEvents {

	public static void register(IEventBus eventBus) {
		eventBus.register(SoulHarvestDeathEvents.class);
		eventBus.register(CarvedBoneEvents.class);
		eventBus.register(PipeCollisionEvents.class);
		eventBus.register(AnimaGlassEvents.class);
		eventBus.register(SpellGameEvents.class);
		eventBus.register(SoulHarvestDataEvents.class);
		eventBus.register(NecroniumDecayEvents.class);
		eventBus.register(AbsoluteIceEvents.class);
	}
}
