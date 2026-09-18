<div align="center">
  <h1>
    <sub>Grimdepth</sub>
  </h1>
  <h3>Deep underground terrors, armor-piercing weaponry, and cave threats for Fabric.</h3>
</div>

## List of features

- **Armor Piercer Enchantment**:
  * Can be applied to Swords, Axes, Bows, Crossbows, Tridents, and Spears/Maces.
  * Maximum level 3: ignores 10%, 20%, or 30% of target's armor on attack. If target has no armor, it does nothing.
  * Rare enchantment rarity (matching Fortune) that can be found in enchanting tables and dungeon loot.
  * Successful armor-piercing hits produce ominous blue skull particles.
  * Projectiles thrown or shot with Armor Piercer (arrows, tridents, spears) leave a continuous trail line of blue skull particles while in flight.

- **Underground Skeletons**:
  * Skeletons spawning underground scale in danger the deeper you go.
  * Chance to spawn with an enchanted bow begins at 5% near the surface, reaches 25% at Deepslate ($Y \le 0$), and ramps up to 50% near Bedrock.
  * Upper underground skeletons roll either Power or Armor Piercer.
  * From Deepslate level downward, skeletons can spawn wielding bows with **both** Power and Armor Piercer (up to Level 3).

- **Underground Zombies & Zombie Leaders**:
  * Zombies follow the same depth-scaled equipment chances as skeletons.
  * Equipped zombies spawn with stone or iron shovels or pickaxes (pickaxes favored). Tools are mostly stone higher up and mostly iron at Deepslate depths.
  * **Zombie Leaders**:
    * Starting at Deepslate level, zombies have a 35% chance to spawn as a Zombie Leader.
    * Maximum of 2 active leaders in nearby terrain (spawns temporarily pause if 2 are active nearby).
    * Guaranteed Iron Sword with Sharpness, Armor Piercer, or both.
    * Equipped with 2 random pieces from an iron & chainmail armor pool.
    * +10% bonus movement speed and increased step height / jump boost capable of jumping 1.5 blocks (over fences).
    * Emits ambient blue skull particles when players are close enough to perceive them.

- **Underground Creepers**:
  * Dynamic fuse times: underground creepers randomly roll a fuse between standard (30 ticks) and 3x faster (10 ticks) whenever they start swelling.
  * **Light Hunters**: deep underground creepers seek out and detonate near light sources (torches, soul torches, lanterns, soul lanterns, glowstone, lit redstone lamps, lit copper bulbs). Player combat always takes priority over light hunting.

- **Deepslate Replacements**:
  * From Deepslate level ($Y \le 0$), Spiders have a 10% chance to spawn as **Cave Spiders**.
  * From Deepslate level ($Y \le 0$), Bats have a 10% chance to spawn as **Vexes**.

---

## Configuration

All values, spawn chances, depth thresholds, and equipment pools can be customized in `.minecraft/config/grimdepth.json`:

```json
{
  "general": {
    "undergroundYLevel": 64,
    "deepslateYLevel": 0,
    "bedrockYLevel": -64
  },
  "armorPiercer": {
    "armorPiercingPerLevel": [
      0.10,
      0.20,
      0.30
    ],
    "spawnBlueSkullParticles": true,
    "projectileTrailParticles": true
  },
  "skeletons": {
    "minUndergroundEnchantedBowChance": 0.05,
    "deepslateEnchantedBowChance": 0.25,
    "bedrockEnchantedBowChance": 0.50,
    "maxEnchantLevel": 3
  },
  "zombies": {
    "minUndergroundToolChance": 0.05,
    "deepslateToolChance": 0.25,
    "bedrockToolChance": 0.50,
    "pickaxeChance": 0.70,
    "shovelChance": 0.30,
    "upperLevelsStoneToolChance": 0.80,
    "deepslateIronToolChance": 0.80,
    "stoneTools": [
      "minecraft:stone_pickaxe",
      "minecraft:stone_shovel"
    ],
    "ironTools": [
      "minecraft:iron_pickaxe",
      "minecraft:iron_shovel"
    ],
    "leaders": {
      "deepslateLeaderChance": 0.35,
      "maxNearbyLeaders": 2,
      "nearbyLeaderCheckRadius": 64.0,
      "bonusMovementSpeed": 0.10,
      "stepHeight": 1.5,
      "particleProximityRadius": 24.0,
      "leaderArmorPool": [
        "minecraft:iron_helmet",
        "minecraft:iron_chestplate",
        "minecraft:chainmail_leggings",
        "minecraft:chainmail_boots"
      ],
      "armorPiecesCount": 2,
      "weapon": "minecraft:iron_sword",
      "bothEnchantmentsChance": 0.30
    }
  },
  "creepers": {
    "undergroundMinFuseTicks": 10,
    "undergroundMaxFuseTicks": 30,
    "huntLightSources": true,
    "lightSearchHorizontalRange": 12,
    "lightSearchVerticalRange": 4
  },
  "spiders": {
    "deepslateCaveSpiderChance": 0.10
  },
  "bats": {
    "deepslateVexChance": 0.10
  }
}
```