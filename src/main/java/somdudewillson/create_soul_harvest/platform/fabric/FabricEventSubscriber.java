package somdudewillson.create_soul_harvest.platform.fabric;

//? fabric {

/*import somdudewillson.create_soul_harvest.event.ExampleEventHandler; // sample_content
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents; // sample_content
import net.minecraft.server.level.ServerPlayer; // sample_content

public class FabricEventSubscriber {

	public static void registerEvents() {
		//? >= 1.21 {
		// sample_content
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> { // sample_content
			if (entity instanceof ServerPlayer && damageTaken > 0) { // sample_content
				ExampleEventHandler.onPlayerHurt((ServerPlayer) entity); // sample_content
			} // sample_content
		}); // sample_content
		//?} else {
		/^ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (entity instanceof ServerPlayer && amount > 0) {
				ExampleEventHandler.onPlayerHurt((ServerPlayer) entity);
			}
			return true;
		});
		^///?}
	}
}
*///?}
