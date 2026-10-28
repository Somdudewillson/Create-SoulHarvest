package somdudewillson.create_soul_harvest.platform;

public interface Platform {
	boolean isModLoaded(String modId);
	
	/**
	 * Like {@code isModLoaded()}, but works earlier in the loading lifecycle
	 */
	boolean isModPresent(String modId);

	ModLoader loader();

	String mcVersion();

	boolean isDevelopmentEnvironment();

	default boolean isDebug() {
		return isDevelopmentEnvironment();
	}

	enum ModLoader {
		NEOFORGE, FORGE
	}
}
