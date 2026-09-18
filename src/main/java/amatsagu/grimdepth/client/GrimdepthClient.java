package amatsagu.grimdepth.client;

import amatsagu.grimdepth.common.Grimdepth;
import amatsagu.grimdepth.common.GrimdepthConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.effect.MobEffects;

public class GrimdepthClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			GrimdepthConfig.LightingConfig config = GrimdepthConfig.INSTANCE.lighting;
			double currentGamma = client.options.gamma().get();

			// Anti-cheat: prevent artificially boosted brightness in configs
			if (currentGamma > config.antiCheatMaxGamma) {
				Grimdepth.LOGGER.warn("Artificially boosted brightness detected in game options ({}). Restoring to {}.",
						currentGamma, config.antiCheatResetGamma);
				client.options.gamma().set(config.antiCheatResetGamma);
				client.options.save();
			} else if (currentGamma < 0.0) {
				client.options.gamma().set(0.0);
				client.options.save();
			}

			// Check whether player currently has Night Vision potion effect on world join for proper calculations
			if (client.player != null) {
				boolean hasNightVision = client.player.hasEffect(MobEffects.NIGHT_VISION);
				int amplifier = hasNightVision ? client.player.getEffect(MobEffects.NIGHT_VISION).getAmplifier() : -1;
				Grimdepth.LOGGER.info("Player joined world. Gamma setting: {}, Night Vision: {} (level: {})",
						client.options.gamma().get(), hasNightVision, amplifier >= 0 ? amplifier + 1 : 0);
			}
		});
	}
}
