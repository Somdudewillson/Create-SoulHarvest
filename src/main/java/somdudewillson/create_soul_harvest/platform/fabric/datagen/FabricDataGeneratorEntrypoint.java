package somdudewillson.create_soul_harvest.platform.fabric.datagen;

//? fabric {
/*import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
//? if >= 1.21 {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//?}

public class FabricDataGeneratorEntrypoint implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		final FabricDataGenerator.Pack pack = generator.createPack();
		//? if >= 1.21 {
		pack.addProvider((FabricDataOutput output) -> new ModRecipeProvider(output, generator.getRegistries()));
		//?} else {
		/^pack.addProvider(ModRecipeProvider::new);
		^///?}
	}

}
*///?}
