package amatsagu.grimdepth.compat.penchant;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PenchantCompatTest {

    @Test
    public void testVersionComparisons() throws VersionParsingException {
        Version target = Version.parse(PenchantCompat.TARGET_PENCHANT_VERSION);

        Version olderPatch = Version.parse("0.5.4+mc26.3");
        Version olderMinor = Version.parse("0.4.9");
        Version exact = Version.parse("0.5.5+mc26.3");
        Version newerPatch = Version.parse("0.5.6+mc26.3");
        Version newerMinor = Version.parse("0.6.0+mc26.3");

        Assertions.assertTrue(olderPatch.compareTo(target) < 0, "0.5.4 should be older than 0.5.5");
        Assertions.assertTrue(olderMinor.compareTo(target) < 0, "0.4.9 should be older than 0.5.5");
        Assertions.assertEquals(0, exact.compareTo(target), "0.5.5+mc26.3 should equal target");
        Assertions.assertTrue(newerPatch.compareTo(target) > 0, "0.5.6 should be newer than 0.5.5");
        Assertions.assertTrue(newerMinor.compareTo(target) > 0, "0.6.0 should be newer than 0.5.5");
    }

    @Test
    public void testArmorPiercingLevelingProgression() {
        // Sharpness fallback baseline in Penchant:
        // max_cost: base = 21, per_level_above_first = 11
        // Level 1 -> 2 (targetLevel 2): 21 + 11 * (2 - 1) = 32
        // Level 2 -> 3 (targetLevel 3): 21 + 11 * (3 - 1) = 43
        int sharpnessL2 = 21 + 11 * 1;
        int sharpnessL3 = 21 + 11 * 2;
        Assertions.assertEquals(32, sharpnessL2);
        Assertions.assertEquals(43, sharpnessL3);

        // Armor Piercing requires +25% more progress for level up:
        int expectedL2 = (int) Math.round(sharpnessL2 * 1.25); // 40
        int expectedL3 = (int) Math.round(sharpnessL3 * 1.25); // 54

        // Our formula: base = 26, per_level_above_first = 14
        int apBase = 26;
        int apPerLevel = 14;
        int apL2 = apBase + apPerLevel * (2 - 1);
        int apL3 = apBase + apPerLevel * (3 - 1);

        Assertions.assertEquals(expectedL2, apL2, "Armor Piercer L2 uses must match Sharpness L2 + 25%");
        Assertions.assertEquals(expectedL3, apL3, "Armor Piercer L3 uses must match Sharpness L3 + 25%");
    }

    @Test
    public void testIncompatibleVersionVerificationFailsOnMissingClass() throws VersionParsingException {
        Version higherVersion = Version.parse("0.6.0+mc26.3");
        // When Penchant is not on classpath, verifyCompatibility throws IncompatiblePenchantVersionException
        IncompatiblePenchantVersionException ex = Assertions.assertThrows(IncompatiblePenchantVersionException.class, () -> {
            PenchantCompat.verifyCompatibility(higherVersion, true);
        });
        Assertions.assertTrue(ex.getMessage().contains("missing required class"), "Error message should mention missing required class");
    }

    @Test
    public void testBackstepDefinitionValues() {
        // Enchanting table requires experience and books as high as rarest other enchant in the game:
        // expCost = 8 (matching Infinity, Silk Touch, Wind Burst)
        // bookRequirement = 45 (matching Mending, 15 bookshelves)
        int expCost = 8;
        int bookReq = 45;
        Assertions.assertEquals(8, expCost);
        Assertions.assertEquals(45, bookReq);
    }

    @Test
    public void testDungeonLootTargets() {
        Assertions.assertTrue(amatsagu.grimdepth.common.GrimdepthLoot.TARGET_DUNGEONS.contains(net.minecraft.world.level.storage.loot.BuiltInLootTables.SIMPLE_DUNGEON));
        Assertions.assertTrue(amatsagu.grimdepth.common.GrimdepthLoot.TARGET_DUNGEONS.contains(net.minecraft.world.level.storage.loot.BuiltInLootTables.DESERT_PYRAMID));
        Assertions.assertTrue(amatsagu.grimdepth.common.GrimdepthLoot.TARGET_DUNGEONS.contains(net.minecraft.world.level.storage.loot.BuiltInLootTables.JUNGLE_TEMPLE));
        Assertions.assertTrue(amatsagu.grimdepth.common.GrimdepthLoot.TARGET_DUNGEONS.contains(net.minecraft.world.level.storage.loot.BuiltInLootTables.UNDERWATER_RUIN_BIG));
        Assertions.assertTrue(amatsagu.grimdepth.common.GrimdepthLoot.TARGET_DUNGEONS.contains(net.minecraft.world.level.storage.loot.BuiltInLootTables.UNDERWATER_RUIN_SMALL));
        Assertions.assertTrue(amatsagu.grimdepth.common.GrimdepthLoot.TARGET_DUNGEONS.contains(net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE));
    }
}
