package amatsagu.grimdepth.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grimdepth implements ModInitializer {
	public static final String MOD_ID = "grimdepth";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GrimdepthConfig.load();
		GrimdepthEffects.init();
		amatsagu.grimdepth.compat.penchant.PenchantCompat.init();

		PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
			NightmareAwarenessHelper.onBlockMined(level, player, pos, state);
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			NightmareAwarenessHelper.load(server.getWorldPath(LevelResource.ROOT));
		});

		ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) -> {
			NightmareAwarenessHelper.save(server.getWorldPath(LevelResource.ROOT));
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			NightmareAwarenessHelper.save(server.getWorldPath(LevelResource.ROOT));
			NightmareAwarenessHelper.clear();
		});

		LOGGER.info("Grimdepth initialized successfully!");
	}
}
