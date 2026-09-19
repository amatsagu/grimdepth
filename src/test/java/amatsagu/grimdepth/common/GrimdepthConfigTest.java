package amatsagu.grimdepth.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GrimdepthConfigTest {

    @Test
    public void testZombiePrimitiveAndAdvancedDefaults() {
        GrimdepthConfig.ZombiesConfig cfg = new GrimdepthConfig.ZombiesConfig();

        Assertions.assertEquals(0.80, cfg.upperLevelsPrimitiveToolChance);
        Assertions.assertEquals(0.80, cfg.deepslateAdvancedToolChance);

        Assertions.assertTrue(cfg.primitiveTools.contains("minecraft:stone_pickaxe"));
        Assertions.assertTrue(cfg.primitiveTools.contains("minecraft:stone_shovel"));

        Assertions.assertTrue(cfg.advancedTools.contains("minecraft:iron_pickaxe"));
        Assertions.assertTrue(cfg.advancedTools.contains("minecraft:iron_shovel"));
    }

    @Test
    public void testConfigSerializationAndDefaults() {
        GrimdepthConfig config = new GrimdepthConfig();
        Assertions.assertNotNull(config.general);
        Assertions.assertNotNull(config.skeletons);
        Assertions.assertNotNull(config.zombies);
        Assertions.assertNotNull(config.creepers);
        Assertions.assertNotNull(config.spiders);
        Assertions.assertNotNull(config.bats);
        Assertions.assertNotNull(config.backstep);
        Assertions.assertNotNull(config.lighting);
        Assertions.assertNotNull(config.nightmareAwareness);
        Assertions.assertNotNull(config.dungeonLoot);

        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(config);
        GrimdepthConfig deserialized = gson.fromJson(json, GrimdepthConfig.class);

        Assertions.assertEquals(config.zombies.upperLevelsPrimitiveToolChance, deserialized.zombies.upperLevelsPrimitiveToolChance);
        Assertions.assertEquals(config.zombies.deepslateAdvancedToolChance, deserialized.zombies.deepslateAdvancedToolChance);
        Assertions.assertEquals(config.zombies.primitiveTools, deserialized.zombies.primitiveTools);
        Assertions.assertEquals(config.zombies.advancedTools, deserialized.zombies.advancedTools);
        Assertions.assertEquals(config.lighting.defaultGamma, deserialized.lighting.defaultGamma);
        Assertions.assertEquals(config.backstep.pushDistanceBlocks, deserialized.backstep.pushDistanceBlocks);
    }
}
