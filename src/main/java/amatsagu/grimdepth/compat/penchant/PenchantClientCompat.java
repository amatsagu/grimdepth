package amatsagu.grimdepth.compat.penchant;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class PenchantClientCompat {
    public static void initClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.level != null && PenchantCompat.isIntegrationActive()) {
                PenchantCompat.injectDefinitions(client.level.registryAccess(), "Client");
            }
        });
    }
}
