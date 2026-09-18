package amatsagu.grimdepth.common;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grimdepth implements ModInitializer {
	public static final String MOD_ID = "grimdepth";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GrimdepthConfig.load();
		LOGGER.info("Grimdepth initialized successfully!");
	}
}
