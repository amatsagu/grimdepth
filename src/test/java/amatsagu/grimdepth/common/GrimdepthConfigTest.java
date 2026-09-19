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
    public void testLegacyZombieConfigMigration() {
        GrimdepthConfig config = new GrimdepthConfig();
        config.zombies = new GrimdepthConfig.ZombiesConfig();
        config.zombies.stoneTools = List.of("minecraft:wooden_pickaxe");
        config.zombies.ironTools = List.of("minecraft:golden_pickaxe");
        config.zombies.upperLevelsStoneToolChance = 0.95;
        config.zombies.deepslateIronToolChance = 0.65;
        config.zombies.primitiveTools = null;
        config.zombies.advancedTools = null;

        // Perform migration logic
        if (config.zombies.stoneTools != null && !config.zombies.stoneTools.isEmpty()
                && (config.zombies.primitiveTools == null || config.zombies.primitiveTools.isEmpty())) {
            config.zombies.primitiveTools = new java.util.ArrayList<>(config.zombies.stoneTools);
        }
        if (config.zombies.ironTools != null && !config.zombies.ironTools.isEmpty()
                && (config.zombies.advancedTools == null || config.zombies.advancedTools.isEmpty())) {
            config.zombies.advancedTools = new java.util.ArrayList<>(config.zombies.ironTools);
        }
        if (config.zombies.upperLevelsStoneToolChance != null) {
            config.zombies.upperLevelsPrimitiveToolChance = config.zombies.upperLevelsStoneToolChance;
        }
        if (config.zombies.deepslateIronToolChance != null) {
            config.zombies.deepslateAdvancedToolChance = config.zombies.deepslateIronToolChance;
        }

        Assertions.assertEquals(List.of("minecraft:wooden_pickaxe"), config.zombies.primitiveTools);
        Assertions.assertEquals(List.of("minecraft:golden_pickaxe"), config.zombies.advancedTools);
        Assertions.assertEquals(0.95, config.zombies.upperLevelsPrimitiveToolChance);
        Assertions.assertEquals(0.65, config.zombies.deepslateAdvancedToolChance);
    }
}
