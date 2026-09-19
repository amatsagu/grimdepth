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
        Assertions.assertEquals(0.75, config.spiders.deepslateScale);
        Assertions.assertEquals(0.10, config.spiders.breakCobwebSpiderSpawnChance);
        Assertions.assertNotNull(config.backstep);
        Assertions.assertNotNull(config.lighting);
        Assertions.assertNotNull(config.nightmareAwareness);
        Assertions.assertEquals(100, config.nightmareAwareness.minSpawnIntervalTicks);
        Assertions.assertEquals(200, config.nightmareAwareness.maxSpawnIntervalTicks);
        Assertions.assertEquals(12.0, config.nightmareAwareness.minSpawnDistance);
        Assertions.assertEquals(24.0, config.nightmareAwareness.maxSpawnDistance);
        Assertions.assertTrue(config.nightmareAwareness.level1Monsters.contains("minecraft:zombie"));
        Assertions.assertTrue(config.nightmareAwareness.level1Monsters.contains("minecraft:skeleton"));
        Assertions.assertTrue(config.nightmareAwareness.level1Monsters.contains("minecraft:spider"));
        Assertions.assertTrue(config.nightmareAwareness.level2Monsters.contains("minecraft:creeper"));
        Assertions.assertTrue(config.nightmareAwareness.level2Monsters.contains("minecraft:husk"));
        Assertions.assertTrue(config.nightmareAwareness.level3Monsters.contains("minecraft:witch"));
        Assertions.assertTrue(config.nightmareAwareness.level3Monsters.contains("minecraft:cave_spider"));
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
        Assertions.assertEquals(config.nightmareAwareness.minSpawnIntervalTicks, deserialized.nightmareAwareness.minSpawnIntervalTicks);
        Assertions.assertEquals(config.nightmareAwareness.level1Monsters, deserialized.nightmareAwareness.level1Monsters);
    }

    @Test
    public void testNightmareAwarenessMonsterPools() {
        GrimdepthConfig.NightmareAwarenessConfig cfg = new GrimdepthConfig.NightmareAwarenessConfig();

        List<String> poolLvl1 = NightmareAwarenessHelper.getMonsterPool(cfg, 0);
        Assertions.assertEquals(3, poolLvl1.size());
        Assertions.assertTrue(poolLvl1.contains("minecraft:zombie"));
        Assertions.assertTrue(poolLvl1.contains("minecraft:skeleton"));
        Assertions.assertTrue(poolLvl1.contains("minecraft:spider"));

        List<String> poolLvl2 = NightmareAwarenessHelper.getMonsterPool(cfg, 1);
        Assertions.assertEquals(5, poolLvl2.size());
        Assertions.assertTrue(poolLvl2.contains("minecraft:creeper"));
        Assertions.assertTrue(poolLvl2.contains("minecraft:husk"));

        List<String> poolLvl3 = NightmareAwarenessHelper.getMonsterPool(cfg, 2);
        Assertions.assertEquals(7, poolLvl3.size());
        Assertions.assertTrue(poolLvl3.contains("minecraft:witch"));
        Assertions.assertTrue(poolLvl3.contains("minecraft:cave_spider"));
    }
}
