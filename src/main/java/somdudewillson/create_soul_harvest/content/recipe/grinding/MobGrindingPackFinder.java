package somdudewillson.create_soul_harvest.content.recipe.grinding;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

//? >= 1.21 {
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import java.util.Optional;
//?}

//? neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddPackFindersEvent;
//?} forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.AddPackFindersEvent;
*///?}

public class MobGrindingPackFinder {

	public static void register(IEventBus modBus) {
		modBus.addListener(MobGrindingPackFinder::onAddPackFinders);
	}

	public static void onAddPackFinders(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			event.addRepositorySource((consumer) -> {
				//? >= 1.21 {
				PackLocationInfo location = new PackLocationInfo(
						"create_soul_harvest:mob_grinding",
						Component.literal("Create: Soul Harvest Mob Grinding Recipes"),
						PackSource.BUILT_IN,
						Optional.empty()
				);
				PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
				Pack pack = Pack.readMetaAndCreate(
						location,
						new Pack.ResourcesSupplier() {
							@Override
							public PackResources openPrimary(PackLocationInfo loc) {
								return new MobGrindingPackResources(loc);
							}

							@Override
							public PackResources openFull(PackLocationInfo loc, Pack.Metadata metadata) {
								return new MobGrindingPackResources(loc);
							}
						},
						PackType.SERVER_DATA,
						selectionConfig
				);
				if (pack != null) {
					consumer.accept(pack);
				}
				//?} else {
				/*Pack pack = Pack.readMetaAndCreate(
						"create_soul_harvest:mob_grinding",
						Component.literal("Create: Soul Harvest Mob Grinding Recipes"),
						true,
						MobGrindingPackResources::new,
						PackType.SERVER_DATA,
						Pack.Position.BOTTOM,
						PackSource.BUILT_IN
				);
				if (pack != null) {
					consumer.accept(pack);
				}
				*///?}
			});
		}
	}
}
