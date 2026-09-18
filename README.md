<div align="center">
  <h1>
    <sub>Grimdepth</sub>
  </h1>
  <h3>Deep underground terrors, armor-piercing weaponry, and cave threats for Fabric.</h3>
</div>

## List of features

- **Armor Piercer Enchantment**:
  * Can be applied to Swords, Axes, Bows, Crossbows, Tridents, and Spears/Maces.
  * Maximum level 3: ignores 10%, 20%, or 30% of target's armor on attack.
  * Rare enchantment rarity (matching Fortune) that can be found in enchanting tables, dungeon loot, and villager trades.
  * **Critical Hit Particle Replacement**: Replaces vanilla critical hit particles with ominous blue skull particles (`TRIAL_OMEN`) across all melee weapons (including spears) and projectiles (bows, crossbows, tridents).
  * Prioritizes blue skull particles over Sharpness enchanted hit particles so visuals are never mixed.
  * Thrown tridents and projectile arrows leave a continuous blue skull trail in flight.

- **Backstep Enchantment**:
  * Rare enchantment (matching Fortune rarity) applicable to **Bows**, **Crossbows**, and **Tridents**.
  * **Smooth Recoil**: When successfully releasing an arrow from a bow, firing from a crossbow, or throwing a trident, the player is gently pushed backward by ~1 block (configurable) in the opposite direction of aim.
  * **Safe Floor Detection**: Recoil only triggers if there is a safe, solid floor behind the player. Prevents accidentally stepping into lava, fire, campfires, cacti, magma, or falling off cliffs into the void.
  * **Mobility & Visuals**: Grants 5 seconds of Movement Speed with subtle, ambient beacon-style swirls (`ambient = true`) and creates a small smoke poof near the player's feet as they recoil.

- **Underground Skeletons**:
  * Skeletons spawning underground scale in danger the deeper you go.
  * Chance to spawn with an enchanted bow begins at 5% near the surface, reaches 25% at Deepslate ($Y \le 0$), and ramps up to 50% near Bedrock.
  * Upper underground skeletons roll either Power or Armor Piercer.
  * From Deepslate level downward ($Y \le 0$), skeletons that roll enchanted bows **always** receive **Power**, **Armor Piercer**, and **Backstep** (up to Level 3). Skeletons that do not roll enchants remain unenchanted.

- **Underground Zombies & Vanilla Zombie Leaders**:
  * Zombies follow the same depth-scaled equipment chances as skeletons.
  * Equipped zombies spawn with stone or iron shovels or pickaxes (pickaxes favored). Tools are mostly stone higher up and mostly iron at Deepslate depths.
  * **Zombie Leaders**:
    * Starting at Deepslate level, zombies have a 35% chance to spawn as a Zombie Leader (max 2 nearby).
    * Integrated with Minecraft's native built-in leader system:
      * Automatically breaks wooden doors (`setCanBreakDoors(true)`).
      * Applies vanilla `leader_zombie_bonus` to reinforcement call chance (+0.5 to +0.75).
      * Applies vanilla `leader_zombie_bonus` to maximum health (2× to 4× health multiplier).
    * Equipped with a guaranteed Iron Sword (Sharpness, Armor Piercer, or both) and 2 random pieces from an iron & chainmail armor pool.
    * Enhanced with +10% bonus movement speed, 1.5-block step height, jump boost, and an ambient blue skull aura when players draw near.

- **Underground Creepers**:
  * **Dynamic Fuse Times**: Underground creepers randomly roll a fuse between standard (30 ticks) and 1.5× faster (20 ticks) whenever swelling.
  * **Light Hunters**: Deep underground creepers search out and detonate near light sources (torches, soul torches, lanterns, soul lanterns, glowstone, lit redstone lamps, lit copper bulbs).
  * Player combat always takes priority over light hunting. When hunting light, creepers navigate to the light source, halt within 3 blocks, and detonate cleanly.

- **Deepslate Replacements**:
  * From Deepslate level ($Y \le 0$), Spiders have a 10% chance to spawn as **Cave Spiders**.
  * From Deepslate level ($Y \le 0$), Bats have a 10% chance to spawn as **Vexes**.

- **Atmospheric Lighting & Night Vision Overhaul**:
  * **Darker Ambience (33.3% Darker Overall)**: The world lightmap is recalibrated with a $\frac{2}{3}$ scaling factor, lowering maximum brightness and deepening shadows for an atmospheric cave exploration experience.
  * **New 75% Default Brightness**: Game settings brightness default is set to **75%** (labeled as "Default"), which renders **identically** to standard Vanilla 50% ($0.75 \times \frac{2}{3} = 0.50$).
  * **Reworked Night Vision Status Effect**:
    - Replaces the artificial fullbright ambient wash and the end-of-potion flashing shader/fog flicker.
    - Enhances vision naturally by boosting the player's brightness setting: **+25% at Level 1** and **+10% per level above 1** (Level 2 = +35%, Level 3 = +45%).
    - Fully compatible with underwater Conduit Power vision.

---

## Configuration

All features, spawn chances, depth thresholds, equipment pools, and recoil settings are 100% configurable in `.minecraft/config/grimdepth.json`:

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
  "backstep": {
    "enabled": true,
    "pushDistanceBlocks": 1.0,
    "scaleWithLevel": true,
    "pushDistancePerLevel": 0.5,
    "speedDurationTicks": 100,
    "speedAmplifier": 0,
    "requireSafeFloor": true,
    "maxSafeDropDistance": 2.0
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
    "undergroundMinFuseTicks": 20,
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
  },
  "lighting": {
    "defaultGamma": 0.75,
    "brightnessDarkeningScale": 0.6666666666666666,
    "nightVisionBaseBoost": 0.25,
    "nightVisionPerLevelBoost": 0.10,
    "disableNightVisionFlashingShader": true
  }
}
```