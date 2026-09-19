<div align="center">
  <h1>
    <sub>Grimdepth</sub>
  </h1>
  <h3>A modern Fabric mod brings deep underground terrors.</h3>
</div>

## List of features

- **Armor Piercer Enchantment**:
  * Can be applied to Swords, Axes, Bows, Crossbows, Tridents, and Spears/Maces.
  * Maximum level 3: ignores 10%, 20%, or 30% of target's armor on attack.
  * Rare enchantment rarity (matching Fortune weight 2).
  * **Table-Exempt**: Impossible to roll from an enchanting table.
  * **Acquisition**: Obtainable very rarely from villager trading (librarians) and rare dungeon chests (mob spawner dungeon, trial chambers, underwater ruins/pyramids, desert pyramids, jungle temples).
  * **Critical Hit Particle Replacement**: Replaces vanilla critical hit particles with ominous blue skull particles (`TRIAL_OMEN`) across all melee weapons (including spears) and projectiles (bows, crossbows, tridents).
  * Prioritizes blue skull particles over Sharpness enchanted hit particles so visuals are never mixed.
  * Thrown tridents and projectile arrows leave a continuous blue skull trail in flight.

- **Backstep Enchantment**:
  * Rare single-level enchantment (`max_level = 1`, matching Fortune rarity weight 2) applicable to **Bows**, **Crossbows**, and **Tridents**.
  * **Table-Exempt**: Impossible to roll from an enchanting table.
  * **Acquisition**: Obtainable very rarely from villager trading and rare dungeon chests (mob spawner dungeon, trial chambers, underwater ruins/pyramids, desert pyramids, jungle temples).
  * **Smooth Recoil**: When successfully releasing an arrow from a bow, firing from a crossbow, or throwing a trident, the entity (player or skeleton) is gently pushed backward by ~1 block in the opposite direction of aim/target.
  * **Safe Floor Detection**: Recoil only triggers if there is a safe, solid floor behind the shooter. Prevents accidentally stepping into lava, fire, campfires, cacti, magma, or falling off cliffs into the void.
  * **Mobility & Visuals**: Grants 5 seconds of Movement Speed with subtle, ambient beacon-style swirls (`ambient = true`) and creates a small smoke poof near the shooter's feet as they recoil.

- **Underground Skeletons**:
  * Skeletons spawning underground scale in danger the deeper you go.
  * Chance to spawn with an enchanted bow begins at 5% near the surface, reaches 25% at Deepslate ($Y \le 0$), and ramps up to 50% near Bedrock.
  * Upper underground skeletons roll either Power or Armor Piercer (Levels 1–2).
  * From Deepslate level downward ($Y \le 0$), skeletons that roll enchanted bows receive either **Power** or **Armor Piercer** (Levels 1–3) plus guaranteed **Backstep I**. When firing their bows, skeletons actively jump back from the player if safe ground is available. Skeletons that do not roll enchants remain unenchanted.

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
  * **Anti-Cheat & Boundary Edge Case Protection**:
    - **No Negative Brightness**: If a player drops their brightness setting to 0% with Night Vision I active, brightness remains at 25% and cleanly returns to 0% when the effect expires without ever dipping into negatives.
    - **200% Absolute Maximum Cap**: Brightness is strictly clamped to a maximum of 200% (`2.0`), preventing runaway values from custom potion amplifiers or commands.
    - **Config Anti-Cheat (Singleplayer & Multiplayer)**: On joining any world (singleplayer or multiplayer), the client inspects `options.gamma`. If artificially boosted in game configs (`options.txt` > 100%), it is immediately restored to default 75% (`0.75`).

- **Nightmare Awareness Status Effect**:
  * **New Harmful Mob Effect**: Signalized with ill omen / raid omen red skulls (`RAID_OMEN` particles and sound).
  * **Sensory Aggro Scaling**: Increases hostile monsters' detection and follow distance by **+25% per level** (up to **+250%** maximum at Level 10).
  * **Depth Ore Mining Triggers**: Triggered when mining naturally generated deepslate ore variants deep underground ($Y \le 0$).
  * **Anti-Exploit Natural Ore Tracking**: Tracks player-placed ores across world saves so player-placed blocks never grant the effect.
  * **Dynamic Risk Chances**:
    - Default deepslate ore break chance: **10%**.
    - In darkness (block light $\le 0$, monster spawn condition): increases to **35%**.
    - High-risk ores (Deepslate Diamond & Emerald Ores): **75%** chance.
  * **Stacking & Timer Reset**: Each trigger sets or refreshes the remaining duration to **30 seconds** (600 ticks). Receiving the effect again while active refreshes duration and increments the effect level by 1 (capped at Level 10).

- **Optional Penchant Mod Integration (0.5.5+mc26.3+)**:
  * Optional, zero-dependency integration automatically detected when the [Penchant](https://github.com/ThePotatoArchivist/Penchant) mod is installed (`0.5.5+mc26.3` or higher).
  * **Table Exclusion & Maximum Costs**:
    - Excluded from standard enchanting table offerings (`#minecraft:enchantment/in_enchanting_table`).
    - If unlocked via chiseled bookshelf in Penchant's table, requires experience and books as high as the rarest enchantments in the game: **8 experience levels** and **45 books**.
  * **Armor Piercer Usage Progression**:
    - Levels up through item usage / durability damage in the same way as Sharpness, but requires **+25% more progress** to level up.
    - Level 1 → 2: **40 durability uses** (Sharpness baseline 32 uses + 25%).
    - Level 2 → 3: **54 durability uses** (Sharpness baseline 43 uses + 25%).
  * **Backstep Instantly Max Level**:
    - Has instantly max level upon enchanting (matching Infinity behavior) with no level progression (`#penchant:enchantment/no_leveling`).
    - Tagged as a rare enchantment (`#penchant:enchantment/rare`, matching Fortune rarity).
  * **Version Guard & Compatibility Validation**:
    - Ignores versions below `0.5.5+mc26.3`.
    - For higher versions, rigorously reflects on and validates required Penchant classes, methods, constructors, and fields.
    - Throws an explicit `IncompatiblePenchantVersionException` with a formatted error banner if any function or field is missing in newer Penchant releases.

- **Rare Dungeon Chest Loot**:
  * Directly injects rare enchanted books (`Armor Piercer` and `Backstep`) into 5 dungeon structures:
    - **Mob Spawner Dungeons** (`minecraft:chests/simple_dungeon`)
    - **Trial Chambers** (reward vaults, ominous vaults, supplies, corridors, intersections)
    - **Underwater Ruins & Pyramids** (ocean ruins big/small, buried treasure)
    - **Desert Pyramids** (`minecraft:chests/desert_pyramid`)
    - **Jungle Temples** (`minecraft:chests/jungle_temple`)
  * Matches Fortune rarity (~5% chance per chest).

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
    "scaleWithLevel": false,
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
    "disableNightVisionFlashingShader": true,
    "maxBrightnessCap": 2.0,
    "antiCheatMaxGamma": 1.0,
    "antiCheatResetGamma": 0.75
  },
  "nightmareAwareness": {
    "enabled": true,
    "maxTriggerYLevel": 0,
    "defaultOreChance": 0.10,
    "darkOreChance": 0.35,
    "highRiskOreChance": 0.75,
    "highRiskOres": [
      "minecraft:deepslate_diamond_ore",
      "minecraft:deepslate_emerald_ore"
    ],
    "darkLightLevelThreshold": 0,
    "durationTicks": 600,
    "aggroRangeBonusPerLevel": 0.25,
    "maxAggroRangeBonus": 2.50,
    "maxLevel": 10
  },
  "dungeonLoot": {
    "enabled": true,
    "emptyWeight": 18,
    "armorPiercerWeight": 1,
    "backstepWeight": 1
  }
}
```