package somdudewillson.create_soul_harvest;

import net.minecraft.resources.ResourceLocation;
import somdudewillson.create_soul_harvest.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? neoforge {
import somdudewillson.create_soul_harvest.platform.neoforge.NeoforgePlatform;
 //?} forge {
/*import somdudewillson.create_soul_harvest.platform.forge.ForgePlatform;
 *///?}

public class SoulHarvestMod {

	public static final String MOD_ID = /*$ mod_id*/ "create_soul_harvest";
	public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Create: Soul Harvest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, SoulHarvestMod.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, SoulHarvestMod.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? neoforge {
		return new NeoforgePlatform();
		 //?} forge {
		/*return new ForgePlatform();
		 *///?}
	}

	public static ResourceLocation id(String path) {
		//? >= 1.21 {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
		//?} else {
		/*return new ResourceLocation(MOD_ID, path);
		*///?}
	}

	public static ResourceLocation id(String namespace, String path) {
		//? >= 1.21 {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
		//?} else {
		/*return new ResourceLocation(namespace, path);
		*///?}
	}
}
